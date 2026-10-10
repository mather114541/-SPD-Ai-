/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class ShieldedSprite
extends MobSprite {
    public ShieldedSprite() {
        this.texture("sprites/brute.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 21, 21, 21, 22, 21, 21, 22, 22);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 25, 26, 27, 28);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 23, 24);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 29, 30, 31);
        this.play(this.idle);
    }
}

