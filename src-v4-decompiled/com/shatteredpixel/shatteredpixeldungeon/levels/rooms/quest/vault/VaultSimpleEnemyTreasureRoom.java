/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Elemental;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultSimpleEnemyTreasureRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Mob enemy;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        int enemyPos = 0;
        int treasurePos = 0;
        switch (Random.Int(4)) {
            case 0: {
                Painter.fill(level, this.left + 2, this.top + 2, 6, 6, 4);
                Painter.fill(level, this.left + 3, this.top + 3, 4, 4, 14);
                Painter.fill(level, this.left + 4, this.top + 7, 2, 1, 14);
                Painter.fill(level, this.left + 7, this.top + 4, 1, 2, 14);
                enemyPos = level.pointToCell(new Point(this.left + 4, this.top + 4));
                treasurePos = level.pointToCell(new Point(this.left + 3, this.top + 3));
                break;
            }
            case 1: {
                Painter.fill(level, this.left + 3, this.top + 2, 6, 6, 4);
                Painter.fill(level, this.left + 4, this.top + 3, 4, 4, 14);
                Painter.fill(level, this.left + 5, this.top + 7, 2, 1, 14);
                Painter.fill(level, this.left + 3, this.top + 4, 1, 2, 14);
                enemyPos = level.pointToCell(new Point(this.right - 4, this.top + 4));
                treasurePos = level.pointToCell(new Point(this.right - 3, this.top + 3));
                break;
            }
            case 2: {
                Painter.fill(level, this.left + 3, this.top + 3, 6, 6, 4);
                Painter.fill(level, this.left + 4, this.top + 4, 4, 4, 14);
                Painter.fill(level, this.left + 5, this.top + 3, 2, 1, 14);
                Painter.fill(level, this.left + 3, this.top + 5, 1, 2, 14);
                enemyPos = level.pointToCell(new Point(this.right - 4, this.bottom - 4));
                treasurePos = level.pointToCell(new Point(this.right - 3, this.bottom - 3));
                break;
            }
            case 3: {
                Painter.fill(level, this.left + 2, this.top + 3, 6, 6, 4);
                Painter.fill(level, this.left + 3, this.top + 4, 4, 4, 14);
                Painter.fill(level, this.left + 4, this.top + 3, 2, 1, 14);
                Painter.fill(level, this.left + 7, this.top + 5, 1, 2, 14);
                enemyPos = level.pointToCell(new Point(this.left + 4, this.bottom - 4));
                treasurePos = level.pointToCell(new Point(this.left + 3, this.bottom - 3));
            }
        }
        ArrayList toReturn = new ArrayList();
        boolean valid = true;
        do {
            enemy = level.createMob();
            valid = true;
            for (Class<? extends Mob> cls : VaultLevel.T1Mobs) {
                if (!cls.equals(enemy.getClass())) continue;
                valid = false;
                toReturn.add(enemy.getClass());
            }
        } while (!valid);
        for (Class clazz : toReturn) {
            ((VaultLevel)level).returnMob(clazz);
        }
        int tier = 2;
        for (Class<? extends Mob> cls : VaultLevel.T2Mobs) {
            if (!cls.equals(enemy.getClass())) continue;
            tier = 2;
        }
        for (Class<? extends Mob> cls : VaultLevel.T3Mobs) {
            if (!cls.equals(enemy.getClass())) continue;
            tier = 3;
        }
        if (enemy instanceof Elemental) {
            tier = 3;
        }
        Item item = ((VaultLevel)level).createEquipment(tier);
        level.drop((Item)item, (int)treasurePos).type = Heap.Type.CHEST;
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        enemy.pos = enemyPos;
        level.mobs.add(enemy);
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        Point c = this.center();
        if (Math.abs(c.x - p.x) <= 2) {
            return false;
        }
        if (Math.abs(c.y - p.y) <= 2) {
            return false;
        }
        return super.canPlaceItem(p, l);
    }
}

