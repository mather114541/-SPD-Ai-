/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class MonkSprite
extends MobSprite {
    private MovieClip.Animation kick;

    public MonkSprite() {
        this.texture("sprites/monk.png");
        TextureFilm frames = new TextureFilm(this.texture, 15, 14);
        this.idle = new MovieClip.Animation(6, true);
        this.idle.frames(frames, 1, 0, 1, 2);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 11, 12, 13, 14, 15, 16);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 3, 4, 3, 4);
        this.kick = new MovieClip.Animation(10, false);
        this.kick.frames(frames, 5, 6, 5);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 1, 7, 8, 8, 9, 10);
        this.play(this.idle);
    }

    @Override
    public void attack(int cell) {
        super.attack(cell);
        if (Random.Float() < 0.5f) {
            this.play(this.kick);
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim == this.kick ? this.attack : anim);
    }
}

