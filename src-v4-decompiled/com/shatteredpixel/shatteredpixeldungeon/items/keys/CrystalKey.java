/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.keys;

import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class CrystalKey
extends Key {
    public CrystalKey() {
        this(0);
    }

    public CrystalKey(int depth) {
        this.image = ItemSpriteSheet.CRYSTAL_KEY;
        this.depth = depth;
    }
}

