/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.noosa.Image;

public class Recharging
extends FlavourBuff {
    public static final float DURATION = 30.0f;

    public Recharging() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 34;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 1.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (30.0f - this.visualcooldown()) / 30.0f);
    }

    public float remainder() {
        return Math.min(1.0f, this.cooldown());
    }
}

