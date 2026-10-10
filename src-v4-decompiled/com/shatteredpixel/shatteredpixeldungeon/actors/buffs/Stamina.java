/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.noosa.Image;

public class Stamina
extends FlavourBuff {
    public static final float DURATION = 100.0f;

    public Stamina() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 41;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.5f, 1.0f, 0.5f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (100.0f - this.visualcooldown()) / 100.0f);
    }
}

