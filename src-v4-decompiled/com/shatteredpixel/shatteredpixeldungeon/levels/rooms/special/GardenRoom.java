/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.plants.BlandfruitBush;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.watabou.utils.Random;

public class GardenRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 15);
        Painter.fill(level, this, 2, 2);
        this.entrance().set(Room.Door.Type.LOCKED);
        level.addItemToSpawn(new IronKey(Dungeon.depth));
        int bushes = Random.Int(3);
        if (bushes == 0) {
            level.plant(new Sungrass.Seed(), this.plantPos(level));
        } else if (bushes == 1) {
            level.plant(new BlandfruitBush.Seed(), this.plantPos(level));
        } else if (Random.Int(5) == 0) {
            level.plant(new Sungrass.Seed(), this.plantPos(level));
            level.plant(new BlandfruitBush.Seed(), this.plantPos(level));
        }
        Foliage light = (Foliage)level.blobs.get(Foliage.class);
        if (light == null) {
            light = new Foliage();
        }
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                light.seed(level, j + level.width() * i, 1);
            }
        }
        level.blobs.put(Foliage.class, light);
    }

    private int plantPos(Level level) {
        int pos;
        while (level.plants.get(pos = level.pointToCell(this.random())) != null) {
        }
        return pos;
    }
}

