/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class HallsPainter
extends RegularPainter {
    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        int[] map = level.map;
        int w = level.width();
        int l = level.length();
        for (int i = w + 1; i < l - w - 1; ++i) {
            if (map[i] == 1) {
                int count = 0;
                for (int j = 0; j < PathFinder.NEIGHBOURS8.length; ++j) {
                    if ((Terrain.flags[map[i + PathFinder.NEIGHBOURS8[j]]] & 1) <= 0) continue;
                    ++count;
                }
                if (Random.Int(80) >= count) continue;
                map[i] = 20;
                continue;
            }
            if (map[i] == 4 && map[i - 1] != 12 && map[i - w] != 12 && Random.Int(20) == 0) {
                map[i] = 12;
                continue;
            }
            if (map[i] != 33 || Random.Int(2) != 0) continue;
            map[i] = 34;
        }
        for (Room r : rooms) {
            for (Room n : r.neigbours) {
                if (r.connected.containsKey(n)) continue;
                this.mergeRooms(level, r, n, null, Random.Int(3) == 0 ? 33 : 0);
            }
        }
    }
}

