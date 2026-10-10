/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class WarlockSprite
extends MobSprite {
    public WarlockSprite() {
        this.texture("sprites/warlock.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 0, 2, 3, 4);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 0, 5, 6);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, 0, 7, 8, 8, 9, 10);
        this.play(this.idle);
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, 7, this, cell, new Callback(){

            @Override
            public void call() {
                ((Warlock)WarlockSprite.this.ch).onZapComplete();
            }
        });
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }
}

