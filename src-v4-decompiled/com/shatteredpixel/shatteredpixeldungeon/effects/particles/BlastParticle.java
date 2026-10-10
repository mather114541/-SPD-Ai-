/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class BlastParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((BlastParticle)emitter.recycle(BlastParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public BlastParticle() {
        this.color(0xEE7722);
        this.acc.set(0.0f, 50.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan = Random.Float();
        this.size = 8.0f;
        this.speed.polar(-Random.Float(3.1415925f), Random.Float(32.0f, 64.0f));
    }

    @Override
    public void update() {
        super.update();
        this.am = this.left > 0.8f ? (1.0f - this.left) * 5.0f : 1.0f;
    }
}

