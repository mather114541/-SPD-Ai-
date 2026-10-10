/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalGuardian;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public abstract class CrystalGuardianSprite
extends MobSprite {
    private MovieClip.Animation crumple;

    public CrystalGuardianSprite() {
        this.texture("sprites/crystal_guardian.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        int c = this.texOffset();
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 1 + c, 1 + c);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 2 + c, 3 + c, 4 + c, 5 + c, 6 + c, 7 + c);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 8 + c, 9 + c, 10 + c);
        this.die = new MovieClip.Animation(5, false);
        this.die.frames(frames, 11 + c, 12 + c, 13 + c, 14 + c, 15 + c, 15 + c);
        this.crumple = this.die.clone();
        this.scale.set(1.25f);
        this.play(this.idle);
    }

    public void crumple() {
        this.play(this.crumple);
    }

    public void endCrumple() {
        if (this.curAnim == this.crumple) {
            this.idle();
        }
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof CrystalGuardian && ((CrystalGuardian)ch).recovering()) {
            this.crumple();
        }
    }

    protected abstract int texOffset();

    public static class Red
    extends CrystalGuardianSprite {
        @Override
        protected int texOffset() {
            return 42;
        }

        @Override
        public int blood() {
            return -17613;
        }
    }

    public static class Green
    extends CrystalGuardianSprite {
        @Override
        protected int texOffset() {
            return 21;
        }

        @Override
        public int blood() {
            return -7995448;
        }
    }

    public static class Blue
    extends CrystalGuardianSprite {
        @Override
        protected int texOffset() {
            return 0;
        }

        @Override
        public int blood() {
            return -7412737;
        }
    }
}

