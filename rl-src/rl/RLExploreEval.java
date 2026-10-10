package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import java.util.Random;

/** 专家探索覆盖率评测：fullExplore 开/关对比。覆盖率 = 已探可走格 / 该层可走格总数。 */
public class RLExploreEval implements ApplicationListener {
    private final int episodes, seedBase, maxSteps, target;
    public static void main(String[] a) {
        int ep = a.length > 0 ? Integer.parseInt(a[0]) : 30;
        int sb = a.length > 1 ? Integer.parseInt(a[1]) : 42;
        int mx = a.length > 2 ? Integer.parseInt(a[2]) : 2500;
        int tg = a.length > 3 ? Integer.parseInt(a[3]) : 5;
        new HeadlessApplication(new RLExploreEval(ep, sb, mx, tg));
    }
    public RLExploreEval(int ep, int sb, int mx, int tg) { episodes = ep; seedBase = sb; maxSteps = mx; target = tg; }

    private static double cov(Level lv) {
        int kp = 0, tp = 0;
        for (int i = 0; i < lv.length(); i++) {
            if (lv.passable[i]) { tp++; if (lv.visited[i] || lv.mapped[i]) kp++; }
        }
        return tp == 0 ? 0 : (double) kp / tp;
    }

    private int errCount = 0;

    @Override public void create() {
        for (boolean fe : new boolean[]{false, true}) {
            RLExpert.fullExplore = fe;
            RLController c = new RLController();
            c.targetDepth = target;
            Random rng = new Random(seedBase);
            double[] sum = new double[16];
            int[] cnt = new int[16];
            int deaths = 0, ge2 = 0, ge3 = 0, ge4 = 0;
            long steps = 0;
            for (int e = 0; e < episodes; e++) {
                c.reset(rng.nextLong());
                int st = 0, lastPos = c.getHeroPos(), same = 0;
                double lastCov = cov(Dungeon.level);
                int prevDepth = Dungeon.depth;
                while (st < maxSteps) {
                    if (c.isDone()) break;
                    try {
                        c.step(RLExpert.policy());
                    } catch (Throwable t) {
                        if (errCount++ < 2) {
                            System.err.println("[EXPL] step err: " + t);
                            StackTraceElement[] stt = t.getStackTrace();
                            for (int i = 0; i < Math.min(8, stt.length); i++) System.err.println("    at " + stt[i]);
                        }
                        break;
                    }
                    st++;
                    if (Dungeon.depth > prevDepth) {
                        sum[prevDepth] += lastCov; cnt[prevDepth]++;
                        prevDepth = Dungeon.depth;
                    }
                    lastCov = cov(Dungeon.level);
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 150) break;
                }
                if (fe && Dungeon.depth >= 2 && Dungeon.hero.isAlive()) {
                    Level lv2 = Dungeon.level;
                    boolean[] kn = RLNav.knownMap(lv2);
                    boolean[] wk = RLNav.buildWalkable(lv2, kn, Dungeon.hero.pos);
                    boolean[] fr2 = RLNav.frontierMap(lv2, kn);
                    int nf = 0; for (boolean b : fr2) if (b) nf++;
                    boolean frR = RLNav.firstStep(lv2, wk, Dungeon.hero.pos, fr2) >= 0;
                    boolean[] sp2 = RLNav.suspiciousSpotMask(lv2, kn);
                    boolean spR = RLNav.firstStep(lv2, wk, Dungeon.hero.pos, sp2) >= 0;
                    int exK = 0;
                    for (int i = 0; i < lv2.length(); i++) if ((lv2.visited[i] || lv2.mapped[i]) && lv2.map[i] == 8) exK++;
                    System.out.println("[why] depth=" + Dungeon.depth + " steps=" + st + " cov=" + String.format("%.3f", cov(lv2))
                            + " frN=" + nf + " frReach=" + frR + " spotReach=" + spR + " exitKnown=" + exK);
                }
                int d = c.getDepth();
                if (d >= 2) ge2++;
                if (d >= 3) ge3++;
                if (d >= 4) ge4++;
                if (!Dungeon.hero.isAlive()) deaths++;
                steps += st;
            }
            StringBuilder sb = new StringBuilder();
            for (int d = 1; d < 16; d++) if (cnt[d] > 0) sb.append("f").append(d).append("=").append(String.format("%.3f", sum[d] / cnt[d])).append("(").append(cnt[d]).append(") ");
            System.out.println("EXPLORE fullExplore=" + fe + " ep=" + episodes + " | 下楼时覆盖率: " + sb.toString().trim()
                    + " | deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4 + " avgSteps=" + (steps / episodes));
        }
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
