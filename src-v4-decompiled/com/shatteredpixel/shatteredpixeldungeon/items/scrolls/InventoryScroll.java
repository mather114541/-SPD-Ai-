/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;

public abstract class InventoryScroll
extends Scroll {
    protected static boolean identifiedByUse = false;
    protected Class<? extends Bag> preferredBag = null;
    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return InventoryScroll.this.inventoryTitle();
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return InventoryScroll.this.preferredBag;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return InventoryScroll.this.usableOnItem(item);
        }

        @Override
        public void onSelect(Item item) {
            if (!(curItem instanceof InventoryScroll)) {
                return;
            }
            if (item != null) {
                if (!identifiedByUse && !(curItem instanceof ScrollOfUpgrade)) {
                    curItem = InventoryScroll.this.detach(curUser.belongings.backpack);
                }
                ((InventoryScroll)curItem).onItemSelected(item);
                if (!(curItem instanceof ScrollOfUpgrade)) {
                    ((InventoryScroll)curItem).readAnimation();
                    Sample.INSTANCE.play("sounds/read.mp3");
                }
            } else if (identifiedByUse && !((Scroll)curItem).anonymous) {
                ((InventoryScroll)curItem).confirmCancelation();
            } else if (((Scroll)curItem).anonymous) {
                curUser.spendAndNext(1.0f);
            }
        }
    };

    @Override
    public void doRead() {
        if (!this.isKnown()) {
            this.identify();
            curItem = this.detach(InventoryScroll.curUser.belongings.backpack);
            identifiedByUse = true;
        } else {
            identifiedByUse = false;
        }
        GameScene.selectItem(this.itemSelector);
    }

    private void confirmCancelation() {
        GameScene.show(new WndConfirmCancel());
    }

    private String inventoryTitle() {
        return Messages.get(this, "inv_title", new Object[0]);
    }

    protected boolean usableOnItem(Item item) {
        return true;
    }

    protected abstract void onItemSelected(Item var1);

    public class WndConfirmCancel
    extends WndOptions {
        public WndConfirmCancel() {
            super(new ItemSprite(InventoryScroll.this), Messages.titleCase(InventoryScroll.this.name()), Messages.get(InventoryScroll.this, "warning", new Object[0]), Messages.get(InventoryScroll.this, "yes", new Object[0]), Messages.get(InventoryScroll.this, "no", new Object[0]));
        }

        @Override
        protected void onSelect(int index) {
            switch (index) {
                case 0: {
                    curUser.spendAndNext(1.0f);
                    identifiedByUse = false;
                    break;
                }
                case 1: {
                    GameScene.selectItem(InventoryScroll.this.itemSelector);
                }
            }
        }

        @Override
        public void onBackPressed() {
        }

        public WndBag.ItemSelector getItemSelector() {
            return InventoryScroll.this.itemSelector;
        }
    }
}

