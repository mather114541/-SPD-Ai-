/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import java.util.HashSet;

public class DungeonWallsTilemap
extends DungeonTilemap {
    public static HashSet<Integer> skipCells = new HashSet();

    public DungeonWallsTilemap() {
        super(Dungeon.level.tilesTex());
        skipCells.clear();
        this.map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (flat) {
            return -1;
        }
        if (DungeonTileSheet.wallStitcheable(tile)) {
            if (pos + this.mapWidth < this.size && !DungeonTileSheet.wallStitcheable(this.map[pos + this.mapWidth])) {
                if (this.map[pos + this.mapWidth] == 5) {
                    return DungeonTileSheet.DOOR_SIDEWAYS;
                }
                if (this.map[pos + this.mapWidth] == 10) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_LOCKED;
                }
                if (this.map[pos + this.mapWidth] == 38) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_LOCKED;
                }
                if (this.map[pos + this.mapWidth] == 31) {
                    return DungeonTileSheet.DOOR_SIDEWAYS_CRYSTAL;
                }
                if (this.map[pos + this.mapWidth] == 6) {
                    return -1;
                }
            } else {
                return DungeonTileSheet.stitchInternalWallTile(tile, (pos + 1) % this.mapWidth != 0 ? this.map[pos + 1] : -1, (pos + 1) % this.mapWidth != 0 && pos + this.mapWidth < this.size ? this.map[pos + 1 + this.mapWidth] : -1, pos + this.mapWidth < this.size ? this.map[pos + this.mapWidth] : -1, pos % this.mapWidth != 0 && pos + this.mapWidth < this.size ? this.map[pos - 1 + this.mapWidth] : -1, pos % this.mapWidth != 0 ? this.map[pos - 1] : -1);
            }
        }
        if (skipCells.contains(pos)) {
            return -1;
        }
        if (this.map[pos] == 21 || this.map[pos] == 22) {
            return DungeonTileSheet.EXIT_UNDERHANG;
        }
        if (pos + this.mapWidth < this.size && DungeonTileSheet.wallStitcheable(this.map[pos + this.mapWidth])) {
            return DungeonTileSheet.stitchWallOverhangTile(tile, (pos + 1) % this.mapWidth != 0 ? this.map[pos + 1 + this.mapWidth] : -1, this.map[pos + this.mapWidth], pos % this.mapWidth != 0 ? this.map[pos - 1 + this.mapWidth] : -1);
        }
        if (Dungeon.level.insideMap(pos) && this.map[pos + this.mapWidth] == 5) {
            return DungeonTileSheet.DOOR_OVERHANG;
        }
        if (Dungeon.level.insideMap(pos) && this.map[pos + this.mapWidth] == 10) {
            return DungeonTileSheet.DOOR_OVERHANG;
        }
        if (Dungeon.level.insideMap(pos) && this.map[pos + this.mapWidth] == 38) {
            return DungeonTileSheet.DOOR_OVERHANG;
        }
        if (Dungeon.level.insideMap(pos) && this.map[pos + this.mapWidth] == 6) {
            return DungeonTileSheet.DOOR_OVERHANG_OPEN;
        }
        if (Dungeon.level.insideMap(pos) && this.map[pos + this.mapWidth] == 31) {
            return DungeonTileSheet.DOOR_OVERHANG_CRYSTAL;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 25) {
            return DungeonTileSheet.STATUE_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 26) {
            return DungeonTileSheet.STATUE_SP_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 33) {
            return DungeonTileSheet.REGION_DECO_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 34) {
            return DungeonTileSheet.REGION_DECO_ALT_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 35) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.MINE_CRYSTAL_OVERHANG_BLUE, pos + this.mapWidth);
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 36) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.MINE_BOULDER_OVERHANG, pos + this.mapWidth);
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 28) {
            return DungeonTileSheet.ALCHEMY_POT_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 13) {
            return DungeonTileSheet.BARRICADE_OVERHANG;
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 15) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.HIGH_GRASS_OVERHANG, pos + this.mapWidth);
        }
        if (pos + this.mapWidth < this.size && this.map[pos + this.mapWidth] == 30) {
            return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.FURROWED_OVERHANG, pos + this.mapWidth);
        }
        return -1;
    }

    @Override
    public boolean overlapsPoint(float x, float y) {
        return true;
    }

    @Override
    public boolean overlapsScreenPoint(int x, int y) {
        return true;
    }
}

