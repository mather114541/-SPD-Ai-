# -*- coding: utf-8 -*-
"""验证 Python 通过 stdin/stdout 协议驱动 Java RL 环境。"""
import subprocess, sys, os

base = r"C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
jar = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
hb = os.path.join(base, r"lib\gdx-backend-headless-1.14.0.jar")
cp = os.pathsep.join([os.path.join(base, "build", "classes"), jar, hb])

env = subprocess.Popen(
    ["java", "-cp", cp, "rl.RLCli", "proto"],
    cwd=base, stdin=subprocess.PIPE, stdout=subprocess.PIPE,
    stderr=subprocess.PIPE, text=True, encoding="utf-8",
    bufsize=1)

def cmd(line):
    env.stdin.write(line + "\n"); env.stdin.flush()
    ok = env.stdout.readline().strip()   # OK 行
    obs = env.stdout.readline().strip()  # 观察行
    n = len(obs.split()) if obs else 0
    return ok, n

ok, n = cmd("reset 12345")
print("RESET :", ok, "| obs_len =", n)
assert ok.startswith("OK"), "reset failed: " + ok

total = 0
for i in range(10):
    action = i % 9
    ok, n = cmd("step %d" % action)
    total += 1
    print("STEP %2d : %s | obs_len=%d" % (i, ok, n))

env.stdin.close()
try:
    env.wait(timeout=5)
except subprocess.TimeoutExpired:
    env.kill()

print("PROTO TEST DONE, steps=%d" % total)
