/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;

public class DM100Sprite
extends MobSprite {
    public DM100Sprite() {
        this.texture("sprites/dm100.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 14);
        int c = this.texOffset();
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(frames, 0 + c, 1 + c);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 6 + c, 7 + c, 8 + c, 9 + c);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 2 + c, 3 + c, 4 + c, 0 + c);
        this.zap = new MovieClip.Animation(8, false);
        this.zap.frames(frames, 5 + c, 5 + c, 1 + c);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 10 + c, 11 + c, 12 + c, 13 + c, 14 + c, 15 + c);
        this.play(this.idle);
    }

    protected int texOffset() {
        return 0;
    }

    @Override
    public void zap(int pos) {
        Char enemy = Actor.findChar(pos);
        PointF origin = this.center();
        if (this.flipHorizontal) {
            origin.y -= 6.0f * this.scale.y;
            origin.x -= 1.0f * this.scale.x;
        } else {
            origin.y -= 8.0f * this.scale.y;
            origin.x += 1.0f * this.scale.x;
        }
        if (enemy != null) {
            this.parent.add(new Lightning(origin, enemy.sprite.destinationCenter(), new Callback(){

                @Override
                public void call() {
                    DM100Sprite.this.ch.next();
                }
            }));
        } else {
            this.parent.add(new Lightning(origin, pos, new Callback(){

                @Override
                public void call() {
                    DM100Sprite.this.ch.next();
                }
            }));
        }
        Sample.INSTANCE.play("sounds/lightning.mp3");
        super.zap(this.ch.pos);
        this.flash();
    }

    @Override
    public void die() {
        this.emitter().burst(Speck.factory(7), 5);
        super.die();
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }

    @Override
    public int blood() {
        return -120;
    }

    public static class Vault
    extends DM100Sprite {
        @Override
        protected int texOffset() {
            return 16;
        }
    }
}

