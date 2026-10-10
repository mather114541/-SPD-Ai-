/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;

public class SacrificialParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SacrificialParticle)emitter.recycle(SacrificialParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public SacrificialParticle() {
        this.color(0x4488EE);
        this.lifespan = 0.6f;
        this.acc.set(0.0f, -100.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y - 4.0f;
        this.left = this.lifespan;
        this.size = 4.0f;
        this.speed.set(0.0f);
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p > 0.75f ? (1.0f - p) * 4.0f : 1.0f;
    }
}

