/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmBridgeRoom;
import com.watabou.utils.PathFinder;

public class ChasmBridgeEntranceRoom
extends ChasmBridgeRoom {
    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        int entrance;
        super.paint(level);
        while (this.spaceRect.inside(level.cellToPoint(entrance = level.pointToCell(this.random(2)))) || level.findMob(entrance) != null) {
        }
        for (int i : PathFinder.NEIGHBOURS8) {
            Painter.set(level, entrance + i, 1);
        }
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }
}

