# -*- coding: utf-8 -*-
"""批量把单行 this.sprite.X(...) / Dungeon.hero.sprite.X(...) 调用包裹为 null 安全。
只处理单行完整调用（括号平衡、以分号结尾），排除已有 if/else/return/逻辑条件的行。
用法: python guard_sprite.py <file.java> [<file2.java> ...]
"""
import re, io, sys

def process(path):
    s = io.open(path, encoding='utf-8').read()
    bak = path + '.bak'
    if not __import__('os').path.exists(bak):
        io.open(bak, 'w', encoding='utf-8', newline='\n').write(s)
    lines = s.split('\n')
    out = []
    n = 0
    for line in lines:
        m = re.match(r'^(\s*)((?:this|Dungeon\.hero)\.sprite\.\w+\(.*\);)$', line)
        if m and line.count('(') == line.count(')') \
           and not re.search(r'\b(if|else|return|while|for)\b', line) \
           and not re.search(r'&&|\|\|', line):
            indent, call = m.group(1), m.group(2)
            obj = call.split('.sprite')[0] + '.sprite'
            out.append('%sif (%s != null) %s' % (indent, obj, call))
            n += 1
        else:
            out.append(line)
    io.open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(out))
    print('%s: guarded %d lines' % (path.split('\\')[-1], n))

for p in sys.argv[1:]:
    process(p)
