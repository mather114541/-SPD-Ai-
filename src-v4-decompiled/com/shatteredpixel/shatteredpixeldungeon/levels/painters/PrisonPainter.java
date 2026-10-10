/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ChasmBridgeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.FissureRoom;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class PrisonPainter
extends RegularPainter {
    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        int i;
        int w = level.width();
        int l = level.length();
        int[] map = level.map;
        for (int i2 = w + 1; i2 < l - w - 1; ++i2) {
            if (map[i2] != 1) continue;
            float c = 0.05f;
            if (map[i2 + 1] == 4 && map[i2 + w] == 4) {
                c += 0.2f;
            }
            if (map[i2 - 1] == 4 && map[i2 + w] == 4) {
                c += 0.2f;
            }
            if (map[i2 + 1] == 4 && map[i2 - w] == 4) {
                c += 0.2f;
            }
            if (map[i2 - 1] == 4 && map[i2 - w] == 4) {
                c += 0.2f;
            }
            if (!(Random.Float() < c)) continue;
            map[i2] = 20;
        }
        for (Room r : rooms) {
            if (r instanceof SpecialRoom) continue;
            int chance = 15;
            if (r instanceof FissureRoom) {
                chance = 3;
            } else if (r instanceof ChasmBridgeRoom) {
                chance = 5;
            }
            for (int y = r.bottom - 1; y > r.top; --y) {
                int cell = r.left + 1 + level.width() * y;
                for (int x = r.left + 1; x < r.right; ++x) {
                    if (level.map[cell] == 0 && level.map[cell - level.width()] == 0 && Random.Int(chance) == 0) {
                        level.map[cell] = 34;
                    }
                    ++cell;
                }
            }
        }
        for (i = 0; i < w; ++i) {
            if (map[i] != 4 || map[i + w] != 1 && map[i + w] != 14 || Random.Int(6) != 0) continue;
            map[i] = 12;
        }
        for (i = w; i < l - w; ++i) {
            if (map[i] != 4 || map[i - w] != 4 || map[i + w] != 1 && map[i + w] != 14 || Random.Int(3) != 0) continue;
            map[i] = 12;
        }
    }
}

