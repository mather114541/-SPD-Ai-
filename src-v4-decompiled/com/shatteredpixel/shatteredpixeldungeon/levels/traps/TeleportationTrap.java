/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class TeleportationTrap
extends Trap {
    public TeleportationTrap() {
        this.color = 4;
        this.shape = 0;
    }

    @Override
    public void activate() {
        for (int i : PathFinder.NEIGHBOURS9) {
            Heap heap;
            Char ch = Actor.findChar(this.pos + i);
            if (ch != null && ScrollOfTeleportation.teleportChar(ch) && ch instanceof Mob && ((Mob)ch).state == ((Mob)ch).HUNTING) {
                ((Mob)ch).state = ((Mob)ch).WANDERING;
                Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
            }
            if ((heap = (Heap)Dungeon.level.heaps.get(this.pos + i)) == null || heap.type != Heap.Type.HEAP) continue;
            int cell = Dungeon.level.randomRespawnCell(null);
            Item item = heap.pickUp();
            if (cell == -1) continue;
            Dungeon.level.drop(item, cell);
            if (item instanceof Honeypot.ShatteredPot) {
                ((Honeypot.ShatteredPot)item).movePot(this.pos, cell);
            }
            Sample.INSTANCE.play("sounds/teleport.mp3");
            CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
        }
    }
}

