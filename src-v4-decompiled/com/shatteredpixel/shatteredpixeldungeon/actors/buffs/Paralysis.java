/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Paralysis
extends FlavourBuff {
    public static final float DURATION = 10.0f;

    public Paralysis() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            ++target.paralysed;
            return true;
        }
        return false;
    }

    public void processDamage(int damage) {
        if (this.target == null) {
            return;
        }
        ParalysisResist resist = this.target.buff(ParalysisResist.class);
        if (resist == null) {
            resist = Buff.affect(this.target, ParalysisResist.class);
        }
        resist.damage += damage;
        if (Random.NormalIntRange(0, resist.damage) >= Random.NormalIntRange(0, this.target.HP)) {
            if (Dungeon.level.heroFOV[this.target.pos]) {
                this.target.sprite.showStatus(0xFFFF00, Messages.get(this, "out", new Object[0]), new Object[0]);
            }
            this.detach();
        }
    }

    @Override
    public void detach() {
        super.detach();
        if (this.target.paralysed > 0) {
            --this.target.paralysed;
        }
    }

    @Override
    public int icon() {
        return 4;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.PARALYSED);
        } else if (this.target.paralysed <= 1) {
            this.target.sprite.remove(CharSprite.State.PARALYSED);
        }
    }

    public static class ParalysisResist
    extends Buff {
        private int damage;
        private static final String DAMAGE = "damage";

        public ParalysisResist() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public boolean act() {
            if (this.target.buff(Paralysis.class) == null) {
                this.damage = (int)((double)this.damage - Math.ceil((float)this.damage / 10.0f));
                if (this.damage <= 0) {
                    this.detach();
                }
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DAMAGE, this.damage);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.damage = bundle.getInt(DAMAGE);
        }
    }
}

