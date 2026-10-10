/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoBridgeRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class RegionDecoBridgeExitRoom
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
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        int exit;
        boolean valid;
        super.paint(level);
        do {
            valid = true;
            exit = level.pointToCell(this.random(2));
            if (this.spaceRect.inside(level.cellToPoint(exit))) {
                valid = false;
                continue;
            }
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[exit + i] != 34) continue;
                valid = false;
            }
        } while (!valid);
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

