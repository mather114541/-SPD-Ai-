/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class NewbornElementalSprite
extends MobSprite {
    public NewbornElementalSprite() {
        this.texture("sprites/elemental.png");
        int ofs = 21;
        TextureFilm frames = new TextureFilm(this.texture, 12, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, ofs + 0, ofs + 1, ofs + 2);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, ofs + 0, ofs + 1, ofs + 3);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, ofs + 4, ofs + 5, ofs + 6);
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, ofs + 7, ofs + 8, ofs + 9, ofs + 10, ofs + 11, ofs + 12, ofs + 13, ofs + 12);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.add(CharSprite.State.BURNING);
    }

    @Override
    public void die() {
        super.die();
        this.processStateRemoval(CharSprite.State.BURNING);
    }

    @Override
    public int blood() {
        return -33517;
    }
}

