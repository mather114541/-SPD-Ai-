/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class PotionOfMastery
extends ExoticPotion {
    protected static boolean identifiedByUse = false;
    protected WndBag.ItemSelector itemSelector;

    public PotionOfMastery() {
        this.icon = ItemSpriteSheet.Icons.POTION_MASTERY;
        this.unique = true;
        this.talentFactor = 2.0f;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(PotionOfMastery.class, "prompt", new Object[0]);
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof Weapon && !(item instanceof SpiritBow) && !((Weapon)item).masteryPotionBonus || item instanceof Armor && !((Armor)item).masteryPotionBonus;
            }

            @Override
            public void onSelect(Item item) {
                if (item == null && identifiedByUse) {
                    GameScene.show(new WndOptions(new ItemSprite(PotionOfMastery.this), Messages.titleCase(PotionOfMastery.this.name()), Messages.get(ExoticPotion.class, "warning", new Object[0]), new String[]{Messages.get(ExoticPotion.class, "yes", new Object[0]), Messages.get(ExoticPotion.class, "no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            switch (index) {
                                case 0: {
                                    curUser.spendAndNext(1.0f);
                                    identifiedByUse = false;
                                    break;
                                }
                                case 1: {
                                    GameScene.selectItem(PotionOfMastery.this.itemSelector);
                                }
                            }
                        }

                        @Override
                        public void onBackPressed() {
                        }
                    });
                } else if (item != null) {
                    if (item instanceof Weapon) {
                        ((Weapon)item).masteryPotionBonus = true;
                        GLog.p(Messages.get(PotionOfMastery.class, "weapon_easier", new Object[0]), new Object[0]);
                    } else if (item instanceof Armor) {
                        ((Armor)item).masteryPotionBonus = true;
                        GLog.p(Messages.get(PotionOfMastery.class, "armor_easier", new Object[0]), new Object[0]);
                    }
                    Item.updateQuickslot();
                    Sample.INSTANCE.play("sounds/drink.mp3");
                    curUser.sprite.operate(curUser.pos);
                    if (!identifiedByUse) {
                        curItem.detach(curUser.belongings.backpack);
                    }
                    identifiedByUse = false;
                    if (!PotionOfMastery.this.anonymous) {
                        Catalog.countUse(PotionOfMastery.class);
                        if (Random.Float() < PotionOfMastery.this.talentChance) {
                            Talent.onPotionUsed(curUser, curUser.pos, PotionOfMastery.this.talentFactor);
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void drink(Hero hero) {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(hero.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.selectItem(this.itemSelector);
    }
}

