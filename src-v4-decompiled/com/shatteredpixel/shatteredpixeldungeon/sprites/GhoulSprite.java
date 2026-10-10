/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GhoulSprite
extends MobSprite {
    private MovieClip.Animation crumple;

    public GhoulSprite() {
        this.texture("sprites/ghoul.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 2, 3, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 0, 8, 9);
        this.crumple = new MovieClip.Animation(15, false);
        this.crumple.frames(frames, 0, 10, 11, 12);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 0, 10, 11, 12, 13);
        this.play(this.idle);
    }

    public void crumple() {
        this.hideEmo();
        this.processStateRemoval(CharSprite.State.PARALYSED);
        this.play(this.crumple);
    }

    @Override
    public void die() {
        if (this.curAnim == this.crumple) {
            this.die.frames[1] = this.die.frames[2] = this.die.frames[3];
            this.die.frames[0] = this.die.frames[2];
        }
        super.die();
    }
}

