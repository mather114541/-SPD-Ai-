/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class WebParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            for (int i = 0; i < 3; ++i) {
                ((WebParticle)emitter.recycle(WebParticle.class)).reset(x, y);
            }
        }
    };

    public WebParticle() {
        this.color(0xCCCCCC);
        this.lifespan = 2.0f;
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.left = this.lifespan;
        this.angle = Random.Float(360.0f);
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = p < 0.5f ? p : 1.0f - p;
        this.scale.y = 12.0f + p * 6.0f;
    }
}

