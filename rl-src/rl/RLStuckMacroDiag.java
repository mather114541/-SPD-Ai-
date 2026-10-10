package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

/** 统计"原地不动"时执行的宏，定位卡死真凶。 */
public class RLStuckMacroDiag implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLStuckMacroDiag()); }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 5;
        Random rng = new Random(42);
        String[] names = {"EXPLORE","EXIT","FIGHT","HEAL","EAT","SEARCH","PICKUP","WAIT","THROW"};
        long[] stallMacro = new long[9];
        long stallSteps = 0, allSteps = 0;
        for (int e = 0; e < 20; e++) {
            c.reset(rng.nextLong());
            int st = 0, lastPos = c.getHeroPos(), same = 0;
            while (st < 3000) {
                if (c.isDone()) break;
                int a = RLExpert.policy();
                try { c.step(a); } catch (Throwable t) { break; }
                st++;
                int p = c.getHeroPos();
                if (p == lastPos) {
                    same++;
                    if (same > 3) { stallMacro[a]++; stallSteps++; }   // 连续不动 4 回合以上算"卡"
                } else { lastPos = p; same = 0; }
                if (same > 150) break;
            }
            allSteps += st;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) if (stallMacro[i] > 0) sb.append(names[i]).append("=").append(stallMacro[i]).append(" ");
        System.out.println("STUCKDIAG allSteps=" + allSteps + " stallSteps=" + stallSteps + " | 卡住时的宏: " + sb.toString().trim());
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
