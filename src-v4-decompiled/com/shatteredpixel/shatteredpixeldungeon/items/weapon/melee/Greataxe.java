/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Greataxe
extends MeleeWeapon {
    public Greataxe() {
        this.image = ItemSpriteSheet.GREATAXE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 5;
    }

    @Override
    public int max(int lvl) {
        return 5 * (this.tier + 4) + lvl * (this.tier + 1);
    }

    @Override
    public int STRReq(int lvl) {
        int req = Greataxe.STRReq(this.tier + 1, lvl);
        if (this.masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(final Hero hero, Integer target) {
        if ((float)hero.HP / (float)hero.HT >= 0.5f) {
            GLog.w(Messages.get(this, "ability_cant_use", new Object[0]), new Object[0]);
            return;
        }
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "ability_target_range", new Object[0]), new Object[0]);
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                Greataxe.this.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                int dmgBoost = Greataxe.this.augment.damageFactor(15 + 2 * Greataxe.this.buffedLvl());
                if (hero.attack(enemy, 1.0f, dmgBoost, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                Invisibility.dispel();
                if (!enemy.isAlive()) {
                    hero.next();
                    MeleeWeapon.onAbilityKill(hero, enemy);
                } else {
                    hero.spendAndNext(hero.attackDelay());
                }
                Greataxe.this.afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        int dmgBoost;
        int n = dmgBoost = this.levelKnown ? 15 + 2 * this.buffedLvl() : 15;
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
        }
        return Messages.get(this, "typical_ability_desc", this.min(0) + dmgBoost, this.max(0) + dmgBoost);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 15 + 2 * level;
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }
}

