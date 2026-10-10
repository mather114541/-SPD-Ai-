/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.utils.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class ShieldBuff
extends Buff {
    private int shielding;
    protected int shieldUsePriority = 0;
    protected boolean detachesAtZero = true;
    private static final String SHIELDING = "shielding";

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.needsShieldUpdate = true;
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        this.target.needsShieldUpdate = true;
        super.detach();
    }

    public int shielding() {
        return this.shielding;
    }

    public void setShield(int shield) {
        if (this.shielding <= shield) {
            this.shielding = shield;
        }
        if (this.target != null) {
            this.target.needsShieldUpdate = true;
        }
    }

    public void incShield() {
        this.incShield(1);
    }

    public void incShield(int amt) {
        this.shielding += amt;
        if (this.target != null) {
            this.target.needsShieldUpdate = true;
        }
    }

    public void delay(float value) {
        this.spend(value);
    }

    public void decShield() {
        this.decShield(1);
    }

    public void decShield(int amt) {
        this.shielding -= amt;
        if (this.target != null) {
            this.target.needsShieldUpdate = true;
        }
    }

    public int absorbDamage(int dmg) {
        if (this.shielding >= dmg) {
            this.shielding -= dmg;
            dmg = 0;
        } else {
            dmg -= this.shielding;
            this.shielding = 0;
        }
        if (this.shielding <= 0 && this.detachesAtZero) {
            this.detach();
        }
        if (this.target != null) {
            this.target.needsShieldUpdate = true;
        }
        return dmg;
    }

    public static int processDamage(Char target, int damage, Object src) {
        if (src instanceof Hunger) {
            return damage;
        }
        ArrayList<ShieldBuff> buffs = new ArrayList<ShieldBuff>(target.buffs(ShieldBuff.class));
        if (!buffs.isEmpty()) {
            Collections.sort(buffs, new Comparator<ShieldBuff>(){

                @Override
                public int compare(ShieldBuff a, ShieldBuff b) {
                    return b.shieldUsePriority - a.shieldUsePriority;
                }
            });
            for (ShieldBuff buff : buffs) {
                if (buff.shielding() <= 0) continue;
                damage = buff.absorbDamage(damage);
                if (buff.shielding() <= 0 && target instanceof Hero && ((Hero)target).hasTalent(Talent.PROVOKED_ANGER)) {
                    Buff.affect(target, Talent.ProvokedAngerTracker.class, 5.0f);
                }
                if (damage != 0) continue;
                break;
            }
        }
        return damage;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SHIELDING, this.shielding);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.shielding = bundle.getInt(SHIELDING);
    }
}

