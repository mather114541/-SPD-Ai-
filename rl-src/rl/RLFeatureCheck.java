package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;

/** 验证具体物品识别段：已鉴定药水/卷轴应进入对应槽位，未鉴定应计入"未知"。 */
public class RLFeatureCheck implements ApplicationListener {

    public static void main(String[] args) {
        new HeadlessApplication(new RLFeatureCheck());
    }

    @Override public void create() {
        try {
            RLController env = new RLController();
            env.reset(999L);

            // 已鉴定：治疗药水、升级卷轴 → 应分别进入 pot[0]、scr[0]
            PotionOfHealing ph = new PotionOfHealing();
            ph.identify();
            ph.collect(Dungeon.hero.belongings.backpack);
            ScrollOfUpgrade su = new ScrollOfUpgrade();
            su.identify();
            su.collect(Dungeon.hero.belongings.backpack);
            // 未鉴定：力量药水、鉴定卷轴 → 应计入 unknownPotion / unknownScroll
            new PotionOfStrength().collect(Dungeon.hero.belongings.backpack);
            new ScrollOfIdentify().collect(Dungeon.hero.belongings.backpack);

            int[] o = env.step(8);
            int off = RLController.OFF_ITEMD;
            StringBuilder sb = new StringBuilder("ITEMD=[");
            for (int i = 0; i < RLController.N_ITEM_DETAILS; i++) {
                sb.append(o[off + i]);
                if (i < RLController.N_ITEM_DETAILS - 1) sb.append(',');
            }
            sb.append("]  (0-11药水,12-23卷轴,24未知药水,25未知卷轴)");
            System.out.println(sb);

            // ---- 种子测试 ----
            System.out.print("BACKPACK: ");
            for (Item it : Dungeon.hero.belongings.backpack) System.out.print(it.getClass().getSimpleName() + " ");
            System.out.println();
            Bag pouch = null;
            for (Item it : Dungeon.hero.belongings.backpack) {
                if (it instanceof VelvetPouch) { pouch = (Bag) it; break; }
            }
            // 阳春草放种子袋，护根直接放背包
            new Sungrass.Seed().collect(pouch != null ? pouch : Dungeon.hero.belongings.backpack);
            new Earthroot.Seed().collect(Dungeon.hero.belongings.backpack);
            int[] o2 = env.step(8);
            int soff = RLController.OFF_SEED;
            StringBuilder ss = new StringBuilder("SEED=[");
            for (int i = 0; i < RLController.N_SEED; i++) {
                ss.append(o2[soff + i]);
                if (i < RLController.N_SEED - 1) ss.append(',');
            }
            ss.append("]  (0阳春草,1护根,2传送草,3盲草,...)");
            System.out.println(ss);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        com.badlogic.gdx.Gdx.app.exit();
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
