/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.AquariumRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.BurnedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CavesFissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CellBlockRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleBasinRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CirclePitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CircleWallRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FissureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.GrassyGraveRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.HallwayRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.LibraryHallRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.LibraryRingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.MinefieldRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PillarsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlantsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlatformRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoLineRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RegionDecoPatchRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RitualRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.RuinsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedLibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SegmentedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SewerPipeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SkullsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StatuesRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StripedRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StudyRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.SuspiciousChestRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.WaterBridgeRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public abstract class StandardRoom
extends Room {
    public SizeCategory sizeCat;
    private static ArrayList<Class<? extends StandardRoom>> rooms = new ArrayList();
    private static float[][] chances;

    public StandardRoom() {
        this.setSizeCat();
    }

    public float[] sizeCatProbs() {
        return new float[]{1.0f, 0.0f, 0.0f};
    }

    public boolean setSizeCat() {
        return this.setSizeCat(0, SizeCategory.values().length - 1);
    }

    public boolean setSizeCat(int maxRoomValue) {
        return this.setSizeCat(0, maxRoomValue - 1);
    }

    public boolean setSizeCat(int minOrdinal, int maxOrdinal) {
        int i;
        SizeCategory[] categories;
        float[] probs = this.sizeCatProbs();
        if (probs.length != (categories = SizeCategory.values()).length) {
            return false;
        }
        for (i = 0; i < minOrdinal; ++i) {
            probs[i] = 0.0f;
        }
        for (i = maxOrdinal + 1; i < categories.length; ++i) {
            probs[i] = 0.0f;
        }
        int ordinal = Random.chances(probs);
        if (ordinal != -1) {
            this.sizeCat = categories[ordinal];
            return true;
        }
        return false;
    }

    @Override
    public int minWidth() {
        return this.sizeCat.minDim;
    }

    @Override
    public int maxWidth() {
        return this.sizeCat.maxDim;
    }

    @Override
    public int minHeight() {
        return this.sizeCat.minDim;
    }

    @Override
    public int maxHeight() {
        return this.sizeCat.maxDim;
    }

    public int sizeFactor() {
        return this.sizeCat.roomValue;
    }

    public int mobSpawnWeight() {
        if (this.isEntrance()) {
            return 1;
        }
        return this.sizeFactor();
    }

    public int connectionWeight() {
        return this.sizeFactor() * this.sizeFactor();
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        int cell = l.pointToCell(this.pointInside(p, 1));
        return (Terrain.flags[l.map[cell]] & 0x10) == 0;
    }

    public static StandardRoom createRoom() {
        return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
    }

    static {
        rooms.add(SewerPipeRoom.class);
        rooms.add(RingRoom.class);
        rooms.add(WaterBridgeRoom.class);
        rooms.add(RegionDecoPatchRoom.class);
        rooms.add(CircleBasinRoom.class);
        rooms.add(RegionDecoLineRoom.class);
        rooms.add(SegmentedRoom.class);
        rooms.add(PillarsRoom.class);
        rooms.add(ChasmBridgeRoom.class);
        rooms.add(CellBlockRoom.class);
        rooms.add(CaveRoom.class);
        rooms.add(RegionDecoBridgeRoom.class);
        rooms.add(CavesFissureRoom.class);
        rooms.add(CirclePitRoom.class);
        rooms.add(CircleWallRoom.class);
        rooms.add(HallwayRoom.class);
        rooms.add(LibraryHallRoom.class);
        rooms.add(LibraryRingRoom.class);
        rooms.add(StatuesRoom.class);
        rooms.add(SegmentedLibraryRoom.class);
        rooms.add(RuinsRoom.class);
        rooms.add(RegionDecoPatchRoom.class);
        rooms.add(ChasmRoom.class);
        rooms.add(SkullsRoom.class);
        rooms.add(RitualRoom.class);
        rooms.add(PlantsRoom.class);
        rooms.add(AquariumRoom.class);
        rooms.add(PlatformRoom.class);
        rooms.add(BurnedRoom.class);
        rooms.add(FissureRoom.class);
        rooms.add(GrassyGraveRoom.class);
        rooms.add(StripedRoom.class);
        rooms.add(StudyRoom.class);
        rooms.add(SuspiciousChestRoom.class);
        rooms.add(MinefieldRoom.class);
        chances = new float[27][];
        StandardRoom.chances[1] = new float[]{16.0f, 8.0f, 8.0f, 4.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f};
        StandardRoom.chances[2] = new float[]{16.0f, 8.0f, 8.0f, 4.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        StandardRoom.chances[3] = chances[2];
        StandardRoom.chances[4] = StandardRoom.chances[3];
        StandardRoom.chances[5] = new float[]{16.0f, 8.0f, 8.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        StandardRoom.chances[6] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 10.0f, 10.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        StandardRoom.chances[7] = chances[6];
        StandardRoom.chances[8] = StandardRoom.chances[7];
        StandardRoom.chances[9] = StandardRoom.chances[7];
        StandardRoom.chances[10] = StandardRoom.chances[7];
        StandardRoom.chances[11] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 16.0f, 8.0f, 8.0f, 4.0f, 4.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        StandardRoom.chances[12] = chances[11];
        StandardRoom.chances[13] = StandardRoom.chances[12];
        StandardRoom.chances[14] = StandardRoom.chances[12];
        StandardRoom.chances[15] = StandardRoom.chances[12];
        StandardRoom.chances[16] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 10.0f, 10.0f, 5.0f, 5.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        StandardRoom.chances[17] = chances[16];
        StandardRoom.chances[18] = StandardRoom.chances[17];
        StandardRoom.chances[19] = StandardRoom.chances[17];
        StandardRoom.chances[20] = StandardRoom.chances[17];
        StandardRoom.chances[21] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 10.0f, 10.0f, 10.0f, 5.0f, 5.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
        StandardRoom.chances[22] = chances[21];
        StandardRoom.chances[23] = StandardRoom.chances[22];
        StandardRoom.chances[24] = StandardRoom.chances[22];
        StandardRoom.chances[25] = StandardRoom.chances[22];
        StandardRoom.chances[26] = StandardRoom.chances[22];
    }

    public static enum SizeCategory {
        NORMAL(4, 10, 1),
        LARGE(10, 14, 2),
        GIANT(14, 18, 3);

        public final int minDim;
        public final int maxDim;
        public final int roomValue;

        private SizeCategory(int min, int max, int val) {
            this.minDim = min;
            this.maxDim = max;
            this.roomValue = val;
        }
    }
}

