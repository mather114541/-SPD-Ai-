/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class LayOnHands
extends TargetedClericSpell {
    public static LayOnHands INSTANCE = new LayOnHands();

    @Override
    public int icon() {
        return 55;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 10 + 5 * Dungeon.hero.pointsInTalent(Talent.LAY_ON_HANDS)) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public int targetingFlags() {
        return -1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.LAY_ON_HANDS);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (Dungeon.level.distance(hero.pos, target) > 1) {
            GLog.w(Messages.get(this, "invalid_target", new Object[0]), new Object[0]);
            return;
        }
        Char ch = Actor.findChar(target);
        if (ch == null) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        Sample.INSTANCE.play("sounds/teleport.mp3");
        this.affectChar(hero, ch);
        if (ch == hero) {
            hero.sprite.operate(ch.pos);
            hero.next();
        } else {
            hero.sprite.zap(ch.pos);
            hero.next();
        }
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
            if (ch == hero) {
                this.affectChar(hero, ally);
            } else if (ally == ch) {
                this.affectChar(hero, hero);
            }
        }
        this.onSpellCast(tome, hero);
    }

    private void affectChar(Hero hero, Char ch) {
        int totalHeal = 10 + 5 * hero.pointsInTalent(Talent.LAY_ON_HANDS);
        int totalBarrier = 0;
        if (ch == hero) {
            Barrier barrier = Buff.affect(ch, Barrier.class);
            totalBarrier = totalHeal;
            totalBarrier = Math.min(3 * totalHeal - barrier.shielding(), totalBarrier);
            totalBarrier = Math.max(0, totalBarrier);
            Buff.affect(ch, Barrier.class).incShield(totalBarrier);
            ch.sprite.showStatusWithIcon(65280, Integer.toString(totalBarrier), FloatingText.SHIELDING, new Object[0]);
        } else if (ch.HT - ch.HP < totalHeal) {
            totalBarrier = totalHeal - (ch.HT - ch.HP);
            if (ch.HP != ch.HT) {
                ch.HP = ch.HT;
                ch.sprite.showStatusWithIcon(65280, Integer.toString(totalHeal - totalBarrier), FloatingText.HEALING, new Object[0]);
            }
            if (totalBarrier > 0) {
                Barrier barrier = Buff.affect(ch, Barrier.class);
                totalBarrier = Math.min(3 * totalHeal - barrier.shielding(), totalBarrier);
                if ((totalBarrier = Math.max(0, totalBarrier)) > 0) {
                    barrier.incShield(totalBarrier);
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(totalBarrier), FloatingText.SHIELDING, new Object[0]);
                }
            }
        } else {
            ch.HP += totalHeal;
            ch.sprite.showStatusWithIcon(65280, Integer.toString(totalHeal), FloatingText.HEALING, new Object[0]);
        }
    }
}

