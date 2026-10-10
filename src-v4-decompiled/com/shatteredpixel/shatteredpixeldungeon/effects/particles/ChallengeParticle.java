/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class ChallengeParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((ChallengeParticle)emitter.recycle(ChallengeParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return false;
        }
    };

    public ChallengeParticle() {
        this.lifespan = 0.6f;
        this.color(0xFF0000);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.size = 8.0f;
        this.speed.set(Random.Float(-8.0f, 8.0f), Random.Float(-16.0f, -32.0f));
    }

    @Override
    public void update() {
        super.update();
        this.am = 1.0f - this.left / this.lifespan;
    }
}

