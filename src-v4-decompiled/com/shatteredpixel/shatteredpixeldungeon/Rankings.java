/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.EscapeCrystal;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Rankings {
    INSTANCE;

    public static final int TABLE_SIZE = 11;
    public static final String RANKINGS_FILE = "rankings.dat";
    public ArrayList<Record> records;
    public int lastRecord;
    public int totalNumber;
    public int wonNumber;
    public int localTotal;
    public int localWon;
    public Record latestDaily;
    public Record latestDailyReplay = null;
    public LinkedHashMap<Long, Integer> dailyScoreHistory = new LinkedHashMap();
    public static final String HERO = "hero";
    public static final String STATS = "stats";
    public static final String BADGES = "badges";
    public static final String HANDLERS = "handlers";
    public static final String CHALLENGES = "challenges";
    public static final String GAME_VERSION = "game_version";
    public static final String SEED = "seed";
    public static final String CUSTOM_SEED = "custom_seed";
    public static final String DAILY = "daily";
    public static final String DAILY_REPLAY = "daily_replay";
    private static final String RECORDS = "records";
    private static final String LATEST = "latest";
    private static final String TOTAL = "total";
    private static final String WON = "won";
    public static final String LATEST_DAILY = "latest_daily";
    public static final String DAILY_HISTORY_DATES = "daily_history_dates";
    public static final String DAILY_HISTORY_SCORES = "daily_history_scores";
    public static final Comparator<Record> scoreComparator;

    public void submit(boolean win, Object cause) {
        this.load();
        Record rec = new Record();
        Pattern p = Pattern.compile("\\d+\\.\\d+\\.\\d+");
        Matcher m = p.matcher(ShatteredPixelDungeon.version);
        rec.version = m.find() ? "v" + m.group() : "";
        EscapeCrystal crystal = Dungeon.hero.belongings.getItem(EscapeCrystal.class);
        EscapeCrystal tempStore = new EscapeCrystal();
        if (crystal != null) {
            crystal.detachAll(Dungeon.hero.belongings.backpack);
            tempStore.storeHeroBelongings(Dungeon.hero);
            crystal.restoreHeroBelongings(Dungeon.hero, null);
            tempStore.collect();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        rec.date = format.format(new Date(Game.realTime));
        rec.cause = cause instanceof Class ? (Class<?>)cause : cause.getClass();
        rec.win = win;
        rec.heroClass = Dungeon.hero.heroClass;
        rec.armorTier = Dungeon.hero.tier();
        rec.herolevel = Dungeon.hero.lvl;
        if (Statistics.highestAscent == 0) {
            rec.depth = Statistics.deepestFloor;
            rec.ascending = false;
        } else {
            rec.depth = Statistics.highestAscent;
            rec.ascending = true;
        }
        rec.score = this.calculateScore();
        rec.customSeed = Dungeon.customSeedText;
        rec.daily = Dungeon.daily;
        Badges.validateHighScore(rec.score);
        INSTANCE.saveGameData(rec);
        rec.gameID = UUID.randomUUID().toString();
        if (rec.daily) {
            if (Dungeon.dailyReplay) {
                this.latestDailyReplay = rec;
                return;
            }
            this.latestDaily = rec;
            if (Dungeon.seed <= DungeonSeed.TOTAL_SEEDS) {
                this.dailyScoreHistory.put(Dungeon.seed, rec.score);
            } else {
                this.dailyScoreHistory.put(Dungeon.seed - DungeonSeed.TOTAL_SEEDS, rec.score);
            }
            this.save();
            return;
        }
        this.records.add(rec);
        Collections.sort(this.records, scoreComparator);
        this.lastRecord = this.records.indexOf(rec);
        int size = this.records.size();
        while (size > 11) {
            if (this.lastRecord == size - 1) {
                this.records.remove(size - 2);
                --this.lastRecord;
            } else {
                this.records.remove(size - 1);
            }
            size = this.records.size();
        }
        if (rec.customSeed.isEmpty()) {
            ++this.totalNumber;
            if (win) {
                ++this.wonNumber;
            }
        }
        Badges.validateGamesPlayed();
        this.save();
        if (crystal != null) {
            tempStore.restoreHeroBelongings(Dungeon.hero, null);
        }
    }

    private int score(boolean win) {
        return (Statistics.goldCollected + Dungeon.hero.lvl * (win ? 26 : Dungeon.depth) * 100) * (win ? 2 : 1);
    }

    public int calculateScore() {
        if (Dungeon.initialVersion > 628) {
            Statistics.progressScore = Dungeon.hero.lvl * Statistics.deepestFloor * 65;
            Statistics.progressScore = Math.min(Statistics.progressScore, 50000);
            if (Statistics.heldItemValue == 0) {
                EscapeCrystal crystal = Dungeon.hero.belongings.getItem(EscapeCrystal.class);
                if (crystal != null && crystal.storedItems != null) {
                    Belongings stored = new Belongings(Dungeon.hero);
                    stored.restoreFromBundle(crystal.storedItems.getBundle(EscapeCrystal.BELONGINGS));
                    for (Item item : stored) {
                    }
                }
                for (Item i : Dungeon.hero.belongings) {
                    Statistics.heldItemValue += i.value();
                    if (!(i instanceof CorpseDust) || Statistics.deepestFloor < 10) continue;
                    Statistics.questScores[1] = 2000;
                }
            }
            Statistics.treasureScore = Statistics.goldCollected + Statistics.heldItemValue;
            Statistics.treasureScore = Math.min(Statistics.treasureScore, 20000);
            Statistics.exploreScore = 0;
            int scorePerFloor = Statistics.floorsExplored.size * 50;
            Iterator<Float> iterator = Statistics.floorsExplored.valueList().iterator();
            while (iterator.hasNext()) {
                float percentExplored = ((Float)iterator.next()).floatValue();
                Statistics.exploreScore += Math.round(percentExplored * (float)scorePerFloor);
            }
            Statistics.totalBossScore = 0;
            for (Object i : (Iterator<Float>)Statistics.bossScores) {
                if (i <= 0) continue;
                Statistics.totalBossScore += i;
            }
            Statistics.totalQuestScore = 0;
            for (Object i : (Iterator<Float>)Statistics.questScores) {
                if (i <= 0) continue;
                Statistics.totalQuestScore += i;
            }
            Statistics.winMultiplier = 1.0f;
            if (Statistics.gameWon) {
                Statistics.winMultiplier += 1.0f;
            }
            if (Statistics.ascended) {
                Statistics.winMultiplier += 0.5f;
            }
        } else {
            Statistics.progressScore = Dungeon.hero.lvl * Statistics.deepestFloor * 100;
            Statistics.treasureScore = Math.min(Statistics.goldCollected, 30000);
            Statistics.totalQuestScore = 0;
            Statistics.totalBossScore = 0;
            Statistics.exploreScore = 0;
            Statistics.winMultiplier = Statistics.gameWon ? 2.0f : 1.0f;
        }
        Statistics.chalMultiplier = (float)Math.pow(1.25, Challenges.activeChallenges());
        Statistics.chalMultiplier = (float)Math.round(Statistics.chalMultiplier * 20.0f) / 20.0f;
        Statistics.totalScore = Statistics.progressScore + Statistics.treasureScore + Statistics.exploreScore + Statistics.totalBossScore + Statistics.totalQuestScore;
        Statistics.totalScore = (int)((float)Statistics.totalScore * (Statistics.winMultiplier * Statistics.chalMultiplier));
        return Statistics.totalScore;
    }

    public void saveGameData(Record rec) {
        if (Dungeon.hero == null) {
            rec.gameData = null;
            return;
        }
        rec.gameData = new Bundle();
        Belongings belongings = Dungeon.hero.belongings;
        ArrayList allItems = (ArrayList)belongings.backpack.items.clone();
        for (Item item : belongings.backpack.items.toArray(new Item[0])) {
            if (item instanceof Bag) {
                for (Item bagItem : ((Bag)item).items.toArray(new Item[0])) {
                    if (!Dungeon.quickslot.contains(bagItem) || Dungeon.quickslot.contains(item)) continue;
                    belongings.backpack.items.add(bagItem);
                }
            }
            if (item instanceof Trinket || item instanceof EscapeCrystal || Dungeon.quickslot.contains(item)) continue;
            belongings.backpack.items.remove(item);
        }
        for (Buff b : Dungeon.hero.buffs()) {
            if (b instanceof MeleeWeapon.Charger) continue;
            Dungeon.hero.remove(b);
        }
        rec.gameData.put(HERO, Dungeon.hero);
        Bundle stats = new Bundle();
        Statistics.storeInBundle(stats);
        rec.gameData.put(STATS, stats);
        Bundle badges = new Bundle();
        Badges.saveLocal(badges);
        rec.gameData.put(BADGES, badges);
        Bundle handler = new Bundle();
        Scroll.saveSelectively(handler, belongings.backpack.items);
        Potion.saveSelectively(handler, belongings.backpack.items);
        if (belongings.misc != null) {
            belongings.backpack.items.add(belongings.misc);
        }
        if (belongings.ring != null) {
            belongings.backpack.items.add(belongings.ring);
        }
        Ring.saveSelectively(handler, belongings.backpack.items);
        rec.gameData.put(HANDLERS, handler);
        belongings.backpack.items = allItems;
        rec.gameData.put(CHALLENGES, Dungeon.challenges);
        rec.gameData.put(GAME_VERSION, Dungeon.initialVersion);
        rec.gameData.put(SEED, Dungeon.seed);
        rec.gameData.put(CUSTOM_SEED, Dungeon.customSeedText);
        rec.gameData.put(DAILY, Dungeon.daily);
        rec.gameData.put(DAILY_REPLAY, Dungeon.dailyReplay);
    }

    public void loadGameData(Record rec) {
        Bundle data = rec.gameData;
        Actor.clear();
        Dungeon.hero = null;
        Dungeon.level = null;
        Generator.fullReset();
        Notes.reset();
        Dungeon.quickslot.reset();
        QuickSlotButton.reset();
        Toolbar.swappedQuickslots = false;
        if (data == null) {
            return;
        }
        Bundle handler = data.getBundle(HANDLERS);
        Scroll.restore(handler);
        Potion.restore(handler);
        Ring.restore(handler);
        Badges.loadLocal(data.getBundle(BADGES));
        Dungeon.hero = (Hero)data.get(HERO);
        Dungeon.hero.belongings.identify();
        Statistics.restoreFromBundle(data.getBundle(STATS));
        Dungeon.challenges = data.getInt(CHALLENGES);
        Dungeon.initialVersion = data.getInt(GAME_VERSION);
        if (Dungeon.initialVersion <= 628) {
            Statistics.gameWon = rec.win;
        }
        rec.score = this.calculateScore();
        if (rec.gameData.contains(SEED)) {
            Dungeon.seed = rec.gameData.getLong(SEED);
            Dungeon.customSeedText = rec.gameData.getString(CUSTOM_SEED);
            Dungeon.daily = rec.gameData.getBoolean(DAILY);
            Dungeon.dailyReplay = rec.gameData.getBoolean(DAILY_REPLAY);
        } else {
            Dungeon.seed = -1L;
            Dungeon.customSeedText = "";
            Dungeon.dailyReplay = false;
            Dungeon.daily = false;
        }
    }

    public void save() {
        Bundle bundle = new Bundle();
        bundle.put(RECORDS, this.records);
        bundle.put(LATEST, this.lastRecord);
        bundle.put(TOTAL, this.totalNumber);
        bundle.put(WON, this.wonNumber);
        bundle.put(LATEST_DAILY, this.latestDaily);
        long[] dates = new long[this.dailyScoreHistory.size()];
        int[] scores = new int[this.dailyScoreHistory.size()];
        int i = 0;
        for (Long l : this.dailyScoreHistory.keySet()) {
            dates[i] = l;
            scores[i] = this.dailyScoreHistory.get(l);
            ++i;
        }
        bundle.put(DAILY_HISTORY_DATES, dates);
        bundle.put(DAILY_HISTORY_SCORES, scores);
        try {
            FileUtils.bundleToFile(RANKINGS_FILE, bundle);
        }
        catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
    }

    public void load() {
        if (this.records != null) {
            return;
        }
        this.records = new ArrayList();
        try {
            Bundle bundle = FileUtils.bundleFromFile(RANKINGS_FILE);
            for (Bundlable record : bundle.getCollection(RECORDS)) {
                this.records.add((Record)record);
            }
            this.lastRecord = bundle.getInt(LATEST);
            this.totalNumber = bundle.getInt(TOTAL);
            if (this.totalNumber == 0) {
                this.totalNumber = this.records.size();
            }
            this.wonNumber = bundle.getInt(WON);
            if (this.wonNumber == 0) {
                for (Record rec : this.records) {
                    if (!rec.win) continue;
                    ++this.wonNumber;
                }
            }
            if (bundle.contains(LATEST_DAILY)) {
                this.latestDaily = (Record)bundle.get(LATEST_DAILY);
                this.dailyScoreHistory.clear();
                int[] scores = bundle.getIntArray(DAILY_HISTORY_SCORES);
                int i = 0;
                long latestDate = 0L;
                for (long date : bundle.getLongArray(DAILY_HISTORY_DATES)) {
                    this.dailyScoreHistory.put(date, scores[i]);
                    if (date > latestDate) {
                        latestDate = date;
                    }
                    ++i;
                }
                if (latestDate > SPDSettings.lastDaily()) {
                    SPDSettings.lastDaily(latestDate);
                }
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    static {
        scoreComparator = new Comparator<Record>(){

            @Override
            public int compare(Record lhs, Record rhs) {
                if (rhs.customSeed.isEmpty() && !lhs.customSeed.isEmpty()) {
                    return 1;
                }
                if (lhs.customSeed.isEmpty() && !rhs.customSeed.isEmpty()) {
                    return -1;
                }
                int result = (int)Math.signum(rhs.score - lhs.score);
                if (result == 0) {
                    return (int)Math.signum(rhs.gameID.hashCode() - lhs.gameID.hashCode());
                }
                return result;
            }
        };
    }

    public static class Record
    implements Bundlable {
        private static final String CAUSE = "cause";
        private static final String WIN = "win";
        private static final String SCORE = "score";
        private static final String CLASS = "class";
        private static final String TIER = "tier";
        private static final String LEVEL = "level";
        private static final String DEPTH = "depth";
        private static final String ASCEND = "ascending";
        private static final String DATA = "gameData";
        private static final String ID = "gameID";
        private static final String SEED = "custom_seed";
        private static final String DAILY = "daily";
        private static final String DATE = "date";
        private static final String VERSION = "version";
        public Class cause;
        public boolean win;
        public HeroClass heroClass;
        public int armorTier;
        public int herolevel;
        public int depth;
        public boolean ascending;
        public Bundle gameData;
        public String gameID;
        public int score;
        public String customSeed;
        public boolean daily;
        public String date;
        public String version;

        public String desc() {
            if (this.win) {
                if (this.ascending) {
                    return Messages.get(this, "ascended", new Object[0]);
                }
                return Messages.get(this, Rankings.WON, new Object[0]);
            }
            if (this.cause == null) {
                return Messages.get(this, "something", new Object[0]);
            }
            String result = Messages.get(this.cause, "rankings_desc", Messages.get(this.cause, "name", new Object[0]));
            if (result.contains("!!!NO TEXT FOUND!!!")) {
                return Messages.get(this, "something", new Object[0]);
            }
            return result;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            this.cause = bundle.contains(CAUSE) ? bundle.getClass(CAUSE) : null;
            this.win = bundle.getBoolean(WIN);
            this.score = bundle.getInt(SCORE);
            this.customSeed = bundle.getString("custom_seed");
            this.daily = bundle.getBoolean("daily");
            this.heroClass = bundle.getEnum(CLASS, HeroClass.class);
            this.armorTier = bundle.getInt(TIER);
            this.herolevel = bundle.getInt(LEVEL);
            this.depth = bundle.getInt(DEPTH);
            this.ascending = bundle.getBoolean(ASCEND);
            if (bundle.contains(DATE)) {
                this.date = bundle.getString(DATE);
                this.version = bundle.getString(VERSION);
            } else {
                this.version = null;
                this.date = null;
            }
            if (bundle.contains(DATA)) {
                this.gameData = bundle.getBundle(DATA);
            }
            if (bundle.contains(ID)) {
                this.gameID = bundle.getString(ID);
            }
            if (this.gameID == null) {
                this.gameID = UUID.randomUUID().toString();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            if (this.cause != null) {
                bundle.put(CAUSE, this.cause);
            }
            bundle.put(WIN, this.win);
            bundle.put(SCORE, this.score);
            bundle.put("custom_seed", this.customSeed);
            bundle.put("daily", this.daily);
            bundle.put(CLASS, this.heroClass);
            bundle.put(TIER, this.armorTier);
            bundle.put(LEVEL, this.herolevel);
            bundle.put(DEPTH, this.depth);
            bundle.put(ASCEND, this.ascending);
            bundle.put(DATE, this.date);
            bundle.put(VERSION, this.version);
            if (this.gameData != null) {
                bundle.put(DATA, this.gameData);
            }
            bundle.put(ID, this.gameID);
        }
    }
}

