/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;

public class WallBlockingTilemap
extends Tilemap {
    public static final int SIZE = 16;
    private static final int CLEARED = -2;
    private static final int BLOCK_NONE = -1;
    private static final int BLOCK_RIGHT = 0;
    private static final int BLOCK_LEFT = 1;
    private static final int BLOCK_ALL = 2;
    private static final int BLOCK_BELOW = 3;
    private int curr;

    public WallBlockingTilemap() {
        super("environment/wall_blocking.png", new TextureFilm("environment/wall_blocking.png", 16, 16));
        this.map(new int[Dungeon.level.length()], Dungeon.level.width());
    }

    @Override
    public synchronized void updateMap() {
        for (int cell = 0; cell < this.data.length; ++cell) {
            if (!Dungeon.level.discoverable[cell] || cell - this.mapWidth <= 0 || cell + this.mapWidth >= this.size) {
                this.data[cell] = -2;
                continue;
            }
            this.updateMapCell(cell);
        }
        super.updateMap();
    }

    @Override
    public synchronized void updateMapCell(int cell) {
        if (Dungeon.level instanceof HallsBossLevel) {
            this.data[cell] = -2;
            super.updateMapCell(cell);
            return;
        }
        if (!this.wall(cell)) {
            this.curr = !this.fogHidden(cell) || !this.wall(cell + this.mapWidth) ? -2 : (!this.fogHidden(cell + this.mapWidth) && (this.fogHidden(cell - 1) || this.wall(cell - 1)) && (this.fogHidden(cell + 1) || this.wall(cell + 1)) && (this.fogHidden(cell - 1 + this.mapWidth) || this.wall(cell - 1 + this.mapWidth)) && (this.fogHidden(cell + 1 + this.mapWidth) || this.wall(cell + 1 + this.mapWidth)) ? 3 : -1);
        } else if (!this.wall(cell + this.mapWidth)) {
            this.curr = this.wall(cell - 1 - this.mapWidth) && this.wall(cell - this.mapWidth) && this.wall(cell + 1 - this.mapWidth) ? (this.shelf(cell - 1 - this.mapWidth) || this.shelf(cell - this.mapWidth) || this.shelf(cell + 1 - this.mapWidth) || Dungeon.level instanceof MiningLevel ? -1 : -2) : (!this.wall(cell - 1 - this.mapWidth) && !this.fogHidden(cell - 1 - this.mapWidth) && this.wall(cell - 1) || !this.wall(cell - this.mapWidth) && !this.fogHidden(cell - this.mapWidth) || !this.wall(cell + 1 - this.mapWidth) && !this.fogHidden(cell + 1 - this.mapWidth) && this.wall(cell + 1) ? (!this.fogHidden(cell + this.mapWidth) || !this.wall(cell - 1) && !this.fogHidden(cell - 1) || !this.wall(cell - 1 + this.mapWidth) && !this.fogHidden(cell - 1 + this.mapWidth) || !this.wall(cell + 1) && !this.fogHidden(cell + 1) || !this.wall(cell + 1 + this.mapWidth) && !this.fogHidden(cell + 1 + this.mapWidth) ? -2 : 2) : -1);
        } else {
            this.curr = -1;
            if (!(this.fogHidden(cell - this.mapWidth) && this.fogHidden(cell - this.mapWidth - 1) && this.fogHidden(cell - this.mapWidth + 1) && this.fogHidden(cell) && this.fogHidden(cell + this.mapWidth))) {
                if ((cell + 1) % this.mapWidth != 0 && (this.wall(cell + 1) || this.fogHidden(cell + 1 - this.mapWidth)) && (this.wall(cell + 1) || this.fogHidden(cell + 1)) && (this.wall(cell + 1 + this.mapWidth) || this.fogHidden(cell + 1 + this.mapWidth))) {
                    ++this.curr;
                }
                if (cell % this.mapWidth != 0 && (this.wall(cell - 1) || this.fogHidden(cell - 1 - this.mapWidth)) && (this.wall(cell - 1) || this.fogHidden(cell - 1)) && (this.wall(cell - 1 + this.mapWidth) || this.fogHidden(cell - 1 + this.mapWidth))) {
                    this.curr += 2;
                }
                if (this.curr == -1) {
                    this.curr = -2;
                }
            }
        }
        if (this.data[cell] != this.curr) {
            this.data[cell] = this.curr;
            super.updateMapCell(cell);
        }
    }

    private boolean fogHidden(int cell) {
        if (!Dungeon.level.visited[cell] && !Dungeon.level.mapped[cell]) {
            return true;
        }
        return this.wall(cell) && cell + this.mapWidth < this.size && !this.wall(cell + this.mapWidth) && !Dungeon.level.visited[cell + this.mapWidth] && !Dungeon.level.mapped[cell + this.mapWidth];
    }

    private boolean wall(int cell) {
        return DungeonTileSheet.wallStitcheable(Dungeon.level.map[cell]);
    }

    private boolean shelf(int cell) {
        return Dungeon.level.map[cell] == 27;
    }

    private boolean door(int cell) {
        return DungeonTileSheet.doorTile(Dungeon.level.map[cell]);
    }

    public synchronized void updateArea(int cell, int radius) {
        int l = cell % this.mapWidth - radius;
        int t = cell / this.mapWidth - radius;
        int r = cell % this.mapWidth - radius + 1 + 2 * radius;
        int b = cell / this.mapWidth - radius + 1 + 2 * radius;
        this.updateArea(Math.max(0, l), Math.max(0, t), Math.min(this.mapWidth - 1, r - l), Math.min(this.mapHeight - 1, b - t));
    }

    public synchronized void updateArea(int x, int y, int w, int h) {
        for (int i = x; i <= x + w; ++i) {
            for (int j = y; j <= y + h; ++j) {
                int cell = i + j * this.mapWidth;
                if (cell >= this.data.length || this.data[cell] == -2) continue;
                this.updateMapCell(cell);
            }
        }
    }
}

