/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.EmptyRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class CrystalChoiceRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Room.Door entrance = this.entrance();
        EmptyRoom entry = new EmptyRoom();
        EmptyRoom room1 = new EmptyRoom();
        EmptyRoom room2 = new EmptyRoom();
        if (entrance.x == this.left) {
            entry.set(this.left + 1, this.top + 1, this.left + 2, this.bottom - 1);
            room1.set(entry.right + 2, this.top + 1, this.right - 1, this.center().y - 1);
            room2.set(entry.right + 2, room1.bottom + 2, this.right - 1, this.bottom - 1);
            Painter.set(level, new Point(entry.right + 1, (room1.top + room1.bottom + 1) / 2), 31);
            Painter.set(level, new Point(entry.right + 1, (room2.top + room2.bottom) / 2), 31);
        } else if (entrance.y == this.top) {
            entry.set(this.left + 1, this.top + 1, this.right - 1, this.top + 2);
            room1.set(this.left + 1, entry.bottom + 2, this.center().x - 1, this.bottom - 1);
            room2.set(room1.right + 2, entry.bottom + 2, this.right - 1, this.bottom - 1);
            Painter.set(level, new Point((room1.left + room1.right + 1) / 2, entry.bottom + 1), 31);
            Painter.set(level, new Point((room2.left + room2.right) / 2, entry.bottom + 1), 31);
        } else if (entrance.x == this.right) {
            entry.set(this.right - 2, this.top + 1, this.right - 1, this.bottom - 1);
            Painter.drawLine(level, new Point(this.right - 1, this.top + 1), new Point(this.right - 1, this.bottom - 1), 1);
            room1.set(this.left + 1, this.top + 1, entry.left - 2, this.center().y - 1);
            room2.set(this.left + 1, room1.bottom + 2, entry.left - 2, this.bottom - 1);
            Painter.set(level, new Point(entry.left - 1, (room1.top + room1.bottom + 1) / 2), 31);
            Painter.set(level, new Point(entry.left - 1, (room2.top + room2.bottom) / 2), 31);
        } else if (entrance.y == this.bottom) {
            entry.set(this.left + 1, this.bottom - 2, this.right - 1, this.bottom - 1);
            room1.set(this.left + 1, this.top + 1, this.center().x - 1, entry.top - 2);
            room2.set(room1.right + 2, this.top + 1, this.right - 1, entry.top - 2);
            Painter.set(level, new Point((room1.left + room1.right + 1) / 2, entry.top - 1), 31);
            Painter.set(level, new Point((room2.left + room2.right) / 2, entry.top - 1), 31);
        }
        Painter.fill(level, entry, 1);
        Painter.fill(level, room1, 14);
        Painter.fill(level, room2, 14);
        if (Random.Int(2) == 0) {
            EmptyRoom tmp = room1;
            room1 = room2;
            room2 = tmp;
        }
        int n = Random.NormalIntRange(3, 4);
        for (int i = 0; i < n; ++i) {
            int pos;
            Item reward = Generator.random(Random.oneOf(Generator.Category.POTION, Generator.Category.SCROLL));
            while (level.heaps.get(pos = room1.square() >= 16 ? level.pointToCell(room1.random(1)) : level.pointToCell(room1.random(0))) != null) {
            }
            level.drop(reward, pos);
        }
        Item hidden = Generator.random(Random.oneOf(Generator.Category.WAND, Generator.Category.RING, Generator.Category.ARTIFACT));
        Heap chest = level.drop(hidden, level.pointToCell(room2.center()));
        chest.type = Heap.Type.CHEST;
        chest.autoExplored = true;
        level.addItemToSpawn(new CrystalKey(Dungeon.depth));
        this.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }
}

