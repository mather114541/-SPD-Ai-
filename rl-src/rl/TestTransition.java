package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.utils.PathFinder;

/**
 * 定向下楼测试：用 watabou 寻路从英雄位置走向 exit 楼梯，到达后点击自身格
 * 触发下楼，验证换层（depth 增加）在 headless 下可用。
 */
public class TestTransition implements ApplicationListener {

    public static void main(String[] args) {
        new HeadlessApplication(new TestTransition());
    }

    @Override
    public void create() {
        try {
            RLController env = new RLController();
            int[] obs = env.reset(12345L);
            Level lv = Dungeon.level;
            int exit = lv.exit();
            System.out.println("start depth=" + env.getDepth() + " pos=" + env.getHeroPos() + " exit=" + exit);

            PathFinder.buildDistanceMap(exit, lv.passable);
            int guard = 0;
            while (env.getHeroPos() != exit && !env.isDone() && guard++ < 3000) {
                int cur = env.getHeroPos();
                int step = PathFinder.getStep(cur, exit, lv.passable);
                if (step == -1 || step == cur) {
                    System.out.println("cannot advance from " + cur);
                    break;
                }
                int dir = -1;
                for (int d = 0; d < 8; d++) {
                    if (cur + PathFinder.NEIGHBOURS8[d] == step) { dir = d; break; }
                }
                if (dir == -1) { System.out.println("no dir to step " + step); break; }
                env.step(dir);
                System.out.println("move->" + step + " pos=" + env.getHeroPos()
                        + " r=" + env.getLastReward() + " depth=" + env.getDepth()
                        + " hp=" + (obs[obs.length - 5]));
            }
            System.out.println("at pos=" + env.getHeroPos() + " (exit=" + exit + ") depth=" + env.getDepth());
            if (env.getHeroPos() != exit) {
                // 寻路可能被关闭的门挡住；直接传送英雄到楼梯格，验证换层逻辑无头化可用
                System.out.println("-- teleporting hero to exit for transition test --");
                Dungeon.hero.pos = exit;
                boolean hdl = Dungeon.hero.handle(Dungeon.hero.pos);
                System.out.println("DIAG handle=" + hdl + " curAction="
                        + (Dungeon.hero.curAction == null ? "null"
                           : Dungeon.hero.curAction.getClass().getSimpleName()));
                com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition tr =
                        Dungeon.level.getTransition(exit);
                System.out.println("DIAG getTransition=" + tr + " inside=" + (tr != null && tr.inside(exit)));
                // 清空怪物，避免它们打断英雄的换层动作（验证纯换层路径）
                for (com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob m :
                        Dungeon.level.mobs.toArray(new com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob[0])) {
                    m.destroy();
                }
                Dungeon.level.mobs.clear();
                Dungeon.hero.curAction = null;
                env.step(8); // 点击自身格 -> LvlTransition（下楼）
                System.out.println("after teleport-descend: depth=" + env.getDepth()
                        + " pos=" + env.getHeroPos() + " r=" + env.getLastReward()
                        + " done=" + env.isDone());
            } else {
                // 站在楼梯格，点击自身（动作8）触发下楼
                env.step(8);
                System.out.println("after descend: depth=" + env.getDepth()
                        + " pos=" + env.getHeroPos() + " r=" + env.getLastReward()
                        + " done=" + env.isDone());
            }
            System.out.println("TRANSITION TEST END depth=" + env.getDepth());
        } catch (Throwable t) {
            System.err.println("=== TRANSITION FAILED ===");
            t.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
