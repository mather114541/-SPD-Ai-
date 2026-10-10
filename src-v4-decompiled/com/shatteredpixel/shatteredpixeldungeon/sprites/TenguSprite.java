/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;

public class TenguSprite
extends MobSprite {
    public TenguSprite() {
        this.texture("sprites/tengu.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 16);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1);
        this.run = new MovieClip.Animation(15, false);
        this.run.frames(frames, 2, 3, 4, 5, 0);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 6, 7, 7, 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 8, 9, 10, 10, 10, 10, 10, 10);
        this.play(this.run);
        this.isMoving = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void play(MovieClip.Animation anim) {
        if (this.isMoving && anim != this.run) {
            TenguSprite tenguSprite = this;
            synchronized (tenguSprite) {
                this.isMoving = false;
                this.notifyAll();
            }
        }
        super.play(anim);
    }

    @Override
    public void move(int from, int to) {
        this.place(to);
        this.play(this.run);
        this.turnTo(from, to);
        this.isMoving = true;
        if (Dungeon.level.water[to]) {
            GameScene.ripple(to);
        }
    }

    @Override
    public void update() {
        if (this.paused && !this.curAnim.looped) {
            this.updateAnimation();
        }
        super.update();
    }

    @Override
    public void attack(int cell) {
        if (!Dungeon.level.adjacent(cell, this.ch.pos)) {
            ((MissileSprite)this.parent.recycle(MissileSprite.class)).reset((Visual)this, cell, (Item)new TenguShuriken(), new Callback(){

                @Override
                public void call() {
                    TenguSprite.this.ch.onAttackComplete();
                }
            });
            this.zap(this.ch.pos);
        } else {
            super.attack(cell);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.run) {
            TenguSprite tenguSprite = this;
            synchronized (tenguSprite) {
                this.isMoving = false;
                this.idle();
                this.notifyAll();
            }
        } else {
            super.onComplete(anim);
        }
    }

    public static class TenguShuriken
    extends Item {
        public TenguShuriken() {
            this.image = ItemSpriteSheet.SHURIKEN;
        }
    }
}

