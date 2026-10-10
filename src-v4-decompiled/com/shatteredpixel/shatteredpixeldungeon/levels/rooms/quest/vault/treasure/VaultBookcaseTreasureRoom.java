/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class VaultBookcaseTreasureRoom
extends VaultTreasureRoom {
    @Override
    public void paint(Level level) {
        int secondItem;
        int firstItem;
        Painter.fill(level, this, 4);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        if (entrance.x == this.left || entrance.x == this.right) {
            int bookTop = (int)GameMath.gate(this.top + 1, entrance.y - 2, this.bottom - 5);
            Painter.fill(level, this.left + 1, bookTop, 9, 5, 27);
            Painter.fill(level, this.left + 2, bookTop + 1, 3, 3, 14);
            Painter.fill(level, this.left + 6, bookTop + 1, 3, 3, 14);
            if (entrance.x == this.left) {
                firstItem = this.left + 3 + level.width() * (bookTop + 2);
                secondItem = this.right - 3 + level.width() * (bookTop + 2);
            } else {
                firstItem = this.right - 3 + level.width() * (bookTop + 2);
                secondItem = this.left + 3 + level.width() * (bookTop + 2);
            }
        } else {
            int bookLeft = (int)GameMath.gate(this.left + 1, entrance.x - 2, this.right - 5);
            Painter.fill(level, bookLeft, this.top + 1, 5, 9, 27);
            Painter.fill(level, bookLeft + 1, this.top + 2, 3, 3, 14);
            Painter.fill(level, bookLeft + 1, this.top + 6, 3, 3, 14);
            if (entrance.y == this.top) {
                firstItem = bookLeft + 2 + level.width() * (this.top + 3);
                secondItem = bookLeft + 2 + level.width() * (this.bottom - 3);
            } else {
                firstItem = bookLeft + 2 + level.width() * (this.bottom - 3);
                secondItem = bookLeft + 2 + level.width() * (this.top + 3);
            }
        }
        Painter.set(level, firstItem, 11);
        Painter.set(level, secondItem, 11);
        Item treasureItem = level.findPrizeItem();
        if (treasureItem != null) {
            level.drop(treasureItem, firstItem);
        }
        treasureItem = ((VaultLevel)level).createEquipment(2);
        level.drop((Item)treasureItem, (int)secondItem).type = Heap.Type.CHEST;
        treasureItem = ((VaultLevel)level).findT3SolveItem();
        if (treasureItem == null) {
            treasureItem = ((VaultLevel)level).createConsumabe(2);
        }
        level.drop(treasureItem, secondItem + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)]);
        treasureItem = new DwarfToken();
        level.drop(treasureItem, secondItem + PathFinder.NEIGHBOURS8[Random.Int(PathFinder.NEIGHBOURS8.length)]);
        level.addItemToSpawn(new PotionOfLiquidFlame());
        Painter.drawInside(level, this, entrance, 2, 14);
    }
}

