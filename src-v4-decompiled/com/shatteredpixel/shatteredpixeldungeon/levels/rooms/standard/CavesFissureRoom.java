/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class CavesFissureRoom
extends StandardRoom {
    private static final double A = 57.29577951308232;

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
        return new float[]{9.0f, 3.0f, 1.0f};
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        if (mergeTerrain == 0) {
            return true;
        }
        int cell = l.pointToCell(this.pointInside(p, 1));
        return l.map[cell] != 0;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && l.map[l.pointToCell(p)] != 14;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.map[l.pointToCell(p)] != 14;
    }

    @Override
    public void paint(Level level) {
        boolean pathable = true;
        PathFinder.setMapSize(this.width() - 2, this.height() - 2);
        block0: do {
            Painter.fill(level, this, 4);
            Painter.fill(level, this, 1, 1);
            for (Room.Door door : this.connected.values()) {
                door.set(Room.Door.Type.REGULAR);
            }
            PointF center = new PointF(this.center());
            center.x += 0.5f;
            center.y += 0.5f;
            ArrayList<Float> doorAngles = new ArrayList<Float>();
            for (Room.Door d : this.connected.values()) {
                PointF doorCenter = new PointF((float)d.x + 0.5f, (float)d.y + 0.5f);
                float doorAngle = CavesFissureRoom.angleBetweenPoints(center, doorCenter);
                if (doorAngle < 0.0f) {
                    doorAngle += 360.0f;
                }
                doorAngles.add(Float.valueOf(doorAngle));
            }
            ArrayList<Float> lineAngles = new ArrayList<Float>();
            int numLines = 1 + this.sizeFactor();
            for (int i = 0; i < numLines; ++i) {
                boolean valid;
                int tries = 100;
                do {
                    float angleDiff;
                    valid = true;
                    float lineAngle = Random.Float(0.0f, 360.0f);
                    Iterator iterator = doorAngles.iterator();
                    while (iterator.hasNext()) {
                        float doorAngle = ((Float)iterator.next()).floatValue();
                        angleDiff = Math.abs(lineAngle - doorAngle);
                        if (angleDiff > 180.0f) {
                            angleDiff = 360.0f - angleDiff;
                        }
                        float f = this.sizeCat == StandardRoom.SizeCategory.NORMAL ? 30.0f : 15.0f;
                        if (!(angleDiff <= f)) continue;
                        valid = false;
                        break;
                    }
                    iterator = lineAngles.iterator();
                    while (iterator.hasNext()) {
                        float existingLineAngle = ((Float)iterator.next()).floatValue();
                        angleDiff = Math.abs(lineAngle - existingLineAngle);
                        if (angleDiff > 180.0f) {
                            angleDiff = 360.0f - angleDiff;
                        }
                        float f = numLines == 2 ? 120.0f : 60.0f;
                        if (!(angleDiff <= f)) continue;
                        valid = false;
                        break;
                    }
                    if (!valid) continue;
                    lineAngles.add(Float.valueOf(lineAngle));
                } while (!valid && --tries > 0);
            }
            if (lineAngles.size() < 2) {
                PathFinder.setMapSize(level.width(), level.height());
                return;
            }
            Iterator i = lineAngles.iterator();
            while (i.hasNext()) {
                boolean horizontal;
                float lineAngle = ((Float)i.next()).floatValue();
                float dX = (float)Math.cos((double)lineAngle / 57.29577951308232 - 1.5707963267948966);
                float dY = (float)Math.sin((double)lineAngle / 57.29577951308232 - 1.5707963267948966);
                if (Math.abs(dX) >= Math.abs(dY)) {
                    horizontal = true;
                    dY /= Math.abs(dX);
                    dX /= Math.abs(dX);
                } else {
                    horizontal = false;
                    dX /= Math.abs(dY);
                    dY /= Math.abs(dY);
                }
                PointF curr = new PointF(center);
                int cell = (int)curr.x + (int)curr.y * level.width();
                Painter.set(level, cell, 0);
                do {
                    if (!horizontal) {
                        if (level.map[cell - 1] == 1 && (curr.x % 1.0f <= 0.5f || this.sizeCat == StandardRoom.SizeCategory.GIANT)) {
                            Painter.set(level, cell - 1, 0);
                        }
                        if (level.map[cell] == 1) {
                            Painter.set(level, cell, 0);
                        }
                        if (level.map[cell + 1] == 1 && (curr.x % 1.0f > 0.5f || this.sizeCat == StandardRoom.SizeCategory.GIANT)) {
                            Painter.set(level, cell + 1, 0);
                        }
                    } else {
                        if (level.map[cell - level.width()] == 1 && (curr.y % 1.0f <= 0.5f || this.sizeCat == StandardRoom.SizeCategory.GIANT)) {
                            Painter.set(level, cell - level.width(), 0);
                        }
                        if (level.map[cell] == 1) {
                            Painter.set(level, cell, 0);
                        }
                        if (level.map[cell + level.width()] == 1 && (curr.y % 1.0f > 0.5f || this.sizeCat == StandardRoom.SizeCategory.GIANT)) {
                            Painter.set(level, cell + level.width(), 0);
                        }
                    }
                    curr.x += dX;
                    curr.y += dY;
                } while (level.map[cell = (int)curr.x + (int)curr.y * level.width()] == 1 || level.map[cell] == 0);
            }
            if (lineAngles.size() >= 3) {
                if (this.sizeCat == StandardRoom.SizeCategory.GIANT) {
                    Painter.fill(level, (int)center.x - 2, (int)center.y - 2, 5, 5, 0);
                } else {
                    Painter.fill(level, (int)center.x - 1, (int)center.y - 1, 3, 3, 0);
                }
            }
            if (lineAngles.size() == 2) {
                this.buildBridge(level, ((Float)Random.element(lineAngles)).floatValue(), center, 1);
            } else {
                i = lineAngles.iterator();
                while (i.hasNext()) {
                    float angle = ((Float)i.next()).floatValue();
                    this.buildBridge(level, angle, center, this.sizeFactor());
                }
            }
            int doorPoint = 0;
            for (Room.Door door : this.connected.values()) {
                Painter.drawInside(level, this, door, 1, 1);
                if (door.x == this.left) {
                    doorPoint = this.xyToRoomCoords(door.x + 1, door.y);
                    continue;
                }
                if (door.x == this.right) {
                    doorPoint = this.xyToRoomCoords(door.x - 1, door.y);
                    continue;
                }
                if (door.y == this.top) {
                    doorPoint = this.xyToRoomCoords(door.x, door.y + 1);
                    continue;
                }
                if (door.y != this.bottom) continue;
                doorPoint = this.xyToRoomCoords(door.x, door.y - 1);
            }
            pathable = true;
            boolean[] passable = new boolean[PathFinder.distance.length];
            for (Point p : this.shrink().getPoints()) {
                int i2 = this.xyToRoomCoords(p.x, p.y);
                passable[i2] = level.map[level.pointToCell(p)] != 0;
            }
            PathFinder.buildDistanceMap(doorPoint, passable);
            for (Point p : this.shrink().getPoints()) {
                int i3 = this.xyToRoomCoords(p.x, p.y);
                if (!passable[i3] || PathFinder.distance[i3] != Integer.MAX_VALUE) continue;
                pathable = false;
                continue block0;
            }
        } while (!pathable);
        PathFinder.setMapSize(level.width(), level.height());
    }

    private void buildBridge(Level level, float fisssureAngle, PointF center, int centerMargin) {
        float dX = (float)Math.cos((double)fisssureAngle / 57.29577951308232 - 1.5707963267948966);
        float dY = (float)Math.sin((double)fisssureAngle / 57.29577951308232 - 1.5707963267948966);
        int edgemargin = 2;
        if (Math.abs(dY) >= Math.abs(dX)) {
            int Y = dY > 0.0f ? Random.IntRange((int)center.y + centerMargin, this.bottom - edgemargin) : Random.IntRange(this.top + edgemargin, (int)center.y - centerMargin);
            boolean foundChasm = false;
            if (dX <= 0.0f) {
                for (int X = this.left + 1; X <= this.right - 1; ++X) {
                    int cell = X + Y * level.width();
                    if (level.map[cell] == 0) {
                        foundChasm = true;
                        Painter.set(level, cell, 14);
                        continue;
                    }
                    if (!foundChasm) {
                        continue;
                    }
                    break;
                }
            } else {
                for (int X = this.right - 1; X >= this.left + 1; --X) {
                    int cell = X + Y * level.width();
                    if (level.map[cell] == 0) {
                        foundChasm = true;
                        Painter.set(level, cell, 14);
                        continue;
                    }
                    if (!foundChasm) {
                        continue;
                    }
                    break;
                }
            }
        } else {
            int X = dX > 0.0f ? Random.IntRange((int)center.x + centerMargin, this.right - edgemargin) : Random.IntRange(this.left + edgemargin, (int)center.x - centerMargin);
            boolean foundChasm = false;
            if (dY <= 0.0f) {
                for (int Y = this.top + 1; Y <= this.bottom - 1; ++Y) {
                    int cell = X + Y * level.width();
                    if (level.map[cell] == 0) {
                        foundChasm = true;
                        Painter.set(level, cell, 14);
                        continue;
                    }
                    if (!foundChasm) {
                        continue;
                    }
                    break;
                }
            } else {
                for (int Y = this.bottom - 1; Y >= this.top + 1; --Y) {
                    int cell = X + Y * level.width();
                    if (level.map[cell] == 0) {
                        foundChasm = true;
                        Painter.set(level, cell, 14);
                        continue;
                    }
                    if (!foundChasm) {
                        continue;
                    }
                    break;
                }
            }
        }
    }

    protected static float angleBetweenPoints(PointF from, PointF to) {
        double m = (to.y - from.y) / (to.x - from.x);
        float angle = (float)(57.29577951308232 * (Math.atan(m) + 1.5707963267948966));
        if (from.x > to.x) {
            angle -= 180.0f;
        }
        return angle;
    }

    protected int xyToRoomCoords(int x, int y) {
        return x - this.left - 1 + (y - this.top - 1) * (this.width() - 2);
    }
}

