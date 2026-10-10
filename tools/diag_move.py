# -*- coding: utf-8 -*-
"""诊断：环境里英雄是否真的能移动/换层。reset 后逐步喂动作，打印 pos/depth。"""
import os, sys, time
BASE = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, os.path.join(BASE, "rl-train"))
from train_ppo import SPDEnv

def main():
    env = SPDEnv(2026)
    obs, fields = env.reset(2026)
    def stats(o):
        explore = o[1330+2] if len(o) > 1332 else -1     # OFF_GLOBAL+2 已探格
        fov = sum(1 for x in o[882:1323] if x > 0) if len(o) >= 1323 else -1  # FOV 通道非零
        return explore, fov
    e0, f0 = stats(obs)
    print("reset depth=%s pos=%s explore=%s fov=%s" % (fields.get("depth"), fields.get("pos"), e0, f0), flush=True)
    time.sleep(0.3)

    for a in range(9):
        o, r, d, f, rest = env.step(a)
        e, fv = stats(o)
        print("a=%d -> pos=%s depth=%s reward=%d explore=%s fov=%s" % (
            a, f.get("pos"), f.get("depth"), r, e, fv), flush=True)
        time.sleep(0.3)

    env.close()

    # 连发动作 4（假设向下/随机）10 次看累计
    print("--- spam action 4 x10 ---", flush=True)
    for i in range(10):
        o, r, d, f, rest = env.step(4)
        print("step%d pos=%s depth=%s reward=%d done=%s" % (i, f.get("pos"), f.get("depth"), r, d), flush=True)
        time.sleep(0.2)

    env.close()

if __name__ == "__main__":
    main()
