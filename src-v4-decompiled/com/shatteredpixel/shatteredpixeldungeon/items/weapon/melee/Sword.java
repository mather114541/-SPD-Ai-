/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Sword
extends MeleeWeapon {
    public Sword() {
        this.image = ItemSpriteSheet.SWORD;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 3;
    }

    @Override
    protected int baseChargeUse(Hero hero, Char target) {
        if (hero.buff(CleaveTracker.class) != null) {
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
        int dmgBoost = this.augment.damageFactor(5 + this.buffedLvl());
        Sword.cleaveAbility(hero, target, 1.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 5 + this.buffedLvl() : 5;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 5 + level;
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }

    public static void cleaveAbility(final Hero hero, Integer target, final float dmgMulti, final int dmgBoost, final MeleeWeapon wep) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(wep, "ability_target_range", new Object[0]), new Object[0]);
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                Invisibility.dispel();
                if (!enemy.isAlive()) {
                    hero.next();
                    MeleeWeapon.onAbilityKill(hero, enemy);
                    if (hero.buff(CleaveTracker.class) != null) {
                        hero.buff(CleaveTracker.class).detach();
                    } else {
                        Buff.prolong(hero, CleaveTracker.class, 4.0f);
                    }
                } else {
                    hero.spendAndNext(hero.attackDelay());
                    if (hero.buff(CleaveTracker.class) != null) {
                        hero.buff(CleaveTracker.class).detach();
                    }
                }
                wep.afterAbilityUsed(hero);
            }
        });
    }

    public static class CleaveTracker
    extends FlavourBuff {
        public CleaveTracker() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 60;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
        }
    }
}

