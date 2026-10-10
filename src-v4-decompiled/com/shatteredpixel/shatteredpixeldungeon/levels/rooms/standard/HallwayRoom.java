/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class HallwayRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return other instanceof HallwayRoom && super.canMerge(l, other, p, mergeTerrain);
    }

    @Override
    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        super.merge(l, other, merge, mergeTerrain);
        Painter.set(l, (Point)this.connected.get(other), 14);
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
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
            Painter.drawLine(level, start, mid, 14);
            Painter.drawLine(level, mid, end, 14);
        }
        Painter.fill(level, c.left, c.top, 3, 3, 14);
        Carpet carpet = new Carpet();
        carpet.setRect(c.left, c.top, 3, 3);
        if (this.isEntrance()) {
            Painter.fill(level, c.left + 1, c.top + 1, 1, 1, 37);
            carpet.overrideTile(1, 1, 82);
        } else if (this.isExit()) {
            Painter.fill(level, c.left + 1, c.top + 1, 1, 1, 8);
            carpet.overrideTile(1, 1, -1);
        } else if (Random.Int(2) == 0) {
            Painter.fill(level, c.left + 1, c.top + 1, 1, 1, 26);
            carpet.overrideTile(1, 1, 80);
        } else {
            Painter.fill(level, c.left + 1, c.top + 1, 1, 1, 34);
            carpet.overrideTile(1, 1, 81);
        }
        level.customTiles.add(carpet);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    protected Rect getConnectionSpace() {
        Point c = this.center();
        c.x = (int)GameMath.gate(this.left + 2, c.x, this.right - 2);
        c.y = (int)GameMath.gate(this.top + 2, c.y, this.bottom - 2);
        return new Rect(c.x - 1, c.y - 1, c.x + 1, c.y + 1);
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
        c.x = (int)GameMath.gate(this.left + 2, c.x, this.right - 2);
        c.y = (int)GameMath.gate(this.top + 2, c.y, this.bottom - 2);
        return c;
    }
}

