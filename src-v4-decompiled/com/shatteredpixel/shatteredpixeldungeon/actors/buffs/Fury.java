/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;

public class Fury
extends Buff {
    public static float LEVEL = 0.5f;

    public Fury() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public boolean act() {
        if ((float)this.target.HP > (float)this.target.HT * LEVEL) {
            this.detach();
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public int icon() {
        return 18;
    }
}

