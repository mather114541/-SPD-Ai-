/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class StripedRoom
extends StandardRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{2.0f, 1.0f, 0.0f};
    }

    @Override
    public void merge(Level l, Room other, Rect merge, int mergeTerrain) {
        if (other instanceof StripedRoom && mergeTerrain == 1) {
            super.merge(l, other, merge, 14);
        } else {
            super.merge(l, other, merge, mergeTerrain);
        }
    }

    @Override
    public void paint(Level level) {
        block7: {
            block6: {
                Painter.fill(level, this, 4);
                for (Room.Door door : this.connected.values()) {
                    door.set(Room.Door.Type.REGULAR);
                }
                if (this.sizeCat != StandardRoom.SizeCategory.NORMAL) break block6;
                Painter.fill(level, this, 1, 14);
                if (this.width() > this.height() || this.width() == this.height() && Random.Int(2) == 0) {
                    for (int i = this.left + 2; i < this.right; i += 2) {
                        Painter.fill(level, i, this.top + 1, 1, this.height() - 2, 15);
                    }
                } else {
                    for (int i = this.top + 2; i < this.bottom; i += 2) {
                        Painter.fill(level, this.left + 1, i, this.width() - 2, 1, 15);
                    }
                }
                break block7;
            }
            if (this.sizeCat != StandardRoom.SizeCategory.LARGE) break block7;
            int layers = (Math.min(this.width(), this.height()) - 1) / 2;
            for (int i = 1; i <= layers; ++i) {
                Painter.fill(level, this, i, i % 2 == 1 ? 14 : 15);
            }
        }
    }
}

