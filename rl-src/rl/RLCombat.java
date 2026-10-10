package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * 战斗执行器（微操层），统一接管"怎么打"：
 *  1) 按怪速度维持安全站位（抢先后手）
 *  2) 一打多时退到漏斗/窄地，把 1vN 化成串行 1v1（利用怪不能重叠）
 *  3) 突袭：怪未发现我们时出手，引擎自动判定必中（surprisedBy）
 *  4) 远程投掷：用弹道校验找能命中的瞄准格，再程序化投掷
 */
public final class RLCombat {

    public static final int NO_TARGET = -2;
    public static final int HOLD = -1;
    public static boolean standoffEnabled = true;
    public static boolean funnelEnabled = false;   // 漏斗撤退会振荡，默认关（需滞回后再启用）
    public static int engageRange = 2;   // 消融：2 比 4 好得多（4 会让英雄沉迷战斗、少推进）
    public static int holdCap = 1;
    public static int funnelRadius = 6;

    public static int holdSlow = 0, holdFast = 0, funnelMoves = 0, throwsDone = 0;
    public static int throwErrCount = 0;
    public static void resetCounters() { holdSlow = 0; holdFast = 0; funnelMoves = 0; throwsDone = 0; throwErrCount = 0; }

    private static int lastPos = -1;
    private static int heldCount = 0;
    public static void resetState() { lastPos = -1; heldCount = 0; }

    private RLCombat() {}

    // ---------------------------------------------------------------- engage
    public static int engageDir(Level lv, int pos) {
        Mob m = RLNav.nearestVisibleMob(lv, pos);
        if (m == null || !RLNav.engageable(m)) return NO_TARGET;   // 中立 NPC / 活化石像 / 食人鱼：不交战
        int w = lv.width();
        int d = lv.distance(pos, m.pos);

        // 贴身：直接攻击。若怪此刻未发现我们，引擎会自动按突袭结算（必中+额外伤害）
        if (d <= 1) {
            return RLNav.dirOf(m.pos % w - pos % w, m.pos / w - pos / w);
        }

        boolean[] known = RLNav.knownMap(lv);
        boolean[] walk = RLNav.buildWalkable(lv, known, pos);

        // 一打多：优先退到窄地（漏斗），把 1vN 化成一串 1v1
        if (funnelEnabled && visibleHostileCount(lv, pos, 3) >= 2) {
            int fs = funnelStep(lv, pos, walk);
            if (fs != NO_TARGET) {
                if (fs != HOLD) funnelMoves++;
                return fs;
            }
        }

        boolean[] tgt = new boolean[lv.length()];
        tgt[m.pos] = true;
        int approach = RLNav.firstStep(lv, walk, pos, tgt);

        if (!standoffEnabled) return approach < 0 ? NO_TARGET : approach;

        int safe = (int) Math.ceil(m.speed()) + 1;
        if (d == safe) {
            if (pos == lastPos && heldCount >= holdCap) {
                return approach < 0 ? NO_TARGET : approach;
            }
            lastPos = pos;
            heldCount++;
            if (m.speed() > 1.0f) holdFast++; else holdSlow++;
            return HOLD;
        }
        if (d < safe) {
            int away = stepAway(lv, walk, pos, m);
            return away < 0 ? (approach < 0 ? NO_TARGET : approach) : away;
        }
        return approach < 0 ? NO_TARGET : approach;
    }

    public static int visibleHostileCount(Level lv, int pos, int range) {
        int n = 0;
        if (lv.mobs != null) {
            for (Mob m : lv.mobs) {
                if (!m.isAlive()) continue;
                if (!lv.heroFOV[m.pos]) continue;
                if (lv.distance(pos, m.pos) <= range) n++;
            }
        }
        return n;
    }

    /** 后退一步，选择能最大化与目标怪距离的可走邻格。 */
    public static int stepAway(Level lv, boolean[] walk, int pos, Mob m) {
        int w = lv.width();
        int[] nb8 = RLNav.neighbours8(w);
        int best = -1, bestD = lv.distance(pos, m.pos);
        for (int k = 0; k < 8; k++) {
            int n = pos + nb8[k];
            if (n < 0 || n >= lv.length()) continue;
            int dx = n % w - pos % w, dy = n / w - pos / w;
            if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || (dx == 0 && dy == 0)) continue;
            if (dx != 0 && dy != 0) {
                int a = pos + (dx > 0 ? 1 : -1);
                int b = pos + (dy > 0 ? w : -w);
                if (!walk[a] && !walk[b]) continue;
            }
            if (!walk[n]) continue;
            int nd = lv.distance(n, m.pos);
            if (nd > bestD) { bestD = nd; best = k; }
        }
        return best;
    }

    /** 附近（funnelRadius 内）能到达的最窄格的开敌方向数；越小越好。 */
    public static int bestFunnelOpen(Level lv, int pos, boolean[] walk) {
        int len = lv.length();
        int myOpen = RLNav.openDirs(lv, pos);
        int[] dist = new int[len];
        Arrays.fill(dist, -1);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        dist[pos] = 0; q.add(pos);
        int best = myOpen;
        int[] nb8 = RLNav.neighbours8(lv.width());
        while (!q.isEmpty()) {
            int cur = q.poll();
            int od = RLNav.openDirs(lv, cur);
            if (od < best) best = od;
            if (dist[cur] >= funnelRadius) continue;
            for (int k = 0; k < 8; k++) {
                int n = cur + nb8[k];
                if (n < 0 || n >= len) continue;
                int dx = n % lv.width() - cur % lv.width(), dy = n / lv.width() - cur / lv.width();
                if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || (dx == 0 && dy == 0)) continue;
                if (dx != 0 && dy != 0) {
                    int a = cur + (dx > 0 ? 1 : -1);
                    int b = cur + (dy > 0 ? lv.width() : -lv.width());
                    if (!walk[a] && !walk[b]) continue;
                }
                if (dist[n] != -1 || !walk[n]) continue;
                dist[n] = dist[cur] + 1;
                q.add(n);
            }
        }
        return best;
    }

    // ---------------------------------------------------------------- funnel
    /** 返回走向最近更好"窄地"的第一步；HOLD=已站住；NO_TARGET=找不到更好位置。 */
    public static int funnelStep(Level lv, int pos, boolean[] walk) {
        int len = lv.length();
        int myOpen = RLNav.openDirs(lv, pos);
        int[] from = new int[len];
        int[] dist = new int[len];
        Arrays.fill(from, -2);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        from[pos] = -1; dist[pos] = 0;
        q.add(pos);
        int best = pos, bestOpen = myOpen, bestDist = 0;
        int[] nb8 = RLNav.neighbours8(lv.width());
        while (!q.isEmpty()) {
            int cur = q.poll();
            int od = RLNav.openDirs(lv, cur);
            if (cur != pos && (od < bestOpen || (od == bestOpen && dist[cur] < bestDist))) {
                best = cur; bestOpen = od; bestDist = dist[cur];
            }
            if (dist[cur] >= funnelRadius) continue;
            for (int k = 0; k < 8; k++) {
                int n = cur + nb8[k];
                if (n < 0 || n >= len) continue;
                int dx = n % lv.width() - cur % lv.width(), dy = n / lv.width() - cur / lv.width();
                if (Math.abs(dx) > 1 || Math.abs(dy) > 1 || (dx == 0 && dy == 0)) continue;
                if (dx != 0 && dy != 0) {
                    int a = cur + (dx > 0 ? 1 : -1);
                    int b = cur + (dy > 0 ? lv.width() : -lv.width());
                    if (!walk[a] && !walk[b]) continue;
                }
                if (from[n] != -2 || !walk[n]) continue;
                from[n] = cur; dist[n] = dist[cur] + 1;
                q.add(n);
            }
        }
        if (best == pos) return myOpen <= 2 ? HOLD : NO_TARGET;
        int cur = best;
        while (from[cur] != pos) {
            if (from[cur] == -1 || from[cur] == -2) return NO_TARGET;
            cur = from[cur];
        }
        return RLNav.dirOf(cur % lv.width() - pos % lv.width(), cur / lv.width() - pos / lv.width());
    }

    // ---------------------------------------------------------------- throw
    /** 找一件可用投掷武器（背包迭代器会展开子袋）。 */
    public static MissileWeapon bestMissile(Hero hero) {
        if (hero == null || hero.belongings == null || hero.belongings.backpack == null) return null;
        for (Item it : hero.belongings.backpack) {
            if (it instanceof MissileWeapon) {
                MissileWeapon mw = (MissileWeapon) it;
                try {
                    if (mw.durabilityPerUse() <= 0f || mw.durabilityLeft() > mw.durabilityPerUse()) return mw;
                } catch (Throwable t) {
                    return mw;
                }
            }
        }
        return null;
    }

    /** 返回 {瞄准格, 目标怪格} 或 null。用 Ballistica 精确校验落点，不自己推几何。 */
    public static int[] findThrow(Level lv, int pos) {
        Mob m = RLNav.nearestVisibleMob(lv, pos);
        if (m == null || !RLNav.engageable(m)) return null;
        if (lv.distance(pos, m.pos) <= 1) return null;      // 贴身有 0.5x 命中惩罚，不丢
        MissileWeapon wep = bestMissile(Dungeon.hero);
        if (wep == null) return null;
        int w = lv.width(), len = lv.length();
        int[] nb8 = RLNav.neighbours8(w);
        int bestAim = -1, bestD = Integer.MAX_VALUE;
        // 候选：目标格本身 + 目标格 8 方向的 1..3 格外（"瞄得更靠前"往往才能穿到目标）
        for (int k = 0; k <= 3; k++) {
            for (int dir = 0; dir < 8; dir++) {
                int cand;
                if (k == 0) { if (dir > 0) continue; cand = m.pos; }
                else {
                    cand = m.pos + nb8[dir] * k;
                    if (cand < 0 || cand >= len) continue;
                    if (Math.abs(cand % w - m.pos % w) > k || Math.abs(cand / w - m.pos / w) > k) continue;
                }
                if (cand == pos) continue;
                if (lv.distance(pos, cand) > 8) continue;
                int landing;
                try {
                    landing = new Ballistica(pos, cand, Ballistica.PROJECTILE).collisionPos;
                } catch (Throwable t) { continue; }
                if (landing == m.pos) {
                    int dd = lv.distance(pos, cand);
                    if (dd < bestD) { bestD = dd; bestAim = cand; }
                }
            }
        }
        if (bestAim < 0) return null;
        return new int[]{bestAim, m.pos};
    }

    /** 程序化投掷（绕过必须精灵动画的 Item.cast）。返回 true 表示投出并占用回合。 */
    public static boolean executeThrow(Hero hero, int aimCell) {
        MissileWeapon wep = bestMissile(hero);
        if (wep == null) return false;
        try {
            wep.setCurrent(hero);
            int cell = wep.throwPos(hero, aimCell);
            Item i = wep.detach(hero.belongings.backpack);
            if (i == null) return false;
            throwsDone++;
            i.setCurrent(hero);
            // 1) 先当场结算：与 headless 路径逐字一致，保证命中判定/伤害/训练口径完全不变。
            i.rlOnThrow(cell);
            hero.spendAndNext(1.0f);
            // 2) 再补一条纯视觉飞行轨迹：不注册回调、不参与任何判定。
            //    （之前把结算挪进落地回调导致丢空——动画要 200ms，回调在 GL 线程执行时怪已移动，落点判空。）
            if (!rl.RLMode.headless && hero.sprite != null && hero.sprite.parent != null) {
                try {
                    ((com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite)
                            hero.sprite.parent.recycle(com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite.class))
                            .reset(hero.sprite, cell, i, new com.watabou.utils.Callback() {
                                @Override
                                public void call() { }
                            });
                } catch (Throwable t) { }
            }
            return true;
        } catch (Throwable t) {
            if (throwErrCount++ < 2) {
                System.err.println("[RL] throw exception: " + t);
                StackTraceElement[] st = t.getStackTrace();
                for (int i = 0; i < Math.min(9, st.length); i++) System.err.println("    at " + st[i]);
            }
            try { hero.spendAndNext(1.0f); } catch (Throwable t2) {}
            return true;
        }
    }
}
