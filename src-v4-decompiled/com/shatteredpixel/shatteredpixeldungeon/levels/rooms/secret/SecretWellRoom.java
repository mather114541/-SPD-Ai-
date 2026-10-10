/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SecretWellRoom
extends SecretRoom {
    private static final Class<?>[] WATERS = new Class[]{WaterOfAwareness.class, WaterOfHealth.class};

    @Override
    public boolean canConnect(Point p) {
        return super.canConnect(p) && (p.x > this.left + 1 && p.x < this.right - 1 || p.y > this.top + 1 && p.y < this.bottom - 1);
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Room.Door door = this.entrance();
        Point well = door.x == this.left ? new Point(this.right - 2, door.y) : (door.x == this.right ? new Point(this.left + 2, door.y) : (door.y == this.top ? new Point(door.x, this.bottom - 2) : new Point(door.x, this.top + 2)));
        Painter.fill(level, well.x - 1, well.y - 1, 3, 3, 0);
        Painter.drawLine(level, door, well, 1);
        Painter.set(level, well, 24);
        Class<?> waterClass = Random.element(WATERS);
        WellWater.seed(well.x + level.width() * well.y, 1, waterClass, level);
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

