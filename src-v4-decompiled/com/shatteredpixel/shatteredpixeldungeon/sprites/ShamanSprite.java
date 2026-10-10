/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Shaman;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public abstract class ShamanSprite
extends MobSprite {
    protected int boltType;

    protected abstract int texOffset();

    public ShamanSprite() {
        int c = this.texOffset();
        this.texture("sprites/shaman.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, c + 0, c + 0, c + 0, c + 1, c + 0, c + 0, c + 1, c + 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, c + 4, c + 5, c + 6, c + 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, c + 2, c + 3, c + 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, c + 8, c + 9, c + 10);
        this.play(this.idle);
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, this.boltType, this, cell, new Callback(){

            @Override
            public void call() {
                ((Shaman)ShamanSprite.this.ch).onZapComplete();
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

    public static class Vault
    extends ShamanSprite {
        public Vault() {
            this.boltType = 14;
        }

        @Override
        protected int texOffset() {
            return 63;
        }
    }

    public static class Purple
    extends ShamanSprite {
        public Purple() {
            this.boltType = 13;
        }

        @Override
        protected int texOffset() {
            return 42;
        }
    }

    public static class Blue
    extends ShamanSprite {
        public Blue() {
            this.boltType = 12;
        }

        @Override
        protected int texOffset() {
            return 21;
        }
    }

    public static class Red
    extends ShamanSprite {
        public Red() {
            this.boltType = 11;
        }

        @Override
        protected int texOffset() {
            return 0;
        }
    }
}

