/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Necromancer;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public class NecromancerSprite
extends MobSprite {
    private MovieClip.Animation charging;
    private Emitter summoningBones;

    public NecromancerSprite() {
        this.texture("sprites/necromancer.png");
        TextureFilm film = new TextureFilm(this.texture, 16, 16);
        this.idle = new MovieClip.Animation(1, true);
        this.idle.frames(film, 0, 0, 0, 1, 0, 0, 0, 0, 1);
        this.run = new MovieClip.Animation(8, true);
        this.run.frames(film, 0, 0, 0, 2, 3, 4);
        this.zap = new MovieClip.Animation(10, false);
        this.zap.frames(film, 5, 6, 7, 8);
        this.charging = new MovieClip.Animation(5, true);
        this.charging.frames(film, 7, 8);
        this.die = new MovieClip.Animation(10, false);
        this.die.frames(film, 9, 10, 11, 12);
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
        if (this.summoningBones != null && ((Necromancer)this.ch).summoningPos != -1) {
            this.summoningBones.visible = Dungeon.level.heroFOV[((Necromancer)this.ch).summoningPos];
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.summoningBones != null) {
            this.summoningBones.on = false;
            this.summoningBones = null;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.summoningBones != null) {
            this.summoningBones.on = false;
            this.summoningBones = null;
        }
    }

    public void cancelSummoning() {
        if (this.summoningBones != null) {
            this.summoningBones.on = false;
            this.summoningBones = null;
        }
    }

    public void finishSummoning() {
        if (this.summoningBones != null) {
            if (this.summoningBones.visible) {
                Sample.INSTANCE.play("sounds/bones.mp3");
                this.summoningBones.burst(Speck.factory(105), 5);
            } else {
                this.summoningBones.on = false;
            }
            this.summoningBones = null;
        }
        this.idle();
    }

    public void charge() {
        this.play(this.charging);
    }

    @Override
    public void zap(int cell) {
        super.zap(cell);
        if (this.ch instanceof Necromancer && ((Necromancer)this.ch).summoning) {
            if (this.summoningBones != null) {
                this.summoningBones.on = false;
            }
            this.summoningBones = CellEmitter.get(((Necromancer)this.ch).summoningPos);
            this.summoningBones.pour(Speck.factory(105), 0.2f);
            this.summoningBones.visible = Dungeon.level.heroFOV[((Necromancer)this.ch).summoningPos];
            if (this.visible || this.summoningBones.visible) {
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

