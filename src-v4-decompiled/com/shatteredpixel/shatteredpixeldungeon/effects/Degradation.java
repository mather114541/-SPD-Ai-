/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.watabou.glwrap.Blending;
import com.watabou.noosa.Group;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class Degradation
extends Group {
    private static int[] WEAPON = new int[]{2, -2, 1, -1, 0, 0, -1, 1, -2, 2, -2, 0, 0, 2};
    private static int[] ARMOR = new int[]{-2, -1, -1, -1, 1, -1, 2, -1, -2, 0, -1, 0, 0, 0, 1, 0, 2, 0, -1, 1, 1, 1, -1, 2, 0, 2, 1, 2};
    private static int[] RING = new int[]{0, -1, -1, 0, 0, 0, 1, 0, -1, 1, 1, 1, -1, 2, 0, 2, 1, 2};
    private static int[] WAND = new int[]{2, -2, 1, -1, 0, 0, -1, 1, -2, 2, 1, -2, 2, -1};

    public static Degradation weapon(PointF p) {
        return new Degradation(p, WEAPON);
    }

    public static Degradation armor(PointF p) {
        return new Degradation(p, ARMOR);
    }

    public static Degradation ring(PointF p) {
        return new Degradation(p, RING);
    }

    public static Degradation wand(PointF p) {
        return new Degradation(p, WAND);
    }

    private Degradation(PointF p, int[] matrix) {
        for (int i = 0; i < matrix.length; i += 2) {
            this.add(new Speck(p.x, p.y, matrix[i], matrix[i + 1]));
            this.add(new Speck(p.x, p.y, matrix[i], matrix[i + 1]));
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
        private static final int COLOR = 0xFF4422;
        private static final int SIZE = 3;

        public Speck(float x0, float y0, int mx, int my) {
            this.color(0xFF4422);
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

