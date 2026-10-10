/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;

public class ScorpioSprite
extends MobSprite {
    private int cellToAttack;

    public ScorpioSprite() {
        this.texture("sprites/scorpio.png");
        TextureFilm frames = new TextureFilm(this.texture, 17, 17);
        this.idle = new MovieClip.Animation(12, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 2);
        this.run = new MovieClip.Animation(8, true);
        this.run.frames(frames, 5, 5, 6, 6);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 0, 3, 4);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 0, 7, 8, 9, 10);
        this.play(this.idle);
    }

    @Override
    public int blood() {
        return -12255454;
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
            ((MissileSprite)this.parent.recycle(MissileSprite.class)).reset((Visual)this, this.cellToAttack, (Item)new ScorpioShot(), new Callback(){

                @Override
                public void call() {
                    ScorpioSprite.this.ch.onAttackComplete();
                }
            });
        } else {
            super.onComplete(anim);
        }
    }

    public class ScorpioShot
    extends Item {
        public ScorpioShot() {
            this.image = ItemSpriteSheet.FISHING_SPEAR;
        }
    }
}

