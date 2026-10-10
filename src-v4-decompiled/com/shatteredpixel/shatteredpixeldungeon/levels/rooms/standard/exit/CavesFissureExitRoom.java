/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;

public class CavesFissureExitRoom
extends CavesFissureRoom {
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
        int exit;
        super.paint(level);
        while (level.map[exit = level.pointToCell(this.random(2))] == 0 || level.map[exit] == 14 || level.findMob(exit) != null) {
        }
        for (int i : PathFinder.NEIGHBOURS4) {
            if (level.map[exit + i] != 0) continue;
            Painter.set(level, exit + i, 1);
        }
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }
}

