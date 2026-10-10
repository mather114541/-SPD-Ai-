/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class VaultSingleEnemyTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        int i;
        int treasurePos;
        Painter.fill(level, this, 4);
        Painter.fillEllipse(level, this, 3, 1);
        Painter.drawInside(level, this, this.entrance(), 3, 1);
        Mob enemy = Reflection.newInstance(Random.oneOf(VaultLevel.T2Mobs));
        enemy.pos = level.pointToCell(this.center());
        level.mobs.add(enemy);
        if (this.entrance().x == this.left) {
            treasurePos = enemy.pos + 2;
            ++enemy.pos;
        } else if (this.entrance().y == this.top) {
            treasurePos = enemy.pos + 2 * level.width();
            enemy.pos += level.width();
        } else if (this.entrance().x == this.right) {
            treasurePos = enemy.pos - 2;
            --enemy.pos;
        } else {
            treasurePos = enemy.pos - 2 * level.width();
            enemy.pos -= level.width();
        }
        Item treasureItem = ((VaultLevel)level).createEquipment(2);
        level.drop((Item)treasureItem, (int)treasurePos).type = Heap.Type.CHEST;
        while (level.map[treasurePos + (i = PathFinder.NEIGHBOURS4[Random.Int(PathFinder.NEIGHBOURS4.length)])] == 4 || treasurePos + i == enemy.pos) {
        }
        treasureItem = ((VaultLevel)level).createConsumabe(2);
        level.drop(treasureItem, treasurePos + i);
        this.entrance().set(Room.Door.Type.REGULAR);
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return (Math.abs(c.x - p.x) <= 2 || Math.abs(c.y - p.y) <= 2) && super.canConnect(p);
    }
}

