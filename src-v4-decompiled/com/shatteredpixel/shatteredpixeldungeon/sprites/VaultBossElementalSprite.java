/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultBossElemental;
import com.shatteredpixel.shatteredpixeldungeon.effects.Lightning;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class VaultBossElementalSprite
extends MobSprite {
    private Emitter particles;
    private VaultBossElemental.ElementalForm form;

    public VaultBossElementalSprite() {
        this.perspectiveRaise = 0.125f;
        this.texture("sprites/vault_boss_elemental.png");
        this.updateAnimations();
    }

    public void setForm(VaultBossElemental.ElementalForm form) {
        this.form = form;
        this.updateAnimations();
    }

    @Override
    public void linkVisuals(Char ch) {
        super.linkVisuals(ch);
        if (ch instanceof VaultBossElemental && ((VaultBossElemental)ch).curForm() != null) {
            this.form = ((VaultBossElemental)ch).curForm();
        }
        this.updateAnimations();
    }

    public void updateForm() {
        if (this.ch instanceof VaultBossElemental && ((VaultBossElemental)this.ch).curForm() != null) {
            this.form = ((VaultBossElemental)this.ch).curForm();
        }
        this.updateAnimations();
    }

    private void updateAnimations() {
        if (this.form == null) {
            this.form = VaultBossElemental.ElementalForm.values()[Random.Int(3)];
        }
        int c = 16 * this.form.ordinal();
        TextureFilm frames = new TextureFilm(this.texture, 31, 30);
        this.idle = new MovieClip.Animation(10, true);
        this.idle.frames(frames, c + 0, c + 1, c + 2, c + 3);
        this.run = new MovieClip.Animation(12, true);
        this.run.frames(frames, c + 0, c + 1, c + 2, c + 3);
        this.attack = new MovieClip.Animation(12, false);
        this.attack.frames(frames, c + 4, c + 5, c + 6);
        this.zap = this.attack.clone();
        this.die = new MovieClip.Animation(15, false);
        this.die.frames(frames, c + 7, c + 8, c + 9, c + 10, c + 11, c + 12, c + 13, c + 12);
        this.operate = this.attack.clone();
        this.play(this.idle);
        if (this.particles != null) {
            this.particles.killAndErase();
            this.particles = this.createEmitter();
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
    public void zap(int cell) {
        this.zap(cell, null);
        switch (this.form) {
            default: {
                MagicMissile.boltFromChar(this.parent, 2, this, cell, new Callback(){

                    @Override
                    public void call() {
                        ((VaultBossElemental)VaultBossElementalSprite.this.ch).onZapComplete();
                    }
                });
                Sample.INSTANCE.play("sounds/zap.mp3");
                break;
            }
            case FROST: {
                ((VaultBossElemental)this.ch).onZapComplete();
                Sample.INSTANCE.play("sounds/shatter.mp3");
                break;
            }
            case SHOCK: {
                this.zap(cell, null);
                Ballistica b = new Ballistica(this.ch.pos, cell, 4);
                cell = b.collisionPos;
                ((VaultBossElemental)this.ch).onZapComplete();
                this.parent.add(new Lightning(this.center(), cell, null));
                Sample.INSTANCE.play("sounds/lightning.mp3");
            }
        }
    }

    private Emitter createEmitter() {
        Emitter emitter = this.emitter();
        emitter.pos(this, 8.0f, 8.0f, 16.0f, 16.0f);
        emitter.fillTarget = false;
        switch (this.form) {
            default: {
                emitter.pour(FlameParticle.FACTORY, 0.03f);
                break;
            }
            case FROST: {
                emitter.pour(MagicMissile.MagicParticle.FACTORY, 0.03f);
                break;
            }
            case SHOCK: {
                emitter.pour(SparkParticle.STATIC, 0.03f);
            }
        }
        return emitter;
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        if (this.particles == null) {
            this.particles = this.createEmitter();
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.particles != null) {
            this.particles.visible = this.visible;
        }
    }

    @Override
    public void die() {
        super.die();
        if (this.particles != null) {
            this.particles.on = false;
        }
    }

    @Override
    public void kill() {
        super.kill();
        if (this.particles != null) {
            this.particles.killAndErase();
        }
    }

    @Override
    public int blood() {
        switch (this.form) {
            default: {
                return -17613;
            }
            case FROST: {
                return -7412737;
            }
            case SHOCK: 
        }
        return -123;
    }
}

