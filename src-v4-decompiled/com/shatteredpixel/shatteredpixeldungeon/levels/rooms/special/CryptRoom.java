/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;

public class CryptRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Point c = this.center();
        int cx = c.x;
        int cy = c.y;
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
        if (entrance.x == this.left) {
            Painter.set(level, new Point(this.right - 1, this.top + 1), 25);
            Painter.set(level, new Point(this.right - 1, this.bottom - 1), 25);
            cx = this.right - 2;
        } else if (entrance.x == this.right) {
            Painter.set(level, new Point(this.left + 1, this.top + 1), 25);
            Painter.set(level, new Point(this.left + 1, this.bottom - 1), 25);
            cx = this.left + 2;
        } else if (entrance.y == this.top) {
            Painter.set(level, new Point(this.left + 1, this.bottom - 1), 25);
            Painter.set(level, new Point(this.right - 1, this.bottom - 1), 25);
            cy = this.bottom - 2;
        } else if (entrance.y == this.bottom) {
            Painter.set(level, new Point(this.left + 1, this.top + 1), 25);
            Painter.set(level, new Point(this.right - 1, this.top + 1), 25);
            cy = this.top + 2;
        }
        level.drop((Item)CryptRoom.prize((Level)level), (int)(cx + cy * level.width())).type = Heap.Type.TOMB;
    }

    private static Item prize(Level level) {
        Armor prize = Generator.randomArmor(Dungeon.depth / 5 + 1);
        if (Challenges.isItemBlocked(prize)) {
            return new Gold().random();
        }
        Armor.Glyph curse = Armor.Glyph.randomCurse(new Class[0]);
        if (!prize.cursed) {
            prize.upgrade();
            if (!prize.hasGoodGlyph()) {
                prize.inscribe(curse);
            }
        }
        prize.cursedKnown = true;
        prize.cursed = true;
        return prize;
    }
}

