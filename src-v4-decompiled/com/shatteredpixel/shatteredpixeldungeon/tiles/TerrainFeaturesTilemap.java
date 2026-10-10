/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.LastShopLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.tweeners.ScaleTweener;
import com.watabou.utils.PointF;
import com.watabou.utils.RectF;
import com.watabou.utils.SparseArray;

public class TerrainFeaturesTilemap
extends DungeonTilemap {
    private static TerrainFeaturesTilemap instance;
    private SparseArray<Plant> plants;
    private SparseArray<Trap> traps;

    public TerrainFeaturesTilemap(SparseArray<Plant> plants, SparseArray<Trap> traps) {
        super("environment/terrain_features.png");
        this.plants = plants;
        this.traps = traps;
        if (Dungeon.level != null) {
            this.map(Dungeon.level.map, Dungeon.level.width());
        }
        instance = this;
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (this.traps.get(pos) != null) {
            Trap trap = (Trap)this.traps.get(pos);
            if (!trap.visible) {
                return -1;
            }
            return (trap.active ? trap.color : 8) + trap.shape * 16;
        }
        if (this.plants.get(pos) != null) {
            return ((Plant)this.plants.get((int)pos)).image + 112;
        }
        int stage = (Dungeon.depth - 1) / 5;
        if (Dungeon.depth == 21 && Dungeon.level instanceof LastShopLevel) {
            --stage;
        }
        stage = Math.min(stage, 4);
        if (tile == 15) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_HIGH_GRASS, pos) == DungeonTileSheet.RAISED_HIGH_GRASS_ALT) {
                return 128 + 16 * stage + 1;
            }
            return 128 + 16 * stage;
        }
        if (tile == 30) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_FURROWED_GRASS, pos) == DungeonTileSheet.RAISED_FURROWED_ALT) {
                return 130 + 16 * stage + 1;
            }
            return 130 + 16 * stage;
        }
        if (tile == 2) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.GRASS, pos) == DungeonTileSheet.GRASS_ALT) {
                return 132 + 16 * stage + 1;
            }
            return 132 + 16 * stage;
        }
        if (tile == 13) {
            return 134 + 16 * stage;
        }
        if (tile == 28) {
            return 135 + 16 * stage;
        }
        if (tile == 25 || tile == 26) {
            return 136 + 16 * stage;
        }
        if (tile == 33) {
            return 137 + 16 * stage;
        }
        if (tile == 34) {
            return 138 + 16 * stage;
        }
        if (tile == 9) {
            if (DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.EMBERS, pos) == DungeonTileSheet.EMBERS_ALT) {
                return 209;
            }
            return 208;
        }
        if (tile == 35) {
            int vis = DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_MINE_CRYSTAL_BLUE_1, pos);
            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_RED_2) {
                return 215;
            }
            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_RED_1) {
                return 214;
            }
            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_GREEN_2) {
                return 213;
            }
            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_GREEN_1) {
                return 212;
            }
            if (vis == DungeonTileSheet.RAISED_MINE_CRYSTAL_BLUE_2) {
                return 211;
            }
            return 210;
        }
        if (tile == 36) {
            int vis = DungeonTileSheet.getVisualWithAlts(DungeonTileSheet.RAISED_MINE_BOULDER, pos);
            if (vis == DungeonTileSheet.RAISED_MINE_BOULDER_ALT_2) {
                return 218;
            }
            if (vis == DungeonTileSheet.RAISED_MINE_BOULDER_ALT) {
                return 217;
            }
            return 216;
        }
        return -1;
    }

    public static Image getTrapVisual(Trap trap) {
        RectF uv;
        if (instance == null) {
            instance = new TerrainFeaturesTilemap(null, null);
        }
        if ((uv = TerrainFeaturesTilemap.instance.tileset.get((trap.active ? trap.color : 8) + trap.shape * 16)) == null) {
            return null;
        }
        Image img = new Image(TerrainFeaturesTilemap.instance.texture);
        img.frame(uv);
        return img;
    }

    public static Image getPlantVisual(Plant plant) {
        RectF uv;
        if (instance == null) {
            instance = new TerrainFeaturesTilemap(null, null);
        }
        if ((uv = TerrainFeaturesTilemap.instance.tileset.get(plant.image + 112)) == null) {
            return null;
        }
        Image img = new Image(TerrainFeaturesTilemap.instance.texture);
        img.frame(uv);
        return img;
    }

    public static Image tile(int pos, int tile) {
        RectF uv = TerrainFeaturesTilemap.instance.tileset.get(instance.getTileVisual(pos, tile, true));
        if (uv == null) {
            return null;
        }
        Image img = new Image(TerrainFeaturesTilemap.instance.texture);
        img.frame(uv);
        return img;
    }

    public void growPlant(final int pos) {
        final Image plant = TerrainFeaturesTilemap.tile(pos, this.map[pos]);
        if (plant == null) {
            return;
        }
        plant.origin.set(8.0f, 12.0f);
        plant.scale.set(0.0f);
        plant.point(DungeonTilemap.tileToWorld(pos));
        this.parent.add(plant);
        this.parent.add(new ScaleTweener(plant, new PointF(1.0f, 1.0f), 0.2f){

            @Override
            protected void onComplete() {
                plant.killAndErase();
                this.killAndErase();
                TerrainFeaturesTilemap.this.updateMapCell(pos);
            }
        });
    }
}

