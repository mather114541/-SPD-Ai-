/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class RoundShield
extends MeleeWeapon {
    public RoundShield() {
        this.image = ItemSpriteSheet.ROUND_SHIELD;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 3;
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
        return 4 + lvl;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            return Messages.get(this, "stats_desc", 4 + this.buffedLvl());
        }
        return Messages.get(this, "typical_stats_desc", 4);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        RoundShield.guardAbility(hero, 5 + this.buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 5 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 5);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(5 + level);
    }

    public static void guardAbility(Hero hero, int duration, MeleeWeapon wep) {
        wep.beforeAbilityUsed(hero, null);
        Buff.prolong((Char)hero, GuardTracker.class, (float)((float)duration)).hasBlocked = false;
        hero.sprite.operate(hero.pos);
        hero.spendAndNext(1.0f);
        wep.afterAbilityUsed(hero);
    }

    public static class GuardTracker
    extends FlavourBuff {
        public boolean hasBlocked;
        private static final String BLOCKED = "blocked";

        public GuardTracker() {
            this.announced = true;
            this.type = Buff.buffType.POSITIVE;
            this.hasBlocked = false;
        }

        @Override
        public int icon() {
            return 61;
        }

        @Override
        public void tintIcon(Image icon) {
            if (this.hasBlocked) {
                icon.tint(6627174, 0.5f);
            } else {
                icon.resetColor();
            }
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(BLOCKED, this.hasBlocked);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.hasBlocked = bundle.getBoolean(BLOCKED);
        }
    }
}

