/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class ImpSprite
extends MobSprite {
    public ImpSprite() {
        this.texture("sprites/imp.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 0, 1, 2, 3, 0, 1, 2, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(frames, 0);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.die) {
            this.emitter().burst(Speck.factory(7), 15);
            this.killAndErase();
        } else {
            super.onComplete(anim);
        }
    }
}

