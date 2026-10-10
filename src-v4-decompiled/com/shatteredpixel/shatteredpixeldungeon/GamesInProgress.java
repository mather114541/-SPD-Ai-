/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class GamesInProgress {
    public static final int MAX_SLOTS = HeroClass.values().length;
    private static HashMap<Integer, Info> slotStates = new HashMap();
    public static int curSlot;
    public static HeroClass selectedClass;
    public static boolean randomizedClass;
    private static final String GAME_FOLDER = "game%d";
    private static final String GAME_FILE = "game.dat";
    private static final String DEPTH_FILE = "depth%d.dat";
    private static final String DEPTH_BRANCH_FILE = "depth%d-branch%d.dat";
    public static final Comparator<Info> levelComparator;
    public static final Comparator<Info> lastPlayedComparator;

    public static boolean gameExists(int slot) {
        return FileUtils.dirExists(GamesInProgress.gameFolder(slot)) && FileUtils.fileLength(GamesInProgress.gameFile(slot)) > 1L;
    }

    public static String gameFolder(int slot) {
        return Messages.format(GAME_FOLDER, slot);
    }

    public static String gameFile(int slot) {
        return GamesInProgress.gameFolder(slot) + "/game.dat";
    }

    public static String depthFile(int slot, int depth, int branch) {
        if (branch == 0) {
            return GamesInProgress.gameFolder(slot) + "/" + Messages.format(DEPTH_FILE, depth);
        }
        return GamesInProgress.gameFolder(slot) + "/" + Messages.format(DEPTH_BRANCH_FILE, depth, branch);
    }

    public static int firstEmpty() {
        for (int i = 1; i <= MAX_SLOTS; ++i) {
            if (GamesInProgress.check(i) != null) continue;
            return i;
        }
        return -1;
    }

    public static ArrayList<Info> checkAll() {
        ArrayList<Info> result = new ArrayList<Info>();
        for (int i = 1; i <= MAX_SLOTS; ++i) {
            Info curr = GamesInProgress.check(i);
            if (curr == null) continue;
            result.add(curr);
        }
        switch (SPDSettings.gamesInProgressSort()) {
            default: {
                Collections.sort(result, levelComparator);
                break;
            }
            case "last_played": {
                Collections.sort(result, lastPlayedComparator);
            }
        }
        return result;
    }

    public static Info check(int slot) {
        Info info;
        if (slotStates.containsKey(slot)) {
            return slotStates.get(slot);
        }
        if (!GamesInProgress.gameExists(slot)) {
            slotStates.put(slot, null);
            return null;
        }
        try {
            Bundle bundle = FileUtils.bundleFromFile(GamesInProgress.gameFile(slot));
            if (bundle.getInt("version") < 850) {
                info = null;
            } else {
                info = new Info();
                info.slot = slot;
                Dungeon.preview(info, bundle);
            }
        }
        catch (IOException e) {
            info = null;
        }
        catch (Exception e) {
            ShatteredPixelDungeon.reportException(e);
            info = null;
        }
        slotStates.put(slot, info);
        return info;
    }

    public static void set(int slot) {
        Info info = new Info();
        info.slot = slot;
        info.lastPlayed = Dungeon.lastPlayed;
        info.depth = Dungeon.depth;
        info.challenges = Dungeon.challenges;
        info.seed = Dungeon.seed;
        info.customSeed = Dungeon.customSeedText;
        info.daily = Dungeon.daily;
        info.dailyReplay = Dungeon.dailyReplay;
        info.level = Dungeon.hero.lvl;
        info.str = Dungeon.hero.STR;
        info.strBonus = Dungeon.hero.STR() - Dungeon.hero.STR;
        info.exp = Dungeon.hero.exp;
        info.hp = Dungeon.hero.HP;
        info.ht = Dungeon.hero.HT;
        info.shld = Dungeon.hero.shielding();
        info.heroClass = Dungeon.hero.heroClass;
        info.subClass = Dungeon.hero.subClass;
        info.armorTier = Dungeon.hero.tier();
        info.goldCollected = Statistics.goldCollected;
        info.maxDepth = Statistics.deepestFloor;
        slotStates.put(slot, info);
    }

    public static void setUnknown(int slot) {
        slotStates.remove(slot);
    }

    public static void delete(int slot) {
        slotStates.put(slot, null);
    }

    static {
        randomizedClass = false;
        levelComparator = new Comparator<Info>(){

            @Override
            public int compare(Info lhs, Info rhs) {
                if (rhs.level != lhs.level) {
                    return (int)Math.signum(rhs.level - lhs.level);
                }
                return lastPlayedComparator.compare(lhs, rhs);
            }
        };
        lastPlayedComparator = new Comparator<Info>(){

            @Override
            public int compare(Info lhs, Info rhs) {
                return (int)Math.signum(rhs.lastPlayed - lhs.lastPlayed);
            }
        };
    }

    public static class Info {
        public int slot;
        public int depth;
        public int version;
        public int challenges;
        public long seed;
        public String customSeed;
        public boolean daily;
        public boolean dailyReplay;
        public long lastPlayed;
        public int level;
        public int str;
        public int strBonus;
        public int exp;
        public int hp;
        public int ht;
        public int shld;
        public HeroClass heroClass;
        public HeroSubClass subClass;
        public int armorTier;
        public int goldCollected;
        public int maxDepth;
    }
}

