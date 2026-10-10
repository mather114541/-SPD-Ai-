/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class SpectralNecromancerSprite
extends MobSprite {
    private MovieClip.Animation charging;
    private Emitter summoningParticles;

    public SpectralNecromancerSprite() {
        this.texture("sprites/necromancer.png");
        TextureFilm film = new TextureFilm(this.texture, 16, 16);
        int c = 16;
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, c + 0, c + 0, c + 0, c + 1, c + 0, c + 0, c + 0, c + 0, c + 1);
        this.run = new MovieClip.Animation(8, true);
        this.run.frames(film, c + 0, c + 0, c + 0, c + 2, c + 3, c + 4);
        this.zap = new MovieClip.Animation(10, false);
        this.zap.frames(film, c + 5, c + 6, c + 7, c + 8);
        this.charging = new MovieClip.Animation(5, true);
        this.charging.frames(film, c + 7, c + 8);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(film, c + 9, c + 10, c + 11, c + 12);
        this.attack = this.zap.clone();
        this.idle();
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (ch instanceof Necromancer && ((Necromancer)ch).summoning) {
            this.zap(((Necromancer)ch).summoningPos);
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.summoningParticles != null && ((Necromancer)this.ch).summoningPos != -1) {
            this.summoningParticles.visible = Dungeon.level.heroFOV[((Necromancer)this.ch).summoningPos];
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.summoningParticles != null) {
            this.summoningParticles.on = false;
            this.summoningParticles = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.summoningParticles != null) {
            this.summoningParticles.on = false;
            this.summoningParticles = null;
        }
    }

    public void cancelSummoning() {
        if (this.summoningParticles != null) {
            this.summoningParticles.on = false;
            this.summoningParticles = null;
        }
    }

    public void finishSummoning() {
        if (this.summoningParticles.visible) {
            Sample.INSTANCE.play("sounds/cursed.mp3");
            this.summoningParticles.burst(ShadowParticle.CURSE, 5);
        } else {
            this.summoningParticles.on = false;
        }
        this.summoningParticles = null;
        this.idle();
    }

    public void charge() {
        this.play(this.charging);
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        if (this.ch instanceof Necromancer && ((Necromancer)this.ch).summoning) {
            if (this.summoningParticles != null) {
                this.summoningParticles.on = false;
            }
            this.summoningParticles = CellEmitter.get(((Necromancer)this.ch).summoningPos);
            this.summoningParticles.pour(ShadowParticle.MISSILE, 0.1f);
            this.summoningParticles.visible = Dungeon.level.heroFOV[((Necromancer)this.ch).summoningPos];
            if (this.visible || this.summoningParticles.visible) {
                Sample.INSTANCE.play("sounds/chargeup.mp3", 1.0f, 0.8f);
            }
        }
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.zap) {
            if (this.ch instanceof Necromancer) {
                if (((Necromancer)this.ch).summoning) {
                    this.charge();
                } else {
                    ((Necromancer)this.ch).onZapComplete();
                    this.idle();
                }
            } else {
                this.idle();
            }
        }
    }
}

