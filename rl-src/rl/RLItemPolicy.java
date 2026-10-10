package rl;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;

/**
 * 用药动作执行器（供 RL 环境调用，供神经网络决策是否用药）。
 *
 * 关键设计：**环境不做任何自动用药**。喝治疗药水 / 喝力量药水 / 读升级卷轴
 * 均作为离散动作加入动作空间，由神经网络通过试错学习"何时用、值不值得用"。
 * 用药前必须持有对应物品且已鉴定（信息与玩家同权，未鉴定不碰）；
 * 物品缺失/未鉴定时动作失效：不消耗物品、不推进，等效浪费一回合等待，
 * 从而让 RL 学会不要选择无效的用药动作。
 *
 * 动作码与 RLController.step 约定一致：
 *   9 = 喝已鉴定治疗药水   10 = 喝已鉴定力量药水   11 = 读已鉴定升级卷轴(升级当前武器)
 */
public final class RLItemPolicy {

    public static final int A_DRINK_HEAL     = 9;
    public static final int A_DRINK_STRENGTH = 10;
    public static final int A_UPGRADE_WEAPON = 11;
    /** 13 = 吃食物（饥饿管理，由网络决策何时吃）。 */
    public static final int A_EAT_FOOD = 13;
    /** 扩展后总动作数：0-8 移动 + 9-11 用药 + 12 搜索 + 13 吃饭。 */
    public static final int N_ACTIONS = 14;

    private RLItemPolicy() {}

    /** 执行单个用药动作。返回 true 表示用药成功并占用回合；false 表示动作失效（无对应已鉴定物品）。 */
    public static boolean applyAction(Hero h, int action) {
        if (h == null || !h.isAlive()) return false;
        switch (action) {
            case A_DRINK_HEAL: {
                PotionOfHealing ph = findIdentified(h, PotionOfHealing.class);
                if (ph == null) return false;
                ph.execute(h, Potion.AC_DRINK);
                return true;
            }
            case A_DRINK_STRENGTH: {
                PotionOfStrength ps = findIdentified(h, PotionOfStrength.class);
                if (ps == null) return false;
                ps.execute(h, Potion.AC_DRINK);
                return true;
            }
            case A_UPGRADE_WEAPON: {
                Object wObj = h.belongings.weapon;
                if (!(wObj instanceof Weapon)) return false;
                ScrollOfUpgrade su = findIdentified(h, ScrollOfUpgrade.class);
                if (su == null) return false;
                su.upgradeItem((Weapon) wObj);
                su.detach(h.belongings.backpack);
                return true;
            }
            case A_EAT_FOOD: {
                Food f = findIdentified(h, Food.class);
                if (f == null) return false;
                f.execute(h, Food.AC_EAT);
                return true;
            }
            default:
                return false;
        }
    }

    /** 在背包中查找第一个已鉴定的指定类型物品（背包迭代器会扁平展开子袋）。 */
    @SuppressWarnings("unchecked")
    private static <T extends Item> T findIdentified(Hero h, Class<T> type) {
        for (Item it : h.belongings.backpack) {
            if (type.isInstance(it) && it.isIdentified()) return (T) it;
        }
        return null;
    }
}
