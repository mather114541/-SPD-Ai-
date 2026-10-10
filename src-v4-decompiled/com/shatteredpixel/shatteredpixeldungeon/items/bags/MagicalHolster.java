/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.bags;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class MagicalHolster
extends Bag {
    public static final float HOLSTER_SCALE_FACTOR = 0.85f;
    public static final float HOLSTER_DURABILITY_FACTOR = 1.2f;

    public MagicalHolster() {
        this.image = ItemSpriteSheet.HOLSTER;
    }

    @Override
    public boolean canHold(Item item) {
        if (item instanceof Wand || item instanceof MissileWeapon || item instanceof Bomb) {
            return super.canHold(item);
        }
        return false;
    }

    @Override
    public int capacity() {
        return 19;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (this.owner != null) {
                for (Item item : this.items) {
                    if (item instanceof Wand) {
                        ((Wand)item).charge(this.owner, 0.85f);
                        continue;
                    }
                    if (!(item instanceof MissileWeapon)) continue;
                    ((MissileWeapon)item).holster = true;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        for (Item item : this.items) {
            if (item instanceof Wand) {
                ((Wand)item).stopCharging();
                continue;
            }
            if (!(item instanceof MissileWeapon)) continue;
            ((MissileWeapon)item).holster = false;
        }
    }

    @Override
    public int value() {
        return 60;
    }
}

