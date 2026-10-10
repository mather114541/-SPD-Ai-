package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

/** 按宏统计步数分布，看探索时间到底花在哪。 */
public class RLMacroHist implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLMacroHist()); }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 5;
        Random rng = new Random(42);
        String[] names = {"EXPLORE", "EXIT", "FIGHT", "HEAL", "EAT", "SEARCH", "PICKUP", "WAIT", "THROW"};
        long[] hist = new long[9];
        long steps = 0;
        for (int e = 0; e < 6; e++) {
            c.reset(rng.nextLong());
            int st = 0, lastPos = c.getHeroPos(), same = 0;
            while (st < 2000) {
                if (c.isDone()) break;
                int a = RLExpert.policy();
                hist[a]++;
                try { c.step(a); } catch (Throwable t) { break; }
                st++;
                int p = c.getHeroPos();
                if (p == lastPos) same++; else { lastPos = p; same = 0; }
                if (same > 150) break;
            }
            steps += st;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) if (hist[i] > 0) sb.append(names[i]).append("=").append(hist[i]).append(String.format("(%.0f%%) ", 100.0 * hist[i] / steps));
        System.out.println("HIST totalSteps=" + steps + " | " + sb.toString().trim());
        System.out.println("HIST avgSteps/ep=" + (steps / 6));
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
