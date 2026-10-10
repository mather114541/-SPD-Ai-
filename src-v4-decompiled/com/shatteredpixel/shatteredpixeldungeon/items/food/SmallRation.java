/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class SmallRation
extends Food {
    public SmallRation() {
        this.image = ItemSpriteSheet.OVERPRICED;
        this.energy = 150.0f;
    }

    @Override
    public int value() {
        return 10 * this.quantity;
    }
}

