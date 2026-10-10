/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RingRoom;

public class RingEntranceRoom
extends RingRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    protected int centerDecoTiles() {
        return 14;
    }

    @Override
    protected void placeCenterDetail(Level level, int pos) {
        Painter.set(level, pos, 37);
        level.transitions.add(new LevelTransition(level, pos, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

