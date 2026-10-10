/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class Scimitar
extends MeleeWeapon {
    public Scimitar() {
        this.image = ItemSpriteSheet.SCIMITAR;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.2f;
        this.tier = 3;
        this.DLY = 0.8f;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        this.beforeAbilityUsed(hero, null);
        Buff.prolong(hero, SwordDance.class, 3 + this.buffedLvl());
        hero.sprite.operate(hero.pos);
        hero.next();
        this.afterAbilityUsed(hero);
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 4 + this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 4);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(4 + level);
    }

    public static class SwordDance
    extends FlavourBuff {
        public SwordDance() {
            this.announced = true;
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 64;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (4.0f - this.visualcooldown()) / 4.0f);
        }
    }
}

