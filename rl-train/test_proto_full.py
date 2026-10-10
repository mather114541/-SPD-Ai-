# -*- coding: utf-8 -*-
"""复刻 train_ppo.SPDEnv 的启动方式，reset+连续 step，定位 proto 污染点。"""
import subprocess, os

BASE = r"C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
JAR = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS = os.path.join(BASE, "build", "classes")
CP = ";".join([CLS, JAR, HB])

p = subprocess.Popen(["java", "-Xmx2g", "-cp", CP, "rl.RLCli", "proto"],
                     stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                     text=True, encoding="utf-8", bufsize=1, cwd=BASE)

def read_pair():
    meta = p.stdout.readline().strip()
    obs = p.stdout.readline().strip()
    ok = meta.startswith("OK ")
    return meta, obs, ok

p.stdin.write("reset 2026\n"); p.stdin.flush()
meta, obs, ok = read_pair()
print("RESET ok=", ok, "|", meta[:60], "| obs_nums=", len(obs.split()))

for i in range(20):
    p.stdin.write("step %d\n" % (i % 12)); p.stdin.flush()
    meta, obs, ok = read_pair()
    print("STEP", i, "ok=", ok, "|", meta[:60], "| obs_nums=", len(obs.split()))

p.stdin.close()
try: p.terminate()
except Exception: pass
