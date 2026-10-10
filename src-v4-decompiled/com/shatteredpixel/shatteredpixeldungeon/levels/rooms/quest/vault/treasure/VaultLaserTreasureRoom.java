/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultLaser;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class VaultLaserTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        VaultLaser sentry;
        Rect itemPlace;
        Painter.fill(level, this, 4);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        if (entrance.x == this.left || entrance.x == this.right) {
            int x;
            int areaTop = (int)GameMath.gate(this.top + 2, entrance.y - 2, this.bottom - 6);
            Painter.fill(level, this.left + 1, areaTop, 9, 5, 1);
            if (entrance.x == this.left) {
                itemPlace = new Rect(this.right - 1, areaTop, this.right - 1, areaTop + 4);
                for (x = this.right - 2; x > this.left + 1; --x) {
                    sentry = new VaultLaser();
                    sentry.pos = x + level.width() * (areaTop - 1);
                    sentry.laserDirs = new int[]{sentry.pos + level.width()};
                    sentry.curCooldown = this.right - x;
                    sentry.afterShotCooldown = 3;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                    sentry = new VaultLaser();
                    sentry.pos = x + level.width() * (areaTop + 5);
                    sentry.laserDirs = new int[]{sentry.pos - level.width()};
                    sentry.curCooldown = Integer.MAX_VALUE;
                    sentry.afterShotCooldown = Integer.MAX_VALUE;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                }
            } else {
                itemPlace = new Rect(this.left + 1, areaTop, this.left + 1, areaTop + 4);
                for (x = this.left + 2; x < this.right - 1; ++x) {
                    sentry = new VaultLaser();
                    sentry.pos = x + level.width() * (areaTop - 1);
                    sentry.laserDirs = new int[]{sentry.pos + level.width()};
                    sentry.curCooldown = x - this.left;
                    sentry.afterShotCooldown = 3;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                    sentry = new VaultLaser();
                    sentry.pos = x + level.width() * (areaTop + 5);
                    sentry.laserDirs = new int[]{sentry.pos - level.width()};
                    sentry.curCooldown = Integer.MAX_VALUE;
                    sentry.afterShotCooldown = Integer.MAX_VALUE;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                }
            }
        } else {
            int y;
            int areaLeft = (int)GameMath.gate(this.left + 2, entrance.x - 2, this.right - 6);
            Painter.fill(level, areaLeft, this.top + 1, 5, 9, 1);
            if (entrance.y == this.top) {
                itemPlace = new Rect(areaLeft, this.bottom - 1, areaLeft + 4, this.bottom - 1);
                for (y = this.bottom - 2; y > this.top + 1; --y) {
                    sentry = new VaultLaser();
                    sentry.pos = areaLeft - 1 + level.width() * y;
                    sentry.laserDirs = new int[]{sentry.pos + 1};
                    sentry.curCooldown = this.bottom - y;
                    sentry.afterShotCooldown = 3;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                    sentry = new VaultLaser();
                    sentry.pos = areaLeft + 5 + level.width() * y;
                    sentry.laserDirs = new int[]{sentry.pos - 1};
                    sentry.curCooldown = Integer.MAX_VALUE;
                    sentry.afterShotCooldown = Integer.MAX_VALUE;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                }
            } else {
                itemPlace = new Rect(areaLeft, this.top + 1, areaLeft + 4, this.top + 1);
                for (y = this.top + 2; y < this.bottom - 1; ++y) {
                    sentry = new VaultLaser();
                    sentry.pos = areaLeft - 1 + level.width() * y;
                    sentry.laserDirs = new int[]{sentry.pos + 1};
                    sentry.curCooldown = y - this.top;
                    sentry.afterShotCooldown = 3;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                    sentry = new VaultLaser();
                    sentry.pos = areaLeft + 5 + level.width() * y;
                    sentry.laserDirs = new int[]{sentry.pos - 1};
                    sentry.curCooldown = Integer.MAX_VALUE;
                    sentry.afterShotCooldown = Integer.MAX_VALUE;
                    sentry.giveWarning = false;
                    Painter.set(level, sentry.pos, 11);
                    level.mobs.add(sentry);
                }
            }
        }
        Painter.drawInside(level, this, entrance, 1, 1);
        Painter.fill(level, itemPlace.left, itemPlace.top, itemPlace.width() + 1, itemPlace.height() + 1, 14);
        int treasurePos = level.pointToCell(Random.element(itemPlace.getPoints()));
        Item treasureItem = ((VaultLevel)level).createEquipment(1);
        level.drop((Item)treasureItem, (int)treasurePos).type = Heap.Type.CHEST;
        treasureItem = ((VaultLevel)level).findT2SolveItem();
        if (treasureItem == null) {
            treasureItem = ((VaultLevel)level).createConsumabe(1);
        }
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(itemPlace.getPoints()))) != null) {
        }
        level.drop(treasureItem, treasurePos);
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(itemPlace.getPoints()))) != null) {
        }
        level.drop(new DwarfToken(), treasurePos);
    }
}

