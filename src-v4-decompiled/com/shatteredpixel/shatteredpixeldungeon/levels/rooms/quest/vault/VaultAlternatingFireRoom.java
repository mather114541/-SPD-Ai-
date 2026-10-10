/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault;

import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.VaultRoom;
import com.watabou.utils.Point;

public class VaultAlternatingFireRoom
extends VaultRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Point c = this.center();
        Painter.set(level, c, 11);
        Item i = ((VaultLevel)level).createEquipment(0);
        if (i != null) {
            level.drop(i, level.pointToCell(c));
        }
        boolean alternate = false;
        for (int x = this.left + 1; x <= this.right - 1; ++x) {
            for (int y = this.top + 1; y <= this.bottom - 1; ++y) {
                int cell = x + y * level.width();
                if (level.map[cell] != 11) {
                    VaultLevel.VaultFlameTrap.setupTrap(level, cell, alternate ? 1 : 0, 2, 1);
                }
                alternate = !alternate;
            }
        }
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && p == this.center();
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }
}

