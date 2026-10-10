package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.util.Random;

/**
 * 换层机制 gate 验证脚本（用户 gate：确认"探索第一层→发现楼梯→顺利下楼"在训练环境真实可行，再投入训练）。
 * 驱动 RLController + RLExpert（已改为"一旦探索到已探出口楼梯立即下楼"），
 * 循环多局直到某局换层成功，打印完整 trace：站上出口楼梯 → 点脚下 action8 → 换到第 2 层。
 * 若 60 局均未换层则报失败退出(exit 1)。
 */
public class RLVerify implements ApplicationListener {
    @Override public void render() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    public static void main(String[] args) {
        new HeadlessApplication(new RLVerify());
    }

    @Override
    public void create() {
        RLController c = new RLController();
        Random rng = new Random(20261009L);
        for (int ep = 0; ep < 60; ep++) {
            long seed = rng.nextLong();
            int[] obs = c.reset(seed);
            int depthStart = Dungeon.depth;
            StringBuilder detail = new StringBuilder();
            int steps = 0;
            for (int s = 0; s < 900; s++) {
                int mapAt = Dungeon.level.map[Dungeon.hero.pos];
                int action = RLExpert.policy();
                if (mapAt == 8) detail.append("    [站上出口楼梯(map8)] s=" + s + "\n");
                if (mapAt == 8 && action == 8) detail.append("    [点脚下 action8 触发换层] s=" + s + "\n");
                obs = c.step(action);
                steps = s + 1;
                if (Dungeon.level.map[Dungeon.hero.pos] == 8 || obs[RLController.OFF_ONEXIT] > 0) {
                    StringBuilder exb = new StringBuilder();
                    for (int k = RLController.OFF_EXIT; k < RLController.OFF_EXIT + 8; k++) exb.append(obs[k]).append(',');
                    detail.append("    [obs-EXIT] ONEXIT=" + obs[RLController.OFF_ONEXIT]
                        + " EXIT扇区=[" + exb + "]\n");
                }
                if (Dungeon.depth > depthStart) {
                    System.err.println("[VERIFY] 换层成功 ep=" + ep + " steps=" + steps
                        + " depth=" + Dungeon.depth + "（已到第 " + Dungeon.depth + " 层）");
                    System.err.println("[VERIFY] 关键换层 trace：\n" + detail);
                    System.exit(0);
                }
                if (!Dungeon.hero.isAlive()) break;
            }
            System.err.println("[VERIFY] ep=" + ep + " 未换层 steps=" + steps
                + " depth=" + Dungeon.depth + " alive=" + Dungeon.hero.isAlive());
        }
        System.err.println("[VERIFY] FAILED: 60 局均未换层，换层机制可能异常");
        System.exit(1);
    }
}
