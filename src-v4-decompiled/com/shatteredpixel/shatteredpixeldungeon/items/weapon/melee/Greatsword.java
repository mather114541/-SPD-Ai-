/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Greatsword
extends MeleeWeapon {
    public Greatsword() {
        this.image = ItemSpriteSheet.GREATSWORD;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 5;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        if (hero.buff(Sword.CleaveTracker.class) != null) {
            return 0;
        }
        return 1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(7 + this.buffedLvl());
        Sword.cleaveAbility(hero, target, 1.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 7 + this.buffedLvl() : 7;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 7 + level;
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }
}

