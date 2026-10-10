/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.GameMath;

public class StatueSprite
extends MobSprite {
    private static int[] tierFrames = new int[]{0, 21, 32, 43, 54, 65};

    public StatueSprite() {
        this.texture("sprites/statue.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 2, 3, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 8, 9, 10);
        this.die = new MovieClip.Animation(5, false);
        this.die.frames(frames, 11, 12, 13, 14, 15, 15);
        this.play(this.idle);
    }

    public void setArmor(int tier) {
        int c = tierFrames[(int)GameMath.gate(0.0f, tier, 5.0f)];
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        this.idle.frames(frames, 0 + c, 0 + c, 0 + c, 0 + c, 0 + c, 1 + c, 1 + c);
        this.run.frames(frames, 2 + c, 3 + c, 4 + c, 5 + c, 6 + c, 7 + c);
        this.attack.frames(frames, 8 + c, 9 + c, 10 + c);
        this.play(this.idle, true);
    }

    @Override
    public int blood() {
        return -3289673;
    }
}

