/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class EyeSprite
extends MobSprite {
    private int zapPos;
    private MovieClip.Animation charging;
    private Emitter chargeParticles;

    public EyeSprite() {
        this.texture("sprites/eye.png");
        TextureFilm frames = new TextureFilm(this.texture, 16, 18);
        this.idle = new MovieClip.Animation(8, true);
        this.idle.frames(frames, 0, 1, 2);
        this.charging = new MovieClip.Animation(12, true);
        this.charging.frames(frames, 3, 4);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, 5, 6);
        this.attack = new MovieClip.Animation(8, false);
        this.attack.frames(frames, 4, 3);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(8, false);
        this.die.frames(frames, 7, 8, 9);
        this.play(this.idle);
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.chargeParticles = this.centerEmitter();
        this.chargeParticles.autoKill = false;
        this.chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
        this.chargeParticles.on = false;
        if (((Eye)ch).beamCharged) {
            this.play(this.charging);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.chargeParticles != null) {
            this.chargeParticles.pos(this.center());
            this.chargeParticles.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.chargeParticles != null) {
            this.chargeParticles.on = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.chargeParticles != null) {
            this.chargeParticles.killAndErase();
        }
    }

    public void charge(int pos) {
        this.turnTo(this.ch.pos, pos);
        this.play(this.charging);
        if (this.visible) {
            Sample.INSTANCE.play("sounds/chargeup.mp3");
        }
    }

    @Override
    public void play(MovieClip.Animation anim) {
        if (this.chargeParticles != null) {
            this.chargeParticles.on = anim == this.charging;
        }
        super.play(anim);
    }

    @Override
    public void zap(int pos) {
        this.zapPos = pos;
        super.zap(pos);
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.zap) {
            this.idle();
            if (Actor.findChar(this.zapPos) != null) {
                this.parent.add(new Beam.DeathRay(this.center(), Actor.findChar((int)this.zapPos).sprite.center()));
            } else {
                this.parent.add(new Beam.DeathRay(this.center(), DungeonTilemap.raisedTileCenterToWorld(this.zapPos)));
            }
            Sample.INSTANCE.play("sounds/ray.mp3");
            ((Eye)this.ch).deathGaze();
            this.ch.next();
        } else if (anim == this.die) {
            this.chargeParticles.killAndErase();
        }
    }
}

