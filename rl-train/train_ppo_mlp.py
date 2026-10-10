# -*- coding: utf-8 -*-
"""
破碎像素地牢 RL —— MLP 版向量化多环境 PPO（从 BC 完整 warm start 学换层）

背景：纯 BC（逐样本监督）只学高频动作，轨迹里探索动作占 99%、换层 <1%，
导致 BC 策略"会探索但从不找出口/换层"。BC 的真正价值 = 给 PPO 一个会探索的初始化。
本脚本用 MLP ActorCritic（与 train_bc.MLP 的 net+actor 键完全对齐），
从 bc_rich.pt 完整 warm start（net+actor 复用，critic 随机），GPU 向量化微调。
此时 actor 已会系统探索（会接近出口），换层奖励 100+ 的梯度不再稀疏不可达，PPO 才能学换层。

用法：
  python rl-train/train_ppo_mlp.py --init <bc.pt> --steps 1000000 --nenv 6 --tag bcmlp
"""
import argparse
import math
import os
import queue
import subprocess
import sys
import threading
import time
from concurrent.futures import ThreadPoolExecutor

import torch
import torch.nn as nn
from torch.distributions import Categorical

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAR   = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB    = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS   = os.path.join(BASE, "build", "classes")
CLASSPATH = ";".join([CLS, JAR, HB])
OUTDIR = os.path.join(BASE, "rl-train", "out")
os.makedirs(OUTDIR, exist_ok=True)

OBS_LEN = 1434
N_ACT   = 13
TARGET_DEPTH = 2


class SPDEnv:
    def __init__(self, seed, cwd=None):
        self._cur_seed = seed
        self._cwd = cwd
        self._spawn()

    def _spawn(self):
        self.proc = subprocess.Popen(
            ["java", "-Xmx1g", "-cp", CLASSPATH, "rl.RLCli", "proto"],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE,
            text=True, encoding="utf-8", bufsize=1, cwd=self._cwd)
        self._q = queue.Queue()
        self._reader = threading.Thread(target=self._read_loop, daemon=True)
        self._reader.start()
        time.sleep(1.5)

    def _read_loop(self):
        try:
            for line in self.proc.stdout:
                self._q.put(line)
        except Exception:
            pass
        self._q.put(None)

    def _read(self, timeout=20):
        try:
            return self._q.get(timeout=timeout)
        except queue.Empty:
            return None

    def _kill(self):
        try:
            if self.proc.poll() is None:
                self.proc.kill()
        except Exception:
            pass
        try:
            self.proc.stdin.close()
        except Exception:
            pass

    def _parse(self, meta, obs):
        meta = (meta or "").strip(); obs = (obs or "").strip()
        fields = {}
        for kv in meta.split():
            if "=" not in kv:
                continue
            k, v = kv.split("=", 1); fields[k] = v
        if fields.get("len") is None:
            raise RuntimeError("bad proto: " + meta)
        obs_ints = [int(x) for x in obs.split()] if obs else []
        return obs_ints, fields

    def _send_reset(self, seed):
        for _ in range(3):
            self.proc.stdin.write("reset %d\n" % seed); self.proc.stdin.flush()
            meta = self._read(); obs = self._read()
            if meta is not None and obs is not None:
                self._cur_seed = seed
                return self._parse(meta, obs)
            self._kill(); self._spawn()
        raise RuntimeError("SPDEnv reset stuck seed=%d" % seed)

    def reset(self, seed):
        return self._send_reset(seed)

    def step(self, action):
        for _ in range(3):
            self.proc.stdin.write("step %s\n" % action); self.proc.stdin.flush()   # %s 支持数字动作或 "E"(专家)
            meta = self._read(); obs = self._read()
            if meta is not None and obs is not None:
                obs_ints, fields = self._parse(meta, obs)
                reward = int(fields.get("reward", 0))
                done = fields.get("done", "false") == "true"
                return obs_ints, reward, done, fields, False
            self._kill(); self._spawn()
            if self._cur_seed is not None:
                obs_ints, fields = self._send_reset(self._cur_seed)
                return obs_ints, int(fields.get("reward", 0)), True, fields, True
        raise RuntimeError("SPDEnv step stuck action=%d" % action)

    def close(self):
        self._kill()


class VecEnv:
    def __init__(self, seeds):
        self.envs = []
        for i, s in enumerate(seeds):
            cwd = os.path.join(BASE, "build", "envs", "env%d" % i)
            os.makedirs(cwd, exist_ok=True)
            self.envs.append(SPDEnv(s, cwd))
        self.n = len(seeds)
        self._pool = ThreadPoolExecutor(max_workers=self.n)
        self.obs = [None] * self.n

    def reset_all(self, seeds):
        futs = [self._pool.submit(self.envs[i].reset, seeds[i]) for i in range(self.n)]
        for i, f in enumerate(futs):
            self.obs[i], _ = f.result()
        return self.obs

    def reset_one(self, i, seed):
        self.obs[i], _ = self.envs[i].reset(seed)

    def step_all(self, actions):
        futs = [self._pool.submit(self.envs[i].step, actions[i]) for i in range(self.n)]   # 支持 "E"(专家)或 int
        obs_list, rew_list, done_list, fields_list, restart_list = [], [], [], [], []
        for i, f in enumerate(futs):
            o, r, d, fields, rt = f.result()
            obs_list.append(o); rew_list.append(r); done_list.append(d)
            fields_list.append(fields); restart_list.append(rt)
            self.obs[i] = o
        return obs_list, rew_list, done_list, fields_list, restart_list

    def close(self):
        for e in self.envs:
            e.close()


def layer_init(layer, scale=1.0):
    nn.init.orthogonal_(layer.weight, gain=scale)
    nn.init.zeros_(layer.bias)
    return layer


class ActorCritic(nn.Module):
    """MLP 版：net+actor 与 train_bc.MLP 键完全对齐（warm start），critic 随机。"""
    def __init__(self, obs=OBS_LEN, act=N_ACT, feat_dim=256):
        super().__init__()
        self.net = nn.Sequential(
            layer_init(nn.Linear(obs, 512), scale=math.sqrt(2)), nn.Tanh(),
            layer_init(nn.Linear(512, feat_dim), scale=math.sqrt(2)), nn.Tanh(),
        )
        self.actor  = layer_init(nn.Linear(feat_dim, act), scale=0.01)
        self.critic = layer_init(nn.Linear(feat_dim, 1), scale=1.0)

    def get_action(self, x, hidden=None, action=None):
        x = x.float()
        if x.dim() == 1:
            x = x.unsqueeze(0)
        feat = self.net(x)
        logits = self.actor(feat)
        dist = Categorical(logits=logits)
        if action is None:
            action = dist.sample()
        logp = dist.log_prob(action)
        entropy = dist.entropy()
        value = self.critic(feat).squeeze(-1)
        return action, logp, entropy, value, None

    def get_value(self, x, hidden=None):
        x = x.float()
        if x.dim() == 1:
            x = x.unsqueeze(0)
        return self.critic(self.net(x)).squeeze(-1)


def make_rollout(envs, policy, device, T, N, seed_base, gamma, lam, eps_expert):
    obs_t = torch.zeros((T, N, OBS_LEN), dtype=torch.float32)
    act_t = torch.zeros((T, N), dtype=torch.long)
    logp_t = torch.zeros((T, N), dtype=torch.float32)
    rew_t = torch.zeros((T, N), dtype=torch.float32)
    don_t = torch.zeros((T, N), dtype=torch.bool)
    val_t = torch.zeros((T, N), dtype=torch.float32)
    exp_t = torch.zeros((T, N), dtype=torch.bool)   # 该步是否由专家动作执行（DAgger-BC 监督用）

    obs_list = envs.reset_all([seed_base + i for i in range(N)])

    for t in range(T):
        o_batch = torch.stack([torch.tensor(o, dtype=torch.float32) for o in obs_list], 0).to(device)
        with torch.no_grad():
            action_pi, _, _, value, _ = policy.get_action(o_batch)   # 策略采样
        # DAgger 专家混合：以 eps_expert 概率用专家动作(传 "E")执行，其余用策略动作
        use_expert = (torch.rand(N) < eps_expert).tolist()
        exp_t[t] = torch.tensor(use_expert, dtype=torch.bool)
        send = ["E" if use_expert[i] else int(action_pi[i]) for i in range(N)]
        obs_list, rew_list, done_list, fields_list, restart_list = envs.step_all(send)
        # 实际执行动作（含专家动作），从 proto meta 读取；专家动作用当前策略重算 logp（DAgger off-policy 基准）
        actual_list = []
        for i, f in enumerate(fields_list):
            a = int(f.get("action", int(action_pi[i])))
            if a < 0 or a > 12:
                a = int(action_pi[i])   # 专家动作执行失败(-1，走不动) → 用策略动作，避免非法 BC 监督
            actual_list.append(a)
        actual = torch.tensor(actual_list, dtype=torch.long)
        with torch.no_grad():
            _, logp_actual, _, _, _ = policy.get_action(o_batch, action=actual.to(device))
        obs_t[t] = o_batch.to("cpu")
        act_t[t] = actual.to("cpu")
        logp_t[t] = logp_actual.to("cpu")
        val_t[t] = value.to("cpu")
        rew_t[t] = torch.tensor(rew_list)
        don_t[t] = torch.tensor([d or rt for d, rt in zip(done_list, restart_list)])

        for i in range(N):
            if bool(don_t[t][i]):
                envs.reset_one(i, seed_base + 200000 + t * N + i)

    return (obs_t.to(device), act_t.to(device), logp_t.to(device),
            rew_t.to(device), don_t.to(device), val_t.to(device), exp_t.to(device))


def compute_gae(rew, don, val, gamma, lam):
    T, N = rew.shape
    adv = torch.zeros_like(rew)
    last = torch.zeros(N, device=rew.device)
    for t in reversed(range(T)):
        next_non_term = 1.0 - don[t].float()
        delta = rew[t] + gamma * last * next_non_term - val[t]
        last = torch.clamp(delta + gamma * lam * next_non_term * last, -1e4, 1e4)
        adv[t] = last
    returns = adv + val
    return adv, returns


def update_ppo(policy, optimizer, rollout, clip, epochs, batch, ent_coef, vf_coef, bc_coef):
    obs, act, logp_old, rew, don, val, exp = rollout
    T, N = rew.shape
    obs_f = obs.reshape(-1, OBS_LEN)
    act_f = act.reshape(-1)
    logp_f = logp_old.reshape(-1)
    exp_f = exp.reshape(-1).float()   # 1=专家动作步（BC 监督），0=策略动作步（PPO）
    adv, returns = compute_gae(rew, don, val, 0.99, 0.95)
    adv_f = torch.nan_to_num(adv.reshape(-1), nan=0.0, posinf=50.0, neginf=-50.0)
    returns_f = torch.nan_to_num(returns.reshape(-1), nan=0.0, posinf=5000.0, neginf=-5000.0)
    adv_f = torch.clamp(adv_f, -50.0, 50.0)          # 换层奖励 3000 后优势可达数十，上限放宽
    returns_f = (returns_f - returns_f.mean()) / (returns_f.std() + 1e-8)   # returns 标准化，防 critic vf loss 巨大/发散
    adv_f = (adv_f - adv_f.mean()) / (adv_f.std() + 1e-8)

    idx = torch.randperm(obs_f.shape[0], device=obs_f.device)
    total_pi = total_vf = total_ent = total_bc = 0.0
    n_upd = 0
    for _ in range(epochs):
        for start in range(0, obs_f.shape[0], batch):
            b = idx[start:start + batch]
            o, a, lp = obs_f[b], act_f[b], logp_f[b]
            exp_b = exp_f[b]
            _, logp, entropy, value, _ = policy.get_action(o, None, a)
            # 专家步：直接 BC 监督 -logp(actual)（DAgger 式模仿，不走 importance-ratio 以免 clip 吞掉换层梯度）
            # 策略步：标准 PPO（含换层奖励的 GAE 优势）
            ratio = torch.exp(logp - lp)
            surr1 = ratio * adv_f[b]
            surr2 = torch.clamp(ratio, 1 - clip, 1 + clip) * adv_f[b]
            pi_loss_p = -torch.min(surr1, surr2)
            bc_loss = (-logp * exp_b).mean()
            pi_loss = (pi_loss_p * (1.0 - exp_b)).mean() + bc_coef * bc_loss
            vf_loss = 0.5 * ((value - returns_f[b]) ** 2).mean()
            ent_loss = -entropy.mean()
            loss = pi_loss + vf_coef * vf_loss + ent_coef * ent_loss
            optimizer.zero_grad()
            loss.backward()
            nn.utils.clip_grad_norm_(policy.parameters(), 0.5)
            optimizer.step()
            total_pi += pi_loss.item(); total_vf += vf_loss.item()
            total_ent += ent_loss.item(); total_bc += bc_loss.item(); n_upd += 1
    return total_pi / n_upd, total_vf / n_upd, total_ent / n_upd, total_bc / n_upd


def evaluate(envs, policy, device, max_steps=200):
    N = envs.n
    seeds = [777777 + i for i in range(N)]
    obs_list = envs.reset_all(seeds)
    alive = [True] * N
    rets = [0.0] * N
    depths = [1] * N
    deaths = [False] * N
    targets = [False] * N
    global_steps = 0
    while any(alive) and global_steps < max_steps:
        idx = [i for i in range(N) if alive[i]]
        o_batch = torch.stack([torch.tensor(obs_list[i], dtype=torch.float32) for i in idx], 0).to(device)
        with torch.no_grad():
            feat = policy.net(o_batch)
            logits = policy.actor(feat)
            acts = logits.argmax(-1).tolist()
        futs = [envs._pool.submit(envs.envs[idx[j]].step, acts[j]) for j in range(len(idx))]
        for j, f in enumerate(futs):
            i = idx[j]
            o, r, done, fields, _ = f.result()
            obs_list[i] = o
            rets[i] += r
            global_steps += 1
            if int(fields.get("depth", 1)) > depths[i]:
                depths[i] = int(fields.get("depth", 1))
            if int(fields.get("depth", 1)) >= TARGET_DEPTH:
                targets[i] = True
            if done:
                alive[i] = False
                deaths[i] = True
    return depths, rets, deaths, targets


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--steps", type=int, default=40000)
    ap.add_argument("--seed", type=int, default=2026)
    ap.add_argument("--nenv", type=int, default=6)
    ap.add_argument("--time-steps", type=int, default=1024)
    ap.add_argument("--no-gpu", action="store_true")
    ap.add_argument("--tag", type=str, default="bcmlp")
    ap.add_argument("--init", type=str, default=None)
    ap.add_argument("--ent", type=float, default=0.01)
    ap.add_argument("--eps-expert", type=float, default=0.5, help="DAgger 专家动作混合比例(0-1)，让换层奖励进入 PPO 数据；随训练衰减至 0.05")
    ap.add_argument("--bc-coef", type=float, default=1.0, help="专家动作的 BC 监督权重（DAgger：专家步用 -logp 模仿，不走 importance-ratio）")
    args = ap.parse_args()

    device = torch.device("cpu" if args.no_gpu else "cuda" if torch.cuda.is_available() else "cpu")
    print(f"[env] device={device} gpu={torch.cuda.get_device_name(0) if device.type=='cuda' else 'none'} nenv={args.nenv}")
    torch.manual_seed(args.seed)

    policy = ActorCritic().to(device)
    if args.init:
        bc = torch.load(args.init, map_location=device)
        sd = policy.state_dict()
        if set(bc.keys()) == set(sd.keys()):
            policy.load_state_dict(bc)
            print(f"[train] 完整加载续训权重: {args.init}")
        else:
            filtered = {k: v for k, v in bc.items() if k in sd and sd[k].shape == v.shape}
            policy.load_state_dict(filtered, strict=False)
            print(f"[train] warm-start 加载 BC 权重: {args.init} (匹配键 net+actor 复用，critic 随机)")
    optimizer = torch.optim.Adam(policy.parameters(), lr=3e-4, eps=1e-5)

    envs = VecEnv([args.seed + i for i in range(args.nenv)])
    T = args.time_steps
    N = args.nenv
    PER_ITER = T * N
    EVAL_EVERY = max(1, PER_ITER // 300)

    print(f"[train] total_steps={args.steps} time_steps/iter={T} nenv={N} rollout_per_iter={PER_ITER} obs={OBS_LEN} act={N_ACT}")
    log_path = os.path.join(OUTDIR, f"ppo_{args.tag}.log")
    logf = open(log_path, "w", encoding="utf-8")
    t0 = time.time()
    iters = max(1, args.steps // PER_ITER)
    eps_expert = args.eps_expert
    for it in range(iters):
        rollout = make_rollout(envs, policy, device, T, N, args.seed, 0.99, 0.95, eps_expert)
        pi_l, vf_l, ent_l, bc_l = update_ppo(policy, optimizer, rollout, 0.2, 4, 256, args.ent, 0.5, args.bc_coef)
        eps_expert = max(0.05, eps_expert * 0.995)   # 衰减专家混合，让 PPO 逐渐自主换层
        mean_r = rollout[3].mean().item()
        steps_done = PER_ITER * (it + 1)

        if (it + 1) % EVAL_EVERY == 0 or it == iters - 1:
            depths, rets, deaths, targets = evaluate(envs, policy, device)
            avg_d = sum(depths) / len(depths)
            avg_ret = sum(rets) / len(rets)
            msg = (f"[iter {it+1}] steps={steps_done} | pi={pi_l:.4f} vf={vf_l:.4f} ent={ent_l:.4f} bc={bc_l:.4f} "
                   f"| roll_reward={mean_r:.3f} | eval avg_depth={avg_d:.2f} avg_ret={avg_ret:.2f} "
                   f"deaths={sum(deaths)}/{N} targets={sum(targets)}/{N} | {time.time()-t0:.0f}s")
            print(msg)
            logf.write(msg + "\n"); logf.flush()
            torch.save(policy.state_dict(), os.path.join(OUTDIR, f"ppo_{args.tag}.pt"))

    envs.close()
    logf.close()
    print(f"[done] {time.time()-t0:.0f}s total_steps={PER_ITER*(it+1)} saved={os.path.join(OUTDIR, f'ppo_{args.tag}.pt')}")


if __name__ == "__main__":
    main()
