/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class LibraryRingRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 9);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 9);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{4.0f, 2.0f, 1.0f};
    }

    @Override
    public Rect resize(int w, int h) {
        super.resize(w, h);
        if (this.sizeCat == StandardRoom.SizeCategory.GIANT) {
            if (this.width() % 2 == 1) {
                --this.right;
            }
            if (this.height() % 2 == 1) {
                --this.bottom;
            }
        }
        return this;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 27);
        Painter.fill(level, this, 2, 1);
        Painter.fill(level, this, 4, 27);
        if (this.sizeCat == StandardRoom.SizeCategory.GIANT) {
            Point c = new Point((this.left + this.right) / 2, (this.top + this.bottom) / 2);
            Painter.fill(level, c.x - 4, c.y, 10, 2, 1);
            Painter.fill(level, c.x, c.y - 4, 2, 10, 1);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 2, 1);
        }
    }
}

