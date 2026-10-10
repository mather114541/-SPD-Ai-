/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultRingsRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Mob enemy;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this.left + 2, this.top + 2, 3, 3, 4);
        Painter.fill(level, this.right - 4, this.top + 2, 3, 3, 4);
        Painter.fill(level, this.left + 2, this.bottom - 4, 3, 3, 4);
        Painter.fill(level, this.right - 4, this.bottom - 4, 3, 3, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        ArrayList toReturn = new ArrayList();
        do {
            if (!Char.hasProp(enemy = level.createMob(), Char.Property.LARGE)) continue;
            toReturn.add(enemy.getClass());
        } while (Char.hasProp(enemy, Char.Property.LARGE));
        do {
            enemy.pos = level.pointToCell(this.random(1));
        } while (level.map[enemy.pos] == 4);
        enemy.state = enemy.WANDERING;
        level.mobs.add(enemy);
        int[] wanderPositions = new int[]{level.pointToCell(new Point(this.left + 1, this.top + 1)), level.pointToCell(new Point(this.left + 1, this.top + 5)), level.pointToCell(new Point(this.left + 1, this.top + 9)), level.pointToCell(new Point(this.left + 5, this.top + 1)), level.pointToCell(new Point(this.left + 5, this.top + 5)), level.pointToCell(new Point(this.left + 5, this.top + 9)), level.pointToCell(new Point(this.left + 9, this.top + 1)), level.pointToCell(new Point(this.left + 9, this.top + 5)), level.pointToCell(new Point(this.left + 9, this.top + 9))};
        Random.shuffle(wanderPositions);
        enemy.setupStealthGameplayWanderPositions(wanderPositions, 0);
        for (Class clazz : toReturn) {
            ((VaultLevel)level).returnMob(clazz);
        }
    }
}

