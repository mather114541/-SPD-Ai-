/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.utils.Random;

public class StatuesRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(7, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(7, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{9.0f, 3.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        int rows = (this.width() + 1) / 6;
        int cols = (this.height() + 1) / 6;
        int w = (this.width() - 4 - (rows - 1)) / rows;
        int h = (this.height() - 4 - (cols - 1)) / cols;
        int Wspacing = rows % 2 == this.width() % 2 ? 2 : 1;
        int Hspacing = cols % 2 == this.height() % 2 ? 2 : 1;
        for (int x = 0; x < rows; ++x) {
            for (int y = 0; y < cols; ++y) {
                int left = this.left + 2 + x * (w + Wspacing);
                int top = this.top + 2 + y * (h + Hspacing);
                Painter.fill(level, left, top, w, h, 32);
                Painter.set(level, left, top, 25);
                Painter.set(level, left + w - 1, top, 25);
                Painter.set(level, left, top + h - 1, 25);
                Painter.set(level, left + w - 1, top + h - 1, 25);
                Carpet carpet = new Carpet();
                carpet.setRect(left, top, w, h);
                carpet.overrideTile(0, 0, 85);
                carpet.overrideTile(carpet.tileW - 1, 0, 83);
                carpet.overrideTile(0, carpet.tileH - 1, 86);
                carpet.overrideTile(carpet.tileW - 1, carpet.tileH - 1, 84);
                level.customTiles.add(carpet);
                if ((w < 5 || h < 5) && (!this.isEntrance() && !this.isExit() || rows != 1 || cols != 1)) continue;
                int cx = left + w / 2;
                if (w % 2 == 0 && Random.Int(2) == 0) {
                    --cx;
                }
                int cy = top + h / 2;
                if (h % 2 == 0 && Random.Int(2) == 0) {
                    --cy;
                }
                if (this.isEntrance() && rows == 1 && cols == 1) {
                    Painter.set(level, cx, cy, 7);
                    carpet.overrideTile(level, cx, cy, 82);
                    continue;
                }
                if (this.isExit() && rows == 1 && cols == 1) {
                    Painter.set(level, cx, cy, 8);
                    carpet.overrideTile(level, cx, cy, -1);
                    continue;
                }
                Painter.set(level, cx, cy, 33);
                carpet.overrideTile(level, cx, cy, 81);
            }
        }
    }
}

