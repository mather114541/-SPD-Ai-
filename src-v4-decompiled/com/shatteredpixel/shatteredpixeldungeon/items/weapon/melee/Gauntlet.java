/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Gauntlet
extends MeleeWeapon {
    public Gauntlet() {
        this.image = ItemSpriteSheet.GAUNTLETS;
        this.hitSound = "sounds/hit_crush.mp3";
        this.hitSoundPitch = 1.2f;
        this.tier = 5;
        this.DLY = 0.5f;
    }

    @Override
    public int max(int lvl) {
        return Math.round(2.5f * (float)(this.tier + 1)) + lvl * Math.round(0.5f * (float)(this.tier + 1));
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(5 + this.buffedLvl());
        Sai.comboStrikeAbility(hero, target, 0.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 5 + this.buffedLvl() : 5;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.augment.damageFactor(dmgBoost));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + this.augment.damageFactor(5 + level);
    }
}

