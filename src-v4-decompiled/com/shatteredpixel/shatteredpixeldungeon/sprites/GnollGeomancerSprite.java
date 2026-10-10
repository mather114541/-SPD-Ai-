/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.EarthParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.particles.Emitter;

public class GnollGeomancerSprite
extends MobSprite {
    boolean isStatue = false;
    private Emitter earthArmor;

    public GnollGeomancerSprite() {
        this.texture("sprites/gnoll_geomancer.png");
        this.updateAnims();
        this.scale.set(1.25f);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof GnollGeomancer && ((GnollGeomancer)ch).hasSapper()) {
            this.setupArmor();
        }
        if (ch != null && ch.buff(GnollGeomancer.RockArmor.class) != null != this.isStatue) {
            this.isStatue = !this.isStatue;
            this.updateAnims();
        }
    }

    private void updateAnims() {
        TextureFilm frames = new TextureFilm(this.texture, 12, 16);
        int ofs = this.isStatue ? 21 : 0;
        this.idle = new MovieClip.Animation(this.isStatue ? 1 : 2, true);
        this.idle.frames(frames, ofs + 0, ofs + 0, ofs + 0, ofs + 1, ofs + 0, ofs + 0, ofs + 1, ofs + 1);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, ofs + 4, ofs + 5, ofs + 6, ofs + 7);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, ofs + 2, ofs + 3, ofs + 0);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(12, false);
        this.die.frames(frames, ofs + 8, ofs + 9, ofs + 10);
        this.play(this.idle);
        this.play(this.idle);
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

    @Override
    public void onComplete(MovieClip.Animation anim) {
        if (anim == this.zap) {
            this.idle();
        }
        super.onComplete(anim);
    }

    @Override
    public void idle() {
        super.idle();
        if (this.ch != null && this.ch.buff(GnollGeomancer.RockArmor.class) != null != this.isStatue) {
            this.isStatue = !this.isStatue;
            this.updateAnims();
        }
    }

    @Override
    public int blood() {
        return this.isStatue ? 0x555555 : super.blood();
    }
}

