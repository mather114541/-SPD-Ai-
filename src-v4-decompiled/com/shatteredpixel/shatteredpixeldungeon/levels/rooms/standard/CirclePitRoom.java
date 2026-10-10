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

public class CirclePitRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(8, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(8, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{4.0f, 2.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        block10: {
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
            if (this.sizeCat == StandardRoom.SizeCategory.NORMAL || Random.Int(4 - this.sizeFactor()) != 0) break block10;
            Point center = this.center();
            center.x += Random.IntRange(-1, 1);
            center.y += Random.IntRange(-1, 1);
            Point edge = new Point(center);
            switch (Random.Int(4)) {
                case 0: {
                    edge.x = this.left;
                    break;
                }
                case 1: {
                    edge.y = this.top;
                    break;
                }
                case 2: {
                    edge.x = this.right;
                    break;
                }
                case 3: {
                    edge.y = this.bottom;
                }
            }
            boolean valid = true;
            for (Point door : this.connected.values()) {
                if (!door.equals(edge)) continue;
                valid = false;
            }
            if (valid) {
                Painter.drawLine(level, edge, center, 34);
                Painter.drawInside(level, this, edge, 1, 14);
                Painter.set(level, edge, 4);
            }
        }
    }
}

