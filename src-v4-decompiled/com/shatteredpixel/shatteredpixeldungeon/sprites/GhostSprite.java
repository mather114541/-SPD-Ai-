/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;

public class GhostSprite
extends MobSprite {
    public GhostSprite() {
        this.texture("sprites/ghost.png");
        TextureFilm frames = new TextureFilm(this.texture, 14, 15);
        this.idle = new MovieClip.Animation(5, true);
        this.idle.frames(frames, 0, 1);
        this.run = new MovieClip.Animation(10, true);
        this.run.frames(frames, 0, 1);
        this.attack = new MovieClip.Animation(10, false);
        this.attack.frames(frames, 0, 2, 3);
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 0, 4, 5, 6, 7);
        this.play(this.idle);
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    @Override
    public void die() {
        super.die();
        this.emitter().start(ShaftParticle.FACTORY, 0.3f, 4);
        this.emitter().start(Speck.factory(2), 0.2f, 3);
    }

    @Override
    public int blood() {
        return 0xFFFFFF;
    }
}

