/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.builders;

import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Point;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import com.watabou.utils.SparseArray;
import java.util.ArrayList;

public class GridBuilder
extends Builder {
    public static int ROOM_SIZE = 11;
    protected float extraConnectionChance = 0.55f;

    @Override
    public ArrayList<Room> build(ArrayList<Room> rooms) {
        int entryY;
        int entryX;
        for (Room r : rooms) {
            r.setEmpty();
        }
        Room entrance = null;
        Room exit = null;
        for (Room r : rooms) {
            if (r.isEntrance()) {
                entrance = r;
                continue;
            }
            if (!r.isExit()) continue;
            exit = r;
        }
        if (!entrance.forceSize(ROOM_SIZE, ROOM_SIZE)) {
            throw new RuntimeException("rigid room sizes for now!");
        }
        entrance.setPos(0, 0);
        ArrayList<Room> toPlace = new ArrayList<Room>();
        ArrayList<Room> multis = new ArrayList<Room>();
        ArrayList<Room> singles = new ArrayList<Room>();
        for (Room r : rooms) {
            if (r.maxConnections(0) == 1) {
                singles.add(r);
                continue;
            }
            multis.add(r);
        }
        int maxWidth = 0;
        int maxHeight = 0;
        while ((float)(maxWidth * maxHeight) < (float)rooms.size() * 1.25f) {
            if (maxWidth < maxHeight || maxWidth == maxHeight && Random.Int(2) == 0) {
                ++maxWidth;
                continue;
            }
            ++maxHeight;
        }
        ArrayList<Room> placed = new ArrayList<Room>();
        if (!multis.isEmpty()) {
            toPlace.add((Room)multis.remove(0));
        }
        if (!multis.isEmpty()) {
            toPlace.add((Room)multis.remove(0));
        }
        while (!multis.isEmpty() || !singles.isEmpty()) {
            if (!multis.isEmpty()) {
                toPlace.add((Room)multis.remove(0));
            }
            if (!multis.isEmpty()) {
                toPlace.add((Room)multis.remove(0));
            }
            if (!multis.isEmpty()) {
                toPlace.add((Room)multis.remove(0));
            }
            if (singles.isEmpty()) continue;
            toPlace.add((Room)singles.remove(0));
        }
        toPlace.remove(entrance);
        switch (Random.Int(4)) {
            default: {
                entryX = 0;
                entryY = Random.Int(0, maxHeight);
                break;
            }
            case 1: {
                entryX = Random.Int(0, maxWidth);
                entryY = 0;
                break;
            }
            case 2: {
                entryX = maxWidth - 1;
                entryY = Random.Int(0, maxHeight);
                break;
            }
            case 3: {
                entryX = Random.Int(0, maxWidth);
                entryY = maxHeight - 1;
            }
        }
        entrance.setPos(entryX * (ROOM_SIZE - 1), entryY * (ROOM_SIZE - 1));
        placed.add(entrance);
        if (exit != null) {
            toPlace.remove(exit);
            toPlace.add(exit);
        }
        PointF aimCenter = new PointF((float)maxWidth / 2.0f, (float)maxHeight / 2.0f);
        SparseArray<Room> gridCells = new SparseArray<Room>();
        gridCells.put(this.getIdx(entryX, entryY), entrance);
        int roomPlacementFailures = 0;
        block16: while (!toPlace.isEmpty()) {
            Room r = (Room)toPlace.remove(0);
            int cellWidth = 1;
            int cellHeight = 1;
            if (!r.forceSize(ROOM_SIZE, ROOM_SIZE)) {
                if (!r.forceSize(2 * ROOM_SIZE - 1, 2 * ROOM_SIZE - 1)) {
                    if (!r.forceSize(ROOM_SIZE, 2 * ROOM_SIZE - 1)) {
                        if (!r.forceSize(2 * ROOM_SIZE - 1, ROOM_SIZE)) {
                            throw new RuntimeException("rigid room sizes for now!");
                        }
                        cellWidth = 2;
                        cellHeight = 1;
                    } else {
                        cellWidth = 1;
                        cellHeight = 2;
                    }
                } else {
                    cellHeight = 2;
                    cellWidth = 2;
                }
            }
            int tries = 0;
            do {
                boolean valid;
                int nIdx;
                Room n;
                r.neigbours.clear();
                if (++tries > 100) {
                    toPlace.add(Math.min(2, toPlace.size()), r);
                    if (++roomPlacementFailures <= 100) continue block16;
                    return null;
                }
                if (placed.size() < 3) {
                    n = entrance;
                    nIdx = this.getIdx(entryX, entryY);
                } else {
                    int[] keys = gridCells.keyArray();
                    nIdx = keys[Random.Int(keys.length)];
                    n = gridCells.get(nIdx, null);
                }
                int rIdx = nIdx;
                float xDiff = aimCenter.x - (float)(rIdx % 1000 - 100);
                float yDiff = aimCenter.y - (float)(rIdx / 1000 - 100);
                float dist = (float)Math.sqrt(Math.pow(xDiff, 2.0) + Math.pow(yDiff, 2.0));
                if (Random.Float(12.0f) < 8.0f + dist || placed.size() == 1) {
                    rIdx = Math.abs(xDiff) >= Math.abs(yDiff) ? (xDiff > 0.0f ? ++rIdx : --rIdx) : (yDiff > 0.0f ? (rIdx += 1000) : (rIdx -= 1000));
                } else {
                    switch (Random.Int(4)) {
                        case 0: {
                            ++rIdx;
                            break;
                        }
                        case 1: {
                            rIdx -= 1000;
                            break;
                        }
                        case 2: {
                            --rIdx;
                            break;
                        }
                        case 3: {
                            rIdx += 1000;
                        }
                    }
                }
                int x = rIdx % 1000 - 100;
                int y = rIdx / 1000 - 100;
                if (x < 0 || x >= maxHeight || y < 0 || y >= maxHeight) {
                    valid = false;
                } else if (gridCells.containsKey(rIdx)) {
                    valid = false;
                } else if (cellWidth > 1 || cellHeight > 1) {
                    Rect space = this.findFreeGridSpace(new Point(x, y), gridCells, cellWidth, cellHeight);
                    if (cellWidth * cellHeight <= 2) {
                        space.left = Math.max(0, space.left);
                        space.top = Math.max(0, space.top);
                        space.right = Math.min(maxWidth - 1, space.right);
                        space.bottom = Math.min(maxHeight - 1, space.bottom);
                    }
                    int excessWidth = space.width() + 1 - cellWidth;
                    int excessHeight = space.height() + 1 - cellHeight;
                    boolean bl = valid = excessWidth >= 0 && excessHeight >= 0;
                    if (valid) {
                        x = space.left + Random.Int(excessWidth + 1);
                        y = space.top + Random.Int(excessHeight + 1);
                        rIdx = this.getIdx(x, y);
                    }
                } else {
                    valid = true;
                }
                if (valid) {
                    r.setPos(x * (ROOM_SIZE - 1), y * (ROOM_SIZE - 1));
                    for (Room other : placed) {
                        Rect i;
                        if (other.getClass() != r.getClass() || (i = r.intersect(other)).width() <= 0 && i.height() <= 0) continue;
                        valid = false;
                    }
                }
                if (!valid || !r.connect(n)) continue;
                placed.add(r);
                for (int i = 0; i < cellWidth; ++i) {
                    for (int j = 0; j < cellHeight; ++j) {
                        gridCells.put(rIdx + i + j * 1000, r);
                    }
                }
            } while (!placed.contains(r));
        }
        GridBuilder.findNeighbours(rooms);
        for (Room r : rooms) {
            for (Room n : r.neigbours) {
                if (n.connected.containsKey(r) || !(Random.Float() < this.extraConnectionChance)) continue;
                r.connect(n);
            }
        }
        return rooms;
    }

    public Rect findFreeGridSpace(Point start, SparseArray<Room> collision, int maxWidth, int maxHeight) {
        Rect space = new Rect(start.x, start.y, start.x, start.y);
        boolean expanded = true;
        while (expanded) {
            int x;
            int y;
            boolean valid;
            expanded = false;
            if (space.left > start.x - (maxWidth - 1)) {
                valid = true;
                for (y = space.top; y <= space.bottom; ++y) {
                    if (!collision.containsKey(this.getIdx(space.left - 1, y))) continue;
                    valid = false;
                    break;
                }
                if (valid) {
                    --space.left;
                    expanded = true;
                }
            }
            if (space.top > start.y - (maxHeight - 1)) {
                valid = true;
                for (x = space.left; x <= space.right; ++x) {
                    if (!collision.containsKey(this.getIdx(x, space.top - 1))) continue;
                    valid = false;
                    break;
                }
                if (valid) {
                    --space.top;
                    expanded = true;
                }
            }
            if (space.right < start.x + (maxWidth - 1)) {
                valid = true;
                for (y = space.top; y <= space.bottom; ++y) {
                    if (!collision.containsKey(this.getIdx(space.right + 1, y))) continue;
                    valid = false;
                    break;
                }
                if (valid) {
                    ++space.right;
                    expanded = true;
                }
            }
            if (space.bottom >= start.y + (maxHeight - 1)) continue;
            valid = true;
            for (x = space.left; x <= space.right; ++x) {
                if (!collision.containsKey(this.getIdx(x, space.bottom + 1))) continue;
                valid = false;
                break;
            }
            if (!valid) continue;
            ++space.bottom;
            expanded = true;
        }
        return space;
    }

    private int getIdx(Point p) {
        return p.x + 1000 * p.y + 100;
    }

    private int getIdx(int x, int y) {
        return x + 100 + 1000 * (y + 100);
    }
}

