/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuickBag;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import java.util.Iterator;

public class Bag
extends Item
implements Iterable<Item> {
    public static final String AC_OPEN = "OPEN";
    public Char owner;
    public ArrayList<Item> items;
    public Item quickUseItem;
    private static final String ITEMS = "inventory";
    private boolean loading;

    public Bag() {
        this.image = 11;
        this.defaultAction = AC_OPEN;
        this.unique = true;
        this.items = new ArrayList();
    }

    public int capacity() {
        return 20;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (this.quickUseItem != null) {
            return this.quickUseItem.targetingPos(user, dst);
        }
        return super.targetingPos(user, dst);
    }

    @Override
    public void execute(Hero hero, String action) {
        this.quickUseItem = null;
        super.execute(hero, action);
        if (action.equals(AC_OPEN) && !this.items.isEmpty()) {
            GameScene.show(new WndQuickBag(this));
        }
    }

    @Override
    public boolean collect(Bag container) {
        this.grabItems(container);
        for (Item item : this.items) {
            Dungeon.quickslot.replacePlaceholder(item);
        }
        if (super.collect(container)) {
            this.owner = container.owner;
            Badges.validateAllBagsBought(this);
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        this.owner = null;
        for (Item item : this.items) {
            Dungeon.quickslot.clearItem(item);
        }
        Bag.updateQuickslot();
    }

    public void grabItems() {
        if (this.owner != null && this.owner instanceof Hero && this != ((Hero)this.owner).belongings.backpack) {
            this.grabItems(((Hero)this.owner).belongings.backpack);
        }
    }

    public void grabItems(Bag container) {
        for (Item item : container.items.toArray(new Item[0])) {
            if (!this.canHold(item)) continue;
            int slot = Dungeon.quickslot.getSlot(item);
            item.detachAll(container);
            if (!item.collect(this)) {
                item.collect(container);
            }
            if (slot == -1) continue;
            Dungeon.quickslot.setSlot(slot, item);
        }
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    public void clear() {
        this.items.clear();
    }

    public void resurrect() {
        for (Item item : this.items.toArray(new Item[0])) {
            if (item.unique) continue;
            this.items.remove(item);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ITEMS, this.items);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.loading = true;
        for (Bundlable item : bundle.getCollection(ITEMS)) {
            if (item == null || ((Item)item).collect(this)) continue;
            this.items.add((Item)item);
        }
        this.loading = false;
    }

    public boolean contains(Item item) {
        for (Item i : this.items) {
            if (i == item) {
                return true;
            }
            if (!(i instanceof Bag) || !((Bag)i).contains(item)) continue;
            return true;
        }
        return false;
    }

    public boolean canHold(Item item) {
        if (!this.loading && this.owner != null && this.owner.buff(LostInventory.class) != null && !item.keptThroughLostInventory()) {
            return false;
        }
        if (this.items.contains(item) || item instanceof Bag || this.items.size() < this.capacity()) {
            return true;
        }
        if (item.stackable) {
            for (Item i : this.items) {
                if (!item.isSimilar(i)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Item> iterator() {
        return new ItemIterator();
    }

    private class ItemIterator
    implements Iterator<Item> {
        private int index = 0;
        private Iterator<Item> nested = null;

        private ItemIterator() {
        }

        @Override
        public boolean hasNext() {
            if (this.nested != null) {
                return this.nested.hasNext() || this.index < Bag.this.items.size();
            }
            return this.index < Bag.this.items.size();
        }

        @Override
        public Item next() {
            Item item;
            if (this.nested != null && this.nested.hasNext()) {
                return this.nested.next();
            }
            this.nested = null;
            if ((item = Bag.this.items.get(this.index++)) instanceof Bag) {
                this.nested = ((Bag)item).iterator();
            }
            return item;
        }

        @Override
        public void remove() {
            if (this.nested != null) {
                this.nested.remove();
            } else {
                Bag.this.items.remove(this.index);
            }
        }
    }
}

