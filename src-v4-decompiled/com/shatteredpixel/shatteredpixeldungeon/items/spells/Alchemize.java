/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndEnergizeItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTradeItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class Alchemize
extends Spell {
    private static WndBag parentWnd;
    private static WndBag.ItemSelector itemSelector;

    public Alchemize() {
        this.image = ItemSpriteSheet.ALCHEMIZE;
        this.talentChance = 0.125f;
    }

    @Override
    protected void onCast(Hero hero) {
        parentWnd = GameScene.selectItem(itemSelector);
    }

    @Override
    public int value() {
        return (int)(20.0f * ((float)this.quantity / 8.0f));
    }

    @Override
    public int energyVal() {
        return (int)(4.0f * ((float)this.quantity / 8.0f));
    }

    static {
        itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(Alchemize.class, "prompt", new Object[0]);
            }

            @Override
            public boolean itemSelectable(Item item) {
                return !(item instanceof Alchemize) && (Shopkeeper.canSell(item) || item.energyVal() > 0);
            }

            @Override
            public void onSelect(Item item) {
                if (item != null) {
                    if (parentWnd != null) {
                        parentWnd = GameScene.selectItem(itemSelector);
                    }
                    GameScene.show(new WndAlchemizeItem(item, parentWnd));
                }
            }
        };
    }

    public static class Recipe
    extends com.shatteredpixel.shatteredpixeldungeon.items.Recipe {
        private static final int OUT_QUANTITY = 8;

        @Override
        public boolean testIngredients(ArrayList<Item> ingredients) {
            if (ingredients.size() != 2) {
                return false;
            }
            if (ingredients.get(0) instanceof Plant.Seed && ingredients.get(1) instanceof Runestone) {
                return true;
            }
            return ingredients.get(0) instanceof Runestone && ingredients.get(1) instanceof Plant.Seed;
        }

        @Override
        public int cost(ArrayList<Item> ingredients) {
            return 2;
        }

        @Override
        public Item brew(ArrayList<Item> ingredients) {
            ingredients.get(0).quantity(ingredients.get(0).quantity() - 1);
            ingredients.get(1).quantity(ingredients.get(1).quantity() - 1);
            return this.sampleOutput(null);
        }

        @Override
        public Item sampleOutput(ArrayList<Item> ingredients) {
            return new Alchemize().quantity(8);
        }
    }

    public static class WndAlchemizeItem
    extends WndInfoItem {
        private static final float GAP = 2.0f;
        private static final int BTN_HEIGHT = 18;
        private WndBag owner;

        public WndAlchemizeItem(final Item item, WndBag owner) {
            super(item);
            this.owner = owner;
            float pos = this.height;
            if (Shopkeeper.canSell(item)) {
                if (item.quantity() == 1 || item instanceof MissileWeapon && item.isUpgradable()) {
                    if (item instanceof MissileWeapon && ((MissileWeapon)item).extraThrownLeft) {
                        RenderedTextBlock warn = PixelScene.renderTextBlock(Messages.get(WndUpgrade.class, "thrown_dust", new Object[0]), 6);
                        warn.hardlight(0xFF8800);
                        warn.maxWidth(this.width);
                        warn.setPos(0.0f, pos + 2.0f);
                        this.add(warn);
                        pos = warn.bottom();
                    }
                    RedButton btnSell = new RedButton(Messages.get(this, "sell", item.value())){

                        @Override
                        protected void onClick() {
                            WndTradeItem.sell(item);
                            this.hide();
                            this.consumeAlchemize();
                        }
                    };
                    btnSell.setRect(0.0f, pos + 2.0f, this.width, 18.0f);
                    btnSell.icon(new ItemSprite(ItemSpriteSheet.GOLD));
                    this.add(btnSell);
                    pos = btnSell.bottom();
                } else {
                    int priceAll = item.value();
                    RedButton btnSell1 = new RedButton(Messages.get(this, "sell_1", priceAll / item.quantity())){

                        @Override
                        protected void onClick() {
                            WndTradeItem.sellOne(item);
                            this.hide();
                            this.consumeAlchemize();
                        }
                    };
                    btnSell1.setRect(0.0f, pos + 2.0f, this.width, 18.0f);
                    btnSell1.icon(new ItemSprite(ItemSpriteSheet.GOLD));
                    this.add(btnSell1);
                    RedButton btnSellAll = new RedButton(Messages.get(this, "sell_all", priceAll)){

                        @Override
                        protected void onClick() {
                            WndTradeItem.sell(item);
                            this.hide();
                            this.consumeAlchemize();
                        }
                    };
                    btnSellAll.setRect(0.0f, btnSell1.bottom() + 1.0f, this.width, 18.0f);
                    btnSellAll.icon(new ItemSprite(ItemSpriteSheet.GOLD));
                    this.add(btnSellAll);
                    pos = btnSellAll.bottom();
                }
            }
            if (item.energyVal() > 0) {
                if (item.quantity() == 1) {
                    RedButton btnEnergize = new RedButton(Messages.get(this, "energize", item.energyVal())){

                        @Override
                        protected void onClick() {
                            if (item instanceof Trinket) {
                                GameScene.show(new WndOptions(new ItemSprite(item), Messages.titleCase(item.name()), Messages.get(WndEnergizeItem.class, "trinket_warn", new Object[0]), new String[]{Messages.get(WndEnergizeItem.class, "trinket_yes", new Object[0]), Messages.get(WndEnergizeItem.class, "trinket_no", new Object[0])}){

                                    @Override
                                    protected void onSelect(int index) {
                                        if (index == 0) {
                                            WndEnergizeItem.energizeAll(item);
                                        }
                                    }

                                    @Override
                                    public void hide() {
                                        super.hide();
                                        this.hide();
                                    }
                                });
                            } else {
                                WndEnergizeItem.energizeAll(item);
                                this.hide();
                                this.consumeAlchemize();
                            }
                        }
                    };
                    btnEnergize.setRect(0.0f, pos + 2.0f, this.width, 18.0f);
                    btnEnergize.icon(new ItemSprite(ItemSpriteSheet.ENERGY));
                    this.add(btnEnergize);
                    pos = btnEnergize.bottom();
                } else {
                    int energyAll = item.energyVal();
                    RedButton btnEnergize1 = new RedButton(Messages.get(this, "energize_1", energyAll / item.quantity())){

                        @Override
                        protected void onClick() {
                            WndEnergizeItem.energizeOne(item);
                            this.hide();
                            this.consumeAlchemize();
                        }
                    };
                    btnEnergize1.setRect(0.0f, pos + 2.0f, this.width, 18.0f);
                    btnEnergize1.icon(new ItemSprite(ItemSpriteSheet.ENERGY));
                    this.add(btnEnergize1);
                    RedButton btnEnergizeAll = new RedButton(Messages.get(this, "energize_all", energyAll)){

                        @Override
                        protected void onClick() {
                            WndEnergizeItem.energizeAll(item);
                            this.hide();
                            this.consumeAlchemize();
                        }
                    };
                    btnEnergizeAll.setRect(0.0f, btnEnergize1.bottom() + 1.0f, this.width, 18.0f);
                    btnEnergizeAll.icon(new ItemSprite(ItemSpriteSheet.ENERGY));
                    this.add(btnEnergizeAll);
                    pos = btnEnergizeAll.bottom();
                }
            }
            this.resize(this.width, (int)pos);
        }

        private void consumeAlchemize() {
            Sample.INSTANCE.play("sounds/teleport.mp3");
            if (curItem.quantity() <= 1) {
                curItem.detachAll(Dungeon.hero.belongings.backpack);
                if (this.owner != null) {
                    this.owner.hide();
                }
            } else {
                curItem.detach(Dungeon.hero.belongings.backpack);
                if (this.owner != null) {
                    this.owner.hide();
                }
                GameScene.selectItem(itemSelector);
            }
            Catalog.countUse(this.getClass());
            if (curItem instanceof Alchemize && Random.Float() < ((Alchemize)curItem).talentChance) {
                Talent.onScrollUsed(curUser, curUser.pos, ((Alchemize)curItem).talentFactor, curItem.getClass());
            }
        }
    }
}

