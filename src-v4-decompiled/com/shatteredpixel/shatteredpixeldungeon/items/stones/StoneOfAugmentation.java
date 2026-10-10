/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class StoneOfAugmentation
extends InventoryStone {
    public StoneOfAugmentation() {
        this.preferredBag = Belongings.Backpack.class;
        this.image = ItemSpriteSheet.STONE_AUGMENTATION;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return ScrollOfEnchantment.enchantable(item);
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndAugment(item));
    }

    public void apply(Weapon weapon, Weapon.Augment augment) {
        weapon.augment = augment;
        this.useAnimation();
        ScrollOfUpgrade.upgrade(curUser);
        if (!this.anonymous) {
            curItem.detach(StoneOfAugmentation.curUser.belongings.backpack);
            Catalog.countUse(this.getClass());
            Talent.onRunestoneUsed(curUser, StoneOfAugmentation.curUser.pos, this.getClass());
        }
    }

    public void apply(Armor armor, Armor.Augment augment) {
        armor.augment = augment;
        this.useAnimation();
        ScrollOfUpgrade.upgrade(curUser);
        if (!this.anonymous) {
            curItem.detach(StoneOfAugmentation.curUser.belongings.backpack);
            Catalog.countUse(this.getClass());
            Talent.onRunestoneUsed(curUser, StoneOfAugmentation.curUser.pos, this.getClass());
        }
    }

    @Override
    public int value() {
        return 30 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 5 * this.quantity;
    }

    public class WndAugment
    extends Window {
        private static final int WIDTH = 120;
        private static final int MARGIN = 2;
        private static final int BUTTON_WIDTH = 116;
        private static final int BUTTON_HEIGHT = 18;

        public WndAugment(final Item toAugment) {
            IconTitle titlebar = new IconTitle(toAugment);
            titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
            this.add(titlebar);
            Object msg = Messages.get(this, "choice", new Object[0]);
            if (toAugment instanceof Weapon && ((Weapon)toAugment).augment != Weapon.Augment.NONE) {
                msg = (String)msg + "\n\n" + Messages.get(this, "already", new Object[0]);
            } else if (toAugment instanceof Armor && ((Armor)toAugment).augment != Armor.Augment.NONE) {
                msg = (String)msg + "\n\n" + Messages.get(this, "already", new Object[0]);
            }
            RenderedTextBlock tfMesage = PixelScene.renderTextBlock((String)msg, 6);
            tfMesage.maxWidth(116);
            tfMesage.setPos(2.0f, titlebar.bottom() + 2.0f);
            this.add(tfMesage);
            float pos = tfMesage.bottom() + 2.0f;
            if (toAugment instanceof Weapon) {
                for (Enum enum_ : Weapon.Augment.values()) {
                    if (((Weapon)toAugment).augment == enum_) continue;
                    RedButton btnAug = new RedButton(Messages.get(this, enum_.name(), new Object[0]), (Weapon.Augment)enum_){
                        final /* synthetic */ Weapon.Augment val$aug;
                        {
                            this.val$aug = augment;
                            super(label);
                        }

                        @Override
                        protected void onClick() {
                            WndAugment.this.hide();
                            StoneOfAugmentation.this.apply((Weapon)toAugment, this.val$aug);
                        }
                    };
                    btnAug.setRect(2.0f, pos + 2.0f, 116.0f, 18.0f);
                    this.add(btnAug);
                    pos = btnAug.bottom();
                }
            } else if (toAugment instanceof Armor) {
                for (Enum enum_ : Armor.Augment.values()) {
                    if (((Armor)toAugment).augment == enum_) continue;
                    RedButton btnAug = new RedButton(Messages.get(this, enum_.name(), new Object[0]), (Armor.Augment)enum_){
                        final /* synthetic */ Armor.Augment val$aug;
                        {
                            this.val$aug = augment;
                            super(label);
                        }

                        @Override
                        protected void onClick() {
                            WndAugment.this.hide();
                            StoneOfAugmentation.this.apply((Armor)toAugment, this.val$aug);
                        }
                    };
                    btnAug.setRect(2.0f, pos + 2.0f, 116.0f, 18.0f);
                    this.add(btnAug);
                    pos = btnAug.bottom();
                }
            }
            RedButton btnCancel = new RedButton(Messages.get(this, "cancel", new Object[0])){

                @Override
                protected void onClick() {
                    WndAugment.this.hide();
                    StoneOfAugmentation.this.activate(curUser.pos);
                }
            };
            btnCancel.setRect(2.0f, pos + 2.0f, 116.0f, 18.0f);
            this.add(btnCancel);
            this.resize(120, (int)btnCancel.bottom() + 2);
        }

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            StoneOfAugmentation.this.activate(curUser.pos);
        }
    }
}

