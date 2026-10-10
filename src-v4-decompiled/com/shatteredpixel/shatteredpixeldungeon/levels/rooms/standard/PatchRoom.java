/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;

public abstract class PatchRoom
extends StandardRoom {
    protected boolean[] patch;

    protected abstract float fill();

    protected abstract int clustering();

    protected abstract boolean ensurePath();

    protected abstract boolean cleanEdges();

    protected void setupPatch(Level level) {
        int attempts = 0;
        if (this.ensurePath()) {
            boolean valid;
            float fill = this.fill();
            PathFinder.setMapSize(this.width() - 2, this.height() - 2);
            do {
                this.patch = Patch.generate(this.width() - 2, this.height() - 2, fill, this.clustering(), true);
                int startPoint = level.pointToCell(this.center());
                for (Room.Door door : this.connected.values()) {
                    if (door.x == this.left) {
                        startPoint = this.xyToPatchCoords(door.x + 1, door.y);
                        this.patch[this.xyToPatchCoords((int)(door.x + 1), (int)door.y)] = false;
                        this.patch[this.xyToPatchCoords((int)(door.x + 2), (int)door.y)] = false;
                        continue;
                    }
                    if (door.x == this.right) {
                        startPoint = this.xyToPatchCoords(door.x - 1, door.y);
                        this.patch[this.xyToPatchCoords((int)(door.x - 1), (int)door.y)] = false;
                        this.patch[this.xyToPatchCoords((int)(door.x - 2), (int)door.y)] = false;
                        continue;
                    }
                    if (door.y == this.top) {
                        startPoint = this.xyToPatchCoords(door.x, door.y + 1);
                        this.patch[this.xyToPatchCoords((int)door.x, (int)(door.y + 1))] = false;
                        this.patch[this.xyToPatchCoords((int)door.x, (int)(door.y + 2))] = false;
                        continue;
                    }
                    if (door.y != this.bottom) continue;
                    startPoint = this.xyToPatchCoords(door.x, door.y - 1);
                    this.patch[this.xyToPatchCoords((int)door.x, (int)(door.y - 1))] = false;
                    this.patch[this.xyToPatchCoords((int)door.x, (int)(door.y - 2))] = false;
                }
                PathFinder.buildDistanceMap(startPoint, BArray.not(this.patch, null));
                valid = true;
                for (int i = 0; i < this.patch.length; ++i) {
                    if (this.patch[i] || PathFinder.distance[i] != Integer.MAX_VALUE) continue;
                    valid = false;
                    break;
                }
                if (++attempts <= 100) continue;
                fill -= 0.01f;
                attempts = 0;
            } while (!valid);
            PathFinder.setMapSize(level.width(), level.height());
        } else {
            this.patch = Patch.generate(this.width() - 2, this.height() - 2, this.fill(), this.clustering(), true);
        }
        if (this.cleanEdges()) {
            this.cleanDiagonalEdges();
        }
    }

    protected void fillPatch(Level level, int terrain) {
        for (int i = this.top + 1; i < this.bottom; ++i) {
            for (int j = this.left + 1; j < this.right; ++j) {
                if (!this.patch[this.xyToPatchCoords(j, i)]) continue;
                int cell = i * level.width() + j;
                level.map[cell] = terrain;
            }
        }
    }

    protected void cleanDiagonalEdges() {
        if (this.patch == null) {
            return;
        }
        int pWidth = this.width() - 2;
        for (int i = 0; i < this.patch.length - pWidth; ++i) {
            if (!this.patch[i]) continue;
            if (i % pWidth != 0 && this.patch[i - 1 + pWidth] && !this.patch[i - 1] && !this.patch[i + pWidth]) {
                this.patch[i - 1 + pWidth] = false;
            }
            if ((i + 1) % pWidth == 0 || !this.patch[i + 1 + pWidth] || this.patch[i + 1] || this.patch[i + pWidth]) continue;
            this.patch[i + 1 + pWidth] = false;
        }
    }

    protected int xyToPatchCoords(int x, int y) {
        return x - this.left - 1 + (y - this.top - 1) * (this.width() - 2);
    }
}

