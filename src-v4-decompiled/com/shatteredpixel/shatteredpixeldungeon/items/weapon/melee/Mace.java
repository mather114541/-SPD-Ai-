/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Daze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Mace
extends MeleeWeapon {
    public Mace() {
        this.image = ItemSpriteSheet.MACE;
        this.hitSound = "sounds/hit_crush.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 3;
        this.ACC = 1.28f;
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

    public static void heavyBlowAbility(final Hero hero, Integer target, float dmgMulti, int dmgBoost, final MeleeWeapon wep) {
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
        if (enemy instanceof Mob && !((Mob)enemy).surprisedBy(hero)) {
            dmgMulti = Math.min(1.0f, dmgMulti);
            dmgBoost = 0;
        }
        final float finalDmgMulti = dmgMulti;
        final int finalDmgBoost = dmgBoost;
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                wep.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, finalDmgMulti, finalDmgBoost, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                    if (enemy.isAlive()) {
                        Buff.affect(enemy, Daze.class, 5.0f);
                    } else {
                        MeleeWeapon.onAbilityKill(hero, enemy);
                    }
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                wep.afterAbilityUsed(hero);
            }
        });
    }
}

