/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleWallRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CircleWallEntranceRoom
extends CircleWallRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 11);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 11);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        super.paint(level);
        Point p = this.center();
        int cell = level.pointToCell(p);
        for (int i : PathFinder.NEIGHBOURS8) {
            if (level.map[cell + 2 * i] != 4) continue;
            Painter.set(level, cell + i, 1);
        }
        Painter.set(level, p, 7);
        level.transitions.add(new LevelTransition(level, level.pointToCell(p), LevelTransition.Type.REGULAR_ENTRANCE));
        int xDir = 0;
        int yDir = 0;
        if (Random.Int(2) == 0) {
            xDir = Random.Int(2) == 0 ? 1 : -1;
        } else {
            yDir = Random.Int(2) == 0 ? 1 : -1;
        }
        p.x += 2 * xDir;
        p.y += 2 * yDir;
        while (level.map[level.pointToCell(p)] == 4) {
            Painter.set(level, p, 1);
            p.x += xDir;
            p.y += yDir;
        }
    }
}

