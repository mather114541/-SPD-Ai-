/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class PiranhaSprite
extends MobSprite {
    public PiranhaSprite() {
        this.renderShadow = false;
        this.perspectiveRaise = 0.2f;
        this.texture("sprites/piranha.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 1, 2, 1);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(frames, 0, 1, 2, 1);
        this.attack = new MovieClip.Animation(20, false);
        this.attack.frames(frames, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        this.die = new MovieClip.Animation(4, false);
        this.die.frames(frames, 12, 13, 14);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.attack) {
            GameScene.ripple(this.ch.pos);
        }
    }
}

