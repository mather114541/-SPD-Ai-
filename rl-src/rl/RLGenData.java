package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

/**
 * 用 RLExpert 专家机器人批量生成行为克隆轨迹。
 * 必须经 HeadlessApplication 启动（Gdx.app 生命周期，与 RLCli 一致）。
 * 每行：1417 个观察值(空格分隔) + 末尾一个动作(0-11)。
 * 用法：java rl.RLGenData --out <file> --per <局数> --max <每局最大步数>
 */
public class RLGenData implements ApplicationListener {
    private final String out;
    private final int per, max;

    public static void main(String[] args) {
        String out = "rl-train\\out\\traj.txt";
        int per = 40;
        int max = 600;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--out") && i + 1 < args.length) out = args[i + 1];
            if (args[i].equals("--per") && i + 1 < args.length) per = Integer.parseInt(args[i + 1]);
            if (args[i].equals("--max") && i + 1 < args.length) max = Integer.parseInt(args[i + 1]);
        }
        new HeadlessApplication(new RLGenData(out, per, max));
    }

    public RLGenData(String out, int per, int max) {
        this.out = out;
        this.per = per;
        this.max = max;
    }

    @Override public void render() {}
    @Override public void resize(int w, int h) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}

    @Override
    public void create() {
        Random rng = new Random(987654321L);
        RLController c = new RLController();
        long totalLines = 0;
        int reached = 0;
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(out, false), 1 << 20))) {
            for (int e = 0; e < per; e++) {
                long seed = rng.nextLong();
                int[] obs = c.reset(seed);
                int depthStart = Dungeon.depth;
                boolean epDone = false;
                int steps = 0;
                int lastPos = -1, stuck = 0;
                // 每局先写入内存缓冲，局末仅当"换层成功"才落盘 → 生成的轨迹是纯净的换层富集轨迹
                java.io.StringWriter sw = new java.io.StringWriter();
                PrintWriter epw = new PrintWriter(sw);
                int epLines = 0;
                for (int s = 0; s < max && !epDone; s++) {
                    int action = RLExpert.policy();
                    writeLine(epw, obs, action);
                    epLines++;
                    obs = c.step(action);
                    steps++;
                    if (steps % 25 == 0) System.err.println("[pos] s=" + steps + " pos=" + Dungeon.hero.pos + " d=" + Dungeon.depth + " a=" + action);
                    if (Dungeon.hero.pos == lastPos) {
                        stuck++;
                        if (stuck > 5 && stuck % 10 == 0) System.err.println("STUCK pos=" + Dungeon.hero.pos + " map=" + Dungeon.level.map[Dungeon.hero.pos] + " a=" + action + " hhp=" + Dungeon.hero.HP);
                        if (stuck > 30) { epDone = true; System.err.println("ABORT pos=" + Dungeon.hero.pos); }
                    } else { lastPos = Dungeon.hero.pos; stuck = 0; }
                    if (!Dungeon.hero.isAlive() || Dungeon.depth >= c.targetDepth) epDone = true;
                }
                int vn = 0;
                for (int i = 0; i < Dungeon.level.length(); i++) if (Dungeon.level.visited[i] || Dungeon.level.mapped[i]) vn++;
                System.err.println("[gen] visited=" + vn);
                if (Dungeon.depth > depthStart) {
                    reached++;
                    pw.write(sw.toString());   // 仅换层成功的局写入轨迹
                    totalLines += epLines;
                } else {
                    System.err.println("[gen] DROP non-layered ep=" + e + " steps=" + steps + " depth=" + Dungeon.depth);
                }
                System.err.println("[gen] ep=" + e + " steps=" + steps + " depth=" + Dungeon.depth
                    + " alive=" + Dungeon.hero.isAlive() + " cum=" + totalLines);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.err.println("[gen] DONE lines=" + totalLines + " reached=" + reached + "/" + per);
        System.exit(0);
    }

    private static void writeLine(PrintWriter pw, int[] obs, int action) {
        StringBuilder sb = new StringBuilder(obs.length * 4 + 4);
        for (int v : obs) sb.append(v).append(' ');
        sb.append(action);
        pw.println(sb);
    }
}
