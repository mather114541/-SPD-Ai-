/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;

public class Amok
extends FlavourBuff {
    public Amok() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public int icon() {
        return 9;
    }

    @Override
    public void detach() {
        if (this.target.isAlive() && this.target.alignment == Char.Alignment.ENEMY) {
            for (Mob m : Dungeon.level.mobs) {
                if (m.alignment == Char.Alignment.ENEMY && m.isTargeting(this.target)) {
                    m.aggro(null);
                }
                if (!(this.target instanceof Mob) || !((Mob)this.target).isTargeting(m)) continue;
                ((Mob)this.target).aggro(null);
            }
        }
        super.detach();
    }
}

