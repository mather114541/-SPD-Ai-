/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class ArmoryRoom
extends SpecialRoom {
    private static float[] prizeCats;

    @Override
    public void paint(Level level) {
        int pos;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Room.Door entrance = this.entrance();
        Point statue = null;
        if (entrance.x == this.left) {
            statue = new Point(this.right - 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.x == this.right) {
            statue = new Point(this.left + 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.y == this.top) {
            statue = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.bottom - 1);
        } else if (entrance.y == this.bottom) {
            statue = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.top + 1);
        }
        if (statue != null) {
            Painter.set(level, statue, 25);
        }
        int n = Random.IntRange(2, 3);
        prizeCats = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        for (int i = 0; i < n; ++i) {
            while (level.map[pos = level.pointToCell(this.random())] != 1 || level.heaps.get(pos) != null) {
            }
            level.drop(ArmoryRoom.prize(level), pos);
        }
        Item cata = level.findPrizeItem(TrinketCatalyst.class);
        if (cata != null) {
            while (level.map[pos = level.pointToCell(this.random())] != 1 || level.heaps.get(pos) != null) {
            }
            level.drop(cata, pos);
        }
        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {
        int index = Random.chances(prizeCats);
        ArmoryRoom.prizeCats[index] = 0.0f;
        switch (index) {
            case 0: {
                return new Bomb().random();
            }
            case 1: {
                return Generator.randomWeapon();
            }
            case 2: {
                return Generator.randomArmor();
            }
        }
        return Generator.randomMissile();
    }
}

