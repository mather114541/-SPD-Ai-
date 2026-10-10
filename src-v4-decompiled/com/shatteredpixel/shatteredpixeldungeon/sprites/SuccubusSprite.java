/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SuccubusSprite
extends MobSprite {
    public SuccubusSprite() {
        this.texture("sprites/succubus.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 3, 4, 5, 6, 7, 8);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 9, 10, 11);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 12);
        this.play(this.idle);
    }

    @Override
    public void die() {
        super.die();
        this.emitter().burst(Speck.factory(11), 6);
        this.emitter().burst(ShadowParticle.UP, 8);
    }
}

