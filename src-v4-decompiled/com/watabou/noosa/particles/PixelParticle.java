/*
 * Decompiled with CFR 0.152.
 */
package com.watabou.noosa.particles;

import com.watabou.noosa.Game;
import com.watabou.noosa.PseudoPixel;

public class PixelParticle
extends PseudoPixel {
    protected float size;
    protected float lifespan;
    protected float left;

    public PixelParticle() {
        this.origin.set(0.5f);
    }

    public void reset(float x, float y, int color, float size, float lifespan) {
        this.revive();
        this.x = x;
        this.y = y;
        this.color(color);
        this.size = size;
        this.size(this.size);
        this.left = this.lifespan = lifespan;
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.left -= Game.elapsed;
        if (f <= 0.0f) {
            this.kill();
        }
    }

    public static class Shrinking
    extends PixelParticle {
        @Override
        public void update() {
            super.update();
            this.size(this.size * this.left / this.lifespan);
        }
    }
}

