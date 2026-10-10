/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.ItemStatusHandler;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.AquaBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfHoneyedHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCleansing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfCorrosiveGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfShroudingFog;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfSnapFreeze;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfStormClouds;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

public class Potion
extends Item {
    public static final String AC_DRINK = "DRINK";
    public static final String AC_CHOOSE = "CHOOSE";
    private static final float TIME_TO_DRINK = 1.0f;
    private static final LinkedHashMap<String, Integer> colors = new LinkedHashMap<String, Integer>(){
        {
            this.put("crimson", ItemSpriteSheet.POTION_CRIMSON);
            this.put("amber", ItemSpriteSheet.POTION_AMBER);
            this.put("golden", ItemSpriteSheet.POTION_GOLDEN);
            this.put("jade", ItemSpriteSheet.POTION_JADE);
            this.put("turquoise", ItemSpriteSheet.POTION_TURQUOISE);
            this.put("azure", ItemSpriteSheet.POTION_AZURE);
            this.put("indigo", ItemSpriteSheet.POTION_INDIGO);
            this.put("magenta", ItemSpriteSheet.POTION_MAGENTA);
            this.put("bistre", ItemSpriteSheet.POTION_BISTRE);
            this.put("charcoal", ItemSpriteSheet.POTION_CHARCOAL);
            this.put("silver", ItemSpriteSheet.POTION_SILVER);
            this.put("ivory", ItemSpriteSheet.POTION_IVORY);
        }
    };
    protected static final HashSet<Class<? extends Potion>> mustThrowPots = new HashSet();
    protected static final HashSet<Class<? extends Potion>> canThrowPots;
    protected static ItemStatusHandler<Potion> handler;
    protected String color;
    protected float talentFactor = 1.0f;
    protected float talentChance = 1.0f;
    protected boolean anonymous;

    public static void initColors() {
        handler = new ItemStatusHandler(Generator.Category.POTION.classes, colors);
    }

    public static void clearColors() {
        handler = null;
    }

    public static void save(Bundle bundle) {
        handler.save(bundle);
    }

    public static void saveSelectively(Bundle bundle, ArrayList<Item> items) {
        ArrayList<Class<? extends Item>> classes = new ArrayList<Class<? extends Item>>();
        for (Item i : items) {
            if (i instanceof ExoticPotion) {
                if (classes.contains(ExoticPotion.exoToReg.get(i.getClass()))) continue;
                classes.add(ExoticPotion.exoToReg.get(i.getClass()));
                continue;
            }
            if (!(i instanceof Potion) || classes.contains(i.getClass())) continue;
            classes.add(i.getClass());
        }
        handler.saveClassesSelectively(bundle, classes);
    }

    public static void restore(Bundle bundle) {
        handler = new ItemStatusHandler(Generator.Category.POTION.classes, colors, bundle);
    }

    public Potion() {
        this.stackable = true;
        this.defaultAction = AC_DRINK;
        this.anonymous = false;
        this.reset();
    }

    public void anonymize() {
        if (!this.isKnown()) {
            this.image = ItemSpriteSheet.POTION_HOLDER;
        }
        this.anonymous = true;
    }

    @Override
    public void reset() {
        super.reset();
        if (handler != null && handler.contains(this)) {
            this.image = handler.image(this);
            this.color = handler.label(this);
        } else {
            this.image = ItemSpriteSheet.POTION_CRIMSON;
            this.color = "crimson";
        }
    }

    @Override
    public String defaultAction() {
        if (this.isKnown() && mustThrowPots.contains(this.getClass())) {
            return "THROW";
        }
        if (this.isKnown() && canThrowPots.contains(this.getClass())) {
            return AC_CHOOSE;
        }
        return AC_DRINK;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_DRINK);
        return actions;
    }

    @Override
    public void execute(final Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_CHOOSE)) {
            GameScene.show(new WndUseItem(null, this));
        } else if (action.equals(AC_DRINK)) {
            if (this.isKnown() && mustThrowPots.contains(this.getClass())) {
                GameScene.show(new WndOptions(new ItemSprite(this), Messages.get(Potion.class, "harmful", new Object[0]), Messages.get(Potion.class, "sure_drink", new Object[0]), new String[]{Messages.get(Potion.class, "yes", new Object[0]), Messages.get(Potion.class, "no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            Potion.this.drink(hero);
                        }
                    }
                });
            } else {
                this.drink(hero);
            }
        }
    }

    @Override
    public void doThrow(final Hero hero) {
        if (this.isKnown() && !mustThrowPots.contains(this.getClass()) && !canThrowPots.contains(this.getClass())) {
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.get(Potion.class, "beneficial", new Object[0]), Messages.get(Potion.class, "sure_throw", new Object[0]), new String[]{Messages.get(Potion.class, "yes", new Object[0]), Messages.get(Potion.class, "no", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        Potion.super.doThrow(hero);
                    }
                }
            });
        } else {
            super.doThrow(hero);
        }
    }

    protected void drink(Hero hero) {
        this.detach(hero.belongings.backpack);
        hero.spend(1.0f);
        hero.busy();
        this.apply(hero);
        if (!rl.RLMode.headless) {
            Sample.INSTANCE.play("sounds/drink.mp3");
            hero.sprite.operate(hero.pos);
        }
        if (!this.anonymous) {
            Catalog.countUse(this.getClass());
            if (Random.Float() < this.talentChance) {
                Talent.onPotionUsed(curUser, Potion.curUser.pos, this.talentFactor);
            }
        }
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.map[cell] == 24 || Dungeon.level.pit[cell]) {
            super.onThrow(cell);
        } else {
            if (!(this instanceof AquaBrew) && !(this instanceof PotionOfStormClouds)) {
                Dungeon.level.pressCell(cell);
            }
            this.shatter(cell);
            if (!this.anonymous && (mustThrowPots.contains(this.getClass()) || canThrowPots.contains(this.getClass()) || this instanceof Brew)) {
                Catalog.countUse(this.getClass());
                if (Random.Float() < this.talentChance) {
                    Talent.onPotionUsed(curUser, cell, this.talentFactor);
                }
            }
        }
    }

    public void apply(Hero hero) {
        this.shatter(hero.pos);
    }

    public void shatter(int cell) {
        this.splash(cell);
        if (Dungeon.level.heroFOV[cell]) {
            GLog.i(Messages.get(Potion.class, "shatter", new Object[0]), new Object[0]);
            Sample.INSTANCE.play("sounds/shatter.mp3");
        }
    }

    @Override
    public void cast(Hero user, int dst) {
        super.cast(user, dst);
    }

    public boolean isKnown() {
        return this.anonymous || handler != null && handler.isKnown(this);
    }

    public void setKnown() {
        if (!this.anonymous) {
            if (!this.isKnown()) {
                handler.know(this);
                Potion.updateQuickslot();
            }
            if (Dungeon.hero.isAlive()) {
                Catalog.setSeen(this.getClass());
                Statistics.itemTypesDiscovered.add(this.getClass());
            }
        }
    }

    @Override
    public Item identify(boolean byHero) {
        super.identify(byHero);
        if (!this.isKnown()) {
            this.setKnown();
        }
        return this;
    }

    @Override
    public String name() {
        return this.isKnown() ? super.name() : Messages.get(this, this.color, new Object[0]);
    }

    @Override
    public String info() {
        return this.anonymous && (handler == null || !handler.isKnown(this)) ? this.desc() : super.info();
    }

    @Override
    public String desc() {
        return this.isKnown() ? super.desc() : Messages.get(this, "unknown_desc", new Object[0]);
    }

    @Override
    public boolean isIdentified() {
        return this.isKnown();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public static HashSet<Class<? extends Potion>> getKnown() {
        return handler.known();
    }

    public static HashSet<Class<? extends Potion>> getUnknown() {
        return handler.unknown();
    }

    public static boolean allKnown() {
        return handler != null && handler.known().size() == Generator.Category.POTION.classes.length;
    }

    protected int splashColor() {
        return this.anonymous ? 43775 : ItemSprite.pick(this.image, 5, 9);
    }

    protected void splash(int cell) {
        Char ch;
        Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
        if (fire != null) {
            fire.clear(cell);
        }
        if ((ch = Actor.findChar(cell)) != null && ch.alignment == Char.Alignment.ALLY) {
            Buff.detach(ch, Burning.class);
            Buff.detach(ch, Ooze.class);
        }
        if (Dungeon.level.heroFOV[cell]) {
            if (ch != null) {
                Splash.at(ch.sprite.center(), this.splashColor(), 5);
            } else {
                Splash.at(cell, this.splashColor(), 5);
            }
        }
    }

    @Override
    public int value() {
        return 30 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 6 * this.quantity;
    }

    static {
        mustThrowPots.add(PotionOfToxicGas.class);
        mustThrowPots.add(PotionOfLiquidFlame.class);
        mustThrowPots.add(PotionOfParalyticGas.class);
        mustThrowPots.add(PotionOfFrost.class);
        mustThrowPots.add(PotionOfCorrosiveGas.class);
        mustThrowPots.add(PotionOfSnapFreeze.class);
        mustThrowPots.add(PotionOfShroudingFog.class);
        mustThrowPots.add(PotionOfStormClouds.class);
        canThrowPots = new HashSet();
        canThrowPots.add(PotionOfPurity.class);
        canThrowPots.add(PotionOfLevitation.class);
        canThrowPots.add(PotionOfCleansing.class);
        canThrowPots.add(ElixirOfHoneyedHealing.class);
    }

    public static class SeedToPotion
    extends Recipe {
        public static HashMap<Class<? extends Plant.Seed>, Class<? extends Potion>> types = new HashMap();

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            if (ingredients.size() != 3) {
                return false;
            }
            for (Item ingredient : ingredients) {
                if (ingredient instanceof Plant.Seed && ingredient.quantity() >= 1 && types.containsKey(ingredient.getClass())) continue;
                return false;
            }
            return true;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 0;
        }

        /*
         * WARNING - void declaration
         */
        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            for (Item item : ingredients) {
                item.quantity(item.quantity() - 1);
            }
            ArrayList seeds = new ArrayList();
            for (Item i : ingredients) {
                if (seeds.contains(i.getClass())) continue;
                seeds.add(i.getClass());
            }
            Potion potion;
            if (seeds.size() == 2 && Random.Int(4) == 0 || seeds.size() == 3 && Random.Int(2) == 0) {
                potion = (Potion)Generator.randomUsingDefaults(Generator.Category.POTION);
            } else {
                potion = Reflection.newInstance(types.get(Random.element(ingredients).getClass()));
            }
            if (seeds.size() == 1) {
                potion.identify();
            }
            while (potion instanceof PotionOfHealing && Random.Int(10) < Dungeon.LimitedDrops.COOKING_HP.count) {
                potion = (Potion)Generator.randomUsingDefaults(Generator.Category.POTION);
            }
            if (potion instanceof PotionOfHealing) {
                ++Dungeon.LimitedDrops.COOKING_HP.count;
            }
            return potion;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new WndBag.Placeholder(ItemSpriteSheet.POTION_HOLDER){

                @Override
                public String name() {
                    return Messages.get(SeedToPotion.class, "name", new Object[0]);
                }

                @Override
                public String info() {
                    return "";
                }
            };
        }

        static {
            types.put(Blindweed.Seed.class, PotionOfInvisibility.class);
            types.put(Mageroyal.Seed.class, PotionOfPurity.class);
            types.put(Earthroot.Seed.class, PotionOfParalyticGas.class);
            types.put(Fadeleaf.Seed.class, PotionOfMindVision.class);
            types.put(Firebloom.Seed.class, PotionOfLiquidFlame.class);
            types.put(Icecap.Seed.class, PotionOfFrost.class);
            types.put(Rotberry.Seed.class, PotionOfStrength.class);
            types.put(Sorrowmoss.Seed.class, PotionOfToxicGas.class);
            types.put(Starflower.Seed.class, PotionOfExperience.class);
            types.put(Stormvine.Seed.class, PotionOfLevitation.class);
            types.put(Sungrass.Seed.class, PotionOfHealing.class);
            types.put(Swiftthistle.Seed.class, PotionOfHaste.class);
        }
    }

    public static class PlaceHolder
    extends Potion {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.POTION_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return ExoticPotion.regToExo.containsKey(item.getClass()) || ExoticPotion.regToExo.containsValue(item.getClass());
        }

        @Override
        public String info() {
            return "";
        }
    }
}

