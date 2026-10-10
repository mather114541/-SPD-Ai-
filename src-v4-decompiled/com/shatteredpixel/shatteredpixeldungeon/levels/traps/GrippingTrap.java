/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;

public class GrippingTrap
extends Trap {
    public GrippingTrap() {
        this.color = 7;
        this.shape = 0;
        this.disarmedByActivation = false;
        this.avoidsHallways = true;
    }

    @Override
    public void activate() {
        Char c = Actor.findChar(this.pos);
        if (c != null && !c.flying) {
            if (c instanceof Mob) {
                Buff.prolong(c, Trap.HazardAssistTracker.class, 50.0f);
            }
            int damage = Math.max(0, 2 + this.scalingDepth() / 2 - c.drRoll() / 2);
            Buff.affect(c, Bleeding.class).set(damage);
            Buff.prolong(c, Cripple.class, 10.0f);
            Wound.hit(c);
        } else {
            Wound.hit(this.pos);
        }
    }
}

