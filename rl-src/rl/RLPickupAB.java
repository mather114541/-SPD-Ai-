package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import java.util.Random;

/** 拾取 A/B。 */
public class RLPickupAB implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLPickupAB()); }

    private static int bagCount() {
        int n = 0;
        if (Dungeon.hero != null && Dungeon.hero.belongings != null && Dungeon.hero.belongings.backpack != null)
            for (Item it : Dungeon.hero.belongings.backpack) n += it.quantity();
        return n;
    }

    @Override public void create() {
        for (boolean pk : new boolean[]{false, true}) {
            RLExpert.pickupEnabled = pk;
            RLController c = new RLController();
            c.targetDepth = 5;
            Random rng = new Random(42);
            int deaths = 0, ge2 = 0, ge3 = 0, ge4 = 0, picks = 0;
            long steps = 0;
            for (int e = 0; e < 30; e++) {
                c.reset(rng.nextLong());
                int prev = bagCount(), st = 0, lastPos = c.getHeroPos(), same = 0;
                while (st < 3000) {
                    if (c.isDone()) break;
                    try { c.step(RLExpert.policy()); } catch (Throwable t) { break; }
                    st++;
                    int cur = bagCount();
                    if (cur > prev) picks += cur - prev;
                    prev = cur;
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 150) break;
                }
                int dep = c.getDepth();
                if (dep >= 2) ge2++;
                if (dep >= 3) ge3++;
                if (dep >= 4) ge4++;
                if (!Dungeon.hero.isAlive()) deaths++;
                steps += st;
            }
            System.out.println("AB pickup=" + pk + " deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4
                    + " picks=" + picks + " throws=" + RLCombat.throwsDone + " avgSteps=" + (steps / 30));
        }
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
