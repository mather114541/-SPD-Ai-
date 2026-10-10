/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineIntervention;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class AscendedForm
extends ArmorAbility {
    public AscendedForm() {
        this.baseChargeUse = 50.0f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        Buff.affect(hero, AscendBuff.class).reset();
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        new Flare(6, 48.0f).color(0xFFFF00, true).show(hero.sprite, 2.0f);
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(1.0f);
    }

    @Override
    public int icon() {
        return 31;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.DIVINE_INTERVENTION, Talent.JUDGEMENT, Talent.FLASH, Talent.HEROIC_ENERGY};
    }

    public static class AscendBuff
    extends ShieldBuff {
        public static float DURATION = 10.0f;
        public int left;
        public int spellCasts;
        public int flashCasts;
        public boolean divineInverventionCast;
        public static final String LEFT = "left";
        public static final String SPELL_CASTS = "spell_casts";
        public static final String FLASH_CASTS = "flash_casts";
        public static final String DIVINE_INTERVENTION_CAST = "divine_intervention_cast";

        public AscendBuff() {
            this.type = Buff.buffType.POSITIVE;
            this.detachesAtZero = false;
            this.shieldUsePriority = 1;
            this.left = 10;
            this.spellCasts = 0;
            this.flashCasts = 0;
            this.divineInverventionCast = false;
        }

        @Override
        public int icon() {
            return 79;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - (float)this.left) / DURATION);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.left);
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.GLOWING);
            } else {
                this.target.sprite.remove(CharSprite.State.GLOWING);
            }
        }

        public void reset() {
            this.setShield(30);
            this.left = (int)DURATION;
        }

        public void extend(int amt) {
            this.left += amt;
        }

        @Override
        public boolean act() {
            --this.left;
            if (this.left <= 0) {
                this.detach();
                for (Char ch : Actor.chars()) {
                    if (ch.buff(DivineIntervention.DivineShield.class) == null) continue;
                    ch.buff(DivineIntervention.DivineShield.class).detach();
                }
                return true;
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.shielding(), this.left);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LEFT, this.left);
            bundle.put(SPELL_CASTS, this.spellCasts);
            bundle.put(FLASH_CASTS, this.flashCasts);
            bundle.put(DIVINE_INTERVENTION_CAST, this.divineInverventionCast);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.left = bundle.getInt(LEFT);
            this.spellCasts = bundle.getInt(SPELL_CASTS);
            this.flashCasts = bundle.getInt(FLASH_CASTS);
            this.divineInverventionCast = bundle.getBoolean(DIVINE_INTERVENTION_CAST);
        }
    }
}

