/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DwarfToken;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.vault.treasure.VaultTreasureRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class VaultFlamePathRoom
extends VaultTreasureRoom {
    final int LEFT = 0;
    final int UP = 1;
    final int RIGHT = 2;
    final int DOWN = 3;

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.REGULAR);
        Point c = this.center();
        Rect leftSide = new Rect(this.left + 1, this.top + 1, this.left + 3, this.bottom - 1);
        Rect topSide = new Rect(this.left + 1, this.top + 1, this.right - 1, this.top + 3);
        Rect rightSide = new Rect(this.right - 3, this.top + 1, this.right - 1, this.bottom - 1);
        Rect bottomSide = new Rect(this.left + 1, this.bottom - 3, this.right - 1, this.bottom - 1);
        Rect treasure = null;
        if (entrance.x == this.left) {
            if (entrance.y < c.y) {
                this.fillGroup(level, bottomSide, 2);
                this.fillGroup(level, rightSide, 1);
                this.fillGroup(level, topSide, 0);
                Painter.set(level, this.left + 1 + level.width() * (this.top + 6), 14);
            } else {
                this.fillGroup(level, topSide, 2);
                this.fillGroup(level, rightSide, 3);
                this.fillGroup(level, bottomSide, 0);
                Painter.set(level, this.left + 1 + level.width() * (this.top + 4), 14);
            }
            treasure = new Rect(this.left + 1, this.top + 5, this.left + 5, this.top + 5);
        } else if (entrance.y == this.top) {
            if (entrance.x < c.x) {
                this.fillGroup(level, rightSide, 3);
                this.fillGroup(level, bottomSide, 0);
                this.fillGroup(level, leftSide, 1);
                Painter.set(level, this.left + 6 + level.width() * (this.top + 1), 14);
            } else {
                this.fillGroup(level, leftSide, 3);
                this.fillGroup(level, bottomSide, 2);
                this.fillGroup(level, rightSide, 1);
                Painter.set(level, this.left + 4 + level.width() * (this.top + 1), 14);
            }
            treasure = new Rect(this.left + 5, this.top + 1, this.left + 5, this.top + 5);
        } else if (entrance.x == this.right) {
            if (entrance.y < c.y) {
                this.fillGroup(level, bottomSide, 0);
                this.fillGroup(level, leftSide, 1);
                this.fillGroup(level, topSide, 2);
                Painter.set(level, this.right - 1 + level.width() * (this.top + 6), 14);
            } else {
                this.fillGroup(level, topSide, 0);
                this.fillGroup(level, leftSide, 3);
                this.fillGroup(level, bottomSide, 2);
                Painter.set(level, this.right - 1 + level.width() * (this.top + 4), 14);
            }
            treasure = new Rect(this.right - 5, this.top + 5, this.right - 1, this.top + 5);
        } else if (entrance.y == this.bottom) {
            if (entrance.x < c.x) {
                this.fillGroup(level, rightSide, 1);
                this.fillGroup(level, topSide, 0);
                this.fillGroup(level, leftSide, 3);
                Painter.set(level, this.left + 6 + level.width() * (this.bottom - 1), 14);
            } else {
                this.fillGroup(level, leftSide, 1);
                this.fillGroup(level, topSide, 2);
                this.fillGroup(level, rightSide, 3);
                Painter.set(level, this.left + 4 + level.width() * (this.bottom - 1), 14);
            }
            treasure = new Rect(this.left + 5, this.bottom - 5, this.left + 5, this.bottom - 1);
        }
        Painter.fill(level, treasure.left, treasure.top, treasure.width() + 1, treasure.height() + 1, 14);
        int treasurePos = level.pointToCell(Random.element(treasure.getPoints()));
        Item treasureItem = ((VaultLevel)level).createEquipment(1);
        level.drop((Item)treasureItem, (int)treasurePos).type = Heap.Type.CHEST;
        treasureItem = ((VaultLevel)level).findT2SolveItem();
        if (treasureItem == null) {
            treasureItem = ((VaultLevel)level).createConsumabe(1);
        }
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(treasure.getPoints()))) != null) {
        }
        level.drop(treasureItem, treasurePos);
        while (level.heaps.get(treasurePos = level.pointToCell(Random.element(treasure.getPoints()))) != null) {
        }
        level.drop(new DwarfToken(), treasurePos);
    }

    public void fillGroup(Level level, Rect space, int dir) {
        block13: {
            ArrayList<Integer> priorOffsets;
            block15: {
                block14: {
                    block12: {
                        priorOffsets = new ArrayList<Integer>();
                        if (dir != 0) break block12;
                        for (int y = space.top; y <= space.bottom; ++y) {
                            int ofs;
                            while (priorOffsets.contains(ofs = Random.Int(5))) {
                            }
                            int delay = 0;
                            for (int x = space.right; x >= space.left; --x) {
                                VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, delay + ofs, 5, 2);
                                ++delay;
                            }
                            priorOffsets.add(ofs);
                        }
                        break block13;
                    }
                    if (dir != 2) break block14;
                    for (int y = space.top; y <= space.bottom; ++y) {
                        int ofs;
                        while (priorOffsets.contains(ofs = Random.Int(5))) {
                        }
                        int delay = 0;
                        for (int x = space.left; x <= space.right; ++x) {
                            VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, delay + ofs, 5, 2);
                            ++delay;
                        }
                        priorOffsets.add(ofs);
                    }
                    break block13;
                }
                if (dir != 1) break block15;
                for (int x = space.left; x <= space.right; ++x) {
                    int ofs;
                    while (priorOffsets.contains(ofs = Random.Int(5))) {
                    }
                    int delay = 0;
                    for (int y = space.bottom; y >= space.top; --y) {
                        VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, delay + ofs, 5, 2);
                        ++delay;
                    }
                    priorOffsets.add(ofs);
                }
                break block13;
            }
            if (dir != 3) break block13;
            for (int x = space.left; x <= space.right; ++x) {
                int ofs;
                while (priorOffsets.contains(ofs = Random.Int(5))) {
                }
                int delay = 0;
                for (int y = space.top; y <= space.bottom; ++y) {
                    VaultLevel.VaultFlameTrap.setupTrap(level, x + level.width() * y, delay + ofs, 5, 2);
                    ++delay;
                }
                priorOffsets.add(ofs);
            }
        }
    }

    @Override
    public boolean canConnect(Point p) {
        Point c = this.center();
        return Math.abs(c.x - p.x) > 1 && Math.abs(c.y - p.y) > 1 && super.canConnect(p);
    }
}

