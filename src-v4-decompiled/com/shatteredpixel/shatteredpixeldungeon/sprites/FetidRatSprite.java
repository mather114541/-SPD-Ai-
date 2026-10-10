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

public class FetidRatSprite
extends MobSprite {
    private Emitter cloud;

    public FetidRatSprite() {
        this.texture("sprites/rat.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 15);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 32, 32, 32, 33);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 38, 39, 40, 41, 42);
        this.attack = new MovieClip.Animation(15, false);
        this.attack.frames(frames, 34, 35, 36, 37, 32);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(frames, 43, 44, 45, 46);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (this.cloud == null) {
            this.cloud = this.emitter();
            this.cloud.pour(Speck.factory(111), 0.7f);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.cloud != null) {
            this.cloud.visible = this.visible;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.cloud != null) {
            this.cloud.on = false;
        }
    }
}

