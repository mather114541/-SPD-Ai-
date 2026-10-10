package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import java.util.Random;

/** 验证三条新战斗 primitive：投掷是否命中掉血、漏斗撤退、突袭。 */
public class RLThrowCheck implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLThrowCheck()); }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 3;
        Random rng = new Random(31337);
        int throwHit = 0, throwMiss = 0, deaths = 0, ge2 = 0, ge3 = 0, mThrowChosen = 0;
        long steps = 0;
        for (int e = 0; e < 12; e++) {
            int[] obs = c.reset(rng.nextLong());
            if (e == 0) System.out.println("CHECK obsLen=" + obs.length + " N_ACTIONS=" + RLController.N_ACTIONS);
            int st = 0;
            while (st < 900 && !c.isDone()) {
                int a = RLExpert.policy();
                Mob mb = RLNav.nearestVisibleMob(Dungeon.level, Dungeon.hero.pos);
                int hpB = (mb != null && mb.isAlive()) ? mb.HP : -1;
                if (a == RLExpert.M_THROW) mThrowChosen++;
                c.step(a);
                st++;
                if (a == RLExpert.M_THROW && hpB >= 0 && mb != null) {
                    if (mb.HP < hpB || !mb.isAlive()) throwHit++; else throwMiss++;
                }
            }
            int dep = c.getDepth();
            if (dep >= 2) ge2++;
            if (dep >= 3) ge3++;
            if (!Dungeon.hero.isAlive()) deaths++;
            steps += st;
        }
        System.out.println("CHECK throwsExec=" + RLCombat.throwsDone + " chosen=" + mThrowChosen
                + " hit=" + throwHit + " miss=" + throwMiss
                + " | holdSlow=" + RLCombat.holdSlow + " holdFast=" + RLCombat.holdFast
                + " funnelMoves=" + RLCombat.funnelMoves
                + " | sneak=" + Statistics.sneakAttacks + " slain=" + Statistics.enemiesSlain
                + " | deaths=" + deaths + " >=2:" + ge2 + " >=3:" + ge3 + " avgSteps=" + (steps / 12));
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
