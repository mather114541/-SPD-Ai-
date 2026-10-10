/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class VaultFlamesTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fillEllipse(level, this, 2, 1);
        Point c = this.center();
        int[] treasurePositions = new int[]{level.pointToCell(c) - 3 * level.width(), level.pointToCell(c) + 3, level.pointToCell(c) + 3 * level.width(), level.pointToCell(c) - 3, level.pointToCell(c) - 3 * level.width(), level.pointToCell(c) + 3};
        int treasureIdx = this.entrance().x == this.left ? 1 : (this.entrance().y == this.top ? 2 : (this.entrance().x == this.right ? 3 : 4));
        Painter.set(level, treasurePositions[treasureIdx - 1], 11);
        Painter.set(level, treasurePositions[treasureIdx], 11);
        Painter.set(level, treasurePositions[treasureIdx + 1], 11);
        Item treasureItem = ((VaultLevel)level).createEquipment(2);
        level.drop((Item)treasureItem, (int)treasurePositions[treasureIdx]).type = Heap.Type.CHEST;
        for (int x = this.left + 2; x <= this.right - 2; ++x) {
            for (int y = this.top + 2; y <= this.bottom - 2; ++y) {
                int cell = x + level.width() * y;
                if (level.map[cell] != 1) continue;
                VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, 1, 1, 1);
            }
        }
        if (Math.abs(c.x - this.entrance().x) <= 1 || Math.abs(c.y - this.entrance().y) <= 1) {
            Painter.drawInside(level, this, this.entrance(), 1, 1);
        } else {
            Painter.drawInside(level, this, this.entrance(), 2, 1);
        }
        treasureItem = ((VaultLevel)level).findT3SolveItem();
        if (treasureItem == null) {
            treasureItem = ((VaultLevel)level).createConsumabe(2);
        }
        if (Random.Int(2) == 0) {
            level.drop(treasureItem, treasurePositions[treasureIdx - 1]);
            level.drop(new DwarfToken(), treasurePositions[treasureIdx + 1]);
        } else {
            level.drop(new DwarfToken(), treasurePositions[treasureIdx - 1]);
            level.drop(treasureItem, treasurePositions[treasureIdx + 1]);
        }
        level.addItemToSpawn(new PotionOfPurity());
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

