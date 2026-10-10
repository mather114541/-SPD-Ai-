package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;

/** 冒烟验证：专家是否主动搜索可疑点、能否发现隐藏门/陷阱、换层是否仍正常。须走 HeadlessApplication。 */
public class SearchSmoke implements ApplicationListener {

    private static String[] ARGS;

    @Override public void create() { run(); }
    @Override public void resize(int width, int height) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    static int countSecret() {
        Level lv = Dungeon.level;
        if (lv == null) return 0;
        int c = 0;
        for (int i = 0; i < lv.length(); i++) {
            int m = lv.map[i];
            if (m == Terrain.SECRET_DOOR || m == Terrain.SECRET_TRAP) c++;
        }
        return c;
    }

    void run() {
        int n = ARGS.length > 0 ? Integer.parseInt(ARGS[0]) : 12;
        int seedbase = ARGS.length > 1 ? Integer.parseInt(ARGS[1]) : 42;
        RLController env = new RLController();
        int totalFound = 0, searches = 0, effSearch = 0, foundEps = 0, reach2 = 0, maxDepth = 0;
        int totalSpot = 0, nSteps = 0;
        for (int ep = 0; ep < n; ep++) {
            env.reset(seedbase + ep * 1000L);
            int depth = 1, foundThis = 0, steps = 0;
            while (steps < 2500) {
                int before = countSecret();
                boolean[] spNow = RLNav.suspiciousSpotMask(Dungeon.level, RLNav.knownMap(Dungeon.level));
                for (int t = 0; t < spNow.length; t++) if (spNow[t]) totalSpot++;
                nSteps++;
                int macro = RLExpert.policy();
                boolean isSearch = (macro == RLExpert.M_SEARCH);
                if (isSearch) searches++;
                env.step(macro);
                int after = countSecret();
                int f = before - after;
                if (f > 0) { foundThis += f; totalFound += f; if (isSearch) effSearch++; }
                int d = Dungeon.depth;
                if (d > depth) depth = d;
                steps++;
                if (!Dungeon.hero.isAlive()) break;
            }
            if (foundThis > 0) foundEps++;
            if (depth >= 2) reach2++;
            if (depth > maxDepth) maxDepth = depth;
            System.out.println("[ep " + ep + "] seed=" + (seedbase + ep * 1000L)
                    + " finalDepth=" + depth + " secretsFound=" + foundThis + " steps=" + steps);
        }
        int actualSearch = RLController.searchDone;
        double avgSpot = nSteps > 0 ? (double) totalSpot / nSteps : 0.0;
        double hitRate = actualSearch > 0 ? 100.0 * effSearch / actualSearch : 0.0;
        double hitRateByMacro = searches > 0 ? 100.0 * effSearch / searches : 0.0;
        System.out.println("=== totalSecrets=" + totalFound + " epsWithSecrets=" + foundEps + "/" + n
                + " M_SEARCH次数=" + searches + " 实际搜索=" + actualSearch + " 有效搜索=" + effSearch
                + " 平均可疑点=" + String.format("%.1f", avgSpot) + " 实际命中率=" + String.format("%.1f", hitRate) + "%"
                + " (按宏指令=" + String.format("%.1f", hitRateByMacro) + "%)"
                + " reach2=" + reach2 + "/" + n + " maxDepth=" + maxDepth);
        System.exit(0);
    }

    public static void main(String[] a) {
        ARGS = a;
        new HeadlessApplication(new SearchSmoke());
    }
}
