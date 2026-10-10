/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.food;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
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
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class Blandfruit
extends Food {
    public Potion potionAttrib = null;
    public ItemSprite.Glowing potionGlow = null;
    public static final String POTIONATTRIB = "potionattrib";

    public Blandfruit() {
        this.stackable = true;
        this.image = ItemSpriteSheet.BLANDFRUIT;
        this.energy = 450.0f;
        this.bones = true;
    }

    @Override
    public boolean isSimilar(Item item) {
        if (super.isSimilar(item)) {
            Blandfruit other = (Blandfruit)item;
            if (this.potionAttrib == null && other.potionAttrib == null) {
                return true;
            }
            if (this.potionAttrib != null && other.potionAttrib != null && this.potionAttrib.isSimilar(other.potionAttrib)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String defaultAction() {
        if (this.potionAttrib == null) {
            return null;
        }
        if (this.potionAttrib.defaultAction().equals("DRINK")) {
            return "EAT";
        }
        return this.potionAttrib.defaultAction();
    }

    @Override
    public void execute(Hero hero, String action) {
        if (action.equals("CHOOSE")) {
            GameScene.show(new WndUseItem(null, this));
            return;
        }
        if (action.equals("EAT") && this.potionAttrib == null) {
            GLog.w(Messages.get(this, "raw", new Object[0]), new Object[0]);
            return;
        }
        super.execute(hero, action);
        if (action.equals("EAT") && this.potionAttrib != null) {
            this.potionAttrib.apply(hero);
        }
    }

    @Override
    public String name() {
        if (this.potionAttrib instanceof PotionOfHealing) {
            return Messages.get(this, "sunfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfStrength) {
            return Messages.get(this, "rotfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfParalyticGas) {
            return Messages.get(this, "earthfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfInvisibility) {
            return Messages.get(this, "blindfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfLiquidFlame) {
            return Messages.get(this, "firefruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfFrost) {
            return Messages.get(this, "icefruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfMindVision) {
            return Messages.get(this, "fadefruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfToxicGas) {
            return Messages.get(this, "sorrowfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfLevitation) {
            return Messages.get(this, "stormfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfPurity) {
            return Messages.get(this, "dreamfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfExperience) {
            return Messages.get(this, "starfruit", new Object[0]);
        }
        if (this.potionAttrib instanceof PotionOfHaste) {
            return Messages.get(this, "swiftfruit", new Object[0]);
        }
        return super.name();
    }

    @Override
    public String desc() {
        if (this.potionAttrib == null) {
            return super.desc();
        }
        String desc = Messages.get(this, "desc_cooked", new Object[0]) + "\n\n";
        desc = this.potionAttrib instanceof PotionOfFrost || this.potionAttrib instanceof PotionOfLiquidFlame || this.potionAttrib instanceof PotionOfToxicGas || this.potionAttrib instanceof PotionOfParalyticGas ? desc + Messages.get(this, "desc_throw", new Object[0]) : desc + Messages.get(this, "desc_eat", new Object[0]);
        return desc;
    }

    @Override
    public int value() {
        return 20 * this.quantity;
    }

    public Item cook(Plant.Seed seed) {
        return this.imbuePotion(Reflection.newInstance(Potion.SeedToPotion.types.get(seed.getClass())));
    }

    public Item imbuePotion(Potion potion) {
        this.potionAttrib = potion;
        this.potionAttrib.anonymize();
        this.potionAttrib.image = ItemSpriteSheet.BLANDFRUIT;
        if (this.potionAttrib instanceof PotionOfHealing) {
            this.potionGlow = new ItemSprite.Glowing(0x2EE62E);
        }
        if (this.potionAttrib instanceof PotionOfStrength) {
            this.potionGlow = new ItemSprite.Glowing(0xCC0022);
        }
        if (this.potionAttrib instanceof PotionOfParalyticGas) {
            this.potionGlow = new ItemSprite.Glowing(6772797);
        }
        if (this.potionAttrib instanceof PotionOfInvisibility) {
            this.potionGlow = new ItemSprite.Glowing(0xD9D9D9);
        }
        if (this.potionAttrib instanceof PotionOfLiquidFlame) {
            this.potionGlow = new ItemSprite.Glowing(0xFF7F00);
        }
        if (this.potionAttrib instanceof PotionOfFrost) {
            this.potionGlow = new ItemSprite.Glowing(6730751);
        }
        if (this.potionAttrib instanceof PotionOfMindVision) {
            this.potionGlow = new ItemSprite.Glowing(0x919999);
        }
        if (this.potionAttrib instanceof PotionOfToxicGas) {
            this.potionGlow = new ItemSprite.Glowing(10575077);
        }
        if (this.potionAttrib instanceof PotionOfLevitation) {
            this.potionGlow = new ItemSprite.Glowing(1793913);
        }
        if (this.potionAttrib instanceof PotionOfPurity) {
            this.potionGlow = new ItemSprite.Glowing(12669610);
        }
        if (this.potionAttrib instanceof PotionOfExperience) {
            this.potionGlow = new ItemSprite.Glowing(0x404040);
        }
        if (this.potionAttrib instanceof PotionOfHaste) {
            this.potionGlow = new ItemSprite.Glowing(0xCCBB00);
        }
        return this;
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.map[cell] == 24 || Dungeon.level.pit[cell]) {
            super.onThrow(cell);
        } else if (this.potionAttrib instanceof PotionOfLiquidFlame || this.potionAttrib instanceof PotionOfToxicGas || this.potionAttrib instanceof PotionOfParalyticGas || this.potionAttrib instanceof PotionOfFrost || this.potionAttrib instanceof PotionOfLevitation || this.potionAttrib instanceof PotionOfPurity) {
            Catalog.countUse(this.getClass());
            this.potionAttrib.shatter(cell);
            Dungeon.level.drop((Item)new Chunks(), (int)cell).sprite.drop();
        } else {
            super.onThrow(cell);
        }
    }

    @Override
    public void reset() {
        super.reset();
        if (this.potionAttrib != null) {
            this.imbuePotion(this.potionAttrib);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POTIONATTRIB, this.potionAttrib);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(POTIONATTRIB)) {
            this.imbuePotion((Potion)bundle.get(POTIONATTRIB));
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        return this.potionGlow;
    }

    public static class Chunks
    extends Food {
        public Chunks() {
            this.stackable = true;
            this.image = ItemSpriteSheet.BLAND_CHUNKS;
            this.energy = 450.0f;
            this.bones = true;
        }
    }

    public static class CookFruit
    extends Recipe {
        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            if (ingredients.size() != 2) {
                return false;
            }
            if (ingredients.get(0) instanceof Blandfruit) {
                if (!(ingredients.get(1) instanceof Plant.Seed)) {
                    return false;
                }
            } else {
                if (!(ingredients.get(0) instanceof Plant.Seed)) return false;
                if (!(ingredients.get(1) instanceof Blandfruit)) return false;
                Item temp = ingredients.get(0);
                ingredients.set(0, ingredients.get(1));
                ingredients.set(1, temp);
            }
            Blandfruit fruit = (Blandfruit)ingredients.get(0);
            Plant.Seed seed = (Plant.Seed)ingredients.get(1);
            return fruit.quantity() >= 1 && fruit.potionAttrib == null && seed.quantity() >= 1;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 2;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            ingredients.get(0).quantity(ingredients.get(0).quantity() - 1);
            ingredients.get(1).quantity(ingredients.get(1).quantity() - 1);
            return new Blandfruit().cook((Plant.Seed)ingredients.get(1));
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            if (!this.testIngredients(ingredients)) {
                return null;
            }
            return new Blandfruit().cook((Plant.Seed)ingredients.get(1));
        }
    }
}

