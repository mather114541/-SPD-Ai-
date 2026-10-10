package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.watabou.noosa.Game;
import com.watabou.utils.FileUtils;

/**
 * 最小可行性实验 v2：
 * 补上 headless 全局初始化（Game 版本、文件属性），再尝试初始化 Dungeon 并生成关卡。
 */
public class RLSpike implements ApplicationListener {

    public static void main(String[] args) {
        new HeadlessApplication(new RLSpike());
    }

    @Override
    public void create() {
        try {
            // ---- headless 全局初始化（对应 DesktopLauncher 的关键步骤） ----
            Game.version = "4.0.0";
            Game.versionCode = 400;
            String dataDir = System.getProperty("user.dir") + "\\savedata";
            FileUtils.setDefaultFileProperties(Files.FileType.Absolute, dataDir);
            SPDSettings.customSeed("");                        // 随机种子
            GamesInProgress.selectedClass = HeroClass.WARRIOR;  // 战士

            System.out.println("=== Dungeon.init() ===");
            Dungeon.init();
            System.out.println("hero = " + Dungeon.hero);

            System.out.println("=== Dungeon.newLevel() + switchLevel ===");
            Level lv = Dungeon.newLevel();
            Dungeon.switchLevel(lv, lv.entrance());
            System.out.println("LEVEL OK len=" + lv.length()
                    + " w=" + lv.width() + " h=" + lv.height()
                    + " depth=" + Dungeon.depth
                    + " mobs=" + lv.mobs.size());
            System.out.println("hero.pos=" + Dungeon.hero.pos
                    + " entrance=" + lv.entrance() + " exit=" + lv.exit());

            System.out.println("=== 移动测试：设置 Move 动作 ===");
            int target = -1;
            for (int i : com.watabou.utils.PathFinder.NEIGHBOURS8) {
                int c = Dungeon.hero.pos + i;
                if (c >= 0 && c < lv.length() && lv.passable[c]
                        && com.shatteredpixel.shatteredpixeldungeon.actors.Actor.findChar(c) == null) {
                    target = c;
                    break;
                }
            }
            if (target == -1) {
                System.out.println("no adjacent free cell");
            } else {
                Dungeon.hero.curAction = new com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroAction.Move(target);
                com.shatteredpixel.shatteredpixeldungeon.actors.Actor.processTurn();
                System.out.println("after turn: pos=" + Dungeon.hero.pos
                        + " target=" + target + " ready=" + Dungeon.hero.ready
                        + " hp=" + Dungeon.hero.HP + "/" + Dungeon.hero.HT);
            }

            System.out.println("SPIKE SUCCESS");
        } catch (Throwable t) {
            System.err.println("=== SPIKE FAILED ===");
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
