/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ClothArmor
extends Armor {
    public ClothArmor() {
        super(1);
        this.image = ItemSpriteSheet.ARMOR_CLOTH;
        this.bones = false;
    }
}

