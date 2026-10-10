/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GoldenKey
extends Key {
    public GoldenKey() {
        this(0);
    }

    public GoldenKey(int depth) {
        this.image = ItemSpriteSheet.GOLDEN_KEY;
        this.depth = depth;
    }
}

