/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class DistortionTrap
extends Trap {
    private static final float DELAY = 2.0f;

    public DistortionTrap() {
        this.color = 4;
        this.shape = 6;
    }

    @Override
    public void activate() {
        int nMobs = 3;
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
        int summoned = 0;
        block12: for (Integer point : respawnPoints) {
            Mob mob;
            block0 : switch (++summoned) {
                case 1: {
                    if (Dungeon.depth != 5 && Random.Int(100) == 0) {
                        mob = new RatKing();
                        break;
                    }
                }
                default: {
                    int floor;
                    while (Dungeon.bossLevel(floor = Random.Int(25))) {
                    }
                    mob = Reflection.newInstance(MobSpawner.getMobRotation(floor).get(0));
                    break;
                }
                case 2: {
                    switch (Random.Int(4)) {
                        default: {
                            Wraith.spawnAt(point);
                            continue block12;
                        }
                        case 1: {
                            mob = Piranha.random();
                            break block0;
                        }
                        case 2: {
                            mob = Mimic.spawnAt((int)point, false, new Item[0]);
                            ((Mimic)mob).stopHiding();
                            mob.alignment = Char.Alignment.ENEMY;
                            break block0;
                        }
                        case 3: 
                    }
                    mob = Statue.random(false);
                    break;
                }
                case 4: {
                    mob = Reflection.newInstance(Random.element(MobSpawner.RARE_ALTS.values()));
                }
            }
            if (Char.hasProp(mob, Char.Property.LARGE) && !Dungeon.level.openSpace[point]) continue;
            mob.maxLvl = 29;
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

