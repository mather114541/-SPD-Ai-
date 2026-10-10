/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SnakeSprite
extends MobSprite {
    public SnakeSprite() {
        this.texture("sprites/snake.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 11);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 3, 2, 1, 1);
        this.run = new MovieClip.Animation(8, true);
        this.run.frames(frames, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 8, 9, 10, 9, 0);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 11, 12, 13);
        this.play(this.idle);
    }
}

