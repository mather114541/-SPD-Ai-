/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class RotLasherSprite
extends MobSprite {
    public RotLasherSprite() {
        this.texture("sprites/rot_lasher.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(0, true);
        this.idle.frames(frames, 0);
        this.run = new MovieClip.Animation(0, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(24, false);
        this.attack.frames(frames, 0, 1, 2, 2, 1);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 3, 4, 5, 6);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -7812028;
    }
}

