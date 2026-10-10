/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class HoldFast
extends Buff {
    public int pos;
    private static final String POS = "pos";

    public HoldFast() {
        this.type = Buff.buffType.POSITIVE;
        this.pos = -1;
    }

    @Override
    public boolean act() {
        if (this.pos != this.target.pos) {
            this.detach();
        } else {
            this.spend(1.0f);
        }
        return true;
    }

    public int armorBonus() {
        if (this.pos == this.target.pos && this.target instanceof Hero) {
            int max = 0;
            if (((Hero)this.target).heroClass == HeroClass.WARRIOR) {
                max = 6;
            } else {
                Armor armor = ((Hero)this.target).belongings.armor();
                if (armor != null) {
                    max = armor.tier + armor.buffedLvl();
                }
            }
            int block = Random.NormalIntRange(((Hero)this.target).pointsInTalent(Talent.HOLD_FAST), 2 * ((Hero)this.target).pointsInTalent(Talent.HOLD_FAST));
            return Math.min(block, max);
        }
        this.detach();
        return 0;
    }

    public static float buffDecayFactor(Char target) {
        HoldFast buff = target.buff(HoldFast.class);
        if (buff != null && target.pos == buff.pos && target instanceof Hero) {
            switch (((Hero)target).pointsInTalent(Talent.HOLD_FAST)) {
                case 1: {
                    return 0.5f;
                }
                case 2: {
                    return 0.25f;
                }
                case 3: {
                    return 0.0f;
                }
            }
        } else if (buff != null) {
            buff.detach();
        }
        return 1.0f;
    }

    @Override
    public int icon() {
        return 20;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.9f, 2.4f, 3.25f);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", Dungeon.hero.pointsInTalent(Talent.HOLD_FAST), 2 * Dungeon.hero.pointsInTalent(Talent.HOLD_FAST), 25 + 25 * Dungeon.hero.pointsInTalent(Talent.HOLD_FAST));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(POS, this.pos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.pos = bundle.getInt(POS);
    }
}

