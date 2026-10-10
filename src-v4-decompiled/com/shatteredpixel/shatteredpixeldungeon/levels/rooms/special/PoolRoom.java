/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Random;

public class PoolRoom
extends SpecialRoom {
    private static final int NPIRANHAS = 3;

    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 29);
        Room.Door door = this.entrance();
        door.set(Room.Door.Type.REGULAR);
        int x = -1;
        int y = -1;
        if (door.x == this.left) {
            x = this.right - 1;
            y = this.top + this.height() / 2;
            Painter.fill(level, this.left + 1, this.top + 1, 1, this.height() - 2, 14);
        } else if (door.x == this.right) {
            x = this.left + 1;
            y = this.top + this.height() / 2;
            Painter.fill(level, this.right - 1, this.top + 1, 1, this.height() - 2, 14);
        } else if (door.y == this.top) {
            x = this.left + this.width() / 2;
            y = this.bottom - 1;
            Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, 1, 14);
        } else if (door.y == this.bottom) {
            x = this.left + this.width() / 2;
            y = this.top + 1;
            Painter.fill(level, this.left + 1, this.bottom - 1, this.width() - 2, 1, 14);
        }
        int pos = x + y * level.width();
        level.drop((Item)PoolRoom.prize((Level)level), (int)pos).type = Heap.Type.CHEST;
        Painter.set(level, pos, 11);
        level.addItemToSpawn(new PotionOfInvisibility());
        for (int i = 0; i < 3; ++i) {
            Piranha piranha = Piranha.random();
            do {
                piranha.pos = level.pointToCell(this.random());
            } while (level.map[piranha.pos] != 29 || level.findMob(piranha.pos) != null);
            level.mobs.add(piranha);
        }
    }

    private static Item prize(Level level) {
        Item prize;
        if (Random.Int(3) == 0 && (prize = level.findPrizeItem()) != null) {
            return prize;
        }
        switch (Random.Int(5)) {
            default: {
                prize = Generator.randomWeapon(Dungeon.depth / 5 + 1);
                if (!((Weapon)prize).hasCurseEnchant()) break;
                ((Weapon)prize).enchant(null);
                break;
            }
            case 2: {
                prize = Generator.randomMissile(Dungeon.depth / 5 + 1);
                if (!((Weapon)prize).hasCurseEnchant()) break;
                ((Weapon)prize).enchant(null);
                break;
            }
            case 3: 
            case 4: {
                prize = Generator.randomArmor(Dungeon.depth / 5 + 1);
                if (!((Armor)prize).hasCurseGlyph()) break;
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

