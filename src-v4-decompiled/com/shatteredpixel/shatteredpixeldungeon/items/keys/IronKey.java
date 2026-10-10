/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class IronKey
extends Key {
    public IronKey() {
        this(0);
    }

    public IronKey(int depth) {
        this.image = ItemSpriteSheet.IRON_KEY;
        this.depth = depth;
    }
}

