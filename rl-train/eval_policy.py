# -*- coding: utf-8 -*-
"""在无头 Java 环境里评估 BC 策略：跑 N 局，统计下楼率/平均深度/死亡数。

用法：python rl-train/eval_policy.py rl-train/out/bc2.pt --episodes 30 --seedbase 700000
"""
import argparse
import os
import sys
import time

import numpy as np
import torch

from spd_v2 import N_ACT, NMAP, N_SCALAR, BASE, expand, load_ckpt, norm_scalars
from train_ppo_vec import SPDEnv


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("ckpt")
    ap.add_argument("--episodes", type=int, default=30)
    ap.add_argument("--seedbase", type=int, default=700000)
    ap.add_argument("--max-steps", type=int, default=2500)
    ap.add_argument("--target", type=int, default=6, help="环境终止层，设大一点以测量能走多深")
    ap.add_argument("--tag", default="eval")
    args = ap.parse_args()

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model, mean, std, meta = load_ckpt(args.ckpt, device)
    print("[eval] ckpt=%s meta=%s device=%s" % (args.ckpt, meta, device), flush=True)

    cwd = os.path.join(BASE, "build", "envs", "eval_" + args.tag)
    os.makedirs(cwd, exist_ok=True)
    env = SPDEnv(args.seedbase, cwd)
    try:
        env.proc.stdin.write("target %d\n" % args.target)
        env.proc.stdin.flush()
        env._read()
    except Exception as ex:
        print("[eval] target set failed:", ex)

    reached = 0
    deaths = 0
    depths, rets, steps_l = [], [], []
    seen_exit = 0
    on_exit = 0
    used8 = 0
    max_explored = []
    EVER = 0
    t0 = time.time()
    try:
        for ep in range(args.episodes):
            seed = args.seedbase + ep
            obs, fields = env.reset(seed)
            o = np.asarray(obs, dtype=np.int64)
            rsum = 0
            steps = 0
            saw_exit = False
            stood_exit = False
            a8 = 0
            mx = 0
            while steps < args.max_steps:
                m = o[:NMAP].astype(np.uint16)
                s = o[NMAP:].astype(np.int32)
                mb = torch.from_numpy(expand(m[None, :])).to(device)
                sb = torch.from_numpy(norm_scalars(s[None, :], mean, std)).to(device)
                with torch.no_grad():
                    logits, _ = model(mb, sb)
                a = int(logits.argmax(1).item())
                if a == 1:
                    a8 += 1
                g = o[NMAP + 7:NMAP + 15]
                if int(g[6]) > 0:
                    saw_exit = True
                if int(g[7]) > 0:
                    stood_exit = True
                mx = max(mx, int(g[1]))
                obs, r, done, fields, _ = env.step(a)
                o = np.asarray(obs, dtype=np.int64)
                rsum += r
                steps += 1
                if done:
                    break
            if saw_exit:
                seen_exit += 1
            if stood_exit:
                on_exit += 1
            used8 += a8
            max_explored.append(mx)
            d = int(fields.get("depth", 1))
            depths.append(d)
            rets.append(rsum)
            steps_l.append(steps)
            if d >= 2:
                reached += 1
            print("[ep %2d] seed=%d depth=%d ret=%d steps=%d sawExit=%s onExit=%s a8=%d maxExp=%d"
                  % (ep, seed, d, rsum, steps, saw_exit, stood_exit, a8, mx), flush=True)
    finally:
        env.close()

    n = args.episodes
    hist = {}
    for d in depths:
        hist[d] = hist.get(d, 0) + 1
    hs = " ".join("d%d=%d" % (k, hist[k]) for k in sorted(hist))
    ge = {k: sum(1 for d in depths if d >= k) for k in (2, 3, 4, 5)}
    print("=== %s: reached>=2 %d/%d  >=3 %d  >=4 %d  >=5 %d | hist=[%s] | avg_depth=%.2f avg_ret=%.1f avg_steps=%.1f | sawExit=%d onExit=%d exitMacro=%d | %.0fs ==="
          % (args.tag, reached, n, ge[3], ge[4], ge[5], hs, float(np.mean(depths)),
             float(np.mean(rets)), float(np.mean(steps_l)), seen_exit, on_exit, used8,
             time.time() - t0), flush=True)


if __name__ == "__main__":
    main()
