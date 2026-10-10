/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class RipperSprite
extends MobSprite {
    private MovieClip.Animation stab;
    private MovieClip.Animation prep;
    private MovieClip.Animation leap;
    private boolean alt = Random.Int(2) == 0;

    public RipperSprite() {
        this.texture("sprites/ripper.png");
        TextureFilm frames = new TextureFilm(this.texture, 15, 14);
        this.idle = new MovieClip.Animation(4, true);
        this.idle.frames(frames, 1, 0, 1, 2);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 3, 4, 5, 6, 7, 8);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 0, 9, 10, 9);
        this.stab = new MovieClip.Animation(12, false);
        this.stab.frames(frames, 0, 9, 11, 9);
        this.prep = new MovieClip.Animation(1, true);
        this.prep.frames(frames, 9);
        this.leap = new MovieClip.Animation(1, true);
        this.leap.frames(frames, 12);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 1, 13, 14, 15, 16);
        this.play(this.idle);
    }

    public void leapPrep(int cell) {
        this.turnTo(this.ch.pos, cell);
        this.play(this.prep);
    }

    @Override
    public void jump(int from, int to, float height, float duration, Callback callback) {
        super.jump(from, to, height, duration, callback);
        this.play(this.leap);
    }

    @Override
    public void attack(int cell) {
        super.attack(cell);
        if (this.alt) {
            this.play(this.stab);
        }
        this.alt = !this.alt;
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim == this.stab ? this.attack : anim);
    }
}

