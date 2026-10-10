/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class WeakFloorRoom
extends SpecialRoom {
    @Override
    public void paint(Level level) {
        int i;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 0);
        Room.Door door = this.entrance();
        door.set(Room.Door.Type.REGULAR);
        Point well = null;
        if (door.x == this.left) {
            for (i = this.top + 1; i < this.bottom; ++i) {
                Painter.drawInside(level, this, new Point(this.left, i), Random.IntRange(1, this.width() - 4), 14);
            }
            well = new Point(this.right - 1, Random.Int(2) == 0 ? this.top + 2 : this.bottom - 1);
        } else if (door.x == this.right) {
            for (i = this.top + 1; i < this.bottom; ++i) {
                Painter.drawInside(level, this, new Point(this.right, i), Random.IntRange(1, this.width() - 4), 14);
            }
            well = new Point(this.left + 1, Random.Int(2) == 0 ? this.top + 2 : this.bottom - 1);
        } else if (door.y == this.top) {
            for (i = this.left + 1; i < this.right; ++i) {
                Painter.drawInside(level, this, new Point(i, this.top), Random.IntRange(1, this.height() - 4), 14);
            }
            well = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.bottom - 1);
        } else if (door.y == this.bottom) {
            for (i = this.left + 1; i < this.right; ++i) {
                Painter.drawInside(level, this, new Point(i, this.bottom), Random.IntRange(1, this.height() - 4), 14);
            }
            well = new Point(Random.Int(2) == 0 ? this.left + 1 : this.right - 1, this.top + 2);
        }
        Painter.set(level, well, 0);
        HiddenWell vis = new HiddenWell();
        vis.pos(well.x, well.y);
        level.customTiles.add(vis);
        Blob.seed(well.x + level.width() * well.y, 1, WellID.class, level);
    }

    public static class HiddenWell
    extends CustomTilemap {
        public HiddenWell() {
            this.texture = "environment/custom_tiles/weak_floor.png";
            this.tileH = 1;
            this.tileW = 1;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(new int[]{Dungeon.depth / 5}, 1);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc", new Object[0]);
        }
    }

    public static class WellID
    extends Blob {
        @Override
        public Notes.Landmark landmark() {
            return Notes.Landmark.DISTANT_WELL;
        }

        @Override
        protected void evolve() {
            for (int i = this.area.top - 1; i <= this.area.bottom; ++i) {
                for (int j = this.area.left - 1; j <= this.area.right; ++j) {
                    int cell = j + i * Dungeon.level.width();
                    if (!Dungeon.level.insideMap(cell)) continue;
                    this.off[cell] = this.cur[cell];
                    this.volume += this.off[cell];
                }
            }
        }
    }
}

