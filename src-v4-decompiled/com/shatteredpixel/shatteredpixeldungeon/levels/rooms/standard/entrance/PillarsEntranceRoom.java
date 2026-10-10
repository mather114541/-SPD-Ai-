/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PillarsRoom;
import com.watabou.utils.PathFinder;

public class PillarsEntranceRoom
extends PillarsRoom {
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
        boolean valid;
        int entrance;
        super.paint(level);
        do {
            entrance = level.pointToCell(this.random(2));
            valid = true;
            for (int i : PathFinder.NEIGHBOURS4) {
                if (i == -level.width() || level.map[entrance + i] != 4) continue;
                valid = false;
            }
        } while (level.findMob(entrance) != null || level.map[entrance] == 4 || !valid);
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

