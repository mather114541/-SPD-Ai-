# -*- coding: utf-8 -*-
"""评估行为克隆初始化策略：加载 bc_init.pt，argmax 玩若干局，看能否脱离出生层(avg_depth>1)。"""
import os, sys, torch
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from train_ppo import SPDEnv, ActorCritic

def main():
    policy = ActorCritic()
    bc = torch.load('rl-train/out/bc_lc.pt', map_location='cpu')
    policy.load_state_dict(bc, strict=False)
    policy.eval()
    env = SPDEnv(2026)
    depths, rets, deaths, targets, explored_list = [], [], 0, 0, []
    for e in range(10):
        obs, _ = env.reset(777777 + e)
        ret = 0.0; done = False; target = False; steps = 0
        while not done and steps < 300:
            with torch.no_grad():
                feat = policy.net(torch.tensor(obs).float())
                logits = policy.actor(feat)
                action = logits.argmax().item()
            obs, reward, done, fields, _ = env.step(action)
            ret += reward; steps += 1
            explored_list.append(obs[1332])  # GLOBAL: 已探索格数
            if e < 1 and steps <= 40:
                print(f"  step{steps} pos={obs[1324]} a={action}")
            if int(fields.get('depth', 0)) >= 3:
                target = True; break
        depths.append(int(fields.get('depth', 1))); rets.append(ret)
        if done: deaths += 1
        if target: targets += 1
        ev = max(explored_list) if explored_list else 0
        print(f'ep{e} depth={depths[-1]} ret={rets[-1]:.0f} done={done} target={target} steps={steps} explored={ev}')
    print(f'BC eval avg_depth={sum(depths)/len(depths):.2f} avg_ret={sum(rets)/len(rets):.1f} '
          f'deaths={deaths}/{len(depths)} targets={targets}/{len(depths)} '
          f'avg_explored={sum(explored_list)/max(1,len(explored_list)):.0f} max_explored={max(explored_list) if explored_list else 0}')
    env.close()

if __name__ == '__main__':
    main()
