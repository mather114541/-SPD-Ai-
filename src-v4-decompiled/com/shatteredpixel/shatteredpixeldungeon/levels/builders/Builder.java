/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class Builder {
    private static final double A = 57.29577951308232;

    public abstract ArrayList<Room> build(ArrayList<Room> var1);

    protected static void findNeighbours(ArrayList<Room> rooms) {
        Room[] ra = rooms.toArray(new Room[0]);
        for (int i = 0; i < ra.length - 1; ++i) {
            for (int j = i + 1; j < ra.length; ++j) {
                ra[i].addNeigbour(ra[j]);
            }
        }
    }

    protected static Rect findFreeSpace(Point start, ArrayList<Room> collision, int maxSize) {
        Rect space = new Rect(start.x - maxSize, start.y - maxSize, start.x + maxSize, start.y + maxSize);
        ArrayList<Room> colliding = new ArrayList<Room>(collision);
        do {
            Iterator<Room> it = colliding.iterator();
            while (it.hasNext()) {
                Room room = it.next();
                if (!room.isEmpty() && Math.max(space.left, room.left) < Math.min(space.right, room.right) && Math.max(space.top, room.top) < Math.min(space.bottom, room.bottom)) continue;
                it.remove();
            }
            Room closestRoom = null;
            float closestDiff = 2.14748365E9f;
            Point curDiff = new Point();
            for (Room curRoom : colliding) {
                curDiff.y = 0;
                curDiff.x = 0;
                boolean inside = true;
                if (start.x <= curRoom.left) {
                    inside = false;
                    curDiff.x = curRoom.left - start.x;
                } else if (start.x >= curRoom.right) {
                    inside = false;
                    curDiff.x = start.x - curRoom.right;
                }
                if (start.y <= curRoom.top) {
                    inside = false;
                    curDiff.y = curRoom.top - start.y;
                } else if (start.y >= curRoom.bottom) {
                    inside = false;
                    curDiff.y = start.y - curRoom.bottom;
                }
                if (inside) {
                    space.set(start.x, start.y, start.x, start.y);
                    return space;
                }
                if (!(curDiff.length() < closestDiff)) continue;
                closestDiff = curDiff.length();
                closestRoom = curRoom;
            }
            if (closestRoom != null) {
                int wDiff = Integer.MAX_VALUE;
                if (closestRoom.left >= start.x) {
                    wDiff = (space.right - closestRoom.left) * (space.height() + 1);
                } else if (closestRoom.right <= start.x) {
                    wDiff = (closestRoom.right - space.left) * (space.height() + 1);
                }
                int hDiff = Integer.MAX_VALUE;
                if (closestRoom.top >= start.y) {
                    hDiff = (space.bottom - closestRoom.top) * (space.width() + 1);
                } else if (closestRoom.bottom <= start.y) {
                    hDiff = (closestRoom.bottom - space.top) * (space.width() + 1);
                }
                if (wDiff < hDiff || wDiff == hDiff && Random.Int(2) == 0) {
                    if (closestRoom.left >= start.x && closestRoom.left < space.right) {
                        space.right = closestRoom.left;
                    }
                    if (closestRoom.right <= start.x && closestRoom.right > space.left) {
                        space.left = closestRoom.right;
                    }
                } else {
                    if (closestRoom.top >= start.y && closestRoom.top < space.bottom) {
                        space.bottom = closestRoom.top;
                    }
                    if (closestRoom.bottom <= start.y && closestRoom.bottom > space.top) {
                        space.top = closestRoom.bottom;
                    }
                }
                colliding.remove(closestRoom);
                continue;
            }
            colliding.clear();
        } while (!colliding.isEmpty());
        return space;
    }

    protected static float angleBetweenRooms(Room from, Room to) {
        PointF fromCenter = new PointF((float)(from.left + from.right) / 2.0f, (float)(from.top + from.bottom) / 2.0f);
        PointF toCenter = new PointF((float)(to.left + to.right) / 2.0f, (float)(to.top + to.bottom) / 2.0f);
        return Builder.angleBetweenPoints(fromCenter, toCenter);
    }

    protected static float angleBetweenPoints(PointF from, PointF to) {
        double m = (to.y - from.y) / (to.x - from.x);
        float angle = (float)(57.29577951308232 * (Math.atan(m) + 1.5707963267948966));
        if (from.x > to.x) {
            angle -= 180.0f;
        }
        return angle;
    }

    protected static float placeRoom(ArrayList<Room> collision, Room prev, Room next, float angle) {
        Point start;
        int direction;
        if ((angle %= 360.0f) < 0.0f) {
            angle += 360.0f;
        }
        PointF prevCenter = new PointF((float)(prev.left + prev.right) / 2.0f, (float)(prev.top + prev.bottom) / 2.0f);
        double m = Math.tan((double)angle / 57.29577951308232 + 1.5707963267948966);
        double b = (double)prevCenter.y - m * (double)prevCenter.x;
        if (Math.abs(m) >= 1.0) {
            if (angle < 90.0f || angle > 270.0f) {
                direction = 2;
                start = new Point((int)Math.round(((double)prev.top - b) / m), prev.top);
            } else {
                direction = 4;
                start = new Point((int)Math.round(((double)prev.bottom - b) / m), prev.bottom);
            }
        } else if (angle < 180.0f) {
            direction = 3;
            start = new Point(prev.right, (int)Math.round(m * (double)prev.right + b));
        } else {
            direction = 1;
            start = new Point(prev.left, (int)Math.round(m * (double)prev.left + b));
        }
        if (direction == 2 || direction == 4) {
            start.x = (int)GameMath.gate(prev.left + 1, start.x, prev.right - 1);
        } else {
            start.y = (int)GameMath.gate(prev.top + 1, start.y, prev.bottom - 1);
        }
        Rect space = Builder.findFreeSpace(start, collision, Math.max(next.maxWidth(), next.maxHeight()));
        if (!next.setSizeWithLimit(space.width() + 1, space.height() + 1)) {
            return -1.0f;
        }
        PointF targetCenter = new PointF();
        if (direction == 2) {
            targetCenter.y = (float)prev.top - (float)(next.height() - 1) / 2.0f;
            targetCenter.x = (float)(((double)targetCenter.y - b) / m);
            next.setPos(Math.round(targetCenter.x - (float)(next.width() - 1) / 2.0f), prev.top - (next.height() - 1));
        } else if (direction == 4) {
            targetCenter.y = (float)prev.bottom + (float)(next.height() - 1) / 2.0f;
            targetCenter.x = (float)(((double)targetCenter.y - b) / m);
            next.setPos(Math.round(targetCenter.x - (float)(next.width() - 1) / 2.0f), prev.bottom);
        } else if (direction == 3) {
            targetCenter.x = (float)prev.right + (float)(next.width() - 1) / 2.0f;
            targetCenter.y = (float)(m * (double)targetCenter.x + b);
            next.setPos(prev.right, Math.round(targetCenter.y - (float)(next.height() - 1) / 2.0f));
        } else if (direction == 1) {
            targetCenter.x = (float)prev.left - (float)(next.width() - 1) / 2.0f;
            targetCenter.y = (float)(m * (double)targetCenter.x + b);
            next.setPos(prev.left - (next.width() - 1), Math.round(targetCenter.y - (float)(next.height() - 1) / 2.0f));
        }
        if (direction == 2 || direction == 4) {
            if (next.right < prev.left + 2) {
                next.shift(prev.left + 2 - next.right, 0);
            } else if (next.left > prev.right - 2) {
                next.shift(prev.right - 2 - next.left, 0);
            }
            if (next.right > space.right) {
                next.shift(space.right - next.right, 0);
            } else if (next.left < space.left) {
                next.shift(space.left - next.left, 0);
            }
        } else {
            if (next.bottom < prev.top + 2) {
                next.shift(0, prev.top + 2 - next.bottom);
            } else if (next.top > prev.bottom - 2) {
                next.shift(0, prev.bottom - 2 - next.top);
            }
            if (next.bottom > space.bottom) {
                next.shift(0, space.bottom - next.bottom);
            } else if (next.top < space.top) {
                next.shift(0, space.top - next.top);
            }
        }
        if (next.connect(prev)) {
            return Builder.angleBetweenRooms(prev, next);
        }
        return -1.0f;
    }
}

