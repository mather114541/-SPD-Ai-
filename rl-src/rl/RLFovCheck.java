package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;

/** 直查 heroFOV / visited 是否随移动更新，绕过观察编码层。 */
public class RLFovCheck implements ApplicationListener {
    public static void main(String[] args) {
        new HeadlessApplication(new RLFovCheck());
    }
    @Override public void create() {
        System.setProperty("rl.debug", "true");
        RLController env = new RLController();
        env.reset(2026L);
        // 多 seed 快速扫描：出生房连通域大小 + 连通域边界是否有门/非墙出口
        for (long sd : new long[]{2026L, 7L, 123L, 9999L}) {
            RLController e2 = new RLController();
            e2.reset(sd);
            int w = Dungeon.level.width();
            boolean[] vs = new boolean[Dungeon.level.length()];
            java.util.ArrayDeque<Integer> qq = new java.util.ArrayDeque<>();
            int st = Dungeon.hero.pos; qq.add(st); vs[st] = true;
            int c2 = 0;
            java.util.HashSet<Integer> b2 = new java.util.HashSet<>();
            while (!qq.isEmpty()) {
                int cur = qq.poll(); c2++;
                int x = cur % w, y = cur / w;
                int[] nb = {cur - w, cur + w, cur - 1, cur + 1};
                for (int n : nb) {
                    if (n < 0 || n >= vs.length) continue;
                    int nx = n % w, ny = n / w;
                    if (Math.abs(nx - x) + Math.abs(ny - y) != 1) continue;
                    if (vs[n]) continue;
                    if (Dungeon.level.passable[n]) { vs[n] = true; qq.add(n); }
                    else b2.add(n);
                }
            }
            int borderDoor = 0, borderPass = 0;
            for (int n : b2) {
                if (Dungeon.level.map[n] == 5 || Dungeon.level.map[n] == 6) borderDoor++;
                if (Dungeon.level.passable[n]) borderPass++;
            }
            System.err.println("SEED " + sd + " comp=" + c2 + " borderCells=" + b2.size()
                + " borderDoor=" + borderDoor + " borderPass=" + borderPass);
        }
        env.reset(2026L);
        System.err.println("w=" + Dungeon.level.width() + " losTrues=" + losTrues()
            + " hero.viewDistance=" + Dungeon.hero.viewDistance + " pos=" + Dungeon.hero.pos
            + " heroFOV.len=" + (Dungeon.level.heroFOV == null ? "null" : "" + Dungeon.level.heroFOV.length));
        System.err.println("reset fov=" + fovSet() + " visited=" + vis());
        {
            int doorCnt = 0, openDoorCnt = 0, waterCnt = 0;
            for (int i = 0; i < Dungeon.level.map.length; i++) {
                if (Dungeon.level.map[i] == 5) doorCnt++;
                else if (Dungeon.level.map[i] == 6) openDoorCnt++;
                else if (Dungeon.level.map[i] == 29) waterCnt++;
            }
            System.err.println("mapCounts closedDoor5=" + doorCnt + " openDoor6=" + openDoorCnt + " water29=" + waterCnt);
        }
        // 打印出生区域 map 值网格，找门(D=closed5/O=open6)/楼梯/出口
        {
            int w = Dungeon.level.width(), cx = Dungeon.hero.pos % w, cy = Dungeon.hero.pos / w;
            for (int dy = -10; dy <= 10; dy++) {
                StringBuilder sb = new StringBuilder();
                for (int dx = -10; dx <= 10; dx++) {
                    int x = cx + dx, y = cy + dy;
                    if (x < 0 || y < 0 || x >= w || y >= Dungeon.level.height()) { sb.append('X'); continue; }
                    int p = y * w + x, m = Dungeon.level.map[p];
                    char ch;
                    if (m == 5) ch = 'D';
                    else if (m == 6) ch = 'O';
                    else if (m == 7 || m == 8) ch = 'S';
                    else if (m == 4 || m == 16 || m == 12) ch = '#';
                    else if (m == 2 || m == 30) ch = 'g';
                    else if (m == 0) ch = '~';
                    else ch = '.';
                    sb.append(ch);
                }
                System.err.println("MAP " + sb);
            }
        }
        // 计算 visited 区域边界外相邻的可走格数（判断英雄能否走出出生房间）
        int w2 = Dungeon.level.width(), h2 = Dungeon.level.height(), reachOut = 0;
        boolean[] vis2 = Dungeon.level.visited;
        for (int i = 0; i < vis2.length; i++) if (vis2[i]) {
            int x = i % w2, y = i / w2;
            if (y > 0 && Dungeon.level.passable[i - w2] && !vis2[i - w2]) reachOut++;
            if (y < h2 - 1 && Dungeon.level.passable[i + w2] && !vis2[i + w2]) reachOut++;
            if (x > 0 && Dungeon.level.passable[i - 1] && !vis2[i - 1]) reachOut++;
            if (x < w2 - 1 && Dungeon.level.passable[i + 1] && !vis2[i + 1]) reachOut++;
        }
        System.err.println("reachableOutsideVisited=" + reachOut);
        // BFS 从英雄出发 passable 连通域，判断英雄可达区域是否 > visited 的 64 格
        int hc = Dungeon.hero.pos;
        boolean[] seen = new boolean[Dungeon.level.length()];
        java.util.ArrayDeque<Integer> q = new java.util.ArrayDeque<>();
        q.add(hc); seen[hc] = true;
        int comp = 0, compOutside = 0;
        while (!q.isEmpty()) {
            int cur = q.poll(); comp++;
            int x = cur % w2, y = cur / w2;
            int[] nb = {cur - w2, cur + w2, cur - 1, cur + 1};
            for (int n : nb) {
                if (n < 0 || n >= seen.length) continue;
                int nx = n % w2, ny = n / w2;
                if (Math.abs(nx - x) + Math.abs(ny - y) != 1) continue;
                if (!seen[n] && Dungeon.level.passable[n]) { seen[n] = true; q.add(n); }
            }
        }
        for (int i = 0; i < seen.length; i++) if (seen[i] && !Dungeon.level.visited[i]) compOutside++;
        System.err.println("BFS reachableComp=" + comp + " outsideVisited=" + compOutside);
        // 打印连通域边界外相邻格（找门/通道）
        java.util.HashSet<Integer> bset = new java.util.HashSet<>();
        for (int i = 0; i < seen.length; i++) if (seen[i]) {
            int x = i % w2, y = i / w2;
            int[] nb = {i - w2, i + w2, i - 1, i + 1};
            for (int n : nb) {
                if (n < 0 || n >= seen.length) continue;
                int nx = n % w2, ny = n / w2;
                if (Math.abs(nx - x) + Math.abs(ny - y) != 1) continue;
                if (!seen[n]) bset.add(n);
            }
        }
        for (int n : bset)
            System.err.println("boundaryNeighbor pos=" + n + " map=" + Dungeon.level.map[n]
                + " passable=" + Dungeon.level.passable[n]);
        // 重算连通域，打印所有楼梯(map7/8)及可达性，选可达出口换层
        com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero hero = Dungeon.hero;
        int stair = -1, best = Integer.MAX_VALUE;
        {
            boolean[] vs = new boolean[Dungeon.level.length()];
            java.util.ArrayDeque<Integer> qq = new java.util.ArrayDeque<>();
            qq.add(hero.pos); vs[hero.pos] = true;
            while (!qq.isEmpty()) {
                int cur = qq.poll();
                int x = cur % w2, y = cur / w2;
                int[] nb = {cur - w2, cur + w2, cur - 1, cur + 1};
                for (int n : nb) {
                    if (n < 0 || n >= vs.length) continue;
                    int nx = n % w2, ny = n / w2;
                    if (Math.abs(nx - x) + Math.abs(ny - y) != 1) continue;
                    if (!vs[n] && Dungeon.level.passable[n]) { vs[n] = true; qq.add(n); }
                }
            }
            for (int i = 0; i < Dungeon.level.map.length; i++) {
                int m = Dungeon.level.map[i];
                if (m != 7 && m != 8) continue;
                System.err.println("stair pos=" + i + " map=" + m + " passable=" + Dungeon.level.passable[i]
                    + " reachable=" + (vs[i] ? "Y" : "n"));
                if (m == 8 && Dungeon.level.passable[i] && vs[i]) {
                    int d = Math.abs((i % w2) - (hero.pos % w2)) + Math.abs((i / w2) - (hero.pos / w2));
                    if (d < best) { best = d; stair = i; }
                }
            }
        }
        System.err.println("nearestReachableExit=" + stair);
        // 直接确认换层机制：英雄瞬移到出口楼梯触发换层
        if (stair >= 0) {
            com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition tr
                = Dungeon.level.getTransition(stair);
            System.err.println("tr pos=" + stair + " type=" + (tr != null ? tr.type : "null")
                + " destDepth=" + (tr != null ? tr.destDepth : "-") + " inside=" + (tr != null ? tr.inside(stair) : "-")
                + " locked=" + Dungeon.level.locked);
            int oldPos = Dungeon.hero.pos;
            Dungeon.hero.pos = stair;
            Dungeon.level.pressCell(stair);
            hero.handle(stair);
            System.err.println("afterHandle curAction=" + (hero.curAction != null ? hero.curAction.getClass().getSimpleName() : "null"));
            com.shatteredpixel.shatteredpixeldungeon.actors.Actor.processTurn();
            System.err.println("teleportExit pos=" + Dungeon.hero.pos + " depth=" + Dungeon.depth + " visited=" + vis());
            if (Dungeon.depth == 1 && tr != null) {
                boolean ok = Dungeon.level.activateTransition(hero, tr);
                System.err.println("directActivate ok=" + ok + " depth=" + Dungeon.depth);
            }
            if (Dungeon.depth == 1) Dungeon.hero.pos = oldPos;
        }
        Dungeon.observe();
        System.err.println("manual observe fov=" + fovSet() + " visited=" + vis());
        for (int i = 0; i < 3; i++) {
            env.step(4);
            System.err.println("step" + i + " fov=" + fovSet() + " visited=" + vis() + " pos=" + Dungeon.hero.pos);
        }
        // 多步混合移动，验证 visited 增长与换层（探索奖励是否生效）
        java.util.Random rnd = new java.util.Random(7);
        int lastVis = vis();
        for (int i = 0; i < 100; i++) {
            try {
                int a = rnd.nextInt(9);
                env.step(a);
                int v = vis();
                if (v != lastVis || Dungeon.depth > 1) {
                    System.err.println("s" + i + " act=" + a + " pos=" + Dungeon.hero.pos
                        + " visited=" + v + " depth=" + Dungeon.depth + " fov=" + fov());
                    lastVis = v;
                }
            } catch (Throwable t) {
                System.err.println("s" + i + " EXC " + t);
                t.printStackTrace(System.err);
                System.exit(1);
            }
        }
        // RL 流程换层测试：英雄探索后持续 handle 最近出口，看能否真正换层
        {
            int ex = -1, bd = Integer.MAX_VALUE;
            for (int i = 0; i < Dungeon.level.map.length; i++) {
                if (Dungeon.level.map[i] == 8 && Dungeon.level.passable[i]) {
                    int d = Math.abs((i % w2) - (hero.pos % w2)) + Math.abs((i / w2) - (hero.pos / w2));
                    if (d < bd) { bd = d; ex = i; }
                }
            }
            if (ex >= 0) {
                for (int k = 0; k < 150 && Dungeon.depth == 1; k++) {
                    hero.handle(ex);
                    com.shatteredpixel.shatteredpixeldungeon.actors.Actor.processTurn();
                    if (k % 20 == 0) System.err.println("towardExit k=" + k + " pos=" + hero.pos + " depth=" + Dungeon.depth + " vis=" + vis());
                }
                System.err.println("RLflowExit pos=" + hero.pos + " depth=" + Dungeon.depth);
            }
        }
        System.err.println("DONE maxDepth=" + Dungeon.depth + " finalVis=" + vis());
        System.exit(0);
    }
    static int losTrues() {
        int n = 0;
        if (Dungeon.level != null && Dungeon.level.losBlocking != null)
            for (boolean b : Dungeon.level.losBlocking) if (b) n++;
        return n;
    }
    static String setOf(boolean[] arr) {
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (int i = 0; i < arr.length; i++)
            if (arr[i]) { sb.append(i).append(' '); n++; }
        return "[" + n + "] " + sb.toString().trim();
    }
    static String fovSet() {
        if (Dungeon.level == null || Dungeon.level.heroFOV == null) return "null";
        StringBuilder sb = new StringBuilder();
        int n = 0;
        for (int i = 0; i < Dungeon.level.length(); i++)
            if (Dungeon.level.heroFOV[i]) { sb.append(i).append(' '); n++; }
        return "[" + n + "] " + sb.toString().trim();
    }
    static int fov() {
        int n = 0;
        if (Dungeon.level != null && Dungeon.level.heroFOV != null)
            for (boolean b : Dungeon.level.heroFOV) if (b) n++;
        return n;
    }
    static void printGrid() {
        int w = Dungeon.level.width();
        int c = Dungeon.hero.pos;
        System.err.println("[grid] heroPos=" + c + " passable=" + Dungeon.level.passable[c]
            + " cx=" + (c % w) + " cy=" + (c / w));
        int open = 0, total = 0;
        for (int dy = -10; dy <= 10; dy++)
            for (int dx = -10; dx <= 10; dx++) {
                int x = (c % w) + dx, y = (c / w) + dy;
                if (x < 0 || y < 0 || x >= w || y >= Dungeon.level.height()) continue;
                total++;
                if (Dungeon.level.passable[y * w + x]) open++;
            }
        System.err.println("[grid] hero21x21 open=" + open + "/" + total);
        int cx = c % w, cy = c / w;
        for (int dy = -10; dy <= 10; dy++) {
            StringBuilder sb = new StringBuilder();
            for (int dx = -10; dx <= 10; dx++) {
                int x = cx + dx, y = cy + dy;
                if (x < 0 || y < 0 || x >= w || y >= Dungeon.level.height()) { sb.append('X'); continue; }
                int p = y * w + x;
                sb.append(Dungeon.level.passable[p] ? '.' : '#');
            }
            System.err.println("grid " + sb);
        }
    }
    static int fovUnvisited() {
        int n = 0;
        if (Dungeon.level != null && Dungeon.level.heroFOV != null)
            for (int i = 0; i < Dungeon.level.length(); i++)
                if (Dungeon.level.heroFOV[i] && !(Dungeon.level.visited[i] || Dungeon.level.mapped[i])) n++;
        return n;
    }
    static int vis() {
        int n = 0;
        if (Dungeon.level != null)
            for (int i = 0; i < Dungeon.level.length(); i++)
                if (Dungeon.level.visited[i] || Dungeon.level.mapped[i]) n++;
        return n;
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
