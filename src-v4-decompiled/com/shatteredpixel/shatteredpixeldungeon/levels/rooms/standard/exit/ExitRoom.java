/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CaveExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CavesFissureExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CellBlockExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ChasmBridgeExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ChasmExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CircleBasinExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.CircleWallExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.HallwayExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.LibraryHallExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.LibraryRingExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.PillarsExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RegionDecoBridgeExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RegionDecoLineExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RegionDecoPatchExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RingExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RitualExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.RuinsExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.StatuesExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.WaterBridgeExitRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class ExitRoom
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
    public boolean isExit() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        int exit = level.pointToCell(this.random(2));
        Painter.set(level, exit, 8);
        level.transitions.add(new LevelTransition(level, exit, LevelTransition.Type.REGULAR_EXIT));
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.pointToCell(p) != l.exit();
    }

    public static StandardRoom createExit() {
        return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
    }

    static {
        rooms.add(WaterBridgeExitRoom.class);
        rooms.add(RegionDecoPatchExitRoom.class);
        rooms.add(RingExitRoom.class);
        rooms.add(CircleBasinExitRoom.class);
        rooms.add(RegionDecoLineExitRoom.class);
        rooms.add(ChasmBridgeExitRoom.class);
        rooms.add(PillarsExitRoom.class);
        rooms.add(CellBlockExitRoom.class);
        rooms.add(CaveExitRoom.class);
        rooms.add(RegionDecoBridgeExitRoom.class);
        rooms.add(CavesFissureExitRoom.class);
        rooms.add(CircleWallExitRoom.class);
        rooms.add(HallwayExitRoom.class);
        rooms.add(StatuesExitRoom.class);
        rooms.add(LibraryHallExitRoom.class);
        rooms.add(LibraryRingExitRoom.class);
        rooms.add(RegionDecoPatchExitRoom.class);
        rooms.add(RuinsExitRoom.class);
        rooms.add(ChasmExitRoom.class);
        rooms.add(RitualExitRoom.class);
        chances = new float[27][];
        ExitRoom.chances[1] = new float[]{4.0f, 3.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ExitRoom.chances[2] = new float[]{4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ExitRoom.chances[3] = chances[2];
        ExitRoom.chances[4] = ExitRoom.chances[3];
        ExitRoom.chances[5] = ExitRoom.chances[3];
        ExitRoom.chances[6] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ExitRoom.chances[7] = chances[6];
        ExitRoom.chances[8] = ExitRoom.chances[7];
        ExitRoom.chances[9] = ExitRoom.chances[7];
        ExitRoom.chances[10] = ExitRoom.chances[7];
        ExitRoom.chances[11] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ExitRoom.chances[12] = chances[11];
        ExitRoom.chances[13] = ExitRoom.chances[12];
        ExitRoom.chances[14] = ExitRoom.chances[12];
        ExitRoom.chances[15] = ExitRoom.chances[12];
        ExitRoom.chances[16] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ExitRoom.chances[17] = chances[16];
        ExitRoom.chances[18] = ExitRoom.chances[17];
        ExitRoom.chances[19] = ExitRoom.chances[17];
        ExitRoom.chances[20] = ExitRoom.chances[17];
        ExitRoom.chances[21] = new float[]{0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 4.0f, 3.0f, 2.0f, 1.0f};
        ExitRoom.chances[22] = chances[21];
        ExitRoom.chances[23] = ExitRoom.chances[22];
        ExitRoom.chances[24] = ExitRoom.chances[22];
        ExitRoom.chances[25] = ExitRoom.chances[22];
        ExitRoom.chances[26] = ExitRoom.chances[22];
    }
}

