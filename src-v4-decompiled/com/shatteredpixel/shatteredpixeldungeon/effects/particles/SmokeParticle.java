/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SmokeParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SmokeParticle)emitter.recycle(SmokeParticle.class)).reset(x, y);
        }
    };
    public static final Emitter.Factory SPEW = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SmokeParticle)emitter.recycle(SmokeParticle.class)).resetSpew(x, y);
        }
    };

    public SmokeParticle() {
        this.color(0x222222);
        this.acc.set(0.0f, -40.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan = Random.Float(0.6f, 1.0f);
        this.speed.set(Random.Float(-4.0f, 4.0f), Random.Float(-8.0f, 8.0f));
    }

    public void resetSpew(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.acc.set(-40.0f, 40.0f);
        this.left = this.lifespan = Random.Float(0.6f, 1.0f);
        this.speed.polar(Random.Float(5.3407073f, 5.654866f), Random.Float(30.0f, 60.0f));
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p > 0.8f ? 2.0f - 2.0f * p : p * 0.5f;
        this.size(16.0f - p * 8.0f);
    }
}

