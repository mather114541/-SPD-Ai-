/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class TunnelRoom
extends ConnectionRoom {
    @Override
    public void paint(Level level) {
        int ofs;
        int cell;
        int floor = level.tunnelTile();
        Rect c = this.getConnectionSpace();
        for (Room.Door door : this.connected.values()) {
            Point end;
            Point mid;
            Point start = new Point(door);
            if (start.x == this.left) {
                ++start.x;
            } else if (start.y == this.top) {
                ++start.y;
            } else if (start.x == this.right) {
                --start.x;
            } else if (start.y == this.bottom) {
                --start.y;
            }
            int rightShift = start.x < c.left ? c.left - start.x : (start.x > c.right ? c.right - start.x : 0);
            int downShift = start.y < c.top ? c.top - start.y : (start.y > c.bottom ? c.bottom - start.y : 0);
            if (door.x == this.left || door.x == this.right) {
                mid = new Point(start.x + rightShift, start.y);
                end = new Point(mid.x, mid.y + downShift);
            } else {
                mid = new Point(start.x, start.y + downShift);
                end = new Point(mid.x + rightShift, mid.y);
            }
            Painter.drawLine(level, start, mid, floor);
            Painter.drawLine(level, mid, end, floor);
        }
        if (this.width() >= 7 && this.height() >= 7 && this.connected.size() >= 4 && c.square() == 0 && level.map[(cell = level.pointToCell(new Point(c.left, c.top))) + PathFinder.CIRCLE8[((ofs = 2 * Random.Int(4)) + 7) % 8]] == floor && level.map[cell + PathFinder.CIRCLE8[(ofs + 1) % 8]] == floor) {
            Painter.set(level, cell + PathFinder.CIRCLE8[ofs], floor);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.TUNNEL);
        }
    }

    protected Rect getConnectionSpace() {
        Point c = this.getDoorCenter();
        return new Rect(c.x, c.y, c.x, c.y);
    }

    protected final Point getDoorCenter() {
        PointF doorCenter = new PointF(0.0f, 0.0f);
        for (Room.Door door : this.connected.values()) {
            doorCenter.x += (float)door.x;
            doorCenter.y += (float)door.y;
        }
        Point c = new Point((int)doorCenter.x / this.connected.size(), (int)doorCenter.y / this.connected.size());
        if (Random.Float() < doorCenter.x % 1.0f) {
            ++c.x;
        }
        if (Random.Float() < doorCenter.y % 1.0f) {
            ++c.y;
        }
        c.x = (int)GameMath.gate(this.left + 1, c.x, this.right - 1);
        c.y = (int)GameMath.gate(this.top + 1, c.y, this.bottom - 1);
        return c;
    }
}

