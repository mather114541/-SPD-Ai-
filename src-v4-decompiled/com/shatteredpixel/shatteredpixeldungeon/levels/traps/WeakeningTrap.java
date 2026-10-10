/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Weakness;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;

public class WeakeningTrap
extends Trap {
    public WeakeningTrap() {
        this.color = 3;
        this.shape = 1;
    }

    @Override
    public void activate() {
        Char ch;
        if (Dungeon.level.heroFOV[this.pos]) {
            CellEmitter.get(this.pos).burst(ShadowParticle.UP, 5);
        }
        if ((ch = Actor.findChar(this.pos)) != null) {
            if (ch.properties().contains((Object)Char.Property.BOSS) || ch.properties().contains((Object)Char.Property.MINIBOSS)) {
                Buff.prolong(ch, Weakness.class, 10.0f);
            }
            Buff.prolong(ch, Weakness.class, 60.0f);
            if (ch instanceof Mob) {
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
        }
    }
}

