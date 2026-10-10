/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SnowParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SnowParticle)emitter.recycle(SnowParticle.class)).reset(x, y);
        }
    };

    public SnowParticle() {
        this.speed.set(0.0f, Random.Float(5.0f, 8.0f));
        this.lifespan = 1.2f;
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y - this.speed.y * this.lifespan;
        this.left = this.lifespan;
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = (p < 0.5f ? p : 1.0f - p) * 1.5f;
    }
}

