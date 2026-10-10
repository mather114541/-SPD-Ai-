/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class CellBlockRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 3.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        int openRooms;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 3, 4);
        EmptyRoom internal = new EmptyRoom();
        internal.set(this.left + 3, this.top + 3, this.right - 3, this.bottom - 3);
        int rows = (((Rect)internal).width() - 1) / 3;
        int cols = (((Rect)internal).height() - 1) / 3;
        if (((Rect)internal).height() == 11) {
            --cols;
        }
        if (((Rect)internal).width() == 11) {
            --rows;
        }
        int w = (((Rect)internal).width() - 2 - (rows - 1)) / rows;
        int h = (((Rect)internal).height() - 2 - (cols - 1)) / cols;
        int Wspacing = rows * w + (rows + 1) == ((Rect)internal).width() ? 1 : 2;
        int Hspacing = cols * h + (cols + 1) == ((Rect)internal).height() ? 1 : 2;
        Boolean topBottomDoors = rows > cols || rows == cols && Random.Int(2) == 0;
        if (rows == 1 || cols == 1) {
            topBottomDoors = topBottomDoors == false;
        }
        if (rows == 1 && cols == 1) {
            topBottomDoors = null;
        }
        if ((openRooms = rows * cols) == 9) {
            --openRooms;
        }
        boolean guaranteeOpenRoom = this.isEntrance() || this.isExit();
        for (int x = 0; x < rows; ++x) {
            for (int y = 0; y < cols; ++y) {
                if (rows == 3 && cols == 3 && x == 1 && y == 1) continue;
                int left = internal.left + 1 + x * (w + Wspacing);
                int top = internal.top + 1 + y * (h + Hspacing);
                if (!(Random.Int(w * h) != 0 || guaranteeOpenRoom && openRooms <= 1)) {
                    Painter.fill(level, left, top, w, h, 33);
                    --openRooms;
                } else {
                    Painter.fill(level, left, top, w, h, 14);
                }
                if (topBottomDoors == null) {
                    switch (Random.Int(4)) {
                        case 0: {
                            Painter.set(level, internal.left, internal.top + ((Rect)internal).height() / 2, 5);
                            break;
                        }
                        case 1: {
                            Painter.set(level, internal.left + ((Rect)internal).width() / 2, internal.top, 5);
                            break;
                        }
                        case 2: {
                            Painter.set(level, internal.right, internal.top + ((Rect)internal).height() / 2, 5);
                            break;
                        }
                        case 3: {
                            Painter.set(level, internal.left + ((Rect)internal).width() / 2, internal.bottom, 5);
                        }
                    }
                    continue;
                }
                if (topBottomDoors.booleanValue()) {
                    if (y == 0) {
                        Painter.set(level, left + w / 2, top - 1, 5);
                        continue;
                    }
                    if (y == cols - 1) {
                        Painter.set(level, left + w / 2 - 1, top + h, 5);
                        continue;
                    }
                    if (x == 0) {
                        Painter.set(level, left - 1, top + h / 2 - 1, 5);
                        continue;
                    }
                    if (x != rows - 1) continue;
                    Painter.set(level, left + w, top + h / 2, 5);
                    continue;
                }
                if (topBottomDoors.booleanValue()) continue;
                if (x == 0) {
                    Painter.set(level, left - 1, top + h / 2 - 1, 5);
                    continue;
                }
                if (x == rows - 1) {
                    Painter.set(level, left + w, top + h / 2, 5);
                    continue;
                }
                if (y == 0) {
                    Painter.set(level, left + w / 2, top - 1, 5);
                    continue;
                }
                if (y != cols - 1) continue;
                Painter.set(level, left + w / 2 - 1, top + h, 5);
            }
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }
}

