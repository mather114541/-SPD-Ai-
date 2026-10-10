/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class ShadowParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory MISSILE = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((ShadowParticle)emitter.recycle(ShadowParticle.class)).reset(x, y);
        }
    };
    public static final Emitter.Factory CURSE = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((ShadowParticle)emitter.recycle(ShadowParticle.class)).resetCurse(x, y);
        }
    };
    public static final Emitter.Factory UP = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((ShadowParticle)emitter.recycle(ShadowParticle.class)).resetUp(x, y);
        }
    };

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.speed.set(Random.Float(-5.0f, 5.0f), Random.Float(-5.0f, 5.0f));
        this.size = 6.0f;
        this.lifespan = 0.5f;
        this.left = 0.5f;
    }

    public void resetCurse(float x, float y) {
        this.revive();
        this.size = 8.0f;
        this.lifespan = 0.5f;
        this.left = 0.5f;
        this.speed.polar(Random.Float(6.283185f), Random.Float(16.0f, 32.0f));
        this.x = x - this.speed.x * this.lifespan;
        this.y = y - this.speed.y * this.lifespan;
    }

    public void resetUp(float x, float y) {
        this.revive();
        this.speed.set(Random.Float(-8.0f, 8.0f), Random.Float(-32.0f, -48.0f));
        this.x = x;
        this.y = y;
        this.size = 6.0f;
        this.lifespan = 1.0f;
        this.left = 1.0f;
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.color(ColorMath.interpolate(0, 0x440044, p));
        this.am = p < 0.5f ? p * p * 4.0f : (1.0f - p) * 2.0f;
    }
}

