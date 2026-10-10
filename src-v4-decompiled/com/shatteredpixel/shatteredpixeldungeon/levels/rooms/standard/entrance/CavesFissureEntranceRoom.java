/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.watabou.utils.PathFinder;

public class CavesFissureEntranceRoom
extends CavesFissureRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        int entrance;
        super.paint(level);
        while (level.map[entrance = level.pointToCell(this.random(2))] == 0 || level.map[entrance] == 14 || level.findMob(entrance) != null) {
        }
        for (int i : PathFinder.NEIGHBOURS4) {
            if (level.map[entrance + i] != 0) continue;
            Painter.set(level, entrance + i, 1);
        }
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

