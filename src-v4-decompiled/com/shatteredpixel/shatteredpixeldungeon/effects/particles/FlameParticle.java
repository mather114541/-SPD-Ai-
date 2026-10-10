/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;

public class FlameParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((FlameParticle)emitter.recycle(FlameParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public FlameParticle() {
        this.color(0xEE7722);
        this.lifespan = 0.6f;
        this.acc.set(0.0f, -80.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.size = 4.0f;
        this.speed.set(0.0f);
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p > 0.8f ? (1.0f - p) * 5.0f : 1.0f;
    }
}

