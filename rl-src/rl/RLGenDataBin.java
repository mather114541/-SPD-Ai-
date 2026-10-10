package rl;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 行为克隆二进制轨迹生成器（obs v2 全层已探地图）。
 * 文件格式（大端）：
 *   头 32 字节：magic 'SPD2' + int32 MAPW + int32 MAPH + int32 NSCALAR + int32 NACT + 12 字节 0 填充
 *   记录：uint16[NMAP] + int32[NSCALAR] + int32 action
 * 用法：java rl.RLGenDataBin --out <f> --per <局数> --max <每局步数> --seedbase <s> [--all]
 */
public class RLGenDataBin implements ApplicationListener {
    private final String out;
    private final int per, max, target, minDepth;
    private final long seedBase;
    private final boolean keepAll;

    public static void main(String[] args) {
        String out = "rl-train/out/traj2.bin";
        int per = 40, max = 800, target = 5, minDepth = 2;
        long seedBase = 1L;
        boolean all = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--out") && i + 1 < args.length) out = args[i + 1];
            if (args[i].equals("--per") && i + 1 < args.length) per = Integer.parseInt(args[i + 1]);
            if (args[i].equals("--max") && i + 1 < args.length) max = Integer.parseInt(args[i + 1]);
            if (args[i].equals("--seedbase") && i + 1 < args.length) seedBase = Long.parseLong(args[i + 1]);
            if (args[i].equals("--target") && i + 1 < args.length) target = Integer.parseInt(args[i + 1]);
            if (args[i].equals("--mindepth") && i + 1 < args.length) minDepth = Integer.parseInt(args[i + 1]);
            if (args[i].equals("--all")) all = true;
        }
        new HeadlessApplication(new RLGenDataBin(out, per, max, seedBase, all, target, minDepth));
    }

    public RLGenDataBin(String out, int per, int max, long seedBase, boolean keepAll, int target, int minDepth) {
        this.out = out; this.per = per; this.max = max; this.seedBase = seedBase; this.keepAll = keepAll;
        this.target = target; this.minDepth = minDepth;
    }

    @Override public void create() {
        Random rng = new Random(seedBase);
        RLController c = new RLController();
        c.targetDepth = target;
        int reached = 0;
        long records = 0;
        int nscalar = RLController.OBS_LEN - RLController.NMAP;
        try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(out), 1 << 22))) {
            os.writeBytes("SPD2");
            os.writeInt(RLController.MAPW);
            os.writeInt(RLController.MAPH);
            os.writeInt(nscalar);
            os.writeInt(RLController.N_ACTIONS);
            for (int i = 0; i < 12; i++) os.writeByte(0);
            for (int e = 0; e < per; e++) {
                long seed = rng.nextLong();
                int[] obs = c.reset(seed);
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1 << 16);
                DataOutputStream ep = new DataOutputStream(baos);
                int steps = 0, lastPos = c.getHeroPos(), same = 0;
                int epRecords = 0;
                while (steps < max) {
                    if (c.isDone()) break;
                    int a = RLExpert.policy();
                    for (int i = 0; i < RLController.NMAP; i++) ep.writeShort(obs[i]);
                    for (int i = RLController.NMAP; i < RLController.OBS_LEN; i++) ep.writeInt(obs[i]);
                    ep.writeInt(a);
                    epRecords++;
                    obs = c.step(a);
                    steps++;
                    int p = c.getHeroPos();
                    if (p == lastPos) same++; else { lastPos = p; same = 0; }
                    if (same > 60) break;
                }
                boolean ok = c.getDepth() >= minDepth;
                if (ok) reached++;
                if (ok || keepAll) {
                    os.write(baos.toByteArray());
                    records += epRecords;
                }
                if (e % 20 == 0) System.err.println("[gen] ep=" + e + " depth=" + c.getDepth() + " steps=" + steps + " rec=" + records);
            }
            os.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.err.println("[gen] DONE episodes=" + per + " reached=" + reached + " records=" + records);
        System.exit(0);
    }

    @Override public void resize(int w, int h) {}
    @Override public void render() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void dispose() {}
}
