/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collection;

public class QuickSlot {
    public static int SIZE = 6;
    private Item[] slots = new Item[SIZE];
    private final String PLACEHOLDERS = "placeholders";
    private final String PLACEMENTS = "placements";

    public void setSlot(int slot, Item item) {
        this.clearItem(item);
        this.slots[slot] = item;
    }

    public void clearSlot(int slot) {
        this.slots[slot] = null;
    }

    public void reset() {
        this.slots = new Item[SIZE];
    }

    public Item getItem(int slot) {
        return this.slots[slot];
    }

    public int getSlot(Item item) {
        for (int i = 0; i < SIZE; ++i) {
            if (this.getItem(i) != item) continue;
            return i;
        }
        return -1;
    }

    public Boolean isPlaceholder(int slot) {
        return this.getItem(slot) != null && this.getItem(slot).quantity() == 0;
    }

    public Boolean isNonePlaceholder(int slot) {
        return this.getItem(slot) != null && this.getItem(slot).quantity() > 0;
    }

    public void clearItem(Item item) {
        if (this.contains(item)) {
            this.clearSlot(this.getSlot(item));
        }
    }

    public boolean contains(Item item) {
        return this.getSlot(item) != -1;
    }

    public void replacePlaceholder(Item item) {
        for (int i = 0; i < SIZE; ++i) {
            if (!this.isPlaceholder(i).booleanValue() || !item.isSimilar(this.getItem(i))) continue;
            this.setSlot(i, item);
        }
    }

    public void convertToPlaceholder(Item item) {
        if (this.contains(item)) {
            Item placeholder = item.virtual();
            if (placeholder == null) {
                return;
            }
            for (int i = 0; i < SIZE; ++i) {
                if (this.getItem(i) != item) continue;
                this.setSlot(i, placeholder);
            }
        }
    }

    public Item randomNonePlaceholder() {
        ArrayList<Item> result = new ArrayList<Item>();
        for (int i = 0; i < SIZE; ++i) {
            if (this.getItem(i) == null || this.isPlaceholder(i).booleanValue()) continue;
            result.add(this.getItem(i));
        }
        return (Item)Random.element(result);
    }

    public void storePlaceholders(Bundle bundle) {
        ArrayList<Item> placeholders = new ArrayList<Item>(SIZE);
        boolean[] placements = new boolean[SIZE];
        for (int i = 0; i < SIZE; ++i) {
            if (!this.isPlaceholder(i).booleanValue()) continue;
            placeholders.add(this.getItem(i));
            placements[i] = true;
        }
        bundle.put("placeholders", placeholders);
        bundle.put("placements", placements);
    }

    public void restorePlaceholders(Bundle bundle) {
        Collection<Bundlable> placeholders = bundle.getCollection("placeholders");
        boolean[] placements = bundle.getBooleanArray("placements");
        int i = 0;
        for (Bundlable item : placeholders) {
            while (!placements[i]) {
                ++i;
            }
            this.setSlot(i, (Item)item);
            ++i;
        }
    }
}

