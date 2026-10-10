/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.LibraryHallRoom;
import com.watabou.utils.Point;

public class LibraryHallEntranceRoom
extends LibraryHallRoom {
    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Point p;
        super.paint(level);
        while (level.map[level.pointToCell(p = this.random(2))] != 33) {
        }
        int entrance = level.pointToCell(p);
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

