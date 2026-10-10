/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfMight
extends Ring {
    public RingOfMight() {
        this.icon = ItemSpriteSheet.Icons.RING_MIGHT;
        this.buffClass = Might.class;
    }

    @Override
    public boolean doEquip(Hero hero) {
        if (super.doEquip(hero)) {
            hero.updateHT(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            hero.updateHT(false);
            return true;
        }
        return false;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        this.updateTargetHT();
        return this;
    }

    @Override
    public Item level(int value) {
        super.level(value);
        this.updateTargetHT();
        return this;
    }

    private void updateTargetHT() {
        if (this.buff != null && this.buff.target instanceof Hero) {
            ((Hero)this.buff.target).updateHT(false);
        }
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", this.soloBonus(), Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.035, this.soloBuffedBonus()) - 1.0)));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", RingOfMight.getBonus(Dungeon.hero, Might.class), Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.035, this.combinedBuffedBonus(Dungeon.hero)) - 1.0)));
            }
            return info;
        }
        return Messages.get(this, "typical_stats", 1, Messages.decimalFormat("#.##", 3.5));
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
        return Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.035, level + 1) - 1.0)) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Might();
    }

    public static int strengthBonus(Char target) {
        return RingOfMight.getBonus(target, Might.class);
    }

    public static float HTMultiplier(Char target) {
        return (float)Math.pow(1.035, RingOfMight.getBuffedBonus(target, Might.class));
    }

    public class Might
    extends Ring.RingBuff {
    }
}

