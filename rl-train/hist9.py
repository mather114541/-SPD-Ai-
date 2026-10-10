import struct, sys, collections, glob
files = sorted(glob.glob(sys.argv[1]))
MAPW = MAPH = NSC = NACT = None
tot = 0; cnt = collections.Counter()
rec = None
for p in files:
    with open(p, 'rb') as f:
        d = f.read()
    if MAPW is None:
        MAPW, MAPH, NSC, NACT = struct.unpack('>4i', d[4:20])
        rec = MAPW * MAPH * 2 + NSC * 4 + 4
    n = (len(d) - 32) // rec
    tot += n
    for i in range(n):
        off = 32 + i * rec
        act = struct.unpack('>i', d[off + MAPW * MAPH * 2 + NSC * 4: off + MAPW * MAPH * 2 + NSC * 4 + 4])[0]
        cnt[act] += 1
names = {0: 'EXPLORE', 1: 'EXIT', 2: 'FIGHT', 3: 'HEAL', 4: 'EAT', 5: 'SEARCH', 6: 'PICKUP', 7: 'WAIT', 8: 'THROW'}
print('files', len(files), 'records', tot)
for a in range(NACT):
    print(f'{a} {names[a]:8} {cnt[a]:6}  {100 * cnt[a] / tot:.1f}%')
