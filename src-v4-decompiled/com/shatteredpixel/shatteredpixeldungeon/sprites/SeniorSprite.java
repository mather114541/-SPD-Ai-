/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class SeniorSprite
extends MobSprite {
    private MovieClip.Animation kick;

    public SeniorSprite() {
        this.texture("sprites/monk.png");
        TextureFilm frames = new TextureFilm(this.texture, 15, 14);
        this.idle = new MovieClip.Animation(6, true);
        this.idle.frames(frames, 18, 17, 18, 19);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 28, 29, 30, 31, 32, 33);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 20, 21, 20, 21);
        this.kick = new MovieClip.Animation(10, false);
        this.kick.frames(frames, 22, 23, 22);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 18, 24, 25, 25, 26, 27);
        this.play(this.idle);
    }

    @Override
    public void attack(int cell) {
        super.attack(cell);
        if (Random.Float() < 0.3f) {
            this.play(this.kick);
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim == this.kick ? this.attack : anim);
    }
}

