/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.Point;

public class SecretRunestoneRoom
extends SecretRoom {
    @Override
    public void paint(Level level) {
        int dropPos;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Room.Door entrance = this.entrance();
        Point center = this.center();
        if (entrance.x == this.left || entrance.x == this.right) {
            Painter.drawLine(level, new Point(center.x, this.top + 1), new Point(center.x, this.bottom - 1), 27);
            if (entrance.x == this.left) {
                Painter.fill(level, center.x + 1, this.top + 1, this.right - center.x - 1, this.height() - 2, 14);
            } else {
                Painter.fill(level, this.left + 1, this.top + 1, center.x - this.left - 1, this.height() - 2, 14);
            }
        } else {
            Painter.drawLine(level, new Point(this.left + 1, center.y), new Point(this.right - 1, center.y), 27);
            if (entrance.y == this.top) {
                Painter.fill(level, this.left + 1, center.y + 1, this.width() - 2, this.bottom - center.y - 1, 14);
            } else {
                Painter.fill(level, this.left + 1, this.top + 1, this.width() - 2, center.y - this.top - 1, 14);
            }
        }
        level.addItemToSpawn(new PotionOfLiquidFlame());
        while (level.map[dropPos = level.pointToCell(this.random())] != 1) {
        }
        level.drop(Generator.randomUsingDefaults(Generator.Category.STONE), dropPos);
        while (level.map[dropPos = level.pointToCell(this.random())] != 1 || level.heaps.get(dropPos) != null) {
        }
        level.drop(Generator.randomUsingDefaults(Generator.Category.STONE), dropPos);
        while (level.map[dropPos = level.pointToCell(this.random())] != 14) {
        }
        level.drop(new StoneOfEnchantment(), dropPos);
        entrance.set(Room.Door.Type.HIDDEN);
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.map[l.pointToCell(p)] != 14;
    }
}

