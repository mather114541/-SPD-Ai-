/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.glwrap.Blending;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Identification
extends Group {
    private static int[] DOTS = new int[]{-1, -3, 0, -3, 1, -3, -1, -2, 1, -2, 1, -1, 0, 0, 1, 0, 0, 1, 0, 3};

    public Identification(PointF p) {
        for (int i = 0; i < DOTS.length; i += 2) {
            this.add(new Speck(p.x, p.y, DOTS[i], DOTS[i + 1]));
            this.add(new Speck(p.x, p.y, DOTS[i], DOTS[i + 1]));
        }
    }

    @Override
    public void update() {
        super.update();
        if (this.countLiving() == 0) {
            this.killAndErase();
        }
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public static class Speck
    extends PixelParticle {
        private static final int COLOR = 0x4488CC;
        private static final int SIZE = 3;

        public Speck(float x0, float y0, int mx, int my) {
            this.color(0x4488CC);
            float x1 = x0 + (float)(mx * 3);
            float y1 = y0 + (float)(my * 3);
            PointF p = new PointF().polar(Random.Float(6.283185f), 8.0f);
            float dx = x1 - (x0 += p.x);
            float dy = y1 - (y0 += p.y);
            this.x = x0;
            this.y = y0;
            this.speed.set(dx, dy);
            this.acc.set(-dx / 4.0f, -dy / 4.0f);
            this.lifespan = 2.0f;
            this.left = 2.0f;
        }

        @Override
        public void update() {
            super.update();
            this.am = 1.0f - Math.abs(this.left / this.lifespan - 0.5f) * 2.0f;
            this.am *= this.am;
            this.size(this.am * 3.0f);
        }
    }
}

