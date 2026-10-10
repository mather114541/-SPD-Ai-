/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.ColorMath;
import com.watabou.utils.Random;

public class LeafParticle
extends PixelParticle.Shrinking {
    public static int color1;
    public static int color2;
    public static final Emitter.Factory GENERAL;
    public static final Emitter.Factory LEVEL_SPECIFIC;

    public LeafParticle() {
        this.lifespan = 1.2f;
        this.acc.set(0.0f, 25.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.x = x;
        this.y = y;
        this.speed.set(Random.Float(-8.0f, 8.0f), -20.0f);
        this.left = this.lifespan;
        this.size = Random.Float(2.0f, 3.0f);
    }

    static {
        GENERAL = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                LeafParticle p = (LeafParticle)emitter.recycle(LeafParticle.class);
                p.color(ColorMath.random(17408, 0x88CC44));
                p.reset(x, y);
            }
        };
        LEVEL_SPECIFIC = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                LeafParticle p = (LeafParticle)emitter.recycle(LeafParticle.class);
                p.color(ColorMath.random(Dungeon.level.color1, Dungeon.level.color2));
                p.reset(x, y);
            }
        };
    }
}

