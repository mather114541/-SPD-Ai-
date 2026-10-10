/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.tiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.watabou.utils.Random;
import com.watabou.utils.SparseArray;
import java.util.Arrays;
import java.util.HashSet;

public class DungeonTileSheet {
    private static final int WIDTH = 16;
    public static final int NULL_TILE = -1;
    private static final int GROUND = DungeonTileSheet.xy(1, 1);
    public static final int FLOOR = GROUND + 0;
    public static final int FLOOR_DECO = GROUND + 1;
    public static final int GRASS = GROUND + 2;
    public static final int EMBERS = GROUND + 3;
    public static final int FLOOR_SP = GROUND + 4;
    public static final int MINE_FLOOR_DECO_HEAVY = GROUND + 5;
    public static final int FLOOR_ALT_1 = GROUND + 6;
    public static final int FLOOR_DECO_ALT = GROUND + 7;
    public static final int GRASS_ALT = GROUND + 8;
    public static final int EMBERS_ALT = GROUND + 9;
    public static final int FLOOR_SP_ALT = GROUND + 10;
    public static final int MINE_FLOOR_DECO_HEAVY_ALT = GROUND + 11;
    public static final int FLOOR_ALT_2 = GROUND + 12;
    public static final int ENTRANCE = GROUND + 16;
    public static final int EXIT = GROUND + 17;
    public static final int WELL = GROUND + 18;
    public static final int EMPTY_WELL = GROUND + 19;
    public static final int PEDESTAL = GROUND + 20;
    public static final int ENTRANCE_SP = GROUND + 22;
    public static final int CHASM = DungeonTileSheet.xy(9, 2);
    public static final int CHASM_FLOOR = CHASM + 1;
    public static final int CHASM_FLOOR_SP = CHASM + 2;
    public static final int CHASM_WALL = CHASM + 3;
    public static final int CHASM_WATER = CHASM + 4;
    public static SparseArray<Integer> chasmStitcheable = new SparseArray();
    public static final int WATER;
    public static HashSet<Integer> waterStitcheable;
    private static final int FLAT_WALLS;
    public static final int FLAT_WALL;
    public static final int FLAT_WALL_DECO;
    public static final int FLAT_BOOKSHELF;
    public static final int FLAT_WALL_ALT;
    public static final int FLAT_WALL_DECO_ALT;
    public static final int FLAT_BOOKSHELF_ALT;
    public static final int FLAT_DOOR;
    public static final int FLAT_DOOR_OPEN;
    public static final int FLAT_DOOR_LOCKED;
    public static final int FLAT_DOOR_CRYSTAL;
    public static final int UNLOCKED_EXIT;
    public static final int LOCKED_EXIT;
    public static final int FLAT_OTHER;
    public static final int FLAT_ALCHEMY_POT;
    public static final int FLAT_BARRICADE;
    public static final int FLAT_HIGH_GRASS;
    public static final int FLAT_FURROWED_GRASS;
    public static final int FLAT_HIGH_GRASS_ALT;
    public static final int FLAT_FURROWED_ALT;
    public static final int FLAT_STATUE;
    public static final int FLAT_STATUE_SP;
    public static final int FLAT_REGION_DECO;
    public static final int FLAT_REGION_DECO_ALT;
    public static final int FLAT_MINE_CRYSTAL;
    public static final int FLAT_MINE_CRYSTAL_ALT;
    public static final int FLAT_MINE_CRYSTAL_ALT_2;
    public static final int FLAT_MINE_BOULDER;
    public static final int FLAT_MINE_BOULDER_ALT;
    public static final int FLAT_MINE_BOULDER_ALT_2;
    private static final int RAISED_WALLS;
    public static final int RAISED_WALL;
    public static final int RAISED_WALL_DECO;
    public static final int RAISED_WALL_DOOR;
    public static final int RAISED_WALL_BOOKSHELF;
    public static final int RAISED_WALL_ALT;
    public static final int RAISED_WALL_DECO_ALT;
    public static final int RAISED_WALL_BOOKSHELF_ALT;
    private static int[] wallStitcheable;
    private static final int RAISED_DOORS;
    public static final int RAISED_DOOR;
    public static final int RAISED_DOOR_OPEN;
    public static final int RAISED_DOOR_LOCKED;
    public static final int RAISED_DOOR_CRYSTAL;
    public static final int RAISED_DOOR_SIDEWAYS;
    private static int[] doorTiles;
    private static final int RAISED_OTHER;
    public static final int RAISED_ALCHEMY_POT;
    public static final int RAISED_BARRICADE;
    public static final int RAISED_HIGH_GRASS;
    public static final int RAISED_FURROWED_GRASS;
    public static final int RAISED_HIGH_GRASS_ALT;
    public static final int RAISED_FURROWED_ALT;
    public static final int RAISED_STATUE;
    public static final int RAISED_STATUE_SP;
    public static final int RAISED_REGION_DECO;
    public static final int RAISED_REGION_DECO_ALT;
    public static final int RAISED_MINE_CRYSTAL_BLUE_1;
    public static final int RAISED_MINE_CRYSTAL_BLUE_2;
    public static final int RAISED_MINE_CRYSTAL_GREEN_1;
    public static final int RAISED_MINE_CRYSTAL_GREEN_2;
    public static final int RAISED_MINE_CRYSTAL_RED_1;
    public static final int RAISED_MINE_CRYSTAL_RED_2;
    public static final int RAISED_MINE_BOULDER;
    public static final int RAISED_MINE_BOULDER_ALT;
    public static final int RAISED_MINE_BOULDER_ALT_2;
    public static final int WALLS_INTERNAL;
    private static final int WALL_INTERNAL;
    private static final int WALL_INTERNAL_DECO;
    private static final int WALL_INTERNAL_WOODEN;
    private static final int WALLS_OVERHANG;
    public static final int WALL_OVERHANG;
    public static final int WALL_OVERHANG_DECO;
    public static final int WALL_OVERHANG_WOODEN;
    public static final int DOOR_SIDEWAYS_OVERHANG;
    public static final int DOOR_SIDEWAYS_OVERHANG_CLOSED;
    public static final int DOOR_SIDEWAYS_OVERHANG_LOCKED;
    public static final int DOOR_SIDEWAYS_OVERHANG_CRYSTAL;
    public static final int DOOR_OVERHANG;
    public static final int DOOR_OVERHANG_OPEN;
    public static final int DOOR_OVERHANG_CRYSTAL;
    public static final int DOOR_SIDEWAYS;
    public static final int DOOR_SIDEWAYS_LOCKED;
    public static final int DOOR_SIDEWAYS_CRYSTAL;
    public static final int EXIT_UNDERHANG;
    private static final int OTHER_OVERHANG;
    public static final int ALCHEMY_POT_OVERHANG;
    public static final int BARRICADE_OVERHANG;
    public static final int HIGH_GRASS_OVERHANG;
    public static final int FURROWED_OVERHANG;
    public static final int HIGH_GRASS_OVERHANG_ALT;
    public static final int FURROWED_OVERHANG_ALT;
    public static final int STATUE_OVERHANG;
    public static final int STATUE_SP_OVERHANG;
    public static final int REGION_DECO_OVERHANG;
    public static final int REGION_DECO_ALT_OVERHANG;
    public static final int MINE_CRYSTAL_OVERHANG_BLUE;
    public static final int MINE_CRYSTAL_OVERHANG_GREEN;
    public static final int MINE_CRYSTAL_OVERHANG_RED;
    public static final int MINE_BOULDER_OVERHANG;
    public static final int MINE_BOULDER_OVERHANG_ALT;
    public static final int MINE_BOULDER_OVERHANG_ALT_2;
    public static SparseArray<Integer> directVisuals;
    public static SparseArray<Integer> directFlatVisuals;
    public static byte[] tileVariance;
    private static SparseArray<tileAlt> tileAltVisuals;

    private static int xy(int x, int y) {
        return --x + 16 * --y;
    }

    public static int stitchChasmTile(int above) {
        if (above == 34) {
            if (Dungeon.depth <= 5) {
                return CHASM_FLOOR_SP;
            }
            if (Dungeon.depth <= 10) {
                return CHASM;
            }
            if (Dungeon.depth <= 20) {
                return CHASM_FLOOR_SP;
            }
            return CHASM_FLOOR;
        }
        return chasmStitcheable.get(above, CHASM);
    }

    public static boolean waterStitcheable(int tile) {
        if (tile == 34) {
            return Dungeon.depth > 20;
        }
        return waterStitcheable.contains(tile);
    }

    public static int stitchWaterTile(int top, int right, int bottom, int left) {
        int result = WATER;
        if (DungeonTileSheet.waterStitcheable(top)) {
            ++result;
        }
        if (DungeonTileSheet.waterStitcheable(right)) {
            result += 2;
        }
        if (DungeonTileSheet.waterStitcheable(bottom)) {
            result += 4;
        }
        if (DungeonTileSheet.waterStitcheable(left)) {
            result += 8;
        }
        return result;
    }

    public static boolean floorTile(int tile) {
        return tile == 29 || directVisuals.get(tile, CHASM) < CHASM;
    }

    public static boolean wallStitcheable(int tile) {
        for (int i : wallStitcheable) {
            if (tile != i) continue;
            return true;
        }
        return false;
    }

    public static int getRaisedWallTile(int tile, int pos, int right, int below, int left) {
        int result;
        if (below == -1 || DungeonTileSheet.wallStitcheable(below)) {
            return -1;
        }
        if (DungeonTileSheet.doorTile(below)) {
            result = RAISED_WALL_DOOR;
        } else if (tile == 4 || tile == 16) {
            result = RAISED_WALL;
        } else if (tile == 12) {
            result = RAISED_WALL_DECO;
        } else if (tile == 27) {
            result = RAISED_WALL_BOOKSHELF;
        } else {
            return -1;
        }
        result = DungeonTileSheet.getVisualWithAlts(result, pos);
        if (!DungeonTileSheet.wallStitcheable(right)) {
            ++result;
        }
        if (!DungeonTileSheet.wallStitcheable(left)) {
            result += 2;
        }
        return result;
    }

    public static int getRaisedDoorTile(int tile, int below) {
        if (DungeonTileSheet.wallStitcheable(below)) {
            return RAISED_DOOR_SIDEWAYS;
        }
        if (tile == 5) {
            return RAISED_DOOR;
        }
        if (tile == 6) {
            return RAISED_DOOR_OPEN;
        }
        if (tile == 10) {
            return RAISED_DOOR_LOCKED;
        }
        if (tile == 38) {
            return RAISED_DOOR_LOCKED;
        }
        if (tile == 31) {
            return RAISED_DOOR_CRYSTAL;
        }
        return -1;
    }

    public static boolean doorTile(int tile) {
        for (int i : doorTiles) {
            if (tile != i) continue;
            return true;
        }
        return false;
    }

    public static int stitchInternalWallTile(int tile, int right, int rightBelow, int below, int leftBelow, int left) {
        int result = tile == 27 || below == 27 ? WALL_INTERNAL_WOODEN : (Dungeon.branch == 1 && tile == 12 ? WALL_INTERNAL_DECO : WALL_INTERNAL);
        if (!DungeonTileSheet.wallStitcheable(right)) {
            ++result;
        }
        if (!DungeonTileSheet.wallStitcheable(rightBelow)) {
            result += 2;
        }
        if (!DungeonTileSheet.wallStitcheable(leftBelow)) {
            result += 4;
        }
        if (!DungeonTileSheet.wallStitcheable(left)) {
            result += 8;
        }
        return result;
    }

    public static int stitchWallOverhangTile(int tile, int rightBelow, int below, int leftBelow) {
        int visual = tile == 6 ? DOOR_SIDEWAYS_OVERHANG : (tile == 5 ? DOOR_SIDEWAYS_OVERHANG_CLOSED : (tile == 10 ? DOOR_SIDEWAYS_OVERHANG_LOCKED : (tile == 38 ? DOOR_SIDEWAYS_OVERHANG_LOCKED : (tile == 31 ? DOOR_SIDEWAYS_OVERHANG_CRYSTAL : (Dungeon.branch == 1 && below == 12 ? WALL_OVERHANG_DECO : (below == 27 ? WALL_OVERHANG_WOODEN : WALL_OVERHANG))))));
        if (!DungeonTileSheet.wallStitcheable(rightBelow)) {
            ++visual;
        }
        if (!DungeonTileSheet.wallStitcheable(leftBelow)) {
            visual += 2;
        }
        return visual;
    }

    public static void setupVariance(int size, long seed) {
        Random.pushGenerator(seed);
        tileVariance = new byte[size];
        for (int i = 0; i < tileVariance.length; ++i) {
            DungeonTileSheet.tileVariance[i] = (byte)Random.Int(100);
        }
        Random.popGenerator();
        DungeonTileSheet.updateAltVariants();
    }

    public static void updateAltVariants() {
        if (Dungeon.level instanceof MiningLevel) {
            if (Blacksmith.Quest.Type() == 1) {
                tileAltVisuals.put(FLAT_MINE_CRYSTAL, new tileAlt(new float[]{66.7f, 33.3f}, FLAT_MINE_CRYSTAL_ALT, FLAT_MINE_CRYSTAL_ALT_2));
                tileAltVisuals.put(RAISED_MINE_CRYSTAL_BLUE_1, new tileAlt(new float[]{83.3f, 66.7f, 50.0f, 33.3f, 16.7f}, RAISED_MINE_CRYSTAL_BLUE_2, RAISED_MINE_CRYSTAL_GREEN_1, RAISED_MINE_CRYSTAL_GREEN_2, RAISED_MINE_CRYSTAL_RED_1, RAISED_MINE_CRYSTAL_RED_2));
                tileAltVisuals.put(MINE_CRYSTAL_OVERHANG_BLUE, new tileAlt(new float[]{66.7f, 33.3f}, MINE_CRYSTAL_OVERHANG_GREEN, MINE_CRYSTAL_OVERHANG_RED));
            } else if (Blacksmith.Quest.Type() == 2) {
                tileAltVisuals.put(MINE_FLOOR_DECO_HEAVY, new tileAlt(new float[]{50.0f}, MINE_FLOOR_DECO_HEAVY_ALT));
                tileAltVisuals.put(FLAT_MINE_BOULDER, new tileAlt(new float[]{66.7f, 33.3f}, FLAT_MINE_BOULDER_ALT, FLAT_MINE_BOULDER_ALT_2));
                tileAltVisuals.put(RAISED_MINE_BOULDER, new tileAlt(new float[]{66.7f, 33.3f}, RAISED_MINE_BOULDER_ALT, RAISED_MINE_BOULDER_ALT_2));
                tileAltVisuals.put(MINE_BOULDER_OVERHANG, new tileAlt(new float[]{66.7f, 33.3f}, MINE_BOULDER_OVERHANG_ALT, MINE_BOULDER_OVERHANG_ALT_2));
            }
        }
    }

    public static int getVisualWithAlts(int visual, int pos) {
        if (tileAltVisuals.containsKey(visual)) {
            tileAlt alts = (tileAlt)tileAltVisuals.get(visual);
            for (int i = 0; i < alts.chances.length; ++i) {
                if (!((float)tileVariance[pos] < alts.chances[i])) continue;
                visual = alts.alts[i];
            }
        }
        return visual;
    }

    static {
        chasmStitcheable.put(1, CHASM_FLOOR);
        chasmStitcheable.put(2, CHASM_FLOOR);
        chasmStitcheable.put(9, CHASM_FLOOR);
        chasmStitcheable.put(3, CHASM_FLOOR);
        chasmStitcheable.put(15, CHASM_FLOOR);
        chasmStitcheable.put(30, CHASM_FLOOR);
        chasmStitcheable.put(20, CHASM_FLOOR);
        chasmStitcheable.put(23, CHASM_FLOOR);
        chasmStitcheable.put(3, CHASM_FLOOR);
        chasmStitcheable.put(24, CHASM_FLOOR);
        chasmStitcheable.put(25, CHASM_FLOOR);
        chasmStitcheable.put(33, CHASM_FLOOR);
        chasmStitcheable.put(17, CHASM_FLOOR);
        chasmStitcheable.put(19, CHASM_FLOOR);
        chasmStitcheable.put(18, CHASM_FLOOR);
        chasmStitcheable.put(27, CHASM_FLOOR);
        chasmStitcheable.put(13, CHASM_FLOOR);
        chasmStitcheable.put(11, CHASM_FLOOR);
        chasmStitcheable.put(32, CHASM_FLOOR);
        chasmStitcheable.put(36, CHASM_FLOOR);
        chasmStitcheable.put(35, CHASM_FLOOR);
        chasmStitcheable.put(14, CHASM_FLOOR_SP);
        chasmStitcheable.put(26, CHASM_FLOOR_SP);
        chasmStitcheable.put(4, CHASM_WALL);
        chasmStitcheable.put(5, CHASM_WALL);
        chasmStitcheable.put(6, CHASM_WALL);
        chasmStitcheable.put(10, CHASM_WALL);
        chasmStitcheable.put(38, CHASM_WALL);
        chasmStitcheable.put(16, CHASM_WALL);
        chasmStitcheable.put(12, CHASM_WALL);
        chasmStitcheable.put(29, CHASM_WATER);
        WATER = DungeonTileSheet.xy(1, 3);
        waterStitcheable = new HashSet<Integer>(Arrays.asList(1, 2, 3, 7, 8, 9, 13, 15, 30, 17, 18, 19, 20, 23, 24, 25, 33, 28, 32, 35, 36, 5, 6, 10, 38, 31));
        FLAT_WALLS = DungeonTileSheet.xy(1, 4);
        FLAT_WALL = FLAT_WALLS + 0;
        FLAT_WALL_DECO = FLAT_WALLS + 1;
        FLAT_BOOKSHELF = FLAT_WALLS + 2;
        FLAT_WALL_ALT = FLAT_WALLS + 4;
        FLAT_WALL_DECO_ALT = FLAT_WALLS + 5;
        FLAT_BOOKSHELF_ALT = FLAT_WALLS + 6;
        FLAT_DOOR = FLAT_WALLS + 8;
        FLAT_DOOR_OPEN = FLAT_WALLS + 9;
        FLAT_DOOR_LOCKED = FLAT_WALLS + 10;
        FLAT_DOOR_CRYSTAL = FLAT_WALLS + 11;
        UNLOCKED_EXIT = FLAT_WALLS + 12;
        LOCKED_EXIT = FLAT_WALLS + 13;
        FLAT_OTHER = DungeonTileSheet.xy(1, 5);
        FLAT_ALCHEMY_POT = FLAT_OTHER + 0;
        FLAT_BARRICADE = FLAT_OTHER + 1;
        FLAT_HIGH_GRASS = FLAT_OTHER + 2;
        FLAT_FURROWED_GRASS = FLAT_OTHER + 3;
        FLAT_HIGH_GRASS_ALT = FLAT_OTHER + 5;
        FLAT_FURROWED_ALT = FLAT_OTHER + 6;
        FLAT_STATUE = FLAT_OTHER + 8;
        FLAT_STATUE_SP = FLAT_OTHER + 9;
        FLAT_REGION_DECO = FLAT_OTHER + 10;
        FLAT_REGION_DECO_ALT = FLAT_OTHER + 11;
        FLAT_MINE_CRYSTAL = FLAT_OTHER + 12;
        FLAT_MINE_CRYSTAL_ALT = FLAT_OTHER + 13;
        FLAT_MINE_CRYSTAL_ALT_2 = FLAT_OTHER + 14;
        FLAT_MINE_BOULDER = FLAT_OTHER + 12;
        FLAT_MINE_BOULDER_ALT = FLAT_OTHER + 13;
        FLAT_MINE_BOULDER_ALT_2 = FLAT_OTHER + 14;
        RAISED_WALLS = DungeonTileSheet.xy(1, 6);
        RAISED_WALL = RAISED_WALLS + 0;
        RAISED_WALL_DECO = RAISED_WALLS + 4;
        RAISED_WALL_DOOR = RAISED_WALLS + 8;
        RAISED_WALL_BOOKSHELF = RAISED_WALLS + 12;
        RAISED_WALL_ALT = RAISED_WALLS + 16;
        RAISED_WALL_DECO_ALT = RAISED_WALLS + 20;
        RAISED_WALL_BOOKSHELF_ALT = RAISED_WALLS + 28;
        wallStitcheable = new int[]{4, 12, 16, 21, 22, 27, -1};
        RAISED_DOORS = DungeonTileSheet.xy(1, 8);
        RAISED_DOOR = RAISED_DOORS + 0;
        RAISED_DOOR_OPEN = RAISED_DOORS + 1;
        RAISED_DOOR_LOCKED = RAISED_DOORS + 2;
        RAISED_DOOR_CRYSTAL = RAISED_DOORS + 3;
        RAISED_DOOR_SIDEWAYS = RAISED_DOORS + 4;
        doorTiles = new int[]{5, 10, 38, 31, 6};
        RAISED_OTHER = DungeonTileSheet.xy(9, 8);
        RAISED_ALCHEMY_POT = RAISED_OTHER + 0;
        RAISED_BARRICADE = RAISED_OTHER + 1;
        RAISED_HIGH_GRASS = RAISED_OTHER + 2;
        RAISED_FURROWED_GRASS = RAISED_OTHER + 3;
        RAISED_HIGH_GRASS_ALT = RAISED_OTHER + 5;
        RAISED_FURROWED_ALT = RAISED_OTHER + 6;
        RAISED_STATUE = RAISED_OTHER + 8;
        RAISED_STATUE_SP = RAISED_OTHER + 9;
        RAISED_REGION_DECO = RAISED_OTHER + 10;
        RAISED_REGION_DECO_ALT = RAISED_OTHER + 11;
        RAISED_MINE_CRYSTAL_BLUE_1 = RAISED_OTHER + 12;
        RAISED_MINE_CRYSTAL_BLUE_2 = RAISED_OTHER + 13;
        RAISED_MINE_CRYSTAL_GREEN_1 = RAISED_OTHER + 14;
        RAISED_MINE_CRYSTAL_GREEN_2 = RAISED_OTHER + 15;
        RAISED_MINE_CRYSTAL_RED_1 = RAISED_OTHER + 16;
        RAISED_MINE_CRYSTAL_RED_2 = RAISED_OTHER + 17;
        RAISED_MINE_BOULDER = RAISED_OTHER + 12;
        RAISED_MINE_BOULDER_ALT = RAISED_OTHER + 13;
        RAISED_MINE_BOULDER_ALT_2 = RAISED_OTHER + 14;
        WALLS_INTERNAL = DungeonTileSheet.xy(1, 10);
        WALL_INTERNAL = WALLS_INTERNAL + 0;
        WALL_INTERNAL_DECO = WALLS_INTERNAL + 16;
        WALL_INTERNAL_WOODEN = WALLS_INTERNAL + 32;
        WALLS_OVERHANG = DungeonTileSheet.xy(1, 13);
        WALL_OVERHANG = WALLS_OVERHANG + 0;
        WALL_OVERHANG_DECO = WALLS_OVERHANG + 4;
        WALL_OVERHANG_WOODEN = WALLS_OVERHANG + 8;
        DOOR_SIDEWAYS_OVERHANG = WALLS_OVERHANG + 16;
        DOOR_SIDEWAYS_OVERHANG_CLOSED = WALLS_OVERHANG + 20;
        DOOR_SIDEWAYS_OVERHANG_LOCKED = WALLS_OVERHANG + 24;
        DOOR_SIDEWAYS_OVERHANG_CRYSTAL = WALLS_OVERHANG + 28;
        DOOR_OVERHANG = DungeonTileSheet.xy(1, 15);
        DOOR_OVERHANG_OPEN = DOOR_OVERHANG + 1;
        DOOR_OVERHANG_CRYSTAL = DOOR_OVERHANG + 2;
        DOOR_SIDEWAYS = DOOR_OVERHANG + 3;
        DOOR_SIDEWAYS_LOCKED = DOOR_OVERHANG + 4;
        DOOR_SIDEWAYS_CRYSTAL = DOOR_OVERHANG + 5;
        EXIT_UNDERHANG = DOOR_OVERHANG + 6;
        OTHER_OVERHANG = DungeonTileSheet.xy(9, 15);
        ALCHEMY_POT_OVERHANG = OTHER_OVERHANG + 0;
        BARRICADE_OVERHANG = OTHER_OVERHANG + 1;
        HIGH_GRASS_OVERHANG = OTHER_OVERHANG + 2;
        FURROWED_OVERHANG = OTHER_OVERHANG + 3;
        HIGH_GRASS_OVERHANG_ALT = OTHER_OVERHANG + 5;
        FURROWED_OVERHANG_ALT = OTHER_OVERHANG + 6;
        STATUE_OVERHANG = OTHER_OVERHANG + 8;
        STATUE_SP_OVERHANG = OTHER_OVERHANG + 9;
        REGION_DECO_OVERHANG = OTHER_OVERHANG + 10;
        REGION_DECO_ALT_OVERHANG = OTHER_OVERHANG + 11;
        MINE_CRYSTAL_OVERHANG_BLUE = OTHER_OVERHANG + 12;
        MINE_CRYSTAL_OVERHANG_GREEN = OTHER_OVERHANG + 13;
        MINE_CRYSTAL_OVERHANG_RED = OTHER_OVERHANG + 14;
        MINE_BOULDER_OVERHANG = OTHER_OVERHANG + 12;
        MINE_BOULDER_OVERHANG_ALT = OTHER_OVERHANG + 13;
        MINE_BOULDER_OVERHANG_ALT_2 = OTHER_OVERHANG + 14;
        directVisuals = new SparseArray();
        directVisuals.put(1, FLOOR);
        directVisuals.put(2, GRASS);
        directVisuals.put(3, EMPTY_WELL);
        directVisuals.put(7, ENTRANCE);
        directVisuals.put(8, EXIT);
        directVisuals.put(9, EMBERS);
        directVisuals.put(11, PEDESTAL);
        directVisuals.put(14, FLOOR_SP);
        directVisuals.put(37, ENTRANCE_SP);
        directVisuals.put(17, (Integer)directVisuals.get(1));
        directVisuals.put(18, (Integer)directVisuals.get(1));
        directVisuals.put(19, (Integer)directVisuals.get(1));
        directVisuals.put(23, (Integer)directVisuals.get(1));
        directVisuals.put(32, (Integer)directVisuals.get(1));
        directVisuals.put(39, WATER);
        directVisuals.put(20, FLOOR_DECO);
        directVisuals.put(20, FLOOR_DECO);
        directVisuals.put(21, LOCKED_EXIT);
        directVisuals.put(22, UNLOCKED_EXIT);
        directVisuals.put(24, WELL);
        directFlatVisuals = new SparseArray();
        directFlatVisuals.put(4, FLAT_WALL);
        directFlatVisuals.put(5, FLAT_DOOR);
        directFlatVisuals.put(6, FLAT_DOOR_OPEN);
        directFlatVisuals.put(10, FLAT_DOOR_LOCKED);
        directFlatVisuals.put(38, FLAT_DOOR_LOCKED);
        directFlatVisuals.put(31, FLAT_DOOR_CRYSTAL);
        directFlatVisuals.put(12, FLAT_WALL_DECO);
        directFlatVisuals.put(27, FLAT_BOOKSHELF);
        directFlatVisuals.put(28, FLAT_ALCHEMY_POT);
        directFlatVisuals.put(13, FLAT_BARRICADE);
        directFlatVisuals.put(15, FLAT_HIGH_GRASS);
        directFlatVisuals.put(30, FLAT_FURROWED_GRASS);
        directFlatVisuals.put(25, FLAT_STATUE);
        directFlatVisuals.put(26, FLAT_STATUE_SP);
        directFlatVisuals.put(33, FLAT_REGION_DECO);
        directFlatVisuals.put(34, FLAT_REGION_DECO_ALT);
        directFlatVisuals.put(35, FLAT_MINE_CRYSTAL);
        directFlatVisuals.put(36, FLAT_MINE_BOULDER);
        directFlatVisuals.put(16, (Integer)directFlatVisuals.get(4));
        tileAltVisuals = new SparseArray();
        tileAltVisuals.put(FLOOR, new tileAlt(new float[]{52.5f, 5.0f}, FLOOR_ALT_1, FLOOR_ALT_2));
        tileAltVisuals.put(GRASS, new tileAlt(new float[]{50.0f}, GRASS_ALT));
        tileAltVisuals.put(FLAT_WALL, new tileAlt(new float[]{50.0f}, FLAT_WALL_ALT));
        tileAltVisuals.put(EMBERS, new tileAlt(new float[]{50.0f}, EMBERS_ALT));
        tileAltVisuals.put(FLAT_WALL_DECO, new tileAlt(new float[]{50.0f}, FLAT_WALL_DECO_ALT));
        tileAltVisuals.put(FLOOR_SP, new tileAlt(new float[]{50.0f}, FLOOR_SP_ALT));
        tileAltVisuals.put(FLOOR_DECO, new tileAlt(new float[]{50.0f}, FLOOR_DECO_ALT));
        tileAltVisuals.put(GRASS, new tileAlt(new float[]{50.0f}, GRASS_ALT));
        tileAltVisuals.put(FLAT_BOOKSHELF, new tileAlt(new float[]{50.0f}, FLAT_BOOKSHELF_ALT));
        tileAltVisuals.put(FLAT_HIGH_GRASS, new tileAlt(new float[]{50.0f}, FLAT_HIGH_GRASS_ALT));
        tileAltVisuals.put(FLAT_FURROWED_GRASS, new tileAlt(new float[]{50.0f}, FLAT_FURROWED_ALT));
        tileAltVisuals.put(RAISED_WALL, new tileAlt(new float[]{50.0f}, RAISED_WALL_ALT));
        tileAltVisuals.put(RAISED_WALL_DECO, new tileAlt(new float[]{50.0f}, RAISED_WALL_DECO_ALT));
        tileAltVisuals.put(RAISED_WALL_BOOKSHELF, new tileAlt(new float[]{50.0f}, RAISED_WALL_BOOKSHELF_ALT));
        tileAltVisuals.put(RAISED_HIGH_GRASS, new tileAlt(new float[]{50.0f}, RAISED_HIGH_GRASS_ALT));
        tileAltVisuals.put(RAISED_FURROWED_GRASS, new tileAlt(new float[]{50.0f}, RAISED_FURROWED_ALT));
        tileAltVisuals.put(HIGH_GRASS_OVERHANG, new tileAlt(new float[]{50.0f}, HIGH_GRASS_OVERHANG_ALT));
        tileAltVisuals.put(FURROWED_OVERHANG, new tileAlt(new float[]{50.0f}, FURROWED_OVERHANG_ALT));
    }

    private static class tileAlt {
        float[] chances;
        int[] alts;

        public tileAlt(float[] chances, int ... alts) {
            this.chances = chances;
            this.alts = alts;
        }
    }
}

