/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class WarScythe
extends MeleeWeapon {
    public WarScythe() {
        this.image = ItemSpriteSheet.WAR_SCYTHE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 0.9f;
        this.tier = 5;
        this.ACC = 0.8f;
    }

    @Override
    public int max(int lvl) {
        return Math.round(6.67f * (float)(this.tier + 1)) + lvl * (this.tier + 1);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int bleedAmt = this.augment.damageFactor(Math.round(30.0f + 4.5f * (float)this.buffedLvl()));
        Sickle.harvestAbility(hero, target, 0.0f, bleedAmt, this);
    }

    @Override
    public String abilityInfo() {
        int bleedAmt;
        int n = bleedAmt = this.levelKnown ? Math.round(30.0f + 4.5f * (float)this.buffedLvl()) : 30;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(bleedAmt));
        }
        return Messages.get(this, "typical_ability_desc", bleedAmt);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(this.augment.damageFactor(Math.round(30.0f + 4.5f * (float)level)));
    }
}

