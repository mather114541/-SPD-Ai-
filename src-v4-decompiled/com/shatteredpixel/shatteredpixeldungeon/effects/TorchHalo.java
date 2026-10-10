/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Halo;

public class TorchHalo
extends Halo {
    private CharSprite target;
    private float phase = 0.0f;

    public TorchHalo(CharSprite sprite) {
        super(20.0f, 0xFFDDCC, 0.2f);
        this.target = sprite;
        this.am = 0.0f;
    }

    @Override
    public void update() {
        super.update();
        if (this.phase < 0.0f) {
            float f;
            this.phase += Game.elapsed;
            if (f >= 0.0f) {
                this.killAndErase();
            } else {
                this.scale.set((2.0f + this.phase) * this.radius / 128.0f);
                this.am = -this.phase * this.brightness;
            }
        } else if (this.phase < 1.0f) {
            float f;
            this.phase += 2.0f * Game.elapsed;
            if (f >= 1.0f) {
                this.phase = 1.0f;
            }
            this.scale.set(this.phase * this.radius / 128.0f);
            this.am = this.phase * this.brightness;
        }
        this.point(this.target.x + this.target.width() / 2.0f, this.target.y + this.target.height() / 2.0f);
    }

    @Override
    public void draw() {
        Blending.setLightMode();
        super.draw();
        Blending.setNormalMode();
    }

    public void putOut() {
        this.phase = -1.0f;
    }
}

