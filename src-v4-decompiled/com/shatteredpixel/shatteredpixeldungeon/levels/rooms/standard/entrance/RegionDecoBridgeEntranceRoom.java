/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoBridgeRoom;
import com.watabou.utils.PathFinder;

public class RegionDecoBridgeEntranceRoom
extends RegionDecoBridgeRoom {
    @Override
    public int minWidth() {
        return Math.max(8, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(8, super.minHeight());
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        int entrance;
        boolean valid;
        super.paint(level);
        do {
            valid = true;
            entrance = level.pointToCell(this.random(2));
            if (this.spaceRect.inside(level.cellToPoint(entrance))) {
                valid = false;
                continue;
            }
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[entrance + i] != 34) continue;
                valid = false;
            }
        } while (!valid);
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

