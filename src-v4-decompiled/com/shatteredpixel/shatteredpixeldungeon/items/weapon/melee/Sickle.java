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

public class Sickle
extends MeleeWeapon {
    public Sickle() {
        this.image = ItemSpriteSheet.SICKLE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 2;
        this.ACC = 0.68f;
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
        int bleedAmt = this.augment.damageFactor(Math.round(15.0f + 2.5f * (float)this.buffedLvl()));
        Sickle.harvestAbility(hero, target, 0.0f, bleedAmt, this);
    }

    @Override
    public String abilityInfo() {
        int bleedAmt;
        int n = bleedAmt = this.levelKnown ? Math.round(15.0f + 2.5f * (float)this.buffedLvl()) : 15;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(bleedAmt));
        }
        return Messages.get(this, "typical_ability_desc", bleedAmt);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return Integer.toString(this.augment.damageFactor(Math.round(15.0f + 2.5f * (float)level)));
    }

    public static void harvestAbility(final Hero hero, Integer target, final float bleedMulti, final int bleedBoost, final MeleeWeapon wep) {
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
                Buff.affect(hero, HarvestBleedTracker.class, 0.0f);
                if (hero.attack(enemy, bleedMulti, bleedBoost, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                if (!enemy.isAlive()) {
                    MeleeWeapon.onAbilityKill(hero, enemy);
                }
                wep.afterAbilityUsed(hero);
            }
        });
    }

    public static class HarvestBleedTracker
    extends FlavourBuff {
    }
}

