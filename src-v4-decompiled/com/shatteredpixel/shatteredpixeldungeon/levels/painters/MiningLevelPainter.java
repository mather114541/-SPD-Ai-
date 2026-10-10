/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineSecretRoom;
import com.watabou.utils.Graph;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.HashMap;

public class MiningLevelPainter
extends CavesPainter {
    private int goldToAdd = 0;

    @Override
    protected int padding(Level level) {
        return 3;
    }

    public RegularPainter setGold(int amount) {
        this.goldToAdd = amount;
        return this;
    }

    @Override
    protected void generateGold(Level level, ArrayList<Room> rooms) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 12) continue;
            --this.goldToAdd;
        }
        for (Heap h : level.heaps.valueList()) {
            for (Item i : h.items) {
                if (!(i instanceof DarkGold)) continue;
                this.goldToAdd -= i.quantity();
            }
        }
        int[] map = level.map;
        do {
            Random.shuffle(rooms);
            for (Room r : rooms) {
                if (r instanceof MineSecretRoom) continue;
                ArrayList<Integer> goldPosCandidates = new ArrayList<Integer>();
                block5: for (Point p : r.getPoints()) {
                    int i = level.pointToCell(p);
                    if (!level.insideMap(i) || this.goldToAdd <= 0 || map[i] != 4) continue;
                    for (int j : PathFinder.NEIGHBOURS4) {
                        Point o;
                        if (!level.insideMap(i + j) || map[i + j] == 4 || !r.inside(o = level.cellToPoint(i + j))) continue;
                        goldPosCandidates.add(i);
                        continue block5;
                    }
                }
                if (this.goldToAdd <= 0 || goldPosCandidates.isEmpty()) continue;
                int pos = (Integer)Random.element(goldPosCandidates);
                map[pos] = 12;
                --this.goldToAdd;
                if (this.goldToAdd <= 0) continue;
                int i = PathFinder.NEIGHBOURS4[Random.Int(4)];
                if (level.insideMap(pos + i) && map[pos + i] == 4) {
                    map[pos + i] = 12;
                    --this.goldToAdd;
                }
                if (Random.Int(2) != 0 || !level.insideMap(pos + (i = PathFinder.NEIGHBOURS4[Random.Int(4)])) || map[pos + i] != 4) continue;
                map[pos + i] = 12;
                --this.goldToAdd;
            }
        } while (this.goldToAdd > 0);
    }

    @Override
    protected void paintDoors(Level l, ArrayList<Room> rooms) {
        HashMap<Room, Room> roomMerges = new HashMap<Room, Room>();
        float hiddenDoorChance = 0.9f;
        for (Room r : rooms) {
            for (Room n : r.connected.keySet()) {
                Room.Door d = r.connected.get(n);
                int door = d.x + d.y * l.width();
                if (d.type == Room.Door.Type.WALL || d.type == Room.Door.Type.HIDDEN) {
                    l.map[door] = 4;
                } else if (Random.Float() < hiddenDoorChance) {
                    d.type = Room.Door.Type.HIDDEN;
                    Graph.buildDistanceMap(rooms, r);
                    if (n.distance == Integer.MAX_VALUE) {
                        l.map[door] = 1;
                        d.type = Room.Door.Type.EMPTY;
                    } else {
                        l.map[door] = 4;
                    }
                } else {
                    l.map[door] = 1;
                    d.type = Room.Door.Type.EMPTY;
                }
                if (l.map[door] != 1 || roomMerges.get(r) == n || roomMerges.get(n) == r || !this.mergeRooms(l, r, n, r.connected.get(n), 1)) continue;
                roomMerges.put(r, n);
                roomMerges.put(n, r);
            }
        }
    }

    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        super.decorate(level, rooms);
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 0) continue;
            level.map[i] = 1;
        }
    }
}

