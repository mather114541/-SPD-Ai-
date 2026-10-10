/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RuinsRoom;
import com.watabou.utils.PathFinder;

public class RuinsEntranceRoom
extends RuinsRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{2.0f, 1.0f, 0.0f};
    }

    @Override
    public void paint(Level level) {
        int entrance;
        boolean valid;
        super.paint(level);
        int tries = 30;
        do {
            entrance = level.pointToCell(this.random(2));
            if (tries-- > 0) {
                valid = level.map[entrance] != 4 && level.findMob(entrance) == null;
                continue;
            }
            valid = false;
            for (int i : PathFinder.NEIGHBOURS4) {
                if (level.map[entrance + i] == 4 || level.map[entrance + i] == 33) continue;
                valid = true;
            }
            boolean bl = valid = valid && level.findMob(entrance) == null;
        } while (!valid);
        Painter.set(level, entrance, 7);
        for (int i : PathFinder.NEIGHBOURS8) {
            Painter.set(level, entrance + i, 1);
        }
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

