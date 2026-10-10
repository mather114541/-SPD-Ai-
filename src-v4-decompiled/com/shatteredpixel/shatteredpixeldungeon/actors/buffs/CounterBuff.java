/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.utils.Bundle;

public class CounterBuff
extends Buff {
    private float count = 0.0f;
    private static final String COUNT = "count";

    public void countUp(float inc) {
        this.count += inc;
    }

    public void countDown(float inc) {
        this.count -= inc;
    }

    public float count() {
        return this.count;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(COUNT, this.count);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.count = bundle.getFloat(COUNT);
    }
}

