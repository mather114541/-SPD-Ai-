/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

public class Terrain {
    public static final int CHASM = 0;
    public static final int EMPTY = 1;
    public static final int GRASS = 2;
    public static final int EMPTY_WELL = 3;
    public static final int WALL = 4;
    public static final int DOOR = 5;
    public static final int OPEN_DOOR = 6;
    public static final int ENTRANCE = 7;
    public static final int ENTRANCE_SP = 37;
    public static final int EXIT = 8;
    public static final int EMBERS = 9;
    public static final int LOCKED_DOOR = 10;
    public static final int HERO_LKD_DR = 38;
    public static final int CRYSTAL_DOOR = 31;
    public static final int PEDESTAL = 11;
    public static final int WALL_DECO = 12;
    public static final int BARRICADE = 13;
    public static final int EMPTY_SP = 14;
    public static final int HIGH_GRASS = 15;
    public static final int FURROWED_GRASS = 30;
    public static final int SECRET_DOOR = 16;
    public static final int SECRET_TRAP = 17;
    public static final int TRAP = 18;
    public static final int INACTIVE_TRAP = 19;
    public static final int EMPTY_DECO = 20;
    public static final int LOCKED_EXIT = 21;
    public static final int UNLOCKED_EXIT = 22;
    public static final int WELL = 24;
    public static final int BOOKSHELF = 27;
    public static final int ALCHEMY = 28;
    public static final int CUSTOM_DECO_EMPTY = 32;
    public static final int CUSTOM_DECO = 23;
    public static final int CUSTOM_DECO_WTR = 39;
    public static final int STATUE = 25;
    public static final int STATUE_SP = 26;
    public static final int REGION_DECO = 33;
    public static final int REGION_DECO_ALT = 34;
    public static final int MINE_CRYSTAL = 35;
    public static final int MINE_BOULDER = 36;
    public static final int WATER = 29;
    public static final int PASSABLE = 1;
    public static final int LOS_BLOCKING = 2;
    public static final int FLAMABLE = 4;
    public static final int SECRET = 8;
    public static final int SOLID = 16;
    public static final int AVOID = 32;
    public static final int LIQUID = 64;
    public static final int PIT = 128;
    public static final int[] flags = new int[256];

    public static int discover(int terr) {
        switch (terr) {
            case 16: {
                return 5;
            }
            case 17: {
                return 18;
            }
        }
        return terr;
    }

    static {
        Terrain.flags[0] = 160;
        Terrain.flags[1] = 1;
        Terrain.flags[2] = 5;
        Terrain.flags[3] = 1;
        Terrain.flags[29] = 65;
        Terrain.flags[4] = 18;
        Terrain.flags[5] = 23;
        Terrain.flags[6] = 5;
        Terrain.flags[7] = 1;
        Terrain.flags[37] = flags[7];
        Terrain.flags[8] = 1;
        Terrain.flags[9] = 1;
        Terrain.flags[10] = 18;
        Terrain.flags[38] = flags[10];
        Terrain.flags[31] = 16;
        Terrain.flags[11] = 1;
        Terrain.flags[12] = flags[4];
        Terrain.flags[13] = 22;
        Terrain.flags[14] = flags[1];
        Terrain.flags[15] = 7;
        Terrain.flags[30] = flags[15];
        Terrain.flags[16] = flags[4] | 8;
        Terrain.flags[17] = flags[1] | 8;
        Terrain.flags[18] = 32;
        Terrain.flags[19] = flags[1];
        Terrain.flags[20] = flags[1];
        Terrain.flags[21] = 16;
        Terrain.flags[22] = 1;
        Terrain.flags[24] = 32;
        Terrain.flags[27] = flags[13];
        Terrain.flags[28] = 16;
        Terrain.flags[32] = flags[1];
        Terrain.flags[23] = 16;
        Terrain.flags[39] = 16;
        Terrain.flags[25] = 16;
        Terrain.flags[26] = flags[25];
        Terrain.flags[33] = flags[25];
        Terrain.flags[34] = flags[26];
        Terrain.flags[35] = 16;
        Terrain.flags[36] = 16;
    }
}

