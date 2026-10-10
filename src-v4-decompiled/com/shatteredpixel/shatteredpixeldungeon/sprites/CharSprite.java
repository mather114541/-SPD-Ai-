/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.DarkBlock;
import com.shatteredpixel.shatteredpixeldungeon.effects.EmoIcon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.GlowBlock;
import com.shatteredpixel.shatteredpixeldungeon.effects.IceBlock;
import com.shatteredpixel.shatteredpixeldungeon.effects.ShieldHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.TorchHalo;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.CharHealthIndicator;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.PosTweener;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.nio.Buffer;
import java.util.HashSet;

public class CharSprite
extends MovieClip
implements Tweener.Listener,
MovieClip.Listener {
    public static final int DEFAULT = 0xFFFFFF;
    public static final int POSITIVE = 65280;
    public static final int NEGATIVE = 0xFF0000;
    public static final int WARNING = 0xFF8800;
    public static final int NEUTRAL = 0xFFFF00;
    public static final float DEFAULT_MOVE_INTERVAL = 0.1f;
    private static float moveInterval = 0.1f;
    private static final float FLASH_INTERVAL = 0.05f;
    protected float perspectiveRaise = 0.375f;
    protected boolean renderShadow = false;
    protected float shadowWidth = 1.2f;
    protected float shadowHeight = 0.25f;
    protected float shadowOffset = 0.25f;
    public boolean visibleOutOfFFOV = false;
    protected MovieClip.Animation idle;
    protected MovieClip.Animation run;
    protected MovieClip.Animation attack;
    protected MovieClip.Animation operate;
    protected MovieClip.Animation zap;
    protected MovieClip.Animation die;
    protected Callback animCallback;
    protected PosTweener motion;
    protected Emitter burning;
    protected Emitter chilled;
    protected Emitter marked;
    protected Emitter levitation;
    protected Emitter healing;
    protected Emitter hearts;
    protected IceBlock iceBlock;
    protected DarkBlock darkBlock;
    protected GlowBlock glowBlock;
    protected TorchHalo light;
    protected ShieldHalo shield;
    protected AlphaTweener invisible;
    protected Flare aura;
    protected EmoIcon emo;
    protected CharHealthIndicator health;
    private Tweener jumpTweener;
    private Callback jumpCallback;
    protected float flashTime = 0.0f;
    protected boolean sleeping = false;
    public Char ch;
    public volatile boolean isMoving = false;
    private final HashSet<State> stateAdditions = new HashSet();
    private int auraColor = 0;
    private int auraRays = 0;
    private final HashSet<State> stateRemovals = new HashSet();
    private float[] shadowMatrix = new float[16];

    public CharSprite() {
        this.listener = this;
    }

    @Override
    public void play(MovieClip.Animation anim) {
        if (this.curAnim == null || this.curAnim != this.die) {
            super.play(anim);
        }
    }

    public void link(Char ch) {
        this.linkVisuals(ch);
        this.ch = ch;
        ch.sprite = this;
        this.place(ch.pos);
        this.turnTo(ch.pos, Random.Int(Dungeon.level.length()));
        this.renderShadow = true;
        if (ch != Dungeon.hero) {
            if (this.health == null) {
                this.health = new CharHealthIndicator(ch);
            } else {
                this.health.target(ch);
            }
        }
        ch.updateSpriteState();
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.ch != null && this.ch.sprite == this) {
            this.ch.sprite = null;
        }
    }

    public void linkVisuals(Char ch) {
    }

    public PointF worldToCamera(int cell) {
        int csize = 16;
        return new PointF(PixelScene.align(Camera.main, ((float)(cell % Dungeon.level.width()) + 0.5f) * 16.0f - this.width() * 0.5f), PixelScene.align(Camera.main, ((float)(cell / Dungeon.level.width()) + 1.0f) * 16.0f - this.height() - 16.0f * this.perspectiveRaise));
    }

    public void place(int cell) {
        this.point(this.worldToCamera(cell));
    }

    public void showStatus(int color, String text, Object ... args) {
        this.showStatusWithIcon(color, text, FloatingText.NO_ICON, args);
    }

    public void showStatusWithIcon(int color, String text, int icon, Object ... args) {
        if (this.visible) {
            if (args.length > 0) {
                text = Messages.format(text, args);
            }
            float x = this.destinationCenter().x;
            float y = this.destinationCenter().y - this.height() / 2.0f;
            int pos = DungeonTilemap.worldToTile(x, y + this.height(), Dungeon.level.width());
            if (this.ch != null) {
                FloatingText.show(x, y, pos, text, color, icon, true);
            } else {
                FloatingText.show(x, y, -1, text, color, icon, true);
            }
        }
    }

    public void idle() {
        this.play(this.idle);
    }

    public void move(int from, int to) {
        this.turnTo(from, to);
        this.play(this.run);
        this.motion = new PosTweener(this, this.worldToCamera(to), moveInterval);
        this.motion.listener = this;
        this.parent.add(this.motion);
        this.isMoving = true;
        if (this.visible && Dungeon.level.water[from] && !this.ch.flying) {
            GameScene.ripple(from);
        }
    }

    public static void setMoveInterval(float interval) {
        moveInterval = interval;
    }

    public PointF destinationCenter() {
        PosTweener motion = this.motion;
        if (motion != null && motion.elapsed >= 0.0f) {
            return new PointF(motion.end.x + this.width() / 2.0f, motion.end.y + this.height() / 2.0f);
        }
        return this.center();
    }

    public void interruptMotion() {
        if (this.motion != null) {
            this.motion.stop(false);
        }
    }

    public void attack(int cell) {
        this.attack(cell, null);
    }

    public synchronized void attack(int cell, Callback callback) {
        this.animCallback = callback;
        this.turnTo(this.ch.pos, cell);
        this.play(this.attack);
    }

    public void operate(int cell) {
        this.operate(cell, null);
    }

    public synchronized void operate(int cell, Callback callback) {
        this.animCallback = callback;
        this.turnTo(this.ch.pos, cell);
        this.play(this.operate);
    }

    public void zap(int cell) {
        this.zap(cell, null);
    }

    public synchronized void zap(int cell, Callback callback) {
        this.animCallback = callback;
        this.turnTo(this.ch.pos, cell);
        this.play(this.zap);
    }

    public void turnTo(int from, int to) {
        int fx = from % Dungeon.level.width();
        int tx = to % Dungeon.level.width();
        if (tx > fx) {
            this.flipHorizontal = false;
        } else if (tx < fx) {
            this.flipHorizontal = true;
        }
    }

    public void jump(int from, int to, Callback callback) {
        float distance = Math.max(1.0f, Dungeon.level.trueDistance(from, to));
        this.jump(from, to, distance * 2.0f, distance * 0.1f, callback);
    }

    public void jump(int from, int to, float height, float duration, Callback callback) {
        this.jumpCallback = callback;
        this.jumpTweener = new JumpTweener(this, this.worldToCamera(to), height, duration);
        this.jumpTweener.listener = this;
        this.parent.add(this.jumpTweener);
        this.turnTo(from, to);
    }

    public void die() {
        this.sleeping = false;
        this.processStateRemoval(State.PARALYSED);
        this.play(this.die);
        this.hideEmo();
        if (this.health != null) {
            this.health.killAndErase();
        }
    }

    public Emitter emitter() {
        Emitter emitter = GameScene.emitter();
        if (emitter != null) {
            emitter.pos(this);
        }
        return emitter;
    }

    public Emitter centerEmitter() {
        Emitter emitter = GameScene.emitter();
        if (emitter != null) {
            emitter.pos(this.center());
        }
        return emitter;
    }

    public Emitter bottomEmitter() {
        Emitter emitter = GameScene.emitter();
        if (emitter != null) {
            emitter.pos(this.x, this.y + this.height, this.width, 0.0f);
        }
        return emitter;
    }

    public void burst(int color, int n) {
        if (this.visible) {
            Splash.at(this.center(), color, n);
        }
    }

    public void bloodBurstA(PointF from, int damage) {
        if (this.visible) {
            PointF c = this.center();
            int n = (int)Math.min(9.0 * Math.sqrt((double)damage / (double)this.ch.HT), 9.0);
            Splash.at(c, PointF.angle(from, c), 1.5707963f, this.blood(), n);
        }
    }

    public int blood() {
        return -4521984;
    }

    public void flash() {
        this.ga = 1.0f;
        this.ba = 1.0f;
        this.ra = 1.0f;
        this.flashTime = 0.05f;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void add(State state) {
        if (state == State.PARALYSED) {
            this.paused = true;
            return;
        }
        Class<State> clazz = State.class;
        synchronized (State.class) {
            this.stateRemovals.remove((Object)state);
            this.stateAdditions.add(state);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public void aura(int color, int nRays) {
        this.add(State.AURA);
        this.auraColor = color;
        this.auraRays = nRays;
    }

    protected synchronized void processStateAddition(State state) {
        switch (state.ordinal()) {
            case 0: {
                if (this.burning != null) {
                    this.burning.on = false;
                }
                this.burning = this.emitter();
                this.burning.pour(FlameParticle.FACTORY, 0.06f);
                if (!this.visible) break;
                Sample.INSTANCE.play("sounds/burning.mp3");
                break;
            }
            case 1: {
                if (this.levitation != null) {
                    this.levitation.on = false;
                }
                this.levitation = this.emitter();
                this.levitation.pour(Speck.factory(106), 0.02f);
                break;
            }
            case 2: {
                if (this.invisible != null) {
                    this.invisible.killAndErase();
                }
                this.invisible = new AlphaTweener(this, 0.4f, 0.4f);
                if (this.parent != null) {
                    this.parent.add(this.invisible);
                    break;
                }
                this.alpha(0.4f);
                break;
            }
            case 3: {
                this.paused = true;
                break;
            }
            case 4: {
                if (this.iceBlock != null) {
                    this.iceBlock.killAndErase();
                }
                this.iceBlock = IceBlock.freeze(this);
                break;
            }
            case 5: {
                if (this.light != null) {
                    this.light.putOut();
                }
                this.light = new TorchHalo(this);
                GameScene.effect(this.light);
                break;
            }
            case 6: {
                if (this.chilled != null) {
                    this.chilled.on = false;
                }
                this.chilled = this.emitter();
                this.chilled.pour(SnowParticle.FACTORY, 0.1f);
                break;
            }
            case 7: {
                if (this.darkBlock != null) {
                    this.darkBlock.killAndErase();
                }
                this.darkBlock = DarkBlock.darken(this);
                break;
            }
            case 8: {
                if (this.marked != null) {
                    this.marked.on = false;
                }
                this.marked = this.emitter();
                this.marked.pour(ShadowParticle.UP, 0.1f);
                break;
            }
            case 9: {
                if (this.healing != null) {
                    this.healing.on = false;
                }
                this.healing = this.emitter();
                this.healing.pour(Speck.factory(0), 0.5f);
                break;
            }
            case 10: {
                if (this.shield != null) {
                    this.shield.killAndErase();
                }
                this.shield = new ShieldHalo(this);
                GameScene.effect(this.shield);
                break;
            }
            case 11: {
                if (this.hearts != null) {
                    this.hearts.on = false;
                }
                this.hearts = this.emitter();
                this.hearts.pour(Speck.factory(11), 0.5f);
                break;
            }
            case 12: {
                if (this.glowBlock != null) {
                    this.glowBlock.killAndErase();
                }
                this.glowBlock = GlowBlock.lighten(this);
                break;
            }
            case 13: {
                if (this.aura != null) {
                    this.aura.killAndErase();
                }
                float size = Math.max(this.width(), this.height());
                size = Math.max(size + 4.0f, 16.0f);
                this.aura = new Flare(this.auraRays, size);
                this.aura.angularSpeed = 90.0f;
                this.aura.color(this.auraColor, true);
                this.aura.visible = this.visible;
                if (this.parent == null) break;
                this.aura.show(this, 0.0f);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void remove(State state) {
        if (state == State.PARALYSED) {
            this.paused = false;
            return;
        }
        Class<State> clazz = State.class;
        synchronized (State.class) {
            this.stateAdditions.remove((Object)state);
            this.stateRemovals.add(state);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public void clearAura() {
        this.remove(State.AURA);
    }

    protected synchronized void processStateRemoval(State state) {
        switch (state.ordinal()) {
            case 0: {
                if (this.burning == null) break;
                this.burning.on = false;
                this.burning = null;
                break;
            }
            case 1: {
                if (this.levitation == null) break;
                this.levitation.on = false;
                this.levitation = null;
                break;
            }
            case 2: {
                if (this.invisible != null) {
                    this.invisible.killAndErase();
                    this.invisible = null;
                }
                this.alpha(1.0f);
                break;
            }
            case 3: {
                this.paused = false;
                break;
            }
            case 4: {
                if (this.iceBlock == null) break;
                this.iceBlock.melt();
                this.iceBlock = null;
                break;
            }
            case 5: {
                if (this.light == null) break;
                this.light.putOut();
                this.light = null;
                break;
            }
            case 6: {
                if (this.chilled == null) break;
                this.chilled.on = false;
                this.chilled = null;
                break;
            }
            case 7: {
                if (this.darkBlock == null) break;
                this.darkBlock.lighten();
                this.darkBlock = null;
                break;
            }
            case 8: {
                if (this.marked == null) break;
                this.marked.on = false;
                this.marked = null;
                break;
            }
            case 9: {
                if (this.healing == null) break;
                this.healing.on = false;
                this.healing = null;
                break;
            }
            case 10: {
                if (this.shield == null) break;
                this.shield.putOut();
                break;
            }
            case 11: {
                if (this.hearts == null) break;
                this.hearts.on = false;
                this.hearts = null;
                break;
            }
            case 12: {
                if (this.glowBlock == null) break;
                this.glowBlock.darken();
                this.glowBlock = null;
                break;
            }
            case 13: {
                if (this.aura == null) break;
                this.aura.killAndErase();
                this.aura = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update() {
        if (this.paused && this.ch != null && this.curAnim != null && !this.curAnim.looped && !this.finished) {
            this.listener.onComplete(this.curAnim);
            this.finished = true;
        }
        super.update();
        if (this.flashTime > 0.0f) {
            float f;
            this.flashTime -= Game.elapsed;
            if (f <= 0.0f) {
                this.resetColor();
            }
        }
        Class clazz = State.class;
        synchronized (State.class) {
            for (State s : this.stateAdditions) {
                this.processStateAddition(s);
            }
            this.stateAdditions.clear();
            for (State s : this.stateRemovals) {
                this.processStateRemoval(s);
            }
            this.stateRemovals.clear();
            // ** MonitorExit[var1_1 /* !! */ ] (shouldn't be in output)
            if (this.burning != null) {
                this.burning.visible = this.visible;
            }
            if (this.levitation != null) {
                this.levitation.visible = this.visible;
            }
            if (this.iceBlock != null) {
                this.iceBlock.visible = this.visible;
            }
            if (this.light != null) {
                this.light.visible = this.visible;
            }
            if (this.chilled != null) {
                this.chilled.visible = this.visible;
            }
            if (this.darkBlock != null) {
                this.darkBlock.visible = this.visible;
            }
            if (this.marked != null) {
                this.marked.visible = this.visible;
            }
            if (this.healing != null) {
                this.healing.visible = this.visible;
            }
            if (this.hearts != null) {
                this.hearts.visible = this.visible;
            }
            if (this.aura != null) {
                if (this.aura.parent == null) {
                    this.aura.show(this, 0.0f);
                }
                this.aura.visible = this.visible;
                this.aura.point(this.center());
            }
            if (this.glowBlock != null) {
                this.glowBlock.visible = this.visible;
            }
            if (this.sleeping) {
                this.showSleep();
            } else {
                this.hideSleep();
            }
            clazz = EmoIcon.class;
            synchronized (EmoIcon.class) {
                if (this.emo != null && this.emo.alive) {
                    this.emo.visible = this.visible;
                }
                // ** MonitorExit[var1_1 /* !! */ ] (shouldn't be in output)
                return;
            }
        }
    }

    @Override
    public void resetColor() {
        super.resetColor();
        if (this.invisible != null) {
            this.alpha(0.4f);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showSleep() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (!(this.emo instanceof EmoIcon.Sleep)) {
                if (this.emo != null) {
                    this.emo.killAndErase();
                }
                this.emo = new EmoIcon.Sleep(this);
                this.emo.visible = this.visible;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            this.idle();
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideSleep() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (this.emo instanceof EmoIcon.Sleep) {
                this.emo.killAndErase();
                this.emo = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showAlert() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (!(this.emo instanceof EmoIcon.Alert)) {
                if (this.emo != null) {
                    this.emo.killAndErase();
                }
                this.emo = new EmoIcon.Alert(this);
                this.emo.visible = this.visible;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideAlert() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (this.emo instanceof EmoIcon.Alert) {
                this.emo.killAndErase();
                this.emo = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showInvestigate() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (!(this.emo instanceof EmoIcon.Investigate)) {
                if (this.emo != null) {
                    this.emo.killAndErase();
                }
                this.emo = new EmoIcon.Investigate(this);
                this.emo.visible = this.visible;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideInvestigate() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (this.emo instanceof EmoIcon.Investigate) {
                this.emo.killAndErase();
                this.emo = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void showLost() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (!(this.emo instanceof EmoIcon.Lost)) {
                if (this.emo != null) {
                    this.emo.killAndErase();
                }
                this.emo = new EmoIcon.Lost(this);
                this.emo.visible = this.visible;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideLost() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (this.emo instanceof EmoIcon.Lost) {
                this.emo.killAndErase();
                this.emo = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void hideEmo() {
        Class<EmoIcon> clazz = EmoIcon.class;
        synchronized (EmoIcon.class) {
            if (this.emo != null) {
                this.emo.killAndErase();
                this.emo = null;
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    @Override
    public void kill() {
        super.kill();
        this.hideEmo();
        for (State s : State.values()) {
            this.processStateRemoval(s);
        }
        if (this.health != null) {
            this.health.killAndErase();
        }
    }

    @Override
    protected void updateMatrix() {
        super.updateMatrix();
        Matrix.copy(this.matrix, this.shadowMatrix);
        Matrix.translate(this.shadowMatrix, this.width * (1.0f - this.shadowWidth) / 2.0f, this.height * (1.0f - this.shadowHeight) + this.shadowOffset);
        Matrix.scale(this.shadowMatrix, this.shadowWidth, this.shadowHeight);
    }

    @Override
    public void draw() {
        if (this.texture == null || !this.dirty && this.buffer == null) {
            return;
        }
        if (this.renderShadow) {
            if (this.dirty) {
                ((Buffer)this.verticesBuffer).position(0);
                this.verticesBuffer.put(this.vertices);
                if (this.buffer == null) {
                    this.buffer = new Vertexbuffer(this.verticesBuffer);
                } else {
                    this.buffer.updateVertices(this.verticesBuffer);
                }
                this.dirty = false;
            }
            NoosaScript script = this.script();
            this.texture.bind();
            script.camera(this.camera());
            this.updateMatrix();
            script.uModel.valueM4(this.shadowMatrix);
            script.lighting(0.0f, 0.0f, 0.0f, this.am * 0.6f, 0.0f, 0.0f, 0.0f, this.aa * 0.6f);
            script.drawQuad(this.buffer);
        }
        super.draw();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onComplete(Tweener tweener) {
        if (tweener == this.jumpTweener) {
            if (this.visible && Dungeon.level.water[this.ch.pos] && !this.ch.flying) {
                GameScene.ripple(this.ch.pos);
            }
            if (this.jumpCallback != null) {
                this.jumpCallback.call();
            }
            GameScene.sortMobSprites();
        } else if (tweener == this.motion) {
            CharSprite charSprite = this;
            synchronized (charSprite) {
                this.isMoving = false;
                this.motion.killAndErase();
                this.motion = null;
                this.ch.onMotionComplete();
                GameScene.sortMobSprites();
                this.notifyAll();
            }
        }
    }

    @Override
    public synchronized void onComplete(MovieClip.Animation anim) {
        if (this.animCallback != null) {
            Callback executing = this.animCallback;
            this.animCallback = null;
            executing.call();
        } else if (anim == this.attack) {
            this.idle();
            this.ch.onAttackComplete();
        } else if (anim == this.operate) {
            this.idle();
            this.ch.onOperateComplete();
        }
    }

    private static class JumpTweener
    extends Tweener {
        public CharSprite visual;
        public PointF start;
        public PointF end;
        public float height;

        public JumpTweener(CharSprite visual, PointF pos, float height, float time) {
            super(visual, time);
            this.visual = visual;
            this.start = visual.point();
            this.end = pos;
            this.height = height;
        }

        @Override
        protected void updateValues(float progress) {
            float hVal = -this.height * 4.0f * progress * (1.0f - progress);
            this.visual.point(PointF.inter(this.start, this.end, progress).offset(0.0f, hVal));
            this.visual.shadowOffset = 0.25f - hVal * 0.8f;
        }
    }

    public static enum State {
        BURNING,
        LEVITATING,
        INVISIBLE,
        PARALYSED,
        FROZEN,
        ILLUMINATED,
        CHILLED,
        DARKENED,
        MARKED,
        HEALING,
        SHIELDED,
        HEARTS,
        GLOWING,
        AURA;

    }
}

