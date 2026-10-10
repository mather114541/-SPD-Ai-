/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CellBlockRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class CellBlockExitRoom
extends CellBlockRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Point p;
        super.paint(level);
        while (true) {
            if (level.map[level.pointToCell(p = this.random(3))] != 14) {
                continue;
            }
            boolean valid = true;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[level.pointToCell(p) + i] != 5) continue;
                valid = false;
            }
            if (valid) break;
        }
        int entrance = level.pointToCell(p);
        Painter.set(level, entrance, 8);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

