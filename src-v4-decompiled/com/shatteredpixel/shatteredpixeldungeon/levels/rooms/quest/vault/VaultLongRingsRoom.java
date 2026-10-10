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

public class VaultLongRingsRoom
extends VaultLongRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 4, 4);
        if (this.wide()) {
            Painter.fill(level, this, 8, 4, 8, 4, 1);
        } else {
            Painter.fill(level, this, 4, 8, 4, 8, 1);
        }
        Point c = this.center();
        Item i = level.findPrizeItem(EquipableItem.class);
        if (i != null) {
            level.drop(i, level.pointToCell(c));
        }
        for (int n = 0; n < 2; ++n) {
            Mob enemy = level.createMob();
            int[] wanderPositions = new int[20];
            wanderPositions[0] = this.randomWander(level, -1);
            for (int idx = 1; idx < wanderPositions.length; ++idx) {
                wanderPositions[idx] = this.randomWander(level, wanderPositions[idx - 1]);
            }
            enemy.pos = wanderPositions[0];
            enemy.setupStealthGameplayWanderPositions(wanderPositions, 0);
            enemy.state = enemy.WANDERING;
            level.mobs.add(enemy);
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
    }

    private int randomWander(Level level, int previous) {
        int pos;
        while (level.map[pos = level.pointToCell(this.random(1))] == 4 || level.map[pos] == 12 || level.distance(pos, previous) < 6) {
        }
        return pos;
    }
}

