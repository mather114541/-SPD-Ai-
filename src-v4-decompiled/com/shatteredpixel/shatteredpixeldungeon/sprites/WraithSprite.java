/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class WraithSprite
extends MobSprite {
    public WraithSprite() {
        this.texture("sprites/wraith.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 15);
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 0, 1);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 0, 1);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, 0, 2, 3);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 0, 4, 5, 6, 7);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -2013265920;
    }
}

