/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultAlternatingFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultCircleRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultCrossRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultEnemyCenterRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultHallwayRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultLasersRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultLongRingsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultQuadrantsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRingsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public abstract class VaultRoom
extends StandardRoom {
    private static ArrayList<Class<? extends VaultRoom>> rooms = new ArrayList();
    private static float[] chances;

    @Override
    public float[] sizeCatProbs() {
        return new float[]{0.0f, 1.0f, 0.0f};
    }

    @Override
    public int minHeight() {
        return 11;
    }

    @Override
    public int maxHeight() {
        return 11;
    }

    @Override
    public int minWidth() {
        return 11;
    }

    @Override
    public int maxWidth() {
        return 11;
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return false;
    }

    @Override
    public int sizeFactor() {
        return 1;
    }

    public static void setupChances() {
        chances = new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    }

    public static VaultRoom createRoom() {
        int idx = Random.chances(chances);
        if (idx == -1) {
            VaultRoom.setupChances();
            idx = Random.chances(chances);
        }
        int n = idx;
        chances[n] = chances[n] - 1.0f;
        return Reflection.newInstance(rooms.get(idx));
    }

    static {
        rooms.add(VaultRingRoom.class);
        rooms.add(VaultCrossRoom.class);
        rooms.add(VaultQuadrantsRoom.class);
        rooms.add(VaultRingsRoom.class);
        rooms.add(VaultEnemyCenterRoom.class);
        rooms.add(VaultHallwayRoom.class);
        rooms.add(VaultLongRingsRoom.class);
        rooms.add(VaultCircleRoom.class);
        rooms.add(VaultAlternatingFireRoom.class);
        rooms.add(VaultLasersRoom.class);
        chances = new float[0];
    }
}

