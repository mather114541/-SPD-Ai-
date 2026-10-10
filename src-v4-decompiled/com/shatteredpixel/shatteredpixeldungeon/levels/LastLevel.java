/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class LastLevel
extends Level {
    private static final int ROOM_TOP = 10;
    private static final int WIDTH = 16;
    private static final int MID = 8;
    public static int AMULET_POS = 200;

    public LastLevel() {
        this.color1 = 8393984;
        this.color2 = 10913057;
        this.viewDistance = Math.min(4, this.viewDistance);
    }

    @Override
    public void playLevelMusic() {
        if (Statistics.amuletObtained) {
            Music.INSTANCE.end();
        } else {
            Music.INSTANCE.play("music/theme_finale.ogg", true);
        }
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_halls.png";
    }

    @Override
    public String waterTex() {
        return "environment/water4.png";
    }

    @Override
    public void buildFlagMaps() {
        super.buildFlagMaps();
        for (int i = 0; i < this.length(); ++i) {
            if (!this.pit[i] && i <= (this.height - 10 + 2) * this.width) continue;
            this.avoid[i] = false;
            this.passable[i] = false;
            this.solid[i] = true;
        }
    }

    @Override
    public void cleanWalls() {
        super.cleanWalls();
        for (int i = (this.height - 10 + 2) * this.width; i < this.length; ++i) {
            if (i % this.width < 4 || i % this.width > 12 || i >= this.length - this.width) {
                this.discoverable[i] = false;
                continue;
            }
            this.visited[i] = true;
        }
    }

    @Override
    public void updateCellFlags(int cell) {
        super.updateCellFlags(cell);
        if (this.pit[cell] || cell > (this.height - 10 + 2) * this.width) {
            this.avoid[cell] = false;
            this.passable[cell] = false;
            this.solid[cell] = true;
        }
        if (cell > (this.height - 10 + 1) * this.width) {
            if (cell % this.width < 4 || cell % this.width > 12 || cell >= this.length - this.width) {
                this.discoverable[cell] = false;
            } else {
                this.visited[cell] = true;
            }
        }
    }

    @Override
    protected boolean build() {
        this.setSize(16, 64);
        Arrays.fill(this.map, 0);
        int MID = this.width / 2;
        Painter.fill(this, 0, this.height - 1, this.width, 1, 4);
        Painter.fill(this, MID - 1, 10, 3, this.height - 11, 1);
        Painter.fill(this, MID - 2, this.height - 3, 5, 1, 1);
        Painter.fill(this, MID - 3, this.height - 2, 7, 1, 1);
        int entrance = (this.height - 10) * this.width() + MID;
        Painter.fill(this, 0, this.height - 10, this.width, 2, 4);
        this.map[entrance] = 7;
        this.map[entrance + this.width] = 7;
        LevelTransition entry = new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE);
        --entry.left;
        ++entry.right;
        entry.bottom += 2;
        this.transitions.add(entry);
        Painter.fill(this, 0, this.height - 10 + 2, this.width, 8, 1);
        Painter.fill(this, MID - 1, this.height - 10 + 2, 3, 1, 7);
        for (int i = 0; i < this.length(); ++i) {
            if (this.map[i] != 1 || Random.Int(5) != 0) continue;
            this.map[i] = 20;
        }
        Painter.fill(this, MID - 2, 9, 5, 7, 1);
        Painter.fill(this, MID - 3, 10, 7, 5, 1);
        this.feeling = Level.Feeling.NONE;
        this.viewDistance = 4;
        CustomTilemap vis = new CustomFloor();
        vis.setRect(5, 0, 7, this.height - 10);
        this.customTiles.add(vis);
        vis = new CenterPieceVisuals();
        vis.pos(0, this.height - 10);
        this.customTiles.add(vis);
        vis = new CenterPieceWalls();
        vis.pos(0, this.height - 10 - 1);
        this.customWalls.add(vis);
        return true;
    }

    @Override
    public Mob createMob() {
        return null;
    }

    @Override
    protected void createMobs() {
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        this.drop(new Amulet(), AMULET_POS);
    }

    @Override
    public int randomRespawnCell(Char ch) {
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = this.entrance() + i;
            if (!this.passable[cell] || Actor.findChar(cell) != null || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) continue;
            candidates.add(cell);
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(candidates);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(HallsLevel.class, "water_name", new Object[0]);
            }
            case 2: {
                return Messages.get(HallsLevel.class, "grass_name", new Object[0]);
            }
            case 15: {
                return Messages.get(HallsLevel.class, "high_grass_name", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(HallsLevel.class, "statue_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(HallsLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(HallsLevel.class, "water_desc", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(HallsLevel.class, "statue_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(HallsLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(HallsLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        HallsLevel.addHallsVisuals(this, this.visuals);
        return this.visuals;
    }

    public static class CustomFloor
    extends CustomTilemap {
        private static final int[] CANDLES = new int[]{-1, 42, 46, 46, 46, 43, -1, 42, 46, 46, 46, 46, 46, 43, 46, 46, 45, 19, 44, 46, 46, 46, 46, 19, 19, 19, 46, 46, 46, 46, 43, 19, 42, 46, 46, 44, 46, 46, 19, 46, 46, 45, -1, 44, 45, 19, 44, 45, -1};

        public CustomFloor() {
            this.texture = "environment/custom_tiles/halls_special.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int candlesStart = AMULET_POS - 3 - 3 * Dungeon.level.width();
            int cell = this.tileX + this.tileY * Dungeon.level.width();
            int[] map = Dungeon.level.map;
            int[] data = new int[this.tileW * this.tileH];
            for (int i = 0; i < data.length; ++i) {
                if (i % this.tileW == 0) {
                    cell = this.tileX + (this.tileY + i / this.tileW) * Dungeon.level.width();
                }
                if (cell == candlesStart) {
                    for (int candle : CANDLES) {
                        if (data[i] == 0) {
                            data[i] = candle;
                        }
                        if (data[i] == 46 && DungeonTileSheet.tileVariance[cell] >= 50) {
                            int n = i;
                            data[n] = data[n] + 1;
                        }
                        if (Statistics.amuletObtained && data[i] > 40) {
                            int n = i;
                            data[n] = data[n] + 8;
                        }
                        if (map[cell] != 0 && map[cell + Dungeon.level.width] == 0) {
                            data[i + this.tileW] = 6;
                        }
                        ++cell;
                        if (++i % this.tileW != 0) continue;
                        cell = this.tileX + (this.tileY + i / this.tileW) * Dungeon.level.width();
                    }
                }
                if (map[cell] == 20) {
                    data[i] = Statistics.amuletObtained ? 31 : 27;
                } else if (map[cell] == 1) {
                    data[i] = 19;
                } else if (data[i] == 0) {
                    data[i] = -1;
                }
                ++cell;
            }
            v.map(data, this.tileW);
            return v;
        }
    }

    public static class CenterPieceVisuals
    extends CustomTilemap {
        private static final int[] map = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, 19, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 8, 9, 10, 11, 19, 11, 12, 13, 14, 0, 0, 0, 0, 0, 0, 0, 16, 17, 18, 31, 19, 31, 20, 21, 22, 0, 0, 0, 0, 0, 0, 0, 24, 25, 26, 19, 19, 19, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 24, 25, 26, 19, 19, 19, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 24, 25, 26, 19, 19, 19, 28, 29, 30, 0, 0, 0, 0, 0, 0, 0, 24, 25, 34, 35, 35, 35, 34, 29, 30, 0, 0, 0, 0, 0, 0, 0, 40, 41, 36, 36, 36, 36, 36, 40, 41, 0, 0, 0, 0, 0, 0, 0, 48, 49, 36, 36, 36, 36, 36, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        public CenterPieceVisuals() {
            this.texture = "environment/custom_tiles/halls_special.png";
            this.tileW = 16;
            this.tileH = 10;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(map, this.tileW);
            return v;
        }
    }

    public static class CenterPieceWalls
    extends CustomTilemap {
        private static final int[] map = new int[]{4, 4, 4, 4, 4, 4, 4, 5, 7, 3, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0, 1, 15, 2, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, 23, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, 33, -1, -1, -1, -1, -1, 32, 33, -1, -1, -1, -1, -1, -1, -1, 40, 41, -1, -1, -1, -1, -1, 40, 41, -1, -1, -1};

        public CenterPieceWalls() {
            this.texture = "environment/custom_tiles/halls_special.png";
            this.tileW = 16;
            this.tileH = 9;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(map, this.tileW);
            return v;
        }
    }
}

