/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class ArcaneResin
extends Item {
    private static final String AC_APPLY = "APPLY";
    private final WndBag.ItemSelector itemSelector;

    public ArcaneResin() {
        this.image = ItemSpriteSheet.ARCANE_RESIN;
        this.stackable = true;
        this.defaultAction = AC_APPLY;
        this.bones = true;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(ArcaneResin.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return MagicalHolster.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof Wand && item.isIdentified();
            }

            @Override
            public void onSelect(Item item) {
                if (item != null && item instanceof Wand) {
                    Wand w = (Wand)item;
                    if (w.level() >= 3) {
                        GLog.w(Messages.get(ArcaneResin.class, "level_too_high", new Object[0]), new Object[0]);
                        return;
                    }
                    int resinToUse = w.level() + 1;
                    if (ArcaneResin.this.quantity() < resinToUse) {
                        GLog.w(Messages.get(ArcaneResin.class, "not_enough", new Object[0]), new Object[0]);
                    } else {
                        Catalog.countUses(ArcaneResin.class, resinToUse);
                        if (resinToUse < ArcaneResin.this.quantity()) {
                            ArcaneResin.this.quantity(ArcaneResin.this.quantity() - resinToUse);
                        } else {
                            ArcaneResin.this.detachAll(Dungeon.hero.belongings.backpack);
                        }
                        ++w.resinBonus;
                        ++w.curCharges;
                        w.updateLevel();
                        Item.updateQuickslot();
                        Item.curUser.sprite.operate(Item.curUser.pos);
                        Sample.INSTANCE.play("sounds/teleport.mp3");
                        Item.curUser.sprite.emitter().start(Speck.factory(4), 0.2f, 3);
                        Item.curUser.spendAndNext(1.0f);
                        GLog.p(Messages.get(ArcaneResin.class, "apply", new Object[0]), new Object[0]);
                    }
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
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 30 * this.quantity();
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            return ingredients.size() == 1 && ingredients.get(0) instanceof Wand && ingredients.get((int)0).cursedKnown && !ingredients.get((int)0).cursed;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 5;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            Item result = this.sampleOutput(ingredients);
            Wand w = (Wand)ingredients.get(0);
            if (!w.levelKnown) {
                result.quantity(this.resinQuantity(w));
            }
            w.quantity(0);
            return result;
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            Wand w = (Wand)ingredients.get(0);
            if (w.levelKnown) {
                return new ArcaneResin().quantity(this.resinQuantity(w));
            }
            return new ArcaneResin();
        }

        private int resinQuantity(Wand w) {
            int level = w.level() - w.resinBonus;
            int quantity = 2 * (level + 1);
            if (Dungeon.hero.heroClass != HeroClass.MAGE && Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION)) {
                quantity += Dungeon.hero.pointsInTalent(Talent.WAND_PRESERVATION);
            }
            return quantity;
        }
    }
}

