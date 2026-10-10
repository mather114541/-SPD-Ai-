/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;

public class WallOcclusionTilemap
extends DungeonTilemap {
    public static final int SIZE = 16;
    private static final int CLEARED = 0;
    private static final int EMPTY = 0;
    private static final int DOOR_VERT = 6;
    private static final int DOOR_HORIZ = 7;
    private static final int WALL_ABOVE = 40;
    private static final int WALL_LEFT_BELOW = 1;
    private static final int WALL_LEFT = 2;
    private static final int WALL_LEFT_ABOVE = 3;
    private static final int WALL_LEFT_BOTH = 4;
    private static final int WALL_RIGHT_BELOW = 8;
    private static final int WALL_RIGHT = 16;
    private static final int WALL_RIGHT_ABOVE = 24;
    private static final int WALL_RIGHT_BOTH = 32;

    public WallOcclusionTilemap() {
        super("environment/occlusion_shadows.png");
        this.map(Dungeon.level.map, Dungeon.level.width());
    }

    @Override
    protected int getTileVisual(int pos, int tile, boolean flat) {
        if (!Dungeon.level.insideMap(pos) || !Dungeon.level.discoverable[pos]) {
            return 0;
        }
        int curr = 0;
        if (DungeonTileSheet.doorTile(tile)) {
            curr = this.wall(pos - 1) && this.wall(pos + 1) ? 7 : 6;
        } else if (!DungeonTileSheet.wallStitcheable(tile) && !Dungeon.level.pit[pos]) {
            if (this.wall(pos - this.mapWidth) && this.map[pos] != 28) {
                curr += 40;
                if (this.wall(pos - 1)) {
                    curr += 2;
                } else if (this.wall(pos + this.mapWidth - 1)) {
                    ++curr;
                }
                if (this.wall(pos + 1)) {
                    curr += 16;
                } else if (this.wall(pos + this.mapWidth + 1)) {
                    curr += 8;
                }
            } else {
                if (this.wall(pos - 1)) {
                    curr += 2;
                } else if (this.wall(pos + this.mapWidth - 1)) {
                    curr = this.wall(pos - this.mapWidth - 1) ? (curr += 4) : ++curr;
                } else if (this.wall(pos - this.mapWidth - 1)) {
                    curr += 3;
                }
                if (this.wall(pos + 1)) {
                    curr += 16;
                } else if (this.wall(pos + this.mapWidth + 1)) {
                    curr = this.wall(pos - this.mapWidth + 1) ? (curr += 32) : (curr += 8);
                } else if (this.wall(pos - this.mapWidth + 1)) {
                    curr += 24;
                }
            }
        } else {
            curr = 0;
        }
        return curr;
    }

    private boolean wall(int cell) {
        return DungeonTileSheet.wallStitcheable(this.map[cell]);
    }

    private boolean door(int cell) {
        return DungeonTileSheet.doorTile(this.map[cell]);
    }
}

