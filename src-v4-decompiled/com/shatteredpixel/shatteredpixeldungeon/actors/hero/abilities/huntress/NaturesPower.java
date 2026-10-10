/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.watabou.noosa.audio.Sample;

public class NaturesPower
extends ArmorAbility {
    public NaturesPower() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        Buff.prolong(hero, naturesPowerTracker.class, 8.0f);
        hero.buff(naturesPowerTracker.class).extensionsLeft = 2;
        hero.sprite.operate(hero.pos);
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        hero.sprite.emitter().burst(LeafParticle.GENERAL, 10);
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(1.0f);
    }

    @Override
    public int icon() {
        return 26;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.GROWING_POWER, Talent.NATURES_WRATH, Talent.WILD_MOMENTUM, Talent.HEROIC_ENERGY};
    }

    public static class naturesPowerTracker
    extends FlavourBuff {
        public static final float DURATION = 8.0f;
        public int extensionsLeft;

        public naturesPowerTracker() {
            this.type = Buff.buffType.POSITIVE;
            this.extensionsLeft = 2;
        }

        public void extend(int turns) {
            if (this.extensionsLeft > 0 && turns > 0) {
                this.spend(turns);
                --this.extensionsLeft;
            }
        }

        @Override
        public int icon() {
            return 58;
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (8.0f - this.visualcooldown()) / 8.0f);
        }
    }
}

