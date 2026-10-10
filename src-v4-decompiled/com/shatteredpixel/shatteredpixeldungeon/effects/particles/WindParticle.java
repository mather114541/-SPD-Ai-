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

public class WindParticle
extends PixelParticle {
    public static final Emitter.Factory FACTORY = new Emitter.Factory(){

        @Override
        public void emit(Emitter emitter, int index, float x, float y) {
            ((WindParticle)emitter.recycle(WindParticle.class)).reset(x, y);
        }
    };
    private static float angle = Random.Float(6.283185f);
    private static PointF speed = new PointF().polar(angle, 5.0f);

    public WindParticle() {
        this.lifespan = Random.Float(1.0f, 2.0f);
        this.size = Random.Float(3.0f);
        this.scale.set(this.size);
    }

    public void reset(float x, float y) {
        this.revive();
        this.left = this.lifespan;
        ((PixelParticle)this).speed.set(speed);
        ((PixelParticle)this).speed.scale(this.size);
        this.x = x - ((PixelParticle)this).speed.x * this.lifespan / 2.0f;
        this.y = y - ((PixelParticle)this).speed.y * this.lifespan / 2.0f;
        speed = new PointF().polar(angle += Random.Float(-0.1f, 0.1f), 5.0f);
        this.am = 0.0f;
    }

    @Override
    public void update() {
        super.update();
        float p = this.left / this.lifespan;
        this.am = (p < 0.5f ? p : 1.0f - p) * this.size * 0.2f;
    }

    public static class Wind
    extends Emitter {
        private int pos;

        public Wind(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.tileToWorld(pos);
            this.pos(p.x, p.y, 16.0f, 16.0f);
            this.pour(FACTORY, 2.5f);
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

