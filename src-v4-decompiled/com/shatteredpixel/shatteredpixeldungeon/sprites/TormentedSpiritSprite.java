/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class TormentedSpiritSprite
extends MobSprite {
    public TormentedSpiritSprite() {
        this.texture("sprites/wraith.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 15);
        int c = 9;
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, c + 0, c + 1);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, c + 0, c + 1);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, c + 0, c + 2, c + 3);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, c + 0, c + 4, c + 5, c + 6, c + 7);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -2001010688;
    }
}

