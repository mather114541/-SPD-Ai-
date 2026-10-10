# -*- coding: utf-8 -*-
"""观测 v2 的 CNN 行为克隆训练。读取 rl-train/out/traj2_*.bin，训练并保存 bc2.pt。

用法：python rl-train/train_bc2.py --glob "rl-train/out/traj2_*.bin" --out rl-train/out/bc2.pt --epochs 15
"""
import argparse
import glob
import os
import time

import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F

from spd_v2 import CNN, NMAP, N_SCALAR, N_ACT, expand, norm_scalars, read_bin, save_ckpt

torch.backends.cudnn.benchmark = True


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--glob", default="rl-train/out/traj2_*.bin")
    ap.add_argument("--out", default="rl-train/out/bc2.pt")
    ap.add_argument("--epochs", type=int, default=15)
    ap.add_argument("--batch", type=int, default=256)
    ap.add_argument("--lr", type=float, default=3e-4)
    ap.add_argument("--exit-weight", type=float, default=4.0, help="换层动作 action8 的类别权重")
    ap.add_argument("--val-frac", type=float, default=0.05)
    ap.add_argument("--max-samples", type=int, default=0, help=">0 时只用前 N 条（调试）")
    args = ap.parse_args()

    files = sorted(glob.glob(args.glob))
    if not files:
        raise SystemExit("no files matched " + args.glob)
    print("[data] files=%d" % len(files))
    maps, scs, acts = [], [], []
    for f in files:
        mm = read_bin(f)
        maps.append(np.asarray(mm["map"]))
        scs.append(np.asarray(mm["sc"]))
        acts.append(np.asarray(mm["act"]))
        print("   %s n=%d" % (os.path.basename(f), len(mm)))
    M = np.concatenate(maps)
    S = np.concatenate(scs).astype(np.float32)
    A = np.concatenate(acts).astype(np.int64)
    if args.max_samples > 0:
        M, S, A = M[:args.max_samples], S[:args.max_samples], A[:args.max_samples]
    N = len(A)
    print("[data] total=%d  action hist=%s" % (N, np.bincount(A, minlength=N_ACT).tolist()))

    mean = S.mean(axis=0)
    std = np.maximum(S.std(axis=0), 1e-3)
    S = norm_scalars(S, mean, std)

    perm = np.random.permutation(N)
    nval = max(1, int(N * args.val_frac))
    val_idx, tr_idx = perm[:nval], perm[nval:]
    print("[data] train=%d val=%d" % (len(tr_idx), len(val_idx)))

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print("[env] device=%s %s" % (device, torch.cuda.get_device_name(0) if torch.cuda.is_available() else ""))
    model = CNN().to(device)
    opt = torch.optim.Adam(model.parameters(), lr=args.lr, eps=1e-5)
    cw = torch.ones(N_ACT)
    cw[1] = args.exit_weight   # M_EXIT
    cw = cw.to(device)

    def run_eval(idx, bs=1024):
        model.eval()
        correct = 0
        tot = 0
        c8c = 0
        c8t = 0
        loss_sum = 0.0
        with torch.no_grad():
            for i in range(0, len(idx), bs):
                b = idx[i:i + bs]
                mb = torch.from_numpy(expand(M[b])).to(device)
                sb = torch.from_numpy(S[b]).to(device)
                ab = torch.from_numpy(A[b]).to(device)
                logits, _ = model(mb, sb)
                loss_sum += F.cross_entropy(logits, ab, weight=cw).item() * len(b)
                pred = logits.argmax(1)
                correct += int((pred == ab).sum())
                tot += len(b)
                m8 = ab == 1
                c8t += int(m8.sum())
                c8c += int(((pred == 1) & m8).sum())
        model.train()
        return loss_sum / tot, correct / tot, (c8c / c8t if c8t else float("nan")), c8t

    t0 = time.time()
    for ep in range(args.epochs):
        model.train()
        p = np.random.permutation(tr_idx)
        tl = 0.0
        correct = 0
        tot = 0
        for i in range(0, len(p), args.batch):
            b = p[i:i + args.batch]
            mb = torch.from_numpy(expand(M[b])).to(device)
            sb = torch.from_numpy(S[b]).to(device)
            ab = torch.from_numpy(A[b]).to(device)
            logits, _ = model(mb, sb)
            loss = F.cross_entropy(logits, ab, weight=cw)
            opt.zero_grad()
            loss.backward()
            nn.utils.clip_grad_norm_(model.parameters(), 5.0)
            opt.step()
            tl += loss.item() * len(b)
            correct += int((logits.argmax(1) == ab).sum())
            tot += len(b)
        vl, va, r8, n8 = run_eval(val_idx)
        print("[ep %2d] train_loss=%.4f train_acc=%.4f | val_loss=%.4f val_acc=%.4f recall@8=%.3f (n8=%d) | %.0fs"
              % (ep, tl / tot, correct / tot, vl, va, r8, n8, time.time() - t0), flush=True)
        save_ckpt(args.out, model, mean, std, {"files": files, "N": N, "epochs": ep + 1})
    print("[done] saved " + args.out)


if __name__ == "__main__":
    main()
