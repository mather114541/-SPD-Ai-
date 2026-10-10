/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class UndeadSprite
extends MobSprite {
    public UndeadSprite() {
        this.texture("sprites/undead.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 4, 5, 6, 7, 8, 9);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 14, 15, 16);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 10, 11, 12, 13);
        this.play(this.idle);
    }

    @Override
    public void die() {
        super.die();
        if (Dungeon.level.heroFOV[this.ch.pos]) {
            this.emitter().burst(Speck.factory(6), 3);
        }
    }

    @Override
    public int blood() {
        return -3355444;
    }
}

