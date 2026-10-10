/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.painters;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.RegularPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.BlacksmithRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CavesPainter
extends RegularPainter {
    @Override
    protected void decorate(Level level, ArrayList<Room> rooms) {
        int w = level.width();
        int l = level.length();
        int[] map = level.map;
        for (Room r : rooms) {
            for (Room n : r.neigbours) {
                if (r.connected.containsKey(n)) continue;
                this.mergeRooms(level, r, n, null, Random.Int(3) == 0 ? 33 : 0);
            }
        }
        for (Room room : rooms) {
            int corner;
            if (!(room instanceof StandardRoom) || room instanceof BlacksmithRoom || room.width() <= 4 || room.height() <= 4) continue;
            int s = room.square();
            if (Random.Int(s) > 8 && (Terrain.flags[map[corner = room.left + 1 + (room.top + 1) * w]] & 0x10) == 0 && map[corner - 1] == 4 && !room.connected.containsValue(level.cellToPoint(corner - 1)) && map[corner - w] == 4 && !room.connected.containsValue(level.cellToPoint(corner - w)) && map[corner + 1] != 18 && map[corner + w] != 18) {
                map[corner] = 4;
                level.traps.remove(corner);
            }
            if (Random.Int(s) > 8 && (Terrain.flags[map[corner = room.right - 1 + (room.top + 1) * w]] & 0x10) == 0 && map[corner + 1] == 4 && !room.connected.containsValue(level.cellToPoint(corner + 1)) && map[corner - w] == 4 && !room.connected.containsValue(level.cellToPoint(corner - w)) && map[corner - 1] != 18 && map[corner + w] != 18) {
                map[corner] = 4;
                level.traps.remove(corner);
            }
            if (Random.Int(s) > 8 && (Terrain.flags[map[corner = room.left + 1 + (room.bottom - 1) * w]] & 0x10) == 0 && map[corner - 1] == 4 && !room.connected.containsValue(level.cellToPoint(corner - 1)) && map[corner + w] == 4 && !room.connected.containsValue(level.cellToPoint(corner + w)) && map[corner + 1] != 18 && map[corner - w] != 18) {
                map[corner] = 4;
                level.traps.remove(corner);
            }
            if (Random.Int(s) <= 8 || (Terrain.flags[map[corner = room.right - 1 + (room.bottom - 1) * w]] & 0x10) != 0 || map[corner + 1] != 4 || room.connected.containsValue(level.cellToPoint(corner + 1)) || map[corner + w] != 4 || room.connected.containsValue(level.cellToPoint(corner + w)) || map[corner - 1] == 18 || map[corner - w] == 18) continue;
            map[corner] = 4;
            level.traps.remove(corner);
        }
        for (int i = w + 1; i < l - w; ++i) {
            if (map[i] != 1) continue;
            int n = 0;
            if (map[i + 1] == 4) {
                ++n;
            }
            if (map[i - 1] == 4) {
                ++n;
            }
            if (map[i + w] == 4) {
                ++n;
            }
            if (map[i - w] == 4) {
                ++n;
            }
            if (Random.Int(6) > n) continue;
            map[i] = 20;
        }
        this.generateGold(level, rooms);
    }

    protected void generateGold(Level level, ArrayList<Room> rooms) {
        int w = level.width();
        int l = level.length();
        int[] map = level.map;
        for (int i = 0; i < l - w; ++i) {
            if (map[i] != 4 || !DungeonTileSheet.floorTile(map[i + w]) || Random.Int(4) != 0) continue;
            map[i] = 12;
        }
    }
}

