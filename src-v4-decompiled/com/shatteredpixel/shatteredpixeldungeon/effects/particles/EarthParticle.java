/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class EarthParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((EarthParticle)emitter.recycle(EarthParticle.class)).reset(x, y);
        }
    };
    public static final Emitter.Factory SMALL = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((EarthParticle)emitter.recycle(EarthParticle.class)).resetSmall(x, y);
        }
    };
    public static final Emitter.Factory FALLING = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((EarthParticle)emitter.recycle(EarthParticle.class)).resetFalling(x, y);
        }
    };

    public EarthParticle() {
        this.color(ColorMath.random(0x444444, 0x777766));
        this.angle = Random.Float(-30.0f, 30.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.lifespan = 0.5f;
        this.left = 0.5f;
        this.size = 16.0f;
        this.acc.y = 0.0f;
        this.speed.y = 0.0f;
        this.angularSpeed = 0.0f;
    }

    public void resetSmall(float x, float y) {
        this.reset(x, y);
        this.lifespan = 1.0f;
        this.left = 1.0f;
        this.size = 8.0f;
    }

    public void resetFalling(float x, float y) {
        this.reset(x, y);
        this.lifespan = 1.0f;
        this.left = 1.0f;
        this.size = 8.0f;
        this.acc.y = 30.0f;
        this.speed.y = -5.0f;
        this.angularSpeed = Random.Float(-90.0f, 90.0f);
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.size((p < 0.5f ? p : 1.0f - p) * this.size);
    }
}

