/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Hex
extends FlavourBuff {
    public static final float DURATION = 30.0f;

    public Hex() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 47;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (30.0f - this.visualcooldown()) / 30.0f);
    }
}

