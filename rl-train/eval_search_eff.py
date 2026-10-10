# -*- coding: utf-8 -*-
"""精确评估"有效搜索"占比：每次 M_SEARCH 动作后，统计地图上新出现的 B_DOOR/B_TRAP 格增量。
M_SEARCH 是站定搜索（Hero 不移动），搜索后视野不变，该步新增的门/陷阱即该次搜索真发现的秘密。
有效搜索 = 搜索后 door/trap 位域增量 > 0。天然门/陷阱在探索移动时出现，不归因给搜索步。

用法：python rl-train/eval_search_eff.py rl-train/out/ppo_search3.pt --raw --norm-ckpt rl-train/out/bc6.pt --episodes 8
"""
import argparse
import os
import time

import numpy as np
import torch

from spd_v2 import N_ACT, NMAP, N_SCALAR, BASE, expand, load_ckpt, norm_scalars
from train_ppo_vec import SPDEnv

B_TRAP = 1 << 10
B_DOOR = 1 << 3
SEARCH = 5  # M_SEARCH


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("ckpt")
    ap.add_argument("--episodes", type=int, default=8)
    ap.add_argument("--seedbase", type=int, default=970001)
    ap.add_argument("--max-steps", type=int, default=1200)
    ap.add_argument("--target", type=int, default=6)
    ap.add_argument("--tag", default="eff")
    ap.add_argument("--raw", action="store_true")
    ap.add_argument("--norm-ckpt", default=None)
    args = ap.parse_args()

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    if args.raw:
        st = torch.load(args.ckpt, map_location=device, weights_only=False)
        from spd_v2 import CNN
        model = CNN().to(device)
        model.load_state_dict(st)
        model.eval()
        nrm = torch.load(args.norm_ckpt, map_location="cpu", weights_only=False)
        mean, std = nrm["mean"], nrm["std"]
    else:
        model, mean, std, _ = load_ckpt(args.ckpt, device)
    print("[eval] ckpt=%s device=%s" % (args.ckpt, device), flush=True)

    cwd = os.path.join(BASE, "build", "envs", "eval_" + args.tag)
    os.makedirs(cwd, exist_ok=True)
    env = SPDEnv(args.seedbase, cwd)
    try:
        env.proc.stdin.write("target %d\n" % args.target)
        env.proc.stdin.flush()
        env._read()
    except Exception as ex:
        print("[eval] target set failed:", ex)

    tot_search = 0
    eff_search = 0
    eps_with_eff = 0
    door_found = 0
    trap_found = 0
    reached2 = 0
    depths, rets = [], []
    t0 = time.time()
    try:
        for ep in range(args.episodes):
            seed = args.seedbase + ep
            obs, fields = env.reset(seed)
            o = np.asarray(obs, dtype=np.int64)
            prev_depth = int(fields.get("depth", 1))
            e_door = e_trap = 0
            s_cnt = 0
            e_cnt = 0
            prev_doors = None
            prev_traps = None
            rsum = 0
            steps = 0
            while steps < args.max_steps:
                m = o[:NMAP].astype(np.uint16)
                d_cur = int(fields.get("depth", prev_depth))
                if d_cur != prev_depth:
                    prev_doors = None
                    prev_traps = None
                    prev_depth = d_cur
                if prev_doors is None:
                    prev_doors = int(np.count_nonzero(m & B_DOOR))
                    prev_traps = int(np.count_nonzero(m & B_TRAP))

                s = o[NMAP:].astype(np.int32)
                mb = torch.from_numpy(expand(m[None, :])).to(device)
                sb = torch.from_numpy(norm_scalars(s[None, :], mean, std)).to(device)
                with torch.no_grad():
                    logits, _ = model(mb, sb)
                a = int(logits.argmax(1).item())

                obs, r, done, fields, _ = env.step(a)
                o_new = np.asarray(obs, dtype=np.int64)
                m_new = o_new[:NMAP].astype(np.uint16)
                nd = int(np.count_nonzero(m_new & B_DOOR))
                nt = int(np.count_nonzero(m_new & B_TRAP))

                # 归因：搜索步(站定)后新增的门/陷阱 = 该次搜索发现
                if a == SEARCH:
                    s_cnt += 1
                    gain = (nd - prev_doors) + (nt - prev_traps)
                    if gain > 0:
                        e_cnt += 1
                        eff_search += 1
                        if nd > prev_doors:
                            e_door += nd - prev_doors
                        if nt > prev_traps:
                            e_trap += nt - prev_traps
                else:
                    # 非搜索步的新增（天然门/踩陷阱），只更新全局发现计数，不归因搜索
                    if nd > prev_doors:
                        door_found += nd - prev_doors
                    if nt > prev_traps:
                        trap_found += nt - prev_traps

                prev_doors = nd
                prev_traps = nt
                o = o_new
                rsum += r
                steps += 1
                if done:
                    break
            tot_search += s_cnt
            if e_cnt > 0:
                eps_with_eff += 1
            door_found += e_door
            trap_found += e_trap
            d = int(fields.get("depth", 1))
            depths.append(d)
            rets.append(rsum)
            if d >= 2:
                reached2 += 1
            print("[ep %2d] seed=%d depth=%d ret=%d steps=%d search=%d eff_search=%d(%.1f%%) | 搜索发现 door+=%d trap+=%d"
                  % (ep, seed, d, rsum, steps, s_cnt, e_cnt,
                     (100.0 * e_cnt / s_cnt) if s_cnt else 0.0, e_door, e_trap), flush=True)
    finally:
        env.close()

    n = args.episodes
    eff_total = sum(1 for _ in [1])  # placeholder
    rate = (100.0 * eff_search / tot_search) if tot_search else 0.0
    print("=== %s: search_total=%d eff_search=%d 有效搜索占比=%.2f%% | 搜索发现 door=%d trap=%d (天然door=%d trap=%d) | "
          "reached>=2 %d/%d avg_depth=%.2f avg_ret=%.1f eps_with_eff=%d | %.0fs ==="
          % (args.tag, tot_search, eff_search, rate, door_found, trap_found, 0, 0,
             reached2, n, float(np.mean(depths)), float(np.mean(rets)), eps_with_eff,
             time.time() - t0), flush=True)


if __name__ == "__main__":
    main()
