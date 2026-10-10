/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSadGhost;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public class WndBlacksmith
extends Window {
    private static final int WIDTH_P = 120;
    private static final int WIDTH_L = 180;
    private static final int GAP = 2;

    public WndBlacksmith(final Blacksmith troll, final Hero hero) {
        int width = PixelScene.landscape() ? 180 : 120;
        IconTitle titlebar = new IconTitle();
        titlebar.icon(troll.sprite());
        titlebar.label(Messages.titleCase(troll.name()));
        titlebar.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(titlebar);
        RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "prompt", Blacksmith.Quest.favor), 6);
        message.maxWidth(width);
        message.setPos(0.0f, titlebar.bottom() + 2.0f);
        this.add(message);
        ArrayList<RedButton> buttons = new ArrayList<RedButton>();
        final int pickaxeCost = Blacksmith.Quest.freePickaxe ? 0 : 250;
        RedButton pickaxe = new RedButton(Messages.get(this, "pickaxe", pickaxeCost), 6){

            @Override
            protected void onClick() {
                GameScene.show(new WndOptions(troll.sprite(), Messages.titleCase(troll.name()), Messages.get(WndBlacksmith.class, "pickaxe_verify", new Object[0]) + (String)(pickaxeCost == 0 ? "\n\n" + Messages.get(WndBlacksmith.class, "pickaxe_free", new Object[0]) : ""), new String[]{Messages.get(WndBlacksmith.class, "pickaxe_yes", new Object[0]), Messages.get(WndBlacksmith.class, "pickaxe_no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            if (Blacksmith.Quest.pickaxe.doPickUp(Dungeon.hero)) {
                                GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", Blacksmith.Quest.pickaxe.name())), new Object[0]);
                            } else {
                                Dungeon.level.drop((Item)Blacksmith.Quest.pickaxe, (int)Dungeon.hero.pos).sprite.drop();
                            }
                            Blacksmith.Quest.favor -= pickaxeCost;
                            Blacksmith.Quest.pickaxe = null;
                            WndBlacksmith.this.hide();
                            if (!Blacksmith.Quest.rewardsAvailable()) {
                                Notes.remove(Notes.Landmark.TROLL);
                            }
                        }
                    }
                });
            }
        };
        pickaxe.enable(Blacksmith.Quest.pickaxe != null && Blacksmith.Quest.favor >= pickaxeCost);
        buttons.add(pickaxe);
        int reforgecost = 500 + 1000 * Blacksmith.Quest.reforges;
        RedButton reforge = new RedButton(Messages.get(this, "reforge", reforgecost), 6){

            @Override
            protected void onClick() {
                GameScene.show(new WndReforge(troll, WndBlacksmith.this));
            }
        };
        reforge.enable(Blacksmith.Quest.favor >= reforgecost);
        buttons.add(reforge);
        int hardenCost = 500 + 1000 * Blacksmith.Quest.hardens;
        RedButton harden = new RedButton(Messages.get(this, "harden", hardenCost), 6){

            @Override
            protected void onClick() {
                GameScene.selectItem(new HardenSelector());
            }
        };
        harden.enable(Blacksmith.Quest.favor >= hardenCost);
        buttons.add(harden);
        int upgradeCost = 1000 + 1000 * Blacksmith.Quest.upgrades;
        RedButton upgrade = new RedButton(Messages.get(this, "upgrade", upgradeCost), 6){

            @Override
            protected void onClick() {
                GameScene.selectItem(new UpgradeSelector());
            }
        };
        upgrade.enable(Blacksmith.Quest.favor >= upgradeCost);
        buttons.add(upgrade);
        RedButton smith = new RedButton(Messages.get(this, "smith", 2000), 6){

            @Override
            protected void onClick() {
                GameScene.show(new WndOptions(troll.sprite(), Messages.titleCase(troll.name()), Messages.get(WndBlacksmith.class, "smith_verify", new Object[0]), new String[]{Messages.get(WndBlacksmith.class, "smith_yes", new Object[0]), Messages.get(WndBlacksmith.class, "smith_no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            Blacksmith.Quest.favor -= 2000;
                            ++Blacksmith.Quest.smiths;
                            WndBlacksmith.this.hide();
                            GameScene.show(new WndSmith(troll, hero));
                        }
                    }
                });
            }
        };
        smith.enable(Blacksmith.Quest.favor >= 2000);
        buttons.add(smith);
        RedButton cashOut = new RedButton(Messages.get(this, "cashout", new Object[0]), 6){

            @Override
            protected void onClick() {
                GameScene.show(new WndOptions(troll.sprite(), Messages.titleCase(troll.name()), Messages.get(WndBlacksmith.class, "cashout_verify", Blacksmith.Quest.favor), new String[]{Messages.get(WndBlacksmith.class, "cashout_yes", new Object[0]), Messages.get(WndBlacksmith.class, "cashout_no", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            new Gold(Blacksmith.Quest.favor).doPickUp(Dungeon.hero, Dungeon.hero.pos);
                            Blacksmith.Quest.favor = 0;
                            WndBlacksmith.this.hide();
                        }
                    }
                });
            }
        };
        cashOut.enable(Blacksmith.Quest.favor > 0);
        buttons.add(cashOut);
        float pos = message.bottom() + 6.0f;
        for (RedButton b : buttons) {
            b.leftJustify = true;
            b.multiline = true;
            b.setSize(width, b.reqHeight());
            b.setRect(0.0f, pos, width, b.reqHeight());
            b.enable(b.active);
            this.add(b);
            pos = b.bottom() + 2.0f;
        }
        this.resize(width, (int)pos);
    }

    public static class WndSmith
    extends Window {
        private static final int WIDTH = 128;
        private static final int BTN_SIZE = 28;
        private static final int BTN_GAP = 4;
        private static final int GAP = 2;

        public WndSmith(final Blacksmith troll, final Hero hero) {
            IconTitle titlebar = new IconTitle();
            titlebar.icon(troll.sprite());
            titlebar.label(Messages.titleCase(troll.name()));
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "prompt", new Object[0]), 6);
            titlebar.setRect(0.0f, 0.0f, 128.0f, 0.0f);
            this.add(titlebar);
            message.maxWidth(128);
            message.setPos(0.0f, titlebar.bottom() + 2.0f);
            this.add(message);
            if (Blacksmith.Quest.smithRewards == null || Blacksmith.Quest.smithRewards.isEmpty()) {
                Blacksmith.Quest.generateRewards(false);
            }
            int count = 0;
            for (Item i : Blacksmith.Quest.smithRewards) {
                ItemButton btnReward = new ItemButton(){

                    @Override
                    protected void onClick() {
                        GameScene.show(new RewardWindow(troll, hero, this.item()));
                    }
                };
                btnReward.item(i);
                btnReward.setRect(++count * 124 / Blacksmith.Quest.smithRewards.size() - 28, message.top() + message.height() + 4.0f, 28.0f, 28.0f);
                this.add(btnReward);
            }
            this.resize(128, (int)message.bottom() + 8 + 28);
        }

        @Override
        public void onBackPressed() {
        }

        private class RewardWindow
        extends WndInfoItem {
            public RewardWindow(Blacksmith troll, Hero hero, final Item item) {
                super(item);
                RedButton btnConfirm = new RedButton(Messages.get(WndSadGhost.class, "confirm", new Object[0])){

                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();
                        if (item instanceof Weapon && Blacksmith.Quest.smithEnchant != null) {
                            ((Weapon)item).enchant(Blacksmith.Quest.smithEnchant);
                        } else if (item instanceof Armor && Blacksmith.Quest.smithGlyph != null) {
                            ((Armor)item).inscribe(Blacksmith.Quest.smithGlyph);
                        }
                        item.identify(false);
                        Sample.INSTANCE.play("sounds/evoke.mp3");
                        Item.evoke(Dungeon.hero);
                        if (item.doPickUp(Dungeon.hero)) {
                            GLog.i(Messages.capitalize(Messages.get(Dungeon.hero, "you_now_have", item.name())), new Object[0]);
                        } else {
                            Dungeon.level.drop((Item)item, (int)Dungeon.hero.pos).sprite.drop();
                        }
                        WndSmith.this.hide();
                        Blacksmith.Quest.smithRewards = null;
                        if (!Blacksmith.Quest.rewardsAvailable()) {
                            Notes.remove(Notes.Landmark.TROLL);
                        }
                    }
                };
                btnConfirm.setRect(0.0f, this.height + 2, this.width / 2 - 1, 16.0f);
                this.add(btnConfirm);
                RedButton btnCancel = new RedButton(Messages.get(WndSadGhost.class, "cancel", new Object[0])){

                    @Override
                    protected void onClick() {
                        RewardWindow.this.hide();
                    }
                };
                btnCancel.setRect(btnConfirm.right() + 2.0f, this.height + 2, btnConfirm.width(), 16.0f);
                this.add(btnCancel);
                this.resize(this.width, (int)btnCancel.bottom());
            }
        }
    }

    private class UpgradeSelector
    extends WndBag.ItemSelector {
        private UpgradeSelector() {
        }

        @Override
        public String textPrompt() {
            return Messages.get(this, "prompt", new Object[0]);
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item.isUpgradable() && item.isIdentified() && !item.cursed && item.level() < 2;
        }

        @Override
        public void onSelect(Item item) {
            if (item != null) {
                item.upgrade();
                int upgradeCost = 1000 + 1000 * Blacksmith.Quest.upgrades;
                Blacksmith.Quest.favor -= upgradeCost;
                ++Blacksmith.Quest.upgrades;
                WndBlacksmith.this.hide();
                Sample.INSTANCE.play("sounds/evoke.mp3");
                ScrollOfUpgrade.upgrade(Dungeon.hero);
                Item.evoke(Dungeon.hero);
                Badges.validateItemLevelAquired(item);
                if (!Blacksmith.Quest.rewardsAvailable()) {
                    Notes.remove(Notes.Landmark.TROLL);
                }
                Catalog.countUse(item.getClass());
            }
        }
    }

    private class HardenSelector
    extends WndBag.ItemSelector {
        private HardenSelector() {
        }

        @Override
        public String textPrompt() {
            return Messages.get(this, "prompt", new Object[0]);
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return Belongings.Backpack.class;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return item.isUpgradable() && item.isIdentified() && !item.cursed && (item instanceof Weapon && !((Weapon)item).enchantHardened || item instanceof Armor && !((Armor)item).glyphHardened);
        }

        @Override
        public void onSelect(Item item) {
            if (item != null) {
                if (item instanceof Weapon) {
                    ((Weapon)item).enchantHardened = true;
                } else if (item instanceof Armor) {
                    ((Armor)item).glyphHardened = true;
                }
                Blacksmith.Quest.favor -= 500 + 1000 * Blacksmith.Quest.hardens;
                ++Blacksmith.Quest.hardens;
                WndBlacksmith.this.hide();
                Sample.INSTANCE.play("sounds/evoke.mp3");
                Item.evoke(Dungeon.hero);
                if (!Blacksmith.Quest.rewardsAvailable()) {
                    Notes.remove(Notes.Landmark.TROLL);
                }
            }
        }
    }

    protected static class WndReforge
    extends Window {
        private static final int WIDTH = 120;
        private static final int BTN_SIZE = 32;
        private static final float GAP = 2.0f;
        private static final float BTN_GAP = 5.0f;
        private ItemButton btnPressed;
        private ItemButton btnItem1;
        private ItemButton btnItem2;
        private RedButton btnReforge;
        protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(WndReforge.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return Belongings.Backpack.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item.isIdentified() && !item.cursed && item.isUpgradable();
            }

            @Override
            public void onSelect(Item item) {
                if (item != null && btnPressed.parent != null) {
                    btnPressed.item(item);
                    Item item1 = btnItem1.item();
                    Item item2 = btnItem2.item();
                    if (item1 == null || item2 == null) {
                        btnReforge.enable(false);
                    } else if (item1.getClass() != item2.getClass()) {
                        btnReforge.enable(false);
                    } else if (item1 == item2) {
                        btnReforge.enable(false);
                    } else {
                        btnReforge.enable(true);
                    }
                }
            }
        };

        public WndReforge(Blacksmith troll, final Window wndParent) {
            IconTitle titlebar = new IconTitle();
            titlebar.icon(troll.sprite());
            titlebar.label(Messages.titleCase(troll.name()));
            titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(titlebar);
            RenderedTextBlock message = PixelScene.renderTextBlock(Messages.get(this, "message", new Object[0]), 6);
            message.maxWidth(120);
            message.setPos(0.0f, titlebar.bottom() + 2.0f);
            this.add(message);
            this.btnItem1 = new ItemButton(){

                @Override
                protected void onClick() {
                    btnPressed = btnItem1;
                    GameScene.selectItem(itemSelector);
                }
            };
            this.btnItem1.setRect(25.5f, message.top() + message.height() + 5.0f, 32.0f, 32.0f);
            this.add(this.btnItem1);
            this.btnItem2 = new ItemButton(){

                @Override
                protected void onClick() {
                    btnPressed = btnItem2;
                    GameScene.selectItem(itemSelector);
                }
            };
            this.btnItem2.setRect(this.btnItem1.right() + 5.0f, this.btnItem1.top(), 32.0f, 32.0f);
            this.add(this.btnItem2);
            this.btnReforge = new RedButton(Messages.get(this, "reforge", new Object[0])){

                @Override
                protected void onClick() {
                    Item second;
                    Item first;
                    if (btnItem1.item().trueLevel() >= btnItem2.item().trueLevel()) {
                        first = btnItem1.item();
                        second = btnItem2.item();
                    } else {
                        first = btnItem2.item();
                        second = btnItem1.item();
                    }
                    Sample.INSTANCE.play("sounds/evoke.mp3");
                    ScrollOfUpgrade.upgrade(Dungeon.hero);
                    Item.evoke(Dungeon.hero);
                    if (second.isEquipped(Dungeon.hero)) {
                        ((EquipableItem)second).doUnequip(Dungeon.hero, false);
                    }
                    second.detachAll(Dungeon.hero.belongings.backpack);
                    if (second instanceof Armor) {
                        BrokenSeal seal = ((Armor)second).checkSeal();
                        if (seal != null) {
                            Dungeon.level.drop(seal, Dungeon.hero.pos);
                        }
                    } else if (second instanceof MissileWeapon) {
                        Buff.affect((Char)Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(((MissileWeapon)second).setID, Integer.MAX_VALUE);
                    }
                    if (first instanceof Weapon && ((Weapon)first).hasGoodEnchant()) {
                        ((Weapon)first).upgrade(true);
                    } else if (first instanceof Armor && ((Armor)first).hasGoodGlyph()) {
                        ((Armor)first).upgrade(true);
                    } else {
                        first.upgrade();
                    }
                    Badges.validateItemLevelAquired(first);
                    Item.updateQuickslot();
                    Blacksmith.Quest.favor -= 500 + 1000 * Blacksmith.Quest.reforges;
                    ++Blacksmith.Quest.reforges;
                    if (!Blacksmith.Quest.rewardsAvailable()) {
                        Notes.remove(Notes.Landmark.TROLL);
                    }
                    this.hide();
                    if (wndParent != null) {
                        wndParent.hide();
                    }
                }
            };
            this.btnReforge.enable(false);
            this.btnReforge.setRect(0.0f, this.btnItem1.bottom() + 5.0f, 120.0f, 20.0f);
            this.add(this.btnReforge);
            this.resize(120, (int)this.btnReforge.bottom());
        }
    }
}

