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

public class RingRoom
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
        int minDim = Math.min(this.width(), this.height());
        int passageWidth = (int)Math.floor(0.2f * (float)(minDim + 3));
        Painter.fill(level, this, passageWidth + 1, 4);
        if (minDim >= 10) {
            Painter.fill(level, this, passageWidth + 2, this.centerDecoTiles());
            Point center = this.center();
            int xDir = 0;
            int yDir = 0;
            if (Random.Int(2) == 0) {
                xDir = (float)center.x < (float)(this.left + this.right) / 2.0f ? 1 : ((float)center.x > (float)(this.left + this.right) / 2.0f ? -1 : (Random.Int(2) == 0 ? 1 : -1));
            } else {
                yDir = (float)center.y < (float)(this.top + this.bottom) / 2.0f ? 1 : ((float)center.y > (float)(this.top + this.bottom) / 2.0f ? -1 : (Random.Int(2) == 0 ? 1 : -1));
            }
            Painter.set(level, center, 14);
            this.placeCenterDetail(level, level.pointToCell(center));
            center.x += xDir;
            center.y += yDir;
            while (level.map[level.pointToCell(center)] != 4) {
                Painter.set(level, center, 14);
                center.x += xDir;
                center.y += yDir;
            }
            Painter.set(level, center, 5);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    protected int centerDecoTiles() {
        return 34;
    }

    protected void placeCenterDetail(Level level, int pos) {
        level.drop(level.findPrizeItem(), pos);
    }
}

