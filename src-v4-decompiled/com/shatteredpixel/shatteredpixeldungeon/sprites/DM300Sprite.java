/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;

public class DM300Sprite
extends MobSprite {
    private MovieClip.Animation charge;
    private MovieClip.Animation slam;
    private Emitter superchargeSparks;

    public DM300Sprite() {
        this.texture("sprites/dm300.png");
        this.updateChargeState(false);
    }

    public void updateChargeState(boolean enraged) {
        if (this.superchargeSparks != null) {
            this.superchargeSparks.on = enraged;
        }
        int c = enraged ? 10 : 0;
        TextureFilm frames = new TextureFilm(this.texture, 25, 22);
        this.idle = new MovieClip.Animation(enraged ? 15 : 10, true);
        this.idle.frames(frames, c + 0, c + 1);
        this.run = new MovieClip.Animation(enraged ? 15 : 10, true);
        this.run.frames(frames, c + 0, c + 2);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, c + 3, c + 4, c + 5);
        if (this.charge == null) {
            this.charge = new MovieClip.Animation(4, true);
            this.charge.frames(frames, 0, 10);
            this.slam = this.attack.clone();
            this.zap = new MovieClip.Animation(15, false);
            this.zap.frames(frames, 6, 7, 7, 6);
            this.die = new MovieClip.Animation(20, false);
            this.die.frames(frames, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10, 0, 10);
        }
        if (this.curAnim != this.charge) {
            this.play(this.idle);
        }
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, 1107, this, cell, new Callback(){

            @Override
            public void call() {
                ((DM300)DM300Sprite.this.ch).onZapComplete();
            }
        });
        Sample.INSTANCE.play("sounds/gas.mp3");
    }

    public void charge() {
        this.play(this.charge);
    }

    public void slam(int cell) {
        this.turnTo(this.ch.pos, cell);
        this.play(this.slam);
        Sample.INSTANCE.play("sounds/rocks.mp3");
        PixelScene.shake(3.0f, 0.7f);
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap || anim == this.slam) {
            this.idle();
        }
        if (anim == this.slam) {
            ((DM300)this.ch).onSlamComplete();
        }
        super.onComplete(anim);
        if (anim == this.die) {
            Sample.INSTANCE.play("sounds/blast.mp3");
            this.emitter().burst(BlastParticle.FACTORY, 100);
            this.killAndErase();
        }
    }

    @Override
    public void place(int cell) {
        if (this.parent != null) {
            this.parent.bringToFront(this);
        }
        super.place(cell);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.superchargeSparks = this.emitter();
        this.superchargeSparks.autoKill = false;
        this.superchargeSparks.pour(SparkParticle.STATIC, 0.05f);
        this.superchargeSparks.on = false;
        if (ch instanceof DM300 && ((DM300)ch).isSupercharged()) {
            this.updateChargeState(true);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.superchargeSparks != null) {
            this.superchargeSparks.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.superchargeSparks != null) {
            this.superchargeSparks.on = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.superchargeSparks != null) {
            this.superchargeSparks.killAndErase();
        }
    }

    @Override
    public int blood() {
        return -120;
    }
}

