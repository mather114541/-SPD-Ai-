/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class PinCushion
extends Buff {
    private ArrayList<MissileWeapon> items = new ArrayList();
    private static final String ITEMS = "items";

    public void stick(MissileWeapon projectile) {
        for (int i = 0; i < this.items.size(); ++i) {
            if (!projectile.isSimilar(this.items.get(i))) continue;
            projectile.merge(this.items.get(i));
            this.items.set(i, projectile);
            if (TippedDart.lostDarts > 0) {
                Dart d = new Dart();
                d.quantity(TippedDart.lostDarts);
                TippedDart.lostDarts = 0;
                this.stick(d);
            }
            return;
        }
        this.items.add(projectile);
    }

    public Item grabOne() {
        Item item = this.items.remove(0);
        if (this.items.isEmpty()) {
            this.detach();
        }
        return item;
    }

    public ArrayList<MissileWeapon> getStuckItems() {
        return new ArrayList<MissileWeapon>(this.items);
    }

    @Override
    public void detach() {
        for (Item item : this.items) {
            Dungeon.level.drop((Item)item, (int)this.target.pos).sprite.drop();
        }
        super.detach();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(ITEMS, this.items);
        super.storeInBundle(bundle);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.items = new ArrayList<Bundlable>(bundle.getCollection(ITEMS));
        super.restoreFromBundle(bundle);
    }

    @Override
    public int icon() {
        return 49;
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", new Object[0]);
        for (Item item : this.items) {
            desc = (String)desc + "\n" + item.title();
        }
        return desc;
    }
}

