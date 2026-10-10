/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.watabou.noosa.Game;

public class DiscardedItemSprite
extends ItemSprite {
    @Override
    public void drop() {
        this.scale.set(1.0f);
        this.am = 1.0f;
        if (this.emitter != null) {
            this.emitter.killAndErase();
        }
        this.origin.set(this.width / 2.0f, this.height - 8.0f);
        this.angularSpeed = 720.0f;
    }

    @Override
    public void update() {
        float f;
        super.update();
        this.scale.set(this.scale.x -= Game.elapsed);
        this.y += 12.0f * Game.elapsed;
        this.am -= Game.elapsed;
        if (f <= 0.0f) {
            this.remove();
        }
    }
}

