package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

public class RLTechAB implements ApplicationListener {
    private final int episodes, seedBase, target, maxSteps;
    public static void main(String[] a) {
        int ep = a.length > 0 ? Integer.parseInt(a[0]) : 40;
        int sb = a.length > 1 ? Integer.parseInt(a[1]) : 42;
        new HeadlessApplication(new RLTechAB(ep, sb, a.length > 2 ? Integer.parseInt(a[2]) : 5, 2500));
    }
    public RLTechAB(int ep, int sb, int tg, int mx) { episodes = ep; seedBase = sb; target = tg; maxSteps = mx; }

    @Override public void create() {
        // {名称, engageRange, funnelEnabled, throwEnabled}
        Object[][] cfgs = {
            {"PASSIVE_throw        ", 0, false, true },
            {"ENG2_throw_nofunnel  ", 2, false, true },
            {"ENG2_throw_funnel    ", 2, true,  true },
            {"ENG3_throw_nofunnel  ", 3, false, true },
        };
        for (Object[] cfg : cfgs) {
            int er = (Integer) cfg[1];
            RLExpert.engageNearby = er > 0;
            RLCombat.engageRange = er > 0 ? er : 0;
            RLCombat.funnelEnabled = (Boolean) cfg[2];
            RLExpert.throwEnabled = (Boolean) cfg[3];
            RLCombat.resetCounters();
            RLController c = new RLController();
            c.targetDepth = target;
            Random rng = new Random(seedBase);
            int deaths = 0, ge2 = 0, ge3 = 0, ge4 = 0;
            long steps = 0;
            for (int e = 0; e < episodes; e++) {
                c.reset(rng.nextLong());
                int st = 0, lastPos = c.getHeroPos(), same = 0;
                while (st < maxSteps) {
                    if (c.isDone()) break;
                    c.step(RLExpert.policy());
                    st++;
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 120) break;
                }
                int d = c.getDepth();
                if (d >= 2) ge2++;
                if (d >= 3) ge3++;
                if (d >= 4) ge4++;
                if (!Dungeon.hero.isAlive()) deaths++;
                steps += st;
            }
            System.out.println("AB " + (String) cfg[0] + " deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4
                    + " throws=" + RLCombat.throwsDone + " funnels=" + RLCombat.funnelMoves + " avgSteps=" + (steps / episodes));
        }
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
