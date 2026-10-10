/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.PointF;

public class Beam
extends Image {
    private static final double A = 57.29577951308232;
    private float duration;
    private float timeLeft;

    private Beam(PointF s, PointF e, Effects.Type asset, float duration) {
        super(Effects.get(asset));
        this.origin.set(0.0f, this.height / 2.0f);
        this.x = s.x - this.origin.x;
        this.y = s.y - this.origin.y;
        float dx = e.x - s.x;
        float dy = e.y - s.y;
        this.angle = (float)(Math.atan2(dy, dx) * 57.29577951308232);
        this.scale.x = (float)Math.sqrt(dx * dx + dy * dy) / this.width;
        this.timeLeft = this.duration = duration;
    }

    @Override
    public void update() {
        float f;
        super.update();
        float p = this.timeLeft / this.duration;
        this.alpha(p);
        this.scale.set(this.scale.x, p);
        this.timeLeft -= Game.elapsed;
        if (f <= 0.0f) {
            this.killAndErase();
        }
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public static class HealthRay
    extends Beam {
        public HealthRay(PointF s, PointF e) {
            super(s, e, Effects.Type.HEALTH_RAY, 0.75f);
        }
    }

    public static class SunRay
    extends Beam {
        public SunRay(PointF s, PointF e) {
            super(s, e, Effects.Type.LIGHT_RAY, 1.0f);
            this.tint(1.0f, 1.0f, 0.25f, 1.0f);
        }
    }

    public static class LightRay
    extends Beam {
        public LightRay(PointF s, PointF e) {
            super(s, e, Effects.Type.LIGHT_RAY, 1.0f);
        }
    }

    public static class DeathRay
    extends Beam {
        public DeathRay(PointF s, PointF e) {
            super(s, e, Effects.Type.DEATH_RAY, 0.5f);
        }
    }
}

