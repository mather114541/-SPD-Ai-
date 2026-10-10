/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class LarvaSprite
extends MobSprite {
    public LarvaSprite() {
        this.texture("sprites/larva.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 8);
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 4, 4, 4, 4, 4, 5, 5);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 0, 1, 2, 3);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 6, 5, 7);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 8);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return 0xBBCC66;
    }

    @Override
    public void die() {
        Splash.at(this.center(), this.blood(), 10);
        super.die();
    }
}

