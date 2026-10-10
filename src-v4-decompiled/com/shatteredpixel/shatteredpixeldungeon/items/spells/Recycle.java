/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.InventorySpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

public class Recycle
extends InventorySpell {
    public Recycle() {
        this.image = ItemSpriteSheet.RECYCLE;
        this.talentFactor = 2.0f;
        this.talentChance = 0.083333336f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item instanceof Potion && !(item instanceof Elixir) && !(item instanceof Brew) || item instanceof Scroll || item instanceof Plant.Seed || item instanceof Runestone || item instanceof TippedDart;
    }

    @Override
    protected void onItemSelected(Item item) {
        Item result;
        do {
            if (item instanceof Potion) {
                result = Generator.randomUsingDefaults(Generator.Category.POTION);
                if (!(item instanceof ExoticPotion)) continue;
                result = Reflection.newInstance(ExoticPotion.regToExo.get(result.getClass()));
                continue;
            }
            if (item instanceof Scroll) {
                result = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                if (!(item instanceof ExoticScroll)) continue;
                result = Reflection.newInstance(ExoticScroll.regToExo.get(result.getClass()));
                continue;
            }
            result = item instanceof Plant.Seed ? Generator.randomUsingDefaults(Generator.Category.SEED) : (item instanceof Runestone ? Generator.randomUsingDefaults(Generator.Category.STONE) : TippedDart.randomTipped(1));
        } while (result.getClass() == item.getClass() || Challenges.isItemBlocked(result));
        item.detach(Recycle.curUser.belongings.backpack);
        GLog.p(Messages.get(this, "recycled", result.name()), new Object[0]);
        if (!result.collect()) {
            Dungeon.level.drop((Item)result, (int)Recycle.curUser.pos).sprite.drop();
        }
        Transmuting.show((Char)curUser, item, result);
        Recycle.curUser.sprite.emitter().start(Speck.factory(10), 0.2f, 10);
    }

    @Override
    public int value() {
        return (int)(60.0f * ((float)this.quantity / 12.0f));
    }

    @Override
    public int energyVal() {
        return (int)(12.0f * ((float)this.quantity / 12.0f));
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        private static final int OUT_QUANTITY = 12;

        public Recipe() {
            this.inputs = new Class[]{ScrollOfTransmutation.class};
            this.inQuantity = new int[]{1};
            this.cost = 12;
            this.output = Recycle.class;
            this.outQuantity = 12;
        }
    }
}

