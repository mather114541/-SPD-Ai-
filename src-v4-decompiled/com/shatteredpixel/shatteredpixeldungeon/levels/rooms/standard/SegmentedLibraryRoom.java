/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class SegmentedLibraryRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 3.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 27);
        Painter.fill(level, this, 2, 14);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 2, 14);
        }
        this.createWalls(level, new Rect(this.left + 2, this.top + 2, this.right - 2, this.bottom - 2));
        for (int x = this.left + 1; x < this.right - 2; ++x) {
            for (int y = this.top + 1; y < this.bottom - 2; ++y) {
                int cell = x + y * level.width();
                if (level.map[cell] != 14 || level.map[cell - 1] != 27 && level.map[cell - 1 + level.width()] != 27) continue;
                int width = 1;
                int height = 1;
                while (level.map[cell + width] == 14 && level.map[cell + width + level.width()] == 14) {
                    ++width;
                }
                while (level.map[cell + height * level.width()] == 14 && level.map[cell + height * level.width() + 1] == 14) {
                    ++height;
                }
                if (width <= 1 || height <= 1) continue;
                Carpet carpet = new Carpet();
                carpet.setRect(x, y, width, height);
                y += height - 1;
            }
        }
    }

    private void createWalls(Level level, Rect area) {
        if (Math.max(area.width() + 1, area.height() + 1) >= 4 && Math.min(area.width() + 1, area.height() + 1) >= 3) {
            int tries = 10;
            if (area.width() > area.height() || area.width() == area.height() && Random.Int(2) == 0) {
                do {
                    int splitX;
                    if (level.map[(splitX = Random.IntRange(area.left + 2, area.right - 2)) + level.width() * (area.top - 1)] != 27 || level.map[splitX + level.width() * (area.bottom + 1)] != 27) continue;
                    Painter.drawLine(level, new Point(splitX, area.top), new Point(splitX, area.bottom), 27);
                    int spaceTop = Random.IntRange(area.top, area.bottom - 1);
                    Painter.set(level, splitX, spaceTop, 14);
                    this.createWalls(level, new Rect(area.left, area.top, splitX - 1, area.bottom));
                    this.createWalls(level, new Rect(splitX + 1, area.top, area.right, area.bottom));
                    return;
                } while (--tries > 0);
            } else {
                do {
                    int splitY = Random.IntRange(area.top + 2, area.bottom - 2);
                    if (level.map[area.left - 1 + level.width() * splitY] != 27 || level.map[area.right + 1 + level.width() * splitY] != 27) continue;
                    Painter.drawLine(level, new Point(area.left, splitY), new Point(area.right, splitY), 27);
                    int spaceLeft = Random.IntRange(area.left, area.right - 1);
                    Painter.set(level, spaceLeft, splitY, 14);
                    this.createWalls(level, new Rect(area.left, area.top, area.right, splitY - 1));
                    this.createWalls(level, new Rect(area.left, splitY + 1, area.right, area.bottom));
                    return;
                } while (--tries > 0);
            }
        }
        if (area.width() + 1 >= 2 && area.height() + 1 >= 2) {
            Carpet carpet = new Carpet();
            carpet.setRect(area.left, area.top, area.width() + 1, area.height() + 1);
            level.customTiles.add(carpet);
        }
    }
}

