/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.audio.Sample;

public class ScrollOfEnchantment
extends ExoticScroll {
    protected static boolean identifiedByUse = false;
    protected WndBag.ItemSelector itemSelector;

    public ScrollOfEnchantment() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_ENCHANT;
        this.unique = true;
        this.talentFactor = 2.0f;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(ScrollOfEnchantment.class, "inv_title", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return Belongings.Backpack.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return ScrollOfEnchantment.enchantable(item);
            }

            @Override
            public void onSelect(Item item) {
                if (item instanceof Weapon) {
                    if (!identifiedByUse) {
                        curItem.detach(curUser.belongings.backpack);
                    }
                    identifiedByUse = false;
                    Weapon.Enchantment[] enchants = new Weapon.Enchantment[3];
                    Class<?> existing = ((Weapon)item).enchantment != null ? ((Weapon)item).enchantment.getClass() : null;
                    enchants[0] = Weapon.Enchantment.randomCommon(existing);
                    enchants[1] = Weapon.Enchantment.randomUncommon(existing);
                    enchants[2] = Weapon.Enchantment.random(existing, enchants[0].getClass(), enchants[1].getClass());
                    GameScene.show(new WndEnchantSelect((Weapon)item, enchants[0], enchants[1], enchants[2]));
                } else if (item instanceof Armor) {
                    if (!identifiedByUse) {
                        curItem.detach(curUser.belongings.backpack);
                    }
                    identifiedByUse = false;
                    Armor.Glyph[] glyphs = new Armor.Glyph[3];
                    Class<?> existing = ((Armor)item).glyph != null ? ((Armor)item).glyph.getClass() : null;
                    glyphs[0] = Armor.Glyph.randomCommon(existing);
                    glyphs[1] = Armor.Glyph.randomUncommon(existing);
                    glyphs[2] = Armor.Glyph.random(existing, glyphs[0].getClass(), glyphs[1].getClass());
                    GameScene.show(new WndGlyphSelect((Armor)item, glyphs[0], glyphs[1], glyphs[2]));
                } else if (identifiedByUse) {
                    ((ScrollOfEnchantment)curItem).confirmCancelation();
                }
            }
        };
    }

    @Override
    public void doRead() {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(ScrollOfEnchantment.curUser.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.selectItem(this.itemSelector);
    }

    public static boolean enchantable(Item item) {
        return !(!(item instanceof Weapon) && !(item instanceof Armor) || !item.isUpgradable() && !(item instanceof SpiritBow));
    }

    private void confirmCancelation() {
        GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(InventoryScroll.class, "warning", new Object[0]), new String[]{Messages.get(InventoryScroll.class, "yes", new Object[0]), Messages.get(InventoryScroll.class, "no", new Object[0])}){

            @Override
            protected void onSelect(int index) {
                switch (index) {
                    case 0: {
                        curUser.spendAndNext(1.0f);
                        identifiedByUse = false;
                        break;
                    }
                    case 1: {
                        GameScene.selectItem(ScrollOfEnchantment.this.itemSelector);
                    }
                }
            }

            @Override
            public void onBackPressed() {
            }
        });
    }

    public static class WndConfirmCancel
    extends WndOptions {
        public WndConfirmCancel() {
            super(new ItemSprite(new ScrollOfEnchantment()), Messages.titleCase(new ScrollOfEnchantment().name()), Messages.get(ScrollOfEnchantment.class, "cancel_warn", new Object[0]), Messages.get(ScrollOfEnchantment.class, "cancel_warn_yes", new Object[0]), Messages.get(ScrollOfEnchantment.class, "cancel_warn_no", new Object[0]));
        }

        @Override
        protected void onSelect(int index) {
            super.onSelect(index);
            if (index == 1) {
                if (WndEnchantSelect.wep != null) {
                    GameScene.show(new WndEnchantSelect());
                } else {
                    GameScene.show(new WndGlyphSelect());
                }
            } else {
                WndEnchantSelect.wep = null;
                WndEnchantSelect.enchantments = null;
                WndGlyphSelect.arm = null;
                WndGlyphSelect.glyphs = null;
            }
        }

        @Override
        public void onBackPressed() {
        }
    }

    public static class WndGlyphSelect
    extends WndOptions {
        private static Armor arm;
        private static Armor.Glyph[] glyphs;

        public WndGlyphSelect() {
            this(arm, glyphs[0], glyphs[1], glyphs[2]);
        }

        public WndGlyphSelect(Armor arm, Armor.Glyph glyph1, Armor.Glyph glyph2, Armor.Glyph glyph3) {
            super(new ItemSprite(new ScrollOfEnchantment()), Messages.titleCase(new ScrollOfEnchantment().name()), Messages.get(ScrollOfEnchantment.class, "armor", new Object[0]), glyph1.name(), glyph2.name(), glyph3.name(), Messages.get(ScrollOfEnchantment.class, "cancel", new Object[0]));
            WndGlyphSelect.arm = arm;
            glyphs = new Armor.Glyph[3];
            WndGlyphSelect.glyphs[0] = glyph1;
            WndGlyphSelect.glyphs[1] = glyph2;
            WndGlyphSelect.glyphs[2] = glyph3;
            WndEnchantSelect.wep = null;
        }

        @Override
        protected void onSelect(int index) {
            if (index < 3) {
                arm.inscribe(glyphs[index]);
                GLog.p(Messages.get(StoneOfEnchantment.class, "armor", new Object[0]), new Object[0]);
                ((ScrollOfEnchantment)curItem).readAnimation();
                Sample.INSTANCE.play("sounds/read.mp3");
                Enchanting.show(curUser, arm);
            } else {
                GameScene.show(new WndConfirmCancel());
            }
        }

        @Override
        protected boolean hasInfo(int index) {
            return index < 3;
        }

        @Override
        protected void onInfo(int index) {
            GameScene.show(new WndTitledMessage(Icons.get(Icons.INFO), Messages.titleCase(glyphs[index].name()), glyphs[index].desc()));
        }

        @Override
        public void onBackPressed() {
        }
    }

    public static class WndEnchantSelect
    extends WndOptions {
        private static Weapon wep;
        private static Weapon.Enchantment[] enchantments;

        public WndEnchantSelect() {
            this(wep, enchantments[0], enchantments[1], enchantments[2]);
        }

        public WndEnchantSelect(Weapon wep, Weapon.Enchantment ench1, Weapon.Enchantment ench2, Weapon.Enchantment ench3) {
            super(new ItemSprite(new ScrollOfEnchantment()), Messages.titleCase(new ScrollOfEnchantment().name()), Messages.get(ScrollOfEnchantment.class, "weapon", new Object[0]), ench1.name(), ench2.name(), ench3.name(), Messages.get(ScrollOfEnchantment.class, "cancel", new Object[0]));
            WndEnchantSelect.wep = wep;
            enchantments = new Weapon.Enchantment[3];
            WndEnchantSelect.enchantments[0] = ench1;
            WndEnchantSelect.enchantments[1] = ench2;
            WndEnchantSelect.enchantments[2] = ench3;
            WndGlyphSelect.arm = null;
        }

        @Override
        protected void onSelect(int index) {
            if (index < 3) {
                wep.enchant(enchantments[index]);
                GLog.p(Messages.get(StoneOfEnchantment.class, "weapon", new Object[0]), new Object[0]);
                ((ScrollOfEnchantment)curItem).readAnimation();
                Sample.INSTANCE.play("sounds/read.mp3");
                Enchanting.show(curUser, wep);
            } else {
                GameScene.show(new WndConfirmCancel());
            }
        }

        @Override
        protected boolean hasInfo(int index) {
            return index < 3;
        }

        @Override
        protected void onInfo(int index) {
            GameScene.show(new WndTitledMessage(Icons.get(Icons.INFO), Messages.titleCase(enchantments[index].name()), enchantments[index].desc()));
        }

        @Override
        public void onBackPressed() {
        }
    }
}

