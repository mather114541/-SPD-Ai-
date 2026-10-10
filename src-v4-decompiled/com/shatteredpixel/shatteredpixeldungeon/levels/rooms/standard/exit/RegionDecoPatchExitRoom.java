/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoPatchRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class RegionDecoPatchExitRoom
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
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        int exit;
        boolean valid;
        super.paint(level);
        int tries = 30;
        do {
            exit = level.pointToCell(this.random(2));
            if (tries-- > 0) {
                valid = level.map[exit] != 33 && level.findMob(exit) == null;
                continue;
            }
            valid = false;
            for (int i : PathFinder.NEIGHBOURS4) {
                if (level.map[exit + i] == 33) continue;
                valid = true;
            }
            boolean bl = valid = valid && level.findMob(exit) == null;
        } while (!valid);
        Painter.set(level, exit, 8);
        for (int i : PathFinder.NEIGHBOURS8) {
            Painter.set(level, exit + i, 1);
        }
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

