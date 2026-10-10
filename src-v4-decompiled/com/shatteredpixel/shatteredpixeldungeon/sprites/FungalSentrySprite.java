/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Callback;

public class FungalSentrySprite
extends MobSprite {
    private int cellToAttack;

    public FungalSentrySprite() {
        this.texture("sprites/fungal_sentry.png");
        TextureFilm frames = new TextureFilm(this.texture, 18, 18);
        this.idle = new MovieClip.Animation(0, true);
        this.idle.frames(frames, 0);
        this.run = new MovieClip.Animation(0, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(24, false);
        this.attack.frames(frames, 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 0);
        this.play(this.idle);
    }

    @Override
    public void attack(int cell) {
        if (!Dungeon.level.adjacent(cell, this.ch.pos)) {
            this.cellToAttack = cell;
            this.zap(cell);
        } else {
            super.attack(cell);
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
            MagicMissile.boltFromChar(this.parent, 16, this, this.cellToAttack, new Callback(){

                @Override
                public void call() {
                    FungalSentrySprite.this.ch.onAttackComplete();
                }
            });
        } else {
            super.onComplete(anim);
        }
    }

    @Override
    public void turnTo(int from, int to) {
    }

    @Override
    public int blood() {
        return -7812028;
    }
}

