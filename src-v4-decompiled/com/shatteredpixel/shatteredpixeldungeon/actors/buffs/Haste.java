/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.noosa.Image;

public class Haste
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public Haste() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 41;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.8f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }
}

