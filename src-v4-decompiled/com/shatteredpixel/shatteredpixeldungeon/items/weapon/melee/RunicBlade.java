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

public class RunicBlade
extends MeleeWeapon {
    public RunicBlade() {
        this.image = ItemSpriteSheet.RUNIC_BLADE;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.0f;
        this.tier = 4;
    }

    @Override
    public int max(int lvl) {
        return 5 * this.tier + Math.round(lvl * (this.tier + 2));
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(final Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        final RunicSlashTracker tracker = Buff.affect(hero, RunicSlashTracker.class);
        tracker.boost = 3.0f + 0.5f * (float)this.buffedLvl();
        hero.belongings.abilityWeapon = this;
        if (!hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "ability_target_range", new Object[0]), new Object[0]);
            tracker.detach();
            hero.belongings.abilityWeapon = null;
            return;
        }
        hero.belongings.abilityWeapon = null;
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                RunicBlade.this.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, 1.0f, 0.0f, Char.INFINITE_ACCURACY)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                    if (!enemy.isAlive()) {
                        MeleeWeapon.onAbilityKill(hero, enemy);
                    }
                }
                tracker.detach();
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                RunicBlade.this.afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        if (this.levelKnown) {
            return Messages.get(this, "ability_desc", 300 + 50 * this.buffedLvl());
        }
        return Messages.get(this, "typical_ability_desc", 300);
    }

    @Override
    public String upgradeAbilityStat(int level) {
        return "+" + (300 + 50 * level) + "%";
    }

    public static class RunicSlashTracker
    extends FlavourBuff {
        public float boost = 2.0f;
    }
}

