/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoPatchRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class RegionDecoPatchEntranceRoom
extends RegionDecoPatchRoom {
    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        if (Dungeon.depth <= 2) {
            return false;
        }
        return super.canMerge(l, other, p, mergeTerrain);
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        if (Dungeon.depth == 1) {
            return false;
        }
        return super.canPlaceTrap(p);
    }

    @Override
    public void paint(Level level) {
        int entrance;
        boolean valid;
        super.paint(level);
        int tries = 30;
        do {
            entrance = level.pointToCell(this.random(2));
            if (tries-- > 0) {
                valid = level.map[entrance] != 33 && level.findMob(entrance) == null;
                continue;
            }
            valid = false;
            for (int i : PathFinder.NEIGHBOURS4) {
                if (level.map[entrance + i] == 33) continue;
                valid = true;
            }
            boolean bl = valid = valid && level.findMob(entrance) == null;
        } while (!valid);
        Painter.set(level, entrance, 7);
        for (int i : PathFinder.NEIGHBOURS8) {
            Painter.set(level, entrance + i, 1);
        }
        if (Dungeon.depth == 1) {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.SURFACE));
        } else {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        }
        EntranceRoom.placeEarlyGuidePages(level, this);
    }
}

