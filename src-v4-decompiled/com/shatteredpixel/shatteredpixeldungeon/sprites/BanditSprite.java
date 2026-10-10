/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class BanditSprite
extends MobSprite {
    public BanditSprite() {
        this.texture("sprites/thief.png");
        TextureFilm film = new TextureFilm(this.texture, 12, 13);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, 21, 21, 21, 22, 21, 21, 21, 21, 22);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(film, 21, 21, 23, 24, 24, 25);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(film, 25, 27, 28, 29, 30);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(film, 31, 32, 33);
        this.idle();
    }
}

