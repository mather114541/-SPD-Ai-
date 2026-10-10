/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class Smite
extends TargetedClericSpell {
    public static Smite INSTANCE = new Smite();

    @Override
    public int icon() {
        return 54;
    }

    @Override
    public int targetingFlags() {
        return 1;
    }

    @Override
    public String desc() {
        int min = 5 + Dungeon.hero.lvl / 2;
        int max = 10 + Dungeon.hero.lvl;
        return Messages.get(this, "desc", min, max) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public float chargeUse(Hero hero) {
        return 2.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.subClass == HeroSubClass.PALADIN;
    }

    @Override
    protected void onTargetSelected(final HolyTome tome, final Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        final Char enemy = Actor.findChar(target);
        if (enemy == null || enemy == hero) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        final SmiteTracker tracker = Buff.affect(hero, SmiteTracker.class);
        if (hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target] || !hero.canAttack(enemy)) {
            GLog.w(Messages.get(this, "invalid_enemy", new Object[0]), new Object[0]);
            tracker.detach();
            return;
        }
        hero.sprite.attack(enemy.pos, new Callback(){

            @Override
            public void call() {
                AttackIndicator.target(enemy);
                float accMult = 1.0f;
                if (!(hero.belongings.attackingWeapon() instanceof Weapon) || ((Weapon)hero.belongings.attackingWeapon()).STRReq() <= hero.STR()) {
                    accMult = Char.INFINITE_ACCURACY;
                }
                if (hero.attack(enemy, 1.0f, 0.0f, accMult)) {
                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                    enemy.sprite.burst(-1, 10);
                }
                tracker.detach();
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
                Smite.this.onSpellCast(tome, hero);
            }
        });
    }

    public static int bonusDmg(Hero attacker, Char defender) {
        int min = 5 + attacker.lvl / 2;
        int max = 10 + attacker.lvl;
        if (Char.hasProp(defender, Char.Property.UNDEAD) || Char.hasProp(defender, Char.Property.DEMONIC)) {
            return max;
        }
        return Hero.heroDamageIntRange(min, max);
    }

    public static class SmiteTracker
    extends FlavourBuff {
    }
}

