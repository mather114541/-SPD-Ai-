/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Blandfruit;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MeatPie;
import com.shatteredpixel.shatteredpixeldungeon.items.food.StewedMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.BlizzardBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.CausticBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.InfernalBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.ShockingBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfDragonsBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfFeatherFall;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfIcyTouch;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfToxicEssence;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Alchemize;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.CurseInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.PhaseShift;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.ReclaimTrap;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Recycle;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.SummonElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.TelekineticGrab;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.WildEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public abstract class Recipe {
    private static Recipe[] variableRecipes = new Recipe[0];
    private static Recipe[] oneIngredientRecipes = new Recipe[]{new Scroll.ScrollToStone(), new ExoticPotion.PotionToExotic(), new ExoticScroll.ScrollToExotic(), new ArcaneResin.Recipe(), new LiquidMetal.Recipe(), new BlizzardBrew.Recipe(), new InfernalBrew.Recipe(), new AquaBrew.Recipe(), new ShockingBrew.Recipe(), new ElixirOfDragonsBlood.Recipe(), new ElixirOfIcyTouch.Recipe(), new ElixirOfToxicEssence.Recipe(), new ElixirOfMight.Recipe(), new ElixirOfFeatherFall.Recipe(), new MagicalInfusion.Recipe(), new BeaconOfReturning.Recipe(), new PhaseShift.Recipe(), new Recycle.Recipe(), new TelekineticGrab.Recipe(), new SummonElemental.Recipe(), new StewedMeat.oneMeat(), new TrinketCatalyst.Recipe(), new Trinket.UpgradeTrinket()};
    private static Recipe[] twoIngredientRecipes = new Recipe[]{new Blandfruit.CookFruit(), new Bomb.EnhanceBomb(), new UnstableBrew.Recipe(), new CausticBrew.Recipe(), new ElixirOfArcaneArmor.Recipe(), new ElixirOfAquaticRejuvenation.Recipe(), new ElixirOfHoneyedHealing.Recipe(), new UnstableSpell.Recipe(), new Alchemize.Recipe(), new CurseInfusion.Recipe(), new ReclaimTrap.Recipe(), new WildEnergy.Recipe(), new StewedMeat.twoMeat()};
    private static Recipe[] threeIngredientRecipes = new Recipe[]{new Potion.SeedToPotion(), new StewedMeat.threeMeat(), new MeatPie.Recipe()};

    public abstract boolean testIngredients(ArrayList<Item> var1);

    public abstract int cost(ArrayList<Item> var1);

    public abstract Item brew(ArrayList<Item> var1);

    public abstract Item sampleOutput(ArrayList<Item> var1);

    public static ArrayList<Recipe> findRecipes(ArrayList<Item> ingredients) {
        ArrayList<Recipe> result;
        block5: {
            block6: {
                block4: {
                    result = new ArrayList<Recipe>();
                    for (Recipe recipe : variableRecipes) {
                        if (!recipe.testIngredients(ingredients)) continue;
                        result.add(recipe);
                    }
                    if (ingredients.size() != 1) break block4;
                    for (Recipe recipe : oneIngredientRecipes) {
                        if (!recipe.testIngredients(ingredients)) continue;
                        result.add(recipe);
                    }
                    break block5;
                }
                if (ingredients.size() != 2) break block6;
                for (Recipe recipe : twoIngredientRecipes) {
                    if (!recipe.testIngredients(ingredients)) continue;
                    result.add(recipe);
                }
                break block5;
            }
            if (ingredients.size() != 3) break block5;
            for (Recipe recipe : threeIngredientRecipes) {
                if (!recipe.testIngredients(ingredients)) continue;
                result.add(recipe);
            }
        }
        return result;
    }

    public static boolean usableInRecipe(Item item) {
        if (item instanceof EquipableItem) {
            return item.cursedKnown && !item.cursed && item instanceof MissileWeapon && item.isUpgradable();
        }
        if (item instanceof Wand) {
            return item.cursedKnown && !item.cursed;
        }
        return !item.cursed;
    }

    public static abstract class SimpleRecipe
    extends Recipe {
        protected Class<? extends Item>[] inputs;
        protected int[] inQuantity;
        protected int cost;
        protected Class<? extends Item> output;
        protected int outQuantity;

        public ArrayList<Item> getIngredients() {
            ArrayList<Item> result = new ArrayList<Item>();
            for (int i = 0; i < this.inputs.length; ++i) {
                Item ingredient = Reflection.newInstance(this.inputs[i]);
                ingredient.quantity(this.inQuantity[i]);
                result.add(ingredient);
            }
            return result;
        }

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            int[] needed = (int[])this.inQuantity.clone();
            block0: for (Item ingredient : ingredients) {
                if (!ingredient.isIdentified()) {
                    return false;
                }
                for (int i = 0; i < this.inputs.length; ++i) {
                    if (ingredient.getClass() != this.inputs[i]) continue;
                    int n = i;
                    needed[n] = needed[n] - ingredient.quantity();
                    continue block0;
                }
            }
            for (Object i : (Object)needed) {
                if (i <= 0) continue;
                return false;
            }
            return true;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return this.cost;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            int[] needed = (int[])this.inQuantity.clone();
            for (Item ingredient : ingredients) {
                for (int i = 0; i < this.inputs.length; ++i) {
                    if (ingredient.getClass() != this.inputs[i] || needed[i] <= 0) continue;
                    if (needed[i] <= ingredient.quantity()) {
                        ingredient.quantity(ingredient.quantity() - needed[i]);
                        needed[i] = 0;
                        continue;
                    }
                    int n = i;
                    needed[n] = needed[n] - ingredient.quantity();
                    ingredient.quantity(0);
                }
            }
            return this.sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            try {
                Item result = Reflection.newInstance(this.output);
                result.quantity(this.outQuantity);
                return result;
            }
            catch (Exception e) {
                ShatteredPixelDungeon.reportException(e);
                return null;
            }
        }
    }
}

