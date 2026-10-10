/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import com.watabou.utils.Point;

public class WalledGooRoom
extends GooBossRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Painter.fill(level, this, 2, 1);
        int pillarW = (this.width() - 6) / 2;
        int pillarH = (this.height() - 6) / 2;
        Painter.fill(level, this.left + 2, this.top + 2, pillarW, 1, 4);
        Painter.fill(level, this.left + 2, this.top + 2, 1, pillarH, 4);
        Painter.fill(level, this.left + 2, this.bottom - 2, pillarW, 1, 4);
        Painter.fill(level, this.left + 2, this.bottom - 1 - pillarH, 1, pillarH, 4);
        Painter.fill(level, this.right - 1 - pillarW, this.top + 2, pillarW, 1, 4);
        Painter.fill(level, this.right - 2, this.top + 2, 1, pillarH, 4);
        Painter.fill(level, this.right - 1 - pillarW, this.bottom - 2, pillarW, 1, 4);
        Painter.fill(level, this.right - 2, this.bottom - 1 - pillarH, 1, pillarH, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Painter.fill(level, this.left + this.width() / 2 - 1, this.top + this.height() / 2 - 2, 2 + this.width() % 2, 4 + this.height() % 2, 29);
        Painter.fill(level, this.left + this.width() / 2 - 2, this.top + this.height() / 2 - 1, 4 + this.width() % 2, 2 + this.height() % 2, 29);
        this.setupGooNest(level);
        Goo boss = new Goo();
        boss.pos = level.pointToCell(this.center());
        level.mobs.add(boss);
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }
}

