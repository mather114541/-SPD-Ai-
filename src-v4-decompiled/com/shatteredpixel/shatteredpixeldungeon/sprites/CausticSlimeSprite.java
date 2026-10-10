/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class CausticSlimeSprite
extends MobSprite {
    public CausticSlimeSprite() {
        this.texture("sprites/slime.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 12);
        int c = 9;
        this.idle = new MovieClip.Animation(3, true);
        this.idle.frames(frames, c + 0, c + 1, c + 1, c + 0);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, c + 0, c + 2, c + 3, c + 3, c + 2, c + 0);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, c + 2, c + 3, c + 4, c + 6, c + 5);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, c + 0, c + 5, c + 6, c + 7);
        this.play(this.idle);
    }
}

