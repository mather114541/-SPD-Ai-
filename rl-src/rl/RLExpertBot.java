package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.watabou.utils.PathFinder;
import java.util.ArrayDeque;
import java.util.Arrays;

/**
 * 行为克隆用的规则专家。信息与玩家完全同权：
 * 只用视野/已探格（visited/mapped）+ 当前可见状态，不用全局地图、楼梯位置作弊。
 *
 * 策略（与用户认可的"先探索完一层再换层"一致）：
 *   1. HP 低且有已鉴定治疗药水 -> 喝（动作9）
 *   2. 相邻格有怪 -> 朝怪方向移动/攻击
 *   3. 已在楼梯格 -> 原地等待（handle 自己格触发换层）
 *   4. 已探比例 > 55% 且已探格内有楼梯 -> 走向楼梯
 *   5. 否则 -> 走向最近的未探索可达格（BFS，驱动"先探索完一层"）
 *   6. 无可走目标 -> 原地等待
 */
public class RLExpertBot {

    public static int chooseAction(Hero h) {
        if (h == null || !h.isAlive()) return 8;
        // 1. 治疗
        if (h.HP < h.HT * 0.35f && hasHealing(h)) return 9;
        // 2. 邻格有门 -> 开门（map5=关闭门，handle 门格开门后进房）
        for (int i = 0; i < 8; i++) {
            int nc = h.pos + PathFinder.NEIGHBOURS8[i];
            if (nc >= 0 && nc < Dungeon.level.length() && Dungeon.level.map[nc] == 5) return i;
        }
        // 3. 邻格有怪 -> 攻击/朝怪移动
        for (int i = 0; i < 8; i++) {
            int nc = h.pos + PathFinder.NEIGHBOURS8[i];
            Char c = (nc >= 0 && nc < Dungeon.level.length()) ? Actor.findChar(nc) : null;
            if (c instanceof Mob) return i;
        }
        // 3. 已在楼梯格 -> 原地（handle 自己格触发换层）
        if (Dungeon.level.map[h.pos] == 8) return 8;
        // 4. 换层：已探比例够高且已探格内有楼梯 -> 走向楼梯
        int target = -1;
        if (exploredRatio() > 0.55f) target = nearestVisibleStair(h);
        // 5. 探索：最近未探可达格
        if (target < 0) target = nearestUnexplored(h);
        if (target >= 0) {
            int dir = firstStepDir(h.pos, target);
            if (dir >= 0) return dir;
        }
        return 8;
    }

    private static boolean hasHealing(Hero h) {
        if (h.belongings == null || h.belongings.backpack == null) return false;
        for (Item it : h.belongings.backpack.items) {
            if (it instanceof PotionOfHealing && it.isIdentified()) return true;
        }
        return false;
    }

    private static int L() { return Dungeon.level.length(); }

    private static boolean walkable(int cell) {
        if (cell < 0 || cell >= L() || !Dungeon.level.passable[cell]) return false;
        // 允许穿过怪格：英雄走向怪格 = 攻击，用于被围时突围
        return true;
    }

    private static float exploredRatio() {
        int ex = 0, tot = 0;
        for (int i = 0; i < L(); i++) {
            if (Dungeon.level.passable[i]) tot++;
            if (Dungeon.level.mapped[i] || Dungeon.level.visited[i]) ex++;
        }
        return tot > 0 ? (float) ex / tot : 0f;
    }

    private static int nearestVisibleStair(Hero h) {
        int best = -1, bd = Integer.MAX_VALUE;
        int w = Dungeon.level.width();
        for (int i = 0; i < L(); i++) {
            if (Dungeon.level.map[i] == 8 && (Dungeon.level.mapped[i] || Dungeon.level.visited[i])) {
                int d = Math.abs((i % w) - (h.pos % w)) + Math.abs((i / w) - (h.pos / w));
                if (d < bd) { bd = d; best = i; }
            }
        }
        return best;
    }

    private static int nearestUnexplored(Hero h) {
        int[] prev = new int[L()];
        Arrays.fill(prev, -1);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        prev[h.pos] = h.pos;
        q.add(h.pos);
        while (!q.isEmpty()) {
            int cur = q.poll();
            if (!Dungeon.level.mapped[cur] && !Dungeon.level.visited[cur]) return cur;
            for (int i = 0; i < 8; i++) {
                int nc = cur + PathFinder.NEIGHBOURS8[i];
                if (nc >= 0 && nc < L() && walkable(nc) && prev[nc] == -1) {
                    prev[nc] = cur;
                    q.add(nc);
                }
            }
        }
        return -1;
    }

    private static int firstStepDir(int from, int to) {
        int[] prev = new int[L()];
        Arrays.fill(prev, -1);
        ArrayDeque<Integer> q = new ArrayDeque<>();
        prev[from] = from;
        q.add(from);
        while (!q.isEmpty()) {
            int cur = q.poll();
            if (cur == to) break;
            for (int i = 0; i < 8; i++) {
                int nc = cur + PathFinder.NEIGHBOURS8[i];
                if (nc >= 0 && nc < L() && walkable(nc) && prev[nc] == -1) {
                    prev[nc] = cur;
                    q.add(nc);
                }
            }
        }
        if (prev[to] == -1) return -1;
        int cur = to;
        while (prev[cur] != from) cur = prev[cur];
        for (int i = 0; i < 8; i++) {
            if (from + PathFinder.NEIGHBOURS8[i] == cur) return i;
        }
        return -1;
    }
}
