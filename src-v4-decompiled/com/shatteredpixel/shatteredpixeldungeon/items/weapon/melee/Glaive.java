/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Glaive
extends MeleeWeapon {
    public Glaive() {
        this.image = ItemSpriteSheet.GLAIVE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 0.8f;
        this.tier = 5;
        this.DLY = 1.5f;
        this.RCH = 2;
    }

    @Override
    public int max(int lvl) {
        return Math.round(6.67f * (float)(this.tier + 1)) + lvl * Math.round(1.33f * (float)(this.tier + 1));
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(12 + Math.round(2.5f * (float)this.buffedLvl()));
        Spear.spikeAbility(hero, target, 1.0f, dmgBoost, this);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 12 + Math.round(2.5f * (float)level);
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 12 + Math.round(2.5f * (float)this.buffedLvl()) : 12;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }
}

