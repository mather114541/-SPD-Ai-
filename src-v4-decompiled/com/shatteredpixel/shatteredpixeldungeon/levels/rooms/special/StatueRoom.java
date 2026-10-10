/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;

public class StatueRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Point c = this.center();
        int cx = c.x;
        int cy = c.y;
        Room.Door door = this.entrance();
        door.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
        if (door.x == this.left) {
            Painter.fill(level, this.right - 1, this.top + 1, 1, this.height() - 2, 25);
            cx = this.right - 2;
        } else if (door.x == this.right) {
            Painter.fill(level, this.left + 1, this.top + 1, 1, this.height() - 2, 25);
            cx = this.left + 2;
        } else if (door.y == this.top) {
            Painter.fill(level, this.left + 1, this.bottom - 1, this.width() - 2, 1, 25);
            cy = this.bottom - 2;
        } else if (door.y == this.bottom) {
            Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, 1, 25);
            cy = this.top + 2;
        }
        Statue statue = Statue.random();
        statue.pos = cx + cy * level.width();
        level.mobs.add(statue);
    }
}

