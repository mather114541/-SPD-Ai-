/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RingOfEnergy
extends Ring {
    public RingOfEnergy() {
        this.icon = ItemSpriteSheet.Icons.RING_ENERGY;
        this.buffClass = Energy.class;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.175f, this.soloBuffedBonus()) - 1.0)));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.175f, this.combinedBuffedBonus(Dungeon.hero)) - 1.0)));
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
        return Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.175f, level + 1) - 1.0)) + "%";
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Energy();
    }

    public static float wandChargeMultiplier(Char target) {
        float bonus = (float)Math.pow(1.175, RingOfEnergy.getBuffedBonus(target, Energy.class));
        if (target instanceof Hero && ((Hero)target).heroClass != HeroClass.CLERIC && ((Hero)target).hasTalent(Talent.LIGHT_READING)) {
            bonus *= 1.0f + 0.2f * (float)((Hero)target).pointsInTalent(Talent.LIGHT_READING) / 3.0f;
        }
        return bonus;
    }

    public static float artifactChargeMultiplier(Char target) {
        float bonus = (float)Math.pow(1.175, RingOfEnergy.getBuffedBonus(target, Energy.class));
        if (target instanceof Hero && ((Hero)target).heroClass != HeroClass.ROGUE && ((Hero)target).hasTalent(Talent.LIGHT_CLOAK)) {
            bonus *= 1.0f + 0.2f * (float)((Hero)target).pointsInTalent(Talent.LIGHT_CLOAK) / 3.0f;
        }
        return bonus;
    }

    public static float armorChargeMultiplier(Char target) {
        return (float)Math.pow(1.175, RingOfEnergy.getBuffedBonus(target, Energy.class));
    }

    public class Energy
    extends Ring.RingBuff {
    }
}

