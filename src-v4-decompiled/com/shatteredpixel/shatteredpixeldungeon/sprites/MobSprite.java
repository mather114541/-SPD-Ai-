/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class MobSprite
extends CharSprite {
    private static final float FADE_TIME = 3.0f;
    private static final float FALL_TIME = 1.0f;

    @Override
    public void update() {
        this.sleeping = this.ch != null && this.ch.isAlive() && ((Mob)this.ch).state == ((Mob)this.ch).SLEEPING;
        super.update();
    }

    @Override
    public void onComplete(MovieClip.Animation anim) {
        super.onComplete(anim);
        if (anim == this.die && this.parent != null) {
            this.parent.add(new AlphaTweener(this, 0.0f, 3.0f){

                @Override
                protected void onComplete() {
                    MobSprite.this.killAndErase();
                }
            });
        }
    }

    public void fall() {
        this.origin.set(this.width / 2.0f, this.height - 8.0f);
        this.angularSpeed = Random.Int(2) == 0 ? -720.0f : 720.0f;
        this.am = 1.0f;
        this.hideEmo();
        if (this.health != null) {
            this.health.killAndErase();
        }
        if (this.parent != null) {
            this.parent.add(new ScaleTweener(this, new PointF(0.0f, 0.0f), 1.0f){

                @Override
                protected void onComplete() {
                    MobSprite.this.killAndErase();
                    this.parent.erase(this);
                }

                @Override
                protected void updateValues(float progress) {
                    super.updateValues(progress);
                    MobSprite.this.y += 12.0f * Game.elapsed;
                    MobSprite.this.am = 1.0f - progress;
                }
            });
        }
    }
}

