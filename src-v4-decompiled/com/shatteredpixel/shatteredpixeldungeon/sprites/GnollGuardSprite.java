/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class GnollGuardSprite
extends MobSprite {
    private Emitter earthArmor;

    public GnollGuardSprite() {
        this.texture("sprites/gnoll_guard.png");
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        this.idle = new MovieClip.Animation(2, true);
        this.idle.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 4, 5, 6, 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, 2, 3, 0);
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, 8, 9, 10);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof GnollGuard && ((GnollGuard)ch).hasSapper()) {
            this.setupArmor();
        }
    }

    public void setupArmor() {
        if (this.earthArmor == null) {
            this.earthArmor = this.emitter();
            this.earthArmor.fillTarget = false;
            this.earthArmor.y = this.height() / 2.0f;
            this.earthArmor.x = 2.0f * this.scale.x;
            this.earthArmor.width = this.width() - 4.0f * this.scale.x;
            this.earthArmor.height = this.height() - 10.0f * this.scale.y;
            this.earthArmor.pour(EarthParticle.SMALL, 0.15f);
        }
    }

    public void loseArmor() {
        if (this.earthArmor != null) {
            this.earthArmor.on = false;
            this.earthArmor = null;
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.earthArmor != null) {
            this.earthArmor.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.earthArmor != null) {
            this.earthArmor.on = false;
            this.earthArmor = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.earthArmor != null) {
            this.earthArmor.on = false;
            this.earthArmor = null;
        }
    }
}

