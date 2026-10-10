/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class SegmentedRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{9.0f, 3.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.set(level, door, 1);
        }
        this.createWalls(level, new Rect(this.left + 1, this.top + 1, this.right - 1, this.bottom - 1));
    }

    private void createWalls(Level level, Rect area) {
        if (Math.max(area.width() + 1, area.height() + 1) < 5 || Math.min(area.width() + 1, area.height() + 1) < 3) {
            return;
        }
        int tries = 10;
        if (area.width() > area.height() || area.width() == area.height() && Random.Int(2) == 0) {
            do {
                int splitX;
                if (level.map[(splitX = Random.IntRange(area.left + 2, area.right - 2)) + level.width() * (area.top - 1)] != 4 || level.map[splitX + level.width() * (area.bottom + 1)] != 4) continue;
                tries = 0;
                Painter.drawLine(level, new Point(splitX, area.top), new Point(splitX, area.bottom), 4);
                int spaceTop = Random.IntRange(area.top, area.bottom - 1);
                Painter.set(level, splitX, spaceTop, 1);
                Painter.set(level, splitX, spaceTop + 1, 1);
                this.createWalls(level, new Rect(area.left, area.top, splitX - 1, area.bottom));
                this.createWalls(level, new Rect(splitX + 1, area.top, area.right, area.bottom));
            } while (--tries > 0);
        } else {
            do {
                int splitY = Random.IntRange(area.top + 2, area.bottom - 2);
                if (level.map[area.left - 1 + level.width() * splitY] != 4 || level.map[area.right + 1 + level.width() * splitY] != 4) continue;
                tries = 0;
                Painter.drawLine(level, new Point(area.left, splitY), new Point(area.right, splitY), 4);
                int spaceLeft = Random.IntRange(area.left, area.right - 1);
                Painter.set(level, spaceLeft, splitY, 1);
                Painter.set(level, spaceLeft + 1, splitY, 1);
                this.createWalls(level, new Rect(area.left, area.top, area.right, splitY - 1));
                this.createWalls(level, new Rect(area.left, splitY + 1, area.right, area.bottom));
            } while (--tries > 0);
        }
    }
}

