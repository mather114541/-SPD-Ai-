/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import java.util.HashSet;

public class RaisedTerrainTilemap
extends DungeonTilemap {
    public static HashSet<Integer> skipCells = new HashSet();

    public RaisedTerrainTilemap() {
        super("environment/raised_terrain.png");
        skipCells.clear();
        this.map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (flat) {
            return -1;
        }
        if (skipCells.contains(pos)) {
            return -1;
        }
        int region = (Dungeon.depth - 1) / 5;
        int regionOffset = region * 4;
        if (tile == 15) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_HIGH_GRASS, pos) == DungeonTileSheet.RAISED_HIGH_GRASS_ALT) {
                return regionOffset + 2;
            }
            return regionOffset;
        }
        if (tile == 30) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_FURROWED_GRASS, pos) == DungeonTileSheet.RAISED_FURROWED_ALT) {
                return regionOffset + 1 + 2;
            }
            return regionOffset + 1;
        }
        return -1;
    }
}

