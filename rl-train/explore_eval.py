# -*- coding: utf-8 -*-
"""评估策略换层时各层的探索覆盖率（explored/passable）。
用法：python rl-train/explore_eval.py rl-train/out/bc5_combat.pt --episodes 20 --seedbase 950001
"""
import argparse
import os
import numpy as np
import torch
from spd_v2 import N_ACT, NMAP, N_SCALAR, BASE, expand, load_ckpt, norm_scalars
from train_ppo_vec import SPDEnv


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("ckpt")
    ap.add_argument("--episodes", type=int, default=20)
    ap.add_argument("--seedbase", type=int, default=950001)
    ap.add_argument("--max-steps", type=int, default=3000)
    ap.add_argument("--target", type=int, default=6)
    ap.add_argument("--tag", default="explore")
    args = ap.parse_args()

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model, mean, std, meta = load_ckpt(args.ckpt, device)
    print("[explore] ckpt=%s device=%s" % (args.ckpt, device), flush=True)

    cwd = os.path.join(BASE, "build", "envs", "expl_" + args.tag)
    os.makedirs(cwd, exist_ok=True)
    env = SPDEnv(args.seedbase, cwd)
    try:
        env.proc.stdin.write("target %d\n" % args.target)
        env.proc.stdin.flush()
        env._read()
    except Exception as ex:
        print("target set failed:", ex)

    cov = {}              # depth -> list[(explored, passable)]
    all_final = []
    for ep in range(args.episodes):
        seed = args.seedbase + ep
        obs, _ = env.reset(seed)
        o = np.asarray(obs, dtype=np.int64)
        steps = 0
        prev_depth = 1
        ep_cov = {}
        g = o[NMAP + 7:NMAP + 15]
        m0 = o[:NMAP].astype(np.uint16)
        kp = int((((m0 & 1) != 0) & ((m0 & 2) != 0)).sum())
        pending_ex, pending_pa = kp, int(g[2])
        while steps < args.max_steps:
            m = o[:NMAP].astype(np.uint16)
            s = o[NMAP:].astype(np.int32)
            mb = torch.from_numpy(expand(m[None, :])).to(device)
            sb = torch.from_numpy(norm_scalars(s[None, :], mean, std)).to(device)
            with torch.no_grad():
                logits, _ = model(mb, sb)
            a = int(logits.argmax(1).item())
            obs, r, done, fields, _ = env.step(a)
            o = np.asarray(obs, dtype=np.int64)
            cur_depth = int(fields.get("depth", 1))
            if cur_depth > prev_depth:
                ep_cov.setdefault(prev_depth, []).append((pending_ex, pending_pa))
            prev_depth = cur_depth
            g = o[NMAP + 7:NMAP + 15]
            mc = o[:NMAP].astype(np.uint16)
            kpc = int((((mc & 1) != 0) & ((mc & 2) != 0)).sum())
            pending_ex, pending_pa = kpc, int(g[2])
            steps += 1
            if done:
                break
        all_final.append(prev_depth)
        per = {}
        for d, lst in ep_cov.items():
            rs = [ex / pa for ex, pa in lst if pa > 0]
            if rs:
                per[d] = "%.0f%%" % (100 * float(np.mean(rs)))
        print("[ep %2d] seed=%d final_depth=%d cov=%s"
              % (ep, seed, prev_depth, per), flush=True)
    env.close()

    print("=== 汇总：各层换层瞬间的探索覆盖率 ===", flush=True)
    for d in sorted(cov):
        lst = cov[d]
        rs = [ex / pa for ex, pa in lst if pa > 0]
        if rs:
            print("depth %d: n=%d  mean=%.0f%%  min=%.0f%%  max=%.0f%%"
                  % (d, len(rs), 100 * float(np.mean(rs)), 100 * float(np.min(rs)),
                     100 * float(np.max(rs))), flush=True)
    hist = {k: all_final.count(k) for k in sorted(set(all_final))}
    print("final depths hist: %s" % hist, flush=True)


if __name__ == "__main__":
    main()
