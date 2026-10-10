/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.PerimeterRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;

public class ThinPillarsGooRoom
extends GooBossRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 29);
        int pillarW = (this.width() == 14 ? 4 : 2) + this.width() % 2;
        int pillarH = (this.height() == 14 ? 4 : 2) + this.height() % 2;
        if (this.height() < 12) {
            Painter.fill(level, this.left + (this.width() - pillarW) / 2, this.top + 2, pillarW, 1, 4);
            Painter.fill(level, this.left + (this.width() - pillarW) / 2, this.bottom - 2, pillarW, 1, 4);
        } else {
            Painter.fill(level, this.left + (this.width() - pillarW) / 2, this.top + 3, pillarW, 1, 4);
            Painter.fill(level, this.left + (this.width() - pillarW) / 2, this.bottom - 3, pillarW, 1, 4);
        }
        if (this.width() < 12) {
            Painter.fill(level, this.left + 2, this.top + (this.height() - pillarH) / 2, 1, pillarH, 4);
            Painter.fill(level, this.right - 2, this.top + (this.height() - pillarH) / 2, 1, pillarH, 4);
        } else {
            Painter.fill(level, this.left + 3, this.top + (this.height() - pillarH) / 2, 1, pillarH, 4);
            Painter.fill(level, this.right - 3, this.top + (this.height() - pillarH) / 2, 1, pillarH, 4);
        }
        PerimeterRoom.fillPerimiterPaths(level, this, 14);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        this.setupGooNest(level);
        Goo boss = new Goo();
        boss.pos = level.pointToCell(this.center());
        level.mobs.add(boss);
    }
}

