/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultMirror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.VaultTokenDoor;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultLongRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class VaultTokensRoom
extends VaultLongRoom {
    @Override
    public void paint(Level level) {
        Mob enemy;
        Painter.fill(level, this, 4);
        Point c = this.center();
        if (this.wide()) {
            int leftDoor = 0;
            int rightDoor = 0;
            for (Room.Door d : this.connected.values()) {
                if (d.x < c.x) {
                    leftDoor = 1;
                    Painter.fill(level, this.left + 1, this.top + 1, (this.width() - 2) / 2, this.height() - 2, 1);
                    continue;
                }
                rightDoor = 1;
                Painter.fill(level, c.x + 1, this.top + 1, (this.width() - 2) / 2, this.height() - 2, 1);
            }
            Painter.fillDiamond(level, this.left + 3, this.top + 1, 9, 9, 4);
            Painter.fillDiamond(level, this.left + 9, this.top + 1, 9, 9, 4);
            Painter.fillDiamond(level, this.left + 5, this.top + 1, 9, 9, 14);
            Painter.fillDiamond(level, this.left + 7, this.top + 1, 9, 9, 14);
            Painter.fill(level, this.left + 4 - leftDoor, c.y, 13 + leftDoor + rightDoor, 1, 1);
            Painter.fill(level, this.left + 4, c.y, 13, 1, 14);
        } else {
            int topdoor = 0;
            int bottomDoor = 0;
            for (Room.Door d : this.connected.values()) {
                if (d.y < c.y) {
                    topdoor = 1;
                    Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, (this.height() - 2) / 2, 1);
                    continue;
                }
                bottomDoor = 1;
                Painter.fill(level, this.left + 1, c.y + 1, this.width() - 2, (this.height() - 2) / 2, 1);
            }
            Painter.fillDiamond(level, this.left + 1, this.top + 3, 9, 9, 4);
            Painter.fillDiamond(level, this.left + 1, this.top + 9, 9, 9, 4);
            Painter.fillDiamond(level, this.left + 1, this.top + 5, 9, 9, 14);
            Painter.fillDiamond(level, this.left + 1, this.top + 7, 9, 9, 14);
            Painter.fill(level, c.x, this.top + 4 - topdoor, 1, 13 + topdoor + bottomDoor, 1);
            Painter.fill(level, c.x, this.top + 4, 1, 13, 14);
        }
        Painter.fillDiamond(level, c.x - 3, c.y - 3, 7, 7, 4);
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, 14);
        Painter.fill(level, c.x - 2, c.y, 5, 1, 14);
        Painter.fill(level, c.x, c.y - 1, 1, 5, 14);
        Painter.set(level, c.x - 1, c.y - 1, 34);
        Painter.set(level, c.x + 1, c.y - 1, 34);
        Carpet carpet = new Carpet();
        carpet.setRect(c.x - 1, c.y - 1, 3, 3);
        carpet.overrideTile(0, 0, 88);
        carpet.overrideTile(2, 0, 87);
        level.customTiles.add(carpet);
        Painter.set(level, c.x, c.y + 3, 10);
        VaultTokenDoor door = new VaultTokenDoor();
        door.pos = c.x + (c.y + 3) * level.width();
        level.mobs.add(door);
        VaultMirror mirror = new VaultMirror();
        mirror.createReward(Dungeon.hero.heroClass);
        mirror.pos = c.x + (c.y - 1) * level.width();
        level.mobs.add(mirror);
        if (Random.Int(2) == 0) {
            level.drop(((VaultLevel)level).createEquipment(3), c.x - 2 + c.y * level.width());
            level.drop(((VaultLevel)level).createConsumabe(3), c.x + 2 + c.y * level.width());
        } else {
            level.drop(((VaultLevel)level).createEquipment(3), c.x + 2 + c.y * level.width());
            level.drop(((VaultLevel)level).createConsumabe(3), c.x - 2 + c.y * level.width());
        }
        ArrayList toReturn = new ArrayList();
        do {
            if (!Char.hasProp(enemy = level.createMob(), Char.Property.LARGE)) continue;
            toReturn.add(enemy.getClass());
        } while (Char.hasProp(enemy, Char.Property.LARGE));
        int[] wanderPositions = Random.Int(2) == 0 ? new int[]{level.pointToCell(new Point(c.x - 4, c.y)), level.pointToCell(new Point(c.x, c.y - 4)), level.pointToCell(new Point(c.x + 4, c.y)), level.pointToCell(new Point(c.x, c.y + 4))} : new int[]{level.pointToCell(new Point(c.x - 4, c.y)), level.pointToCell(new Point(c.x, c.y - 4)), level.pointToCell(new Point(c.x + 4, c.y)), level.pointToCell(new Point(c.x, c.y + 4))};
        int idx = Random.Int(4);
        enemy.pos = wanderPositions[idx];
        enemy.setupStealthGameplayWanderPositions(wanderPositions, idx);
        enemy.state = enemy.WANDERING;
        level.mobs.add(enemy);
        for (Class clazz : toReturn) {
            ((VaultLevel)level).returnMob(clazz);
        }
    }

    @Override
    public boolean canConnect(Point p) {
        if (!super.canConnect(p)) {
            return false;
        }
        if (this.wide()) {
            return p.x < this.left + 5 || p.x > this.right - 5;
        }
        return p.y < this.top + 5 || p.y > this.bottom - 5;
    }

    @Override
    public boolean canConnect(Room r) {
        if (r.isEntrance()) {
            return false;
        }
        for (Room r1 : r.connected.keySet()) {
            if (r1.isEntrance()) {
                return false;
            }
            for (Room r2 : r1.connected.keySet()) {
                if (!r2.isEntrance()) continue;
                return false;
            }
        }
        return super.canConnect(r);
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && l.map[l.pointToCell(p)] != 14;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.map[l.pointToCell(p)] != 14;
    }
}

