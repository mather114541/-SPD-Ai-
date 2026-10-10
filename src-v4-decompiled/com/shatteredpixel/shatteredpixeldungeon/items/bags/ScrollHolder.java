/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class ScrollHolder
extends Bag {
    public ScrollHolder() {
        this.image = ItemSpriteSheet.HOLDER;
    }

    @Override
    public boolean canHold(Item item) {
        if (item instanceof Scroll || item instanceof Spell || item instanceof ArcaneResin || item instanceof Stylus) {
            return super.canHold(item);
        }
        return false;
    }

    @Override
    public int capacity() {
        return 19;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (Item item : this.items) {
            if (!(item instanceof BeaconOfReturning) || ((BeaconOfReturning)item).returnDepth == -1) continue;
            Notes.remove(Notes.Landmark.BEACON_LOCATION, ((BeaconOfReturning)item).returnDepth);
            ((BeaconOfReturning)item).returnDepth = -1;
        }
    }

    @Override
    public int value() {
        return 40;
    }
}

