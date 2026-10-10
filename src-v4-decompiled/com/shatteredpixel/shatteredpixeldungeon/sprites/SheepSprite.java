/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Random;

public class SheepSprite
extends MobSprite {
    public SheepSprite() {
        this.texture("sprites/sheep.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 15);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 0);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
        this.curFrame = Random.Int(this.curAnim.frames.length);
    }
}

