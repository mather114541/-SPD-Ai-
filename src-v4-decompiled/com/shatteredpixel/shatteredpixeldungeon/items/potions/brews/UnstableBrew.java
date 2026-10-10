/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;

public class UnstableBrew
extends Brew {
    private static HashMap<Class<? extends Potion>, Float> potionChances = new HashMap();

    public UnstableBrew() {
        this.image = ItemSpriteSheet.BREW_UNSTABLE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add("DRINK");
        return actions;
    }

    @Override
    public String defaultAction() {
        return "CHOOSE";
    }

    @Override
    public void apply(Hero hero) {
        if (Dungeon.isChallenged(4)) {
            potionChances.put(PotionOfHealing.class, Float.valueOf(0.0f));
        }
        Potion p = Reflection.newInstance(Random.chances(potionChances));
        while (mustThrowPots.contains(p.getClass())) {
            p = Reflection.newInstance(Random.chances(potionChances));
        }
        p.anonymize();
        p.apply(hero);
        if (Dungeon.isChallenged(4)) {
            potionChances.put(PotionOfHealing.class, Float.valueOf(3.0f));
        }
    }

    @Override
    public void shatter(int cell) {
        Potion p = Reflection.newInstance(Random.chances(potionChances));
        while (!mustThrowPots.contains(p.getClass()) && !canThrowPots.contains(p.getClass())) {
            p = Reflection.newInstance(Random.chances(potionChances));
        }
        p.anonymize();
        curItem = p;
        p.shatter(cell);
    }

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public int value() {
        return 40 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 8 * this.quantity;
    }

    static {
        potionChances.put(PotionOfHealing.class, Float.valueOf(3.0f));
        potionChances.put(PotionOfMindVision.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfFrost.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfLiquidFlame.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfToxicGas.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfHaste.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfInvisibility.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfLevitation.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfParalyticGas.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfPurity.class, Float.valueOf(2.0f));
        potionChances.put(PotionOfExperience.class, Float.valueOf(1.0f));
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            boolean potion = false;
            boolean seed = false;
            for (Item i : ingredients) {
                if (i instanceof Plant.Seed) {
                    seed = true;
                    continue;
                }
                if (!ExoticPotion.regToExo.containsKey(i.getClass()) && !ExoticPotion.regToExo.containsValue(i.getClass())) continue;
                potion = true;
            }
            return potion && seed;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 1;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            for (Item i : ingredients) {
                i.quantity(i.quantity() - 1);
            }
            return this.sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new UnstableBrew();
        }
    }
}

