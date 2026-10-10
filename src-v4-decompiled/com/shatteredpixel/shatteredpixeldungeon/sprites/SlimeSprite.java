/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SlimeSprite
extends MobSprite {
    public SlimeSprite() {
        this.texture("sprites/slime.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 12);
        this.idle = new MovieClip.Animation(3, true);
        this.idle.frames(frames, 0, 1, 1, 0);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 0, 2, 3, 3, 2, 0);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 2, 3, 4, 6, 5);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 0, 5, 6, 7);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -7812028;
    }
}

