/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Bundle;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.Arrays;

public class RitualSiteRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 10);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 10);
    }

    @Override
    public void paint(Level level) {
        for (Object door : this.connected.values()) {
            ((Room.Door)door).set(Room.Door.Type.REGULAR);
        }
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        boolean[] validTopRowCells = new boolean[10];
        Arrays.fill(validTopRowCells, true);
        validTopRowCells[9] = false;
        validTopRowCells[6] = false;
        validTopRowCells[3] = false;
        validTopRowCells[0] = false;
        for (Room.Door d : this.connected.values()) {
            if (d.y == this.top) {
                validTopRowCells[d.x - this.left] = false;
                continue;
            }
            if (d.y > this.top + 2) continue;
            if (d.x == this.left) {
                validTopRowCells[1] = false;
                continue;
            }
            validTopRowCells[8] = false;
        }
        Point topRow = new Point(this.left, this.top);
        int cageRow = Random.Int(2);
        for (int i = this.left; i < this.right; i += 3) {
            topRow.x = i;
            Painter.drawInside(level, this, topRow, 2, 4);
            if (!validTopRowCells[topRow.x + 1 - this.left] && !validTopRowCells[topRow.x + 2 - this.left]) continue;
            topRow.x = !validTopRowCells[topRow.x + 2 - this.left] ? ++topRow.x : (!validTopRowCells[topRow.x + 1 - this.left] ? (topRow.x += 2) : (topRow.x += Random.IntRange(1, 2)));
            validTopRowCells[topRow.x - this.left] = false;
            if (cageRow == 0) {
                Painter.drawInside(level, this, topRow, 2, 33);
            } else {
                Table table = new Table();
                Painter.drawInside(level, this, topRow, 2, 23);
                table.pos(topRow.x, topRow.y + 1);
                level.customTerrain.add(table);
            }
            --cageRow;
        }
        int tries = 100;
        do {
            int i;
            if (!validTopRowCells[i = Random.IntRange(1, 9)]) continue;
            topRow.x = this.left + i;
            Painter.drawInside(level, this, topRow, 1, 33);
            tries = 0;
        } while (tries-- > 0);
        RitualMarker vis = new RitualMarker();
        Point c = this.center();
        ++c.y;
        vis.pos(c.x - 2, c.y - 2);
        level.customTiles.add(vis);
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, 32);
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        level.addItemToSpawn(new CeremonialCandle());
        CeremonialCandle.ritualPos = c.x + level.width() * c.y;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return super.canPlaceItem(p, l) && l.distance(CeremonialCandle.ritualPos, l.pointToCell(p)) >= 2;
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return super.canPlaceCharacter(p, l) && l.distance(CeremonialCandle.ritualPos, l.pointToCell(p)) >= 2;
    }

    @Override
    public boolean canConnect(Point p) {
        if (p.y == this.top && (p.x == this.left + 3 || p.x == this.left + 6)) {
            return false;
        }
        return super.canConnect(p);
    }

    public static class Table
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public Table() {
            this.texture = "environment/custom_tiles/prison_quest.png";
            this.tileW = 1;
            this.tileH = 2;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(0, 0, 256), 1);
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

    public static class RitualMarker
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public RitualMarker() {
            this.texture = "environment/custom_tiles/prison_quest.png";
            this.tileH = 5;
            this.tileW = 5;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(0, 2, 256), 5);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileX == 0 || tileY == 0 || tileX == this.tileW - 1 || tileY == this.tileH - 1) {
                return null;
            }
            return super.image(tileX, tileY);
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc", new Object[0]);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            if (this.tileW == 3) {
                this.tileH = 5;
                this.tileW = 5;
                --this.tileX;
                --this.tileY;
            }
        }
    }
}

