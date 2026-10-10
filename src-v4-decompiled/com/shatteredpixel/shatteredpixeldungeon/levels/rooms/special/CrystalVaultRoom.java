/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class CrystalVaultRoom
extends SpecialRoom {
    private ArrayList<Generator.Category> prizeClasses = new ArrayList<Generator.Category>(Arrays.asList(Generator.Category.WAND, Generator.Category.RING, Generator.Category.ARTIFACT));

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public int maxHeight() {
        return 7;
    }

    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int maxWidth() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        int i2Pos;
        int i1Pos;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Painter.fill(level, this, 2, 1);
        int c = level.pointToCell(this.center());
        Random.shuffle(this.prizeClasses);
        Item i1 = this.prize();
        Item i2 = this.prize();
        int doorPos = level.pointToCell(this.entrance());
        do {
            int neighbourIdx = Random.Int(PathFinder.CIRCLE8.length);
            i1Pos = c + PathFinder.CIRCLE8[neighbourIdx];
            i2Pos = c + PathFinder.CIRCLE8[(neighbourIdx + 4) % 8];
        } while (level.adjacent(i1Pos, doorPos) || level.adjacent(i2Pos, doorPos));
        level.drop((Item)i1, (int)i1Pos).type = Heap.Type.CRYSTAL_CHEST;
        float altChance = 0.1f * RatSkull.exoticChanceMultiplier();
        if (altChance > 0.1f) {
            altChance = (altChance + 0.1f) / 2.0f;
        }
        altChance *= MimicTooth.mimicChanceMultiplier();
        if (Random.Float() < altChance) {
            level.mobs.add(Mimic.spawnAt(i2Pos, CrystalMimic.class, i2));
        } else {
            level.drop((Item)i2, (int)i2Pos).type = Heap.Type.CRYSTAL_CHEST;
        }
        Painter.set(level, i1Pos, 11);
        Painter.set(level, i2Pos, 11);
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        this.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private Item prize() {
        Item prize;
        Generator.Category cat = this.prizeClasses.remove(0);
        this.prizeClasses.add(cat);
        while ((prize = Generator.random(cat)) == null || Challenges.isItemBlocked(prize)) {
        }
        return prize;
    }
}

