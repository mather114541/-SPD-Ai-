/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.watabou.utils.Random;

public class SecretGardenRoom
extends SecretRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 2);
        boolean[] grass = Patch.generate(this.width() - 2, this.height() - 2, 0.5f, 0, true);
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                if (!grass[this.xyToPatchCoords(j, i)]) continue;
                level.map[i * level.width() + j] = 15;
            }
        }
        this.entrance().set(Room.Door.Type.HIDDEN);
        level.plant(new Starflower.Seed(), this.plantPos(level));
        level.plant(new WandOfRegrowth.Seedpod.Seed(), this.plantPos(level));
        level.plant(new WandOfRegrowth.Dewcatcher.Seed(), this.plantPos(level));
        if (Random.Int(2) == 0) {
            level.plant(new WandOfRegrowth.Seedpod.Seed(), this.plantPos(level));
        } else {
            level.plant(new WandOfRegrowth.Dewcatcher.Seed(), this.plantPos(level));
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

    protected int xyToPatchCoords(int x, int y) {
        return x - this.left - 1 + (y - this.top - 1) * (this.width() - 2);
    }
}

