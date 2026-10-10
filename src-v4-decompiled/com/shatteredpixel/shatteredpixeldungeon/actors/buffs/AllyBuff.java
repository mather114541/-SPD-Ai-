/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;

public abstract class AllyBuff
extends Buff {
    public AllyBuff() {
        this.revivePersists = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.alignment = Char.Alignment.ALLY;
            if (target.buff(PinCushion.class) != null) {
                target.buff(PinCushion.class).detach();
            }
            return true;
        }
        return false;
    }

    public static void affectAndLoot(Mob enemy, Hero hero, Class<? extends AllyBuff> buffCls) {
        boolean wasEnemy = enemy.alignment == Char.Alignment.ENEMY || enemy instanceof Mimic;
        Buff.affect(enemy, buffCls);
        if (enemy.buff(buffCls) != null && wasEnemy) {
            int exp;
            enemy.rollToDropLoot();
            ++Statistics.enemiesSlain;
            Badges.validateMonstersSlain();
            Statistics.qualifiedForNoKilling = false;
            Bestiary.setSeen(enemy.getClass());
            Bestiary.countEncounter(enemy.getClass());
            AscensionChallenge.processEnemyKill(enemy);
            int n = exp = hero.lvl <= enemy.maxLvl ? enemy.EXP : 0;
            if (exp > 0) {
                hero.sprite.showStatusWithIcon(65280, Integer.toString(exp), FloatingText.EXPERIENCE, new Object[0]);
            }
            hero.earnExp(exp, enemy.getClass());
            if (hero.subClass == HeroSubClass.MONK) {
                Buff.affect(hero, MonkEnergy.class).gainEnergy(enemy);
            }
        }
    }
}

