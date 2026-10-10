/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;

public class WardSprite
extends MobSprite {
    private MovieClip.Animation[] tierIdles = new MovieClip.Animation[7];
    private float baseY = Float.NaN;

    public WardSprite() {
        this.texture("sprites/wards.png");
        this.tierIdles[1] = new MovieClip.Animation(1, true);
        this.tierIdles[1].frames(this.texture.uvRect(0.0f, 0.0f, 9.0f, 10.0f));
        this.tierIdles[2] = new MovieClip.Animation(1, true);
        this.tierIdles[2].frames(this.texture.uvRect(10.0f, 0.0f, 21.0f, 12.0f));
        this.tierIdles[3] = new MovieClip.Animation(1, true);
        this.tierIdles[3].frames(this.texture.uvRect(22.0f, 0.0f, 37.0f, 16.0f));
        this.tierIdles[4] = new MovieClip.Animation(1, true);
        this.tierIdles[4].frames(this.texture.uvRect(38.0f, 0.0f, 44.0f, 13.0f));
        this.tierIdles[5] = new MovieClip.Animation(1, true);
        this.tierIdles[5].frames(this.texture.uvRect(45.0f, 0.0f, 51.0f, 15.0f));
        this.tierIdles[6] = new MovieClip.Animation(1, true);
        this.tierIdles[6].frames(this.texture.uvRect(52.0f, 0.0f, 60.0f, 15.0f));
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
        ((WandOfWarding.Ward)this.ch).onZapComplete();
    }

    @Override
    public void turnTo(int from, int to) {
    }

    @Override
    public void die() {
        super.die();
        this.play(this.idle, true);
        this.emitter().burst(MagicMissile.WardParticle.UP, 10);
        this.parent.add(new AlphaTweener(this, 0.0f, 2.0f){

            @Override
            protected void onComplete() {
                WardSprite.this.killAndErase();
                this.parent.erase(this);
            }
        });
    }

    @Override
    public void resetColor() {
        super.resetColor();
        if (this.ch instanceof WandOfWarding.Ward) {
            WandOfWarding.Ward ward = (WandOfWarding.Ward)this.ch;
            if (ward.tier <= 3) {
                this.brightness(Math.max(0.2f, 1.0f - (float)ward.totalZaps / (float)(2 * ward.tier - 1)));
            }
        }
    }

    @Override
    public void linkVisuals(Char ch) {
        if (ch instanceof WandOfWarding.Ward) {
            this.updateTier(((WandOfWarding.Ward)ch).tier);
        } else {
            this.updateTier(5);
        }
    }

    public void updateTier(int tier) {
        this.idle = this.tierIdles[tier];
        this.run = this.idle.clone();
        this.attack = this.idle.clone();
        this.die = this.idle.clone();
        if (this.parent != null) {
            this.parent.sendToBack(this);
        }
        this.resetColor();
        if (this.ch != null) {
            this.place(this.ch.pos);
        }
        this.idle();
        if (tier <= 3) {
            this.shadowHeight = 1.0f;
            this.shadowWidth = 1.0f;
            this.perspectiveRaise = (16.0f - this.height()) / 32.0f;
        } else {
            this.shadowWidth = 1.2f;
            this.shadowHeight = 0.25f;
            this.perspectiveRaise = 0.375f;
        }
    }

    @Override
    public void place(int cell) {
        super.place(cell);
        this.baseY = this.y;
    }

    @Override
    public void update() {
        super.update();
        if (this.perspectiveRaise >= 0.375f && !this.paused) {
            if (Float.isNaN(this.baseY)) {
                this.baseY = this.y;
            }
            this.y = this.baseY + (float)Math.sin(Game.timeTotal);
            this.shadowOffset = 0.25f - 0.8f * (float)Math.sin(Game.timeTotal);
        }
    }

    @Override
    public int blood() {
        return -3394561;
    }
}

