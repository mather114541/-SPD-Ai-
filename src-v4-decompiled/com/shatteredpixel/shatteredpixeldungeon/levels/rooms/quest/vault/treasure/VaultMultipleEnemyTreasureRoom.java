/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultGhoul;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class VaultMultipleEnemyTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        int i;
        int treasurePos;
        VaultGhoul ghoul;
        Painter.fill(level, this, 4);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        Point c = this.center();
        if (entrance.x == this.left || entrance.x == this.right) {
            int areaTop = (int)GameMath.gate(this.top + 1, entrance.y - 2, this.bottom - 5);
            Painter.fill(level, this.left + 1, areaTop + 1, 9, 3, 14);
            Painter.fill(level, this.left + 4, areaTop, 3, 5, 14);
            ghoul = new VaultGhoul();
            ghoul.maxLvl = 0;
            ghoul.pos = c.x + areaTop * level.width();
            level.mobs.add(ghoul);
            ghoul = new VaultGhoul();
            ghoul.maxLvl = 0;
            ghoul.pos = c.x + (areaTop + 4) * level.width();
            level.mobs.add(ghoul);
            ghoul = new VaultGhoul();
            if (entrance.x == this.left) {
                ghoul.pos = c.x + 1 + (areaTop + 2) * level.width();
                treasurePos = ghoul.pos + 2;
            } else {
                ghoul.pos = c.x - 1 + (areaTop + 2) * level.width();
                treasurePos = ghoul.pos - 2;
            }
            level.mobs.add(ghoul);
        } else {
            int areaLeft = (int)GameMath.gate(this.left + 1, entrance.x - 2, this.right - 5);
            Painter.fill(level, areaLeft + 1, this.top + 1, 3, 9, 14);
            Painter.fill(level, areaLeft, this.top + 4, 5, 3, 14);
            ghoul = new VaultGhoul();
            ghoul.maxLvl = 0;
            ghoul.pos = areaLeft + c.y * level.width();
            level.mobs.add(ghoul);
            ghoul = new VaultGhoul();
            ghoul.maxLvl = 0;
            ghoul.pos = areaLeft + 4 + c.y * level.width();
            level.mobs.add(ghoul);
            ghoul = new VaultGhoul();
            if (entrance.y == this.top) {
                ghoul.pos = areaLeft + 2 + (c.y + 1) * level.width();
                treasurePos = ghoul.pos + 2 * level.width();
            } else {
                ghoul.pos = areaLeft + 2 + (c.y - 1) * level.width();
                treasurePos = ghoul.pos - 2 * level.width();
            }
            level.mobs.add(ghoul);
        }
        Painter.set(level, treasurePos, 11);
        Item treasureItem = ((VaultLevel)level).createEquipment(3);
        level.drop((Item)treasureItem, (int)treasurePos).type = Heap.Type.CHEST;
        while (level.map[treasurePos + (i = PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)])] == 4) {
        }
        treasureItem = ((VaultLevel)level).createConsumabe(3);
        level.drop(treasureItem, treasurePos + i);
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return (Math.abs(c.x - p.x) <= 3 || Math.abs(c.y - p.y) <= 3) && super.canConnect(p);
    }
}

