/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

public class LostInventory
extends Buff {
    public LostInventory() {
        this.type = Buff.buffType.NEGATIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (target instanceof Hero && ((Hero)target).belongings != null) {
                ((Hero)target).belongings.lostInventory(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        if (this.target instanceof Hero && ((Hero)this.target).belongings != null) {
            ((Hero)this.target).belongings.lostInventory(false);
        }
    }

    @Override
    public int icon() {
        return 53;
    }
}

