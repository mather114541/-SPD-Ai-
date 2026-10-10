/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Random;

public class TreasuryRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        int i;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.set(level, this.center(), 25);
        Heap.Type heapType = Random.Int(2) == 0 ? Heap.Type.CHEST : Heap.Type.HEAP;
        int n = Random.IntRange(2, 3);
        float mimicChance = 0.2f * MimicTooth.mimicChanceMultiplier();
        for (i = 0; i < n; ++i) {
            int pos;
            Item item = level.findPrizeItem(TrinketCatalyst.class);
            if (item == null) {
                item = new Gold().random();
            }
            while (level.map[pos = level.pointToCell(this.random())] != 1 || level.heaps.get(pos) != null || level.findMob(pos) != null) {
            }
            if (heapType == Heap.Type.CHEST && Dungeon.depth > 1 && Random.Float() < mimicChance) {
                level.mobs.add(Mimic.spawnAt(pos, item));
                continue;
            }
            level.drop((Item)item, (int)pos).type = heapType;
        }
        if (heapType == Heap.Type.HEAP) {
            for (i = 0; i < 6; ++i) {
                int pos;
                while (level.map[pos = level.pointToCell(this.random())] != 1) {
                }
                level.drop(new Gold(Random.IntRange(5, 12)), pos);
            }
        }
        this.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }
}

