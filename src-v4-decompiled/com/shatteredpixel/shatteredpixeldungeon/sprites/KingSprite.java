/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class KingSprite
extends MobSprite {
    public KingSprite() {
        this.texture("sprites/king.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 3, 4, 5, 6, 7, 8);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 9, 10, 11);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 12, 13, 14, 15);
        this.play(this.idle);
    }
}

