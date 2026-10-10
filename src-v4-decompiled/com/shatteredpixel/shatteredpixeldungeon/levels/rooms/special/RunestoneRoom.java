/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Random;

public class RunestoneRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 0);
        Painter.drawInside(level, this, this.entrance(), 2, 14);
        Painter.fill(level, this, 2, 1);
        int n = Random.NormalIntRange(2, 3);
        for (int i = 0; i < n; ++i) {
            int dropPos;
            while (level.map[dropPos = level.pointToCell(this.random())] != 1 || level.heaps.get(dropPos) != null) {
            }
            level.drop(RunestoneRoom.prize(level), dropPos);
        }
        this.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {
        Item prize = level.findPrizeItem(TrinketCatalyst.class);
        if (prize == null && (prize = level.findPrizeItem(Runestone.class)) == null) {
            prize = Generator.random(Generator.Category.STONE);
        }
        return prize;
    }
}

