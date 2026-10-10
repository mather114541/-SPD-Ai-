/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class GooBlob
extends Item {
    public GooBlob() {
        this.image = ItemSpriteSheet.BLOB;
        this.stackable = true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return this.quantity * 30;
    }

    @Override
    public int energyVal() {
        return this.quantity * 3;
    }
}

