/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.ParalyticDart;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;

public class GnollTricksterSprite
extends MobSprite {
    private MovieClip.Animation cast;

    public GnollTricksterSprite() {
        this.texture("sprites/gnoll.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 15);
        int c = 42;
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0 + c, 0 + c, 0 + c, 1 + c, 0 + c, 0 + c, 1 + c, 1 + c);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 4 + c, 5 + c, 6 + c, 7 + c);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 2 + c, 3 + c, 0 + c);
        this.cast = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 8 + c, 9 + c, 10 + c);
        this.play(this.idle);
    }

    @Override
    public void attack(int cell) {
        if (!Dungeon.level.adjacent(cell, this.ch.pos)) {
            ((MissileSprite)this.parent.recycle(MissileSprite.class)).reset((Visual)this, cell, (Item)new ParalyticDart(), new Callback(){

                @Override
                public void call() {
                    GnollTricksterSprite.this.ch.onAttackComplete();
                }
            });
            this.play(this.cast);
            this.turnTo(this.ch.pos, cell);
        } else {
            super.attack(cell);
        }
    }
}

