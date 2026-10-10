# -*- coding: utf-8 -*-
"""检查定长观察中的各特征段是否编码了有效信息。"""
import subprocess, os

base = r"C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
jar = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
hb = os.path.join(base, r"lib\gdx-backend-headless-1.14.0.jar")
cp = os.pathsep.join([os.path.join(base, "build", "classes"), jar, hb])

env = subprocess.Popen(
    ["java", "-cp", cp, "rl.RLCli", "proto"],
    cwd=base, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
    stderr=subprocess.PIPE, text=True, encoding="utf-8", bufsize=1)

env.stdin.write("reset 999\n"); env.stdin.flush()
ok = env.stdout.readline().strip()
obs = [int(x) for x in env.stdout.readline().strip().split()]
env.stdin.close()
try: env.wait(timeout=3)
except subprocess.TimeoutExpired: env.kill()

print("reset:", ok, "| 观察总长 =", len(obs))

WIN = 21
OFF_META = 3*WIN*WIN          # 1323
OFF_GLOBAL = OFF_META + 7     # 1330
OFF_BELONG = OFF_GLOBAL + 6   # 1336
OFF_ITEMD  = OFF_BELONG + 26  # 1362
OFF_SEED   = OFF_ITEMD + 26   # 1388
OFF_STATUS = OFF_SEED + 13    # 1401

meta = obs[OFF_META:OFF_META+7]
print("\n-- 元信息 [depth,pos,HP,HT,STR,lvl,gold] --")
print(" ", meta)

glob = obs[OFF_GLOBAL:OFF_GLOBAL+6]
print("-- 全局 [入距,出距,已探,怪,堆,陷阱] --")
print(" ", glob)

slots = obs[OFF_BELONG:OFF_BELONG+12]
bag   = obs[OFF_BELONG+12:OFF_BELONG+26]
cat = {0:"其他",1:"药水",2:"卷轴",3:"食物",4:"武器",5:"护甲",6:"戒指",7:"法器",8:"魔杖",9:"钥匙",10:"炸弹",11:"石片",12:"露珠",13:"金币"}
print("-- 装备槽 (类别,等级)x6 --")
for i in range(6):
    c, lvl = slots[i*2], slots[i*2+1]
    print("   槽%d: %s lvl=%d" % (i, cat.get(c,c), lvl))
print("-- 背包计数 非零项 --")
print(" ", [(cat.get(i,i),bag[i]) for i in range(14) if bag[i]>0] or "(全零)")
itemd = obs[OFF_ITEMD:OFF_ITEMD+26]
print("-- 具体物品识别 [药水12|卷轴12|未知药水|未知卷轴] --")
print(" ", itemd)
seed_names = ["阳春草","护根","传送草","盲草","火焰花","冰盖","魔法皇家","腐烂浆果","悲苔","星花","风暴藤","迅捷蓟","野果灌木"]
seed = obs[OFF_SEED:OFF_SEED+13]
nz_seed = [(seed_names[i], seed[i]) for i in range(13) if seed[i]>0]
print("-- 种子 [13种] --")
print(" ", nz_seed or "(全零)")
print("-- 饥饿+状态 --")
print(" ", obs[OFF_STATUS:OFF_STATUS+16])

# 窗口抽样：检查英雄中心附近窗口非零（地形/实体）
terrain_win = obs[0:WIN*WIN]
nz_terrain = sum(1 for v in terrain_win if v != 0)
entity_win = obs[WIN*WIN:2*WIN*WIN]
nz_entity = sum(1 for v in entity_win if v != 0)
fov_win = obs[2*WIN*WIN:3*WIN*WIN]
nz_fov = sum(1 for v in fov_win if v != 0)
print("\n-- 窗口非零计数 (21x21) -- 地形:%d 实体:%d 视野:%d" % (nz_terrain, nz_entity, nz_fov))
