/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;
import com.watabou.utils.PointF;

public class ShieldHalo
extends Halo {
    private CharSprite target;
    private float phase;

    public ShieldHalo(CharSprite sprite) {
        super((float)Math.sqrt(Math.pow(sprite.width() / 2.0f, 2.0) + Math.pow(sprite.height() / 2.0f, 2.0)), 0xBBAACC, 1.0f);
        this.am = -0.33f;
        this.aa = 0.33f;
        this.target = sprite;
        this.phase = 1.0f;
    }

    @Override
    public void update() {
        super.update();
        if (this.phase < 1.0f) {
            float f;
            this.phase -= Game.elapsed;
            if (f <= 0.0f) {
                this.killAndErase();
            } else {
                this.scale.set((2.0f - this.phase) * this.radius / 128.0f);
                this.am = this.phase * -1.0f;
                this.aa = this.phase * 1.0f;
            }
        }
        if (this.visible = this.target.visible) {
            PointF p = this.target.center();
            this.point(p.x, p.y);
        }
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public void putOut() {
        this.phase = 0.999f;
    }
}

