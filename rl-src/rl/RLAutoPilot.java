package rl;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 游戏内 AI 托管推理器：加载 bc 神经网络(ONNX)，把玩家同权观测 obs[2409] 编码为
 * 地图位域(expand->16通道) + 标量(归一化)，推理输出宏动作(0..8)。
 * 与 Python 训练侧 spd_v2.expand / norm_scalars / CNN 完全对齐。
 */
public class RLAutoPilot {
    public static final int NMAP = 2304, NCH = 16, N_SCALAR = 105, MAPW = 48, MAPH = 48;
    public static final int N_ACT = 9;

    /** 是否处于 AI 托管模式 */
    public static boolean active = false;

    /** 托管步进间隔（毫秒）：默认 1000 = 一秒一步，避免 AI 快到看不清。可用 -Drl.stepMs=500 之类调整。 */
    public static int stepMs = 500;

    private static long lastActMs = 0;

    /** 节奏门：距上次 AI 行动是否已过 stepMs。
     *  必须在英雄决策处判断（绝不能在 actor 线程里 sleep，那会阻塞握手把 UI 冻死）。 */
    public static boolean readyToAct() {
        long now = System.currentTimeMillis();
        if (stepMs <= 0 || now - lastActMs >= stepMs) {
            lastActMs = now;
            return true;
        }
        return false;
    }

    static {
        // 本类是 GUI 托管入口：一旦加载就必须解除 headless 短路，
        // 否则 GameScene.updateFog() 等十几处 if (RLMode.headless) return; 会让视野全黑、UI 无响应。
        RLMode.headless = false;
        String sm = System.getProperty("rl.stepMs");
        if (sm != null) try { stepMs = Integer.parseInt(sm); } catch (NumberFormatException e) { }
        System.out.println("[autopilot] RLMode.headless=" + RLMode.headless + " stepMs=" + stepMs + " (GUI 模式)");
    }

    private static OrtSession session;
    private static float[] mean, std;

    /** 切换托管模式（首次开启自动加载模型） */
    public static void toggle() {
        RLMode.headless = false;   // 每次切换都确保渲染/UI 未被短路
        if (!active && session == null) tryInit();
        active = !active;
        if (active) {
            lastActMs = 0;   // 开启后立刻走第一步
            // actor 线程在英雄空闲时处于 wait()，必须主动唤醒，否则要按一下"等待"键才会开始行动
            try { com.shatteredpixel.shatteredpixeldungeon.actors.Actor.wakeActorThread(); } catch (Throwable t) { }
        }
        System.out.println("[autopilot] " + (active ? "ON" : "OFF"));
    }

    /** 尝试加载模型（bc11 优先：它带拾取能力；bc5 兜底；支持系统属性 -Drl.model=... 或相对项目根 rl-train/out）。 */
    private static void tryInit() {
        String custom = System.getProperty("rl.model");
        if (custom != null && java.nio.file.Files.exists(java.nio.file.Paths.get(custom))) {
            String norm = custom.replaceFirst("\\.onnx$", ".norm.json");
            if (init(custom, norm)) return;
        }
        String[] paths = { "rl-train/out/bc11.onnx", "rl-train/out/bc5.onnx" };
        for (String p : paths) {
            if (java.nio.file.Files.exists(java.nio.file.Paths.get(p))) {
                String norm = p.replaceFirst("\\.onnx$", ".norm.json");
                if (init(p, norm)) return;
            }
        }
        System.err.println("[autopilot] 未找到 ONNX 模型，托管不可用（可用 -Drl.model=path 指定）");
    }

    /** 加载 ONNX 模型 + 归一化参数。返回是否成功。 */
    public static boolean init(String onnxPath, String normJson) {
        try {
            OrtEnvironment env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions so = new OrtSession.SessionOptions();
            session = env.createSession(onnxPath, so);
            parseNorm(normJson);
            System.out.println("[autopilot] ONNX loaded: " + onnxPath);
            return true;
        } catch (Exception e) {
            System.err.println("[autopilot] init failed: " + e);
            session = null;
            return false;
        }
    }

    private static void parseNorm(String path) throws IOException {
        String txt = new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
        mean = parseArray(txt, "mean");
        std = parseArray(txt, "std");
        if (mean.length != N_SCALAR || std.length != N_SCALAR) {
            throw new IOException("norm dim mismatch: mean=" + mean.length + " std=" + std.length);
        }
    }

    private static float[] parseArray(String json, String key) {
        int i = json.indexOf("\"" + key + "\"");
        if (i < 0) i = json.indexOf(key);
        int a = json.indexOf('[', i);
        int b = json.indexOf(']', a);
        String body = json.substring(a + 1, b).replaceAll("\\s", "");
        if (body.isEmpty()) return new float[0];
        String[] parts = body.split(",");
        float[] out = new float[parts.length];
        for (int k = 0; k < parts.length; k++) out[k] = Float.parseFloat(parts[k]);
        return out;
    }

    /**
     * 玩家同权 obs[2409] -> 宏动作(0..8)。调用方负责提供观测（headless RLController 或游戏 GUI 内编码）。
     * 布局：map[0..2304) uint16 位域; sc[2304..2409) int32 标量。
     */
    public static int act(int[] obs) {
        if (session == null || mean == null || std == null) return 0;
        try {
            float[][][][] m = new float[1][NCH][MAPH][MAPW];
            for (int c = 0; c < NMAP; c++) {
                int v = obs[c] & 0xFFFF;
                int y = c / MAPW, x = c % MAPW;
                for (int b = 0; b < NCH; b++) {
                    if (((v >> b) & 1) != 0) m[0][b][y][x] = 1f;
                }
            }
            float[][] s = new float[1][N_SCALAR];
            for (int i = 0; i < N_SCALAR; i++) {
                float val = obs[NMAP + i];
                float denom = (std[i] == 0f) ? 1f : std[i];
                s[0][i] = (val - mean[i]) / denom;
            }
            OrtEnvironment env = OrtEnvironment.getEnvironment();
            try (OnnxTensor mt = OnnxTensor.createTensor(env, m);
                 OnnxTensor st = OnnxTensor.createTensor(env, s);
                 OrtSession.Result res = session.run(Map.of("m", mt, "s", st))) {
                Object v = res.get(0).getValue();
                float[] logits;
                if (v instanceof float[][]) logits = ((float[][]) v)[0];
                else if (v instanceof float[]) logits = (float[]) v;
                else throw new IOException("unexpected actor output: " + v.getClass());
                int a = 0;
                for (int i = 1; i < logits.length; i++) if (logits[i] > logits[a]) a = i;
                return a;
            }
        } catch (Exception e) {
            System.err.println("[autopilot] act failed: " + e);
            return 0;
        }
    }
}
