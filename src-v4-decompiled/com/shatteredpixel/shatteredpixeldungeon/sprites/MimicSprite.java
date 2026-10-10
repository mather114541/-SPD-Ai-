/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class MimicSprite
extends MobSprite {
    protected MovieClip.Animation advancedHiding;
    protected MovieClip.Animation hiding;

    protected int texOffset() {
        return 0;
    }

    public MimicSprite() {
        this.perspectiveRaise = 0.3125f;
        this.shadowWidth = 1.0f;
        this.shadowOffset = -0.4f;
        int c = this.texOffset();
        this.texture("sprites/mimic.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.advancedHiding = new MovieClip.Animation(1, true);
        this.advancedHiding.frames(frames, 0 + c);
        this.hiding = new MovieClip.Animation(1, true);
        this.hiding.frames(frames, 1 + c, 1 + c, 1 + c, 1 + c, 1 + c, 2 + c);
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 3 + c, 3 + c, 3 + c, 4 + c, 4 + c);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 3 + c, 4 + c, 5 + c, 6 + c, 6 + c, 5 + c, 4 + c);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, 3 + c, 7 + c, 8 + c, 9 + c);
        this.die = new MovieClip.Animation(5, false);
        this.die.frames(frames, 10 + c, 11 + c, 12 + c);
        this.play(this.idle);
    }

    @Override
    public void linkVisuals(Char ch) {
        super.linkVisuals(ch);
        if (ch.alignment == Char.Alignment.NEUTRAL) {
            this.hideMimic(ch);
        }
    }

    public void hideMimic(Char ch) {
        if (ch instanceof Mimic && ((Mimic)ch).stealthy()) {
            this.play(this.advancedHiding);
        } else {
            this.play(this.hiding);
        }
        this.hideSleep();
    }

    @Override
    public void showSleep() {
        if (this.curAnim == this.hiding || this.curAnim == this.advancedHiding) {
            return;
        }
        super.showSleep();
    }

    public static class Ebony
    extends MimicSprite {
        @Override
        protected int texOffset() {
            return 48;
        }

        @Override
        public void hideMimic(Char ch) {
            super.hideMimic(ch);
            this.alpha(0.2f);
        }

        @Override
        public void resetColor() {
            super.resetColor();
            if (this.advancedHiding != null && this.curAnim == this.advancedHiding) {
                this.alpha(0.2f);
            }
        }

        @Override
        public void play(MovieClip.Animation anim) {
            if (this.curAnim == this.advancedHiding && anim != this.advancedHiding) {
                this.alpha(1.0f);
            }
            super.play(anim);
        }
    }

    public static class Crystal
    extends MimicSprite {
        @Override
        protected int texOffset() {
            return 32;
        }
    }

    public static class Golden
    extends MimicSprite {
        @Override
        protected int texOffset() {
            return 16;
        }
    }
}

