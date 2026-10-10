/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SummoningTrap
extends Trap {
    private static final float DELAY = 2.0f;

    public SummoningTrap() {
        this.color = 4;
        this.shape = 1;
    }

    @Override
    public void activate() {
        int nMobs = 1;
        if (Random.Int(2) == 0) {
            ++nMobs;
            if (Random.Int(2) == 0) {
                ++nMobs;
            }
        }
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
            int p = this.pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && !Dungeon.level.avoid[p]) continue;
            candidates.add(p);
        }
        ArrayList<Integer> respawnPoints = new ArrayList<Integer>();
        while (nMobs > 0 && candidates.size() > 0) {
            int index = Random.index(candidates);
            respawnPoints.add((Integer)candidates.remove(index));
            --nMobs;
        }
        ArrayList<Mob> mobs = new ArrayList<Mob>();
        for (Integer point : respawnPoints) {
            Mob mob = Dungeon.level.createMob();
            while (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]) {
                mob = Dungeon.level.createMob();
            }
            if (mob == null) continue;
            if (mob.state != mob.PASSIVE) {
                mob.state = mob.WANDERING;
            }
            mob.pos = point;
            GameScene.add(mob, 2.0f);
            mobs.add(mob);
        }
        for (Mob mob : mobs) {
            Trap t = (Trap)Dungeon.level.traps.get(mob.pos);
            if (t != null && t.active) {
                if (t.disarmedByActivation) {
                    t.disarm();
                }
                t.reveal();
                Bestiary.setSeen(t.getClass());
                Bestiary.countEncounter(t.getClass());
                t.activate();
            }
            ScrollOfTeleportation.appear(mob, mob.pos);
            Dungeon.level.occupyCell(mob);
        }
    }
}

