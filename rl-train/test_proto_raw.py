# -*- coding: utf-8 -*-
"""实测 RLCli proto 的 stdout 原始输出，定位解析污染源。"""
import subprocess, os, sys

BASE = r"C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
JAR = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS = os.path.join(BASE, "build", "classes")
CP = ";".join([CLS, JAR, HB])

p = subprocess.Popen(["java", "-Xmx1g", "-cp", CP, "rl.RLCli", "proto"],
                     stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                     stderr=subprocess.PIPE, text=True, encoding="utf-8",
                     bufsize=1, cwd=BASE)
p.stdin.write("reset 5\n"); p.stdin.flush()
# 读前 8 行，看实际内容
for i in range(8):
    line = p.stdout.readline()
    print(i, "|", repr(line))
p.stdin.write("step 4\n"); p.stdin.flush()
for i in range(4):
    line = p.stdout.readline()
    print("step", i, "|", repr(line))
p.stdin.close()
try: p.terminate()
except Exception: pass
