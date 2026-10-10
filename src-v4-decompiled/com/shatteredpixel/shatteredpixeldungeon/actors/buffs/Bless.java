/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Bless
extends FlavourBuff {
    public static final float DURATION = 30.0f;

    public Bless() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 37;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (30.0f - this.visualcooldown()) / 30.0f);
    }
}

