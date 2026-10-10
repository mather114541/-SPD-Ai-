/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Game;
import com.watabou.noosa.Gizmo;

public class GlowBlock
extends Gizmo {
    private CharSprite target;

    public GlowBlock(CharSprite target) {
        this.target = target;
    }

    @Override
    public void update() {
        super.update();
        this.target.tint(1.33f, 1.33f, 0.83f, 0.5f + 0.1f * (float)Math.cos(Math.PI * 2 * (double)Game.timeTotal));
    }

    public void darken() {
        this.target.resetColor();
        this.killAndErase();
    }

    public static GlowBlock lighten(CharSprite sprite) {
        GlowBlock glowBlock = new GlowBlock(sprite);
        if (sprite.parent != null) {
            sprite.parent.add(glowBlock);
        }
        return glowBlock;
    }
}

