package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;

/** 检查无头环境下怪物 FOV / 突袭判定是否真实可用。 */
public class RLMobFovCheck implements ApplicationListener {
    public static void main(String[] a) { new HeadlessApplication(new RLMobFovCheck()); }

    @Override public void create() {
        RLController c = new RLController();
        c.reset(12345L);
        for (int i = 0; i < 120; i++) c.step(8);   // 走很多回合让怪行动、算 FOV
        int nullFov = 0, nonNull = 0;
        for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob mm : Dungeon.level.mobs) {
            if (mm.fieldOfView == null) nullFov++; else nonNull++;
        }
        System.out.println("CHECK mobs nullFov=" + nullFov + " nonNull=" + nonNull);
        Level lv = Dungeon.level;
        int heroPos = Dungeon.hero.pos;
        int hf = 0; for (boolean b : lv.heroFOV) if (b) hf++;
        int lb = 0; for (boolean b : lv.losBlocking) if (b) lb++;
        System.out.println("CHECK heroFOV=" + hf + " losBlocking=" + lb + " levelLen=" + lv.length());
        int shown = 0;
        for (Mob m : lv.mobs) {
            int cnt = 0;
            if (m.fieldOfView != null) for (boolean b : m.fieldOfView) if (b) cnt++;
            boolean sees = m.fieldOfView != null && m.pos >= 0 && m.fieldOfView.length == lv.length() && m.fieldOfView[heroPos];
            System.out.println("CHECK mob=" + m.getClass().getSimpleName() + " pos=" + m.pos
                    + " fovNull=" + (m.fieldOfView == null) + " fovCount=" + cnt
                    + " seesHero=" + sees + " surprised=" + m.surprisedBy(Dungeon.hero)
                    + " dist=" + lv.distance(m.pos, heroPos));
            if (++shown >= 8) break;
        }
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
