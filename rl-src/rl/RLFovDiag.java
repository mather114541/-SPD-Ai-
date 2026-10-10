package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import java.util.Random;

/** 诊断：Char/Mob 的 act 调用次数 vs 突袭次数。 */
public class RLFovDiag implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLFovDiag()); }

    @Override public void create() {
        RLController c = new RLController();
        c.targetDepth = 3;
        Random rng = new Random(4242);
        long steps = 0;
        for (int e = 0; e < 4; e++) {
            c.reset(rng.nextLong());
            int st = 0;
            while (st < 800 && !c.isDone()) { c.step(RLExpert.policy()); st++; }
            steps += st;
        }
        int nullFov = 0, nonNull = 0;
        for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m : Dungeon.level.mobs) {
            if (m.fieldOfView == null) nullFov++; else nonNull++;
        }
        System.out.println("DIAG steps=" + steps + " charAct=" + Char.rlActCount + " mobAct=" + Char.rlMobActCount
                + " sneak=" + Statistics.sneakAttacks + " slain=" + Statistics.enemiesSlain
                + " nullFov=" + nullFov + " nonNull=" + nonNull);
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
