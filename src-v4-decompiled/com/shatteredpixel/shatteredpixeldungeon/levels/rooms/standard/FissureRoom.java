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

public class FissureRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{6.0f, 3.0f, 1.0f};
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Painter.fill(level, this, 1, 1);
        if (this.square() <= 25) {
            Point p = this.center();
            Painter.set(level, p.x, p.y, 0);
        } else {
            int smallestDim = Math.min(this.width(), this.height());
            int floorW = (int)Math.sqrt(smallestDim);
            float edgeFloorChance = (float)Math.sqrt(smallestDim) % 1.0f;
            edgeFloorChance = (edgeFloorChance + (float)(floorW - 1) * 0.5f) / (float)floorW;
            for (int i = this.top + 2; i <= this.bottom - 2; ++i) {
                for (int j = this.left + 2; j <= this.right - 2; ++j) {
                    int h;
                    int v = Math.min(i - this.top, this.bottom - i);
                    if (Math.min(v, h = Math.min(j - this.left, this.right - j)) <= floorW && (Math.min(v, h) != floorW || !(Random.Float() > edgeFloorChance))) continue;
                    Painter.set(level, j, i, 0);
                }
            }
        }
    }
}

