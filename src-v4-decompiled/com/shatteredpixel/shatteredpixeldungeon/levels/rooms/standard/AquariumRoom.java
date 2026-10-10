/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;

public class AquariumRoom
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
        return new float[]{3.0f, 1.0f, 0.0f};
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && l.map[l.pointToCell(p)] != 29;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.map[l.pointToCell(p)] != 29;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 2, 14);
        Painter.fill(level, this, 3, 29);
        int minDim = Math.min(this.width(), this.height());
        int numFish = (minDim - 4) / 3;
        for (int i = 0; i < numFish; ++i) {
            Piranha piranha = Piranha.random();
            do {
                piranha.pos = level.pointToCell(this.random(3));
            } while (level.map[piranha.pos] != 29 || level.findMob(piranha.pos) != null);
            level.mobs.add(piranha);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }
}

