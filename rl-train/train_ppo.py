# -*- coding: utf-8 -*-
"""
破碎像素地牢 RL —— PPO 训练器（纯 RL 基线）

架构：
  Python 端 PPO 策略（MLP: 1417 -> 512 -> 256 -> 12），通过 RLCli proto 协议
  驱动一个常驻的 Java headless 环境进程做 rollout，GPU 上训练网络。

用法：
  python rl-train/train_ppo.py [--steps N] [--seed S] [--eval-every K]
"""
import argparse
import math
import os
import queue
import subprocess
import sys
import threading
import time
from collections import deque

import torch
import torch.nn as nn
import torch.nn.functional as F
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


# ---------------------------------------------------------------------------
# proto 环境封装：子进程跑 rl.RLCli proto，逐行命令/响应
# ---------------------------------------------------------------------------
class SPDEnv:
    """带看门狗的 proto 环境封装。

    后台线程持续读 Java 环境 stdout 并放入队列；reset/step 从队列取行并带超时。
    若超时（Java 环境卡死/无响应），kill 并重启进程、重新 reset 到当前 episode seed，
    保证训练永不因环境卡死而永久挂住（此前会 readline 永久阻塞）。
    """
    def __init__(self, seed):
        self._cur_seed = seed
        self._spawn()

    def _spawn(self):
        self.proc = subprocess.Popen(
            ["java", "-Xmx1g", "-cp", CLASSPATH, "rl.RLCli", "proto"],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE,
            text=True, encoding="utf-8", bufsize=1, cwd=BASE)
        self._q = queue.Queue()
        self._reader = threading.Thread(target=self._read_loop, daemon=True)
        self._reader.start()
        time.sleep(1.5)  # 等待 JVM + headless 初始化完成，避免 reset 竞态

    def _read_loop(self):
        try:
            for line in self.proc.stdout:
                self._q.put(line)
        except Exception:
            pass
        self._q.put(None)  # EOF 哨兵

    def _read(self, timeout=20):
        try:
            return self._q.get(timeout=timeout)
        except queue.Empty:
            return None  # 环境卡死/无响应

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
        meta = (meta or "").strip()
        obs = (obs or "").strip()
        if not meta.startswith("OK "):
            print("[debug] non-OK meta=", repr(meta), file=sys.stderr)
        fields = {}
        for kv in meta.split():
            if "=" not in kv:
                continue  # 跳过 "OK" 等命令标识 token
            k, v = kv.split("=", 1)
            fields[k] = v
        if fields.get("len") is None:
            raise RuntimeError("bad proto: " + meta)
        obs_ints = [int(x) for x in obs.split()] if obs else []
        return obs_ints, fields

    def _send_reset(self, seed):
        for _ in range(3):
            self.proc.stdin.write("reset %d\n" % seed)
            self.proc.stdin.flush()
            meta = self._read()
            obs = self._read()
            if meta is not None and obs is not None:
                self._cur_seed = seed
                return self._parse(meta, obs)
            self._kill()
            self._spawn()
        raise RuntimeError("SPDEnv reset stuck for seed=%d" % seed)

    def reset(self, seed):
        return self._send_reset(seed)

    def step(self, action):
        for _ in range(3):
            self.proc.stdin.write("step %d\n" % action)
            self.proc.stdin.flush()
            meta = self._read()
            obs = self._read()
            if meta is not None and obs is not None:
                obs_ints, fields = self._parse(meta, obs)
                reward = int(fields.get("reward", 0))
                done   = fields.get("done", "false") == "true"
                return obs_ints, reward, done, fields, False
            # 环境卡死：重启并重新 reset 到当前 seed，返回该 obs 并标记 episode 中断
            self._kill()
            self._spawn()
            if self._cur_seed is not None:
                obs_ints, fields = self._send_reset(self._cur_seed)
                return obs_ints, int(fields.get("reward", 0)), True, fields, True
        raise RuntimeError("SPDEnv step stuck for action=%d" % action)

    def close(self):
        self._kill()


# ---------------------------------------------------------------------------
# Actor-Critic 网络（离散动作）
# ---------------------------------------------------------------------------
def layer_init(layer, scale=1.0):
    nn.init.orthogonal_(layer.weight, gain=scale)
    nn.init.zeros_(layer.bias)
    return layer


class ActorCritic(nn.Module):
    """GRU 版 Actor-Critic：特征层(复用 MLP net) + GRU 记忆 + actor/critic。

    单步交互 + hidden 记忆（RNN-RL 标准做法）：策略能从历史观测(hidden 压缩)学习
    系统探索等长程决策。环境 reset(episode 开始/结束) 时 hidden 强制清零，
    绝不让上一局死亡记忆污染新局。
    特征层 self.net 形状与 MLP bc_lc.pt 一致(1434->512->256)，可用 --init 复用；
    GRU / actor / critic 随机初始化，交给 RL 自学。
    """
    def __init__(self, obs=OBS_LEN, act=N_ACT, feat_dim=256, gru_dim=128):
        super().__init__()
        self.net = nn.Sequential(
            layer_init(nn.Linear(obs, 512), scale=math.sqrt(2)),
            nn.Tanh(),
            layer_init(nn.Linear(512, feat_dim), scale=math.sqrt(2)),
            nn.Tanh(),
        )
        self.gru    = nn.GRU(feat_dim, gru_dim)          # 随机初始化，RL 自学记忆
        self.actor  = layer_init(nn.Linear(gru_dim, act), scale=0.01)
        self.critic = layer_init(nn.Linear(gru_dim, 1), scale=1.0)

    def _gru_out(self, feat, hidden):
        # feat: [B, feat_dim]；hidden: [1,B,gru_dim] 或 None
        if hidden is None:
            out, h = self.gru(feat.unsqueeze(0))
        else:
            out, h = self.gru(feat.unsqueeze(0), hidden)
        return out[-1], h   # out[-1]:[B,gru], h:[1,B,gru]

    def get_action(self, x, hidden=None, action=None):
        x = x.float()
        if x.dim() == 1:
            x = x.unsqueeze(0)
        feat = self.net(x)                       # [B, feat]
        h2, h = self._gru_out(feat, hidden)      # h2:[B,gru]
        logits = self.actor(h2)
        dist = Categorical(logits=logits)
        if action is None:
            action = dist.sample()
        logp = dist.log_prob(action)
        entropy = dist.entropy()
        value = self.critic(h2).squeeze(-1)
        return action, logp, entropy, value, h

    def get_value(self, x, hidden=None):
        x = x.float()
        if x.dim() == 1:
            x = x.unsqueeze(0)
        h2, _ = self._gru_out(self.net(x), hidden)
        return self.critic(h2).squeeze(-1)


# ---------------------------------------------------------------------------
# PPO
# ---------------------------------------------------------------------------
def make_rollout(env, policy, device, n_steps, seed_base, gamma, lam):
    """收集 n_steps 步轨迹，返回张量。所有 episode 在同一进程连续跑，episode 结束 reset。
    GRU hidden：每局(episode) 从 reset 后清零开始，episode 结束(死亡/换层) 清零；每步保存输入 hidden 供 PPO update 重算。"""
    obs_t = torch.zeros((n_steps, OBS_LEN), dtype=torch.float32)
    act_t = torch.zeros((n_steps,), dtype=torch.long)
    logp_t = torch.zeros((n_steps,), dtype=torch.float32)
    rew_t = torch.zeros((n_steps,), dtype=torch.float32)
    don_t = torch.zeros((n_steps,), dtype=torch.bool)
    val_t = torch.zeros((n_steps,), dtype=torch.float32)
    hid_t = torch.zeros((n_steps, 1, 128), dtype=torch.float32)   # 每步输入 hidden（GRU dim=128）

    obs, _ = env.reset(seed_base)
    hidden = torch.zeros((1, 1, 128), device=device)   # 新局记忆清零（不继承上一局死亡记忆）
    step_r = 0.0
    for i in range(n_steps):
        o = torch.tensor(obs, device=device)
        hid_t[i] = hidden.squeeze(0).detach().to("cpu")   # [1,128]
        with torch.no_grad():
            action, logp, _, value, hidden = policy.get_action(o, hidden)
        obs_t[i] = o.to("cpu")
        act_t[i] = action
        logp_t[i] = logp
        val_t[i] = value
        obs, reward, done, _, restarted = env.step(int(action))
        rew_t[i] = reward
        if restarted:
            done = True  # 环境卡死重启导致本 episode 中断
        don_t[i] = done
        step_r += reward
        if done:
            obs, _ = env.reset(seed_base + 100000 + i)
            hidden = torch.zeros((1, 1, 128), device=device)   # 新局记忆清零
    return (obs_t.to(device), act_t.to(device), logp_t.to(device),
            rew_t.to(device), don_t.to(device), val_t.to(device), hid_t.to(device))


def compute_gae(rew, don, val, gamma, lam):
    n = rew.shape[0]
    adv = torch.zeros_like(rew)
    lastgaelam = 0.0
    next_val = 0.0
    for t in reversed(range(n)):
        next_non_term = 1.0 - float(don[t])
        delta = rew[t] + gamma * next_val * next_non_term - val[t]
        lastgaelam = delta + gamma * lam * next_non_term * lastgaelam
        adv[t] = lastgaelam
        next_val = val[t]
    returns = adv + val
    return adv, returns


def update_ppo(policy, optimizer, rollout, clip, epochs, batch, ent_coef, vf_coef):
    obs, act, logp_old, rew, don, val, hid = rollout
    adv, returns = compute_gae(rew, don, val, 0.99, 0.95)
    adv = (adv - adv.mean()) / (adv.std() + 1e-8)

    idx = torch.randperm(obs.shape[0], device=obs.device)
    total_pi = total_vf = total_ent = 0.0
    n_upd = 0
    for _ in range(epochs):
        for start in range(0, obs.shape[0], batch):
            b = idx[start:start + batch]
            o, a, lp = obs[b], act[b], logp_old[b]
            hb = hid[b].permute(1, 0, 2)   # [B,1,gru] -> [1,B,gru]，PPO 用 rollout 时该步的输入 hidden 重算
            _, logp, entropy, value, _ = policy.get_action(o, hb, a)
            ratio = torch.exp(logp - lp)
            surr1 = ratio * adv[b]
            surr2 = torch.clamp(ratio, 1 - clip, 1 + clip) * adv[b]
            pi_loss = -torch.min(surr1, surr2).mean()
            vf_loss = 0.5 * ((value - returns[b]) ** 2).mean()
            ent_loss = -entropy.mean()
            loss = pi_loss + vf_coef * vf_loss + ent_coef * ent_loss
            optimizer.zero_grad()
            loss.backward()
            nn.utils.clip_grad_norm_(policy.parameters(), 0.5)
            optimizer.step()
            total_pi += pi_loss.item(); total_vf += vf_loss.item()
            total_ent += ent_loss.item(); n_upd += 1
    return total_pi / n_upd, total_vf / n_upd, total_ent / n_upd


def evaluate(env, policy, device, episodes):
    """argmax 策略评估：记录平均 depth / 累计奖励 / 是否死亡结束 / 是否达标。GRU 每局 hidden 清零。"""
    depths, rets, deaths, targets = [], [], 0, 0
    for e in range(episodes):
        obs, _ = env.reset(777777 + e)
        hidden = torch.zeros((1, 1, 128), device=device)   # 每局记忆清零
        ret = 0.0
        done = False
        target = False
        steps = 0
        while not done and steps < 200:
            o = torch.tensor(obs, device=device)
            with torch.no_grad():
                feat = policy.net(o.float()).unsqueeze(0)   # [1,feat]
                out, hidden = policy.gru(feat.unsqueeze(0), hidden)   # batched [1,1,feat] + hx[1,1,gru]
                logits = policy.actor(out[-1])
                action = logits.argmax().item()
            obs, reward, done, fields, _ = env.step(action)
            ret += reward
            steps += 1
            if int(fields.get("depth", 0)) >= 3:
                target = True
                break
        depths.append(int(fields.get("depth", 1)))
        rets.append(ret)
        if done:
            deaths += 1
        if target:
            targets += 1
    return depths, rets, deaths, targets


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--steps", type=int, default=40000)
    ap.add_argument("--seed", type=int, default=2026)
    ap.add_argument("--eval-every", type=int, default=4000)
    ap.add_argument("--no-gpu", action="store_true")
    ap.add_argument("--tag", type=str, default="base")
    ap.add_argument("--init", type=str, default=None, help="BC 初始化权重路径（net+actor，critic 随机初始化）")
    ap.add_argument("--ent", type=float, default=0.01, help="熵损失系数（越低越贪婪，减少随机乱撞）")
    args = ap.parse_args()

    device = torch.device("cpu" if args.no_gpu else "cuda" if torch.cuda.is_available() else "cpu")
    print(f"[env] device={device} gpu={torch.cuda.get_device_name(0) if device.type=='cuda' else 'none'}")
    torch.manual_seed(args.seed)

    policy = ActorCritic().to(device)
    if args.init:
        bc = torch.load(args.init, map_location=device)
        sd = policy.state_dict()
        if all(k in sd and sd[k].shape == v.shape for k, v in bc.items()):
            policy.load_state_dict(bc)   # 完整 warm start：同架构续训，保留已学 GRU/actor/critic 记忆
            print(f"[train] 完整加载续训权重: {args.init}")
        else:
            # GRU 版只复用 MLP 的特征层 net.*（形状一致）；GRU/actor/critic 维度不同，随机初始化由 RL 自学
            filtered = {k: v for k, v in bc.items() if k.startswith('net.')}
            policy.load_state_dict(filtered, strict=False)
            print(f"[train] 已加载 BC 特征层权重: {args.init} (GRU/actor/critic 随机初始化)")
    optimizer = torch.optim.Adam(policy.parameters(), lr=3e-4, eps=1e-5)

    env = SPDEnv(args.seed)
    N_STEPS = 2048
    EPISODES = min(8, max(1, args.steps // 500))  # eval 局数封顶，避免 eval 拖慢训练

    print(f"[train] total_steps={args.steps} n_steps/iter={N_STEPS} obs={OBS_LEN} act={N_ACT}")
    log_path = os.path.join(OUTDIR, f"ppo_{args.tag}.log")
    logf = open(log_path, "w", encoding="utf-8")
    t0 = time.time()
    it = 0
    for it in range(args.steps // N_STEPS):
        rollout = make_rollout(env, policy, device, N_STEPS, args.seed, 0.99, 0.95)
        pi_l, vf_l, ent_l = update_ppo(policy, optimizer, rollout, 0.2, 4, 256, args.ent, 0.5)
        mean_r = rollout[3].mean().item()

        # 周期性评估
        if (it + 1) % (args.eval_every // N_STEPS) == 0 or it == args.steps // N_STEPS - 1:
            depths, rets, deaths, targets = evaluate(env, policy, device, EPISODES)
            avg_d = sum(depths) / len(depths)
            avg_ret = sum(rets) / len(rets)
            msg = (f"[iter {it+1}] steps={N_STEPS*(it+1)} | pi={pi_l:.4f} vf={vf_l:.4f} ent={ent_l:.4f} "
                   f"| roll_reward={mean_r:.3f} | eval avg_depth={avg_d:.2f} avg_ret={avg_ret:.2f} "
                   f"deaths={deaths}/{EPISODES} targets={targets}/{EPISODES} | {time.time()-t0:.0f}s")
            print(msg)
            logf.write(msg + "\n"); logf.flush()
            torch.save(policy.state_dict(), os.path.join(OUTDIR, f"ppo_{args.tag}.pt"))

    env.close()
    logf.close()
    print(f"[done] {time.time()-t0:.0f}s total_steps={N_STEPS*(it+1)} saved={os.path.join(OUTDIR, f'ppo_{args.tag}.pt')}")


if __name__ == "__main__":
    main()
