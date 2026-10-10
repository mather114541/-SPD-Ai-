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

public class LibraryHallRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{2.0f, 1.0f, 0.0f};
    }

    @Override
    public void paint(Level level) {
        int minorDim;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        float topBottomBooks = 0.0f;
        float leftRightBooks = 0.0f;
        if (this.width() > this.height()) {
            topBottomBooks += (float)(this.width() - this.height());
        } else if (this.height() > this.width()) {
            leftRightBooks += (float)(this.height() - this.width());
        }
        if (this.width() % 2 == 0 && this.height() % 2 != 0) {
            topBottomBooks += 2.0f;
        } else if (this.width() % 2 != 0 && this.height() % 2 == 0) {
            leftRightBooks += 2.0f;
        }
        for (Room.Door door : this.connected.values()) {
            if (door.x == this.left || door.x == this.right) {
                topBottomBooks += 0.1f;
                continue;
            }
            leftRightBooks += 0.1f;
        }
        boolean layingOutLeftToRight = topBottomBooks > leftRightBooks || topBottomBooks == leftRightBooks && Random.Int(2) == 0;
        int majorDim = layingOutLeftToRight ? this.height() : this.width();
        int n = minorDim = layingOutLeftToRight ? this.width() : this.height();
        if (majorDim >= 11 || majorDim < 9) {
            if (layingOutLeftToRight) {
                Painter.drawLine(level, new Point(this.left + 1, this.top + 1), new Point(this.right - 1, this.top + 1), 27);
                Painter.drawLine(level, new Point(this.left + 1, this.bottom - 1), new Point(this.right - 1, this.bottom - 1), 27);
            } else {
                Painter.drawLine(level, new Point(this.left + 1, this.top + 1), new Point(this.left + 1, this.bottom - 1), 27);
                Painter.drawLine(level, new Point(this.right - 1, this.top + 1), new Point(this.right - 1, this.bottom - 1), 27);
            }
        }
        Point center = this.center();
        int lengthInset = 2;
        if (majorDim >= 9) {
            if (minorDim >= 13) {
                ++lengthInset;
            }
            if (layingOutLeftToRight) {
                Painter.drawLine(level, new Point(this.left + lengthInset, center.y - 2), new Point(this.right - lengthInset, center.y - 2), 27);
                Painter.drawLine(level, new Point(this.left + lengthInset, center.y + 2), new Point(this.right - lengthInset, center.y + 2), 27);
            } else {
                Painter.drawLine(level, new Point(center.x - 2, this.top + lengthInset), new Point(center.x - 2, this.bottom - lengthInset), 27);
                Painter.drawLine(level, new Point(center.x + 2, this.top + lengthInset), new Point(center.x + 2, this.bottom - lengthInset), 27);
            }
        }
        if (minorDim % 2 == 1 && minorDim < 9) {
            Painter.set(level, center, 33);
        } else {
            int pedestalInset = 2;
            if (minorDim >= 10) {
                ++pedestalInset;
                if (minorDim >= 13) {
                    ++pedestalInset;
                }
            }
            if (layingOutLeftToRight) {
                Painter.set(level, this.left + pedestalInset, center.y, 33);
                Painter.set(level, this.right - pedestalInset, center.y, 33);
            } else {
                Painter.set(level, center.x, this.top + pedestalInset, 33);
                Painter.set(level, center.x, this.bottom - pedestalInset, 33);
            }
        }
        for (Room.Door door : this.connected.values()) {
            Painter.drawInside(level, this, door, 1, 1);
            door.set(Room.Door.Type.REGULAR);
        }
    }
}

