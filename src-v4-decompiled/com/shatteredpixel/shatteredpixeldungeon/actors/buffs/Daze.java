/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Daze
extends FlavourBuff {
    public static final float DURATION = 5.0f;

    public Daze() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 70;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
    }
}

