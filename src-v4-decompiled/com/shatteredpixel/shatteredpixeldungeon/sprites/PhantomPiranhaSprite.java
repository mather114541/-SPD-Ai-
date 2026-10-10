/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class PhantomPiranhaSprite
extends MobSprite {
    private Emitter sparkles;

    public PhantomPiranhaSprite() {
        this.renderShadow = false;
        this.perspectiveRaise = 0.2f;
        this.texture("sprites/piranha.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        int c = 21;
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, c + 0, c + 1, c + 2, c + 1);
        this.run = new MovieClip.Animation(20, true);
        this.run.frames(frames, c + 0, c + 1, c + 2, c + 1);
        this.attack = new MovieClip.Animation(20, false);
        this.attack.frames(frames, c + 3, c + 4, c + 5, c + 6, c + 7, c + 8, c + 9, c + 10, c + 11);
        this.die = new MovieClip.Animation(4, false);
        this.die.frames(frames, c + 12, c + 13, c + 14);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
        if (this.sparkles == null) {
            this.sparkles = this.emitter();
            this.sparkles.pour(Speck.factory(2), 0.5f);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.sparkles != null) {
            this.sparkles.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.sparkles != null) {
            this.sparkles.on = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.sparkles != null) {
            this.sparkles.on = false;
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.attack) {
            GameScene.ripple(this.ch.pos);
        }
    }
}

