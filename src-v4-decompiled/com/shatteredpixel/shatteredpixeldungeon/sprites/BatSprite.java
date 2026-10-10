/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class BatSprite
extends MobSprite {
    public BatSprite() {
        this.texture("sprites/bat.png");
        TextureFilm frames = new TextureFilm(this.texture, 15, 15);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 0, 1);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 2, 3, 0, 1);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 4, 5, 6);
        this.play(this.idle);
    }
}

