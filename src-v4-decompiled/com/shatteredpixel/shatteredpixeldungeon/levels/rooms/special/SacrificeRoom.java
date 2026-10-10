/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SacrificeRoom
extends SpecialRoom {
    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        Point p;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 0);
        Point c = this.center();
        Room.Door door = this.entrance();
        if (door.x == this.left || door.x == this.right) {
            if (door.y == c.y) {
                c.y = c.y + (Random.Int(2) == 0 ? -1 : 1);
            }
            p = Painter.drawInside(level, this, door, Math.abs(door.x - c.x) - 2, 14);
            while (p.y != c.y) {
                Painter.set(level, p, 14);
                p.y = p.y + (p.y < c.y ? 1 : -1);
            }
        } else {
            if (door.x == c.x) {
                c.x = c.x + (Random.Int(2) == 0 ? -1 : 1);
            }
            p = Painter.drawInside(level, this, door, Math.abs(door.y - c.y) - 2, 14);
            while (p.x != c.x) {
                Painter.set(level, p, 14);
                p.x = p.x + (p.x < c.x ? 1 : -1);
            }
        }
        Point statue = new Point(c);
        statue.x -= 2;
        if (statue.x > this.left) {
            Painter.set(level, statue, 25);
        }
        statue.x += 2;
        statue.y -= 2;
        if (statue.y > this.top) {
            Painter.set(level, statue, 25);
        }
        statue.y += 2;
        statue.x += 2;
        if (statue.x < this.right) {
            Painter.set(level, statue, 25);
        }
        statue.x -= 2;
        statue.y += 2;
        if (statue.y < this.bottom) {
            Painter.set(level, statue, 25);
        }
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, 9);
        Painter.set(level, c, 11);
        Blob.seed(level.pointToCell(c), 6 + Dungeon.depth * 4, SacrificialFire.class, level).setPrize(SacrificeRoom.prize(level));
        door.set(Room.Door.Type.EMPTY);
    }

    public static Item prize(Level level) {
        MeleeWeapon prize = Generator.randomWeapon(Dungeon.depth / 5 + 1);
        if (Challenges.isItemBlocked(prize)) {
            return new Gold().random();
        }
        Weapon.Enchantment curse = Weapon.Enchantment.randomCurse(new Class[0]);
        if (!prize.cursed) {
            prize.upgrade();
            if (!prize.hasGoodEnchant()) {
                prize.enchant(curse);
            }
        }
        prize.cursedKnown = true;
        prize.cursed = true;
        return prize;
    }
}

