# -*- coding: utf-8 -*-
import io
p = r"docs\01-环境封装设计.md"
s = io.open(p, encoding='utf-8').read()
add = """

---

## 六、第一阶段实施记录（已完成 ✅）

> 本阶段目标：把游戏改造成可被外部驱动的 Gymnasium 风格环境。**已全部实现并验证。**

### 完成内容
1. **源码与依赖**：反编译用户 jar（v4.0.0，CFR 0.152）得 2902 个 Java 文件；补 `gdx-backend-headless-1.14.0.jar` 提供 Gdx 桩。
2. **无头化改造**（修改以下类，均有 `.bak` 备份）：
   - `GameScene.java`：45 个 void + 4 个 boolean 静态方法批量加 `if (rl.RLMode.headless) return[ false]`
   - `Mob/Hero/Char`：单行 sprite 调用批量包裹 `if (xxx.sprite != null)`
   - `Hero.java`：`ready()/speed()/interrupt()` 里 UI 调用加 headless 保护（`QuickSlotButton`、`GameScene.resetKeyHold`）
   - `Level.java`：`activateTransition()` 加 headless 分支，直接执行换层（绕过 InterlevelScene 场景系统）
   - 修复 CFR 反编译泛型退化共 9 处（Char×3、Mob×3、Level×3）
3. **同步回合推进**：`Actor` 新增 `processTurn()`——不阻塞、无 UI，循环推进到英雄 ready 或死亡。

### 新增 RL 代码（`rl-src/rl/`）
| 文件 | 作用 |
|---|---|
| `RLMode.java` | `headless=true` 全局开关 |
| `RLController.java` | Gymnasium 接口：`reset(seed)` / `step(action)`，含观察/奖励/终止 |
| `RLCli.java` | CLI 入口：自测模式 + `proto` 协议模式（stdin/stdout，供 Python 驱动） |
| `RLSpike.java` | 早期 spike 验证（已被 RLController 覆盖） |
| `TestTransition.java` | 定向下楼换层测试（含 debug 诊断） |

### 环境规格
- **动作**：9 离散。0-7 = 点击 8 方向相邻格（`Hero.handle` 自动判定移动/攻击/拾取/开门/上下楼）；8 = 点击自身（等待/拾取/上下楼）
- **观察**（定长 int[]，len = 3×地图格数 + 7）：`地形 lv.map` + `实体编码(0空/1怪/2物品/3陷阱/4楼梯)` + `视野 heroFOV` + `元信息[depth,pos,HP,HT,STR,lvl,gold]`
- **奖励**（稀疏）：换层 +1、击杀 +1、受伤 −1、死亡 −1（终止）
- **终止**：英雄死亡 或 达到目标层（默认 3）

### 验证结果（关键输出）
- 无头建局：`LEVEL OK len=1591 w=37 h=43 depth=1 mobs=11`
- 移动+完整回合：`after turn: pos=240 ready=true hp=20/20`
- 换层（processTurn 路径，清怪后）：`depth=2 pos=533 r=1` ✅
- Python 协议驱动 10 步：每次 `OK len=5056 ... reward=.. done=..` ✅

### 关键结论 / 陷阱
- **换层不走场景系统**：`Level.activateTransition` 原本切到 `InterlevelScene`（headless 下不驱动）→ 改为 headless 直接 `depth++ + newLevel + switchLevel`。
- **怪物会打断英雄换层**：teleport 到楼梯被怪发现时，怪在回合里 `hero.interrupt()` 清掉换层动作——这是**正常游戏逻辑**（真实玩家同理），非缺陷。
- **classpath 顺序**：`build\\classes` 必须在 jar 之前，否则取到 jar 旧类。
- **编译反编译源码**会暴露额外泛型退化，按已修模式处理。

### 下一步（第二阶段候选）
特征编码（背包物品向量、装备/状态 buff 编码、奖励塑形如引导向楼梯）、更细粒度动作，或直接先跑一个最小的 DQN/PG 基线验证环境可用性。
"""
io.open(p, 'w', encoding='utf-8', newline='\n').write(s.rstrip('\n') + '\n' + add.lstrip('\n') + '\n')
print('appended ok, final chars =', len(io.open(p, encoding='utf-8').read()))
