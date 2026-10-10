/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfSharpshooting
extends Ring {
    public RingOfSharpshooting() {
        this.icon = ItemSpriteSheet.Icons.RING_SHARPSHOOT;
        this.buffClass = Aim.class;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", this.soloBuffedBonus(), Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2, this.soloBonus()) - 1.0)));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", this.combinedBuffedBonus(Dungeon.hero), Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2, this.combinedBonus(Dungeon.hero)) - 1.0)));
            }
            return info;
        }
        return Messages.get(this, "typical_stats", 1, Messages.decimalFormat("#.##", 20.0));
    }

    @Override
    public String upgradeStat1(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Integer.toString(level + 1);
    }

    @Override
    public String upgradeStat2(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2, level + 1) - 1.0)) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Aim();
    }

    public static int levelDamageBonus(Char target) {
        return RingOfSharpshooting.getBuffedBonus(target, Aim.class);
    }

    public static float durabilityMultiplier(Char target) {
        return (float)Math.pow(1.2, RingOfSharpshooting.getBonus(target, Aim.class));
    }

    public class Aim
    extends Ring.RingBuff {
    }
}

