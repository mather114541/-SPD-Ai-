/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;

public abstract class SentrySprite
extends MobSprite {
    private final MovieClip.Animation charging;
    private Emitter chargeParticles;
    private float baseY = Float.NaN;

    protected abstract int texOffset();

    public SentrySprite() {
        this.texture("sprites/sentry.png");
        this.idle = new MovieClip.Animation(1, true);
        int ofs = this.texOffset();
        this.idle.frames(this.texture.uvRect(ofs * 8, 0.0f, (ofs + 1) * 8, 15.0f));
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.charging = this.idle.clone();
        this.die = this.idle.clone();
        this.zap = this.idle.clone();
        this.play(this.idle);
    }

    @Override
    public void zap(int pos) {
        this.idle();
        this.flash();
        this.emitter().burst(MagicMissile.WardParticle.UP, 2);
        if (Actor.findChar(pos) != null) {
            this.parent.add(new Beam.DeathRay(this.center(), Actor.findChar((int)pos).sprite.center()));
        } else {
            this.parent.add(new Beam.DeathRay(this.center(), DungeonTilemap.raisedTileCenterToWorld(pos)));
        }
        Sample.INSTANCE.play("sounds/ray.mp3");
        ((SentryRoom.Sentry)this.ch).onZapComplete();
    }

    @Override
    public void link(Char ch) {
        super.link(ch);
        this.chargeParticles = this.centerEmitter();
        this.chargeParticles.autoKill = false;
        this.chargeParticles.pour(MagicMissile.MagicParticle.ATTRACTING, 0.05f);
        this.chargeParticles.on = this.curAnim == this.charging;
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

    public void charge() {
        this.play(this.charging);
        if (this.visible && this.ch != null) {
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
    public void place(int cell) {
        super.place(cell);
        this.baseY = this.y;
    }

    @Override
    public void turnTo(int from, int to) {
    }

    @Override
    public void update() {
        super.update();
        if (this.chargeParticles != null) {
            this.chargeParticles.pos(this.center());
            this.chargeParticles.visible = this.visible;
        }
        if (!this.paused) {
            if (Float.isNaN(this.baseY)) {
                this.baseY = this.y;
            }
            this.y = this.baseY + (float)Math.sin(Game.timeTotal);
            this.shadowOffset = 0.25f - 0.8f * (float)Math.sin(Game.timeTotal);
        }
    }

    public static class VaultLaser
    extends SentrySprite {
        @Override
        protected int texOffset() {
            return 2;
        }
    }

    public static class VaultScan
    extends SentrySprite {
        @Override
        protected int texOffset() {
            return 1;
        }
    }

    public static class Red
    extends SentrySprite {
        @Override
        protected int texOffset() {
            return 0;
        }
    }
}

