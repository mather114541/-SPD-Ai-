/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class CorrosionParticle
extends PixelParticle {
    public static final Emitter.Factory MISSILE = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((CorrosionParticle)emitter.recycle(CorrosionParticle.class)).resetMissile(x, y);
        }

        @Override
        public boolean lightMode() {
            return false;
        }
    };
    public static final Emitter.Factory SPLASH = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((CorrosionParticle)emitter.recycle(CorrosionParticle.class)).resetSplash(x, y);
        }

        @Override
        public boolean lightMode() {
            return false;
        }
    };

    public CorrosionParticle() {
        this.lifespan = 0.6f;
        this.acc.set(0.0f, 30.0f);
    }

    public void resetMissile(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.speed.polar(-Random.Float(3.1415925f), Random.Float(6.0f));
    }

    public void resetSplash(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.speed.polar(Random.Float(3.1415925f), Random.Float(10.0f, 20.0f));
    }

    @Override
    public void update() {
        super.update();
        this.am = this.left / this.lifespan;
        this.size(4.0f - this.am * 3.0f);
        this.color(ColorMath.interpolate(0xFF8800, 0xAAAAAA, this.am));
    }
}

