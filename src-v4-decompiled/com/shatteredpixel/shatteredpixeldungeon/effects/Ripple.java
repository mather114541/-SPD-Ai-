/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.Effects;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class Ripple
extends Image {
    private static final float TIME_TO_FADE = 0.5f;
    private float time;

    public Ripple() {
        super(Effects.get(Effects.Type.RIPPLE));
    }

    public void reset(int p) {
        this.revive();
        this.x = p % Dungeon.level.width() * 16;
        this.y = p / Dungeon.level.width() * 16;
        this.origin.set(this.width / 2.0f, this.height / 2.0f);
        this.scale.set(0.0f);
        this.time = 0.5f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.time -= Game.elapsed;
        if (f <= 0.0f) {
            this.kill();
        } else {
            float p = this.time / 0.5f;
            this.scale.set(1.0f - p);
            this.alpha(p);
        }
    }
}

