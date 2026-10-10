/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class BattleAxe
extends MeleeWeapon {
    public BattleAxe() {
        this.image = ItemSpriteSheet.BATTLE_AXE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 0.9f;
        this.tier = 4;
        this.ACC = 1.24f;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(5 + Math.round(1.5f * (float)this.buffedLvl()));
        Mace.heavyBlowAbility(hero, target, 1.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 5 + Math.round(1.5f * (float)this.buffedLvl()) : 5;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 5 + Math.round(1.5f * (float)level);
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }
}

