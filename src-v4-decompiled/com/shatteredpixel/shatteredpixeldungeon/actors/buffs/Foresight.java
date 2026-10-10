/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;

public class Foresight
extends FlavourBuff {
    public static final float DURATION = 400.0f;
    public static final int DISTANCE = 8;

    public Foresight() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 32;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (target == Dungeon.hero) {
                Dungeon.level.mapped[target.pos] = false;
                Dungeon.hero.search(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (400.0f - this.visualcooldown()) / 400.0f);
    }
}

