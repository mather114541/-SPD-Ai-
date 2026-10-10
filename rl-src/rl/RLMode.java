package rl;

/**
 * RL 模式全局开关。
 * headless=true 时，游戏核心逻辑跳过所有 UI/渲染相关调用（GameScene、sprite、BuffIndicator 等），
 * 以便在无图形环境下作为强化学习环境运行。
 */
public class RLMode {
    public static boolean headless = true;
}
