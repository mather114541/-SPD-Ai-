/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class BeeSprite
extends MobSprite {
    public BeeSprite() {
        this.texture("sprites/bee.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 0, 1, 1, 0, 2, 2);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 0, 1, 1, 0, 2, 2);
        this.attack = new MovieClip.Animation(20, false);
        this.attack.frames(frames, 3, 4, 5, 6);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(frames, 7, 8, 9, 10);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return 16766208;
    }
}

