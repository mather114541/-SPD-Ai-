/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PillarsRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class PillarsExitRoom
extends PillarsRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        boolean valid;
        int exit;
        super.paint(level);
        do {
            exit = level.pointToCell(this.random(2));
            valid = true;
            for (int i : PathFinder.NEIGHBOURS4) {
                if (i == -level.width() || level.map[exit + i] != 4) continue;
                valid = false;
            }
        } while (level.findMob(exit) != null || level.map[exit] == 4 || !valid);
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

