/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.food.PhantomMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import java.util.ArrayList;

public class MeatPie
extends Food {
    public MeatPie() {
        this.image = ItemSpriteSheet.MEAT_PIE;
        this.energy = 900.0f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        Buff.affect(hero, WellFed.class).reset();
    }

    @Override
    public int value() {
        return 40 * this.quantity;
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean pasty = false;
            boolean ration = false;
            boolean meat = false;
            for (Item ingredient : ingredients) {
                if (ingredient.quantity() <= 0) continue;
                if (ingredient instanceof Pasty || ingredient instanceof PhantomMeat) {
                    pasty = true;
                    continue;
                }
                if (ingredient.getClass() == Food.class) {
                    ration = true;
                    continue;
                }
                if (!(ingredient instanceof MysteryMeat) && !(ingredient instanceof StewedMeat) && !(ingredient instanceof ChargrilledMeat) && !(ingredient instanceof FrozenCarpaccio)) continue;
                meat = true;
            }
            return pasty && ration && meat;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 6;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            for (Item ingredient : ingredients) {
                ingredient.quantity(ingredient.quantity() - 1);
            }
            return this.sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new MeatPie();
        }
    }
}

