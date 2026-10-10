/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Blindness
extends FlavourBuff {
    public static final float DURATION = 10.0f;

    public Blindness() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public void detach() {
        super.detach();
        Dungeon.observe();
    }

    @Override
    public int icon() {
        return 16;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }
}

