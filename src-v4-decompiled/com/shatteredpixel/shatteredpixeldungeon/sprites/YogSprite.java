/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class YogSprite
extends MobSprite {
    public YogSprite() {
        this.perspectiveRaise = 0.3125f;
        this.texture("sprites/yog.png");
        TextureFilm frames = new TextureFilm(this.texture, 20, 19);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 0, 1, 2, 2, 1, 0, 3, 4, 4, 3, 0, 5, 6, 6, 5);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 0);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 0, 7, 8, 9);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
    }

    @Override
    public void die() {
        super.die();
        Splash.at(this.center(), this.blood(), 12);
    }
}

