/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Greatshield
extends MeleeWeapon {
    public Greatshield() {
        this.image = ItemSpriteSheet.GREATSHIELD;
        this.tier = 5;
    }

    @Override
    public int max(int lvl) {
        return Math.round(3.0f * (float)(this.tier + 1)) + lvl * (this.tier - 1);
    }

    @Override
    public int defenseFactor(Char owner) {
        return this.DRMax();
    }

    public int DRMax() {
        return this.DRMax(this.buffedLvl());
    }

    public int DRMax(int lvl) {
        return 6 + 2 * lvl;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", 6 + 2 * this.buffedLvl());
        }
        return Messages.get(this, "typical_stats_desc", 6);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RoundShield.guardAbility(hero, 3 + this.buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 3 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 3);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(3 + level);
    }
}

