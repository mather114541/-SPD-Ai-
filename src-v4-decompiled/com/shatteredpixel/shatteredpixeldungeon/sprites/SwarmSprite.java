/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SwarmSprite
extends MobSprite {
    public SwarmSprite() {
        this.texture("sprites/swarm.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(15, true);
        this.idle.frames(frames, 0, 1, 2, 3, 4, 5);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 0, 1, 2, 3, 4, 5);
        this.attack = new MovieClip.Animation(20, false);
        this.attack.frames(frames, 6, 7, 8, 9);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 10, 11, 12, 13, 14);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -7626633;
    }
}

