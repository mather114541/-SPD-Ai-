/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfTenacity
extends Ring {
    public RingOfTenacity() {
        this.icon = ItemSpriteSheet.Icons.RING_TENACITY;
        this.buffClass = Tenacity.class;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.85f, this.soloBuffedBonus()))));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.85f, this.combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        }
        return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 15.0));
    }

    @Override
    public String upgradeStat1(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.85f, level + 1))) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Tenacity();
    }

    public static float damageMultiplier(Char t) {
        return (float)Math.pow(0.85, (float)RingOfTenacity.getBuffedBonus(t, Tenacity.class) * ((float)(t.HT - t.HP) / (float)t.HT));
    }

    public class Tenacity
    extends Ring.RingBuff {
    }
}

