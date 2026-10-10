/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.utils.PathFinder;

public class ExplosiveTrap
extends Trap {
    public ExplosiveTrap() {
        this.color = 1;
        this.shape = 4;
    }

    @Override
    public void activate() {
        for (int i : PathFinder.NEIGHBOURS9) {
            if (!(Actor.findChar(this.pos + i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(this.pos + i), Trap.HazardAssistTracker.class, 50.0f);
        }
        new Bomb().explode(this.pos);
        if (this.reclaimed && !Dungeon.hero.isAlive()) {
            Badges.validateDeathFromFriendlyMagic();
        }
    }
}

