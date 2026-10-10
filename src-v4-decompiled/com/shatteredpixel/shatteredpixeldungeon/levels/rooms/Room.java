/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Graph;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public abstract class Room
extends Rect
implements Graph.Node,
Bundlable {
    public ArrayList<Room> neigbours = new ArrayList();
    public LinkedHashMap<Room, Door> connected = new LinkedHashMap();
    public int distance;
    public int price = 1;
    public static final int ALL = 0;
    public static final int LEFT = 1;
    public static final int TOP = 2;
    public static final int RIGHT = 3;
    public static final int BOTTOM = 4;

    public Room() {
    }

    public Room(Rect other) {
        super(other);
    }

    public Room set(Room other) {
        super.set(other);
        for (Room r : other.neigbours) {
            this.neigbours.add(r);
            r.neigbours.remove(other);
            r.neigbours.add(this);
        }
        for (Room r : other.connected.keySet()) {
            Door d = other.connected.get(r);
            r.connected.remove(other);
            r.connected.put(this, d);
            this.connected.put(r, d);
        }
        return this;
    }

    public int minWidth() {
        return -1;
    }

    public int maxWidth() {
        return -1;
    }

    public int minHeight() {
        return -1;
    }

    public int maxHeight() {
        return -1;
    }

    public boolean setSize() {
        return this.setSize(this.minWidth(), this.maxWidth(), this.minHeight(), this.maxHeight());
    }

    public boolean forceSize(int w, int h) {
        return this.setSize(w, w, h, h);
    }

    public boolean setSizeWithLimit(int w, int h) {
        if (w < this.minWidth() || h < this.minHeight()) {
            return false;
        }
        this.setSize();
        if (this.width() > w || this.height() > h) {
            this.resize(Math.min(this.width(), w) - 1, Math.min(this.height(), h) - 1);
        }
        return true;
    }

    protected boolean setSize(int minW, int maxW, int minH, int maxH) {
        if (minW < this.minWidth() || maxW > this.maxWidth() || minH < this.minHeight() || maxH > this.maxHeight() || minW > maxW || minH > maxH) {
            return false;
        }
        this.resize(Random.NormalIntRange(minW, maxW) - 1, Random.NormalIntRange(minH, maxH) - 1);
        return true;
    }

    public Point pointInside(Point from, int n) {
        Point step = new Point(from);
        if (from.x == this.left) {
            step.offset(n, 0);
        } else if (from.x == this.right) {
            step.offset(-n, 0);
        } else if (from.y == this.top) {
            step.offset(0, n);
        } else if (from.y == this.bottom) {
            step.offset(0, -n);
        }
        return step;
    }

    @Override
    public int width() {
        return super.width() + 1;
    }

    @Override
    public int height() {
        return super.height() + 1;
    }

    public Point random() {
        return this.random(1);
    }

    public Point random(int m) {
        return new Point(Random.IntRange(this.left + m, this.right - m), Random.IntRange(this.top + m, this.bottom - m));
    }

    @Override
    public boolean inside(Point p) {
        return p.x > this.left && p.y > this.top && p.x < this.right && p.y < this.bottom;
    }

    @Override
    public Point center() {
        return new Point((this.left + this.right) / 2 + ((this.right - this.left) % 2 == 1 ? Random.Int(2) : 0), (this.top + this.bottom) / 2 + ((this.bottom - this.top) % 2 == 1 ? Random.Int(2) : 0));
    }

    public int minConnections(int direction) {
        if (direction == 0) {
            return 1;
        }
        return 0;
    }

    public int curConnections(int direction) {
        if (direction == 0) {
            return this.connected.size();
        }
        int total = 0;
        for (Room r : this.connected.keySet()) {
            Rect i = this.intersect(r);
            if (direction == 1 && i.width() == 0 && i.left == this.left) {
                ++total;
                continue;
            }
            if (direction == 2 && i.height() == 0 && i.top == this.top) {
                ++total;
                continue;
            }
            if (direction == 3 && i.width() == 0 && i.right == this.right) {
                ++total;
                continue;
            }
            if (direction != 4 || i.height() != 0 || i.bottom != this.bottom) continue;
            ++total;
        }
        return total;
    }

    public int remConnections(int direction) {
        if (this.curConnections(0) >= this.maxConnections(0)) {
            return 0;
        }
        return this.maxConnections(direction) - this.curConnections(direction);
    }

    public int maxConnections(int direction) {
        if (direction == 0) {
            return 16;
        }
        return 4;
    }

    public boolean canConnect(Point p) {
        return (p.x == this.left || p.x == this.right) != (p.y == this.top || p.y == this.bottom);
    }

    public boolean canConnect(int direction) {
        return this.remConnections(direction) > 0;
    }

    public boolean canConnect(Room r) {
        if (this.isExit() && r.isEntrance() || this.isEntrance() && r.isExit()) {
            return false;
        }
        Rect i = this.intersect(r);
        boolean foundPoint = false;
        for (Point p : i.getPoints()) {
            if (!this.canConnect(p) || !r.canConnect(p)) continue;
            foundPoint = true;
            break;
        }
        if (!foundPoint) {
            return false;
        }
        if (i.width() == 0 && i.left == this.left) {
            return this.canConnect(1) && r.canConnect(3);
        }
        if (i.height() == 0 && i.top == this.top) {
            return this.canConnect(2) && r.canConnect(4);
        }
        if (i.width() == 0 && i.right == this.right) {
            return this.canConnect(3) && r.canConnect(1);
        }
        if (i.height() == 0 && i.bottom == this.bottom) {
            return this.canConnect(4) && r.canConnect(2);
        }
        return false;
    }

    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return false;
    }

    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        Painter.fill(l, merge, mergeTerrain);
    }

    public boolean addNeigbour(Room other) {
        if (this.neigbours.contains(other)) {
            return true;
        }
        Rect i = this.intersect(other);
        if (i.width() == 0 && i.height() >= 2 || i.height() == 0 && i.width() >= 2) {
            this.neigbours.add(other);
            other.neigbours.add(this);
            return true;
        }
        return false;
    }

    public boolean connect(Room room) {
        if ((this.neigbours.contains(room) || this.addNeigbour(room)) && !this.connected.containsKey(room) && this.canConnect(room)) {
            this.connected.put(room, null);
            room.connected.put(this, null);
            return true;
        }
        return false;
    }

    public void clearConnections() {
        for (Room r : this.neigbours) {
            r.neigbours.remove(this);
        }
        this.neigbours.clear();
        for (Room r : this.connected.keySet()) {
            r.connected.remove(this);
        }
        this.connected.clear();
    }

    public boolean isEntrance() {
        return false;
    }

    public boolean isExit() {
        return false;
    }

    public abstract void paint(Level var1);

    public boolean canPlaceWater(Point p) {
        return true;
    }

    public final ArrayList<Point> waterPlaceablePoints() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                Point p = new Point(i, j);
                if (!this.canPlaceWater(p)) continue;
                points.add(p);
            }
        }
        return points;
    }

    public boolean canPlaceGrass(Point p) {
        return true;
    }

    public final ArrayList<Point> grassPlaceablePoints() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                Point p = new Point(i, j);
                if (!this.canPlaceGrass(p)) continue;
                points.add(p);
            }
        }
        return points;
    }

    public boolean canPlaceTrap(Point p) {
        return true;
    }

    public final ArrayList<Point> trapPlaceablePoints() {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                Point p = new Point(i, j);
                if (!this.canPlaceTrap(p)) continue;
                points.add(p);
            }
        }
        return points;
    }

    public boolean canPlaceItem(Point p, Level l) {
        return this.inside(p);
    }

    public final ArrayList<Point> itemPlaceablePoints(Level l) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                Point p = new Point(i, j);
                if (!this.canPlaceItem(p, l)) continue;
                points.add(p);
            }
        }
        return points;
    }

    public boolean canPlaceCharacter(Point p, Level l) {
        return this.inside(p);
    }

    public final ArrayList<Point> charPlaceablePoints(Level l) {
        ArrayList<Point> points = new ArrayList<Point>();
        for (int i = this.left; i <= this.right; ++i) {
            for (int j = this.top; j <= this.bottom; ++j) {
                Point p = new Point(i, j);
                if (!this.canPlaceCharacter(p, l)) continue;
                points.add(p);
            }
        }
        return points;
    }

    @Override
    public int distance() {
        return this.distance;
    }

    @Override
    public void distance(int value) {
        this.distance = value;
    }

    @Override
    public int price() {
        return this.price;
    }

    @Override
    public void price(int value) {
        this.price = value;
    }

    public Collection<Room> edges() {
        ArrayList<Room> edges = new ArrayList<Room>();
        for (Room r : this.connected.keySet()) {
            Door d = this.connected.get(r);
            if (d.type != Door.Type.EMPTY && d.type != Door.Type.TUNNEL && d.type != Door.Type.UNLOCKED && d.type != Door.Type.REGULAR) continue;
            edges.add(r);
        }
        return edges;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put("left", this.left);
        bundle.put("top", this.top);
        bundle.put("right", this.right);
        bundle.put("bottom", this.bottom);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        this.left = bundle.getInt("left");
        this.top = bundle.getInt("top");
        this.right = bundle.getInt("right");
        this.bottom = bundle.getInt("bottom");
    }

    public void onLevelLoad(Level level) {
    }

    public static class Door
    extends Point
    implements Bundlable {
        public Type type = Type.EMPTY;
        private boolean typeLocked = false;

        public Door() {
        }

        public Door(Point p) {
            super(p);
        }

        public Door(int x, int y) {
            super(x, y);
        }

        public void lockTypeChanges(boolean lock) {
            this.typeLocked = lock;
        }

        public void set(Type type) {
            if (!this.typeLocked && type.compareTo(this.type) > 0) {
                this.type = type;
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put("x", this.x);
            bundle.put("y", this.y);
            bundle.put("type", this.type);
            bundle.put("type_locked", this.typeLocked);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            this.x = bundle.getInt("x");
            this.y = bundle.getInt("y");
            this.type = bundle.getEnum("type", Type.class);
            this.typeLocked = bundle.getBoolean("type_locked");
        }

        public static enum Type {
            EMPTY,
            TUNNEL,
            WATER,
            REGULAR,
            UNLOCKED,
            HIDDEN,
            BARRICADE,
            LOCKED,
            CRYSTAL,
            WALL;

        }
    }
}

