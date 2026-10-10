/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultLaser;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class VaultLasersRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        int cell;
        VaultLaser laser;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 2, 1);
        for (Room.Door door : this.connected.values()) {
            Painter.drawInside(level, this, door, 2, 1);
            door.set(Room.Door.Type.REGULAR);
        }
        for (int x = this.left + 2; x <= this.right - 2; ++x) {
            if (level.map[x + (this.top + 1) * level.width()] != 4 || level.map[x + (this.bottom - 1) * level.width()] != 4) continue;
            laser = new VaultLaser();
            if (Random.Int(2) == 0) {
                cell = x + level.width() * (this.top + 1);
                Painter.set(level, cell, 11);
                laser.laserDirs = new int[]{cell + level.width()};
                laser.pos = cell;
            } else {
                cell = x + level.width() * (this.bottom - 1);
                Painter.set(level, cell, 11);
                laser.laserDirs = new int[]{cell - level.width()};
                laser.pos = cell;
            }
            laser.afterShotCooldown = Random.IntRange(3, 7);
            laser.curCooldown = Random.IntRange(1, laser.afterShotCooldown);
            level.mobs.add(laser);
        }
        for (int y = this.top + 2; y <= this.bottom - 2; ++y) {
            if (level.map[this.left + 1 + y * level.width()] != 4 || level.map[this.right - 1 + y * level.width()] != 4) continue;
            laser = new VaultLaser();
            if (Random.Int(2) == 0) {
                cell = this.left + 1 + level.width() * y;
                Painter.set(level, cell, 11);
                laser.laserDirs = new int[]{cell + 1};
                laser.pos = cell;
            } else {
                cell = this.right - 1 + level.width() * y;
                Painter.set(level, cell, 11);
                laser.laserDirs = new int[]{cell - 1};
                laser.pos = cell;
            }
            laser.afterShotCooldown = Random.IntRange(3, 7);
            laser.curCooldown = Random.IntRange(1, laser.afterShotCooldown);
            level.mobs.add(laser);
        }
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }
}

