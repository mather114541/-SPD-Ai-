/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Roots
extends FlavourBuff {
    public static final float DURATION = 5.0f;

    public Roots() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (!target.flying && super.attachTo(target)) {
            target.rooted = true;
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        this.target.rooted = false;
        super.detach();
    }

    @Override
    public int icon() {
        return 11;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (5.0f - this.visualcooldown()) / 5.0f);
    }
}

