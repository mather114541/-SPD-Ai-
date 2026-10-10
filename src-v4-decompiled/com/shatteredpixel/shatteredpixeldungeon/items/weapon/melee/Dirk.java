/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Dirk
extends MeleeWeapon {
    public Dirk() {
        this.image = ItemSpriteSheet.DIRK;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 2;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    public int damageRoll(Char owner) {
        Hero hero;
        Char enemy;
        if (owner instanceof Hero && (enemy = (hero = (Hero)owner).attackTarget()) instanceof Mob && ((Mob)enemy).surprisedBy(hero)) {
            int diff = this.max() - this.min();
            int damage = this.augment.damageFactor(Hero.heroDamageIntRange(this.min() + Math.round((float)diff * 0.67f), this.max()));
            int exStr = hero.STR() - this.STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
            return damage;
        }
        return super.damageRoll(owner);
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        Dagger.sneakAbility(hero, target, 4, 2 + this.buffedLvl(), this);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 2 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 2);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(2 + level);
    }
}

