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

public class VaultEnemyCenterRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 2, 4);
        Painter.fill(level, this, 3, 1);
        Painter.drawLine(level, new Point(this.left + 1, this.top + 3), new Point(this.right - 1, this.top + 3), 1);
        Painter.drawLine(level, new Point(this.left + 1, this.bottom - 3), new Point(this.right - 1, this.bottom - 3), 1);
        Painter.drawLine(level, new Point(this.left + 3, this.top + 1), new Point(this.left + 3, this.bottom - 1), 1);
        Painter.drawLine(level, new Point(this.right - 3, this.top + 1), new Point(this.right - 3, this.bottom - 1), 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Mob enemy = level.createMob();
        Point c = this.center();
        int[] wanderPositions = Random.Int(2) == 0 ? new int[]{level.pointToCell(new Point(c.x - 1, c.y - 1)), level.pointToCell(new Point(c.x + 1, c.y - 1)), level.pointToCell(new Point(c.x + 1, c.y + 1)), level.pointToCell(new Point(c.x - 1, c.y + 1))} : new int[]{level.pointToCell(new Point(c.x - 1, c.y - 1)), level.pointToCell(new Point(c.x - 1, c.y + 1)), level.pointToCell(new Point(c.x + 1, c.y + 1)), level.pointToCell(new Point(c.x + 1, c.y - 1))};
        int idx = Random.Int(4);
        enemy.pos = wanderPositions[idx];
        enemy.setupStealthGameplayWanderPositions(wanderPositions, idx);
        enemy.state = enemy.WANDERING;
        level.mobs.add(enemy);
        int tier = 1;
        for (Class<? extends Mob> cls : VaultLevel.T1Mobs) {
            if (!cls.equals(enemy.getClass())) continue;
            tier = 1;
        }
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
        Item treasure = ((VaultLevel)level).createEquipment(tier);
        level.drop((Item)treasure, (int)level.pointToCell((Point)c)).type = Heap.Type.CHEST;
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

