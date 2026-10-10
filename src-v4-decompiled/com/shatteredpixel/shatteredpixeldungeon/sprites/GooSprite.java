/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class GooSprite
extends MobSprite {
    private MovieClip.Animation pump;
    private MovieClip.Animation pumpAttack;
    private Emitter spray;
    private int pumpUpEmitterDist = 0;
    private int lastPumpUpPos = -1;
    private ArrayList<Emitter> pumpUpEmitters = new ArrayList();

    public GooSprite() {
        this.texture("sprites/goo.png");
        TextureFilm frames = new TextureFilm(this.texture, 20, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 2, 1, 0, 0, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 3, 2, 1, 2);
        this.pump = new MovieClip.Animation(20, true);
        this.pump.frames(frames, 4, 3, 2, 1, 0);
        this.pumpAttack = new MovieClip.Animation(20, false);
        this.pumpAttack.frames(frames, 4, 3, 2, 1, 0, 7);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, 8, 9, 10);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 5, 6, 7);
        this.play(this.idle);
        this.spray = this.centerEmitter();
        if (this.spray != null) {
            this.spray.autoKill = false;
            this.spray.pour(GooParticle.FACTORY, 0.04f);
            this.spray.on = false;
        }
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch.HP * 2 <= ch.HT) {
            this.spray(true);
        }
    }

    public void pumpUp(int warnDist) {
        this.pumpUpEmitterDist = warnDist;
        if (warnDist > 0) {
            this.play(this.pump);
            Sample.INSTANCE.play("sounds/chargeup.mp3", 1.0f, warnDist == 1 ? 0.8f : 1.0f);
        }
        this.updateEmitters();
    }

    public void updateEmitters() {
        this.clearEmitters();
        if (this.pumpUpEmitterDist > 0 && this.ch != null) {
            this.lastPumpUpPos = this.ch.pos;
            if (this.ch.fieldOfView == null || this.ch.fieldOfView.length != Dungeon.level.length()) {
                this.ch.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(this.ch, this.ch.fieldOfView);
            }
            for (int i = 0; i < Dungeon.level.length(); ++i) {
                if (this.ch.fieldOfView == null || !this.ch.fieldOfView[i] || Dungeon.level.distance(i, this.ch.pos) > this.pumpUpEmitterDist || new Ballistica((int)this.ch.pos, (int)i, (int)13).collisionPos != i || new Ballistica((int)i, (int)this.ch.pos, (int)13).collisionPos != this.ch.pos) continue;
                Emitter e = CellEmitter.get(i);
                e.pour(GooParticle.FACTORY, 0.04f);
                this.pumpUpEmitters.add(e);
            }
        }
    }

    public void clearEmitters() {
        for (Emitter e : this.pumpUpEmitters) {
            e.on = false;
        }
        this.pumpUpEmitters.clear();
    }

    public void triggerEmitters() {
        for (Emitter e : this.pumpUpEmitters) {
            e.burst(ElmoParticle.FACTORY, 10);
        }
        Sample.INSTANCE.play("sounds/burning.mp3");
        this.pumpUpEmitterDist = 0;
        this.pumpUpEmitters.clear();
    }

    public void pumpAttack() {
        this.play(this.pumpAttack);
    }

    @Override
    public void play(MovieClip.Animation anim) {
        if (anim != this.pump && anim != this.pumpAttack) {
            this.pumpUpEmitterDist = 0;
            this.clearEmitters();
        }
        super.play(anim);
    }

    @Override
    public int blood() {
        return -16777216;
    }

    public void spray(boolean on) {
        this.spray.on = on;
    }

    @Override
    public void update() {
        super.update();
        if (this.spray != null) {
            this.spray.pos(this.center());
            this.spray.visible = this.visible;
        }
        if (this.pumpUpEmitterDist > 0 && this.ch != null && this.ch.pos != this.lastPumpUpPos) {
            this.updateEmitters();
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.pumpAttack) {
            this.triggerEmitters();
            this.idle();
            this.ch.onAttackComplete();
        } else if (anim == this.die) {
            this.spray.killAndErase();
        }
    }

    public static class GooParticle
    extends PixelParticle.Shrinking {
        public static final Emitter.Factory FACTORY = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                ((GooParticle)emitter.recycle(GooParticle.class)).reset(x, y);
            }
        };

        public GooParticle() {
            this.color(0);
            this.lifespan = 0.3f;
            this.acc.set(0.0f, 50.0f);
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
            this.size = 4.0f;
            this.speed.polar(-Random.Float(3.1415925f), Random.Float(32.0f, 48.0f));
        }

        @Override
        public void update() {
            super.update();
            float p = this.left / this.lifespan;
            this.am = p > 0.5f ? (1.0f - p) * 2.0f : 1.0f;
        }
    }
}

