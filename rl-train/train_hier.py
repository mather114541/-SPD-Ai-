"""
分层策略 v5（方案2+1 修正版）：上层 GRU 直接输出【方向意图】，学会系统扫图。
- 下层：冻结 bc_rich6（局部避障/保命）。
- 导引模块（确定性，玩家同权）：给定目标方向扇区 -> 方向试探避障（撞墙依次试相邻扇区），
  hero.pos 检测撞墙。只做路径执行，方向选择由上层学习。
- 上层：HighGRU（17 维高层特征 -> GRU 64 -> 方向意图 10 类 + 价值）。
  方向意图 {0..7=探索该方向扇区, 8=去出口(EXIT方向), 9=换层(ONEXIT时点脚下)}。
  GRU 从历史特征记忆"哪扫过、哪还没去"，系统化扫图（不再依赖无记忆的 DIR argmax）。
- DAgger 专家扫图：50% 注入 RLExpert 扫图方向（站出口->9；已探出口->8；否则->DIR 未探扇区），
  这些步做 BC 监督（-logp）。退火：eps_expert 0.5 线性衰减到 0，后期 100% 纯 PPO。
- 指标：20 万步，eval exit_seen 稳定 >= 4/6（探索覆盖率 60%+）。

用法：python train_hier.py --steps 200000 --nenv 6 --time-steps 2048 --tag hier5 --eps-expert 0.5 --bc-coef 1.0
"""
import argparse
import math
import time
import numpy as np
import torch
import torch.nn as nn
import torch.nn.functional as F

from train_ppo_vec import SPDEnv, VecEnv

OBS_LEN = 1434
N_ACT = 13
HIGH_DIM = 17
N_INTENT = 10
OFF_DIR = slice(1417, 1425)
OFF_EXIT = slice(1425, 1433)
ONEXIT_IDX = 1433
POS_IDX = 1324
GRU_HID = 64
SECTOR2ACT = {0: 4, 1: 7, 2: 6, 3: 5, 4: 3, 5: 0, 6: 1, 7: 2}


def layer_init(layer, scale=1.0):
    nn.init.orthogonal_(layer.weight, gain=scale)
    nn.init.zeros_(layer.bias)
    return layer


class LowerPolicy(nn.Module):
    def __init__(self, ckpt):
        super().__init__()
        self.net = nn.Sequential(
            layer_init(nn.Linear(OBS_LEN, 512), scale=math.sqrt(2)),
            nn.Tanh(),
            layer_init(nn.Linear(512, 256), scale=math.sqrt(2)),
            nn.Tanh(),
        )
        self.actor = layer_init(nn.Linear(256, N_ACT), scale=0.01)
        sd = torch.load(ckpt, map_location='cpu')
        self.load_state_dict(sd, strict=False)
        for p in self.parameters():
            p.requires_grad_(False)
        self.eval()

    @torch.no_grad()
    def act(self, o_batch):
        return self.actor(self.net(o_batch)).argmax(-1)


class HighGRU(nn.Module):
    def __init__(self):
        super().__init__()
        self.gru = nn.GRU(HIGH_DIM, GRU_HID, batch_first=False)
        self.actor = layer_init(nn.Linear(GRU_HID, N_INTENT), scale=0.01)
        self.critic = layer_init(nn.Linear(GRU_HID, 1), scale=1.0)

    def get(self, x, hidden):
        x = x.unsqueeze(0)                 # [1,N,17]
        h, hidden = self.gru(x, hidden)
        h = h.squeeze(0)                   # [N,64]
        return self.actor(h), self.critic(h), hidden


def extract_high(o):
    ex = o[OFF_EXIT]
    dr = o[OFF_DIR]
    on = o[ONEXIT_IDX]
    return np.concatenate([ex, dr, [float(on)]]).astype(np.float32)


def expert_direction(o):
    """RLExpert 扫图方向：站出口->9(换层)；已探出口->8(去出口)；否则->DIR 最大未探扇区(0-7)。"""
    o = np.asarray(o, dtype=np.float32)
    if o[ONEXIT_IDX] > 0:
        return 9
    if o[OFF_EXIT].max() > 0:
        return 8
    if o[OFF_DIR].max() > 0:
        return int(o[OFF_DIR].argmax())
    return 0


def target_sector(d_intent, o):
    """把方向意图映射为目标方向扇区（0-7）或特殊值 9(换层)/None。"""
    o = np.asarray(o, dtype=np.float32)
    on = o[ONEXIT_IDX]
    if d_intent == 9:
        return 9 if on > 0 else None
    if d_intent == 8:
        if o[OFF_EXIT].max() > 0:
            return int(o[OFF_EXIT].argmax())
        return None
    if 0 <= d_intent <= 7:
        return int(d_intent)
    return None


def next_off(off):
    if off <= -4 or off >= 4:
        return -1
    if off > 0:
        return -off
    if off < 0:
        return -off + 1
    return 1


def is_move(a):
    return 0 <= int(a) <= 7


def eval_hier(lower, high, device, seed_base, episodes=6, max_steps=1600):
    high.eval()
    depths, rets, targets, seen = [], [], 0, 0
    for ep in range(episodes):
        env = SPDEnv(seed_base + ep)
        try:
            o, _ = env.reset(seed_base + ep)
            rsum, steps, depth, saw_exit = 0.0, 0, 1, False
            done = False
            prev_pos = int(o[POS_IDX])
            trying, off, attempts = False, 0, 0
            hidden = torch.zeros(1, 1, GRU_HID, device=device)
            while not done and steps < max_steps:
                h = torch.tensor(extract_high(o), dtype=torch.float32).unsqueeze(0).to(device)
                with torch.no_grad():
                    logits, _, hidden = high.get(h, hidden)
                    d_intent = int(logits.argmax(-1).item())
                low = int(lower.act(torch.tensor(o, dtype=torch.float32).unsqueeze(0).to(device)).item())
                tgt = target_sector(d_intent, o)
                if tgt == 9:
                    act = 8; trying, off, attempts = False, 0, 0
                elif tgt is None:
                    act = low; trying, off, attempts = False, 0, 0
                elif trying:
                    act = SECTOR2ACT[(tgt + off) % 8]
                else:
                    act = SECTOR2ACT[tgt]
                o, r, done, fields, _ = env.step(act)
                new_pos = int(o[POS_IDX])
                moved = new_pos != prev_pos
                if tgt == 9 or tgt is None:
                    trying, off, attempts = False, 0, 0
                elif trying:
                    if moved:
                        trying, off, attempts = False, 0, 0
                    else:
                        off = next_off(off); attempts += 1
                        if off == -1 or attempts > 8:
                            trying, off, attempts = False, 0, 0
                elif is_move(act) and not moved:
                    trying, off, attempts = True, 1, 1
                prev_pos = new_pos
                if not saw_exit and np.max(o[OFF_EXIT]) > 0:
                    saw_exit = True
                depth = int(fields.get("depth", depth))
                rsum += r
                steps += 1
            rets.append(rsum)
            depths.append(depth)
            if depth >= 2:
                targets += 1
            if saw_exit:
                seen += 1
        finally:
            env.close()
    return (float(np.mean(depths)), targets, float(np.mean(rets)), seen)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--lower", default="rl-train/out/bc_rich6.pt")
    ap.add_argument("--out", default="rl-train/out/hier.pt")
    ap.add_argument("--steps", type=int, default=200000)
    ap.add_argument("--time-steps", type=int, default=2048)
    ap.add_argument("--nenv", type=int, default=6)
    ap.add_argument("--lr", type=float, default=3e-4)
    ap.add_argument("--seed", type=int, default=0)
    ap.add_argument("--tag", default="hier")
    ap.add_argument("--eval-ep", type=int, default=6)
    ap.add_argument("--eps-expert", type=float, default=0.5)
    ap.add_argument("--bc-coef", type=float, default=1.0)
    args = ap.parse_args()

    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    print(f"[env] device={device} nenv={args.nenv}")
    if torch.cuda.is_available():
        print(f"[env] gpu={torch.cuda.get_device_name(0)}")

    lower = LowerPolicy(args.lower).to(device)
    high = HighGRU().to(device)
    print(f"[train] 下层冻结加载: {args.lower} | 上层 HighGRU({GRU_HID}) 输出方向意图({N_INTENT})")
    print(f"[train] DAgger: eps_expert={args.eps_expert} 线性退火->0, bc_coef={args.bc_coef}")

    opt = torch.optim.Adam(high.parameters(), lr=args.lr, eps=1e-5)
    T = args.time_steps
    N = args.nenv
    envs = VecEnv([args.seed + i for i in range(N)])
    obs_list = envs.reset_all([args.seed + i for i in range(N)])

    PER_ITER = T * N
    iters = max(1, args.steps // PER_ITER)
    EVAL_EVERY = max(1, iters // 24)
    gamma, lam = 0.99, 0.95
    high.train()
    t0 = time.time()
    for it in range(iters):
        eps_expert = args.eps_expert * max(0.0, 1.0 - it / max(1, iters - 1))
        obs_t = np.zeros((T, N, HIGH_DIM), dtype=np.float32)
        hid_t = np.zeros((T, N, 1, GRU_HID), dtype=np.float32)
        act_t = np.zeros((T, N), dtype=np.int64)
        logp_t = np.zeros((T, N), dtype=np.float32)
        rew_t = np.zeros((T, N), dtype=np.float32)
        don_t = np.zeros((T, N), dtype=bool)
        val_t = np.zeros((T, N), dtype=np.float32)
        exp_t = np.zeros((T, N), dtype=bool)
        prev_pos = [int(o[POS_IDX]) for o in obs_list]
        trying = [False] * N
        off = [0] * N
        attempts = [0] * N
        hidden = torch.zeros(1, N, GRU_HID, device=device)
        for t in range(T):
            o_batch = torch.stack([torch.tensor(extract_high(o), dtype=torch.float32) for o in obs_list], 0).to(device)
            hid_t[t] = hidden.detach().cpu().numpy().transpose(1, 0, 2)
            with torch.no_grad():
                logits, value, hidden = high.get(o_batch, hidden)
                probs = F.softmax(logits, -1)
                dist = torch.distributions.Categorical(probs)
                intent_pi = dist.sample()
                logp_pi = dist.log_prob(intent_pi)
            use_expert = (torch.rand(N) < eps_expert).tolist()
            intent = torch.zeros(N, dtype=torch.long)
            logp = torch.zeros(N)
            for i in range(N):
                if use_expert[i]:
                    intent[i] = expert_direction(obs_list[i])
                    with torch.no_grad():
                        lp = dist.log_prob(torch.tensor([intent[i]]).to(device))[0]
                    logp[i] = lp
                else:
                    intent[i] = intent_pi[i]
                    logp[i] = logp_pi[i]
            exp_t[t] = torch.tensor(use_expert, dtype=torch.bool)
            obs_t[t] = o_batch.cpu().numpy()
            act_t[t] = intent.cpu().numpy()
            logp_t[t] = logp.cpu().numpy()
            val_t[t] = value.cpu().numpy().reshape(-1)
            low_batch = lower.act(torch.stack([torch.tensor(o, dtype=torch.float32) for o in obs_list], 0).to(device)).cpu().numpy()
            tgts = [target_sector(int(intent[i].item()), np.asarray(obs_list[i])) for i in range(N)]
            acts = []
            for i in range(N):
                tgt = tgts[i]
                if tgt == 9:
                    acts.append(8); trying[i], off[i], attempts[i] = False, 0, 0
                elif tgt is None:
                    acts.append(int(low_batch[i])); trying[i], off[i], attempts[i] = False, 0, 0
                elif trying[i]:
                    acts.append(SECTOR2ACT[(tgt + off[i]) % 8])
                else:
                    acts.append(SECTOR2ACT[tgt])
            obs_list, rew_list, done_list, _, restart_list = envs.step_all(acts)
            for i in range(N):
                new_pos = int(obs_list[i][POS_IDX])
                moved = new_pos != prev_pos[i]
                tgt = tgts[i]
                if tgt == 9 or tgt is None:
                    trying[i], off[i], attempts[i] = False, 0, 0
                elif trying[i]:
                    if moved:
                        trying[i], off[i], attempts[i] = False, 0, 0
                    else:
                        off[i] = next_off(off[i]); attempts[i] += 1
                        if off[i] == -1 or attempts[i] > 8:
                            trying[i], off[i], attempts[i] = False, 0, 0
                elif is_move(acts[i]) and not moved:
                    trying[i], off[i], attempts[i] = True, 1, 1
                prev_pos[i] = new_pos
            rew_t[t] = rew_list
            don_t[t] = [d or rt for d, rt in zip(done_list, restart_list)]
        obs_f = torch.tensor(obs_t.reshape(-1, HIGH_DIM))
        hid_f = torch.tensor(hid_t.reshape(-1, 1, GRU_HID))
        act_f = torch.tensor(act_t.reshape(-1))
        logp_f = torch.tensor(logp_t.reshape(-1))
        rew_f = torch.tensor(rew_t.reshape(-1))
        don_f = torch.tensor(don_t.reshape(-1))
        val_f = torch.tensor(val_t.reshape(-1))
        exp_f = torch.tensor(exp_t.reshape(-1).astype(np.float32))
        with torch.no_grad():
            _, vnext, _ = high.get(obs_f[-N:].to(device), torch.zeros(1, N, GRU_HID, device=device))
            vnext = vnext.cpu().reshape(-1)
        last_val = torch.zeros(N)
        for i in range(N):
            last_val[i] = 0.0 if don_f[-N + i] else vnext[i]
        adv = torch.zeros_like(rew_f)
        gae = torch.zeros(N)
        for t in reversed(range(T)):
            idx = t * N + np.arange(N)
            not_done = (~don_f[idx]).float()
            if t < T - 1:
                nextidx = (t + 1) * N + np.arange(N)
                delta = rew_f[idx] + gamma * val_f[nextidx] * not_done - val_f[idx]
                gae = delta + gamma * lam * not_done * gae
            else:
                delta = rew_f[idx] + gamma * last_val * not_done - val_f[idx]
                gae = delta
            adv[idx] = gae
        returns = adv + val_f
        adv_f = (adv - adv.mean()) / (adv.std() + 1e-8)
        returns_f = (returns - returns.mean()) / (returns.std() + 1e-8)
        bsize = 256
        idxall = np.arange(obs_f.shape[0])
        for _ in range(3):
            np.random.shuffle(idxall)
            for i in range(0, len(idxall), bsize):
                b = idxall[i:i + bsize]
                ob = obs_f[b].to(device)
                ac = act_f[b].to(device)
                lp = logp_f[b].to(device)
                hb = hid_f[b].to(device).transpose(0, 1)
                logits, value, _ = high.get(ob, hb)
                probs = F.softmax(logits, -1)
                dist = torch.distributions.Categorical(probs)
                logp_n = dist.log_prob(ac)
                entropy = dist.entropy().mean()
                ratio = torch.exp(logp_n - lp)
                surr1 = ratio * adv_f[b].to(device)
                surr2 = torch.clamp(ratio, 1 - 0.2, 1 + 0.2) * adv_f[b].to(device)
                pi_loss = -torch.min(surr1, surr2).mean()
                vf_loss = 0.5 * ((value - returns_f[b].to(device)) ** 2).mean()
                bc_loss = (-logp_n * exp_f[b].to(device)).mean()
                loss = pi_loss + 0.5 * vf_loss - 0.01 * entropy + args.bc_coef * bc_loss
                opt.zero_grad()
                loss.backward()
                nn.utils.clip_grad_norm_(high.parameters(), 0.5)
                opt.step()
        if (it + 1) % EVAL_EVERY == 0 or it == iters - 1:
            high.eval()
            avg_d, targets, avg_r, seen = eval_hier(lower, high, device, args.seed, args.eval_ep)
            high.train()
            el = time.time() - t0
            print(f"[iter {it + 1}/{iters}] steps={PER_ITER * (it + 1)} eps={eps_expert:.3f} | eval avg_depth={avg_d:.2f} avg_ret={avg_r:.2f} targets={targets}/{args.eval_ep} exit_seen={seen}/{args.eval_ep} | {el:.0f}s", flush=True)
            torch.save({"high": high.state_dict()}, args.out)
    envs.close()
    print(f"[done] 保存: {args.out}")


if __name__ == "__main__":
    main()
