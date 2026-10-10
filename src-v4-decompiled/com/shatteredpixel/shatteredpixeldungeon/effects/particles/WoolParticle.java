/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class WoolParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((WoolParticle)emitter.recycle(WoolParticle.class)).reset(x, y);
        }
    };

    public WoolParticle() {
        this.color(ColorMath.random(0x999999, 0xEEEEE0));
        this.acc.set(0.0f, -40.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan = Random.Float(0.6f, 1.0f);
        this.size = 5.0f;
        this.speed.set(Random.Float(-10.0f, 10.0f), Random.Float(-10.0f, 10.0f));
    }
}

