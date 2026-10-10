/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;

public class DivineIntervention
extends ClericSpell {
    public static DivineIntervention INSTANCE = new DivineIntervention();

    @Override
    public int icon() {
        return 58;
    }

    @Override
    public float chargeUse(Hero hero) {
        return 5.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.DIVINE_INTERVENTION) && hero.buff(AscendedForm.AscendBuff.class) != null && !hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast;
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        Sample.INSTANCE.play("sounds/chargeup.mp3", 1.0f, 1.2f);
        hero.sprite.operate(hero.pos);
        for (Char ch : Actor.chars()) {
            if (ch.alignment != Char.Alignment.ALLY || ch == hero) continue;
            Buff.affect(ch, DivineShield.class).setShield(100 + 50 * hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
            new Flare(6, 32.0f).color(0xFFFF00, true).show(ch.sprite, 2.0f);
        }
        hero.spendAndNext(1.0f);
        this.onSpellCast(tome, hero);
        hero.buff(AscendedForm.AscendBuff.class).setShield(100 + 50 * hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
        new Flare(6, 32.0f).color(0xFFFF00, true).show(hero.sprite, 2.0f);
        hero.buff(AscendedForm.AscendBuff.class).divineInverventionCast = true;
        hero.buff(AscendedForm.AscendBuff.class).extend(2 + hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
    }

    @Override
    public String desc() {
        int shield = 100 + 50 * Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
        int leftBonus = 2 + Dungeon.hero.pointsInTalent(Talent.DIVINE_INTERVENTION);
        return Messages.get(this, "desc", shield, leftBonus) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    public static class DivineShield
    extends ShieldBuff {
        public DivineShield() {
            this.shieldUsePriority = 1;
        }

        @Override
        public boolean act() {
            if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public int shielding() {
            if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null) {
                return 0;
            }
            return super.shielding();
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.SHIELDED);
            } else {
                this.target.sprite.remove(CharSprite.State.SHIELDED);
            }
        }
    }
}

