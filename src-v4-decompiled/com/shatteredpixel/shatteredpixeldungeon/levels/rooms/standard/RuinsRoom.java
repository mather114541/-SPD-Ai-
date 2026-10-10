/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class RuinsRoom
extends PatchRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{4.0f, 2.0f, 1.0f};
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return true;
    }

    @Override
    protected float fill() {
        int scale = Math.min(this.width() * this.height(), 324);
        return 0.3f + (float)scale / 1024.0f;
    }

    @Override
    protected int clustering() {
        return 0;
    }

    @Override
    protected boolean ensurePath() {
        return this.connected.size() > 0;
    }

    @Override
    protected boolean cleanEdges() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        this.setupPatch(level);
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                boolean wall;
                if (!this.patch[this.xyToPatchCoords(j, i)]) continue;
                if (i > this.top + 1 && i < this.bottom - 1 && j > this.left + 1 && j < this.right - 1) {
                    int adjacent = 0;
                    if (this.patch[this.xyToPatchCoords(j - 1, i)]) {
                        ++adjacent;
                    }
                    if (this.patch[this.xyToPatchCoords(j + 1, i)]) {
                        ++adjacent;
                    }
                    if (this.patch[this.xyToPatchCoords(j, i - 1)]) {
                        ++adjacent;
                    }
                    if (this.patch[this.xyToPatchCoords(j, i + 1)]) {
                        ++adjacent;
                    }
                    wall = Random.Int(2) < adjacent;
                } else {
                    wall = true;
                }
                int cell = i * level.width() + j;
                level.map[cell] = wall ? 4 : 33;
            }
        }
    }
}

