/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.watabou.utils.Point;
import java.util.ArrayList;

public class PerimeterRoom
extends ConnectionRoom {
    private static Point[] corners;

    @Override
    public void paint(Level level) {
        int floor = level.tunnelTile();
        PerimeterRoom.fillPerimiterPaths(level, this, floor);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.TUNNEL);
        }
    }

    /*
     * WARNING - void declaration
     */
    public static void fillPerimiterPaths(Level l, Room r, int floor) {
        corners = null;
        ArrayList<Point> pointsToFill = new ArrayList<Point>();
        for (Point point : r.connected.values()) {
            Point p = new Point(point);
            if (p.y == r.top) {
                ++p.y;
            } else if (p.y == r.bottom) {
                --p.y;
            } else {
                p.x = p.x == r.left ? ++p.x : --p.x;
            }
            pointsToFill.add(p);
        }
        ArrayList<Point> pointsFilled = new ArrayList<Point>();
        pointsFilled.add((Point)pointsToFill.remove(0));
        Object var5_6 = null;
        Point to = null;
        while (!pointsToFill.isEmpty()) {
            void var5_7;
            int shortestDistance = Integer.MAX_VALUE;
            for (Point f : pointsFilled) {
                for (Point t : pointsToFill) {
                    int dist = PerimeterRoom.distanceBetweenPoints(r, f, t);
                    if (dist >= shortestDistance) continue;
                    Point point = f;
                    to = t;
                    shortestDistance = dist;
                }
            }
            PerimeterRoom.fillBetweenPoints(l, r, (Point)var5_7, to, floor);
            pointsFilled.add(to);
            pointsToFill.remove(to);
        }
    }

    private static int spaceBetween(int a, int b) {
        return Math.abs(a - b) - 1;
    }

    private static int distanceBetweenPoints(Room r, Point a, Point b) {
        if ((a.x == r.left + 1 || a.x == r.right - 1) && a.y == b.y || (a.y == r.top + 1 || a.y == r.bottom - 1) && a.x == b.x) {
            return Math.max(PerimeterRoom.spaceBetween(a.x, b.x), PerimeterRoom.spaceBetween(a.y, b.y));
        }
        return Math.min(PerimeterRoom.spaceBetween(r.left, a.x) + PerimeterRoom.spaceBetween(r.left, b.x), PerimeterRoom.spaceBetween(r.right, a.x) + PerimeterRoom.spaceBetween(r.right, b.x)) + Math.min(PerimeterRoom.spaceBetween(r.top, a.y) + PerimeterRoom.spaceBetween(r.top, b.y), PerimeterRoom.spaceBetween(r.bottom, a.y) + PerimeterRoom.spaceBetween(r.bottom, b.y)) - 1;
    }

    private static void fillBetweenPoints(Level level, Room r, Point from, Point to, int floor) {
        if ((from.x == r.left + 1 || from.x == r.right - 1) && from.x == to.x || (from.y == r.top + 1 || from.y == r.bottom - 1) && from.y == to.y) {
            Painter.fill(level, Math.min(from.x, to.x), Math.min(from.y, to.y), PerimeterRoom.spaceBetween(from.x, to.x) + 2, PerimeterRoom.spaceBetween(from.y, to.y) + 2, floor);
            return;
        }
        if (corners == null) {
            corners = new Point[4];
            PerimeterRoom.corners[0] = new Point(r.left + 1, r.top + 1);
            PerimeterRoom.corners[1] = new Point(r.right - 1, r.top + 1);
            PerimeterRoom.corners[2] = new Point(r.right - 1, r.bottom - 1);
            PerimeterRoom.corners[3] = new Point(r.left + 1, r.bottom - 1);
        }
        for (Point c : corners) {
            if (c.x != from.x && c.y != from.y || c.x != to.x && c.y != to.y) continue;
            Painter.drawLine(level, from, c, floor);
            Painter.drawLine(level, c, to, floor);
            return;
        }
        Point side = from.y == r.top + 1 || from.y == r.bottom - 1 ? (PerimeterRoom.spaceBetween(r.left, from.x) + PerimeterRoom.spaceBetween(r.left, to.x) <= PerimeterRoom.spaceBetween(r.right, from.x) + PerimeterRoom.spaceBetween(r.right, to.x) ? new Point(r.left + 1, r.top + r.height() / 2) : new Point(r.right - 1, r.top + r.height() / 2)) : (PerimeterRoom.spaceBetween(r.top, from.y) + PerimeterRoom.spaceBetween(r.top, to.y) <= PerimeterRoom.spaceBetween(r.bottom, from.y) + PerimeterRoom.spaceBetween(r.bottom, to.y) ? new Point(r.left + r.width() / 2, r.top + 1) : new Point(r.left + r.width() / 2, r.bottom - 1));
        PerimeterRoom.fillBetweenPoints(level, r, from, side, floor);
        PerimeterRoom.fillBetweenPoints(level, r, side, to, floor);
    }
}

