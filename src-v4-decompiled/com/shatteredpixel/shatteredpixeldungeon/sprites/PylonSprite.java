/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;

public class PylonSprite
extends MobSprite {
    private MovieClip.Animation activeIdle;

    public PylonSprite() {
        this.perspectiveRaise = 0.3125f;
        this.renderShadow = false;
        this.texture("sprites/pylon.png");
        TextureFilm frames = new TextureFilm(this.texture, 10, 20);
        this.idle = new MovieClip.Animation(1, false);
        this.idle.frames(frames, 0);
        this.activeIdle = new MovieClip.Animation(1, false);
        this.activeIdle.frames(frames, 1);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = new MovieClip.Animation(1, false);
        this.die.frames(frames, 2);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof Pylon && ch.alignment == Char.Alignment.ENEMY) {
            this.activate();
        }
        this.renderShadow = false;
    }

    @Override
    public void place(int cell) {
        if (this.parent != null) {
            this.parent.bringToFront(this);
        }
        super.place(cell);
    }

    public void activate() {
        this.idle = this.activeIdle.clone();
        this.idle();
    }

    @Override
    public void play(MovieClip.Animation anim) {
        if (anim == this.die) {
            this.turnTo(this.ch.pos, this.ch.pos + 1);
            this.emitter().burst(BlastParticle.FACTORY, 20);
            Sample.INSTANCE.play("sounds/blast.mp3");
        }
        super.play(anim);
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.attack) {
            this.flash();
        }
        super.onComplete(anim);
    }

    @Override
    public int blood() {
        return -120;
    }
}

