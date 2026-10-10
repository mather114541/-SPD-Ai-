/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.food.ChargrilledMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.watabou.utils.Point;

public class SecretLarderRoom
extends SecretRoom {
    @Override
    public int minHeight() {
        return 6;
    }

    @Override
    public int minWidth() {
        return 6;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 14);
        Point c = this.center();
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, 29);
        Painter.set(level, c, 2);
        level.plant(new BlandfruitBush.Seed(), level.pointToCell(c));
        int extraFood = 150 * (1 + Dungeon.depth / 5);
        while (extraFood > 0) {
            int foodPos;
            Food food;
            if ((float)extraFood >= 450.0f) {
                food = new Pasty();
                extraFood = (int)((float)extraFood - 450.0f);
            } else {
                food = new ChargrilledMeat();
                extraFood = (int)((float)extraFood - 150.0f);
            }
            while (level.map[foodPos = level.pointToCell(this.random())] != 14 || level.heaps.get(foodPos) != null) {
            }
            level.drop(food, foodPos);
        }
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

