package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import java.util.Random;

/** 探索目标覆盖率 A/B：0.78（豆包默认） vs 0.99（尽量全探完）。 */
public class RLExploreTargetAB implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLExploreTargetAB()); }

    private static double cov(Level lv) {
        int kp = 0, tp = 0;
        for (int i = 0; i < lv.length(); i++) if (lv.passable[i]) { tp++; if (lv.visited[i] || lv.mapped[i]) kp++; }
        return tp == 0 ? 0 : (double) kp / tp;
    }

    @Override public void create() {
        for (double et : new double[]{0.78, 0.99}) {
            RLExpert.exploreTarget = et;
            RLController c = new RLController();
            c.targetDepth = 5;
            Random rng = new Random(42);
            double[] sum = new double[16];
            int[] cnt = new int[16];
            int deaths = 0, ge2 = 0, ge3 = 0, ge4 = 0;
            long steps = 0;
            for (int e = 0; e < 30; e++) {
                c.reset(rng.nextLong());
                int st = 0, lastPos = c.getHeroPos(), same = 0;
                double lastCov = cov(Dungeon.level);
                int prevDepth = Dungeon.depth;
                while (st < 3000) {
                    if (c.isDone()) break;
                    try { c.step(RLExpert.policy()); } catch (Throwable t) { break; }
                    st++;
                    if (Dungeon.depth > prevDepth) { sum[prevDepth] += lastCov; cnt[prevDepth]++; prevDepth = Dungeon.depth; }
                    lastCov = cov(Dungeon.level);
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 150) break;
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
            System.out.println("AB exploreTarget=" + et + " | 下楼覆盖率: " + sb.toString().trim()
                    + " | deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4 + " avgSteps=" + (steps / 30));
        }
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
