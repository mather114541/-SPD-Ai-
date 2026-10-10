/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.BridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.PerimeterRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.RingBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.RingTunnelRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.TunnelRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.WalkwayRoom;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public abstract class ConnectionRoom
extends Room {
    private static ArrayList<Class<? extends ConnectionRoom>> rooms = new ArrayList();
    private static float[][] chances;

    @Override
    public int minWidth() {
        return 3;
    }

    @Override
    public int maxWidth() {
        return 10;
    }

    @Override
    public int minHeight() {
        return 3;
    }

    @Override
    public int maxHeight() {
        return 10;
    }

    @Override
    public int minConnections(int direction) {
        if (direction == 0) {
            return 2;
        }
        return 0;
    }

    public static ConnectionRoom createRoom() {
        return Reflection.newInstance(rooms.get(Random.chances(chances[Dungeon.depth])));
    }

    static {
        rooms.add(TunnelRoom.class);
        rooms.add(BridgeRoom.class);
        rooms.add(PerimeterRoom.class);
        rooms.add(WalkwayRoom.class);
        rooms.add(RingTunnelRoom.class);
        rooms.add(RingBridgeRoom.class);
        chances = new float[27][];
        ConnectionRoom.chances[1] = new float[]{20.0f, 1.0f, 0.0f, 2.0f, 2.0f, 1.0f};
        ConnectionRoom.chances[2] = chances[1];
        ConnectionRoom.chances[3] = ConnectionRoom.chances[2];
        ConnectionRoom.chances[4] = ConnectionRoom.chances[2];
        ConnectionRoom.chances[5] = new float[]{20.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
        ConnectionRoom.chances[6] = new float[]{0.0f, 0.0f, 22.0f, 3.0f, 0.0f, 0.0f};
        ConnectionRoom.chances[7] = chances[6];
        ConnectionRoom.chances[8] = ConnectionRoom.chances[7];
        ConnectionRoom.chances[9] = ConnectionRoom.chances[7];
        ConnectionRoom.chances[10] = ConnectionRoom.chances[7];
        ConnectionRoom.chances[11] = new float[]{12.0f, 0.0f, 0.0f, 5.0f, 5.0f, 3.0f};
        ConnectionRoom.chances[12] = chances[11];
        ConnectionRoom.chances[13] = ConnectionRoom.chances[12];
        ConnectionRoom.chances[14] = ConnectionRoom.chances[12];
        ConnectionRoom.chances[15] = ConnectionRoom.chances[12];
        ConnectionRoom.chances[16] = new float[]{0.0f, 0.0f, 18.0f, 3.0f, 3.0f, 1.0f};
        ConnectionRoom.chances[17] = chances[16];
        ConnectionRoom.chances[18] = ConnectionRoom.chances[17];
        ConnectionRoom.chances[19] = ConnectionRoom.chances[17];
        ConnectionRoom.chances[20] = ConnectionRoom.chances[17];
        ConnectionRoom.chances[21] = chances[5];
        ConnectionRoom.chances[22] = new float[]{15.0f, 4.0f, 0.0f, 2.0f, 3.0f, 2.0f};
        ConnectionRoom.chances[23] = chances[22];
        ConnectionRoom.chances[24] = ConnectionRoom.chances[23];
        ConnectionRoom.chances[25] = ConnectionRoom.chances[23];
        ConnectionRoom.chances[26] = ConnectionRoom.chances[23];
    }
}

