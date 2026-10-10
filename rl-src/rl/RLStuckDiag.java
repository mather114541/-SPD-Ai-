package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.PathFinder;

public class RLStuckDiag implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLStuckDiag()); }
    @Override public void render() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    @Override
    public void create() {
        RLController c = new RLController();
        c.reset(9999L);
        Level lv = Dungeon.level;
        for (int s = 0; s < 80; s++) {
            Hero h = Dungeon.hero;
            int a = RLExpert.policy();
            StringBuilder sb = new StringBuilder("s=" + s + " a=" + a + " pos=" + h.pos
                + " map=" + lv.map[h.pos] + " visited=" + vis() + " hhp=" + h.HP);
            for (int d = 0; d < 8; d++) {
                int cell = h.pos + PathFinder.NEIGHBOURS8[d];
                if (cell < 0 || cell >= lv.length()) continue;
                sb.append(" [").append(d).append("]m=").append(lv.map[cell]).append("p=").append(lv.passable[cell] ? "1" : "0").append("v=").append((lv.visited[cell] || lv.mapped[cell]) ? "1" : "0");
                Char ch = Actor.findChar(cell);
                if (ch != null) sb.append("ch=").append(ch.getClass().getSimpleName()).append("hp=").append(ch.HP);
            }
            System.err.println(sb);
            c.step(a);
            if (!h.isAlive()) break;
        }
        System.exit(0);
    }

    private static int vis() {
        int n = 0;
        for (int i = 0; i < Dungeon.level.length(); i++) if (Dungeon.level.visited[i] || Dungeon.level.mapped[i]) n++;
        return n;
    }
}
