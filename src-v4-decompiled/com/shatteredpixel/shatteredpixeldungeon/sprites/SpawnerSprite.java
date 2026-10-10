/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.PointF;

public class SpawnerSprite
extends MobSprite {
    private float baseY = Float.NaN;

    public SpawnerSprite() {
        this.texture("sprites/spawner.png");
        this.perspectiveRaise = 0.5f;
        this.shadowOffset = 1.25f;
        this.shadowHeight = 0.4f;
        this.shadowWidth = 1.0f;
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = this.idle.clone();
        this.play(this.idle);
    }

    @Override
    public void place(int cell) {
        super.place(cell);
        this.baseY = this.y;
    }

    @Override
    public void update() {
        super.update();
        if (!this.paused) {
            if (Float.isNaN(this.baseY)) {
                this.baseY = this.y;
            }
            this.y = this.baseY + (float)(Math.sin(Game.timeTotal) / 3.0);
            this.shadowOffset = 1.25f - 0.6f * (float)(Math.sin(Game.timeTotal) / 3.0);
        }
    }

    @Override
    public void die() {
        Splash.around(this, this.blood(), 100);
        this.killAndErase();
    }

    @Override
    public void bloodBurstA(PointF from, int damage) {
        if (this.alive) {
            super.bloodBurstA(from, damage);
        }
    }
}

