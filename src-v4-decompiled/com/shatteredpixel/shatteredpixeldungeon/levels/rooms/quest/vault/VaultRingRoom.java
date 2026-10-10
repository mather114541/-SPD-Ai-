/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class VaultRingRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 4, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Mob enemy = level.createMob();
        int[] wanderPositions = Random.Int(2) == 0 ? new int[]{level.pointToCell(new Point(this.left + 2, this.top + 2)), level.pointToCell(new Point(this.right - 2, this.top + 2)), level.pointToCell(new Point(this.right - 2, this.bottom - 2)), level.pointToCell(new Point(this.left + 2, this.bottom - 2))} : new int[]{level.pointToCell(new Point(this.left + 2, this.bottom - 2)), level.pointToCell(new Point(this.right - 2, this.bottom - 2)), level.pointToCell(new Point(this.right - 2, this.top + 2)), level.pointToCell(new Point(this.left + 2, this.top + 2))};
        int idx = Random.Int(4);
        enemy.pos = wanderPositions[idx];
        enemy.setupStealthGameplayWanderPositions(wanderPositions, idx);
        enemy.state = enemy.WANDERING;
        level.mobs.add(enemy);
    }
}

