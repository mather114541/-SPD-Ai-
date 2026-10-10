/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class BloodParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((BloodParticle)emitter.recycle(BloodParticle.class)).reset(x, y);
        }
    };
    public static final Emitter.Factory BURST = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((BloodParticle)emitter.recycle(BloodParticle.class)).resetBurst(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public BloodParticle() {
        this.color(0xCC0000);
        this.lifespan = 0.8f;
        this.acc.set(0.0f, 40.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.size = 4.0f;
        this.speed.set(0.0f);
    }

    public void resetBurst(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.speed.polar(Random.Float(6.283185f), Random.Float(16.0f, 32.0f));
        this.size = 5.0f;
        this.left = 0.5f;
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p > 0.6f ? (1.0f - p) * 2.5f : 1.0f;
    }
}

