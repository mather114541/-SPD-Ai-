/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PlantsRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class GrassyGraveRoom
extends StandardRoom {
    @Override
    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        if (mergeTerrain == 1 && (other instanceof GrassyGraveRoom || other instanceof PlantsRoom)) {
            super.merge(l, other, merge, 2);
        } else {
            super.merge(l, other, merge, mergeTerrain);
        }
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Painter.fill(level, this, 1, 2);
        int w = this.width() - 2;
        int h = this.height() - 2;
        int nGraves = Math.max(w, h) / 2;
        int index = Random.Int(nGraves);
        int shift = Random.Int(2);
        for (int i = 0; i < nGraves; ++i) {
            int pos = w > h ? this.left + 1 + shift + i * 2 + (this.top + 2 + Random.Int(h - 2)) * level.width() : this.left + 2 + Random.Int(w - 2) + (this.top + 1 + shift + i * 2) * level.width();
            level.drop((Item)(i == index ? Generator.random() : new Gold().random()), (int)pos).type = Heap.Type.TOMB;
        }
    }
}

