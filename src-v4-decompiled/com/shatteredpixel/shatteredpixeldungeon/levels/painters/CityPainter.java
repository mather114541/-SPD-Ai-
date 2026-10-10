/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CityPainter
extends RegularPainter {
    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        int[] map = level.map;
        int w = level.width();
        int l = level.length();
        for (int i = 0; i < l - w; ++i) {
            if (map[i] == 1 && Random.Int(10) == 0) {
                map[i] = 20;
                continue;
            }
            if (map[i] != 4 || DungeonTileSheet.wallStitcheable(map[i + w]) || Random.Int(21 - Dungeon.depth) != 0) continue;
            map[i] = 12;
        }
    }
}

