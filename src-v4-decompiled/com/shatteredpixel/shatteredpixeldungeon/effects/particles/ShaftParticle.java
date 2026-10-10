/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class ShaftParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((ShaftParticle)emitter.recycle(ShaftParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };
    private float offs;

    public ShaftParticle() {
        this.lifespan = 1.2f;
        this.speed.set(0.0f, -6.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.offs = -Random.Float(this.lifespan);
        this.left = this.lifespan - this.offs;
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p < 0.5f ? p : 1.0f - p;
        this.scale.x = (1.0f - p) * 4.0f;
        this.scale.y = 16.0f + (1.0f - p) * 16.0f;
    }
}

