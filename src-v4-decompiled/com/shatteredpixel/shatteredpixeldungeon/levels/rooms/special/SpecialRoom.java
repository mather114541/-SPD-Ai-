/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ArmoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CryptRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalChoiceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalPathRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.CrystalVaultRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.GardenRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LaboratoryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.LibraryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicWellRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PoolRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.RunestoneRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SentryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StorageRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ToxicGasRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TrapsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.TreasuryRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class SpecialRoom
extends Room {
    private Room.Door entrance;
    private static final String ENTRANCE = "entrance";
    private static final ArrayList<Class<? extends SpecialRoom>> EQUIP_SPECIALS = new ArrayList<Class>(Arrays.asList(WeakFloorRoom.class, CryptRoom.class, PoolRoom.class, ArmoryRoom.class, SentryRoom.class, StatueRoom.class, CrystalVaultRoom.class, CrystalChoiceRoom.class, SacrificeRoom.class));
    private static final ArrayList<Class<? extends SpecialRoom>> CONSUMABLE_SPECIALS = new ArrayList<Class>(Arrays.asList(RunestoneRoom.class, GardenRoom.class, LibraryRoom.class, StorageRoom.class, TreasuryRoom.class, MagicWellRoom.class, ToxicGasRoom.class, MagicalFireRoom.class, TrapsRoom.class, CrystalPathRoom.class));
    public static final ArrayList<Class<? extends SpecialRoom>> CRYSTAL_KEY_SPECIALS = new ArrayList<Class>(Arrays.asList(PitRoom.class, CrystalVaultRoom.class, CrystalChoiceRoom.class, CrystalPathRoom.class));
    private static final ArrayList<Class<? extends SpecialRoom>> POTION_SPAWN_ROOMS = new ArrayList<Class>(Arrays.asList(PoolRoom.class, SentryRoom.class, StorageRoom.class, ToxicGasRoom.class, MagicalFireRoom.class, TrapsRoom.class));
    public static ArrayList<Class<? extends Room>> runSpecials = new ArrayList();
    public static ArrayList<Class<? extends Room>> floorSpecials = new ArrayList();
    private static int pitNeededDepth = -1;
    private static final String ROOMS = "special_rooms";
    private static final String PIT = "pit_needed";

    @Override
    public int minWidth() {
        return 5;
    }

    @Override
    public int maxWidth() {
        return 10;
    }

    @Override
    public int minHeight() {
        return 5;
    }

    @Override
    public int maxHeight() {
        return 10;
    }

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
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.entrance() != null) {
            bundle.put(ENTRANCE, this.entrance());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(ENTRANCE)) {
            this.entrance = (Room.Door)bundle.get(ENTRANCE);
        }
    }

    public static void initForRun() {
        runSpecials = new ArrayList();
        ArrayList runEquipSpecials = (ArrayList)EQUIP_SPECIALS.clone();
        ArrayList runConsSpecials = (ArrayList)CONSUMABLE_SPECIALS.clone();
        Random.shuffle(runEquipSpecials);
        Random.shuffle(runConsSpecials);
        runSpecials.add((Class)runConsSpecials.remove(0));
        while (!runEquipSpecials.isEmpty() || !runConsSpecials.isEmpty()) {
            if (!runEquipSpecials.isEmpty()) {
                runSpecials.add((Class)runEquipSpecials.remove(0));
            }
            if (runConsSpecials.isEmpty()) continue;
            runSpecials.add((Class)runConsSpecials.remove(0));
        }
        pitNeededDepth = -1;
    }

    public static void initForFloor() {
        floorSpecials = (ArrayList)runSpecials.clone();
        if (Dungeon.labRoomNeeded()) {
            ++Dungeon.LimitedDrops.LAB_ROOM.count;
            floorSpecials.add(0, LaboratoryRoom.class);
        }
    }

    private static void useType(Class<? extends Room> type) {
        floorSpecials.remove(type);
        if (CRYSTAL_KEY_SPECIALS.contains(type)) {
            floorSpecials.removeAll(CRYSTAL_KEY_SPECIALS);
        }
        if (POTION_SPAWN_ROOMS.contains(type)) {
            floorSpecials.removeAll(POTION_SPAWN_ROOMS);
        }
        if (runSpecials.remove(type)) {
            runSpecials.add(type);
        }
    }

    public static void resetPitRoom(int depth) {
        if (pitNeededDepth == depth) {
            pitNeededDepth = -1;
        }
    }

    public static SpecialRoom createRoom() {
        int index;
        if (Dungeon.depth == pitNeededDepth) {
            pitNeededDepth = -1;
            SpecialRoom.useType(PitRoom.class);
            return new PitRoom();
        }
        if (floorSpecials.contains(LaboratoryRoom.class)) {
            SpecialRoom.useType(LaboratoryRoom.class);
            return new LaboratoryRoom();
        }
        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            floorSpecials.remove(WeakFloorRoom.class);
        }
        for (index = Random.chances(new float[]{6.0f, 3.0f, 1.0f}); index >= floorSpecials.size(); --index) {
        }
        Room r = Reflection.newInstance(floorSpecials.get(index));
        if (r instanceof WeakFloorRoom) {
            pitNeededDepth = Dungeon.depth + 1;
        }
        SpecialRoom.useType(r.getClass());
        return (SpecialRoom)r;
    }

    public static void restoreRoomsFromBundle(Bundle bundle) {
        runSpecials.clear();
        if (bundle.contains(ROOMS)) {
            for (Class type : bundle.getClassArray(ROOMS)) {
                runSpecials.add(type);
            }
        } else {
            SpecialRoom.initForRun();
            ShatteredPixelDungeon.reportException(new Exception("specials array didn't exist!"));
        }
        pitNeededDepth = bundle.getInt(PIT);
    }

    public static void storeRoomsInBundle(Bundle bundle) {
        bundle.put(ROOMS, runSpecials.toArray(new Class[0]));
        bundle.put(PIT, pitNeededDepth);
    }
}

