/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class GolemSprite
extends MobSprite {
    private Emitter teleParticles;
    private boolean died = false;

    public GolemSprite() {
        this.texture("sprites/golem.png");
        TextureFilm frames = new TextureFilm(this.texture, 17, 19);
        this.idle = new MovieClip.Animation(4, true);
        this.idle.frames(frames, 0, 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 2, 3, 4, 5);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, 6, 7, 8);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 9, 10, 11, 12, 13);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.teleParticles = this.emitter();
        this.teleParticles.autoKill = false;
        this.teleParticles.pour(ElmoParticle.FACTORY, 0.05f);
        this.teleParticles.on = false;
    }

    @Override
    public void update() {
        super.update();
        if (this.teleParticles != null) {
            this.teleParticles.pos(this);
            this.teleParticles.visible = this.visible;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.teleParticles != null) {
            this.teleParticles.on = false;
        }
    }

    public void teleParticles(boolean value) {
        if (this.teleParticles != null) {
            this.teleParticles.on = value;
        }
    }

    @Override
    public synchronized void play(MovieClip.Animation anim, boolean force) {
        if (this.teleParticles != null) {
            this.teleParticles.on = false;
        }
        super.play(anim, force);
    }

    @Override
    public int blood() {
        return -8359828;
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, 15, this, cell, new Callback(){

            @Override
            public void call() {
                if (GolemSprite.this.ch instanceof Golem) {
                    ((Golem)GolemSprite.this.ch).onZapComplete();
                }
            }
        });
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.die && !this.died) {
            this.died = true;
            this.emitter().burst(ElmoParticle.FACTORY, 4);
        }
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }
}

