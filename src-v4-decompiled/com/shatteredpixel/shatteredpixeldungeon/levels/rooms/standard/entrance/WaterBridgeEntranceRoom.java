/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.WaterBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class WaterBridgeEntranceRoom
extends WaterBridgeRoom {
    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
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
        super.paint(level);
        while (this.spaceRect.inside(level.cellToPoint(entrance = level.pointToCell(this.random(2)))) || level.findMob(entrance) != null) {
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            Painter.set(level, entrance + i, 1);
        }
        Painter.set(level, entrance, 7);
        if (Dungeon.depth == 1) {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.SURFACE));
        } else {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        }
        EntranceRoom.placeEarlyGuidePages(level, this);
    }
}

