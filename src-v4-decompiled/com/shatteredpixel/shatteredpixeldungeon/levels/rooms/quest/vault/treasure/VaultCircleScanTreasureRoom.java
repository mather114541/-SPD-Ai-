/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultSentry;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class VaultCircleScanTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 2, 1);
        Painter.fill(level, this, 4, 1, 4, 1, 1);
        Painter.fill(level, this, 1, 4, 1, 4, 1);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        Painter.drawInside(level, this, entrance, 3, 1);
        Point c = this.center();
        boolean clockwise = true;
        Painter.set(level, c, 11);
        Rect treasure = null;
        if (entrance.x == this.left) {
            Painter.set(level, c.x + 2, c.y, 4);
            Painter.set(level, c.x + 1, c.y, 25);
            Painter.set(level, c.x - 1, c.y, 25);
            treasure = new Rect(this.left + 1, c.y - 1, c.x - 2, c.y + 1);
            Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 4);
            --treasure.right;
            if (entrance.y < c.y) {
                clockwise = false;
                ++treasure.top;
            } else {
                clockwise = true;
                --treasure.bottom;
            }
        } else if (entrance.y == this.top) {
            Painter.set(level, c.x, c.y + 2, 4);
            Painter.set(level, c.x, c.y + 1, 25);
            Painter.set(level, c.x, c.y - 1, 25);
            treasure = new Rect(c.x - 1, this.top + 1, c.x + 1, this.top + 3);
            Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 4);
            --treasure.bottom;
            if (entrance.x < c.x) {
                clockwise = true;
                ++treasure.left;
            } else {
                clockwise = false;
                --treasure.right;
            }
        } else if (entrance.x == this.right) {
            Painter.set(level, c.x - 2, c.y, 4);
            Painter.set(level, c.x - 1, c.y, 25);
            Painter.set(level, c.x + 1, c.y, 25);
            treasure = new Rect(this.right - 3, c.y - 1, this.right - 1, c.y + 1);
            Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 4);
            ++treasure.left;
            if (entrance.y < c.y) {
                clockwise = true;
                ++treasure.top;
            } else {
                clockwise = false;
                --treasure.bottom;
            }
        } else if (entrance.y == this.bottom) {
            Painter.set(level, c.x, c.y - 2, 4);
            Painter.set(level, c.x, c.y - 1, 25);
            Painter.set(level, c.x, c.y + 1, 25);
            treasure = new Rect(c.x - 1, this.bottom - 3, c.x + 1, this.bottom - 1);
            Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 4);
            ++treasure.top;
            if (entrance.x < c.x) {
                clockwise = false;
                ++treasure.left;
            } else {
                clockwise = true;
                --treasure.right;
            }
        }
        Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 14);
        VaultSentry sentry = new VaultSentry();
        sentry.pos = level.pointToCell(c);
        sentry.scanLength = 4.49f;
        int w = level.width();
        sentry.scanWidth = 45.0f;
        sentry.scanDirs = new int[][]{{sentry.pos - 2}, {sentry.pos - 2 - w}, {sentry.pos - 2 - 2 * w}, {sentry.pos - 1 - 2 * w}, {sentry.pos - 2 * w}, {sentry.pos + 1 - 2 * w}, {sentry.pos + 2 - 2 * w}, {sentry.pos + 2 - w}, {sentry.pos + 2}, {sentry.pos + 2 + w}, {sentry.pos + 2 + 2 * w}, {sentry.pos + 1 + 2 * w}, {sentry.pos + 2 * w}, {sentry.pos - 1 + 2 * w}, {sentry.pos - 2 + 2 * w}, {sentry.pos - 2 + w}};
        if (!clockwise) {
            int[][] reverse = new int[sentry.scanDirs.length][];
            for (int i = 0; i < sentry.scanDirs.length; ++i) {
                reverse[i] = sentry.scanDirs[sentry.scanDirs.length - i - 1];
            }
            sentry.scanDirs = reverse;
        }
        level.mobs.add(sentry);
        int treasurePos = level.pointToCell(Random.element(treasure.getPoints()));
        Item treasureItem = ((VaultLevel)level).createEquipment(1);
        level.drop((Item)treasureItem, (int)treasurePos).type = Heap.Type.CHEST;
        treasureItem = ((VaultLevel)level).findT2SolveItem();
        if (treasureItem == null) {
            treasureItem = ((VaultLevel)level).createConsumabe(1);
        }
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(treasure.getPoints()))) != null) {
        }
        level.drop(treasureItem, treasurePos);
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(treasure.getPoints()))) != null) {
        }
        level.drop(new DwarfToken(), treasurePos);
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return Math.abs(c.x - p.x) > 1 && Math.abs(c.y - p.y) > 1 && super.canConnect(p);
    }
}

