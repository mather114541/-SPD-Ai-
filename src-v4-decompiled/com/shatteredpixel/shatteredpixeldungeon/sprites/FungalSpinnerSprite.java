/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class FungalSpinnerSprite
extends MobSprite {
    public FungalSpinnerSprite() {
        this.perspectiveRaise = 0.0f;
        this.texture("sprites/fungal_spinner.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, 0, 0, 0, 0, 0, 1, 0, 1);
        this.run = new MovieClip.Animation(15, true);
        this.run.frames(frames, 0, 2, 0, 3);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 0, 4, 5, 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 6, 7, 8, 9);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (this.parent != null) {
            this.parent.sendToBack(this);
            if (this.aura != null) {
                this.parent.sendToBack(this.aura);
            }
        }
        this.renderShadow = false;
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        MagicMissile.boltFromChar(this.parent, 4, this, cell, new Callback(){

            @Override
            public void call() {
                ((Spinner)FungalSpinnerSprite.this.ch).shootWeb();
            }
        });
        Sample.INSTANCE.play("sounds/miss.mp3");
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.play(this.run);
        }
        super.onComplete(anim);
    }

    @Override
    public int blood() {
        return -7812028;
    }
}

