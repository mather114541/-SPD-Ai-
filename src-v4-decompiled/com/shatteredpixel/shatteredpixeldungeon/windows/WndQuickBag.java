/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventorySlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.Game;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WndQuickBag
extends Window {
    private static Item bag;
    public static final Comparator<Item> quickBagComparator;

    public WndQuickBag(final Bag bag) {
        super(0, 0, Chrome.get(Chrome.Type.TOAST_TR));
        if (WndBag.INSTANCE != null) {
            WndBag.INSTANCE.hide();
        }
        WndBag.INSTANCE = this;
        WndQuickBag.bag = bag;
        float width = 0.0f;
        float height = 0.0f;
        int maxWidth = PixelScene.landscape() ? 240 : 135;
        int left = 0;
        int top = 10;
        ArrayList<Item> items = new ArrayList<Item>();
        for (Item i : bag == null ? Dungeon.hero.belongings : bag) {
            if (i.defaultAction() == null || i instanceof Bag || i instanceof Artifact && !i.isEquipped(Dungeon.hero) && (!(i instanceof CloakOfShadows) || !Dungeon.hero.hasTalent(Talent.LIGHT_CLOAK)) && (!(i instanceof HolyTome) || !Dungeon.hero.hasTalent(Talent.LIGHT_READING))) continue;
            items.add(i);
        }
        Collections.sort(items, quickBagComparator);
        int btnWidth = 16;
        int btnHeight = 20;
        int targetHeight = PixelScene.uiCamera.height - 100;
        int rows = (int)Math.ceil((float)items.size() / (float)((maxWidth + 1) / (btnWidth + 1)));
        for (int expectedHeight = rows * btnHeight + (rows - 1); expectedHeight > targetHeight && btnHeight > 16; --btnHeight, expectedHeight -= rows) {
        }
        for (Item i : items) {
            InventorySlot slot = new InventorySlot(i){

                @Override
                protected void onClick() {
                    int idx;
                    if (Dungeon.hero == null || !Dungeon.hero.isAlive() || !Dungeon.hero.belongings.contains(this.item)) {
                        Game.scene().addToFront(new WndUseItem(WndQuickBag.this, this.item));
                        return;
                    }
                    WndQuickBag.this.hide();
                    this.item.execute(Dungeon.hero);
                    if (this.item.usesTargeting && bag != null && (idx = Dungeon.quickslot.getSlot(bag)) != -1) {
                        QuickSlotButton.useTargeting(idx);
                        bag.quickUseItem = this.item;
                    }
                }

                @Override
                protected boolean onLongClick() {
                    Game.scene().addToFront(new WndUseItem(WndQuickBag.this, this.item));
                    return true;
                }

                @Override
                protected String hoverText() {
                    return null;
                }
            };
            slot.showExtraInfo(false);
            slot.setRect(left, top, btnWidth, btnHeight);
            this.add(slot);
            if (width < slot.right()) {
                width = slot.right();
            }
            if (height < slot.bottom()) {
                height = slot.bottom();
            }
            if ((left += btnWidth + 1) + btnWidth <= maxWidth) continue;
            left = 0;
            top += btnHeight + 1;
        }
        RenderedTextBlock txtTitle = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title", new Object[0])), 8);
        txtTitle.hardlight(0xFFFF44);
        if (txtTitle.width() > width) {
            width = txtTitle.width();
        }
        txtTitle.setPos((width - txtTitle.width()) / 2.0f, (10.0f - txtTitle.height()) / 2.0f - 1.0f);
        PixelScene.align(txtTitle);
        this.add(txtTitle);
        this.resize((int)width, (int)height);
        int bottom = GameScene.uiCamera.height;
        this.offset(0, (int)((float)(bottom / 2 - 30) - height / 2.0f));
    }

    @Override
    public void hide() {
        super.hide();
        if (WndBag.INSTANCE == this) {
            WndBag.INSTANCE = null;
        }
    }

    static {
        quickBagComparator = new Comparator<Item>(){

            @Override
            public int compare(Item lhs, Item rhs) {
                if (lhs.isEquipped(Dungeon.hero) && !rhs.isEquipped(Dungeon.hero)) {
                    return -1;
                }
                if (!lhs.isEquipped(Dungeon.hero) && rhs.isEquipped(Dungeon.hero)) {
                    return 1;
                }
                return Generator.Category.order(lhs) - Generator.Category.order(rhs);
            }
        };
    }
}

