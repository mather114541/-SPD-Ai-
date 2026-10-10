/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.PixelParticle;

public class ShopkeeperSprite
extends MobSprite {
    private PixelParticle coin;

    public ShopkeeperSprite() {
        this.texture("sprites/shopkeeper.png");
        TextureFilm film = new TextureFilm(this.texture, 14, 14);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(film, 1, 1, 1, 1, 1, 0, 0, 0, 0);
        this.die = new MovieClip.Animation(20, false);
        this.die.frames(film, 0);
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.idle();
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (this.visible && anim == this.idle) {
            if (this.coin == null) {
                this.coin = new PixelParticle();
                this.parent.add(this.coin);
            }
            this.coin.reset(this.x + (float)(this.flipHorizontal ? 0 : 13), this.y + 7.0f, 0xFFFF00, 1.0f, 0.5f);
            this.coin.speed.y = -40.0f;
            this.coin.acc.y = 160.0f;
        }
    }
}

