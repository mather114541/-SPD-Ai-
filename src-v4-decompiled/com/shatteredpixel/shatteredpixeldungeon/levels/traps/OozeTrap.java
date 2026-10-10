/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.utils.PathFinder;

public class OozeTrap
extends Trap {
    public OozeTrap() {
        this.color = 3;
        this.shape = 0;
    }

    @Override
    public void activate() {
        for (int i : PathFinder.NEIGHBOURS9) {
            if (Dungeon.level.solid[this.pos + i]) continue;
            Splash.at(this.pos + i, 0, 5);
            Char ch = Actor.findChar(this.pos + i);
            if (ch == null || ch.flying) continue;
            Buff.affect(ch, Ooze.class).set(20.0f);
            if (!(ch instanceof Mob)) continue;
            Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
        }
    }
}

