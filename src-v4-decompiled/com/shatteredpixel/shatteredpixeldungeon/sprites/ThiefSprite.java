/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class ThiefSprite
extends MobSprite {
    public ThiefSprite() {
        this.texture("sprites/thief.png");
        TextureFilm film = new TextureFilm(this.texture, 12, 13);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(film, 0, 0, 2, 3, 3, 4);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(film, 5, 6, 7, 8, 9);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(film, 10, 11, 12, 0);
        this.idle();
    }
}

