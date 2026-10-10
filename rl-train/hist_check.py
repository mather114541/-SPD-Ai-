import struct, sys, collections
p = sys.argv[1]
with open(p, 'rb') as f:
    d = f.read()
MAPW, MAPH, NSC, NACT = struct.unpack('>4i', d[4:20])
rec = MAPW * MAPH * 2 + NSC * 4 + 4
n = (len(d) - 32) // rec
cnt = collections.Counter()
for i in range(n):
    off = 32 + i * rec
    act = struct.unpack('>i', d[off + MAPW * MAPH * 2 + NSC * 4: off + MAPW * MAPH * 2 + NSC * 4 + 4])[0]
    cnt[act] += 1
names = {0: 'EXPLORE', 1: 'EXIT', 2: 'FIGHT', 3: 'HEAL', 4: 'EAT', 5: 'SEARCH', 6: 'PICKUP', 7: 'WAIT', 8: 'THROW'}
print('records', n)
for a in range(NACT):
    print(f'{a} {names[a]:8} {cnt[a]:6}  {100 * cnt[a] / n:.1f}%')
