/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ChargrilledMeat
extends Food {
    public ChargrilledMeat() {
        this.image = ItemSpriteSheet.STEAK;
        this.energy = 150.0f;
    }

    @Override
    public int value() {
        return 8 * this.quantity;
    }

    public static Food cook(int quantity) {
        ChargrilledMeat result = new ChargrilledMeat();
        result.quantity = quantity;
        return result;
    }
}

