/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.plants;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;

public class BlandfruitBush
extends Plant {
    public BlandfruitBush() {
        this.image = 12;
    }

    @Override
    public void activate(Char ch) {
        Dungeon.level.drop((Item)new Blandfruit(), (int)this.pos).sprite.drop();
    }

    public static class Seed
    extends Plant.Seed {
        public Seed() {
            this.plantClass = BlandfruitBush.class;
        }
    }
}

