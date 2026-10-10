/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultSentry;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class VaultManyScansRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        ArrayList<Integer> corners = new ArrayList<Integer>();
        int w = level.width();
        Point c = this.center();
        corners.add(this.left + 1 + w * (this.top + 1));
        corners.add(c.x + w * (this.top + 1));
        corners.add(this.right - 1 + w * (this.top + 1));
        corners.add(this.left + 1 + w * c.y);
        corners.add(this.right - 1 + w * c.y);
        corners.add(this.left + 1 + w * (this.bottom - 1));
        corners.add(c.x + w * (this.bottom - 1));
        corners.add(this.right - 1 + w * (this.bottom - 1));
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        Iterator iterator = corners.iterator();
        while (iterator.hasNext()) {
            int cell = (Integer)iterator.next();
            if (!(level.trueDistance(cell, level.pointToCell(entrance)) >= 2.0f)) continue;
            VaultSentry sentry = new VaultSentry();
            sentry.pos = cell;
            sentry.scanLength = 7.0f;
            sentry.scanWidth = 70.0f;
            sentry.scanDirs = new int[][]{{c.x + w * c.y}};
            level.mobs.add(sentry);
        }
        Painter.set(level, c, 11);
        Item treasureItem = ((VaultLevel)level).createEquipment(3);
        level.drop((Item)treasureItem, (int)(c.x + w * c.y)).type = Heap.Type.CHEST;
        treasureItem = ((VaultLevel)level).createConsumabe(3);
        level.drop(treasureItem, c.x + w * c.y + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)]);
        level.drop(new DwarfToken(), c.x + w * c.y + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)]);
        level.addItemToSpawn(new PotionOfInvisibility());
    }
}

