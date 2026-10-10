/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.Dewdrop;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class Challenges {
    public static final int NO_FOOD = 1;
    public static final int NO_ARMOR = 2;
    public static final int NO_HEALING = 4;
    public static final int NO_HERBALISM = 8;
    public static final int SWARM_INTELLIGENCE = 16;
    public static final int DARKNESS = 32;
    public static final int NO_SCROLLS = 64;
    public static final int CHAMPION_ENEMIES = 128;
    public static final int STRONGER_BOSSES = 256;
    public static final int MAX_VALUE = 511;
    public static final int MAX_CHALS = 9;
    public static final String[] NAME_IDS = new String[]{"champion_enemies", "stronger_bosses", "no_food", "no_armor", "no_healing", "no_herbalism", "swarm_intelligence", "darkness", "no_scrolls"};
    public static final int[] MASKS = new int[]{128, 256, 1, 2, 4, 8, 16, 32, 64};

    public static int activeChallenges() {
        return Challenges.activeChallenges(Dungeon.challenges);
    }

    public static int activeChallenges(int mask) {
        int chCount = 0;
        for (int ch : MASKS) {
            if ((mask & ch) == 0) continue;
            ++chCount;
        }
        return chCount;
    }

    public static boolean isItemBlocked(Item item) {
        return Dungeon.isChallenged(8) && item instanceof Dewdrop;
    }
}

