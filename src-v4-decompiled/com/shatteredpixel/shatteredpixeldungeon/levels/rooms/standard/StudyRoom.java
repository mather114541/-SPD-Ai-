/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class StudyRoom
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
        return new float[]{2.0f, 1.0f, 0.0f};
    }

    @Override
    public void paint(Level level) {
        Item prize;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 27);
        Painter.fill(level, this, 2, 14);
        for (Room.Door door : this.connected.values()) {
            Painter.drawInside(level, this, door, 2, 14);
            door.set(Room.Door.Type.REGULAR);
        }
        if (this.sizeCat == StandardRoom.SizeCategory.LARGE) {
            int pillarW = (this.width() - 7) / 2;
            int pillarH = (this.height() - 7) / 2;
            Painter.fill(level, this.left + 3, this.top + 3, pillarW, 1, 27);
            Painter.fill(level, this.left + 3, this.top + 3, 1, pillarH, 27);
            Painter.fill(level, this.left + 3, this.bottom - 2 - 1, pillarW, 1, 27);
            Painter.fill(level, this.left + 3, this.bottom - 2 - pillarH, 1, pillarH, 27);
            Painter.fill(level, this.right - 2 - pillarW, this.top + 3, pillarW, 1, 27);
            Painter.fill(level, this.right - 2 - 1, this.top + 3, 1, pillarH, 27);
            Painter.fill(level, this.right - 2 - pillarW, this.bottom - 2 - 1, pillarW, 1, 27);
            Painter.fill(level, this.right - 2 - 1, this.bottom - 2 - pillarH, 1, pillarH, 27);
        }
        Point center = this.center();
        Painter.set(level, center, 11);
        Item item = prize = Random.Int(2) == 0 ? level.findPrizeItem() : null;
        if (prize != null) {
            level.drop(prize, center.x + center.y * level.width());
        } else {
            level.drop(Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL)), center.x + center.y * level.width());
        }
    }
}

