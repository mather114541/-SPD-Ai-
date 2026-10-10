# Shattered Pixel Dungeon RL Autopilot / 破碎像素地牢 AI 托管

Train an AI to **autonomously play Shattered Pixel Dungeon** (v4.0.0). The agent observes **exactly what the player sees** — fog of war, inventory, traps, secret doors — and drives the game through an in-game "autopilot" toggle.

用深度强化学习 + 行为克隆，教 AI 自动玩《破碎像素地牢》。AI 的观测与玩家**严格同权**（战争迷雾 / 背包 / 陷阱 / 隐藏门全部自学，环境不做保姆），游戏局内一键「托管」自动游玩。

> **License / 许可**：上游 Shattered PD 是 GPL-3.0，本项目为其派生改造，遵循 **GPL-3.0**。
> **Repository policy / 仓库策略**：只提交**源码 + 脚本 + ONNX 部署模型(LFS)**；十几 GB 的轨迹数据(`.bin`)与训练权重(`.pt`)由脚本重新生成，不入库。

---

## Architecture / 架构

```
        Shattered PD v4.0.0 jar (headless 改造)
                     │ 玩家同权 obs[2409] + 9 宏动作
                     ▼
        RLController (Java 环境: reset/step/encodeObs/applyMacro)
                     ▲
                     │ RLExpert 专家策略(扫图/进食/拾取/战斗/搜索)
                     ▼
        轨迹数据 .bin  ──►  train_bc2.py(行为克隆)  ──►  .pt
                                                           │
                                                export_onnx.py
                                                           ▼
               游戏 GUI「托管」按钮 ◄── RLAutoPilot(Java ONNX 推理)
                       每回合: encodeObs→act→applyMacro→HeroAction
```

- **Obs**：`48×48` 全层已探自动地图位域(16 通道) + `105` 维标量(HP/饥饿/物品/战斗态势…) = `2409` 维。玩家同权，未探区域为 0。
- **Actions**：9 个宏意图 `0 EXPLORE / 1 EXIT / 2 FIGHT / 3 HEAL / 4 EAT / 5 SEARCH / 6 PICKUP / 7 WAIT / 8 THROW`，逐格寻路交给 `RLNav` 的 BFS 执行器。
- **Policy**：CNN(地图) + MLP(标量) → actor/critic 双头，`spd_v2.py` 定义。

---

## Requirements / 环境要求

- **Java 17+**（编译/运行游戏与 RL 环境）
- **Python 3.10+**，`torch`(CUDA 版，NVIDIA GPU 推荐)、`onnx`, `onnxruntime`(Java, 部署)
- 依赖 jar：`gdx-backend-headless-1.14.0.jar`(headless)、`onnxruntime-<ver>.jar`(ONNX 推理) → 放入 `lib/`（仓库不含）
- ONNX 模型：`rl-train/out/bc5.onnx` / `bc11.onnx`（Git LFS）

---

## Quick Start / 快速开始

### 1) 编译 RL 环境（build\classes 必须排在 jar 前）
```bash
javac -encoding UTF-8 -cp "build/classes;ShatteredPD.jar;lib/gdx-backend-headless-1.14.0.jar;lib/onnxruntime-1.17.3.jar" -d build/classes rl-src/rl/*.java 改过的游戏类(Hero/Toolbar/...)
```
> 注意：`MissileWeapon.java` 反编译源有类型退化编不过，**不要重编**（投掷 NPE 已被 executeThrow try/catch 接住，无害）。

### 2) 生成轨迹数据（8 进程，各自独立工作目录）
```bash
java -cp "..." rl.RLGenDataBin --out rl-train/out/trajX_0.bin --per 150 --max 3000 --target 6 --mindepth 2 --seedbase <S>
```
每个生成进程必须用**不同 `-Duser.dir`/工作目录**，否则 savedata 互相踩。

### 3) 行为克隆训练
```bash
python rl-train/train_bc2.py --glob "rl-train/out/trajX_[0-7].bin" --out rl-train/out/bcX.pt --epochs 20 --batch 256 --exit-weight 3.0
```

### 4) 评估（下楼深度 / 覆盖率 / 有效搜索）
```bash
python rl-train/eval_policy.py rl-train/out/bcX.pt --episodes 30 --seedbase 31000000 --max-steps 3000 --target 6 --tag bcX
python rl-train/explore_report.py rl-train/out/bcX.pt --episodes 20 --seedbase 32000000
```

### 5) 导出 ONNX + 游戏内托管
```bash
python rl-train/export_onnx.py rl-train/out/bcX.pt rl-train/out/bcX.onnx   # 需先 pip install onnx
```
```bash
java -Xmx1g -cp "build/classes;ShatteredPD.jar;lib/gdx-backend-headless-1.14.0.jar;lib/onnxruntime-1.17.3.jar" com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon
```
进局后点工具栏的 **AI 按钮**开启托管，再点关闭。模型路径可用 `-Drl.model=path` 指定（默认 `rl-train/out/bc5.onnx`）。

---

## Key Mechanics / 核心机制

- **玩家同权**：AI 看不到未探索区域、未识别物品、隐藏门/陷阱（只有搜索后才发现）。
- **全探索模式**：`exploreTarget=0.99`（为后续多功能打基础），配合进食规则防饿死。
- **进食规则**：`饥肠辘辘(STARVING) 且 HP<85%` → 立刻吃口粮（首次让"用药"进入训练数据）。
- **拾取/投掷物回收**：`M_PICKUP` 用 `visibleLootMask` 拾取地面普通掉落(口粮/金币/**投掷物**)；投出的投掷物落地上会被回收。
- **高效搜索**：新楼层出生房四周无门才贴墙搜；1 格宽窄通道尽头搜一次（避免浪费回合）。
- **9 宏动作**：宏意图由网络输出，逐格寻路交给 BFS 执行器，把长时程导航从网络搬走。

---

## Layout / 目录结构

```
rl-src/rl/        Java：RLController(环境) RLNav(寻路/掩码) RLExpert(专家)
                  RLCombat(战斗) RLItemPolicy(用药) RLAutoPilot(ONNX推理) RLGuiSmoke(无头托管冒烟)
rl-train/         Python：spd_v2(模型) train_bc2(训练) eval_policy/explore_report(评估)
                  export_onnx(导出) RLGenDataBin(数据生成,Java)
src-v4-decompiled/ 游戏反编译源码 + 我们的改造(Hero/Toolbar 托管注入)
lib/              依赖 jar(仓库不含)
rl-train/out/     .onnx 部署模型(Git LFS)；.bin/.pt 由脚本生成(不入库)
```

---

## License / 许可

GPL-3.0，派生自 [Shattered Pixel Dungeon](https://github.com/00-Evan/shattered-pixel-dungeon)（上游亦 GPL-3.0）。请保留上游版权与许可声明。
