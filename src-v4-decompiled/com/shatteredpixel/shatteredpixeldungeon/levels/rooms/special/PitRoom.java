/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class PitRoom
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
    public int maxWidth() {
        return 9;
    }

    @Override
    public int maxHeight() {
        return 9;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.CRYSTAL);
        Point well = null;
        if (entrance.x == this.left) {
            well = new Point(this.right - 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.x == this.right) {
            well = new Point(this.left + 1, Random.Int(2) == 0 ? this.top + 1 : this.bottom - 1);
        } else if (entrance.y == this.top) {
            well = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.bottom - 1);
        } else if (entrance.y == this.bottom) {
            well = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.top + 1);
        }
        Painter.set(level, well, 3);
        int remains = level.pointToCell(this.center());
        Item mainLoot = null;
        do {
            switch (Random.Int(3)) {
                case 0: {
                    mainLoot = Generator.random(Generator.Category.RING);
                    break;
                }
                case 1: {
                    mainLoot = Generator.random(Generator.Category.ARTIFACT);
                    break;
                }
                case 2: {
                    mainLoot = Generator.random(Random.oneOf(Generator.Category.WEAPON, Generator.Category.WEAPON, Generator.Category.MISSILE, Generator.Category.ARMOR, Generator.Category.ARMOR));
                }
            }
        } while (mainLoot == null || Challenges.isItemBlocked(mainLoot));
        level.drop((Item)mainLoot, (int)remains).setHauntedIfCursed().type = Heap.Type.SKELETON;
        int n = Random.IntRange(1, 2);
        for (int i = 0; i < n; ++i) {
            level.drop(PitRoom.prize(level), remains).setHauntedIfCursed();
        }
        level.drop(new CrystalKey(Dungeon.depth), remains);
    }

    private static Item prize(Level level) {
        return Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL, Generator.Category.FOOD, Generator.Category.GOLD));
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }
}

