/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class RotHeartSprite
extends MobSprite {
    private Emitter cloud;

    public RotHeartSprite() {
        this.perspectiveRaise = 0.2f;
        this.texture("sprites/rot_heart.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(frames, 0);
        this.run = new MovieClip.Animation(1, true);
        this.run.frames(frames, 0);
        this.attack = new MovieClip.Animation(1, false);
        this.attack.frames(frames, 0);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 1, 2, 3, 4, 5, 6, 7, 7, 7);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.renderShadow = false;
        if (this.cloud == null) {
            this.cloud = this.emitter();
            this.cloud.pour(Speck.factory(107), 0.7f);
        }
    }

    @Override
    public void turnTo(int from, int to) {
    }

    @Override
    public void update() {
        super.update();
        if (this.cloud != null) {
            this.cloud.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.cloud != null) {
            this.cloud.on = false;
        }
    }

    @Override
    public int blood() {
        return -7812028;
    }
}

