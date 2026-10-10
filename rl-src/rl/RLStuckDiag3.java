package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import java.util.Random;

public class RLStuckDiag3 implements ApplicationListener {
    private final int episodes, seedBase, maxSteps, target;

    public static void main(String[] a) {
        int ep = a.length > 0 ? Integer.parseInt(a[0]) : 20;
        int sb = a.length > 1 ? Integer.parseInt(a[1]) : 42;
        int mx = a.length > 2 ? Integer.parseInt(a[2]) : 2500;
        int tg = a.length > 3 ? Integer.parseInt(a[3]) : 5;
        new HeadlessApplication(new RLStuckDiag3(ep, sb, mx, tg));
    }

    public RLStuckDiag3(int ep, int sb, int mx, int tg) { episodes = ep; seedBase = sb; maxSteps = mx; target = tg; }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = target;
        Random rng = new Random(seedBase);
        int reported = 0, stuckTotal = 0;
        for (int e = 0; e < episodes; e++) {
            long seed = rng.nextLong();
            c.reset(seed);
            int steps = 0, lastPos = c.getHeroPos(), same = 0;
            while (steps < maxSteps) {
                if (c.isDone()) break;
                int a = RLExpert.policy();
                c.step(a);
                steps++;
                int p = c.getHeroPos();
                if (p == lastPos) same++; else { lastPos = p; same = 0; }
                if (same > 80) break;
            }
            if (same > 80) {
                stuckTotal++;
                if (reported < 8) {
                    reported++;
                    Level lv = Dungeon.level;
                    int w = lv.width();
                    boolean[] known = RLNav.knownMap(lv);
                    boolean[] walk = RLNav.buildWalkable(lv, known, Dungeon.hero.pos);
                    boolean[] fr = RLNav.frontierMap(lv, known);
                    int nf = 0; for (boolean b : fr) if (b) nf++;
                    int ne = 0, npass = 0, nknown = 0, nsec = 0, nsecKnown = 0;
                    for (int i = 0; i < lv.length(); i++) {
                        if (known[i]) nknown++;
                        if (lv.passable[i]) npass++;
                        if (known[i] && lv.map[i] == Terrain.EXIT) ne++;
                        if (lv.map[i] == Terrain.SECRET_DOOR) { nsec++; if (known[i]) nsecKnown++; }
                    }
                    int dExit = RLNav.firstStep(lv, walk, Dungeon.hero.pos, RLNav.exitMask(lv, known));
                    int dFr = RLNav.firstStep(lv, walk, Dungeon.hero.pos, fr);
                    boolean hasKey = RLNav.hasType(Dungeon.hero, com.shatteredpixel.shatteredpixeldungeon.items.keys.Key.class);
                    System.out.println("STUCK seed=" + seed + " depth=" + Dungeon.depth + " pos=" + Dungeon.hero.pos
                            + " map=" + lv.map[Dungeon.hero.pos] + " hp=" + Dungeon.hero.HP + "/" + Dungeon.hero.HT
                            + " known=" + nknown + " passable=" + npass + " frontier=" + nf + " exitKnown=" + ne
                            + " dExit=" + dExit + " dFr=" + dFr + " hasKey=" + hasKey + " sec=" + nsec + "(" + nsecKnown + ")"
                            + " enemy=" + (com.shatteredpixel.shatteredpixeldungeon.Statistics.enemiesSlain));
                    int[] nb8 = RLNav.neighbours8(w);
                    StringBuilder sb = new StringBuilder("   nb:");
                    for (int k = 0; k < 8; k++) {
                        int n = Dungeon.hero.pos + nb8[k];
                        if (n < 0 || n >= lv.length()) continue;
                        sb.append(" ").append(k).append(":").append(lv.map[n]).append(known[n] ? "k" : "u");
                        com.shatteredpixel.shatteredpixeldungeon.actors.Char ch = com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(n);
                        if (ch != null) sb.append("(").append(ch.getClass().getSimpleName()).append(")");
                    }
                    System.out.println(sb.toString());
                    if (dFr >= 0) {
                        int[] nbb = RLNav.neighbours8(lv.width());
                        int tc = Dungeon.hero.pos + nbb[dFr];
                        com.shatteredpixel.shatteredpixeldungeon.actors.Char tch = com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(tc);
                        System.out.println("   targetDir=" + dFr + " cell=" + tc + " map=" + lv.map[tc]
                                + " passable=" + lv.passable[tc] + " walk=" + walk[tc]
                                + " char=" + (tch == null ? "null" : tch.getClass().getSimpleName())
                                + " charHP=" + (tch == null ? -1 : tch.HP) + " charAlive=" + (tch == null ? false : tch.isAlive())
                                + " fov=" + lv.heroFOV[tc]);
                        StringBuilder bs = new StringBuilder("   heroBuffs:");
                        for (com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff b : Dungeon.hero.buffs()) bs.append(" ").append(b.getClass().getSimpleName());
                        System.out.println(bs.toString());
                    }
                    if (reported <= 1) {
                        int hw = lv.width();
                        int hx = Dungeon.hero.pos % hw, hy = Dungeon.hero.pos / hw;
                        for (int yy = 0; yy < lv.height(); yy++) {
                            StringBuilder line = new StringBuilder("   ");
                            for (int xx = 0; xx < lv.width(); xx++) {
                                int idx = yy * hw + xx;
                                if (idx == Dungeon.hero.pos) { line.append('H'); continue; }
                                int t = lv.map[idx];
                                char ch;
                                switch (t) {
                                    case Terrain.WALL: case Terrain.WALL_DECO: ch = '#'; break;
                                    case Terrain.DOOR: case Terrain.OPEN_DOOR: ch = '+'; break;
                                    case Terrain.LOCKED_DOOR: ch = 'L'; break;
                                    case Terrain.SECRET_DOOR: ch = '?'; break;
                                    case Terrain.ENTRANCE: ch = '<'; break;
                                    case Terrain.EXIT: ch = '>'; break;
                                    case Terrain.WATER: ch = '~'; break;
                                    case Terrain.HIGH_GRASS: case Terrain.FURROWED_GRASS: ch = '"'; break;
                                    case Terrain.TRAP: case Terrain.INACTIVE_TRAP: case Terrain.SECRET_TRAP: ch = '^'; break;
                                    case Terrain.CHASM: ch = 'C'; break;
                                    default: ch = lv.passable[idx] ? ' ' : 'x'; break;
                                }
                                if (!known[idx]) ch = Character.toLowerCase(ch);
                                line.append(ch);
                            }
                            System.out.println(line.toString());
                        }
                    }
                }
            }
        }
        System.out.println("STUCKDIAG episodes=" + episodes + " stuck=" + stuckTotal);
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
