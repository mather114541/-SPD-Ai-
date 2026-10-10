package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

/**
 * 宏意图专家（行为克隆数据来源）。只输出"意图"，寻路由环境宏执行器负责。
 * 优先序：喝药保命 > 吃饭 > 下楼(站出口) > 打邻格怪 > 搜秘密门 > 去已探出口 > 探索 frontier > 搜索兜底。
 */
public class RLExpert {

    public static final int M_EXPLORE = 0, M_EXIT = 1, M_FIGHT = 2, M_HEAL = 3,
                            M_EAT = 4, M_SEARCH = 5, M_PICKUP = 6, M_WAIT = 7, M_THROW = 8;

    public static boolean fallbackSearch = false;   // 兜底全墙搜索触发标志（全探完找不到出口时）

    /** 战斗技巧总开关（A/B 实验用）。 */
    public static boolean holdEnabled = true;
    /** 对峙等待：与可见怪相隔一格(切比雪夫==2)时原地等待，让它先走进攻击距离，从而抢到先手。练一次足够。 */
    public static int HOLD_MAX = 1;
    /** 是否主动接敌（可见怪在切比雪夫 <=2 时决定交战）。关闭则退回"只打贴身怪"。 */
    public static boolean engageNearby = true;
    public static boolean throwEnabled = true;
    /** 拾取开关（A/B 用）：看到可拾取的普通掉落就去捡。 */
    public static boolean pickupEnabled = true;
    /** 全探索模式：只要还有能到达的未探区域/秘密门，就先探索完再下楼。 */
    public static boolean fullExplore = false;   // 全探索会因"卡死"拖累下潜，详见备忘录第十节；修好卡死后再打开
    /** 全探索目标覆盖率：探到这个比例就下楼，避免卡在最后几个走不到的 frontier。 */
    public static double fullExploreTarget = 0.92;
    /** 探索/搜索目标覆盖率：达到就下楼。0.99=全探索（用户拍板，为后续多功能打基础；配合"饥肠辘辘吃口粮"防饿死）。 */
    public static double exploreTarget = 0.99;
    /** 关闭速度感知（A/B 用）：true 时对快速怪也照用等待技巧（错误行为）。 */
    public static boolean holdIgnoreSpeed = false;
    private static int holdPos = -1;
    private static int holdCount = 0;
    /** 停滞检测：连续多少回合原地不动。用于"全探索但走不到 frontier"时放弃并下楼。 */
    public static int stallLimit = 12;
    private static int stall = 0;
    private static int stallPos = -1;

    public static void resetStall() {
        stall = 0;
        stallPos = -1;
    }

    public static int holdSlow = 0;
    public static int holdFast = 0;

    public static void resetState() {
        holdPos = -1;
        holdCount = 0;
    }

    public static void resetCounters() {
        holdSlow = 0;
        holdFast = 0;
    }

    public static int policy() {
        fallbackSearch = false;   // 每轮重置；仅末尾兜底全墙搜索分支置 true
        Hero h = Dungeon.hero;
        if (h == null || !h.isAlive()) return M_WAIT;
        Level lv = Dungeon.level;
        boolean[] known = RLNav.knownMap(lv);
        boolean[] walk = RLNav.buildWalkable(lv, known, h.pos);
        int len = lv.length();
        int[] nb8 = RLNav.neighbours8(lv.width());

        if (h.pos == stallPos) stall++; else { stall = 0; stallPos = h.pos; }

        if (h.HP <= Math.max(8, (int)(h.HT * 0.60f)) && RLNav.hasType(h, PotionOfHealing.class)) return M_HEAL;

        Hunger hun = h.buff(Hunger.class);
        // 用户规则：饥肠辘辘（STARVING，开始缓慢掉血）且生命值<85% -> 立刻吃口粮（配合全探索防饿死）
        if (hun != null && hun.hunger() >= Hunger.STARVING && h.HP < h.HT * 0.85f && RLNav.hasType(h, Food.class)) return M_EAT;

        for (int d = 0; d < 8; d++) {
            int c = h.pos + nb8[d];
            if (c < 0 || c >= len) continue;
            if (!lv.heroFOV[c]) continue;
            Char ch = Actor.findChar(c);
            if (ch instanceof Mob && RLNav.engageable((Mob) ch)) { resetState(); return M_FIGHT; }
        }

        // 远程消耗：距离 > 1（避开 0.5x 贴身命中惩罚）、弹道可达、且背包里还有投掷物
        if (throwEnabled && RLCombat.bestMissile(h) != null && RLCombat.findThrow(lv, h.pos) != null) {
            resetState();
            return M_THROW;
        }

        // 战斗技巧 1（抢先后手）：决定接敌时，若与怪相隔一格(切比雪夫==2)，
        // 原地等待一回合让怪先走进攻击距离，下一回合英雄先出手（而不是走上去先挨一下）。
        // 交战：可见怪在接敌范围内 -> 交给 M_FIGHT 执行器处理（内含按怪速度的站位/等待/抢先后手）
        if (engageNearby) {
            com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob nm = RLNav.nearestVisibleMob(lv, h.pos);
            if (nm != null && RLNav.engageable(nm) && lv.distance(h.pos, nm.pos) <= RLCombat.engageRange) {
                holdPos = -1;
                holdCount = 0;
                return M_FIGHT;
            }
            holdPos = -1;
            holdCount = 0;
        }

        // 拾取：只要能看到可拾取的东西（含自己掷出的投掷物）就去捡 —— 信息与玩家同权（只用 heroFOV 内的堆）
        if (pickupEnabled) {
            if (RLNav.lootAt(lv, h.pos)) { resetState(); return M_PICKUP; }
            boolean[] items = RLNav.visibleLootMask(lv);
            if (RLNav.firstStep(lv, walk, h.pos, items) >= 0) { resetState(); return M_PICKUP; }
        }

        // （已移除：旧版"邻格是 SECRET_DOOR 就搜"是穿雾定位，违反玩家同权。搜索决策改为下方可疑位启发式）

        // 还有可探索区域吗（可达 frontier / 可达可疑搜索点）
        boolean[] fr = RLNav.frontierMap(lv, known);
        boolean frReach = RLNav.firstStep(lv, walk, h.pos, fr) >= 0;
        boolean[] spot = RLNav.suspiciousSpotMask(lv, known);
        boolean spotReach = RLNav.firstStep(lv, walk, h.pos, spot) >= 0;

        // 覆盖率：已探可走格 / 总可走格
        int kp = 0, tp = 0;
        for (int i = 0; i < len; i++) {
            if (lv.passable[i]) { tp++; if (known[i]) kp++; }
        }
        double cov = tp > 0 ? (double) kp / tp : 1.0;

        // 当前位置就是可疑搜索点（出生房间边界墙 / 死胡同尽头）→ 立即搜一次（先搜再探索）
        if (spot[h.pos]) return M_SEARCH;

        // 探索/搜索优先于下楼：还有能到达的未探区域或可疑点、且覆盖率未达标、且没停滞 -> 继续探/搜
        if ((frReach || spotReach) && cov < exploreTarget && stall < stallLimit) {
            return frReach ? M_EXPLORE : M_SEARCH;
        }

        // 探索/搜索完毕、覆盖率达标或停滞：可以下楼
        if (lv.map[h.pos] == Terrain.EXIT && (lv.visited[h.pos] || lv.mapped[h.pos])) return M_EXIT;
        boolean[] ex = RLNav.exitMask(lv, known);
        if (RLNav.firstStep(lv, walk, h.pos, ex) >= 0) return M_EXIT;

        // 兜底全墙搜索：全探完（无可达 frontier、覆盖率达 exploreTarget）仍找不到出口（出口藏隐藏门后）
        // -> 系统搜未搜过的贴墙格，直到搜出隐藏门。这是打破"探索100%却下不了楼"卡死的必要兜底，
        //    仅在真正全探完时触发（不含停滞，避免乱搜拖慢）。
        // 触发条件用"真的没得探了"（无可达 frontier、无可达可疑点、出口也不可达），
        // 不能再用 cov >= exploreTarget：可达区只占约 86%，覆盖率永远够不到 0.99 -> 兜底永不触发（死代码）。
        if (RLNav.needFallbackSearch(lv, known, walk, h.pos)) {
            fallbackSearch = true;
            return M_SEARCH;
        }
        return M_SEARCH;
    }
}
