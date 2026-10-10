/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Gizmo;

public class DarkBlock
extends Gizmo {
    private CharSprite target;

    public DarkBlock(CharSprite target) {
        this.target = target;
    }

    @Override
    public void update() {
        super.update();
        this.target.brightness(0.4f);
    }

    public void lighten() {
        this.target.resetColor();
        this.killAndErase();
    }

    public static DarkBlock darken(CharSprite sprite) {
        DarkBlock darkBlock = new DarkBlock(sprite);
        if (sprite.parent != null) {
            sprite.parent.add(darkBlock);
        }
        return darkBlock;
    }
}

