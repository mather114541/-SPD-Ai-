package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;

/** 定位种子段重复计数问题：打印种子袋与背包实际内容，对照 SEED 段。 */
public class RLSeedCheck implements ApplicationListener {
    public static void main(String[] args) {
        new HeadlessApplication(new RLSeedCheck());
    }
    @Override public void create() {
        try {
            RLController env = new RLController();
            env.reset(999L);
            Bag pouch = null;
            for (Item it : Dungeon.hero.belongings.backpack) {
                if (it instanceof VelvetPouch) { pouch = (Bag) it; break; }
            }
            System.out.println("pouch found=" + (pouch != null));
            new Sungrass.Seed().collect(pouch);
            new Earthroot.Seed().collect(Dungeon.hero.belongings.backpack);
            new Sungrass.Seed().collect(Dungeon.hero.belongings.backpack); // 第二个阳春草直接放背包

            System.out.print("POUCH: ");
            for (Item it : pouch) System.out.print(shortName(it) + "|");
            System.out.println("  size=" + pouch.items.size());
            System.out.print("BACKPACK: ");
            for (Item it : Dungeon.hero.belongings.backpack) System.out.print(shortName(it) + "|");
            System.out.println("  size=" + Dungeon.hero.belongings.backpack.items.size());

            int[] o = env.step(8);
            int soff = RLController.OFF_SEED;
            StringBuilder ss = new StringBuilder("SEED=[");
            for (int i = 0; i < RLController.N_SEED; i++) { ss.append(o[soff + i]); if (i < RLController.N_SEED - 1) ss.append(','); }
            ss.append("]");
            System.out.println(ss);
        } catch (Throwable t) { t.printStackTrace(); }
        com.badlogic.gdx.Gdx.app.exit();
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    private static String shortName(Item it) {
        String n = it.getClass().getName();
        int i = n.lastIndexOf('.');
        return i >= 0 ? n.substring(i + 1) : n;
    }
}
