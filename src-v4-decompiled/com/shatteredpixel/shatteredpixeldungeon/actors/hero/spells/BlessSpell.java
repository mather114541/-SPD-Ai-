/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bless;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class BlessSpell
extends TargetedClericSpell {
    public static final BlessSpell INSTANCE = new BlessSpell();

    @Override
    public int icon() {
        return 48;
    }

    @Override
    public int targetingFlags() {
        return -1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.BLESS);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Char ch = Actor.findChar(target);
        if (ch == null || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        Sample.INSTANCE.play("sounds/teleport.mp3");
        this.affectChar(hero, ch);
        if (ch == hero) {
            hero.busy();
            hero.sprite.operate(ch.pos);
            hero.spend(1.0f);
        } else {
            hero.sprite.zap(ch.pos);
            hero.spendAndNext(1.0f);
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
        new Flare(6, 32.0f).color(0xFFFF00, true).show(ch.sprite, 2.0f);
        if (ch == hero) {
            Buff.prolong(ch, Bless.class, 2.0f + (float)(4 * hero.pointsInTalent(Talent.BLESS)));
            Buff.affect(ch, Barrier.class).setShield(5 + 5 * hero.pointsInTalent(Talent.BLESS));
            ch.sprite.showStatusWithIcon(65280, Integer.toString(5 + 5 * hero.pointsInTalent(Talent.BLESS)), FloatingText.SHIELDING, new Object[0]);
        } else {
            Buff.prolong(ch, Bless.class, 5.0f + (float)(5 * hero.pointsInTalent(Talent.BLESS)));
            int totalHeal = 5 + 5 * hero.pointsInTalent(Talent.BLESS);
            if (ch.HT - ch.HP < totalHeal) {
                int barrier = totalHeal - (ch.HT - ch.HP);
                barrier = Math.max(barrier, 0);
                if (ch.HP != ch.HT) {
                    ch.HP = ch.HT;
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(totalHeal - barrier), FloatingText.HEALING, new Object[0]);
                }
                if (barrier > 0) {
                    Buff.affect(ch, Barrier.class).setShield(barrier);
                    ch.sprite.showStatusWithIcon(65280, Integer.toString(barrier), FloatingText.SHIELDING, new Object[0]);
                }
            } else {
                ch.HP += totalHeal;
                ch.sprite.showStatusWithIcon(65280, Integer.toString(totalHeal), FloatingText.HEALING, new Object[0]);
            }
        }
        if (ch.alignment != Char.Alignment.ALLY && hero.subClass == HeroSubClass.PRIEST) {
            Buff.affect(ch, GuidingLight.Illuminated.class);
        }
    }

    @Override
    public String desc() {
        int talentLvl = Dungeon.hero.pointsInTalent(Talent.BLESS);
        return Messages.get(this, "desc", 2 + 4 * talentLvl, 5 + 5 * talentLvl, 5 + 5 * talentLvl, 5 + 5 * talentLvl) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }
}

