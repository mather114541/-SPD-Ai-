/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SewerPainter
extends RegularPainter {
    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        int i;
        int[] map = level.map;
        int w = level.width();
        int l = level.length();
        for (i = 0; i < w; ++i) {
            if (map[i] != 4 || map[i + w] != 29 || Random.Int(4) != 0) continue;
            map[i] = 12;
        }
        for (i = w; i < l - w; ++i) {
            if (map[i] != 4 || map[i - w] != 4 || map[i + w] != 29 || Random.Int(2) != 0) continue;
            map[i] = 12;
        }
        for (i = w + 1; i < l - w - 1; ++i) {
            if (map[i] != 1) continue;
            int count = (map[i + 1] == 4 ? 1 : 0) + (map[i - 1] == 4 ? 1 : 0) + (map[i + w] == 4 ? 1 : 0) + (map[i - w] == 4 ? 1 : 0);
            if (Random.Int(16) >= count * count) continue;
            map[i] = 20;
        }
    }
}

