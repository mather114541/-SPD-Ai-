# -*- coding: utf-8 -*-
"""
破碎像素地牢 —— v2 PPO 微调训练器（无 GRU 版，保留 bc6 换层能力）。
完整加载 bc6.pt（conv/fc_map/fc_sc/trunk/actor/critic 全加载），PPO 微调输出层。
核心目标：用"发现隐藏门/陷阱 +80"奖励 + DAgger 专家退火，教会 AI 主动搜索。
  - eps_expert：每步以该概率用专家动作(RLExpert，含搜索)，随训练衰减至 0.05。
  - bc_coef：专家步的 -logp 模仿权重（DAgger 监督）。
用法：
  python rl-train/train_ppo_v2.py --init rl-train/out/bc6.pt --steps 120000 --nenv 6 --eps-expert 0.5 --bc-coef 1.0 --tag search
"""
import argparse
import math
import os
import subprocess
import sys
import time
from concurrent.futures import ThreadPoolExecutor

import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F
from torch.distributions import Categorical

from spd_v2 import NMAP, N_SCALAR, NCH, MAPW, MAPH, N_ACT, expand, norm_scalars
from train_ppo_vec import SPDEnv

BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAR = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS = os.path.join(BASE, "build", "classes")
CLASSPATH = ";".join([CLS, JAR, HB])
OUTDIR = os.path.join(BASE, "rl-train", "out")
os.makedirs(OUTDIR, exist_ok=True)

OBS_LEN = NMAP + N_SCALAR   # 2409
TARGET_DEPTH = 2

MEAN = None
STD = None


def layer_init(layer, scale=1.0):
    nn.init.orthogonal_(layer.weight, gain=scale)
    nn.init.zeros_(layer.bias)
    return layer


class ActorCriticV2(nn.Module):
    """无 GRU：与 spd_v2.CNN 结构一致，完整可加载 bc6。"""
    def __init__(self, feat_dim=256, act=N_ACT):
        super().__init__()
        self.conv = nn.Sequential(
            nn.Conv2d(NCH, 32, 3, padding=1), nn.ReLU(),
            nn.Conv2d(32, 32, 3, padding=1), nn.ReLU(),
            nn.Conv2d(32, 64, 3, stride=2, padding=1), nn.ReLU(),
            nn.Conv2d(64, 64, 3, stride=2, padding=1), nn.ReLU(),
        )
        self.fc_map = nn.Sequential(nn.Flatten(), nn.Linear(64 * 12 * 12, 512), nn.ReLU())
        self.fc_sc = nn.Sequential(nn.Linear(N_SCALAR, 64), nn.ReLU())
        self.trunk = nn.Sequential(nn.Linear(512 + 64, feat_dim), nn.ReLU())
        self.actor = layer_init(nn.Linear(feat_dim, act), scale=0.01)
        self.critic = layer_init(nn.Linear(feat_dim, 1), scale=1.0)

    def _feat(self, obs):
        m_int = obs[:, :NMAP].to(torch.int64).detach().cpu().numpy()
        mb = torch.from_numpy(expand(m_int)).to(obs.device)
        s = norm_scalars(obs[:, NMAP:].detach().cpu().numpy(), MEAN, STD)
        s = np.clip(s, -10.0, 10.0)   # 防 obs 极端导致 inf/nan
        sb = torch.from_numpy(s).to(obs.device)
        h = self.conv(mb).flatten(1)
        h = self.fc_map(h)
        s2 = self.fc_sc(sb)
        return self.trunk(torch.cat([h, s2], dim=1))

    def get_action(self, obs, action=None):
        z = self._feat(obs)
        logits = self.actor(z)
        dist = Categorical(logits=logits)
        if action is None:
            action = dist.sample()
        logp = dist.log_prob(action)
        entropy = dist.entropy()
        value = self.critic(z).squeeze(-1)
        return action, logp, entropy, value

    def get_value(self, obs):
        return self.critic(self._feat(obs)).squeeze(-1)


class VecEnv:
    def __init__(self, seeds):
        self.envs = []
        for i, s in enumerate(seeds):
            cwd = os.path.join(BASE, "build", "envs", "envp%d" % i)
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
        futs = [self._pool.submit(self.envs[i].step, actions[i]) for i in range(self.n)]
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


def make_rollout(envs, policy, device, T, N, seed_base, gamma, lam, eps_expert=0.0):
    obs_t = torch.zeros((T, N, OBS_LEN), dtype=torch.float32)
    act_t = torch.zeros((T, N), dtype=torch.long)
    logp_t = torch.zeros((T, N), dtype=torch.float32)
    rew_t = torch.zeros((T, N), dtype=torch.float32)
    don_t = torch.zeros((T, N), dtype=torch.bool)
    val_t = torch.zeros((T, N), dtype=torch.float32)
    exp_t = torch.zeros((T, N), dtype=torch.bool)

    obs_list = envs.reset_all([seed_base + i for i in range(N)])

    for t in range(T):
        o_batch = torch.stack([torch.tensor(o, dtype=torch.float32) for o in obs_list], 0).to(device)
        with torch.no_grad():
            action_pi, _, _, value = policy.get_action(o_batch)
        use_expert = (torch.rand(N) < eps_expert).tolist()
        exp_t[t] = torch.tensor(use_expert, dtype=torch.bool)
        send = ["E" if use_expert[i] else int(action_pi[i]) for i in range(N)]
        obs_list, rew_list, done_list, fields_list, restart_list = envs.step_all(send)
        actual_list = []
        for i, f in enumerate(fields_list):
            a = int(f.get("action", int(action_pi[i])))
            if a < 0 or a > 8:
                a = int(action_pi[i])
            actual_list.append(a)
        actual = torch.tensor(actual_list, dtype=torch.long).to(device)
        with torch.no_grad():
            _, logp_actual, _, _ = policy.get_action(o_batch, action=actual)
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


def update_ppo(policy, optimizer, rollout, clip, epochs, batch, ent_coef, vf_coef, bc_coef=0.0):
    obs, act, logp_old, rew, don, val, exp = rollout
    T, N = rew.shape
    obs_f = obs.reshape(-1, OBS_LEN)
    act_f = act.reshape(-1)
    logp_f = logp_old.reshape(-1)
    exp_f = exp.reshape(-1).float()
    adv, returns = compute_gae(rew, don, val, 0.99, 0.95)
    adv_f = torch.nan_to_num(adv.reshape(-1), nan=0.0, posinf=50.0, neginf=-50.0)
    returns_f = torch.nan_to_num(returns.reshape(-1), nan=0.0, posinf=500.0, neginf=-500.0)
    adv_f = torch.clamp(adv_f, -20.0, 20.0)
    returns_f = torch.clamp(returns_f, -500.0, 500.0)
    adv_f = (adv_f - adv_f.mean()) / (adv_f.std() + 1e-8)
    returns_f = (returns_f - returns_f.mean()) / (returns_f.std() + 1e-8)

    idx = torch.randperm(obs_f.shape[0], device=obs_f.device)
    total_pi = total_vf = total_ent = 0.0
    n_upd = 0
    for _ in range(epochs):
        for start in range(0, obs_f.shape[0], batch):
            b = idx[start:start + batch]
            o, a, lp = obs_f[b], act_f[b], logp_f[b]
            exp_b = exp_f[b]
            _, logp, entropy, value = policy.get_action(o, a)
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
            total_ent += ent_loss.item(); n_upd += 1
    return total_pi / n_upd, total_vf / n_upd, total_ent / n_upd


def evaluate(envs, policy, device, max_steps=800):
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
            z = policy._feat(o_batch)
            acts = policy.actor(z).argmax(-1).tolist()
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
    ap.add_argument("--steps", type=int, default=120000)
    ap.add_argument("--seed", type=int, default=2026)
    ap.add_argument("--nenv", type=int, default=6)
    ap.add_argument("--time-steps", type=int, default=2048)
    ap.add_argument("--no-gpu", action="store_true")
    ap.add_argument("--tag", type=str, default="search")
    ap.add_argument("--init", type=str, default=None)
    ap.add_argument("--ent", type=float, default=0.02)
    ap.add_argument("--eps-expert", type=float, default=0.0)
    ap.add_argument("--bc-coef", type=float, default=0.0)
    ap.add_argument("--lr", type=float, default=1e-4)
    ap.add_argument("--freeze", action="store_true", help="冻结特征层(conv/fc_map/fc_sc/trunk)，只微调 actor/critic")
    args = ap.parse_args()

    device = torch.device("cpu" if args.no_gpu else "cuda" if torch.cuda.is_available() else "cpu")
    print(f"[env] device={device} gpu={torch.cuda.get_device_name(0) if device.type=='cuda' else 'none'} nenv={args.nenv}", flush=True)
    torch.manual_seed(args.seed)

    global MEAN, STD
    policy = ActorCriticV2().to(device)
    if args.init:
        ck = torch.load(args.init, map_location="cpu", weights_only=False)
        MEAN = ck["mean"]
        STD = ck["std"]
        sd = policy.state_dict()
        filtered = {k: v for k, v in ck["state"].items() if k in sd and sd[k].shape == v.shape}
        policy.load_state_dict(filtered, strict=False)
        print(f"[train] 已加载权重: {args.init} 匹配 {len(filtered)}/{len(sd)} 层 (保留换层能力)", flush=True)
    else:
        raise SystemExit("必须 --init 提供 BC 权重")

    optimizer = torch.optim.Adam(policy.parameters(), lr=args.lr, eps=1e-5)
    if args.freeze:
        for name, p in policy.named_parameters():
            if not name.startswith(("actor.", "critic.")):
                p.requires_grad = False
        optimizer = torch.optim.Adam(
            [p for p in policy.parameters() if p.requires_grad], lr=args.lr, eps=1e-5)
        print("[train] 已冻结特征层，仅微调 actor/critic", flush=True)
    envs = VecEnv([args.seed + i for i in range(args.nenv)])
    T = args.time_steps
    N = args.nenv
    PER_ITER = T * N
    iters = max(1, args.steps // PER_ITER)

    print(f"[train] total_steps={args.steps} iter_steps={PER_ITER} obs={OBS_LEN} act={N_ACT} iters={iters}", flush=True)
    log_path = os.path.join(OUTDIR, f"ppo_{args.tag}.log")
    logf = open(log_path, "w", encoding="utf-8")
    t0 = time.time()
    eps_expert = args.eps_expert
    for it in range(iters):
        rollout = make_rollout(envs, policy, device, T, N, args.seed, 0.99, 0.95, eps_expert)
        pi_l, vf_l, ent_l = update_ppo(policy, optimizer, rollout, 0.2, 4, 256, args.ent, 0.5, args.bc_coef)
        eps_expert = max(0.05, eps_expert * 0.995)
        mean_r = rollout[3].mean().item()
        steps_done = PER_ITER * (it + 1)
        depths, rets, deaths, targets = evaluate(envs, policy, device)
        avg_d = sum(depths) / len(depths)
        avg_ret = sum(rets) / len(rets)
        msg = (f"[iter {it+1}] steps={steps_done} | pi={pi_l:.4f} vf={vf_l:.4f} ent={ent_l:.4f} "
               f"| roll_r={mean_r:.3f} | eval avg_depth={avg_d:.2f} avg_ret={avg_ret:.2f} "
               f"deaths={sum(deaths)}/{N} targets={sum(targets)}/{N} eps_expert={eps_expert:.2f} | {time.time()-t0:.0f}s")
        print(msg, flush=True)
        logf.write(msg + "\n"); logf.flush()
        torch.save(policy.state_dict(), os.path.join(OUTDIR, f"ppo_{args.tag}.pt"))

    envs.close()
    logf.close()
    print(f"[done] {time.time()-t0:.0f}s total_steps={PER_ITER*(it+1)} saved={os.path.join(OUTDIR, f'ppo_{args.tag}.pt')}", flush=True)


if __name__ == "__main__":
    main()
