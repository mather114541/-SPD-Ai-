/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.FrozenCarpaccio;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Random;

public class MysteryMeat
extends Food {
    public MysteryMeat() {
        this.image = ItemSpriteSheet.MEAT;
        this.energy = 150.0f;
    }

    @Override
    protected void satisfy(Hero hero) {
        super.satisfy(hero);
        MysteryMeat.effect(hero);
    }

    @Override
    public int value() {
        return 5 * this.quantity;
    }

    public static void effect(Hero hero) {
        switch (Random.Int(5)) {
            case 0: {
                GLog.w(Messages.get(MysteryMeat.class, "hot", new Object[0]), new Object[0]);
                Buff.affect(hero, Burning.class).reignite(hero);
                break;
            }
            case 1: {
                GLog.w(Messages.get(MysteryMeat.class, "legs", new Object[0]), new Object[0]);
                Buff.prolong(hero, Roots.class, 10.0f);
                break;
            }
            case 2: {
                GLog.w(Messages.get(MysteryMeat.class, "not_well", new Object[0]), new Object[0]);
                Buff.affect(hero, Poison.class).set(hero.HT / 5);
                break;
            }
            case 3: {
                GLog.w(Messages.get(MysteryMeat.class, "stuffed", new Object[0]), new Object[0]);
                Buff.prolong(hero, Slow.class, 10.0f);
            }
        }
    }

    public static class PlaceHolder
    extends MysteryMeat {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.FOOD_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof MysteryMeat || item instanceof StewedMeat || item instanceof ChargrilledMeat || item instanceof FrozenCarpaccio;
        }

        @Override
        public String info() {
            return "";
        }
    }
}

