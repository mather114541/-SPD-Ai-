package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

/** 验证搜索率：每层实际执行了几次 h.search(true)，多少层需要搜。 */
public class RLSearchRate implements ApplicationListener {
    public static void main(String[] a) {
        int n = a.length > 0 ? Integer.parseInt(a[0]) : 40;
        int sb = a.length > 1 ? Integer.parseInt(a[1]) : 1;
        new HeadlessApplication(new RLSearchRate(n, sb));
    }
    private final int N, seedBase;
    public RLSearchRate(int n, int sb) { N = n; seedBase = sb; }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 5;
        Random rng = new Random(seedBase);
        int floors = 0, floorsWithSearch = 0, floorsWithSpot = 0, totalSearches = 0, maxPerFloor = 0;
        for (int e = 0; e < N; e++) {
            c.reset(rng.nextLong());
            int prevDepth = Dungeon.depth;
            int prevS = RLController.searchDone;
            boolean spotSeen = false;
            int st = 0;
            while (st < 2500) {
                if (c.isDone()) break;
                boolean[] spot = RLNav.suspiciousSpotMask(Dungeon.level, RLNav.knownMap(Dungeon.level));
                boolean any = false; for (boolean b : spot) if (b) { any = true; break; }
                if (any) spotSeen = true;
                try { c.step(RLExpert.policy()); } catch (Throwable t) { break; }
                st++;
                if (Dungeon.depth > prevDepth) {
                    int s = RLController.searchDone - prevS;
                    floors++; if (s > 0) floorsWithSearch++; if (spotSeen) floorsWithSpot++;
                    maxPerFloor = Math.max(maxPerFloor, s); totalSearches += s;
                    prevDepth = Dungeon.depth; prevS = RLController.searchDone; spotSeen = false;
                }
            }
            int s = RLController.searchDone - prevS;
            floors++; if (s > 0) floorsWithSearch++; if (spotSeen) floorsWithSpot++;
            maxPerFloor = Math.max(maxPerFloor, s); totalSearches += s;
        }
        System.out.println("SEARCHRATE seed=" + seedBase + " floors=" + floors
                + " floorsWithSearch=" + floorsWithSearch + String.format(" (%.0f%%)", 100.0 * floorsWithSearch / Math.max(1, floors))
                + " floorsWithAnySpot=" + floorsWithSpot
                + " totalSearches=" + totalSearches + String.format(" perFloor=%.2f maxPerFloor=%d", (double) totalSearches / Math.max(1, floors), maxPerFloor));
        System.exit(0);
    }
    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
