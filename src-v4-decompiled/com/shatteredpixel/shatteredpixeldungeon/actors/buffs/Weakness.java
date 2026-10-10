/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Weakness
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public Weakness() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 14;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }
}

