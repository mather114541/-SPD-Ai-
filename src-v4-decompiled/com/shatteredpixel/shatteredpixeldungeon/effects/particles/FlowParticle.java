/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class FlowParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((FlowParticle)emitter.recycle(FlowParticle.class)).reset(x, y);
        }
    };

    public FlowParticle() {
        this.lifespan = 0.6f;
        this.acc.set(0.0f, 32.0f);
        this.angularSpeed = Random.Float(-360.0f, 360.0f);
    }

    public void reset(float x, float y) {
        this.revive();
        this.left = this.lifespan;
        this.x = x;
        this.y = y;
        this.am = 0.0f;
        this.size(0.0f);
        this.speed.set(0.0f);
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = (p < 0.5f ? p : 1.0f - p) * 0.6f;
        this.size((1.0f - p) * 4.0f);
    }

    public static class Flow
    extends Emitter {
        private int pos;

        public Flow(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.tileToWorld(pos);
            this.pos(p.x, p.y + 16.0f - 1.0f, 16.0f, 0.0f);
            this.pour(FACTORY, 0.05f);
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                super.update();
            }
        }
    }
}

