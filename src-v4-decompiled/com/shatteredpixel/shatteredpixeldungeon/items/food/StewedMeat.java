/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class StewedMeat
extends Food {
    public StewedMeat() {
        this.image = ItemSpriteSheet.STEWED;
        this.energy = 150.0f;
    }

    @Override
    public int value() {
        return 8 * this.quantity;
    }

    public static class threeMeat
    extends Recipe.SimpleRecipe {
        public threeMeat() {
            this.inputs = new Class[]{MysteryMeat.class};
            this.inQuantity = new int[]{3};
            this.cost = 2;
            this.output = StewedMeat.class;
            this.outQuantity = 3;
        }
    }

    public static class twoMeat
    extends Recipe.SimpleRecipe {
        public twoMeat() {
            this.inputs = new Class[]{MysteryMeat.class};
            this.inQuantity = new int[]{2};
            this.cost = 2;
            this.output = StewedMeat.class;
            this.outQuantity = 2;
        }
    }

    public static class oneMeat
    extends Recipe.SimpleRecipe {
        public oneMeat() {
            this.inputs = new Class[]{MysteryMeat.class};
            this.inQuantity = new int[]{1};
            this.cost = 1;
            this.output = StewedMeat.class;
            this.outQuantity = 1;
        }
    }
}

