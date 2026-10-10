/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;

public class SewerBossEntranceRoom
extends EntranceRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public void paint(Level level) {
        int entrance;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, 1, 12);
        Painter.fill(level, this.left + 1, this.top + 2, this.width() - 2, 1, 29);
        while (level.findMob(entrance = level.pointToCell(this.random(3))) != null) {
        }
        Painter.set(level, entrance, 7);
        level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            if (door.y != this.top && door.y != this.top + 1) continue;
            Painter.drawInside(level, this, door, 1, 29);
        }
    }
}

