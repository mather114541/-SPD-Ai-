/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM201;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class DM201Sprite
extends MobSprite {
    public DM201Sprite() {
        this.texture("sprites/dm200.png");
        TextureFilm frames = new TextureFilm(this.texture, 21, 18);
        int c = 12;
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, c + 0, c + 1);
        this.run = this.idle.clone();
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, c + 4, c + 5, c + 6);
        this.zap = new MovieClip.Animation(15, false);
        this.zap.frames(frames, c + 7, c + 8, c + 8, c + 7);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, c + 9, c + 10, c + 11);
        this.play(this.idle);
    }

    @Override
    public void place(int cell) {
        if (this.parent != null) {
            this.parent.bringToFront(this);
        }
        super.place(cell);
    }

    @Override
    public void die() {
        this.emitter().burst(Speck.factory(7), 8);
        super.die();
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, 3, this, cell, new Callback(){

            @Override
            public void call() {
                Sample.INSTANCE.play("sounds/gas.mp3");
                ((DM201)DM201Sprite.this.ch).onZapComplete();
            }
        });
        Sample.INSTANCE.play("sounds/miss.mp3", 1.0f, 1.5f);
        GLog.w(Messages.get(DM201.class, "vent", new Object[0]), new Object[0]);
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
}

