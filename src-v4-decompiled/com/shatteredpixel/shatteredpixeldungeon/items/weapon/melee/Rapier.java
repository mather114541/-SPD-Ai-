/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;

public class Rapier
extends MeleeWeapon {
    public Rapier() {
        this.image = ItemSpriteSheet.RAPIER;
        this.hitSound = "sounds/hit_slash.mp3";
        this.hitSoundPitch = 1.3f;
        this.tier = 1;
        this.bones = false;
    }

    @Override
    public int max(int lvl) {
        return 4 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    public int defenseFactor(Char owner) {
        return 1;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void duelistAbility(Hero hero, Integer target) {
        int dmgBoost = this.augment.damageFactor(5 + Math.round(1.5f * (float)this.buffedLvl()));
        Rapier.lungeAbility(hero, target, 1.0f, dmgBoost, this);
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

    public static void lungeAbility(final Hero hero, Integer target, final float dmgMulti, final int dmgBoost, final MeleeWeapon wep) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (Dungeon.level.heroFOV[target] && (enemy == null || enemy == hero || hero.isCharmedBy(enemy))) {
            GLog.w(Messages.get(wep, "ability_no_target", new Object[0]), new Object[0]);
            return;
        }
        if (hero.rooted || Dungeon.level.distance(hero.pos, target) < 2 || Dungeon.level.distance(hero.pos, target) - 1 > wep.reachFactor(hero)) {
            GLog.w(Messages.get(wep, "ability_target_range", new Object[0]), new Object[0]);
            if (hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
            }
            return;
        }
        int lungeCell = -1;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (Dungeon.level.distance(hero.pos + i, target) > wep.reachFactor(hero) || Actor.findChar(hero.pos + i) != null || !Dungeon.level.passable[hero.pos + i] && (!Dungeon.level.avoid[hero.pos + i] || !hero.flying) || lungeCell != -1 && !(Dungeon.level.trueDistance(hero.pos + i, target) < Dungeon.level.trueDistance(lungeCell, target))) continue;
            lungeCell = hero.pos + i;
        }
        if (lungeCell == -1) {
            GLog.w(Messages.get(wep, "ability_target_range", new Object[0]), new Object[0]);
            return;
        }
        final int dest = lungeCell;
        hero.busy();
        Sample.INSTANCE.play("sounds/miss.mp3");
        hero.sprite.jump(hero.pos, dest, 0.0f, 0.1f, new Callback(){

            @Override
            public void call() {
                if (Dungeon.level.map[hero.pos] == 6) {
                    Door.leave(hero.pos);
                }
                hero.pos = dest;
                Dungeon.level.occupyCell(hero);
                Dungeon.observe();
                hero.belongings.abilityWeapon = wep;
                if (enemy != null && hero.canAttack(enemy)) {
                    hero.sprite.attack(enemy.pos, new Callback(){

                        @Override
                        public void call() {
                            wep.beforeAbilityUsed(hero, enemy);
                            AttackIndicator.target(enemy);
                            if (hero.attack(enemy, dmgMulti, dmgBoost, Char.INFINITE_ACCURACY)) {
                                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                                if (!enemy.isAlive()) {
                                    MeleeWeapon.onAbilityKill(hero, enemy);
                                }
                            }
                            Invisibility.dispel();
                            hero.spendAndNext(hero.attackDelay());
                            wep.afterAbilityUsed(hero);
                        }
                    });
                } else {
                    MeleeWeapon.Charger charger = Buff.affect(hero, MeleeWeapon.Charger.class);
                    charger.partialCharge -= 1.0f;
                    while (charger.partialCharge < 0.0f && charger.charges > 0) {
                        --charger.charges;
                        charger.partialCharge += 1.0f;
                    }
                    hero.belongings.abilityWeapon = null;
                    Item.updateQuickslot();
                    GLog.w(Messages.get(Rapier.class, "ability_no_target", new Object[0]), new Object[0]);
                    hero.spendAndNext(1.0f / hero.speed());
                }
            }
        });
    }
}

