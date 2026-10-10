/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.EnergyCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;

public class WndEnergizeItem
extends WndInfoItem {
    private static final float GAP = 2.0f;
    private static final int BTN_HEIGHT = 18;
    private WndBag owner;
    public static WndBag.ItemSelector selector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return Messages.get(WndEnergizeItem.class, "prompt", new Object[0]);
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item.energyVal() > 0;
        }

        @Override
        public void onSelect(Item item) {
            if (item != null) {
                WndBag parentWnd = WndEnergizeItem.openItemSelector();
                if (ShatteredPixelDungeon.scene() instanceof GameScene) {
                    GameScene.show(new WndEnergizeItem(item, parentWnd));
                } else {
                    ShatteredPixelDungeon.scene().addToFront(new WndEnergizeItem(item, parentWnd));
                }
            }
        }
    };

    public WndEnergizeItem(final Item item, WndBag owner) {
        super(item);
        this.owner = owner;
        float pos = this.height;
        if (item.quantity() == 1) {
            RedButton btnEnergize = new RedButton(Messages.get(this, "energize", item.energyVal())){

                @Override
                protected void onClick() {
                    if (item instanceof Trinket) {
                        Game.scene().addToFront(new WndOptions(new ItemSprite(item), Messages.titleCase(item.name()), Messages.get(WndEnergizeItem.class, "trinket_warn", new Object[0]), new String[]{Messages.get(WndEnergizeItem.class, "trinket_yes", new Object[0]), Messages.get(WndEnergizeItem.class, "trinket_no", new Object[0])}){

                            @Override
                            protected void onSelect(int index) {
                                if (index == 0) {
                                    WndEnergizeItem.energizeAll(item);
                                }
                                WndEnergizeItem.openItemSelector();
                            }

                            @Override
                            public void hide() {
                                super.hide();
                                WndEnergizeItem.this.hide();
                            }
                        });
                    } else {
                        WndEnergizeItem.energizeAll(item);
                        WndEnergizeItem.this.hide();
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
                    WndEnergizeItem.this.hide();
                }
            };
            btnEnergize1.setRect(0.0f, pos + 2.0f, this.width, 18.0f);
            btnEnergize1.icon(new ItemSprite(ItemSpriteSheet.ENERGY));
            this.add(btnEnergize1);
            RedButton btnEnergizeAll = new RedButton(Messages.get(this, "energize_all", energyAll)){

                @Override
                protected void onClick() {
                    WndEnergizeItem.energizeAll(item);
                    WndEnergizeItem.this.hide();
                }
            };
            btnEnergizeAll.setRect(0.0f, btnEnergize1.bottom() + 1.0f, this.width, 18.0f);
            btnEnergizeAll.icon(new ItemSprite(ItemSpriteSheet.ENERGY));
            this.add(btnEnergizeAll);
            pos = btnEnergizeAll.bottom();
        }
        this.resize(this.width, (int)pos);
    }

    @Override
    public void hide() {
        super.hide();
        if (this.owner != null) {
            this.owner.hide();
            WndEnergizeItem.openItemSelector();
        }
    }

    public static void energizeAll(Item item) {
        if (item.isEquipped(Dungeon.hero) && !((EquipableItem)item).doUnequip(Dungeon.hero, false)) {
            return;
        }
        item.detachAll(Dungeon.hero.belongings.backpack);
        WndEnergizeItem.energize(item);
    }

    public static void energizeOne(Item item) {
        if (item.quantity() <= 1) {
            WndEnergizeItem.energizeAll(item);
        } else {
            WndEnergizeItem.energize(item.detach(Dungeon.hero.belongings.backpack));
        }
    }

    private static void energize(Item item) {
        Hero hero = Dungeon.hero;
        if (ShatteredPixelDungeon.scene() instanceof AlchemyScene) {
            Dungeon.energy += item.energyVal();
            ((AlchemyScene)ShatteredPixelDungeon.scene()).createEnergy();
            if (!item.isIdentified()) {
                ((AlchemyScene)ShatteredPixelDungeon.scene()).showIdentify(item);
            }
        } else {
            hero.spend(-hero.cooldown());
            new EnergyCrystal(item.energyVal()).doPickUp(hero);
            item.identify();
            GLog.h("You energized: " + item.name(), new Object[0]);
        }
    }

    public static WndBag openItemSelector() {
        if (ShatteredPixelDungeon.scene() instanceof GameScene) {
            return GameScene.selectItem(selector);
        }
        WndBag window = WndBag.getBag(selector);
        ShatteredPixelDungeon.scene().addToFront(window);
        return window;
    }
}

