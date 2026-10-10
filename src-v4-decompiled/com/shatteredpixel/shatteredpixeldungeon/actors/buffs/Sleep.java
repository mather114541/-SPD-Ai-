/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Sleep
extends FlavourBuff {
    public static final float SWS = 1.5f;

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.idle();
        }
    }
}

