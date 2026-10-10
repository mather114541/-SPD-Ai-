/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GuardSprite
extends MobSprite {
    public GuardSprite() {
        this.texture("sprites/guard.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 2, 3, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 8, 9, 10);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 11, 12, 13, 14);
        this.play(this.idle);
    }

    @Override
    public void play(MovieClip.Animation anim) {
        if (anim == this.die) {
            this.emitter().burst(ShadowParticle.UP, 4);
        }
        super.play(anim);
    }
}

