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

public class DiamondGooRoom
extends GooBossRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fillDiamond(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Point dir = door.x == this.left ? new Point(1, 0) : (door.y == this.top ? new Point(0, 1) : (door.x == this.right ? new Point(-1, 0) : new Point(0, -1)));
            Point curr = new Point(door);
            do {
                Painter.set(level, curr, 14);
                curr.x += dir.x;
                curr.y += dir.y;
            } while (level.map[level.pointToCell(curr)] == 4);
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

