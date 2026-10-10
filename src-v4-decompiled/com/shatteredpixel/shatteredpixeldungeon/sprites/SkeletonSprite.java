/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class SkeletonSprite
extends MobSprite {
    public SkeletonSprite() {
        this.texture("sprites/skeleton.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, this.texHeight());
        int c = this.texOffset();
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 1 + c, 2 + c, 3 + c);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 4 + c, 5 + c, 6 + c, 7 + c, 8 + c, 9 + c);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 14 + c, 15 + c, 16 + c);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 10 + c, 11 + c, 12 + c, 13 + c);
        this.play(this.idle);
    }

    protected int texOffset() {
        return 0;
    }

    protected int texHeight() {
        return 15;
    }

    @Override
    public void die() {
        super.die();
        if (Dungeon.level.heroFOV[this.ch.pos]) {
            this.emitter().burst(Speck.factory(6), 6);
        }
    }

    @Override
    public int blood() {
        return -3355444;
    }

    public static class Vault
    extends SkeletonSprite {
        @Override
        protected int texOffset() {
            return 21;
        }

        @Override
        protected int texHeight() {
            return 16;
        }
    }
}

