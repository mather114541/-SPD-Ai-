# -*- coding: utf-8 -*-
"""给 GameScene.java 的公共静态方法批量加 headless 保护（备份原文件）。"""
import re, io, sys, os

path = sys.argv[1]
s = io.open(path, encoding='utf-8').read()
io.open(path + '.bak', 'w', encoding='utf-8', newline='\n').write(s)

def guard_void(m):
    return m.group(0) + '\n' + m.group(1) + '    if (rl.RLMode.headless) return;'

def guard_bool(m):
    return m.group(0) + '\n' + m.group(1) + '    if (rl.RLMode.headless) return false;'

n_void = len(re.findall(r'(?m)^(\s*)public static void \w+\(([^)]*)\) \{$', s))
n_bool = len(re.findall(r'(?m)^(\s*)public static boolean \w+\(([^)]*)\) \{$', s))

s = re.sub(r'(?m)^(\s*)public static void \w+\(([^)]*)\) \{$', guard_void, s)
s = re.sub(r'(?m)^(\s*)public static boolean \w+\(([^)]*)\) \{$', guard_bool, s)

io.open(path, 'w', encoding='utf-8', newline='\n').write(s)
print('guarded void=%d boolean=%d' % (n_void, n_bool))
