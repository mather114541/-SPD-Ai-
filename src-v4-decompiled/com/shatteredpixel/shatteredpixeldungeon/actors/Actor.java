/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.SparseArray;
import java.util.HashSet;

public abstract class Actor
implements Bundlable {
    public static final float TICK = 1.0f;
    private float time;
    private int id = 0;
    protected static final int VFX_PRIO = 100;
    protected static final int HERO_PRIO = 0;
    protected static final int BLOB_PRIO = -10;
    protected static final int MOB_PRIO = -20;
    protected static final int BUFF_PRIO = -30;
    private static final int DEFAULT = -100;
    protected int actPriority = -100;
    private static final String TIME = "time";
    private static final String ID = "id";
    private static HashSet<Actor> all = new HashSet();
    private static HashSet<Char> chars = new HashSet();
    private static volatile Actor current;
    private static SparseArray<Actor> ids;
    private static int nextID;
    private static float now;
    private static final String NEXTID = "nextid";
    public static boolean keepActorThreadAlive;

    protected abstract boolean act();

    protected void spendConstant(float time) {
        this.time += time;
        float ex = Math.abs(this.time % 1.0f);
        if (ex < 0.001f) {
            this.time = Math.round(this.time);
        }
    }

    protected void spend(float time) {
        this.spendConstant(time);
    }

    public void spendToWhole() {
        this.time = (float)Math.ceil(this.time);
    }

    protected void postpone(float time) {
        if (this.time < now + time) {
            this.time = now + time;
            float ex = Math.abs(this.time % 1.0f);
            if (ex < 0.001f) {
                this.time = Math.round(this.time);
            }
        }
    }

    public float cooldown() {
        return this.time - now;
    }

    public void clearTime() {
        this.spendConstant(-Actor.now());
        if (this instanceof Char) {
            for (Buff b : ((Char)this).buffs()) {
                b.spendConstant(-Actor.now());
            }
        }
    }

    public void timeToNow() {
        this.time = now;
    }

    public void fixTime(float decrement) {
        this.time -= decrement;
    }

    protected void diactivate() {
        this.time = Float.MAX_VALUE;
    }

    protected void onAdd() {
    }

    protected void onRemove() {
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(TIME, this.time);
        bundle.put(ID, this.id);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.time = bundle.getFloat(TIME);
        int incomingID = bundle.getInt(ID);
        this.id = Actor.findById(incomingID) == null ? incomingID : nextID++;
    }

    public int id() {
        if (this.id > 0) {
            return this.id;
        }
        this.id = nextID++;
        return this.id;
    }

    public static float now() {
        return now;
    }

    public static synchronized void clear() {
        now = 0.0f;
        all.clear();
        chars.clear();
        ids.clear();
    }

    public static synchronized void fixTime() {
        if (all.isEmpty()) {
            return;
        }
        float min = Float.MAX_VALUE;
        for (Actor a : all) {
            if (!(a.time < min)) continue;
            min = a.time;
        }
        min = (int)min;
        for (Actor a : all) {
            a.fixTime(min);
        }
        TargetedCell.fixTime(min);
        if (Dungeon.hero != null && all.contains(Dungeon.hero)) {
            Statistics.duration += min;
        }
        now -= min;
    }

    public static void init() {
        Actor.add(Dungeon.hero);
        for (Mob mob : Dungeon.level.mobs) {
            Actor.add(mob);
        }
        for (Mob mob : Dungeon.level.mobs) {
            mob.restoreEnemy();
        }
        for (Blob blob : Dungeon.level.blobs.values()) {
            Actor.add(blob);
        }
        current = null;
    }

    public static void storeNextID(Bundle bundle) {
        bundle.put(NEXTID, nextID);
    }

    public static void restoreNextID(Bundle bundle) {
        nextID = bundle.getInt(NEXTID);
    }

    public static void resetNextID() {
        nextID = 1;
    }

    public void next() {
        if (current == this) {
            current = null;
        }
    }

    public static boolean processing() {
        return current != null;
    }

    public static int curActorPriority() {
        return current != null ? Actor.current.actPriority : 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    /** actor 线程引用（process() 启动时登记），供外部主动唤醒（例如开启 AI 托管时）。 */
    public static Thread actorThreadRef = null;

    /** 主动唤醒沉睡中的 actor 线程。托管开启后必须调用，否则英雄空闲时线程一直 wait()，要点一下"等待"键才会跑。 */
    public static void wakeActorThread() {
        Thread t = actorThreadRef;
        if (t != null) {
            synchronized (t) {
                t.notify();
            }
        }
    }

    public static void process() {
        actorThreadRef = Thread.currentThread();
        boolean interrupted = false;
        do {
            boolean doNext;
            block20: {
                block19: {
                    Object object;
                    current = null;
                    if (!interrupted && !Game.switchingScene()) {
                        float earliest = Float.MAX_VALUE;
                        object = Actor.class;
                        // MONITORENTER : com.shatteredpixel.shatteredpixeldungeon.actors.Actor.class
                        for (Actor actor : all) {
                            if (!(actor.time < earliest) && (actor.time != earliest || current != null && actor.actPriority <= Actor.current.actPriority)) continue;
                            earliest = actor.time;
                            current = actor;
                        }
                        // MONITOREXIT : object
                    }
                    if (current == null) break block19;
                    now = Actor.current.time;
                    Actor acting = current;
                    if (acting instanceof Char && ((Char)acting).sprite != null) {
                        try {
                            object = ((Char)acting).sprite;
                            // MONITORENTER : object（反编译丢 synchronized；补回，否则 sprite.wait() 抛 IllegalMonitorStateException）
                            synchronized (object) {
                                if (((Char)acting).sprite.isMoving) {
                                    ((Char)acting).sprite.wait();
                                }
                            }
                            // MONITOREXIT : object
                        }
                        catch (InterruptedException e) {
                            interrupted = true;
                        }
                    }
                    boolean bl = interrupted = interrupted || Thread.interrupted();
                    if (interrupted) {
                        doNext = false;
                        current = null;
                        break block20;
                    } else {
                        try {
                            doNext = acting.act();
                        } catch (Throwable t) {
                            // 关键：单个 actor 抛异常绝不能打死 actor 线程。
                            // 线程一死，GameScene 与它握手的 wait() 永远超时 -> GL 线程冻结（按钮点不动"无法暂停"）-> 游戏退出。
                            System.out.println("[GAME] actor.act() 异常已隔离: " + t);
                            t.printStackTrace();
                            doNext = false;
                        }
                        if (doNext && (Dungeon.hero == null || !Dungeon.hero.isAlive())) {
                            doNext = false;
                            current = null;
                        }
                    }
                    break block20;
                }
                doNext = false;
            }
            if (doNext) continue;
            Thread thread = Thread.currentThread();
            // MONITORENTER : thread（反编译丢了 synchronized 块；补回以持有线程对象锁，否则 notify() 抛 IllegalMonitorStateException）
            synchronized (Thread.currentThread()) {
                boolean bl = interrupted = interrupted || Thread.interrupted();
                if (interrupted) {
                    current = null;
                    interrupted = false;
                }
                Thread.currentThread().notify();
                try {
                    if (rl.RLAutoPilot.active) {
                        // 托管：限时等待 33ms。既参与 notify/wait 握手（GameScene 切场景/销毁时 actorThread.wait 能正常返回，
                        // 不会 timeout waiting for actor thread），又能自动醒来继续驱动 AI，无需玩家按键唤醒。
                        Thread.currentThread().wait(33L);
                    } else {
                        Thread.currentThread().wait();
                    }
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
            }
        } while (keepActorThreadAlive);
    }

    /**
     * RL 环境专用：同步推进一个完整回合（不阻塞、无 UI 依赖）。
     * 持续让时间最小的 actor 行动，直到没有 actor 需要继续（通常为英雄已 ready）。
     */
    public static void processTurn() {
        if (rl.RLMode.headless) {
            while (true) {
                current = null;
                float earliest = Float.MAX_VALUE;
                for (Actor actor : all) {
                    if (actor.time < earliest || (actor.time == earliest && (current == null || actor.actPriority > current.actPriority))) {
                        earliest = actor.time;
                        current = actor;
                    }
                }
                if (current == null) break;
                now = current.time;
                Actor acting = current;
                boolean doNext;
                try {
                    doNext = acting.act();
                } catch (Throwable t) {
                    // headless 下怪物 sprite/UI 调用可能 NPE：跳过该 actor 并推进其时间，避免进程崩溃与死循环
                    System.err.println("[RL] act() exception on " + acting.getClass().getSimpleName() + ": " + t);
                    StackTraceElement[] stes = t.getStackTrace();
                    for (int si = 0; si < Math.min(8, stes.length); si++) System.err.println("    at " + stes[si]);
                    acting.time += 1.0f;
                    if (Dungeon.hero == null || !Dungeon.hero.isAlive()) break;
                    if (acting == Dungeon.hero) break;
                    continue;
                }
                if (Dungeon.hero == null || !Dungeon.hero.isAlive()) break;
                // 仅当轮到英雄且英雄不再需要行动（已 ready）时结束本回合
                if (acting == Dungeon.hero && !doNext) break;
            }
            current = null;
        } else {
            process();
        }
    }

    public static void add(Actor actor) {
        Actor.add(actor, now);
    }

    public static void addDelayed(Actor actor, float delay) {
        Actor.add(actor, now + Math.max(delay, 0.0f));
    }

    private static synchronized void add(Actor actor, float time) {
        if (all.contains(actor)) {
            return;
        }
        ids.put(actor.id(), actor);
        all.add(actor);
        actor.time += time;
        actor.onAdd();
        if (actor instanceof Char) {
            Char ch = (Char)actor;
            chars.add(ch);
            for (Buff buff : ch.buffs()) {
                Actor.add(buff);
            }
        }
    }

    public static synchronized void remove(Actor actor) {
        if (actor != null) {
            all.remove(actor);
            chars.remove(actor);
            actor.onRemove();
            if (actor.id > 0) {
                ids.remove(actor.id);
            }
        }
    }

    public static void delayChar(Char ch, float time) {
        ch.spendConstant(time);
        for (Buff b : ch.buffs()) {
            b.spendConstant(time);
        }
    }

    public static synchronized Char findChar(int pos) {
        for (Char ch : chars) {
            if (ch.pos != pos) continue;
            return ch;
        }
        return null;
    }

    public static synchronized Actor findById(int id) {
        return (Actor)ids.get(id);
    }

    public static synchronized HashSet<Actor> all() {
        return new HashSet<Actor>(all);
    }

    public static synchronized HashSet<Char> chars() {
        return new HashSet<Char>(chars);
    }

    static {
        ids = new SparseArray();
        nextID = 1;
        now = 0.0f;
        keepActorThreadAlive = true;
    }
}

