package rl;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

/**
 * RL 环境测试/命令行入口。必须通过 HeadlessApplication 启动（Gdx.app 生命周期）。
 *
 * 用法1（自测）：java -cp ... rl.RLCli
 *   在 create() 里跑若干步随机动作，打印观察/奖励/终止/层数。
 *
 * 用法2（协议模式，供 Python 驱动）：java -cp ... rl.RLCli proto
 *   stdin 逐行命令，stdout 逐行响应：
 *     "reset <seed>"  -> "OK len=<obsLen> depth=.. pos=.. reward=.. done=.." + 下一行观察数字
 *     "step <action>" -> 同上
 */
public class RLCli implements ApplicationListener {

    private final boolean proto;

    public static void main(String[] args) {
        boolean proto = args.length > 0 && args[0].equals("proto");
        new HeadlessApplication(new RLCli(proto));
    }

    public RLCli(boolean proto) {
        this.proto = proto;
        if (proto) {
            // 把游戏内部日志/GLog(System.out) 重定向到 stderr，保证 stdout 只有 proto 协议行，
            // 否则游戏消息会污染 Python 端的 reset/step 响应解析。
            try {
                System.setOut(new java.io.PrintStream(new java.io.FileOutputStream(java.io.FileDescriptor.err),
                        true, java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception e) {
                // ignore
            }
        }
    }

    @Override
    public void create() {
        if (proto) {
            runProtoLoop();
        } else {
            selfTest();
        }
    }

    private void selfTest() {
        RLController env = new RLController();
        int[] obs = env.reset(12345L);
        System.out.println("reset len=" + obs.length + " (OBS_LEN=" + RLController.OBS_LEN + ") depth="
                + env.getDepth() + " pos=" + env.getHeroPos());
        int totalR = 0;
        for (int i = 0; i < 30 && !env.isDone(); i++) {
            int a = (int) (Math.random() * RLController.N_ACTIONS);
            int[] o = env.step(a);
            int hp = o[RLController.OFF_META + 2]; // meta 第3位 = HP
            totalR += env.getLastReward();
            System.out.println("step " + i + " a=" + a + " r=" + env.getLastReward()
                    + " done=" + env.isDone() + " depth=" + env.getDepth()
                    + " pos=" + env.getHeroPos() + " hp=" + hp);
        }
        System.out.println("SELFTEST END done=" + env.isDone() + " depth=" + env.getDepth()
                + " totalR=" + totalR);
        System.exit(0);
    }

    private void runProtoLoop() {
        final RLController env = new RLController();
        // proto 响应写到原始 stdout（FileDescriptor.out），不受 System.out 重定向影响
        final java.io.PrintStream proto = new java.io.PrintStream(
                new java.io.FileOutputStream(java.io.FileDescriptor.out), true, java.nio.charset.StandardCharsets.UTF_8);
        Thread t = new Thread(() -> {
            java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    String[] p = line.split("\\s+");
                    int[] obs;
                    int reward = 0;
                    boolean done = false;
                    int protoAction = -1;
                    try {
                        if (p[0].equals("target")) {
                            env.targetDepth = Integer.parseInt(p[1]);
                            proto.println("OK target=" + env.targetDepth);
                            continue;
                        } else if (p[0].equals("reset")) {
                            obs = env.reset(Long.parseLong(p[1]));
                        } else if (p[0].equals("step")) {
                            int a;
                            if (p[1].equals("E")) a = RLExpert.policy();   // 专家动作（DAgger 在线混合用）
                            else a = Integer.parseInt(p[1]);
                            obs = env.step(a);
                            reward = env.getLastReward();
                            done = env.isDone();
                            protoAction = a;
                        } else if (p[0].equals("expert")) {
                            int st = runExpert(env, Long.parseLong(p[1]), Integer.parseInt(p[2]), p[3]);
                            proto.println("OK expert steps=" + st + " depth=" + env.getDepth() + " done=" + env.isDone());
                            continue;
                        } else {
                            proto.println("ERR unknown command");
                            continue;
                        }
                    } catch (Throwable e) {
                        e.printStackTrace(); // 栈打到 stderr，便于诊断
                        proto.println("ERR " + e);
                        continue;
                    }
                    proto.println("OK len=" + obs.length
                            + " depth=" + env.getDepth()
                            + " pos=" + env.getHeroPos()
                            + " reward=" + reward
                            + " done=" + done
                            + " action=" + protoAction);
                    StringBuilder d = new StringBuilder();
                    for (int v : obs) d.append(v).append(' ');
                    proto.println(d.toString().trim());
                }
            } catch (Exception e) {
                proto.println("ERR " + e);
            }
            System.exit(0);
        });
        t.setDaemon(true);
        t.start();
        // 主线程交给 HeadlessApplication 的 mainLoop（sleep 循环），stdin 线程驱动逻辑
    }

    /**
     * 专家模式：用 RLExpertBot 玩一局，把 (action, obs0..obsN) 逐行追加写入 outFile。
     */
    private static int runExpert(RLController env, long seed, int maxSteps, String outFile) {
        int[] obs = env.reset(seed);
        StringBuilder sb = new StringBuilder();
        int steps = 0;
        try (java.io.PrintWriter w = new java.io.PrintWriter(new java.io.BufferedWriter(
                new java.io.FileWriter(outFile, true)))) {
            for (; steps < maxSteps && !env.isDone(); steps++) {
                if (steps % 100 == 0) {
                    int ex = 0, tot = 0, stairM = 0;
                    for (int i = 0; i < Dungeon.level.length(); i++) {
                        if (Dungeon.level.passable[i]) tot++;
                        if (Dungeon.level.mapped[i] || Dungeon.level.visited[i]) ex++;
                        if (Dungeon.level.map[i] == 8 && (Dungeon.level.mapped[i] || Dungeon.level.visited[i])) stairM++;
                    }
                    System.err.println("[expert] step=" + steps + " depth=" + Dungeon.depth
                        + " pos=" + Dungeon.hero.pos + " hp=" + Dungeon.hero.HP
                        + " explored=" + ex + "/" + tot + " stairKnown=" + stairM);
                }
                int a = RLExpertBot.chooseAction(Dungeon.hero);
                obs = env.step(a);
                sb.setLength(0);
                sb.append(a);
                for (int v : obs) sb.append(' ').append(v);
                w.println(sb);
            }
        } catch (java.io.IOException e) {
            // 写文件失败不中断
        }
        return steps;
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
