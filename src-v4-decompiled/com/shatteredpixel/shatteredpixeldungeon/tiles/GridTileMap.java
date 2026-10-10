/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class GridTileMap
extends DungeonTilemap {
    private int gridSetting = -1;

    public GridTileMap() {
        super("environment/visual_grid.png");
        this.map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    public synchronized void updateMap() {
        this.gridSetting = SPDSettings.visualGrid();
        super.updateMap();
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (this.gridSetting == -1 || pos % this.mapWidth % 2 != pos / this.mapWidth % 2) {
            return -1;
        }
        if (DungeonTileSheet.floorTile(tile) || tile == 15 || tile == 30) {
            return this.gridSetting;
        }
        if (DungeonTileSheet.doorTile(tile)) {
            if (DungeonTileSheet.wallStitcheable(this.map[pos - this.mapWidth])) {
                return 12 + this.gridSetting;
            }
            if (tile == 6) {
                return 8 + this.gridSetting;
            }
            return 4 + this.gridSetting;
        }
        return -1;
    }
}

