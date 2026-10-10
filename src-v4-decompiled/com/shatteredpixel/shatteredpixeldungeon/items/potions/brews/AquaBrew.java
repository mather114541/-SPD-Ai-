/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class AquaBrew
extends Brew {
    public AquaBrew() {
        this.image = ItemSpriteSheet.BREW_AQUA;
        this.talentChance = 0.125f;
    }

    @Override
    public void shatter(int cell) {
        int userPos;
        GeyserTrap geyser = new GeyserTrap();
        geyser.pos = cell;
        geyser.source = this;
        int n = userPos = curUser == null ? cell : AquaBrew.curUser.pos;
        if (userPos != cell) {
            Ballistica aim = new Ballistica(userPos, cell, 1);
            if (aim.path.size() > aim.dist + 1) {
                geyser.centerKnockBackDirection = aim.path.get(aim.dist + 1);
            }
        }
        geyser.activate();
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 8.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 8.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 8;

        public Recipe() {
            this.inputs = new Class[]{PotionOfStormClouds.class};
            this.inQuantity = new int[]{1};
            this.cost = 8;
            this.output = AquaBrew.class;
            this.outQuantity = 8;
        }
    }
}

