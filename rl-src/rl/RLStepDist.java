package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Arrays;
import java.util.Random;

public class RLStepDist implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLStepDist()); }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 5;
        Random rng = new Random(42);
        int N = 30;
        int[] stepsArr = new int[N];
        int[] depArr = new int[N];
        int stuckCnt = 0;
        long[] hist = new long[9];
        String[] names = {"EXPLORE","EXIT","FIGHT","HEAL","EAT","SEARCH","PICKUP","WAIT","THROW"};
        for (int e = 0; e < N; e++) {
            c.reset(rng.nextLong());
            int st = 0, lastPos = c.getHeroPos(), same = 0;
            while (st < 3000) {
                if (c.isDone()) break;
                int a = RLExpert.policy(); hist[a]++;
                try { c.step(a); } catch (Throwable t) { break; }
                st++;
                int p = c.getHeroPos();
                if (p == lastPos) same++; else { lastPos = p; same = 0; }
                if (same > 150) { stuckCnt++; break; }
            }
            stepsArr[e] = st; depArr[e] = c.getDepth();
        }
        int[] sorted = stepsArr.clone(); Arrays.sort(sorted);
        long total = 0; for (int v : stepsArr) total += v;
        System.out.println("STEPDIST mean=" + (total / N) + " median=" + sorted[N/2] + " p90=" + sorted[(int)(N*0.9)]
                + " max=" + sorted[N-1] + " min=" + sorted[0] + " stuckBreaks=" + stuckCnt);
        StringBuilder sb = new StringBuilder("STEPDIST depths=");
        for (int i = 0; i < N; i++) sb.append(depArr[i]).append(i<N-1?",":"");
        System.out.println(sb.toString());
        StringBuilder hb = new StringBuilder("STEPDIST macros=");
        for (int i = 0; i < 9; i++) if (hist[i] > 0) hb.append(names[i]).append("=").append(hist[i]).append(String.format("(%.0f%%) ", 100.0*hist[i]/total));
        System.out.println(hb.toString());
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
