/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultLongRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultHallwayRoom
extends VaultLongRoom {
    @Override
    public void paint(Level level) {
        Item i;
        Painter.fill(level, this, 4);
        if (this.wide()) {
            Painter.fill(level, this.left + 1, this.top + 4, this.width() - 2, 3, 1);
            Painter.fill(level, this.left + 6, this.top + 1, 1, this.height() - 2, 1);
            Painter.fill(level, this.right - 7, this.top + 1, 1, this.height() - 2, 1);
        } else {
            Painter.fill(level, this.left + 4, this.top + 1, 3, this.height() - 2, 1);
            Painter.fill(level, this.left + 1, this.top + 6, this.width() - 2, 1, 1);
            Painter.fill(level, this.left + 1, this.bottom - 7, this.width() - 2, 1, 1);
        }
        Point c = this.center();
        ArrayList<Integer> lootPositions = new ArrayList<Integer>();
        if (this.wide()) {
            lootPositions.add(this.left + 2 + c.y * level.width());
            lootPositions.add(this.right - 2 + c.y * level.width());
        } else {
            lootPositions.add(c.x + (this.top + 2) * level.width());
            lootPositions.add(c.x + (this.bottom - 2) * level.width());
        }
        for (Room.Door d : this.connected.values()) {
            Integer[] integerArray = lootPositions.toArray(new Integer[0]);
            int n = integerArray.length;
            for (int j = 0; j < n; ++j) {
                int i2 = integerArray[j];
                if (level.distance(i2, level.pointToCell(d)) > 6) continue;
                lootPositions.remove((Object)i2);
            }
        }
        if (lootPositions.isEmpty()) {
            lootPositions.add(level.pointToCell(c));
        }
        if ((i = level.findPrizeItem(EquipableItem.class)) != null) {
            level.drop(i, (Integer)lootPositions.get(0));
        }
        Mob enemy = level.createMob();
        if (this.wide()) {
            enemy.setupStealthGameplayWanderPositions(new int[]{level.pointToCell(new Point(this.left + 2, c.y)), level.pointToCell(new Point(this.right - 2, c.y))}, Random.Int(2));
        } else {
            enemy.setupStealthGameplayWanderPositions(new int[]{level.pointToCell(new Point(c.x, this.top + 2)), level.pointToCell(new Point(c.x, this.bottom - 2))}, Random.Int(2));
        }
        enemy.pos = level.pointToCell(c);
        enemy.state = enemy.WANDERING;
        level.mobs.add(enemy);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            if (this.wide()) {
                if (door.x == this.left) {
                    Painter.drawLine(level, new Point(door.x + 1, door.y), new Point(this.left + 1, c.y), 1);
                    continue;
                }
                if (door.x == this.right) {
                    Painter.drawLine(level, new Point(door.x - 1, door.y), new Point(this.right - 1, c.y), 1);
                    continue;
                }
                if (door.x <= this.left + 3 || door.x >= this.right - 3) {
                    Painter.drawInside(level, this, door, 5, 1);
                    continue;
                }
                int closestX = door.x < c.x || door.x == c.x && Random.Int(2) == 0 ? this.left + 6 : this.right - 7;
                if (door.y == this.top) {
                    Painter.drawLine(level, new Point(door.x, door.y + 1), new Point(closestX, door.y + 1), 1);
                    continue;
                }
                Painter.drawLine(level, new Point(door.x, door.y - 1), new Point(closestX, door.y - 1), 1);
                continue;
            }
            if (door.y == this.top) {
                Painter.drawLine(level, new Point(door.x, door.y + 1), new Point(c.x, this.top + 1), 1);
                continue;
            }
            if (door.y == this.bottom) {
                Painter.drawLine(level, new Point(door.x, door.y - 1), new Point(c.x, this.bottom - 1), 1);
                continue;
            }
            if (door.y <= this.top + 3 || door.y >= this.bottom - 3) {
                Painter.drawInside(level, this, door, 5, 1);
                continue;
            }
            int closestY = door.y < c.y || door.y == c.y && Random.Int(2) == 0 ? this.top + 6 : this.bottom - 7;
            if (door.x == this.left) {
                Painter.drawLine(level, new Point(door.x + 1, door.y), new Point(door.x + 1, closestY), 1);
                continue;
            }
            Painter.drawLine(level, new Point(door.x - 1, door.y), new Point(door.x - 1, closestY), 1);
        }
    }
}

