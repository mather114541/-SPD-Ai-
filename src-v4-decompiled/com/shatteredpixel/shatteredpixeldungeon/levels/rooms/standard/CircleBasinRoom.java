/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Rect;

public class CircleBasinRoom
extends PatchRoom {
    @Override
    public int minWidth() {
        return this.sizeCat.minDim + 1;
    }

    @Override
    public int minHeight() {
        return this.sizeCat.minDim + 1;
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 3.0f, 1.0f};
    }

    @Override
    public Rect resize(int w, int h) {
        super.resize(w, h);
        if (this.width() % 2 == 0) {
            --this.right;
        }
        if (this.height() % 2 == 0) {
            --this.bottom;
        }
        return this;
    }

    @Override
    protected float fill() {
        return 0.5f;
    }

    @Override
    protected int clustering() {
        return 5;
    }

    @Override
    protected boolean ensurePath() {
        return false;
    }

    @Override
    protected boolean cleanEdges() {
        return false;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fillEllipse(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            if (door.x == this.left || door.x == this.right) {
                Painter.drawInside(level, this, door, this.width() / 2, 1);
                continue;
            }
            Painter.drawInside(level, this, door, this.height() / 2, 1);
        }
        Painter.fillEllipse(level, this, 3, 0);
        Point start = new Point(this.left + this.width() / 2, this.top + 3);
        Point end = new Point(this.left + this.width() / 2, this.bottom - 3);
        Painter.drawLine(level, start, end, 14);
        start.set(this.left + 3, this.top + this.height() / 2);
        end.set(this.right - 3, this.top + this.height() / 2);
        Painter.drawLine(level, start, end, 14);
        if (this.width() > 11 || this.height() > 11) {
            Point center = this.center();
            Painter.fill(level, center.x - 1, center.y - 1, 3, 3, 14);
            Painter.set(level, center, 4);
        }
        this.setupPatch(level);
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                int cell = i * level.width() + j;
                if (level.map[cell] != 1 || !this.patch[this.xyToPatchCoords(j, i)]) continue;
                level.map[cell] = 29;
                if (level.map[cell - level.width()] != 4) continue;
                level.map[cell - level.width()] = 12;
            }
        }
    }
}

