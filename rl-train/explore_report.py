# -*- coding: utf-8 -*-
"""每层探索覆盖率（正确口径）： 已探的可走格数 / 该层可走格总数。
   位域 bit1=PASSABLE 仅在"已知"格上置位，所以 sum(bit1) 就是"已探可走格数"。"""
import argparse, os
from collections import defaultdict
import numpy as np
import torch
from spd_v2 import NMAP, BASE, expand, load_ckpt, norm_scalars
from train_ppo_vec import SPDEnv

DEPTH_IDX = NMAP
PASS_IDX = NMAP + 7 + 2   # global[2] = 该层可走格总数


def kp(o):
    m = o[:NMAP].astype(np.int64)
    return int(((m >> 1) & 1).sum())   # 已探可走格数


def cov(o):
    return kp(o) / max(1, int(o[PASS_IDX]))


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("ckpt")
    ap.add_argument("--episodes", type=int, default=20)
    ap.add_argument("--seedbase", type=int, default=980001)
    ap.add_argument("--max-steps", type=int, default=2000)
    ap.add_argument("--target", type=int, default=6)
    ap.add_argument("--tag", default="expl2")
    args = ap.parse_args()
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model, mean, std, meta = load_ckpt(args.ckpt, device)
    cwd = os.path.join(BASE, "build", "envs", "expl_" + args.tag)
    os.makedirs(cwd, exist_ok=True)
    env = SPDEnv(args.seedbase, cwd)
    try:
        env.proc.stdin.write("target %d\n" % args.target); env.proc.stdin.flush(); env._read()
    except Exception:
        pass
    leftcov = defaultdict(list)
    endcov = defaultdict(list)
    descended = 0
    for ep in range(args.episodes):
        obs, fields = env.reset(args.seedbase + ep)
        o = np.asarray(obs, dtype=np.int64)
        steps = 0
        prev_depth = int(o[DEPTH_IDX])
        best = cov(o)
        while steps < args.max_steps:
            m = o[:NMAP].astype(np.uint16); s = o[NMAP:].astype(np.int32)
            mb = torch.from_numpy(expand(m[None, :])).to(device)
            sb = torch.from_numpy(norm_scalars(s[None, :], mean, std)).to(device)
            with torch.no_grad():
                logits, _ = model(mb, sb)
            a = int(logits.argmax(1).item())
            obs, r, done, fields, _ = env.step(a)
            step_cov = cov(o)          # 上一状态(旧层)的覆盖
            best = max(best, step_cov)
            o = np.asarray(obs, dtype=np.int64)
            steps += 1
            d = int(o[DEPTH_IDX])
            if d > prev_depth:
                leftcov[prev_depth].append(step_cov)
                descended += 1
                prev_depth = d
                best = cov(o)
            if done:
                break
        ec = cov(o)
        endcov[int(o[DEPTH_IDX])].append(ec)
        print("[ep %2d] endDepth=%d endCov=%.3f maxCovThisFloor=%.3f steps=%d" % (ep, int(o[DEPTH_IDX]), ec, best, steps), flush=True)
    print("=== 下楼那一刻该层覆盖率 ===", flush=True)
    for d in sorted(leftcov):
        v = leftcov[d]
        print("  floor %d: n=%d mean=%.3f min=%.3f max=%.3f" % (d, len(v), float(np.mean(v)), float(np.min(v)), float(np.max(v))), flush=True)
    print("=== 结束时所在层覆盖率 ===", flush=True)
    for d in sorted(endcov):
        v = endcov[d]
        print("  floor %d: n=%d mean=%.3f" % (d, len(v), float(np.mean(v))), flush=True)
    print("=== 共下楼 %d 次 / %d 局 ===" % (descended, args.episodes), flush=True)


if __name__ == "__main__":
    main()
