/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemJournalButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class WndUseItem
extends WndInfoItem {
    private static final float BUTTON_HEIGHT = 16.0f;
    private static final float GAP = 2.0f;
    public Window owner;
    public Item item;

    public WndUseItem(final Window owner, final Item item) {
        super(item);
        this.owner = owner;
        this.item = item;
        float y = this.height;
        if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
            y += 2.0f;
            ArrayList<RedButton> buttons = new ArrayList<RedButton>();
            for (final String action : item.actions(Dungeon.hero)) {
                RedButton btn = new RedButton(item.actionName(action, Dungeon.hero), 8){

                    @Override
                    protected void onClick() {
                        WndUseItem.this.hide();
                        if (owner != null && owner.parent != null) {
                            owner.hide();
                        }
                        if (Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(item)) {
                            item.execute(Dungeon.hero, action);
                        }
                        Item.updateQuickslot();
                        if (action.equals(item.defaultAction()) && item.usesTargeting && owner == null) {
                            InventoryPane.useTargeting();
                        }
                    }
                };
                btn.setSize(btn.reqWidth(), 16.0f);
                buttons.add(btn);
                this.add(btn);
                if (!action.equals(item.defaultAction())) continue;
                btn.textColor(0xFFFF44);
            }
            y = WndUseItem.layoutButtons(buttons, this.width, y);
            ItemJournalButton btn = new ItemJournalButton(item, this);
            btn.setRect(this.width - 16, 0.0f, 16.0f, 16.0f);
            this.add(btn);
        }
        this.resize(this.width, (int)y);
    }

    private static float layoutButtons(ArrayList<RedButton> buttons, float width, float y) {
        ArrayList<RedButton> curRow = new ArrayList<RedButton>();
        float widthLeftThisRow = width;
        while (!buttons.isEmpty()) {
            RedButton btn = buttons.get(0);
            widthLeftThisRow -= btn.width();
            if (curRow.isEmpty()) {
                curRow.add(btn);
                buttons.remove(btn);
            } else if ((widthLeftThisRow -= 1.0f) >= 0.0f) {
                curRow.add(btn);
                buttons.remove(btn);
            }
            if (!buttons.isEmpty() && !(widthLeftThisRow <= 0.0f) && curRow.size() < 3) continue;
            widthLeftThisRow = width - (float)(curRow.size() - 1);
            for (RedButton b : curRow) {
                widthLeftThisRow -= b.width();
            }
            while (widthLeftThisRow > 0.0f) {
                float widthToGrow;
                ArrayList<RedButton> shortest = new ArrayList<RedButton>();
                Component secondShortest = null;
                for (RedButton b : curRow) {
                    if (shortest.isEmpty()) {
                        shortest.add(b);
                        continue;
                    }
                    if (b.width() < ((RedButton)shortest.get(0)).width()) {
                        secondShortest = (RedButton)shortest.get(0);
                        shortest.clear();
                        shortest.add(b);
                        continue;
                    }
                    if (b.width() == ((RedButton)shortest.get(0)).width()) {
                        shortest.add(b);
                        continue;
                    }
                    if (secondShortest != null && !(secondShortest.width() > b.width())) continue;
                    secondShortest = b;
                }
                if (secondShortest == null) {
                    widthToGrow = widthLeftThisRow / (float)shortest.size();
                    widthLeftThisRow = 0.0f;
                } else {
                    widthToGrow = secondShortest.width() - ((RedButton)shortest.get(0)).width();
                    if (widthToGrow * (float)shortest.size() >= widthLeftThisRow) {
                        widthToGrow = widthLeftThisRow / (float)shortest.size();
                        widthLeftThisRow = 0.0f;
                    } else {
                        widthLeftThisRow -= widthToGrow * (float)shortest.size();
                    }
                }
                for (RedButton toGrow : shortest) {
                    toGrow.setRect(0.0f, 0.0f, toGrow.width() + widthToGrow, toGrow.height());
                }
            }
            float x = 0.0f;
            for (RedButton b : curRow) {
                b.setRect(x, y, b.width(), b.height());
                x += b.width() + 1.0f;
            }
            y += 17.0f;
            widthLeftThisRow = width;
            curRow.clear();
        }
        return y - 1.0f;
    }
}

