package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.PathFinder;
import java.util.ArrayDeque;

public class RLDiag2 implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLDiag2()); }
    @Override public void render() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    @Override
    public void create() {
        RLController c = new RLController();
        c.reset(1000007L);
        Level lv = Dungeon.level;
        for (int s = 0; s < 800; s++) {
            int a = RLExpert.policy();
            c.step(a);
            if (Dungeon.depth > 1) { System.err.println("REACHED_DEPTH2 at s=" + s); System.exit(0); }
            if (s % 50 == 0) System.err.println("s=" + s + " pos=" + Dungeon.hero.pos + " vis=" + vis());
        }
        lv = Dungeon.level;
        int pass = 0, hidden = 0, exitPos = -1;
        for (int i = 0; i < lv.length(); i++) {
            if (lv.passable[i]) pass++;
            if (lv.map[i] == 16) hidden++;
            if (lv.map[i] == 8) exitPos = i;
        }
        // BFS 从英雄出发，走 passable 格
        boolean[] seen = new boolean[lv.length()];
        ArrayDeque<Integer> q = new ArrayDeque<>();
        seen[Dungeon.hero.pos] = true; q.add(Dungeon.hero.pos);
        int conn = 0, connVisited = 0, exitReach = -1;
        while (!q.isEmpty()) {
            int cur = q.poll(); conn++;
            if (lv.visited[cur] || lv.mapped[cur]) connVisited++;
            for (int d = 0; d < 4; d++) {
                int n = cur + PathFinder.NEIGHBOURS4[d];
                if (n < 0 || n >= lv.length()) continue;
                int cx = cur % lv.width(), cy = cur / lv.width(), nx = n % lv.width(), ny = n / lv.width();
                if (Math.abs(cx - nx) + Math.abs(cy - ny) != 1) continue;
                if (!lv.passable[n] || seen[n]) continue;
                if (lv.map[n] == 8 && exitReach == -1) exitReach = n;
                seen[n] = true; q.add(n);
            }
        }
        System.err.println("FINAL vis=" + vis() + " pass=" + pass + " conn=" + conn
            + " connVisited=" + connVisited + " exitPos=" + exitPos + " exitReach=" + exitReach
            + " hiddenDoors=" + hidden);
        System.exit(0);
    }

    private static int vis() {
        int n = 0;
        for (int i = 0; i < Dungeon.level.length(); i++) if (Dungeon.level.visited[i] || Dungeon.level.mapped[i]) n++;
        return n;
    }
}
