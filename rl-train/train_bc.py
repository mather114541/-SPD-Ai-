"""
行为克隆（Behavior Cloning）监督训练脚本。
读取 RLExpert 生成的轨迹（每行 1417 观察值 + 末尾一个动作 0-11），
用交叉熵监督训练与 PPO train_ppo.py 的 ActorCritic **完全相同**的共享网络层
（net: 1417->512->256, Tanh；actor: 256->12，层名与初始化方式一致），
输出初始化权重，供 train_ppo.py 用 --init 加载作为 PPO actor 起点（路线C）。

用法：python train_bc.py --traj rl-train/out/traj.txt --out rl-train/out/bc_init.pt --epochs 30
"""
import argparse
import math
import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F

OBS_LEN = 1434
N_ACT = 13


def layer_init(layer, scale=1.0):
    nn.init.orthogonal_(layer.weight, gain=scale)
    nn.init.zeros_(layer.bias)
    return layer


class MLP(nn.Module):
    """与 train_ppo.py 的 ActorCritic.net + actor 完全同架构、同层名、同初始化。"""
    def __init__(self):
        super().__init__()
        self.net = nn.Sequential(
            layer_init(nn.Linear(OBS_LEN, 512), scale=math.sqrt(2)),
            nn.Tanh(),
            layer_init(nn.Linear(512, 256), scale=math.sqrt(2)),
            nn.Tanh(),
        )
        self.actor = layer_init(nn.Linear(256, N_ACT), scale=0.01)

    def forward(self, x):
        return self.actor(self.net(x))


def load_traj(path):
    X, Y = [], []
    with open(path, 'r', encoding='utf-8') as f:
        for line in f:
            line = line.strip()
            if not line:
                continue
            parts = line.split()
            y = int(parts[-1])
            x = [float(v) for v in parts[:-1]]
            if len(x) != OBS_LEN or not (0 <= y < N_ACT):
                continue
            X.append(x)
            Y.append(y)
    return np.array(X, dtype=np.float32), np.array(Y, dtype=np.int64)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('--traj', default='rl-train/out/traj.txt')
    ap.add_argument('--out', default='rl-train/out/bc_init.pt')
    ap.add_argument('--epochs', type=int, default=40)
    ap.add_argument('--lr', type=float, default=3e-4)
    ap.add_argument('--batch', type=int, default=512)
    ap.add_argument('--act-weight', type=float, default=1.0, help='对换层动作 action8 样本加权（默认1=不加权；>1 让 BC 学会"站出口格→点脚下换层"条件反射）')
    ap.add_argument('--exit-weight', type=float, default=1.0, help='对已探到出口(EXIT扇区1425..1432>0)状态的样本加权（默认1=不加权；>1 让 BC 专注学"走向出口→换层"，不被探索动作主导）')
    args = ap.parse_args()

    X, Y = load_traj(args.traj)
    print(f'轨迹样本: {X.shape[0]} 条')
    if X.shape[0] < 200:
        print('样本过少，建议检查轨迹生成')
    uniq, cnt = np.unique(Y, return_counts=True)
    print('动作分布:', dict(zip(uniq.tolist(), cnt.tolist())))

    model = MLP()
    opt = torch.optim.Adam(model.parameters(), lr=args.lr)
    cw = torch.ones(N_ACT)
    if args.act_weight > 1.0:
        cw[8] = args.act_weight   # 换层动作 action8（点脚下）加权：轨迹里仅 ~0.9% 样本，不加权则 BC 学不到换层
    crit = nn.CrossEntropyLoss(weight=cw)
    Xb = torch.tensor(X)
    Yb = torch.tensor(Y)
    N = Xb.shape[0]

    model.train()
    for epoch in range(args.epochs):
        idx = np.random.permutation(N)
        tot = 0.0
        for i in range(0, N, args.batch):
            b = idx[i:i + args.batch]
            logits = model(Xb[b])
            if args.exit_weight > 1.0:
                exit_mask = (Xb[b, 1425:1433].sum(1) > 0).float()   # 已探到出口状态样本
                w = 1.0 + (args.exit_weight - 1.0) * exit_mask
                loss = (F.cross_entropy(logits, Yb[b], weight=cw, reduction='none') * w).mean()
            else:
                loss = crit(logits, Yb[b])
            opt.zero_grad()
            loss.backward()
            opt.step()
            tot += loss.item() * len(b)
        if epoch % 5 == 0 or epoch == args.epochs - 1:
            print(f'epoch {epoch} loss {tot / N:.4f}')

    # 保存 net.* + actor.*，供 PPO load_state_dict(strict=False) 用（critic 随机初始化）
    torch.save(model.state_dict(), args.out)
    print(f'已保存 BC 初始化权重: {args.out} (net+actor，可与 PPO 对齐)')


if __name__ == '__main__':
    main()
