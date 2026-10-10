/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;

public class TrapsRoom
extends SpecialRoom {
    private static Class<? extends Trap>[][] levelTraps = new Class[][]{{GrippingTrap.class, TeleportationTrap.class, FlockTrap.class}, {PoisonDartTrap.class, GrippingTrap.class, ExplosiveTrap.class}, {PoisonDartTrap.class, FlashingTrap.class, ExplosiveTrap.class}, {WarpingTrap.class, FlashingTrap.class, DisintegrationTrap.class}, {GrimTrap.class}};

    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public int maxWidth() {
        return 8;
    }

    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public int maxHeight() {
        return 8;
    }

    @Override
    public void paint(Level level) {
        Class<? extends Trap> trapClass;
        Painter.fill(level, this, 4);
        switch (Random.Int(4)) {
            case 0: {
                trapClass = null;
                break;
            }
            default: {
                trapClass = Random.oneOf(levelTraps[Dungeon.depth / 5]);
            }
        }
        if (trapClass == null) {
            Painter.fill(level, this, 1, 0);
        } else {
            Painter.fill(level, this, 1, 18);
        }
        Room.Door door = this.entrance();
        door.set(Room.Door.Type.REGULAR);
        int lastRow = level.map[this.left + 1 + (this.top + 1) * level.width()] == 0 ? 0 : 1;
        int x = -1;
        int y = -1;
        if (door.x == this.left) {
            x = this.right - 1;
            y = this.top + this.height() / 2;
            Painter.fill(level, x, this.top + 1, 1, this.height() - 2, lastRow);
        } else if (door.x == this.right) {
            x = this.left + 1;
            y = this.top + this.height() / 2;
            Painter.fill(level, x, this.top + 1, 1, this.height() - 2, lastRow);
        } else if (door.y == this.top) {
            x = this.left + this.width() / 2;
            y = this.bottom - 1;
            Painter.fill(level, this.left + 1, y, this.width() - 2, 1, lastRow);
        } else if (door.y == this.bottom) {
            x = this.left + this.width() / 2;
            y = this.top + 1;
            Painter.fill(level, this.left + 1, y, this.width() - 2, 1, lastRow);
        }
        for (Point p : this.getPoints()) {
            int cell = level.pointToCell(p);
            if (level.map[cell] != 18) continue;
            level.setTrap(Reflection.newInstance(trapClass).reveal(), cell);
        }
        int pos = x + y * level.width();
        if (Random.Int(3) == 0) {
            if (lastRow == 0) {
                Painter.set(level, pos, 1);
            }
            level.drop((Item)TrapsRoom.prize((Level)level), (int)pos).type = Heap.Type.CHEST;
        } else {
            Painter.set(level, pos, 11);
            level.drop((Item)TrapsRoom.prize((Level)level), (int)pos).type = Heap.Type.CHEST;
        }
        level.addItemToSpawn(new PotionOfLevitation());
    }

    private static Item prize(Level level) {
        Item prize;
        if (Random.Int(3) != 0 && (prize = level.findPrizeItem()) != null) {
            return prize;
        }
        if (Random.Int(2) == 0) {
            prize = Generator.randomWeapon(Dungeon.depth / 5 + 1);
            if (((Weapon)prize).hasCurseEnchant()) {
                ((Weapon)prize).enchant(null);
            }
        } else {
            prize = Generator.randomArmor(Dungeon.depth / 5 + 1);
            if (((Armor)prize).hasCurseGlyph()) {
                ((Armor)prize).inscribe(null);
            }
        }
        prize.cursed = false;
        prize.cursedKnown = true;
        if (Random.Int(3) == 0) {
            prize.upgrade();
        }
        return prize;
    }
}

