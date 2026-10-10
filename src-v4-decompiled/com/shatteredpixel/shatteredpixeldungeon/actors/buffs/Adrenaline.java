/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.noosa.Image;

public class Adrenaline
extends FlavourBuff {
    public static final float DURATION = 10.0f;

    public Adrenaline() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 50;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }
}

