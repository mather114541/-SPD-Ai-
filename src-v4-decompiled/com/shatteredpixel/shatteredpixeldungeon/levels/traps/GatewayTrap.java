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
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class GatewayTrap
extends Trap {
    private int telePos;
    private static final String TELE_POS = "tele_pos";

    public GatewayTrap() {
        this.color = 4;
        this.shape = 5;
        this.disarmedByActivation = false;
        this.avoidsHallways = true;
        this.telePos = -1;
    }

    @Override
    public void activate() {
        if (this.telePos == -1) {
            for (int i : PathFinder.NEIGHBOURS9) {
                Char ch = Actor.findChar(this.pos + i);
                if (ch != null && ScrollOfTeleportation.teleportChar(ch)) {
                    if (ch instanceof Mob && ((Mob)ch).state == ((Mob)ch).HUNTING) {
                        ((Mob)ch).state = ((Mob)ch).WANDERING;
                        Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
                    }
                    this.telePos = ch.pos;
                    break;
                }
                Heap heap = (Heap)Dungeon.level.heaps.get(this.pos + i);
                if (heap == null || heap.type != Heap.Type.HEAP) continue;
                int cell = Dungeon.level.randomRespawnCell(null);
                Item item = heap.pickUp();
                if (cell == -1) continue;
                Dungeon.level.drop(item, cell);
                this.telePos = cell;
                break;
            }
        }
        if (this.telePos != -1) {
            ArrayList<Integer> telePositions = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (!Dungeon.level.passable[this.telePos + i] || Actor.findChar(this.telePos + i) != null) continue;
                telePositions.add(this.telePos + i);
            }
            Random.shuffle(telePositions);
            if (Dungeon.level.passable[this.telePos] && Actor.findChar(this.telePos) == null) {
                telePositions.add(0, this.telePos);
            }
            ArrayList<Integer> largeCharPositions = new ArrayList<Integer>();
            Iterator iterator = telePositions.iterator();
            while (iterator.hasNext()) {
                int pos = (Integer)iterator.next();
                if (!Dungeon.level.openSpace[pos]) continue;
                largeCharPositions.add(pos);
            }
            for (Object i : (Iterator)PathFinder.NEIGHBOURS9) {
                Heap heap;
                Char ch = Actor.findChar(this.pos + i);
                if (ch != null && !Char.hasProp(ch, Char.Property.IMMOVABLE)) {
                    int newPos = -1;
                    if (Char.hasProp(ch, Char.Property.LARGE)) {
                        if (!largeCharPositions.isEmpty()) {
                            newPos = (Integer)largeCharPositions.get(0);
                        }
                    } else if (!telePositions.isEmpty()) {
                        newPos = (Integer)telePositions.get(0);
                    }
                    if (newPos != -1) {
                        telePositions.remove((Object)newPos);
                        largeCharPositions.remove((Object)newPos);
                        if (ScrollOfTeleportation.teleportToLocation(ch, newPos) && ch instanceof Mob && ((Mob)ch).state == ((Mob)ch).HUNTING) {
                            ((Mob)ch).state = ((Mob)ch).WANDERING;
                        }
                    }
                }
                if ((heap = (Heap)Dungeon.level.heaps.get(this.pos + i)) == null || heap.type != Heap.Type.HEAP) continue;
                Item item = heap.pickUp();
                Dungeon.level.drop(item, this.telePos);
                if (item instanceof Honeypot.ShatteredPot) {
                    ((Honeypot.ShatteredPot)item).movePot(this.pos, this.telePos);
                }
                Sample.INSTANCE.play("sounds/teleport.mp3");
                CellEmitter.get(this.pos).burst(Speck.factory(2), 4);
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TELE_POS, this.telePos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.telePos = bundle.getInt(TELE_POS);
    }
}

