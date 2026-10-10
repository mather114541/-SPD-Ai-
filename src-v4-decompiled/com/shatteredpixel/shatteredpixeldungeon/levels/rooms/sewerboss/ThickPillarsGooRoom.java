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

public class ThickPillarsGooRoom
extends GooBossRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 29);
        int pillarW = (this.width() - 8) / 2;
        int pillarH = (this.height() - 8) / 2;
        Painter.fill(level, this.left + 2, this.top + 2, pillarW + 1, pillarH + 1, 4);
        Painter.fill(level, this.left + 2, this.bottom - 2 - pillarH, pillarW + 1, pillarH + 1, 4);
        Painter.fill(level, this.right - 2 - pillarW, this.top + 2, pillarW + 1, pillarH + 1, 4);
        Painter.fill(level, this.right - 2 - pillarW, this.bottom - 2 - pillarH, pillarW + 1, pillarH + 1, 4);
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

