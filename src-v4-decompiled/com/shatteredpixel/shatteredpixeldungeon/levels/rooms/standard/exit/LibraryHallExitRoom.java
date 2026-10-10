/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.LibraryHallRoom;
import com.watabou.utils.Point;

public class LibraryHallExitRoom
extends LibraryHallRoom {
    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Point p;
        super.paint(level);
        while (level.map[level.pointToCell(p = this.random(2))] != 33) {
        }
        int exit = level.pointToCell(p);
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

