/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class SewerPipeRoom
extends StandardRoom {
    private Point[] corners;

    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{3.0f, 2.0f, 1.0f};
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return false;
    }

    @Override
    public boolean canConnect(Point p) {
        return super.canConnect(p) && (p.x > this.left + 1 && p.x < this.right - 1 || p.y > this.top + 1 && p.y < this.bottom - 1);
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Rect c = this.getConnectionSpace();
        if (this.connected.size() == 1 || this.connected.size() == 2 && this.sizeCat == StandardRoom.SizeCategory.NORMAL) {
            for (Room.Door door : this.connected.values()) {
                Point end;
                Object mid;
                Point start = new Point(door);
                if (start.x == this.left) {
                    start.x += 2;
                } else if (start.y == this.top) {
                    start.y += 2;
                } else if (start.x == this.right) {
                    start.x -= 2;
                } else if (start.y == this.bottom) {
                    start.y -= 2;
                }
                int rightShift = start.x < c.left ? c.left - start.x : (start.x > c.right ? c.right - start.x : 0);
                int downShift = start.y < c.top ? c.top - start.y : (start.y > c.bottom ? c.bottom - start.y : 0);
                if (door.x == this.left || door.x == this.right) {
                    mid = new Point(start.x + rightShift, start.y);
                    end = new Point(((Point)mid).x, ((Point)mid).y + downShift);
                } else {
                    mid = new Point(start.x, start.y + downShift);
                    end = new Point(((Point)mid).x + rightShift, ((Point)mid).y);
                }
                Painter.drawLine(level, start, (Point)mid, 29);
                Painter.drawLine(level, (Point)mid, end, 29);
            }
        } else {
            ArrayList doorPoints = new ArrayList(this.connected.values());
            if (doorPoints.size() == 2) {
                boolean valid;
                Point p = new Point();
                do {
                    valid = true;
                    if (Random.Int(2) == 0) {
                        p.x = Random.Int(2) == 0 ? this.left : this.right;
                        p.y = Random.IntRange(this.top + 2, this.bottom - 2);
                    } else {
                        p.x = Random.IntRange(this.left + 2, this.right - 2);
                        p.y = Random.Int(2) == 0 ? this.top : this.bottom;
                    }
                    for (Point door : this.connected.values()) {
                        if (door.x != p.x && door.y != p.y) continue;
                        valid = false;
                    }
                } while (!valid);
                doorPoints.add(p);
            }
            ArrayList<Point> pointsToFill = new ArrayList<Point>();
            for (Point door : doorPoints) {
                Point p = new Point(door);
                if (p.y == this.top) {
                    p.y += 2;
                } else if (p.y == this.bottom) {
                    p.y -= 2;
                } else {
                    p.x = p.x == this.left ? (p.x += 2) : (p.x -= 2);
                }
                pointsToFill.add(p);
            }
            ArrayList<Point> pointsFilled = new ArrayList<Point>();
            pointsFilled.add((Point)pointsToFill.remove(0));
            Point from = null;
            Point to = null;
            while (!pointsToFill.isEmpty()) {
                int shortestDistance = Integer.MAX_VALUE;
                for (Point f : pointsFilled) {
                    for (Point t : pointsToFill) {
                        int dist = this.distanceBetweenPoints(f, t);
                        if (dist >= shortestDistance) continue;
                        from = f;
                        to = t;
                        shortestDistance = dist;
                    }
                }
                this.fillBetweenPoints(level, from, to, 29);
                pointsFilled.add(to);
                pointsToFill.remove(to);
            }
        }
        for (Point p : this.getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] != 29) continue;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (level.map[cell + i] != 4) continue;
                Painter.set(level, cell + i, 1);
            }
        }
        for (Room r : this.connected.keySet()) {
            if (r instanceof SewerPipeRoom) {
                Point door = (Point)this.connected.get(r);
                Painter.fill(level, door.x - 1, door.y - 1, 3, 3, 1);
                if (door.x == this.left || door.x == this.right) {
                    Painter.fill(level, door.x - 1, door.y, 3, 1, 29);
                } else {
                    Painter.fill(level, door.x, door.y - 1, 1, 3, 29);
                }
                ((Room.Door)this.connected.get(r)).set(Room.Door.Type.WATER);
                continue;
            }
            ((Room.Door)this.connected.get(r)).set(Room.Door.Type.REGULAR);
        }
    }

    protected Rect getConnectionSpace() {
        Point c = this.connected.size() <= 1 ? this.center() : this.getDoorCenter();
        return new Rect(c.x, c.y, c.x, c.y);
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
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

    private int spaceBetween(int a, int b) {
        return Math.abs(a - b) - 1;
    }

    private int distanceBetweenPoints(Point a, Point b) {
        if ((a.x == this.left + 2 || a.x == this.right - 2) && a.y == b.y || (a.y == this.top + 2 || a.y == this.bottom - 2) && a.x == b.x) {
            return Math.max(this.spaceBetween(a.x, b.x), this.spaceBetween(a.y, b.y));
        }
        return Math.min(this.spaceBetween(this.left, a.x) + this.spaceBetween(this.left, b.x), this.spaceBetween(this.right, a.x) + this.spaceBetween(this.right, b.x)) + Math.min(this.spaceBetween(this.top, a.y) + this.spaceBetween(this.top, b.y), this.spaceBetween(this.bottom, a.y) + this.spaceBetween(this.bottom, b.y)) - 1;
    }

    private void fillBetweenPoints(Level level, Point from, Point to, int floor) {
        if ((from.x == this.left + 2 || from.x == this.right - 2) && from.x == to.x || (from.y == this.top + 2 || from.y == this.bottom - 2) && from.y == to.y) {
            Painter.fill(level, Math.min(from.x, to.x), Math.min(from.y, to.y), this.spaceBetween(from.x, to.x) + 2, this.spaceBetween(from.y, to.y) + 2, floor);
            return;
        }
        if (this.corners == null) {
            this.corners = new Point[4];
            this.corners[0] = new Point(this.left + 2, this.top + 2);
            this.corners[1] = new Point(this.right - 2, this.top + 2);
            this.corners[2] = new Point(this.right - 2, this.bottom - 2);
            this.corners[3] = new Point(this.left + 2, this.bottom - 2);
        }
        for (Point c : this.corners) {
            if (c.x != from.x && c.y != from.y || c.x != to.x && c.y != to.y) continue;
            Painter.drawLine(level, from, c, floor);
            Painter.drawLine(level, c, to, floor);
            return;
        }
        Point side = from.y == this.top + 2 || from.y == this.bottom - 2 ? (this.spaceBetween(this.left, from.x) + this.spaceBetween(this.left, to.x) <= this.spaceBetween(this.right, from.x) + this.spaceBetween(this.right, to.x) ? new Point(this.left + 2, this.top + this.height() / 2) : new Point(this.right - 2, this.top + this.height() / 2)) : (this.spaceBetween(this.top, from.y) + this.spaceBetween(this.top, to.y) <= this.spaceBetween(this.bottom, from.y) + this.spaceBetween(this.bottom, to.y) ? new Point(this.left + this.width() / 2, this.top + 2) : new Point(this.left + this.width() / 2, this.bottom - 2));
        this.fillBetweenPoints(level, from, side, floor);
        this.fillBetweenPoints(level, side, to, floor);
    }
}

