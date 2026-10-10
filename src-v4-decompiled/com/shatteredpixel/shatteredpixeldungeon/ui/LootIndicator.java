/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.Tag;
import com.watabou.input.GameAction;

public class LootIndicator
extends Tag {
    private ItemSlot slot;
    private Item lastItem = null;
    private int lastQuantity = 0;

    public LootIndicator() {
        super(1595544);
        this.setSize(SIZE, SIZE);
        this.visible = false;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.slot = new ItemSlot(){

            @Override
            protected void onClick() {
                LootIndicator.this.onClick();
                if (Dungeon.hero.ready && Dungeon.hero.handle(Dungeon.hero.pos)) {
                    Dungeon.hero.next();
                }
            }

            @Override
            public GameAction keyAction() {
                return SPDAction.TAG_LOOT;
            }

            @Override
            public GameAction secondaryTooltipAction() {
                return SPDAction.WAIT_OR_PICKUP;
            }
        };
        this.slot.showExtraInfo(false);
        this.add(this.slot);
    }

    @Override
    protected void layout() {
        super.layout();
        if (!this.flipped) {
            this.slot.setRect(this.x, this.y, SIZE, this.height);
            this.slot.setMargins(2, 2, 0, 2);
        } else {
            this.slot.setRect(this.x + (this.width() - (float)SIZE), this.y, SIZE, this.height);
            this.slot.setMargins(0, 2, 2, 2);
        }
    }

    @Override
    public void update() {
        if (Dungeon.hero.ready) {
            Heap heap = (Heap)Dungeon.level.heaps.get(Dungeon.hero.pos);
            if (heap != null) {
                Item item;
                Item item2 = heap.type == Heap.Type.CHEST ? ItemSlot.CHEST : (heap.type == Heap.Type.LOCKED_CHEST ? ItemSlot.LOCKED_CHEST : (heap.type == Heap.Type.CRYSTAL_CHEST ? ItemSlot.CRYSTAL_CHEST : (heap.type == Heap.Type.TOMB ? ItemSlot.TOMB : (heap.type == Heap.Type.SKELETON ? ItemSlot.SKELETON : (item = heap.type == Heap.Type.REMAINS ? ItemSlot.REMAINS : heap.peek())))));
                if (item != this.lastItem || item.quantity() != this.lastQuantity) {
                    this.lastItem = item;
                    this.lastQuantity = item.quantity();
                    this.slot.item(item);
                    this.flash();
                }
                this.visible = true;
            } else {
                this.lastItem = null;
                this.visible = false;
            }
        }
        this.slot.enable(this.visible && Dungeon.hero.ready);
        super.update();
    }
}

