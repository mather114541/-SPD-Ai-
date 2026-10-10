/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Gloves
extends MeleeWeapon {
    public Gloves() {
        this.image = ItemSpriteSheet.GLOVES;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.3f;
        this.tier = 1;
        this.DLY = 0.5f;
        this.bones = false;
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
        int dmgBoost = this.augment.damageFactor(3 + this.buffedLvl());
        Sai.comboStrikeAbility(hero, target, 0.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 3 + this.buffedLvl() : 3;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.augment.damageFactor(dmgBoost));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + this.augment.damageFactor(3 + level);
    }
}

