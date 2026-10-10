/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class RainbowParticle
extends PixelParticle {
    public static final Emitter.Factory BURST = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((RainbowParticle)emitter.recycle(RainbowParticle.class)).resetBurst(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public RainbowParticle() {
        this.color(Random.Int(0x1000000));
        this.lifespan = 0.5f;
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.speed.set(Random.Float(-5.0f, 5.0f), Random.Float(-5.0f, 5.0f));
        this.left = this.lifespan;
    }

    public void resetBurst(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.speed.polar(Random.Float(6.283185f), Random.Float(16.0f, 32.0f));
        this.left = this.lifespan;
    }

    @Override
    public void update() {
        super.update();
        this.am = this.left / this.lifespan;
        this.size(5.0f - this.am * 4.0f);
    }
}

