/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;

public class Drowsy
extends FlavourBuff {
    public static final float DURATION = 5.0f;

    public Drowsy() {
        this.type = Buff.buffType.NEUTRAL;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 29;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
    }

    @Override
    public boolean attachTo(Char target) {
        return !target.isImmune(Sleep.class) && super.attachTo(target);
    }

    @Override
    public boolean act() {
        Buff.affect(this.target, MagicalSleep.class);
        return super.act();
    }
}

