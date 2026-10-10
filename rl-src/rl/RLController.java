package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.watabou.utils.PathFinder;

/**
 * 破碎的像素地牢 RL 环境控制器（headless）。
 *
 * 观察 v2（全层已探自动地图，玩家同权）：定长 int[]，OBS_LEN = 2400。
 *   [0 .. 2303]       MAPW x MAPH = 48x48 的每格位域（见 B_* 常量）
 *   [2304 .. 2310]    meta: depth,pos,HP,HT,STR,lvl,gold
 *   [2311 .. 2318]    global: 到入口距离,已探索格数,可走格总数,怪数,物品堆数,陷阱数,出口已知,站出口
 *   [2319 .. 2344]    belongings: 装备槽6x2 + 背包计数14
 *   [2345 .. 2370]    itemDetails: 药水12 + 卷轴12 + 未知药水 + 未知卷轴
 *   [2371 .. 2383]    seeds: 13 种植物种子
 *   [2384 .. 2399]    status: 饥饿1 + 状态15
 *
 * 动作（14 离散）：0-7 移动八方向；8 原地/等待；9 喝已鉴定治疗药水；10 喝已鉴定力量药水；
 *                  11 读已鉴定升级卷轴；12 搜索；13 吃食物。
 */
public class RLController {

    public static final int N_ACTIONS = 9;   // 宏意图：探索/下楼/攻击/喝药/吃饭/搜索/拾取/等待/投掷

    public static final int M_EXPLORE = 0, M_EXIT = 1, M_FIGHT = 2, M_HEAL = 3,
                            M_EAT = 4, M_SEARCH = 5, M_PICKUP = 6, M_WAIT = 7, M_THROW = 8;

    public static int searchDone = 0;   // 实际执行 h.search(true) 的次数（区别于 M_SEARCH 宏发出次数）
    public static int _dbg = 0;   // 临时调试计数

    public static final int MAPW = 48;
    public static final int MAPH = 48;
    public static final int NMAP = MAPW * MAPH;   // 2304

    // 每格位域
    public static final int B_KNOWN    = 1 << 0;
    public static final int B_PASSABLE = 1 << 1;
    public static final int B_WALL     = 1 << 2;
    public static final int B_DOOR     = 1 << 3;
    public static final int B_AWARE    = 1 << 4;   // 可见且该怪当前视野里有英雄（已发现我们）
    public static final int B_GRASS    = 1 << 5;
    public static final int B_WATER    = 1 << 6;
    public static final int B_FOV      = 1 << 7;
    public static final int B_MOB      = 1 << 8;
    public static final int B_ITEM     = 1 << 9;
    public static final int B_TRAP     = 1 << 10;
    public static final int B_FRONTIER = 1 << 11;
    public static final int B_HERO     = 1 << 12;
    public static final int B_ENTRANCE = 1 << 13;
    public static final int B_EXIT     = 1 << 14;
    public static final int B_MOB_FAST = 1 << 15;   // 可见怪且速度>1（如螃蟹 2 动/回合），玩家能观察到的属性

    public static final int OFF_MAP = 0;
    public static final int N_META = 7;
    public static final int N_GLOBAL = 8;
    public static final int N_BELONGINGS = 12 + 14;       // 装备槽6x2 + 背包计数14
    public static final int N_ITEM_DETAILS = 12 + 12 + 2; // 药水12 + 卷轴12 + 未知药水 + 未知卷轴
    public static final int N_SEED = 13;
    public static final int N_STATUS = 16;
    public static final int N_COMBAT = 9;   // 战斗态势（见 encodeCombat）

    public static final int OFF_META   = NMAP;                        // 2304
    public static final int OFF_GLOBAL = OFF_META + N_META;           // 2311
    public static final int OFF_BELONG = OFF_GLOBAL + N_GLOBAL;       // 2319
    public static final int OFF_ITEMD  = OFF_BELONG + N_BELONGINGS;   // 2345
    public static final int OFF_SEED   = OFF_ITEMD + N_ITEM_DETAILS;  // 2371
    public static final int OFF_STATUS = OFF_SEED + N_SEED;           // 2384
    public static final int OFF_COMBAT = OFF_STATUS + N_STATUS;       // 2400
    public static final int OBS_LEN    = OFF_COMBAT + N_COMBAT;       // 2409

    /** 英雄位置的 obs 下标（元信息第 2 项），供 Python 侧读取。 */
    public static final int POS_IDX = OFF_META + 1;

    private int lastReward;
    private boolean done;
    private int lastFailCell = -1;
    private int failCount = 0;
    public int targetDepth = 2;
    public boolean exitFound = false;
    /** 刚搜索过一个可疑点，下次应移走——避免在同一个可疑点反复原地搜索而卡死。 */
    private boolean pendingMoveAfterSearch = false;

    public RLController() {
        // 绝对不要在这里强制 RLMode.headless = true！
        // GUI 托管路径（RLAutoPilot 创建本控制器）会因此把全局渲染/UI 全部短路：
        //   GameScene.updateFog() 开头就 if (RLMode.headless) return; -> 雾纹理永远停在纯黑 = 视野全黑
        //   GameScene 里另有十几处同样短路，按钮/输入分支也被 return -> 点了没反应 = 不能暂停
        // headless 环境由各自入口（main）显式设置，RLMode.headless 的默认值本来就是 true。
    }

    public int[] reset(long seed) {
        com.watabou.noosa.Game.version = "4.0.0";
        com.watabou.noosa.Game.versionCode = 400;
        String dataDir = System.getProperty("user.dir") + "\\savedata";
        com.watabou.utils.FileUtils.setDefaultFileProperties(com.badlogic.gdx.Files.FileType.Absolute, dataDir);
        com.shatteredpixel.shatteredpixeldungeon.SPDSettings.customSeed("");
        com.shatteredpixel.shatteredpixeldungeon.SPDSettings.intro(false);
        GamesInProgress.selectedClass = HeroClass.WARRIOR;
        Dungeon.seed = seed;
        Dungeon.init();
        Level lv = Dungeon.newLevel();
        Dungeon.switchLevel(lv, lv.entrance());
        computeFOV();
        lastReward = 0;
        done = false;
        exitFound = false;
        RLExpert.resetState();
        RLExpert.resetStall();
        RLCombat.resetState();
        RLNav.resetSearched(lv.length());
        RLNav.resetBlocked(lv.length());
        return encodeObs();
    }

    public int[] step(int macro) {
        Hero h = Dungeon.hero;
        int prevPos = h.pos;
        int moveCell = -1;
        int prevDepth = Dungeon.depth;
        int prevHP = h.HP;
        int prevSlain = Statistics.enemiesSlain;
        int prevExplored = countExplored();
        int prevTotal = countPassable();

        boolean usedItem = false;
        Level lv = Dungeon.level;
        int prevSecrets = countSecretCells(lv);
        int pos = h.pos;
        int[] nb8 = RLNav.neighbours8(lv.width());
        boolean[] known = RLNav.knownMap(lv);
        boolean[] walk = RLNav.buildWalkable(lv, known, pos);

        switch (macro) {
            case M_EXPLORE: {
                int dE = RLNav.exploreStep(lv, known, walk, pos);   // 持久化 frontier 目标，避免来回掉头
                if (dE < 0) dE = RLNav.firstStep(lv, walk, pos, RLNav.suspiciousSpotMask(lv, known));
                if (dE < 0) dE = RLNav.firstStep(lv, walk, pos, RLNav.exitMask(lv, known));
                moveCell = stepOrWait(h, pos, dE, nb8);
                break;
            }
            case M_EXIT: {
                if (lv.map[pos] == Terrain.EXIT && (lv.visited[pos] || lv.mapped[pos])) {
                    moveCell = pos;
                    h.handle(pos);
                } else {
                    boolean[] ex = RLNav.exitMask(lv, known);
                    int dExit = RLNav.firstStep(lv, walk, pos, ex);
                    if (dExit < 0) {
                        // 出口已知但当前不可达（锁门/隐藏门等）→ 回退探索，找钥匙/找路
                        dExit = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    }
                    moveCell = stepOrWait(h, pos, dExit, nb8);
                }
                break;
            }
            case M_FIGHT: {
                int dF = RLCombat.engageDir(lv, pos);   // 战斗微操：按怪速度维持安全站位/等待/抢先后手
                if (dF == RLCombat.HOLD) {
                    moveCell = pos;
                    h.handle(pos);
                } else if (dF == RLCombat.NO_TARGET) {
                    int dE = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    moveCell = stepOrWait(h, pos, dE, nb8);
                } else {
                    moveCell = stepOrWait(h, pos, dF, nb8);
                }
                break;
            }
            case M_PICKUP: {
                if (RLNav.lootAt(lv, pos)) {
                    moveCell = pos;
                    h.handle(pos);
                } else {
                    boolean[] items = RLNav.visibleLootMask(lv);
                    int dI = RLNav.firstStep(lv, walk, pos, items);
                    if (dI < 0) dI = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    moveCell = stepOrWait(h, pos, dI, nb8);
                }
                break;
            }
            case M_HEAL:
                usedItem = RLItemPolicy.applyAction(h, 9);
                if (!usedItem) h.handle(pos);
                break;
            case M_EAT:
                usedItem = RLItemPolicy.applyAction(h, 13);
                if (!usedItem) h.handle(pos);
                break;
            case M_THROW: {
                try {
                    int[] aim = RLCombat.findThrow(lv, pos);
                    if (aim == null || !RLCombat.executeThrow(h, aim[0])) {
                        h.handle(pos);
                    }
                } catch (Exception ex) {
                    h.handle(pos);   // 投掷偶发异常（如目标无效）→ 原地等待，避免崩溃
                }
                break;
            }
            case M_SEARCH: {
                // 兜底判定必须由 env 当场计算：proto 的策略推理路径（step <a>）不会调用 RLExpert.policy()，
                // 依赖"专家先设标志"会导致训练/部署口径不一致（训练是兜底搜索，部署是普通搜索）。
                boolean[] spot = RLNav.needFallbackSearch(lv, known, walk, pos)
                        ? RLNav.fallbackSpotMask(lv, known)   // 兜底全墙搜索（全探完仍找不到出口）
                        : RLNav.suspiciousSpotMask(lv, known);
                if (spot[pos] && !pendingMoveAfterSearch) {
                    searchDone++;
                    h.search(true);   // 当前格可疑，搜一次
                    if (RLNav.searchedCell != null && pos >= 0 && pos < RLNav.searchedCell.length) RLNav.searchedCell[pos] = true;   // 标记已搜，避免反复搜同一点
                    pendingMoveAfterSearch = true;
                    break;
                }
                pendingMoveAfterSearch = false;   // 刚搜过则移走
                int dS = RLNav.firstStep(lv, walk, pos, spot);
                if (dS < 0) dS = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                moveCell = stepOrWait(h, pos, dS, nb8);
                break;
            }
            default:
                h.handle(pos);
                break;
        }

        Actor.processTurn();
        computeFOV();

        // 连续 3 次朝同一格移动都失败 -> 记为阻塞（钥匙不匹配的锁门 / 打不动的物体 …），避免无限原地循环。
        // 用"连续失败 3 次"而不是"失败 1 次"，避免麻痹/动作延迟造成误判把自己围死。
        if (moveCell >= 0 && moveCell != prevPos && h.pos == prevPos) {
            com.shatteredpixel.shatteredpixeldungeon.actors.Char occ =
                    com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(moveCell);
            boolean fightable = occ instanceof com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob
                    && RLNav.engageable((com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob) occ)
                    && Dungeon.level.heroFOV[moveCell];
            if (fightable) {
                lastFailCell = -1;
                failCount = 0;
            } else if (moveCell == lastFailCell) {
                failCount++;
                if (failCount >= 3) RLNav.markBlocked(moveCell);
            } else {
                lastFailCell = moveCell;
                failCount = 1;
            }
        } else {
            lastFailCell = -1;
            failCount = 0;
        }

        int r = 0;
        int found = prevSecrets - countSecretCells(Dungeon.level);
        if (found > 0) r += 80 * found;   // 本回合发现隐藏门/陷阱 -> 高奖励，让 AI 学会主动搜索
        if (!h.isAlive()) {
            r -= 5;
            done = true;
        } else {
            r += 4 * (countExplored() - prevExplored);
            {
                int blen = Dungeon.level.length();
                boolean boundary = false;
                int[] nb8r = com.watabou.utils.PathFinder.NEIGHBOURS8;
                for (int d = 0; d < 8; d++) {
                    int c = h.pos + nb8r[d];
                    if (c < 0 || c >= blen) continue;
                    if (Dungeon.level.passable[c] && !(Dungeon.level.visited[c] || Dungeon.level.mapped[c])) { boundary = true; break; }
                }
                if (boundary) r += 2;
            }
            {
                int blen2 = Dungeon.level.length();
                boolean onExit = false, nearExit = false;
                int ww = Dungeon.level.width();
                int[] my = {0, -1, 1, -ww, ww, -ww - 1, -ww + 1, ww - 1, ww + 1};
                for (int d2 = 0; d2 < 9; d2++) {
                    int cc = h.pos + my[d2];
                    if (cc < 0 || cc >= blen2) continue;
                    if (Dungeon.level.map[cc] == 8 && (Dungeon.level.visited[cc] || Dungeon.level.mapped[cc])) {
                        if (d2 == 0) onExit = true;
                        else nearExit = true;
                    }
                }
                if (onExit) r += 30;
                else if (nearExit) r += 15;
            }
            if (!exitFound) {
                for (int ec = 0; ec < Dungeon.level.length(); ec++) {
                    if (Dungeon.level.map[ec] == 8 && Dungeon.level.heroFOV[ec]) { exitFound = true; r += 80; break; }
                }
            }
            if (Dungeon.depth > prevDepth) {
                float p = prevTotal > 0 ? (float)prevExplored / prevTotal : 0.5f;
                r += 1500 + (int)(p * 1500);
                RLNav.resetSearched(Dungeon.level.length());   // 换层：清空本层已搜标记
                RLNav.resetBlocked(Dungeon.level.length());    // 换层：清空阻塞标记
            }
            if (Statistics.enemiesSlain > prevSlain) r += 2;
            if (h.HP < prevHP) r -= 1;
            else if (h.HP > prevHP) r += 1;
            if (usedItem) r += 1;
            Trap tr = Dungeon.level.traps == null ? null : Dungeon.level.traps.get(h.pos);
            if (tr != null && tr.active) r -= 2;
            if (moveCell >= 0 && moveCell != h.pos && h.pos == prevPos) {
                if (Dungeon.level.map[moveCell] == Terrain.LOCKED_DOOR) {
                    r -= 1;
                } else {
                    Heap hh = Dungeon.level.heaps.get(moveCell);
                    if (hh != null && hh.type == Heap.Type.LOCKED_CHEST) r -= 1;
                }
            }
            if (Dungeon.depth >= targetDepth) done = true;
        }
        lastReward = r;
        return encodeObs();
    }

    /** 托管（游戏 GUI 内）：宏动作 → 设 HeroAction（不含 Actor.processTurn，由 GUI 回合循环执行）。 */
    public static void applyMacro(Hero h, int macro) {
        Level lv = Dungeon.level;
        int pos = h.pos;
        int[] nb8 = RLNav.neighbours8(lv.width());
        boolean[] known = RLNav.knownMap(lv);
        boolean[] walk = RLNav.buildWalkable(lv, known, pos);
        switch (macro) {
            case M_EXPLORE: {
                int dE = RLNav.exploreStep(lv, known, walk, pos);
                if (dE < 0) dE = RLNav.firstStep(lv, walk, pos, RLNav.suspiciousSpotMask(lv, known));
                if (dE < 0) dE = RLNav.firstStep(lv, walk, pos, RLNav.exitMask(lv, known));
                stepOrWait(h, pos, dE, nb8);
                break;
            }
            case M_EXIT: {
                if (lv.map[pos] == Terrain.EXIT && (lv.visited[pos] || lv.mapped[pos])) {
                    h.handle(pos);
                } else {
                    int dExit = RLNav.firstStep(lv, walk, pos, RLNav.exitMask(lv, known));
                    if (dExit < 0) dExit = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    stepOrWait(h, pos, dExit, nb8);
                }
                break;
            }
            case M_FIGHT: {
                int dF = RLCombat.engageDir(lv, pos);
                if (dF == RLCombat.HOLD) {
                    h.handle(pos);
                } else if (dF == RLCombat.NO_TARGET) {
                    int dE = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    stepOrWait(h, pos, dE, nb8);
                } else {
                    stepOrWait(h, pos, dF, nb8);
                }
                break;
            }
            case M_PICKUP: {
                if (RLNav.lootAt(lv, pos)) {
                    h.handle(pos);
                } else {
                    int dI = RLNav.firstStep(lv, walk, pos, RLNav.visibleLootMask(lv));
                    if (dI < 0) dI = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    stepOrWait(h, pos, dI, nb8);
                }
                break;
            }
            case M_HEAL:
                if (!RLItemPolicy.applyAction(h, 9)) h.handle(pos);
                break;
            case M_EAT:
                if (!RLItemPolicy.applyAction(h, 13)) h.handle(pos);
                break;
            case M_THROW: {
                try {
                    int[] aim = RLCombat.findThrow(lv, pos);
                    if (aim == null || !RLCombat.executeThrow(h, aim[0])) h.handle(pos);
                } catch (Exception ex) {
                    h.handle(pos);
                }
                break;
            }
            case M_SEARCH: {
                boolean[] spot = RLNav.needFallbackSearch(lv, known, walk, pos)
                        ? RLNav.fallbackSpotMask(lv, known)
                        : RLNav.suspiciousSpotMask(lv, known);
                if (spot[pos]) {
                    h.search(true);
                } else {
                    int dS = RLNav.firstStep(lv, walk, pos, spot);
                    if (dS < 0) dS = RLNav.firstStep(lv, walk, pos, RLNav.frontierMap(lv, known));
                    stepOrWait(h, pos, dS, nb8);
                }
                break;
            }
            default:
                h.handle(pos);
                break;
        }
    }

    /** 该层当前尚未发现的秘密格数（SECRET_DOOR / SECRET_TRAP）。发现后地形变为真门/真陷阱，计数减少。 */
    private int countSecretCells(Level lv) {
        int c = 0;
        for (int i = 0; i < lv.length(); i++) {
            int m = lv.map[i];
            if (m == Terrain.SECRET_DOOR || m == Terrain.SECRET_TRAP) c++;
        }
        return c;
    }

    private static int stepOrWait(Hero h, int pos, int d, int[] nb8) {
        if (d < 0) { h.handle(pos); return pos; }
        int cell = pos + nb8[d];
        if (cell < 0 || cell >= Dungeon.level.length()) cell = pos;
        if (!h.handle(cell)) h.handle(pos);
        return cell;
    }

    private int countExplored() {
        int n = 0;
        for (int i = 0; i < Dungeon.level.length(); i++) {
            if (Dungeon.level.mapped[i] || Dungeon.level.visited[i]) n++;
        }
        return n;
    }

    private int countPassable() {
        int n = 0;
        for (int i = 0; i < Dungeon.level.length(); i++) {
            if (Dungeon.level.passable[i]) n++;
        }
        return n;
    }

    /** 自研 raycast 视野（玩家同权，用可走性判断遮挡），同步 heroFOV + visited。 */
    private void computeFOV() {
        boolean[] fov = Dungeon.level.heroFOV;
        boolean[] blocking = Dungeon.level.passable;
        int w = Dungeon.level.width();
        int h = Dungeon.level.height();
        int hx = Dungeon.hero.pos % w;
        int hy = Dungeon.hero.pos / w;
        int radius = Math.max(6, Dungeon.hero.viewDistance);
        for (int i = 0; i < fov.length; i++) fov[i] = false;
        fov[Dungeon.hero.pos] = true;
        int r2 = radius * radius;
        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                if (dx * dx + dy * dy > r2) continue;
                int x = hx + dx, y = hy + dy;
                if (x < 0 || y < 0 || x >= w || y >= h) continue;
                if (losClear(hx, hy, x, y, w, blocking)) fov[y * w + x] = true;
            }
        }
        for (int i = 0; i < fov.length; i++) if (fov[i]) Dungeon.level.visited[i] = true;
        Dungeon.hero.fieldOfView = Dungeon.level.heroFOV;
    }

    private boolean losClear(int x0, int y0, int x1, int y1, int w, boolean[] blocking) {
        int dx = Math.abs(x1 - x0), dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1, sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        int x = x0, y = y0;
        while (x != x1 || y != y1) {
            if ((x != x0 || y != y0) && !blocking[y * w + x]) return false;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x += sx; }
            if (e2 < dx) { err += dx; y += sy; }
        }
        return true;
    }

    public static int[] encodeObs() {
        int[] out = new int[OBS_LEN];
        encodeMap(out);
        Level lv = Dungeon.level;
        out[OFF_META]     = Dungeon.depth;
        out[OFF_META + 1] = Dungeon.hero.pos;
        out[OFF_META + 2] = Dungeon.hero.HP;
        out[OFF_META + 3] = Dungeon.hero.HT;
        out[OFF_META + 4] = Dungeon.hero.STR;
        out[OFF_META + 5] = Dungeon.hero.lvl;
        out[OFF_META + 6] = Dungeon.gold;
        encodeGlobal(out, OFF_GLOBAL);
        encodeBelongings(out, OFF_BELONG);
        encodeItemDetails(out, OFF_ITEMD);
        encodeSeeds(out, OFF_SEED);
        encodeStatus(out, OFF_STATUS);
        encodeCombat(out, OFF_COMBAT);
        return out;
    }

    /** 全层已探自动地图：每格一个位域（B_*），越界/未探为 0。玩家同权。 */
    private static void encodeMap(int[] out) {
        Level lv = Dungeon.level;
        int w = lv.width(), h = lv.height();
        int len = lv.length();
        boolean[] known = new boolean[len];
        for (int i = 0; i < len; i++) known[i] = lv.visited[i] || lv.mapped[i];

        boolean[] frontier = new boolean[len];
        int[] nb8 = new int[]{-w - 1, -w, -w + 1, -1, 1, w - 1, w, w + 1};
        for (int i = 0; i < len; i++) {
            if (!known[i] || !lv.passable[i]) continue;
            int cx = i % w, cy = i / w;
            for (int k = 0; k < 8; k++) {
                int n = i + nb8[k];
                if (n < 0 || n >= len) continue;
                if (Math.abs(n % w - cx) > 1 || Math.abs(n / w - cy) > 1) continue;
                if (!known[n]) { frontier[i] = true; break; }
            }
        }

        boolean[] mobHere = new boolean[len];
        boolean[] mobFast = new boolean[len];
        boolean[] mobAware = new boolean[len];
        int heroPos0 = Dungeon.hero.pos;
        if (lv.mobs != null) {
            for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : lv.mobs) {
                if (m.pos >= 0 && m.pos < len && lv.heroFOV[m.pos]) {
                    mobHere[m.pos] = true;
                    if (m.speed() > 1.0f) mobFast[m.pos] = true;
                    if (m.fieldOfView != null && m.fieldOfView.length == len && m.fieldOfView[heroPos0]) mobAware[m.pos] = true;
                }
            }
        }

        int heroPos = Dungeon.hero.pos;
        for (int y = 0; y < h && y < MAPH; y++) {
            for (int x = 0; x < w && x < MAPW; x++) {
                int i = y * w + x;
                int code = 0;
                if (known[i]) {
                    code |= B_KNOWN;
                    int m = lv.map[i];
                    if (lv.passable[i]) code |= B_PASSABLE;
                    else if (m == Terrain.WALL || m == Terrain.WALL_DECO || m == Terrain.SECRET_DOOR
                            || m == Terrain.BOOKSHELF || m == Terrain.STATUE || m == Terrain.STATUE_SP) code |= B_WALL;
                    if (m == Terrain.DOOR || m == Terrain.OPEN_DOOR || m == Terrain.LOCKED_DOOR
                            || m == Terrain.CRYSTAL_DOOR) code |= B_DOOR;
                    if (m == Terrain.ENTRANCE || m == Terrain.ENTRANCE_SP) code |= B_ENTRANCE;
                    if (m == Terrain.EXIT || m == Terrain.LOCKED_EXIT || m == Terrain.UNLOCKED_EXIT) code |= B_EXIT;
                    if (m == Terrain.HIGH_GRASS || m == Terrain.FURROWED_GRASS) code |= B_GRASS;
                    if (m == Terrain.WATER) code |= B_WATER;
                }
                if (lv.heroFOV[i]) code |= B_FOV;
                if (mobHere[i]) code |= B_MOB;
                if (mobFast[i]) code |= B_MOB_FAST;
                if (mobHere[i] && mobAware[i]) code |= B_AWARE;
                if (lv.heroFOV[i] && lv.heaps != null && lv.heaps.get(i) != null) code |= B_ITEM;
                Trap t = lv.traps == null ? null : lv.traps.get(i);
                if (lv.heroFOV[i] && t != null && t.visible) code |= B_TRAP;
                if (frontier[i]) code |= B_FRONTIER;
                if (i == heroPos) code |= B_HERO;
                out[y * MAPW + x] = code;
            }
        }
    }

    private static void encodeGlobal(int[] out, int off) {
        Level lv = Dungeon.level;
        int hero = Dungeon.hero.pos;
        out[off]     = lv.distance(hero, lv.entrance());
        int explored = 0, passable = 0, exitKnown = 0;
        for (int i = 0; i < lv.length(); i++) {
            if (lv.mapped[i] || lv.visited[i]) explored++;
            if (lv.passable[i]) passable++;
            if (lv.map[i] == Terrain.EXIT && (lv.visited[i] || lv.mapped[i])) exitKnown++;
        }
        out[off + 1] = explored;
        out[off + 2] = passable;
        out[off + 3] = lv.mobs == null ? 0 : lv.mobs.size();
        out[off + 4] = lv.heaps == null ? 0 : lv.heaps.size;
        out[off + 5] = lv.traps == null ? 0 : lv.traps.size;
        out[off + 6] = exitKnown;
        out[off + 7] = (lv.map[hero] == Terrain.EXIT && (lv.visited[hero] || lv.mapped[hero])) ? 1 : 0;
    }

    private static void encodeBelongings(int[] out, int off) {
        Belongings bg = Dungeon.hero.belongings;
        Item[] slots = { bg.weapon, bg.armor, bg.artifact, bg.misc, bg.ring, bg.secondWep };
        for (int i = 0; i < 6; i++) {
            Item it = slots[i];
            out[off + i * 2]     = it == null ? 0 : itemCategory(it);
            out[off + i * 2 + 1] = it instanceof EquipableItem ? ((EquipableItem) it).level() : 0;
        }
        int[] counts = new int[14];
        if (bg.backpack != null) {
            for (Item it : bg.backpack) counts[itemCategory(it)]++;
        }
        for (int i = 0; i < 14; i++) out[off + 12 + i] = counts[i];
    }

    private static void encodeItemDetails(int[] out, int off) {
        int[] pot = new int[12];
        int[] scr = new int[12];
        int unPot = 0, unScr = 0;
        Belongings bg = Dungeon.hero.belongings;
        if (bg.backpack != null) {
            for (Item it : bg.backpack) {
                if (it instanceof Potion) {
                    if (it.isIdentified()) { int p = potIdx(it); if (p >= 0) pot[p]++; }
                    else unPot++;
                } else if (it instanceof Scroll) {
                    if (it.isIdentified()) { int s = scIdx(it); if (s >= 0) scr[s]++; }
                    else unScr++;
                }
            }
        }
        for (int i = 0; i < 12; i++) out[off + i] = pot[i];
        for (int i = 0; i < 12; i++) out[off + 12 + i] = scr[i];
        out[off + 24] = unPot;
        out[off + 25] = unScr;
    }

    private static int potIdx(Item it) {
        if (it instanceof PotionOfHealing) return 0;
        if (it instanceof PotionOfStrength) return 1;
        if (it instanceof PotionOfExperience) return 2;
        if (it instanceof PotionOfLiquidFlame) return 3;
        if (it instanceof PotionOfFrost) return 4;
        if (it instanceof PotionOfToxicGas) return 5;
        if (it instanceof PotionOfParalyticGas) return 6;
        if (it instanceof PotionOfHaste) return 7;
        if (it instanceof PotionOfInvisibility) return 8;
        if (it instanceof PotionOfLevitation) return 9;
        if (it instanceof PotionOfMindVision) return 10;
        if (it instanceof PotionOfPurity) return 11;
        return -1;
    }

    private static int scIdx(Item it) {
        if (it instanceof ScrollOfUpgrade) return 0;
        if (it instanceof ScrollOfTeleportation) return 1;
        if (it instanceof ScrollOfIdentify) return 2;
        if (it instanceof ScrollOfMagicMapping) return 3;
        if (it instanceof ScrollOfRetribution) return 4;
        if (it instanceof ScrollOfTerror) return 5;
        if (it instanceof ScrollOfRage) return 6;
        if (it instanceof ScrollOfRemoveCurse) return 7;
        if (it instanceof ScrollOfMirrorImage) return 8;
        if (it instanceof ScrollOfLullaby) return 9;
        if (it instanceof ScrollOfRecharging) return 10;
        if (it instanceof ScrollOfTransmutation) return 11;
        return -1;
    }

    private static void encodeSeeds(int[] out, int off) {
        int[] seed = new int[13];
        Belongings bg = Dungeon.hero.belongings;
        if (bg.backpack != null) {
            for (Item it : bg.backpack) {
                if (it instanceof Plant.Seed) { int s = seedIdx(it); if (s >= 0) seed[s]++; }
            }
        }
        for (int i = 0; i < 13; i++) out[off + i] = seed[i];
    }

    private static int seedIdx(Item it) {
        if (it instanceof Sungrass.Seed) return 0;
        if (it instanceof Earthroot.Seed) return 1;
        if (it instanceof Fadeleaf.Seed) return 2;
        if (it instanceof Blindweed.Seed) return 3;
        if (it instanceof Firebloom.Seed) return 4;
        if (it instanceof Icecap.Seed) return 5;
        if (it instanceof Mageroyal.Seed) return 6;
        if (it instanceof Rotberry.Seed) return 7;
        if (it instanceof Sorrowmoss.Seed) return 8;
        if (it instanceof Starflower.Seed) return 9;
        if (it instanceof Stormvine.Seed) return 10;
        if (it instanceof Swiftthistle.Seed) return 11;
        if (it instanceof BlandfruitBush.Seed) return 12;
        return -1;
    }

    private static void encodeStatus(int[] out, int off) {
        java.util.HashMap<String, Integer> flags = new java.util.HashMap<>();
        int hungerLvl = 0;
        for (Buff b : Dungeon.hero.buffs()) {
            String n = b.getClass().getSimpleName();
            if (n.equals("Hunger")) { hungerLvl = ((Hunger) b).hunger(); continue; }
            flags.put(n, 1);
        }
        String[] names = {
            "Burning", "Poison", "Bleeding", "Frozen", "Paralysis",
            "Vertigo", "Slow", "Charm", "Corrosion", "Weakness",
            "Terror", "Dread", "Bless", "Rejuvenation", "Shield"
        };
        out[off] = hungerLvl;
        for (int i = 0; i < names.length; i++) out[off + 1 + i] = flags.containsKey(names[i]) ? 1 : 0;
    }

    /** 战斗态势段（9 维）：可见敌数/已发现我数/可突袭贴身敌数/最近敌距离/该敌安全距离/我来敌方向数/附近最佳窄地/投掷物剩余次数/投掷物数量。 */
    private static void encodeCombat(int[] out, int off) {
        Level lv = Dungeon.level;
        int pos = Dungeon.hero.pos;
        int vis = 0, aware = 0, surp = 0, minD = Integer.MAX_VALUE, nearSafe = 0;
        if (lv.mobs != null) {
            for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : lv.mobs) {
                if (!m.isAlive() || m.pos < 0 || m.pos >= lv.length()) continue;
                if (!lv.heroFOV[m.pos]) continue;
                vis++;
                if (m.fieldOfView != null && m.fieldOfView.length == lv.length() && m.fieldOfView[pos]) aware++;
                int dd = lv.distance(pos, m.pos);
                if (dd < minD) { minD = dd; nearSafe = (int) Math.ceil(m.speed()) + 1; }
                if (dd <= 1 && m.surprisedBy(Dungeon.hero)) surp++;
            }
        }
        out[off]     = vis;
        out[off + 1] = aware;
        out[off + 2] = surp;
        out[off + 3] = minD == Integer.MAX_VALUE ? -1 : minD;
        out[off + 4] = nearSafe;
        out[off + 5] = RLNav.openDirs(lv, pos);
        boolean[] walk = RLNav.buildWalkable(lv, RLNav.knownMap(lv), pos);
        out[off + 6] = RLCombat.bestFunnelOpen(lv, pos, walk);
        int uses = 0, cnt = 0;
        if (Dungeon.hero.belongings != null && Dungeon.hero.belongings.backpack != null) {
            for (Item it : Dungeon.hero.belongings.backpack) {
                if (it instanceof com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon) {
                    com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon mw =
                            (com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon) it;
                    cnt += it.quantity();
                    try {
                        float per = mw.durabilityPerUse();
                        uses += per <= 0f ? 99 : (int) Math.ceil(mw.durabilityLeft() / per);
                    } catch (Throwable t) { uses += 1; }
                }
            }
        }
        out[off + 7] = uses;
        out[off + 8] = cnt;
    }

    private static int itemCategory(Item it) {
        if (it instanceof Potion) return 1;
        if (it instanceof Scroll) return 2;
        if (it instanceof Food) return 3;
        if (it instanceof Weapon) return 4;
        if (it instanceof Armor) return 5;
        if (it instanceof Ring) return 6;
        if (it instanceof Artifact) return 7;
        if (it instanceof Wand) return 8;
        if (it instanceof Key) return 9;
        if (it instanceof Bomb) return 10;
        if (it instanceof Runestone) return 11;
        if (it instanceof Dewdrop) return 12;
        if (it instanceof Gold) return 13;
        return 0;
    }

    public int getLastReward() { return lastReward; }
    public boolean isDone() { return done; }
    public int getDepth() { return Dungeon.depth; }
    public int getHeroPos() { return Dungeon.hero.pos; }
}
