/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;

public class Endure
extends ArmorAbility {
    public Endure() {
        this.baseChargeUse = 50.0f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (hero.buff(EndureTracker.class) != null) {
            hero.buff(EndureTracker.class).detach();
        }
        Buff.prolong(hero, EndureTracker.class, 12.0f);
        Combo combo = hero.buff(Combo.class);
        if (combo != null) {
            combo.addTime(3.0f);
        }
        hero.sprite.operate(hero.pos);
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(3.0f);
    }

    @Override
    public int icon() {
        return 18;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.SUSTAINED_RETRIBUTION, Talent.SHRUG_IT_OFF, Talent.EVEN_THE_ODDS, Talent.HEROIC_ENERGY};
    }

    public static class EndureTracker
    extends FlavourBuff {
        public boolean enduring;
        public int damageBonus;
        public int hitsLeft;
        public static String ENDURING = "enduring";
        public static String DAMAGE_BONUS = "damage_bonus";
        public static String HITS_LEFT = "hits_left";

        public EndureTracker() {
            this.type = Buff.buffType.POSITIVE;
            this.enduring = true;
            this.damageBonus = 0;
            this.hitsLeft = 0;
        }

        @Override
        public int icon() {
            return this.enduring ? 127 : 20;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.damageBonus, this.hitsLeft);
        }

        public float adjustDamageTaken(float damage) {
            if (this.enduring) {
                this.damageBonus = (int)((float)this.damageBonus + damage / 2.0f);
                float damageMulti = 0.5f;
                if (Dungeon.hero.hasTalent(Talent.SHRUG_IT_OFF)) {
                    damageMulti = (float)((double)damageMulti * Math.pow(0.8f, Dungeon.hero.pointsInTalent(Talent.SHRUG_IT_OFF)));
                }
                return damage * damageMulti;
            }
            return damage;
        }

        public void endEnduring() {
            if (!this.enduring) {
                return;
            }
            this.enduring = false;
            this.damageBonus = (int)((float)this.damageBonus * (1.0f + 0.15f * (float)Dungeon.hero.pointsInTalent(Talent.SUSTAINED_RETRIBUTION)));
            int nearby = 0;
            for (Char ch : Actor.chars()) {
                if (ch.alignment != Char.Alignment.ENEMY || Dungeon.level.distance(this.target.pos, ch.pos) > 2) continue;
                ++nearby;
            }
            this.damageBonus = (int)((float)this.damageBonus * (1.0f + (float)nearby * 0.05f * (float)Dungeon.hero.pointsInTalent(Talent.EVEN_THE_ODDS)));
            this.hitsLeft = 1 + Dungeon.hero.pointsInTalent(Talent.SUSTAINED_RETRIBUTION);
            this.damageBonus /= this.hitsLeft;
            if (this.damageBonus > 0) {
                this.target.sprite.centerEmitter().start(Speck.factory(5), 0.3f, 3);
                Sample.INSTANCE.play("sounds/challenge.mp3");
                SpellSprite.show(this.target, 3);
            } else {
                this.detach();
            }
        }

        public float damageFactor(float damage) {
            if (this.enduring) {
                return damage;
            }
            int bonusDamage = this.damageBonus;
            --this.hitsLeft;
            if (this.hitsLeft <= 0) {
                this.detach();
            }
            return damage + (float)bonusDamage;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ENDURING, this.enduring);
            bundle.put(DAMAGE_BONUS, this.damageBonus);
            bundle.put(HITS_LEFT, this.hitsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.enduring = bundle.getBoolean(ENDURING);
            this.damageBonus = bundle.getInt(DAMAGE_BONUS);
            this.hitsLeft = bundle.getInt(HITS_LEFT);
        }
    }
}

