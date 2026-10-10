package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import java.util.Random;

/** 检查：专家在实战里到底会不会捡东西（背包物品数正向增量），以及投掷物的下落。 */
public class RLPickupCheck implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLPickupCheck()); }

    private static int bagCount() {
        int n = 0;
        if (Dungeon.hero != null && Dungeon.hero.belongings != null && Dungeon.hero.belongings.backpack != null) {
            for (Item it : Dungeon.hero.belongings.backpack) n += it.quantity();
        }
        return n;
    }

    private static int missileUses() {
        int u = 0;
        if (Dungeon.hero == null) return 0;
        for (Item it : Dungeon.hero.belongings.backpack) {
            if (it instanceof MissileWeapon) {
                MissileWeapon mw = (MissileWeapon) it;
                try { float per = mw.durabilityPerUse(); u += per <= 0 ? 99 : (int) Math.ceil(mw.durabilityLeft() / per); } catch (Throwable t) { u += 1; }
            }
        }
        return u;
    }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 5;
        Random rng = new Random(5150);
        int totalPickups = 0, totalThrows = 0, maxItems = 0;
        for (int e = 0; e < 4; e++) {
            c.reset(rng.nextLong());
            int prev = bagCount();
            int startUses = missileUses();
            int st = 0;
            while (st < 1200 && !c.isDone()) {
                c.step(RLExpert.policy());
                st++;
                int cur = bagCount();
                if (cur > prev) totalPickups += (cur - prev);
                prev = cur;
                maxItems = Math.max(maxItems, cur);
            }
            System.out.println("[ep " + e + "] depth=" + c.getDepth() + " steps=" + st
                    + " bag=" + bagCount() + " missileUses(start->end)=" + startUses + "->" + missileUses());
        }
        System.out.println("PICKUP totalIncidentalPickups=" + totalPickups + " throws=" + RLCombat.throwsDone + " maxBag=" + maxItems);
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
