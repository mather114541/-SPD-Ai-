/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GnollSprite
extends MobSprite {
    public GnollSprite() {
        this.texture("sprites/gnoll.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 2, 3, 0);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 8, 9, 10);
        this.play(this.idle);
    }
}

