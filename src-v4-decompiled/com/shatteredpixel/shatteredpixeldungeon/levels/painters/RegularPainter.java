/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.watabou.noosa.Game;
import com.watabou.utils.Graph;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class RegularPainter
extends Painter {
    private float waterFill = 0.0f;
    private int waterSmoothness;
    private float grassFill = 0.0f;
    private int grassSmoothness;
    private int nTraps = 0;
    private Class<? extends Trap>[] trapClasses;
    private float[] trapChances;

    public RegularPainter setWater(float fill, int smoothness) {
        this.waterFill = fill;
        this.waterSmoothness = smoothness;
        return this;
    }

    public RegularPainter setGrass(float fill, int smoothness) {
        this.grassFill = fill;
        this.grassSmoothness = smoothness;
        return this;
    }

    public RegularPainter setTraps(int num, Class<?>[] classes, float[] chances) {
        this.nTraps = num;
        this.trapClasses = classes;
        this.trapChances = chances;
        return this;
    }

    protected int padding(Level level) {
        return level.feeling == Level.Feeling.CHASM ? 2 : 1;
    }

    @Override
    public boolean paint(Level level, ArrayList<Room> rooms) {
        if (rooms != null) {
            int padding = this.padding(level);
            int leftMost = Integer.MAX_VALUE;
            int topMost = Integer.MAX_VALUE;
            for (Room r : rooms) {
                if (r.left < leftMost) {
                    leftMost = r.left;
                }
                if (r.top >= topMost) continue;
                topMost = r.top;
            }
            leftMost -= padding;
            topMost -= padding;
            int rightMost = 0;
            int bottomMost = 0;
            for (Room r : rooms) {
                r.shift(-leftMost, -topMost);
                if (r.right > rightMost) {
                    rightMost = r.right;
                }
                if (r.bottom <= bottomMost) continue;
                bottomMost = r.bottom;
            }
            level.setSize((rightMost += padding) + 1, (bottomMost += padding) + 1);
        } else {
            if (level.length() == 0) {
                return false;
            }
            rooms = new ArrayList();
        }
        Random.shuffle(rooms);
        for (Room r : rooms.toArray(new Room[0])) {
            if (r.connected.isEmpty()) {
                Game.reportException(new RuntimeException("Painting a room with no connections! Room:" + r.getClass().getSimpleName() + " Seed:" + Dungeon.seed + " Depth:" + Dungeon.depth));
                if (r instanceof SpecialRoom) {
                    return false;
                }
            }
            this.placeDoors(r);
            r.paint(level);
        }
        this.paintDoors(level, rooms);
        Random.pushGenerator(Random.Long());
        if (this.waterFill > 0.0f) {
            this.paintWater(level, rooms);
        }
        if (this.grassFill > 0.0f) {
            this.paintGrass(level, rooms);
        }
        if (this.nTraps > 0) {
            this.paintTraps(level, rooms);
        }
        this.decorate(level, rooms);
        Random.popGenerator();
        return true;
    }

    protected abstract void decorate(Level var1, ArrayList<Room> var2);

    private void placeDoors(Room r) {
        for (Room n : r.connected.keySet()) {
            Room.Door door = r.connected.get(n);
            if (door != null) continue;
            Rect i = r.intersect(n);
            ArrayList<Point> doorSpots = new ArrayList<Point>();
            for (Point p : i.getPoints()) {
                if (!r.canConnect(p) || !n.canConnect(p)) continue;
                doorSpots.add(p);
            }
            if (doorSpots.isEmpty()) {
                ShatteredPixelDungeon.reportException(new RuntimeException("Could not place a door! r=" + r.getClass().getSimpleName() + " n=" + n.getClass().getSimpleName()));
                continue;
            }
            door = new Room.Door((Point)Random.element(doorSpots));
            r.connected.put(n, door);
            n.connected.put(r, door);
        }
    }

    public float hiddenDoorChance(Level l) {
        float hiddenDoorChance = 0.0f;
        if (Dungeon.depth > 1) {
            hiddenDoorChance = Math.min(1.0f, (float)Dungeon.depth / 20.0f);
        }
        if (l.feeling == Level.Feeling.SECRETS) {
            hiddenDoorChance = (0.5f + hiddenDoorChance) / 2.0f;
        }
        return hiddenDoorChance;
    }

    protected void paintDoors(Level l, ArrayList<Room> rooms) {
        float hiddenDoorChance = this.hiddenDoorChance(l);
        HashMap<Room, Room> roomMerges = new HashMap<Room, Room>();
        for (Room r : rooms) {
            for (Room n : r.connected.keySet()) {
                if (roomMerges.get(r) == n || roomMerges.get(n) == r) continue;
                if (!roomMerges.containsKey(r) && !roomMerges.containsKey(n) && this.mergeRooms(l, r, n, r.connected.get(n), 1)) {
                    if (((StandardRoom)r).sizeCat == StandardRoom.SizeCategory.NORMAL) {
                        roomMerges.put(r, n);
                    }
                    if (((StandardRoom)n).sizeCat != StandardRoom.SizeCategory.NORMAL) continue;
                    roomMerges.put(n, r);
                    continue;
                }
                Room.Door d = r.connected.get(n);
                int door = d.x + d.y * l.width();
                if (d.type == Room.Door.Type.REGULAR) {
                    if (Random.Float() < hiddenDoorChance) {
                        d.type = Room.Door.Type.HIDDEN;
                        if (l.feeling != Level.Feeling.SECRETS) {
                            Graph.buildDistanceMap(rooms, r);
                            if (n.distance == Integer.MAX_VALUE) {
                                d.type = Room.Door.Type.UNLOCKED;
                            }
                        } else {
                            int roomsInGraph = 0;
                            Graph.buildDistanceMap(rooms, r);
                            for (Room rDest : rooms) {
                                if (rDest.distance == Integer.MAX_VALUE || rDest instanceof ConnectionRoom) continue;
                                ++roomsInGraph;
                            }
                            if (roomsInGraph < 2) {
                                d.type = Room.Door.Type.UNLOCKED;
                            } else {
                                roomsInGraph = 0;
                                Graph.buildDistanceMap(rooms, n);
                                for (Room nDest : rooms) {
                                    if (nDest.distance == Integer.MAX_VALUE || nDest instanceof ConnectionRoom) continue;
                                    ++roomsInGraph;
                                }
                                if (roomsInGraph < 2) {
                                    d.type = Room.Door.Type.UNLOCKED;
                                }
                            }
                        }
                        Graph.buildDistanceMap(rooms, r);
                        if (l.feeling != Level.Feeling.SECRETS && n.distance == Integer.MAX_VALUE) {
                            d.type = Room.Door.Type.UNLOCKED;
                        }
                    } else {
                        d.type = Room.Door.Type.UNLOCKED;
                    }
                }
                if (d.type == Room.Door.Type.UNLOCKED && (r.isEntrance() || n.isEntrance()) && (Dungeon.depth == 1 && SPDSettings.intro() || Dungeon.depth == 2 && !Document.ADVENTURERS_GUIDE.isPageFound("Searching"))) {
                    d.type = Room.Door.Type.HIDDEN;
                }
                switch (d.type) {
                    case EMPTY: {
                        l.map[door] = 1;
                        break;
                    }
                    case TUNNEL: {
                        l.map[door] = l.tunnelTile();
                        break;
                    }
                    case WATER: {
                        l.map[door] = 29;
                        break;
                    }
                    case UNLOCKED: {
                        l.map[door] = 5;
                        break;
                    }
                    case HIDDEN: {
                        l.map[door] = 16;
                        break;
                    }
                    case BARRICADE: {
                        l.map[door] = 13;
                        break;
                    }
                    case LOCKED: {
                        l.map[door] = 10;
                        break;
                    }
                    case CRYSTAL: {
                        l.map[door] = 31;
                        break;
                    }
                    case WALL: {
                        l.map[door] = 4;
                    }
                }
            }
        }
    }

    protected boolean mergeRooms(Level l, Room r, Room n, Point start, int mergeTerrain) {
        Rect intersect = r.intersect(n);
        if (intersect.left == intersect.right) {
            Rect merge = new Rect();
            merge.left = merge.right = intersect.left;
            merge.bottom = start != null ? start.y : intersect.center().y;
            merge.top = merge.bottom;
            Point p = new Point(merge.left, merge.top);
            while (merge.top > intersect.top && n.canMerge(l, r, p, mergeTerrain) && r.canMerge(l, n, p, mergeTerrain)) {
                --merge.top;
                --p.y;
            }
            p.y = merge.bottom;
            while (merge.bottom < intersect.bottom && n.canMerge(l, r, p, mergeTerrain) && r.canMerge(l, n, p, mergeTerrain)) {
                ++merge.bottom;
                ++p.y;
            }
            if (merge.height() >= 3) {
                r.merge(l, n, new Rect(merge.left, merge.top + 1, merge.left + 1, merge.bottom), mergeTerrain);
                return true;
            }
            return false;
        }
        if (intersect.top == intersect.bottom) {
            Rect merge = new Rect();
            merge.right = start != null ? start.x : intersect.center().x;
            merge.left = merge.right;
            merge.top = merge.bottom = intersect.top;
            Point p = new Point(merge.left, merge.top);
            while (merge.left > intersect.left && n.canMerge(l, r, p, mergeTerrain) && r.canMerge(l, n, p, mergeTerrain)) {
                --merge.left;
                --p.x;
            }
            p.x = merge.right;
            while (merge.right < intersect.right && n.canMerge(l, r, p, mergeTerrain) && r.canMerge(l, n, p, mergeTerrain)) {
                ++merge.right;
                ++p.x;
            }
            if (merge.width() >= 3) {
                r.merge(l, n, new Rect(merge.left + 1, merge.top, merge.right, merge.top + 1), mergeTerrain);
                return true;
            }
            return false;
        }
        return false;
    }

    protected void paintWater(Level l, ArrayList<Room> rooms) {
        boolean[] lake = Patch.generate(l.width(), l.height(), this.waterFill, this.waterSmoothness, true);
        if (!rooms.isEmpty()) {
            for (Room r : rooms) {
                for (Point p : r.waterPlaceablePoints()) {
                    int i = l.pointToCell(p);
                    if (!lake[i] || l.map[i] != 1) continue;
                    l.map[i] = 29;
                }
            }
        } else {
            for (int i = 0; i < l.length(); ++i) {
                if (!lake[i] || l.map[i] != 1) continue;
                l.map[i] = 29;
            }
        }
    }

    protected void paintGrass(Level l, ArrayList<Room> rooms) {
        boolean[] grass = Patch.generate(l.width(), l.height(), this.grassFill, this.grassSmoothness, true);
        ArrayList<Integer> grassCells = new ArrayList<Integer>();
        if (!rooms.isEmpty()) {
            for (Room r : rooms) {
                for (Point p : r.grassPlaceablePoints()) {
                    int i = l.pointToCell(p);
                    if (!grass[i] || l.map[i] != 1) continue;
                    grassCells.add(i);
                }
            }
        } else {
            for (int i = 0; i < l.length(); ++i) {
                if (!grass[i] || l.map[i] != 1) continue;
                grassCells.add(i);
            }
        }
        Iterator<Room> iterator = grassCells.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)((Object)iterator.next());
            if (l.heaps.get(i) != null || l.findMob(i) != null) {
                l.map[i] = 2;
                continue;
            }
            int count = 1;
            for (int n : PathFinder.NEIGHBOURS8) {
                if (!grass[i + n]) continue;
                ++count;
            }
            l.map[i] = Random.Float() < (float)count / 12.0f ? 15 : 2;
        }
    }

    protected void paintTraps(Level l, ArrayList<Room> rooms) {
        ArrayList<Integer> validCells = new ArrayList<Integer>();
        if (!rooms.isEmpty()) {
            for (Room r : rooms) {
                for (Point p : r.trapPlaceablePoints()) {
                    int i = l.pointToCell(p);
                    if (l.map[i] != 1) continue;
                    validCells.add(i);
                }
            }
        } else {
            for (int i = 0; i < l.length(); ++i) {
                if (l.map[i] != 1) continue;
                validCells.add(i);
            }
        }
        this.nTraps = Math.min(this.nTraps, validCells.size() / 5);
        ArrayList<Integer> validNonHallways = new ArrayList<Integer>();
        for (int i = 0; i < l.length(); ++i) {
            l.passable[i] = (Terrain.flags[l.map[i]] & 1) != 0;
        }
        Iterator i = validCells.iterator();
        while (i.hasNext()) {
            int i2 = (Integer)i.next();
            if (!l.passable[i2 + PathFinder.CIRCLE4[0]] && !l.passable[i2 + PathFinder.CIRCLE4[2]] || !l.passable[i2 + PathFinder.CIRCLE4[1]] && !l.passable[i2 + PathFinder.CIRCLE4[3]]) continue;
            validNonHallways.add(i2);
        }
        this.nTraps = Math.min(this.nTraps, validCells.size() / 5);
        float revealedChance = TrapMechanism.revealHiddenTrapChance();
        float revealInc = 0.0f;
        for (int i3 = 0; i3 < (l.feeling == Level.Feeling.TRAPS ? 5 * this.nTraps : this.nTraps); ++i3) {
            Trap trap = Reflection.newInstance(this.trapClasses[Random.chances(this.trapChances)]);
            Integer trapPos = trap.avoidsHallways && !validNonHallways.isEmpty() ? (Integer)Random.element(validNonHallways) : (Integer)Random.element(validCells);
            validCells.remove(trapPos);
            validNonHallways.remove(trapPos);
            revealInc += revealedChance;
            if (i3 >= this.nTraps || revealInc >= 1.0f) {
                trap.reveal();
                revealInc -= 1.0f;
            } else {
                trap.hide();
            }
            l.setTrap(trap, trapPos);
            l.map[trapPos.intValue()] = trap.visible ? 18 : 17;
        }
    }
}

