package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import java.util.ArrayDeque;
import java.util.Arrays;

/** 玩家同权的导航工具：已探地图上的 8 方向 BFS（斜走不切墙角），避已知陷阱，有钥匙可把锁门当通路。 */
public final class RLNav {

    private RLNav() {}

    public static int[] neighbours8(int w) {
        return new int[]{-w - 1, -w, -w + 1, -1, 1, w - 1, w, w + 1};
    }

    public static boolean[] knownMap(Level lv) {
        int len = lv.length();
        boolean[] k = new boolean[len];
        for (int i = 0; i < len; i++) k[i] = lv.visited[i] || lv.mapped[i];
        return k;
    }

    /** 已知可走的格且存在未知邻格 = 探索边界。 */
    public static boolean[] frontierMap(Level lv, boolean[] known) {
        int len = lv.length(), w = lv.width();
        boolean[] f = new boolean[len];
        int[] nb8 = neighbours8(w);
        for (int i = 0; i < len; i++) {
            if (!known[i] || !lv.passable[i]) continue;
            int cx = i % w, cy = i / w;
            for (int k = 0; k < 8; k++) {
                int n = i + nb8[k];
                if (n < 0 || n >= len) continue;
                if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
                if (!known[n]) { f[i] = true; break; }
            }
        }
        return f;
    }

    public static boolean[] exitMask(Level lv, boolean[] known) {
        int len = lv.length();
        boolean[] m = new boolean[len];
        for (int i = 0; i < len; i++) {
            if (!known[i]) continue;
            int t = lv.map[i];
            if (t == Terrain.EXIT || t == Terrain.UNLOCKED_EXIT) m[i] = true;
        }
        return m;
    }

    public static boolean[] visibleMobMask(Level lv) {
        int len = lv.length();
        boolean[] m = new boolean[len];
        if (lv.mobs != null) {
            for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mob : lv.mobs) {
                if (mob.pos >= 0 && mob.pos < len && lv.heroFOV[mob.pos]) m[mob.pos] = true;
            }
        }
        return m;
    }

    public static boolean[] visibleItemMask(Level lv) {
        int len = lv.length();
        boolean[] m = new boolean[len];
        if (lv.heaps != null) {
            for (int i = 0; i < len; i++) {
                if (lv.heroFOV[i] && lv.heaps.get(i) != null) m[i] = true;
            }
        }
        return m;
    }

    /** 只包含【可拾取】的普通地面掉落（Heap.Type.HEAP）；箱子/墓碑/骷髅/商店不算，避免站上去开箱失败卡死。 */
    public static boolean[] visibleLootMask(Level lv) {
        int len = lv.length();
        boolean[] m = new boolean[len];
        if (lv.heaps != null) {
            for (int i = 0; i < len; i++) {
                if (!lv.heroFOV[i]) continue;
                com.shatteredpixel.shatteredpixeldungeon.items.Heap h = lv.heaps.get(i);
                if (h != null && h.type == com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type.HEAP) m[i] = true;
            }
        }
        return m;
    }

    /** 当前格是否有可拾取的普通掉落。 */
    public static boolean lootAt(Level lv, int pos) {
        if (lv.heaps == null || pos < 0 || pos >= lv.length()) return false;
        com.shatteredpixel.shatteredpixeldungeon.items.Heap h = lv.heaps.get(pos);
        return h != null && h.type == com.shatteredpixel.shatteredpixeldungeon.items.Heap.Type.HEAP;
    }

    /** 已主动搜索过的可走格（本层内）。搜过没发现的点不再标可疑，保证搜索可收敛、避免反复。换层时重置。 */
    public static boolean[] searchedCell;

    public static void resetSearched(int len) { searchedCell = new boolean[len]; }

    /**
     * 可疑搜索点（按人类实战经验收紧，只标两处极精确的搜索点，放弃陷阱）：
     *  1. 新楼层初始房间的边界墙（经验1）：出生点 entrance 所在房间四周没有其他门时，
     *     才会贴墙搜索找门。出生点房间与相邻区域的相交墙段最可能藏隐藏门
     *     （尤其引导期初始房有强制隐藏门）。玩家同权：AI 不知道哪堵墙是门，只知道
     *     "我刚到这个房间，四周没门" → 贴出生点房间边界墙搜。
     *  2. 1 格宽窄通道尽头（经验2）：可走格 i 的 4 正交邻里 >=3 是墙，且含一对相对墙
     *     （上下或左右都墙 = 1 格宽通道的壁），这才是"窄通道尽头"，在此处只搜一次。
     *     房间角落虽然也三面墙，但不含相对墙对，不标。
     * 放弃对陷阱的主动搜索（玩家经验：普通层陷阱命中率过低，不值得主动搜）。
     */
    private static boolean isDoor(int t) {
        return t == Terrain.DOOR || t == Terrain.OPEN_DOOR || t == Terrain.LOCKED_DOOR
                || t == Terrain.CRYSTAL_DOOR || t == Terrain.SECRET_DOOR;
    }

    public static boolean[] suspiciousSpotMask(Level lv, boolean[] known) {
        int len = lv.length(), w = lv.width();
        boolean[] spot = new boolean[len];
        int[] nb8 = neighbours8(w);
        // 1) 出生点房间（初始房）边界墙（经验1：新楼层初始房四周没门就贴墙找门）
        //    出生点在房间内部，8 邻无墙，需先 BFS 出生点连通的已探可走区（初始房），
        //    再标该区"贴墙可走格"（房间边界墙邻格）为可疑搜索点。
        int ent = lv.entrance;
        if (ent >= 0 && ent < len) {
            boolean[] room = new boolean[len];
            int[] q = new int[len];
            int qh = 0, qt = 0;
            if (known[ent] && lv.passable[ent]) { room[ent] = true; q[qt++] = ent; }
            while (qh < qt) {
                int c = q[qh++];
                int ccx = c % w, ccy = c / w;
                for (int k = 0; k < 8; k++) {
                    int n = c + nb8[k];
                    if (n < 0 || n >= len) continue;
                    if (Math.abs(n % w - ccx) > 1 || Math.abs(n / w - ccy) > 1) continue;
                    if (known[n] && lv.passable[n] && !room[n] && !isDoor(lv.map[n])) { room[n] = true; q[qt++] = n; }
                }
            }
            // 判定出生房间四周是否全是墙（无可见门/出口）：边界墙外没有已探可走格
            boolean hasVisibleExit = false;
            for (int i = 0; i < len; i++) {
                if (!room[i]) continue;
                int cx = i % w, cy = i / w;
                for (int k = 0; k < 8; k++) {
                    int n = i + nb8[k];
                    if (n < 0 || n >= len) continue;
                    if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
                    if (!room[n] && known[n] && lv.passable[n]) { hasVisibleExit = true; break; }
                }
                if (hasVisibleExit) break;
            }
            // 只有"四周都是墙、没有其他门"才贴墙一圈搜找隐藏门（经验1）
            if (!hasVisibleExit) {
                for (int i = 0; i < len; i++) {
                    if (!room[i]) continue;
                    if (searchedCell != null && i < searchedCell.length && searchedCell[i]) continue;
                    int cx = i % w, cy = i / w;
                    if (((cx + cy) & 1) != 0) continue;   // 贴墙"间隔一格"搜一次，避免重复搜索（3x3 覆盖会重叠）
                    for (int k = 0; k < 8; k++) {
                        int n = i + nb8[k];
                        if (n < 0 || n >= len) continue;
                        if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
                        int m = lv.map[n];
                        if (m == Terrain.WALL || m == Terrain.SECRET_DOOR || m == Terrain.WALL_DECO) { spot[i] = true; break; }
                    }
                }
            }
        }
        // 2) 1 格宽窄通道尽头（三面墙 + 含一对相对墙 = 通道壁）
        for (int i = 0; i < len; i++) {
            if (!known[i] || !lv.passable[i]) continue;
            if (searchedCell != null && i < searchedCell.length && searchedCell[i]) continue;
            int cx = i % w, cy = i / w;
            int wallUp = 0, wallDown = 0, wallLeft = 0, wallRight = 0;
            for (int k = 0; k < 8; k++) {
                int n = i + nb8[k];
                if (n < 0 || n >= len) continue;
                int dx = n % w - cx, dy = n / w - cy;
                if (Math.abs(dx) + Math.abs(dy) != 1) continue;
                int m = lv.map[n];
                if (isDoor(m)) { wallUp = wallDown = wallLeft = wallRight = -99; break; }   // 尽头本来就有门 -> 不需要搜
                boolean isWall = (m == Terrain.WALL || m == Terrain.SECRET_DOOR || m == Terrain.WALL_DECO);
                if (dx == 0 && dy == -1) wallUp = isWall ? 1 : 0;
                else if (dx == 0 && dy == 1) wallDown = isWall ? 1 : 0;
                else if (dx == -1 && dy == 0) wallLeft = isWall ? 1 : 0;
                else if (dx == 1 && dy == 0) wallRight = isWall ? 1 : 0;
            }
            int wall4 = wallUp + wallDown + wallLeft + wallRight;
            boolean hasOppositePair = (wallUp == 1 && wallDown == 1) || (wallLeft == 1 && wallRight == 1);
            if (wall4 >= 3 && hasOppositePair) spot[i] = true;
        }
        return spot;
    }

    /**
     * 兜底全墙搜索掩码（备忘录第十二节待办）：全探完却找不到出口（出口藏在隐藏门后）时，
     * 把未搜过的贴墙可走格系统搜一遍，直到搜出隐藏门。仅由 RLExpert 在"全探完+停滞"时触发，
     * 属于打破卡死的必要兜底，不是常规主动搜索。棋盘抽样隔一格搜，覆盖 3x3 不重叠。
     */
    /** 兜底搜索判定（专家与 env 共用同一真相源）：无可达 frontier、无可达可疑点、出口也不可达 -> 需要全墙兜底搜索。 */
    public static boolean needFallbackSearch(Level lv, boolean[] known, boolean[] walk, int pos) {
        if (firstStep(lv, walk, pos, frontierMap(lv, known)) >= 0) return false;
        if (firstStep(lv, walk, pos, suspiciousSpotMask(lv, known)) >= 0) return false;
        if (firstStep(lv, walk, pos, exitMask(lv, known)) >= 0) return false;
        return true;
    }

    public static boolean[] fallbackSpotMask(Level lv, boolean[] known) {
        int len = lv.length(), w = lv.width();
        boolean[] spot = new boolean[len];
        int[] nb8 = neighbours8(w);
        for (int i = 0; i < len; i++) {
            if (!known[i] || !lv.passable[i]) continue;
            if (searchedCell != null && i < searchedCell.length && searchedCell[i]) continue;
            int cx = i % w, cy = i / w;
            if (((cx + cy) & 1) != 0) continue;   // 棋盘抽样：隔一格搜，覆盖不重叠
            for (int k = 0; k < 8; k++) {
                int n = i + nb8[k];
                if (n < 0 || n >= len) continue;
                if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
                int m = lv.map[n];
                if (m == Terrain.WALL || m == Terrain.SECRET_DOOR || m == Terrain.WALL_DECO) { spot[i] = true; break; }
            }
        }
        return spot;
    }

    public static boolean adjacentSecretDoor(Level lv, int pos) {
        int len = lv.length(), w = lv.width();
        int[] nb8 = neighbours8(w);
        for (int k = 0; k < 8; k++) {
            int n = pos + nb8[k];
            if (n < 0 || n >= len) continue;
            if (Math.abs(n % w - pos % w) > 1 || Math.abs(n / w - pos / w) > 1) continue;
            if (lv.map[n] == Terrain.SECRET_DOOR) return true;
        }
        return false;
    }

    /** 最近的【可见】活怪；null 表示没有。 */
    public static com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob nearestVisibleMob(Level lv, int pos) {
        com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob best = null;
        int bd = Integer.MAX_VALUE;
        if (lv.mobs != null) {
            for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : lv.mobs) {
                if (!m.isAlive()) continue;
                if (m.pos < 0 || m.pos >= lv.length()) continue;
                if (!lv.heroFOV[m.pos]) continue;
                int d = lv.distance(pos, m.pos);
                if (d < bd) { bd = d; best = m; }
            }
        }
        return best;
    }

    /** 最近的【可见】活怪距离（Level.distance，8 方向切比雪夫）；-1 表示没有。 */
    public static int nearestVisibleMobDist(Level lv, int pos) {
        int best = Integer.MAX_VALUE;
        if (lv.mobs != null) {
            for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : lv.mobs) {
                if (!m.isAlive()) continue;
                if (m.pos < 0 || m.pos >= lv.length()) continue;
                if (!lv.heroFOV[m.pos]) continue;
                int d = lv.distance(pos, m.pos);
                if (d < best) best = d;
            }
        }
        return best == Integer.MAX_VALUE ? -1 : best;
    }

    /** 该格有几个可通行邻格（= 来敌方向数）；越小越"窄"，越适合把 1vN 化成 1v1。 */
    public static int openDirs(Level lv, int pos) {
        int w = lv.width(), len = lv.length();
        int[] nb8 = neighbours8(w);
        int n = 0;
        for (int k = 0; k < 8; k++) {
            int c = pos + nb8[k];
            if (c < 0 || c >= len) continue;
            if (Math.abs(c % w - pos % w) > 1 || Math.abs(c / w - pos / w) > 1) continue;
            if (lv.passable[c]) n++;
        }
        return n;
    }

    /** 只认【本层】匹配的钥匙（Key.depth == 当前层），避免"有别的层的钥匙"就把全层锁门当可通行。 */
    public static boolean hasKeyForDepth(Hero h, int depth) {
        if (h == null || h.belongings == null || h.belongings.backpack == null) return false;
        for (Item it : h.belongings.backpack) {
            if (it instanceof Key && ((Key) it).depth == depth) return true;
        }
        return false;
    }

    /**
     * 不主动招惹/接触的怪（用户指定的生存规则）：
     *  - npcs 包里的全部 NPC：悲伤幽灵(Ghost)、血色哨卫(VaultSentry)、商店/铁匠/金像等
     *  - 巨型食人鱼 Piranha（极危险，躲开）
     *  - 活化石像 Statue / ArmoredStatue / ImpStatue（中立待机，打了很危险）
     *  - 真菌哨卫 FungalSentry
     */
    public static boolean isDangerous(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m) {
        if (m == null) return true;
        java.lang.Package pkg = m.getClass().getPackage();
        if (pkg != null && pkg.getName().contains(".npcs.")) return true;
        String n = m.getClass().getSimpleName();
        return n.equals("Piranha") || n.equals("Statue") || n.equals("ArmoredStatue")
                || n.equals("ImpStatue") || n.equals("FungalSentry") || n.equals("VaultSentry");
    }

    /** 可以主动交战的怪：敌对阵营且不在危险名单。中立的活化石像、NPC（悲伤幽灵/商店/铁匠）都不碰。 */
    public static boolean engageable(com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m) {
        return m != null && m.isAlive()
                && m.alignment == com.shatteredpixel.shatteredpixeldungeon.actors.Char.Alignment.ENEMY
                && !isDangerous(m);
    }

    /** 尝试走过去但失败的格（例如"有钥匙但钥匙不匹配"的锁门）：本层内视为不可通行，避免反复往上撞。 */
    public static boolean[] blocked;
    public static void resetBlocked(int len) { blocked = new boolean[len]; }
    public static void markBlocked(int cell) {
        if (blocked != null && cell >= 0 && cell < blocked.length) blocked[cell] = true;
    }

    public static boolean[] buildWalkable(Level lv, boolean[] known, int pos) {
        int len = lv.length();
        boolean[] walk = new boolean[len];
        boolean hasKey = hasKeyForDepth(Dungeon.hero, Dungeon.depth);
        for (int i = 0; i < len; i++) {
            if (blocked != null && i < blocked.length && blocked[i]) continue;   // 撞过走不通的格
            if (!known[i]) continue;
            if (lv.passable[i]) walk[i] = true;
            else if (hasKey && lv.map[i] == Terrain.LOCKED_DOOR) walk[i] = true;
            if (walk[i]) {
                Trap t = lv.traps == null ? null : lv.traps.get(i);
                if (t != null && t.visible && t.active) walk[i] = false;
            }
            if (walk[i] && lv.map[i] == Terrain.EMBERS) walk[i] = false;   // 魔法火焰：不要靠近
            if (walk[i]) {   // 不主动接触的怪（中立 NPC / 活化石像 / 食人鱼）占的格不要走进去
                com.shatteredpixel.shatteredpixeldungeon.actors.Char occ =
                        com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(i);
                if (occ instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
                        && !engageable((com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob) occ)) walk[i] = false;
            }
        }
        if (pos >= 0 && pos < len) walk[pos] = true;
        return walk;
    }

    public static boolean hasType(Hero h, Class<?> type) {
        if (h == null || h.belongings == null || h.belongings.backpack == null) return false;
        for (Item it : h.belongings.backpack) {
            if (type.isInstance(it)) return true;
        }
        return false;
    }

    // ------------------------------------------------------------------
    // 高效探索：持久化 frontier 目标。
    // 旧实现每回合都重选"最近的 frontier"，集合随视野变化会让英雄来回掉头（实测一层 900+ 步才探 86%）。
    // 这里锁定一个目标，持续走向它，直到它不再是 frontier 或不可达，才重选。
    // ------------------------------------------------------------------
    private static int exploreTargetCell = -1;
    private static Object exploreTargetLevel = null;

    public static void resetExploreTarget() { exploreTargetCell = -1; exploreTargetLevel = null; }

    public static boolean isFrontier(Level lv, boolean[] known, int cell) {
        if (cell < 0 || cell >= lv.length()) return false;
        if (!known[cell] || !lv.passable[cell]) return false;
        int w = lv.width();
        int[] nb8 = neighbours8(w);
        int cx = cell % w, cy = cell / w;
        for (int k = 0; k < 8; k++) {
            int n = cell + nb8[k];
            if (n < 0 || n >= lv.length()) continue;
            if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
            if (!known[n]) return true;
        }
        return false;
    }

    /** BFS 找最近的 targets 格并返回它本身（不是方向）；-1 表示不可达。 */
    public static int nearestTargetCell(Level lv, boolean[] walk, int start, boolean[] targets) {
        if (targets == null) return -1;
        int len = lv.length(), w = lv.width();
        int[] nb8 = neighbours8(w);
        int[] from = new int[len];
        Arrays.fill(from, -2);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        from[start] = -1;
        q.add(start);
        while (!q.isEmpty()) {
            int cur = q.poll();
            if (cur != start && targets[cur]) return cur;
            for (int k = 0; k < 8; k++) {
                int n = cur + nb8[k];
                if (n < 0 || n >= len) continue;
                int dx = n % w - cur % w, dy = n / w - cur / w;
                if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || (dx == 0 && dy == 0)) continue;
                if (dx != 0 && dy != 0) {
                    int a = cur + (dx > 0 ? 1 : -1);
                    int b = cur + (dy > 0 ? w : -w);
                    if (!walk[a] && !walk[b]) continue;
                }
                if (from[n] != -2 || !walk[n]) continue;
                from[n] = cur;
                q.add(n);
            }
        }
        return -1;
    }

    /** 探索一步：优先继续走向已锁定的 frontier；目标失效/不可达才重选。返回方向 0-7 或 -1。 */
    public static int exploreStep(Level lv, boolean[] known, boolean[] walk, int pos) {
        if (exploreTargetLevel != lv) { exploreTargetCell = -1; exploreTargetLevel = lv; }
        int len = lv.length();
        if (exploreTargetCell >= 0 && exploreTargetCell < len && isFrontier(lv, known, exploreTargetCell)) {
            boolean[] mask = new boolean[len];
            mask[exploreTargetCell] = true;
            int d = firstStep(lv, walk, pos, mask);
            if (d >= 0) return d;
        }
        boolean[] fr = frontierMap(lv, known);
        int goal = nearestTargetCell(lv, walk, pos, fr);
        if (goal < 0) { exploreTargetCell = -1; return -1; }
        exploreTargetCell = goal;
        boolean[] mask = new boolean[len];
        mask[goal] = true;
        return firstStep(lv, walk, pos, mask);
    }

    /** BFS 走向最近的 targets 格，返回第一步方向（PathFinder.NEIGHBOURS8 下标）或 -1。 */
    public static int firstStep(Level lv, boolean[] walk, int start, boolean[] targets) {
        if (targets == null) return -1;
        int len = lv.length(), w = lv.width();
        int[] nb8 = neighbours8(w);
        int[] from = new int[len];
        Arrays.fill(from, -2);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        from[start] = -1;
        q.add(start);
        int goal = -1;
        while (!q.isEmpty()) {
            int cur = q.poll();
            if (cur != start && targets[cur]) { goal = cur; break; }
            for (int k = 0; k < 8; k++) {
                int n = cur + nb8[k];
                if (n < 0 || n >= len) continue;
                int dx = n % w - cur % w, dy = n / w - cur / w;
                if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || (dx == 0 && dy == 0)) continue;
                if (dx != 0 && dy != 0) {
                    int a = cur + (dx > 0 ? 1 : -1);
                    int b = cur + (dy > 0 ? w : -w);
                    if (!walk[a] && !walk[b]) continue;
                }
                if (from[n] != -2 || !walk[n]) continue;
                from[n] = cur;
                q.add(n);
            }
        }
        if (goal < 0) return -1;
        int cur = goal;
        while (from[cur] != start) {
            if (from[cur] == -1 || from[cur] == -2) return -1;
            cur = from[cur];
        }
        return dirOf(cur % w - start % w, cur / w - start / w);
    }

    public static int dirOf(int dx, int dy) {
        if (dx == -1 && dy == -1) return 0;
        if (dx == 0 && dy == -1) return 1;
        if (dx == 1 && dy == -1) return 2;
        if (dx == -1 && dy == 0) return 3;
        if (dx == 1 && dy == 0) return 4;
        if (dx == -1 && dy == 1) return 5;
        if (dx == 0 && dy == 1) return 6;
        if (dx == 1 && dy == 1) return 7;
        return -1;
    }
}
