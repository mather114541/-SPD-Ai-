/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotHeart;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RotLasher;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class RotGardenRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 10;
    }

    @Override
    public int minHeight() {
        return 10;
    }

    @Override
    public void paint(Level level) {
        int i;
        int openCells;
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
        Painter.fill(level, this, 4);
        Painter.set(level, entrance, 10);
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        boolean[] passable = new boolean[level.length()];
        int entryPos = level.pointToCell(this.entrance());
        do {
            int i2;
            Painter.fill(level, this, 1, 15);
            for (int i22 = 0; i22 < 12; ++i22) {
                Painter.set(level, this.random(1), 4);
            }
            for (i2 = 0; i2 < 8; ++i2) {
                Painter.set(level, this.random(2), 4);
            }
            for (i2 = 0; i2 < 4; ++i2) {
                Painter.set(level, this.random(3), 4);
            }
            Painter.drawInside(level, this, entrance, 3, 15);
            for (i2 = 0; i2 < passable.length; ++i2) {
                passable[i2] = level.map[i2] != 4;
            }
            PathFinder.buildDistanceMap(entryPos, passable);
            candidates.clear();
            openCells = 0;
            for (Point point : this.getPoints()) {
                int i3 = level.pointToCell(point);
                if (PathFinder.distance[i3] != Integer.MAX_VALUE) {
                    ++openCells;
                    if (PathFinder.distance[i3] < 7) continue;
                    candidates.add(i3);
                    continue;
                }
                if (level.map[i3] != 15) continue;
                level.map[i3] = 4;
            }
            Random.shuffle(candidates);
            int closestPos = 7;
            while (candidates.size() > 5) {
                for (Integer i4 : candidates.toArray(new Integer[0])) {
                    if (candidates.size() <= 5 || PathFinder.distance[i4] != closestPos) continue;
                    candidates.remove(i4);
                }
                ++closestPos;
            }
        } while (candidates.isEmpty() || openCells < 35);
        int heartPos = (Integer)Random.element(candidates);
        RotGardenRoom.placePlant(level, heartPos, new RotHeart());
        boolean[] blArray = Arrays.copyOf(passable, passable.length);
        int maxLashers = 6;
        for (i = 1; i <= maxLashers; ++i) {
            int pos;
            int tries = 50;
            do {
                pos = level.pointToCell(this.random());
            } while (--tries > 0 && !RotGardenRoom.validPlantPos(passable, blArray, level, pos, heartPos, entryPos));
            if (tries <= 0) break;
            RotGardenRoom.placePlant(level, pos, new RotLasher());
        }
        for (i = 0; i < PathFinder.CIRCLE8.length; i += 2) {
            if (level.map[heartPos + PathFinder.CIRCLE8[i]] == 4) continue;
            Painter.set(level, heartPos + PathFinder.CIRCLE8[i + 1], 15);
        }
    }

    private static boolean validPlantPos(boolean[] passable, boolean[] newPassable, Level level, int pos, int heartPos, int entryPos) {
        if (level.map[pos] != 15) {
            return false;
        }
        for (int i : PathFinder.NEIGHBOURS9) {
            if (level.findMob(pos + i) == null) continue;
            return false;
        }
        newPassable[pos] = false;
        if (level.distance(pos, heartPos) > 2) {
            for (int i : PathFinder.NEIGHBOURS4) {
                newPassable[pos + i] = false;
            }
        } else {
            for (int i : PathFinder.NEIGHBOURS8) {
                newPassable[pos + i] = false;
            }
        }
        PathFinder.buildDistanceMap(heartPos, newPassable);
        if (PathFinder.distance[entryPos] == Integer.MAX_VALUE) {
            System.arraycopy(passable, 0, newPassable, 0, passable.length);
            return false;
        }
        System.arraycopy(newPassable, 0, passable, 0, passable.length);
        return true;
    }

    private static void placePlant(Level level, int pos, Mob plant) {
        plant.pos = pos;
        level.mobs.add(plant);
        Painter.set(level, pos, 2);
    }
}

