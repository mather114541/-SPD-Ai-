/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.utils.Bundle;

public class Terror
extends FlavourBuff {
    public int object = 0;
    private static final String OBJECT = "object";
    public static final float DURATION = 20.0f;
    public boolean ignoreNextHit;

    public Terror() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.ignoreNextHit = false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, this.object);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.object = bundle.getInt(OBJECT);
    }

    @Override
    public int icon() {
        return 10;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }

    public void recover() {
        if (this.ignoreNextHit) {
            this.ignoreNextHit = false;
            return;
        }
        this.spend(-5.0f);
        if (this.cooldown() <= 0.0f) {
            this.detach();
        }
    }
}

