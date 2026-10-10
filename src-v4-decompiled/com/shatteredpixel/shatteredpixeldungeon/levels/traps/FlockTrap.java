/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import java.util.ArrayList;
import java.util.Iterator;

public class FlockTrap
extends Trap {
    public FlockTrap() {
        this.color = 6;
        this.shape = 1;
    }

    @Override
    public void activate() {
        PathFinder.buildDistanceMap(this.pos, BArray.not(Dungeon.level.solid, null), 2);
        ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] >= Integer.MAX_VALUE) continue;
            spawnPoints.add(i);
        }
        Iterator iterator = spawnPoints.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            if (Dungeon.level.insideMap(i) && Actor.findChar(i) == null && !Dungeon.level.pit[i]) {
                Sheep sheep = new Sheep();
                sheep.initialize(6.0f);
                sheep.pos = i;
                GameScene.add(sheep);
                CellEmitter.get(i).burst(Speck.factory(7), 4);
                Trap t = (Trap)Dungeon.level.traps.get(i);
                if (t != null && t.active) {
                    if (t.disarmedByActivation) {
                        t.disarm();
                    }
                    t.reveal();
                    Bestiary.setSeen(t.getClass());
                    Bestiary.countEncounter(t.getClass());
                    t.activate();
                }
                Dungeon.level.occupyCell(sheep);
                continue;
            }
            if (!(Actor.findChar(i) instanceof Mob)) continue;
            Buff.prolong(Actor.findChar(i), Trap.HazardAssistTracker.class, 50.0f);
        }
        Sample.INSTANCE.play("sounds/puff.mp3");
        Sample.INSTANCE.play("sounds/sheep.mp3");
    }
}

