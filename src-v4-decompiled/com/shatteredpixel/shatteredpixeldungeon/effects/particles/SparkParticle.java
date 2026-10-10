/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.Visual;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class SparkParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SparkParticle)emitter.recycle(SparkParticle.class)).reset(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };
    public static final Emitter.Factory STATIC = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((SparkParticle)emitter.recycle(SparkParticle.class)).resetStatic(x, y);
        }

        @Override
        public boolean lightMode() {
            return true;
        }
    };

    public SparkParticle() {
        this.size(2.0f);
        this.acc.set(0.0f, 50.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.size = 5.0f;
        this.left = this.lifespan = Random.Float(0.5f, 1.0f);
        this.speed.polar(-Random.Float(3.1415925f), Random.Float(20.0f, 40.0f));
    }

    public void resetStatic(float x, float y) {
        this.reset(x, y);
        this.left = this.lifespan = Random.Float(0.25f, 0.5f);
        this.acc.set(0.0f, 0.0f);
        this.speed.set(0.0f, 0.0f);
    }

    public void resetAttracting(float x, float y, Visual attracting) {
        this.reset(x, y);
        this.left = this.lifespan = Random.Float(0.2f, 0.35f);
        this.acc.set(0.0f);
        this.speed.set(attracting.x + attracting.width / 2.0f - x, attracting.y + attracting.height / 2.0f - y);
        this.speed.normalize().scale(48.0f);
        this.x -= this.speed.x / 8.0f;
        this.y -= this.speed.y / 8.0f;
    }

    public void setMaxSize(float value) {
        this.size = value;
    }

    @Override
    public void update() {
        super.update();
        this.size(Random.Float(this.size * this.left / this.lifespan));
    }
}

