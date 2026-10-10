/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Random;

public class StorageRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        boolean honeyPot = Random.Int(2) == 0;
        int n = Random.IntRange(3, 4);
        for (int i = 0; i < n; ++i) {
            int pos;
            while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
            }
            if (honeyPot) {
                level.drop(new Honeypot(), pos);
                honeyPot = false;
                continue;
            }
            level.drop(StorageRoom.prize(level), pos);
        }
        this.entrance().set(Room.Door.Type.BARRICADE);
        level.addItemToSpawn(new PotionOfLiquidFlame());
    }

    private static Item prize(Level level) {
        Item prize;
        if (Random.Int(3) != 0 && (prize = level.findPrizeItem()) != null) {
            return prize;
        }
        return Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL, Generator.Category.FOOD, Generator.Category.GOLD));
    }
}

