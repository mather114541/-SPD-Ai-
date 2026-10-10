/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;

public class SecretChestChasmRoom
extends SecretRoom {
    @Override
    public int minWidth() {
        return 8;
    }

    @Override
    public int maxWidth() {
        return 9;
    }

    @Override
    public int minHeight() {
        return 8;
    }

    @Override
    public int maxHeight() {
        return 9;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 0);
        int chests = 0;
        Point p = new Point(this.left + 3, this.top + 3);
        Painter.set(level, p, 14);
        level.drop((Item)Generator.randomUsingDefaults(), (int)level.pointToCell((Point)p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) {
            ++chests;
        }
        p.x = this.right - 3;
        Painter.set(level, p, 14);
        level.drop((Item)Generator.randomUsingDefaults(), (int)level.pointToCell((Point)p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) {
            ++chests;
        }
        p.y = this.bottom - 3;
        Painter.set(level, p, 14);
        level.drop((Item)Generator.randomUsingDefaults(), (int)level.pointToCell((Point)p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) {
            ++chests;
        }
        p.x = this.left + 3;
        Painter.set(level, p, 14);
        level.drop((Item)Generator.randomUsingDefaults(), (int)level.pointToCell((Point)p)).type = Heap.Type.LOCKED_CHEST;
        if (level.heaps.get(level.pointToCell(p)) != null) {
            ++chests;
        }
        p = new Point(this.left + 1, this.top + 1);
        Painter.set(level, p, 14);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            --chests;
        }
        p.x = this.right - 1;
        Painter.set(level, p, 14);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            --chests;
        }
        p.y = this.bottom - 1;
        Painter.set(level, p, 14);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            --chests;
        }
        p.x = this.left + 1;
        Painter.set(level, p, 14);
        if (chests > 0) {
            level.drop(new GoldenKey(Dungeon.depth), level.pointToCell(p));
            --chests;
        }
        level.addItemToSpawn(new PotionOfLevitation());
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

