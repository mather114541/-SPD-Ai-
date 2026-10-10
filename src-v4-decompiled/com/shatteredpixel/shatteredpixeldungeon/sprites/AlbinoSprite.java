/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class AlbinoSprite
extends MobSprite {
    public AlbinoSprite() {
        this.texture("sprites/rat.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 16, 16, 16, 17);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 22, 23, 24, 25, 26);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 18, 19, 20, 21);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 27, 28, 29, 30);
        this.play(this.idle);
    }
}

