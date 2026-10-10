/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultBookcaseTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultCircleScanTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultFlamePathRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultFlamesTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultHardLaserTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultLaserTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultManyScansRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultMultipleEnemyTreasureRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultSingleEnemyTreasureRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class VaultTreasureRoom
extends VaultRoom {
    private Room.Door entrance;
    private static final ArrayList<Class<? extends VaultTreasureRoom>> T1_ROOMS = new ArrayList<Class>(Arrays.asList(VaultFlamePathRoom.class, VaultLaserTreasureRoom.class, VaultCircleScanTreasureRoom.class));
    private static final ArrayList<Class<? extends VaultTreasureRoom>> T2_ROOMS = new ArrayList<Class>(Arrays.asList(VaultSingleEnemyTreasureRoom.class, VaultBookcaseTreasureRoom.class, VaultFlamesTreasureRoom.class));
    private static final ArrayList<Class<? extends VaultTreasureRoom>> T3_ROOMS = new ArrayList<Class>(Arrays.asList(VaultManyScansRoom.class, VaultMultipleEnemyTreasureRoom.class, VaultHardLaserTreasureRoom.class));
    public static ArrayList<Class<? extends VaultTreasureRoom>> treasuresToSpawn = new ArrayList();

    @Override
    public int maxConnections(int direction) {
        return 1;
    }

    public Room.Door entrance() {
        if (this.entrance == null) {
            if (this.connected.isEmpty()) {
                return null;
            }
            this.entrance = (Room.Door)this.connected.values().iterator().next();
        }
        return this.entrance;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return false;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }

    public static void generateRoomList() {
        ArrayList<Class<? extends VaultTreasureRoom>> T1s = new ArrayList<Class<? extends VaultTreasureRoom>>(T1_ROOMS);
        Random.shuffle(T1s);
        ArrayList<Class<? extends VaultTreasureRoom>> T2s = new ArrayList<Class<? extends VaultTreasureRoom>>(T2_ROOMS);
        Random.shuffle(T2s);
        ArrayList<Class<? extends VaultTreasureRoom>> T3s = new ArrayList<Class<? extends VaultTreasureRoom>>(T3_ROOMS);
        Random.shuffle(T3s);
        ArrayList<ArrayList> fullList = new ArrayList<ArrayList>();
        fullList.add(T1s);
        fullList.add(T2s);
        fullList.add(T3s);
        treasuresToSpawn = new ArrayList();
        while (!fullList.isEmpty()) {
            ArrayList current = (ArrayList)fullList.remove(0);
            treasuresToSpawn.add((Class)current.remove(0));
            if (current.isEmpty()) continue;
            fullList.add(current);
        }
    }

    public static VaultTreasureRoom nextRoom() {
        return Reflection.newInstance(treasuresToSpawn.remove(0));
    }
}

