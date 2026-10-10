/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public abstract class StandardBridgeRoom
extends StandardRoom {
    protected Rect spaceRect;
    protected Rect bridgeRect;

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
        int cell = l.pointToCell(this.pointInside(p, 1));
        return l.map[cell] != this.spaceTile();
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && (this.spaceRect == null || !this.spaceRect.inside(p));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && (this.spaceRect == null || !this.spaceRect.inside(p));
    }

    @Override
    public void paint(Level level) {
        ArrayList<Point> spacePoints;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        int doorsXY = 0;
        for (Object door : this.connected.values()) {
            ((Room.Door)door).set(Room.Door.Type.REGULAR);
            if (((Room.Door)door).x == this.left || ((Room.Door)door).x == this.right) {
                ++doorsXY;
                continue;
            }
            --doorsXY;
        }
        if ((doorsXY += (this.width() - this.height()) / 2) > 0 || doorsXY == 0 && Random.Int(2) == 0) {
            spacePoints = new ArrayList<Point>();
            for (Room.Door door : this.connected.values()) {
                if (door.y != this.top && door.y != this.bottom) continue;
                spacePoints.add(door);
            }
            spacePoints.add(new Point(this.left + 1, 0));
            spacePoints.add(new Point(this.right - 1, 0));
            Collections.sort(spacePoints, new Comparator<Point>(){

                @Override
                public int compare(Point d1, Point d2) {
                    return d1.x - d2.x;
                }
            });
            int spaceStart = -1;
            int spaceEnd = -1;
            for (int i = 0; i < spacePoints.size() - 1; ++i) {
                if (spaceEnd - spaceStart >= ((Point)spacePoints.get((int)(i + 1))).x - ((Point)spacePoints.get((int)i)).x) continue;
                spaceStart = ((Point)spacePoints.get((int)i)).x;
                spaceEnd = ((Point)spacePoints.get((int)(i + 1))).x;
            }
            while (spaceEnd - spaceStart > this.maxBridgeWidth(this.width()) + 1) {
                if (Random.Int(2) == 0) {
                    ++spaceStart;
                    continue;
                }
                --spaceEnd;
            }
            this.spaceRect = new Rect(spaceStart + 1, this.top + 1, spaceEnd, this.bottom);
            int bridgeY = Random.NormalIntRange(this.spaceRect.top + 1, this.spaceRect.bottom - 2);
            this.bridgeRect = new Rect(this.spaceRect.left, bridgeY, this.spaceRect.right, bridgeY + 1);
        } else {
            spacePoints = new ArrayList();
            for (Room.Door door : this.connected.values()) {
                if (door.x != this.left && door.x != this.right) continue;
                spacePoints.add(door);
            }
            spacePoints.add(new Point(0, this.top + 1));
            spacePoints.add(new Point(0, this.bottom - 1));
            Collections.sort(spacePoints, new Comparator<Point>(){

                @Override
                public int compare(Point d1, Point d2) {
                    return d1.y - d2.y;
                }
            });
            int spaceStart = -1;
            int spaceEnd = -1;
            for (int i = 0; i < spacePoints.size() - 1; ++i) {
                if (spaceEnd - spaceStart >= ((Point)spacePoints.get((int)(i + 1))).y - ((Point)spacePoints.get((int)i)).y) continue;
                spaceStart = ((Point)spacePoints.get((int)i)).y;
                spaceEnd = ((Point)spacePoints.get((int)(i + 1))).y;
            }
            while (spaceEnd - spaceStart > this.maxBridgeWidth(this.height()) + 1) {
                if (Random.Int(2) == 0) {
                    ++spaceStart;
                    continue;
                }
                --spaceEnd;
            }
            this.spaceRect = new Rect(this.left + 1, spaceStart + 1, this.right, spaceEnd);
            int bridgeX = Random.NormalIntRange(this.spaceRect.left + 1, this.spaceRect.right - 2);
            this.bridgeRect = new Rect(bridgeX, this.spaceRect.top, bridgeX + 1, this.spaceRect.bottom);
        }
        Painter.fill(level, this.spaceRect, this.spaceTile());
        Painter.fill(level, this.bridgeRect, this.bridgeTile());
    }

    protected abstract int maxBridgeWidth(int var1);

    protected abstract int spaceTile();

    protected int bridgeTile() {
        return 14;
    }
}

