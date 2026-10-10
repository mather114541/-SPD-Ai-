/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GreatCrabSprite
extends MobSprite {
    public GreatCrabSprite() {
        this.texture("sprites/crab.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        int c = 32;
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 0 + c, 1 + c, 0 + c, 2 + c);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 3 + c, 4 + c, 5 + c, 6 + c);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 7 + c, 8 + c, 9 + c);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 10 + c, 11 + c, 12 + c, 13 + c);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -5504;
    }
}

