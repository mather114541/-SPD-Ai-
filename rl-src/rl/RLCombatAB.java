package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

/** 站位（standoff）技巧 A/B：同批种子，朴素近战 vs 按怪速度维持安全距离抢先后手。 */
public class RLCombatAB implements ApplicationListener {
    private final int episodes, seedBase, target, maxSteps;

    public static void main(String[] a) {
        int ep = a.length > 0 ? Integer.parseInt(a[0]) : 40;
        int sb = a.length > 1 ? Integer.parseInt(a[1]) : 777;
        int tg = a.length > 2 ? Integer.parseInt(a[2]) : 4;
        int mx = a.length > 3 ? Integer.parseInt(a[3]) : 2500;
        new HeadlessApplication(new RLCombatAB(ep, sb, tg, mx));
    }

    public RLCombatAB(int ep, int sb, int tg, int mx) { episodes = ep; seedBase = sb; target = tg; maxSteps = mx; }

    @Override public void create() {
        Object[][] cfgs = {
            {"A_naive_melee  ", false},
            {"B_standoff     ", true },
        };
        for (Object[] cfg : cfgs) {
            String name = (String) cfg[0];
            RLCombat.standoffEnabled = (Boolean) cfg[1];
            RLCombat.resetCounters();
            RLController c = new RLController();
            c.targetDepth = target;
            Random rng = new Random(seedBase);
            int deaths = 0, ge2 = 0, ge3 = 0, ge4 = 0;
            long dmg = 0, steps = 0, hpLeft = 0;
            for (int e = 0; e < episodes; e++) {
                c.reset(rng.nextLong());
                int st = 0, lastPos = c.getHeroPos(), same = 0;
                while (st < maxSteps) {
                    if (c.isDone()) break;
                    int a = RLExpert.policy();
                    int prevHP = Dungeon.hero.HP;
                    c.step(a);
                    st++;
                    int nowHP = Dungeon.hero.HP;
                    if (nowHP < prevHP) dmg += (prevHP - nowHP);
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 120) break;
                }
                int d = c.getDepth();
                if (d >= 2) ge2++;
                if (d >= 3) ge3++;
                if (d >= 4) ge4++;
                if (!Dungeon.hero.isAlive()) deaths++;
                hpLeft += Math.max(0, Dungeon.hero.HP);
                steps += st;
            }
            System.out.println("AB " + name + " ep=" + episodes + " target=" + target
                    + " | deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4
                    + " | totalDMG=" + dmg + " avgDMG/ep=" + String.format("%.1f", dmg / (double) episodes)
                    + " avgHPleft=" + String.format("%.1f", hpLeft / (double) episodes)
                    + " holdSlow=" + RLCombat.holdSlow + " holdFast=" + RLCombat.holdFast
                    + " avgSteps=" + String.format("%.1f", steps / (double) episodes));
        }
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
