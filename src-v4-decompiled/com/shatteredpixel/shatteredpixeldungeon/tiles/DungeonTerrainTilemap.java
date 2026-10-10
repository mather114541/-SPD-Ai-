/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.utils.PathFinder;

public class DungeonTerrainTilemap
extends DungeonTilemap {
    static DungeonTerrainTilemap instance;

    public DungeonTerrainTilemap() {
        super(Dungeon.level.tilesTex());
        this.map(Dungeon.level.map, Dungeon.level.width());
        instance = this;
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        int visual = DungeonTileSheet.directVisuals.get(tile, -1);
        if (visual != -1) {
            if (visual == DungeonTileSheet.FLOOR_DECO && Dungeon.level instanceof MiningLevel) {
                for (int i : PathFinder.NEIGHBOURS4) {
                    if (this.map[pos + i] != 36) continue;
                    visual = DungeonTileSheet.MINE_FLOOR_DECO_HEAVY;
                    break;
                }
            }
            return DungeonTileSheet.getVisualWithAlts(visual, pos);
        }
        if (tile == 29) {
            return DungeonTileSheet.stitchWaterTile(this.map[pos + PathFinder.CIRCLE4[0]], this.map[pos + PathFinder.CIRCLE4[1]], this.map[pos + PathFinder.CIRCLE4[2]], this.map[pos + PathFinder.CIRCLE4[3]]);
        }
        if (tile == 0) {
            return DungeonTileSheet.stitchChasmTile(pos > this.mapWidth ? this.map[pos - this.mapWidth] : -1);
        }
        if (!flat) {
            if (DungeonTileSheet.doorTile(tile)) {
                return DungeonTileSheet.getRaisedDoorTile(tile, this.map[pos - this.mapWidth]);
            }
            if (DungeonTileSheet.wallStitcheable(tile)) {
                return DungeonTileSheet.getRaisedWallTile(tile, pos, (pos + 1) % this.mapWidth != 0 ? this.map[pos + 1] : -1, pos + this.mapWidth < this.size ? this.map[pos + this.mapWidth] : -1, pos % this.mapWidth != 0 ? this.map[pos - 1] : -1);
            }
            if (tile == 25) {
                return DungeonTileSheet.RAISED_STATUE;
            }
            if (tile == 26) {
                return DungeonTileSheet.RAISED_STATUE_SP;
            }
            if (tile == 33) {
                return DungeonTileSheet.RAISED_REGION_DECO;
            }
            if (tile == 34) {
                return DungeonTileSheet.RAISED_REGION_DECO_ALT;
            }
            if (tile == 35) {
                return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_MINE_CRYSTAL_BLUE_1, pos);
            }
            if (tile == 36) {
                return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_MINE_BOULDER, pos);
            }
            if (tile == 28) {
                return DungeonTileSheet.RAISED_ALCHEMY_POT;
            }
            if (tile == 13) {
                return DungeonTileSheet.RAISED_BARRICADE;
            }
            if (tile == 15) {
                return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_HIGH_GRASS, pos);
            }
            if (tile == 30) {
                return DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_FURROWED_GRASS, pos);
            }
            return -1;
        }
        return DungeonTileSheet.getVisualWithAlts((Integer)DungeonTileSheet.directFlatVisuals.get(tile), pos);
    }

    public static Image tile(int pos, int tile) {
        Image img = new Image(DungeonTerrainTilemap.instance.texture);
        img.frame(DungeonTerrainTilemap.instance.tileset.get(instance.getTileVisual(pos, tile, true)));
        return img;
    }

    @Override
    protected boolean needsRender(int pos) {
        return super.needsRender(pos) && this.data[pos] != DungeonTileSheet.WATER;
    }
}

