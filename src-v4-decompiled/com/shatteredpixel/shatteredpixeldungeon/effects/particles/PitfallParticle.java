/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Random;

public class PitfallParticle
extends PixelParticle.Shrinking {
    public static final Emitter.Factory FACTORY4 = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((PitfallParticle)emitter.recycle(PitfallParticle.class)).reset(x, y, 4);
        }
    };
    public static final Emitter.Factory FACTORY8 = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((PitfallParticle)emitter.recycle(PitfallParticle.class)).reset(x, y, 8);
        }
    };

    public PitfallParticle() {
        this.color(0);
        this.angle = Random.Float(-30.0f, 30.0f);
    }

    public void reset(float x, float y, int size) {
        this.revive();
        this.x = x;
        this.y = y;
        this.lifespan = 1.0f;
        this.left = 1.0f;
        this.size = size;
    }
}

