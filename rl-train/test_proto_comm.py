# -*- coding: utf-8 -*-
"""communicate 方式：一次 reset+step，捕获 java stdout/stderr，定位 step 卡住/污染。"""
import subprocess, os

BASE = r"C:\Users\33040\Doubao\chats\2026-10-08\new-chat"
JAR = r"C:\Users\33040\Downloads\ShatteredPD-v4.0.0-Java.jar"
HB = os.path.join(BASE, "lib", "gdx-backend-headless-1.14.0.jar")
CLS = os.path.join(BASE, "build", "classes")
CP = ";".join([CLS, JAR, HB])

p = subprocess.Popen(["java", "-Xmx2g", "-cp", CP, "rl.RLCli", "proto"],
                     stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                     stderr=subprocess.PIPE, text=True, encoding="utf-8",
                     cwd=BASE)
try:
    out, err = p.communicate(input="reset 2026\nstep 4\n", timeout=40)
    print("=== STDOUT ===")
    lines = out.splitlines()
    for i, l in enumerate(lines):
        print(i, l[:80])
    print("=== STDERR (tail) ===")
    for l in err.splitlines()[-20:]:
        print(l[:120])
except subprocess.TimeoutExpired:
    print("TIMEOUT: java hung")
    p.kill()
    o, e = p.communicate()
    print("=== STDOUT ==="); print(o[:500])
    print("=== STDERR tail ==="); print(e[-800:])
