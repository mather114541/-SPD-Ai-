package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import java.util.Random;

/**
 * 游戏内托管冒烟：模拟 Hero.act 托管分支的时序
 *   （encodeObs → RLAutoPilot.act → RLController.applyMacro 设 HeroAction → Actor.processTurn 推进回合），
 * 验证游戏 GUI 托管实际使用的 applyMacro 链路能否下楼（与 RLOnnxEval 用 step 对照）。
 * java rl.RLGuiSmoke <episodes> <seedBase> <maxSteps> <targetDepth> [onnx] [norm]
 */
public class RLGuiSmoke implements ApplicationListener {
    private final int episodes, seedBase, maxSteps, target;
    private final String onnxPath, normPath;

    public static void main(String[] args) {
        int ep = args.length > 0 ? Integer.parseInt(args[0]) : 20;
        int sb = args.length > 1 ? Integer.parseInt(args[1]) : 1;
        int mx = args.length > 2 ? Integer.parseInt(args[2]) : 2500;
        int tg = args.length > 3 ? Integer.parseInt(args[3]) : 6;
        String onnx = args.length > 4 ? args[4] : "rl-train/out/bc5.onnx";
        String norm = args.length > 5 ? args[5] : "rl-train/out/bc5.norm.json";
        new HeadlessApplication(new RLGuiSmoke(ep, sb, mx, tg, onnx, norm));
    }

    public RLGuiSmoke(int episodes, int seedBase, int maxSteps, int target, String onnx, String norm) {
        this.episodes = episodes; this.seedBase = seedBase; this.maxSteps = maxSteps; this.target = target;
        this.onnxPath = onnx; this.normPath = norm;
    }

    @Override public void create() {
        boolean ok = RLAutoPilot.init(onnxPath, normPath);
        if (!ok) { System.out.println("ONNX_INIT_FAIL"); System.exit(1); return; }
        RLController c = new RLController();
        c.targetDepth = target;
        Random rng = new Random(seedBase);
        int[] hist = new int[16];
        int died = 0, stuckAbort = 0;
        long totalSteps = 0;
        for (int e = 0; e < episodes; e++) {
            long seed = rng.nextLong();
            int[] obs = c.reset(seed);
            int steps = 0, lastPos = c.getHeroPos(), same = 0;
            while (steps < maxSteps) {
                if (c.isDone()) break;
                int macro = RLAutoPilot.act(obs);
                RLController.applyMacro(Dungeon.hero, macro);   // 托管：宏→HeroAction（不含 processTurn）
                Actor.processTurn();                             // 模拟游戏 GUI 回合循环推进
                obs = RLController.encodeObs();
                steps++;
                int p = c.getHeroPos();
                if (p == lastPos) same++; else { lastPos = p; same = 0; }
                if (same > 80) { stuckAbort++; break; }
            }
            int d = Math.min(c.getDepth(), 15);
            hist[d]++;
            if (!Dungeon.hero.isAlive()) died++;
            totalSteps += steps;
        }
        StringBuilder sb = new StringBuilder();
        for (int d = 1; d < 16; d++) if (hist[d] > 0) sb.append("d").append(d).append("=").append(hist[d]).append(" ");
        int ge2 = 0, ge3 = 0, ge4 = 0;
        for (int d = 2; d < 16; d++) ge2 += hist[d];
        for (int d = 3; d < 16; d++) ge3 += hist[d];
        for (int d = 4; d < 16; d++) ge4 += hist[d];
        System.out.println("GUISMOKE ep=" + episodes + " target=" + target + " hist=[" + sb.toString().trim() + "]"
                + " >=2:" + ge2 + " >=3:" + ge3 + " >=4:" + ge4
                + " died=" + died + " stuck=" + stuckAbort
                + " avgSteps=" + String.format("%.1f", totalSteps / (double) episodes));
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
