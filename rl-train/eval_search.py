# -*- coding: utf-8 -*-
"""评估 BC 策略的检索能力：M_SEARCH 选择率 + 发现的隐藏门/陷阱数（obs 位域代理）。

用法：python rl-train/eval_search.py rl-train/out/bc6.pt --episodes 30 --seedbase 950001
口径：
- search：网络输出宏 M_SEARCH(a==5) 的次数。
- doorFound：某层内"已见门(B_DOOR=bit3)格数"的增量（秘密门被发现从墙->门 => +1）。
- trapFound：某层内"已见陷阱(B_TRAP=bit10)格数"的增量（隐藏陷阱被发现/触发 => +1）。
换层时重置基线，避免把天然门/陷阱重复计。
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


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("ckpt")
    ap.add_argument("--episodes", type=int, default=30)
    ap.add_argument("--seedbase", type=int, default=950001)
    ap.add_argument("--max-steps", type=int, default=3000)
    ap.add_argument("--target", type=int, default=6)
    ap.add_argument("--tag", default="search")
    ap.add_argument("--raw", action="store_true", help="ckpt 为纯 state_dict(PPO 输出)，用 --norm-ckpt 提供 mean/std")
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
        meta = {"raw": True, "norm": os.path.basename(args.norm_ckpt)}
    else:
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
    depths, rets, steps_l = [], [], []
    total_search = 0
    eps_with_search = 0
    trap_found = 0
    door_found = 0
    t0 = time.time()
    try:
        for ep in range(args.episodes):
            seed = args.seedbase + ep
            obs, fields = env.reset(seed)
            o = np.asarray(obs, dtype=np.int64)
            rsum = 0
            steps = 0
            s_cnt = 0
            e_trap = 0
            e_door = 0
            trap_base = None
            door_base = None
            prev_depth = int(fields.get("depth", 1))
            while steps < args.max_steps:
                m = o[:NMAP].astype(np.uint16)
                ct = int(np.count_nonzero(m & B_TRAP))
                cd = int(np.count_nonzero(m & B_DOOR))
                d_cur = int(fields.get("depth", prev_depth))
                if d_cur != prev_depth:
                    trap_base = None
                    door_base = None
                    prev_depth = d_cur
                if trap_base is None:
                    trap_base = ct
                if door_base is None:
                    door_base = cd
                if ct > trap_base:
                    e_trap += ct - trap_base
                    trap_base = ct
                if cd > door_base:
                    e_door += cd - door_base
                    door_base = cd

                s = o[NMAP:].astype(np.int32)
                mb = torch.from_numpy(expand(m[None, :])).to(device)
                sb = torch.from_numpy(norm_scalars(s[None, :], mean, std)).to(device)
                with torch.no_grad():
                    logits, _ = model(mb, sb)
                a = int(logits.argmax(1).item())
                if a == N_ACT_INDEX_SEARCH:
                    s_cnt += 1
                obs, r, done, fields, _ = env.step(a)
                o = np.asarray(obs, dtype=np.int64)
                rsum += r
                steps += 1
                if done:
                    break
            total_search += s_cnt
            if s_cnt > 0:
                eps_with_search += 1
            trap_found += e_trap
            door_found += e_door
            d = int(fields.get("depth", 1))
            depths.append(d)
            rets.append(rsum)
            steps_l.append(steps)
            if d >= 2:
                reached += 1
            print("[ep %2d] seed=%d depth=%d ret=%d steps=%d search=%d trap+=%d door+=%d"
                  % (ep, seed, d, rsum, steps, s_cnt, e_trap, e_door), flush=True)
    finally:
        env.close()

    n = args.episodes
    ge = {k: sum(1 for d in depths if d >= k) for k in (2, 3, 4, 5)}
    tot_steps = max(1, sum(steps_l))
    print("=== %s: reached>=2 %d/%d >=3 %d >=4 %d >=5 %d | avg_depth=%.2f avg_ret=%.1f | "
          "search_total=%d (%.1f/ep, %.2f%% of steps) eps_with_search=%d | trapFound=%d doorFound=%d | %.0fs ==="
          % (args.tag, reached, n, ge[3], ge[4], ge[5], float(np.mean(depths)), float(np.mean(rets)),
             total_search, total_search / n, total_search * 100.0 / tot_steps, eps_with_search,
             trap_found, door_found, time.time() - t0), flush=True)


N_ACT_INDEX_SEARCH = 5  # M_SEARCH


if __name__ == "__main__":
    main()
