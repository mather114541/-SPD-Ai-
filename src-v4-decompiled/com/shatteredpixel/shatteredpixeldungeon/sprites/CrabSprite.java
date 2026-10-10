/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class CrabSprite
extends MobSprite {
    public CrabSprite() {
        this.texture("sprites/crab.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 0, 1, 0, 2);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 3, 4, 5, 6);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 7, 8, 9);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 10, 11, 12, 13);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -5504;
    }
}

