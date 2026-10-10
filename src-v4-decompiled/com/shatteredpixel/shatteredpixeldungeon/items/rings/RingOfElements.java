/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import java.util.HashSet;

public class RingOfElements
extends Ring {
    public static final HashSet<Class> RESISTS = new HashSet();

    public RingOfElements() {
        this.icon = ItemSpriteSheet.Icons.RING_ELEMENTS;
        this.buffClass = Resistance.class;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.825f, this.soloBuffedBonus()))));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.825f, this.combinedBuffedBonus(Dungeon.hero)))));
            }
            return info;
        }
        return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 17.5));
    }

    @Override
    public String upgradeStat1(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100.0 * (1.0 - Math.pow(0.825f, level + 1))) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Resistance();
    }

    public static float resist(Char target, Class effect) {
        if (RingOfElements.getBuffedBonus(target, Resistance.class) == 0) {
            return 1.0f;
        }
        for (Class c : RESISTS) {
            if (!c.isAssignableFrom(effect)) continue;
            return (float)Math.pow(0.825, RingOfElements.getBuffedBonus(target, Resistance.class));
        }
        return 1.0f;
    }

    static {
        RESISTS.add(Burning.class);
        RESISTS.add(Chill.class);
        RESISTS.add(Frost.class);
        RESISTS.add(Ooze.class);
        RESISTS.add(Paralysis.class);
        RESISTS.add(Poison.class);
        RESISTS.add(Corrosion.class);
        RESISTS.add(ToxicGas.class);
        RESISTS.add(Electricity.class);
        RESISTS.addAll(AntiMagic.RESISTS);
    }

    public class Resistance
    extends Ring.RingBuff {
    }
}

