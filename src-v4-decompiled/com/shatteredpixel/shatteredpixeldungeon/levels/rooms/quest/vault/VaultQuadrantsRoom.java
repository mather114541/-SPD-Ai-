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

public class VaultQuadrantsRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Point c = this.center();
        Painter.drawInside(level, this, new Point(this.left, c.y), 3, 4);
        Painter.drawInside(level, this, new Point(this.right, c.y), 3, 4);
        Painter.drawInside(level, this, new Point(c.x, this.top), 3, 4);
        Painter.drawInside(level, this, new Point(c.x, this.bottom), 3, 4);
        Painter.set(level, c, 25);
        for (Point[] door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        ArrayList<Point> spawnPositions = new ArrayList<Point>();
        spawnPositions.add(new Point(this.left + 2, this.top + 2));
        spawnPositions.add(new Point(this.right - 2, this.top + 2));
        spawnPositions.add(new Point(this.right - 2, this.bottom - 2));
        spawnPositions.add(new Point(this.left + 2, this.bottom - 2));
        for (Point point : spawnPositions.toArray(new Point[0])) {
            for (Room.Door door : this.connected.values()) {
                if (!(Point.distance(point, door) <= 3.0f)) continue;
                spawnPositions.remove(point);
            }
        }
        if (!spawnPositions.isEmpty()) {
            Mob enemy = level.createMob();
            Point enemyCorner = (Point)Random.element(spawnPositions);
            enemy.pos = level.pointToCell(enemyCorner);
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
            Item item = ((VaultLevel)level).createEquipment(tier);
            int treasurePos = enemy.pos;
            treasurePos = enemyCorner.x < c.x ? --treasurePos : ++treasurePos;
            treasurePos = enemyCorner.y < c.y ? (treasurePos -= level.width()) : (treasurePos += level.width());
            level.drop((Item)item, (int)treasurePos).type = Heap.Type.CHEST;
        }
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return c.x != p.x && c.y != p.y && super.canConnect(p);
    }
}

