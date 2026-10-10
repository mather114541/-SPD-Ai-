/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class PurpleParticle
extends PixelParticle {
    public static final Emitter.Factory MISSILE = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((PurpleParticle)emitter.recycle(PurpleParticle.class)).reset(x, y);
        }
    };
    public static final Emitter.Factory BURST = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((PurpleParticle)emitter.recycle(PurpleParticle.class)).resetBurst(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public PurpleParticle() {
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
        this.color(ColorMath.interpolate(0x220066, 0xFF0044, this.am));
    }
}

