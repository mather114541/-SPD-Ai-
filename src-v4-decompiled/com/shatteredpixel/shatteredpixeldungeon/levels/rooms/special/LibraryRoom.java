/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Random;

public class LibraryRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Room.Door entrance = this.entrance();
        Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, 1, 27);
        Painter.drawInside(level, this, entrance, 1, 14);
        int n = Random.NormalIntRange(1, 3);
        for (int i = 0; i < n; ++i) {
            int pos;
            while (level.map[pos = level.pointToCell(this.random())] != 14 || level.heaps.get(pos) != null) {
            }
            Item item = i == 0 ? (Random.Int(2) == 0 ? new ScrollOfIdentify() : new ScrollOfRemoveCurse()) : LibraryRoom.prize(level);
            level.drop(item, pos);
        }
        entrance.set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
    }

    private static Item prize(Level level) {
        Item prize = level.findPrizeItem(TrinketCatalyst.class);
        if (prize == null && (prize = level.findPrizeItem(Scroll.class)) == null) {
            prize = Generator.random(Generator.Category.SCROLL);
        }
        return prize;
    }
}

