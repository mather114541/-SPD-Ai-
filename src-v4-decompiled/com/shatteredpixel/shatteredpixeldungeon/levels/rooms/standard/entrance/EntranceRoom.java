/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.Guidebook;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.CaveEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.CavesFissureEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.CellBlockEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.ChasmBridgeEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.ChasmEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.CircleBasinEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.HallwayEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.LibraryHallEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.LibraryRingEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.PillarsEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RegionDecoBridgeEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RegionDecoLineEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RegionDecoPatchEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RingEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RitualEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.RuinsEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.StatuesEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.WaterBridgeEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CircleWallEntranceRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class EntranceRoom
extends StandardRoom {
    private static ArrayList<Class<? extends StandardRoom>> rooms = new ArrayList();
    private static float[][] chances;

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 5);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 5);
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        if (Dungeon.depth <= 2) {
            return false;
        }
        return super.canMerge(l, other, p, mergeTerrain);
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        if (Dungeon.depth == 1) {
            return false;
        }
        return super.canPlaceTrap(p);
    }

    @Override
    public void paint(Level level) {
        int entrance;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        while (level.findMob(entrance = level.pointToCell(this.random(2))) != null) {
        }
        Painter.set(level, entrance, 7);
        if (Dungeon.depth == 1) {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.SURFACE));
        } else {
            level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        }
        EntranceRoom.placeEarlyGuidePages(level, this);
    }

    public static void placeEarlyGuidePages(Level level, Room r) {
        int pos;
        Random.pushGenerator();
        if (Dungeon.depth == 1 && (!Document.ADVENTURERS_GUIDE.isPageRead("Intro") || SPDSettings.intro())) {
            while ((pos = level.pointToCell(new Point(Random.IntRange(r.left + 1, r.right - 1), Random.IntRange(r.top + 1, r.bottom - 2)))) == level.entrance() || level.map[pos] == 33) {
            }
            level.drop(new Guidebook(), pos);
            Document.ADVENTURERS_GUIDE.deletePage("Intro");
        }
        if (Dungeon.depth == 2 && !Document.ADVENTURERS_GUIDE.isPageFound("Searching")) {
            while ((pos = level.pointToCell(new Point(Random.IntRange(r.left + 1, r.right - 1), Random.IntRange(r.top + 1, r.bottom - 2)))) == level.entrance() || level.map[pos] == 33) {
            }
            GuidePage p = new GuidePage();
            p.page("Searching");
            level.drop(p, pos);
        }
        Random.popGenerator();
    }

    public static StandardRoom createEntrance() {
        return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
    }

    static {
        rooms.add(WaterBridgeEntranceRoom.class);
        rooms.add(RegionDecoPatchEntranceRoom.class);
        rooms.add(RingEntranceRoom.class);
        rooms.add(CircleBasinEntranceRoom.class);
        rooms.add(RegionDecoLineEntranceRoom.class);
        rooms.add(ChasmBridgeEntranceRoom.class);
        rooms.add(PillarsEntranceRoom.class);
        rooms.add(CellBlockEntranceRoom.class);
        rooms.add(CaveEntranceRoom.class);
        rooms.add(RegionDecoBridgeEntranceRoom.class);
        rooms.add(CavesFissureEntranceRoom.class);
        rooms.add(CircleWallEntranceRoom.class);
        rooms.add(HallwayEntranceRoom.class);
        rooms.add(StatuesEntranceRoom.class);
        rooms.add(LibraryHallEntranceRoom.class);
        rooms.add(LibraryRingEntranceRoom.class);
        rooms.add(RegionDecoPatchEntranceRoom.class);
        rooms.add(RuinsEntranceRoom.class);
        rooms.add(ChasmEntranceRoom.class);
        rooms.add(RitualEntranceRoom.class);
        chances = new float[27][];
        EntranceRoom.chances[1] = new float[]{4.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        EntranceRoom.chances[2] = chances[1];
        EntranceRoom.chances[3] = new float[]{4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        EntranceRoom.chances[4] = chances[3];
        EntranceRoom.chances[5] = EntranceRoom.chances[4];
        EntranceRoom.chances[6] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        EntranceRoom.chances[7] = chances[6];
        EntranceRoom.chances[8] = EntranceRoom.chances[7];
        EntranceRoom.chances[9] = EntranceRoom.chances[7];
        EntranceRoom.chances[10] = EntranceRoom.chances[7];
        EntranceRoom.chances[11] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        EntranceRoom.chances[12] = chances[11];
        EntranceRoom.chances[13] = EntranceRoom.chances[12];
        EntranceRoom.chances[14] = EntranceRoom.chances[12];
        EntranceRoom.chances[15] = EntranceRoom.chances[12];
        EntranceRoom.chances[16] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        EntranceRoom.chances[17] = chances[16];
        EntranceRoom.chances[18] = EntranceRoom.chances[17];
        EntranceRoom.chances[19] = EntranceRoom.chances[17];
        EntranceRoom.chances[20] = EntranceRoom.chances[17];
        EntranceRoom.chances[21] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f};
        EntranceRoom.chances[22] = chances[21];
        EntranceRoom.chances[23] = EntranceRoom.chances[22];
        EntranceRoom.chances[24] = EntranceRoom.chances[22];
        EntranceRoom.chances[25] = EntranceRoom.chances[22];
        EntranceRoom.chances[26] = EntranceRoom.chances[22];
    }
}

