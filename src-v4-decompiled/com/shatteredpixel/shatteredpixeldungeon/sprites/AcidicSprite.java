/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ScorpioSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class AcidicSprite
extends ScorpioSprite {
    public AcidicSprite() {
        this.texture("sprites/scorpio.png");
        TextureFilm frames = new TextureFilm(this.texture, 17, 17);
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 15, 15, 15, 15, 15, 15, 15, 15, 16, 17, 16, 17, 16, 17);
        this.run = new MovieClip.Animation(4, true);
        this.run.frames(frames, 20, 21);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 15, 18, 19);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 15, 22, 23, 24, 25);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -10027230;
    }
}

