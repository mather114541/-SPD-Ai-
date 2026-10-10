/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Crab;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Spinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Swarm;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import java.util.ArrayList;

public class Pickaxe
extends MeleeWeapon {
    public Pickaxe() {
        this.image = ItemSpriteSheet.PICKAXE;
        this.levelKnown = true;
        this.unique = true;
        this.bones = false;
        this.tier = 2;
    }

    @Override
    public int STRReq(int lvl) {
        return super.STRReq(lvl) + 2;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (Dungeon.level instanceof MiningLevel) {
            actions.remove("DROP");
            actions.remove("THROW");
        }
        return actions;
    }

    @Override
    public boolean keptThroughLostInventory() {
        return super.keptThroughLostInventory() || Dungeon.level instanceof MiningLevel;
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
                int damageBoost = 0;
                if (Char.hasProp(enemy, Char.Property.INORGANIC) || enemy instanceof Swarm || enemy instanceof Bee || enemy instanceof Crab || enemy instanceof Spinner || enemy instanceof Scorpio) {
                    damageBoost = Pickaxe.this.augment.damageFactor(8 + 2 * Pickaxe.this.buffedLvl());
                }
                Pickaxe.this.beforeAbilityUsed(hero, enemy);
                AttackIndicator.target(enemy);
                if (hero.attack(enemy, 1.0f, damageBoost, Char.INFINITE_ACCURACY)) {
                    if (enemy.isAlive()) {
                        Buff.affect(enemy, Vulnerable.class, 3.0f);
                    } else {
                        MeleeWeapon.onAbilityKill(hero, enemy);
                    }
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                }
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                Pickaxe.this.afterAbilityUsed(hero);
            }
        });
    }

    @Override
    public String abilityInfo() {
        int dmgBoost = 8 + 2 * this.buffedLvl();
        return Messages.get(this, "ability_desc", this.augment.damageFactor(this.min() + dmgBoost), this.augment.damageFactor(this.max() + dmgBoost));
    }

    @Override
    public String upgradeAbilityStat(int level) {
        int dmgBoost = 8 + 2 * level;
        return this.augment.damageFactor(this.min(level) + dmgBoost) + "-" + this.augment.damageFactor(this.max(level) + dmgBoost);
    }
}

