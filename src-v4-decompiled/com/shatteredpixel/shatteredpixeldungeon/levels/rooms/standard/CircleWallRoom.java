/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;

public class CircleWallRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 3.0f, 1.0f};
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
        Painter.fillEllipse(level, this, 3, 4);
    }
}

