/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class LiquidMetal
extends Item {
    private static final String AC_APPLY = "APPLY";
    private final WndBag.ItemSelector itemSelector;

    public LiquidMetal() {
        this.image = ItemSpriteSheet.LIQUID_METAL;
        this.stackable = true;
        this.defaultAction = AC_APPLY;
        this.bones = true;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(LiquidMetal.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return MagicalHolster.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof MissileWeapon && !(item instanceof Dart);
            }

            /*
             * Unable to fully structure code
             */
            @Override
            public void onSelect(Item item) {
                block6: {
                    block7: {
                        if (item == null || !(item instanceof MissileWeapon)) break block6;
                        m = (MissileWeapon)item;
                        maxToUse = 5 * (m.tier + 1);
                        maxToUse = (float)((double)maxToUse * Math.pow(1.350000023841858, m.level()));
                        durabilityPerMetal = 100.0f / maxToUse;
                        percentDurabilityLost = 0.999f - m.durabilityLeft() / 100.0f;
                        toUse = (int)Math.ceil(maxToUse * percentDurabilityLost);
                        if (toUse == 0) break block7;
                        if (!(Math.ceil(m.durabilityLeft() / m.durabilityPerUse()) >= Math.ceil(100.0f / m.durabilityPerUse()))) ** GOTO lbl32
                    }
                    if (m.quantity() >= m.defaultQuantity()) ** GOTO lbl30
                    if ((float)LiquidMetal.this.quantity() * durabilityPerMetal >= m.durabilityPerUse()) {
                        m.quantity(m.quantity() + 1);
                        if (Math.ceil(maxToUse) < (double)LiquidMetal.this.quantity()) {
                            Catalog.countUses(LiquidMetal.class, (int)Math.ceil(maxToUse));
                            GLog.i(Messages.get(LiquidMetal.class, "apply", new Object[]{(int)Math.ceil(maxToUse)}), new Object[0]);
                            LiquidMetal.this.quantity -= (int)Math.ceil(maxToUse);
                        } else {
                            Catalog.countUses(LiquidMetal.class, LiquidMetal.this.quantity());
                            m.damage(100.0f);
                            m.repair((float)LiquidMetal.this.quantity() * durabilityPerMetal - 1.0f);
                            GLog.i(Messages.get(LiquidMetal.class, "apply", new Object[]{LiquidMetal.this.quantity()}), new Object[0]);
                            LiquidMetal.this.detachAll(Dungeon.hero.belongings.backpack);
                        }
                    } else {
                        GLog.w(Messages.get(LiquidMetal.class, "already_fixed", new Object[0]), new Object[0]);
                        return;
lbl30:
                        // 1 sources

                        GLog.w(Messages.get(LiquidMetal.class, "already_fixed", new Object[0]), new Object[0]);
                        return;
lbl32:
                        // 1 sources

                        if (toUse < LiquidMetal.this.quantity()) {
                            Catalog.countUses(LiquidMetal.class, toUse);
                            m.repair(maxToUse * durabilityPerMetal);
                            LiquidMetal.this.quantity(LiquidMetal.this.quantity() - toUse);
                            GLog.i(Messages.get(LiquidMetal.class, "apply", new Object[]{toUse}), new Object[0]);
                        } else {
                            Catalog.countUses(LiquidMetal.class, LiquidMetal.this.quantity());
                            m.repair((float)LiquidMetal.this.quantity() * durabilityPerMetal);
                            GLog.i(Messages.get(LiquidMetal.class, "apply", new Object[]{LiquidMetal.this.quantity()}), new Object[0]);
                            LiquidMetal.this.detachAll(Dungeon.hero.belongings.backpack);
                        }
                    }
                    Item.curUser.sprite.operate(Item.curUser.pos);
                    Sample.INSTANCE.play("sounds/drink.mp3");
                    Item.updateQuickslot();
                    Item.curUser.sprite.emitter().start(Speck.factory(2), 0.1f, 10);
                }
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_APPLY);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_APPLY)) {
            curUser = hero;
            GameScene.selectItem(this.itemSelector);
        }
    }

    @Override
    protected void onThrow(int cell) {
        if (Dungeon.level.map[cell] == 24 || Dungeon.level.pit[cell]) {
            super.onThrow(cell);
        } else {
            Dungeon.level.pressCell(cell);
            if (Dungeon.level.heroFOV[cell]) {
                GLog.i(Messages.get(Potion.class, "shatter", new Object[0]), new Object[0]);
                Sample.INSTANCE.play("sounds/shatter.mp3");
                Splash.at(cell, 0xBFBFBF, 5);
            }
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return this.quantity;
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1 && ingredients.get(0) instanceof MissileWeapon && ingredients.get((int)0).cursedKnown && !ingredients.get((int)0).cursed;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 3;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = this.sampleOutput(ingredients);
            MissileWeapon m = (MissileWeapon)ingredients.get(0);
            if (!m.levelKnown) {
                result.quantity(this.metalQuantity(m));
            }
            m.quantity(0);
            Buff.affect((Char)Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(m.setID, Integer.MAX_VALUE);
            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            MissileWeapon m = (MissileWeapon)ingredients.get(0);
            if (m.levelKnown) {
                return new LiquidMetal().quantity(this.metalQuantity(m));
            }
            return new LiquidMetal();
        }

        private int metalQuantity(MissileWeapon m) {
            float quantityPerWeapon = 5 * (m.tier + 1);
            if (m.defaultQuantity() != 3) {
                quantityPerWeapon = 3.0f / (float)m.defaultQuantity();
            }
            quantityPerWeapon = (float)((double)quantityPerWeapon * Math.pow(1.35f, Math.min(5, m.level())));
            float quantity = m.quantity() - 1;
            return Math.round((quantity += 0.25f + 0.0075f * m.durabilityLeft()) * quantityPerWeapon);
        }
    }
}

