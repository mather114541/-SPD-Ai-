/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfHaste
extends Ring {
    public RingOfHaste() {
        this.icon = ItemSpriteSheet.Icons.RING_HASTE;
        this.buffClass = Haste.class;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.15f, this.soloBuffedBonus()) - 1.0)));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.15f, this.combinedBuffedBonus(Dungeon.hero)) - 1.0)));
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
        return Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.15f, level + 1) - 1.0)) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Haste();
    }

    public static float speedMultiplier(Char target) {
        return (float)Math.pow(1.15, RingOfHaste.getBuffedBonus(target, Haste.class));
    }

    public class Haste
    extends Ring.RingBuff {
    }
}

