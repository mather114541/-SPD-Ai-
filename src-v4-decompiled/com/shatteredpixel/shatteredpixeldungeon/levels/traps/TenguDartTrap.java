/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;

public class TenguDartTrap
extends PoisonDartTrap {
    public TenguDartTrap() {
        this.canBeHidden = true;
        this.canBeSearched = false;
    }

    @Override
    protected int poisonAmount() {
        if (Dungeon.isChallenged(256)) {
            return 15;
        }
        return 8;
    }

    @Override
    protected boolean canTarget(Char ch) {
        return !(ch instanceof Tengu);
    }
}

