/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Spear
extends MeleeWeapon {
    public Spear() {
        this.image = ItemSpriteSheet.SPEAR;
        this.hitSound = "sounds/hit_stab.mp3";
        this.hitSoundPitch = 0.9f;
        this.tier = 2;
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
        int dmgBoost = this.augment.damageFactor(9 + Math.round(2.0f * (float)this.buffedLvl()));
        Spear.spikeAbility(hero, target, 1.0f, dmgBoost, this);
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 9 + Math.round(2.0f * (float)this.buffedLvl()) : 9;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 9 + Math.round(2.0f * (float)level);
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }

    public static void spikeAbility(final Hero hero, Integer target, final float dmgMulti, final int dmgBoost, final MeleeWeapon wep) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(wep, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        hero.belongings.abilityWeapon = wep;
        if (!hero.canAttack(enemy) || Dungeon.level.adjacent(hero.pos, enemy.pos)) {
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
                int oldPos = enemy.pos;
                if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {
                    if (enemy.isAlive() && enemy.pos == oldPos && !Pushing.pushingExistsForChar(enemy)) {
                        Ballistica trajectory = new Ballistica(hero.pos, enemy.pos, 1);
                        trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
                        WandOfBlastWave.throwChar(enemy, trajectory, 1, true, false, hero);
                    } else if (!enemy.isAlive()) {
                        MeleeWeapon.onAbilityKill(hero, enemy);
                    }
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                wep.afterAbilityUsed(hero);
            }
        });
    }
}

