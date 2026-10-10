/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Foliage;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfAwareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WaterOfHealth;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DemonSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.ImpShopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LostBackpack;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.BeaconOfReturning;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.WeakFloorRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GhostSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatKingSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ShopkeeperSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpawnerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.StatueSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Notes {
    private static ArrayList<Record> records;
    private static final String RECORDS = "records";
    private static final String NEXT_CUSTOM_ID = "next_custom_id";
    protected static int nextCustomID;
    private static final Comparator<Record> comparator;

    public static void reset() {
        records = new ArrayList();
    }

    public static void storeInBundle(Bundle bundle) {
        bundle.put(RECORDS, records);
        bundle.put(NEXT_CUSTOM_ID, nextCustomID);
    }

    public static void restoreFromBundle(Bundle bundle) {
        records = new ArrayList();
        nextCustomID = bundle.getInt(NEXT_CUSTOM_ID);
        for (Bundlable rec : bundle.getCollection(RECORDS)) {
            records.add((Record)rec);
        }
    }

    public static boolean add(Landmark landmark) {
        return Notes.add(landmark, Dungeon.depth);
    }

    public static boolean add(Landmark landmark, int depth) {
        LandmarkRecord l = new LandmarkRecord(landmark, depth);
        if (!records.contains(l)) {
            boolean result = records.add(l);
            Collections.sort(records, comparator);
            return result;
        }
        return false;
    }

    public static boolean contains(Landmark landmark) {
        return Notes.contains(landmark, Dungeon.depth);
    }

    public static boolean contains(Landmark landmark, int depth) {
        return records.contains(new LandmarkRecord(landmark, depth));
    }

    public static boolean remove(Landmark landmark) {
        return Notes.remove(landmark, Dungeon.depth);
    }

    public static boolean remove(Landmark landmark, int depth) {
        return records.remove(new LandmarkRecord(landmark, depth));
    }

    public static boolean add(Key key) {
        KeyRecord k = new KeyRecord(key);
        if (!records.contains(k)) {
            boolean result = records.add(k);
            Collections.sort(records, comparator);
            return result;
        }
        k = (KeyRecord)records.get(records.indexOf(k));
        k.quantity(k.quantity() + key.quantity());
        return true;
    }

    public static boolean remove(Key key) {
        KeyRecord k = new KeyRecord(key);
        if (records.contains(k)) {
            Catalog.countUses(key.getClass(), key.quantity());
            k = (KeyRecord)records.get(records.indexOf(k));
            k.quantity(k.quantity() - key.quantity());
            if (k.quantity() <= 0) {
                records.remove(k);
            }
            return true;
        }
        return false;
    }

    public static int keyCount(Key key) {
        KeyRecord k = new KeyRecord(key);
        if (records.contains(k)) {
            k = (KeyRecord)records.get(records.indexOf(k));
            return k.quantity();
        }
        return 0;
    }

    public static boolean add(CustomRecord rec) {
        rec.assignID();
        if (!records.contains(rec)) {
            boolean result = records.add(rec);
            Collections.sort(records, comparator);
            return result;
        }
        return false;
    }

    public static boolean remove(CustomRecord rec) {
        if (records.contains(rec)) {
            records.remove(rec);
            return true;
        }
        return false;
    }

    public static <T extends Record> ArrayList<T> getRecords(Class<T> recordType) {
        ArrayList<Record> filtered = new ArrayList<Record>();
        for (Record rec : records) {
            if (!recordType.isInstance(rec)) continue;
            filtered.add(rec);
        }
        return filtered;
    }

    public static ArrayList<Record> getRecords(int depth) {
        ArrayList<Record> filtered = new ArrayList<Record>();
        for (Record rec : records) {
            if (rec.depth() != depth || rec instanceof CustomRecord) continue;
            filtered.add(rec);
        }
        Collections.sort(filtered, comparator);
        return filtered;
    }

    public static CustomRecord findCustomRecord(int ID) {
        for (Record rec : records) {
            if (!(rec instanceof CustomRecord) || ((CustomRecord)rec).ID != ID) continue;
            return (CustomRecord)rec;
        }
        return null;
    }

    public static CustomRecord findCustomRecord(Class itemClass) {
        for (Record rec : records) {
            if (!(rec instanceof CustomRecord) || ((CustomRecord)rec).type != CustomType.ITEM_TYPE || ((CustomRecord)rec).itemClass != itemClass) continue;
            return (CustomRecord)rec;
        }
        return null;
    }

    public static int customRecordLimit() {
        return 5;
    }

    static {
        nextCustomID = 0;
        comparator = new Comparator<Record>(){

            @Override
            public int compare(Record r1, Record r2) {
                return r1.order() - r2.order();
            }
        };
    }

    public static abstract class Record
    implements Comparable<Record>,
    Bundlable {
        protected int depth;
        private static final String DEPTH = "depth";

        public int depth() {
            return this.depth;
        }

        public Image icon() {
            return Icons.STAIRS.get();
        }

        public Visual secondIcon() {
            return null;
        }

        public int quantity() {
            return 1;
        }

        protected abstract int order();

        public abstract String title();

        public abstract String desc();

        public abstract boolean equals(Object var1);

        @Override
        public int compareTo(Record another) {
            return another.depth() - this.depth();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            this.depth = bundle.getInt(DEPTH);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            bundle.put(DEPTH, this.depth);
        }
    }

    public static enum Landmark {
        CHASM_FLOOR,
        WATER_FLOOR,
        GRASS_FLOOR,
        DARK_FLOOR,
        LARGE_FLOOR,
        TRAPS_FLOOR,
        SECRETS_FLOOR,
        SHOP,
        ALCHEMY,
        GARDEN,
        DISTANT_WELL,
        WELL_OF_HEALTH,
        WELL_OF_AWARENESS,
        SACRIFICIAL_FIRE,
        STATUE,
        LOST_PACK,
        BEACON_LOCATION,
        GHOST,
        RAT_KING,
        WANDMAKER,
        TROLL,
        IMP,
        DEMON_SPAWNER;

    }

    public static class LandmarkRecord
    extends Record {
        protected Landmark landmark;
        private static final String LANDMARK = "landmark";

        public LandmarkRecord() {
        }

        public LandmarkRecord(Landmark landmark, int depth) {
            this.landmark = landmark;
            this.depth = depth;
        }

        @Override
        public Image icon() {
            switch (this.landmark.ordinal()) {
                default: {
                    return Icons.STAIRS.get();
                }
                case 0: {
                    return Icons.STAIRS_CHASM.get();
                }
                case 1: {
                    return Icons.STAIRS_WATER.get();
                }
                case 2: {
                    return Icons.STAIRS_GRASS.get();
                }
                case 3: {
                    return Icons.STAIRS_DARK.get();
                }
                case 4: {
                    return Icons.STAIRS_LARGE.get();
                }
                case 5: {
                    return Icons.STAIRS_TRAPS.get();
                }
                case 6: {
                    return Icons.STAIRS_SECRETS.get();
                }
                case 7: {
                    if (this.depth == 20) {
                        return new Image(new ImpSprite());
                    }
                    return new Image(new ShopkeeperSprite());
                }
                case 8: {
                    return Icons.get(Icons.ALCHEMY);
                }
                case 9: {
                    return Icons.get(Icons.GRASS);
                }
                case 10: {
                    return Icons.get(Icons.DISTANT_WELL);
                }
                case 11: {
                    return Icons.get(Icons.WELL_HEALTH);
                }
                case 12: {
                    return Icons.get(Icons.WELL_AWARENESS);
                }
                case 13: {
                    return Icons.get(Icons.SACRIFICE_ALTAR);
                }
                case 14: {
                    return new Image(new StatueSprite());
                }
                case 15: {
                    return Icons.get(Icons.BACKPACK_LRG);
                }
                case 16: {
                    return new ItemSprite(ItemSpriteSheet.RETURN_BEACON);
                }
                case 17: {
                    return new Image(new GhostSprite());
                }
                case 18: {
                    return new Image(new RatKingSprite());
                }
                case 19: {
                    return new Image(new WandmakerSprite());
                }
                case 20: {
                    return new Image(new BlacksmithSprite());
                }
                case 21: {
                    return new Image(new ImpSprite());
                }
                case 22: 
            }
            return new Image(new SpawnerSprite());
        }

        @Override
        public String title() {
            switch (this.landmark.ordinal()) {
                default: {
                    return Messages.get(Landmark.class, this.landmark.name(), new Object[0]);
                }
                case 0: {
                    return Messages.get(Level.Feeling.class, "chasm_title", new Object[0]);
                }
                case 1: {
                    return Messages.get(Level.Feeling.class, "water_title", new Object[0]);
                }
                case 2: {
                    return Messages.get(Level.Feeling.class, "grass_title", new Object[0]);
                }
                case 3: {
                    return Messages.get(Level.Feeling.class, "dark_title", new Object[0]);
                }
                case 4: {
                    return Messages.get(Level.Feeling.class, "large_title", new Object[0]);
                }
                case 5: {
                    return Messages.get(Level.Feeling.class, "traps_title", new Object[0]);
                }
                case 6: {
                    return Messages.get(Level.Feeling.class, "secrets_title", new Object[0]);
                }
                case 15: {
                    return Messages.get(LostBackpack.class, "name", new Object[0]);
                }
                case 16: 
            }
            return Messages.get(BeaconOfReturning.class, "name", new Object[0]);
        }

        @Override
        public String desc() {
            switch (this.landmark.ordinal()) {
                default: {
                    return "";
                }
                case 0: {
                    return Messages.get(Level.Feeling.class, "chasm_desc", new Object[0]);
                }
                case 1: {
                    return Messages.get(Level.Feeling.class, "water_desc", new Object[0]);
                }
                case 2: {
                    return Messages.get(Level.Feeling.class, "grass_desc", new Object[0]);
                }
                case 3: {
                    return Messages.get(Level.Feeling.class, "dark_desc", new Object[0]);
                }
                case 4: {
                    return Messages.get(Level.Feeling.class, "large_desc", new Object[0]);
                }
                case 5: {
                    return Messages.get(Level.Feeling.class, "traps_desc", new Object[0]);
                }
                case 6: {
                    return Messages.get(Level.Feeling.class, "secrets_desc", new Object[0]);
                }
                case 7: {
                    if (this.depth == 20) {
                        return Messages.get(ImpShopkeeper.class, "desc", new Object[0]);
                    }
                    return Messages.get(Shopkeeper.class, "desc", new Object[0]);
                }
                case 8: {
                    return Messages.get(Level.class, "alchemy_desc", new Object[0]);
                }
                case 9: {
                    return Messages.get(Foliage.class, "desc", new Object[0]);
                }
                case 10: {
                    return Messages.get(WeakFloorRoom.HiddenWell.class, "desc", new Object[0]);
                }
                case 11: {
                    return Messages.get(WaterOfHealth.class, "desc", new Object[0]);
                }
                case 12: {
                    return Messages.get(WaterOfAwareness.class, "desc", new Object[0]);
                }
                case 13: {
                    return Messages.get(SacrificialFire.class, "desc", new Object[0]);
                }
                case 14: {
                    return Messages.get(Statue.class, "desc", new Object[0]);
                }
                case 15: {
                    return Messages.get(LostBackpack.class, "desc", new Object[0]);
                }
                case 16: {
                    return Messages.get(BeaconOfReturning.class, "desc", new Object[0]);
                }
                case 17: {
                    return Messages.get(Ghost.class, "desc", new Object[0]);
                }
                case 18: {
                    return new RatKing().description();
                }
                case 19: {
                    return Messages.get(Wandmaker.class, "desc", new Object[0]);
                }
                case 20: {
                    return Messages.get(Blacksmith.class, "desc", new Object[0]);
                }
                case 21: {
                    return Messages.get(Imp.class, "desc", new Object[0]);
                }
                case 22: 
            }
            return Messages.get(DemonSpawner.class, "desc", new Object[0]);
        }

        @Override
        protected int order() {
            return this.landmark.ordinal();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof LandmarkRecord && this.landmark == ((LandmarkRecord)obj).landmark && this.depth() == ((LandmarkRecord)obj).depth();
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.landmark = Landmark.valueOf(bundle.getString(LANDMARK));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(LANDMARK, this.landmark.name());
        }
    }

    public static class KeyRecord
    extends Record {
        protected Key key;
        private static final String KEY = "key";

        public KeyRecord() {
        }

        public KeyRecord(Key key) {
            this.key = key;
        }

        @Override
        public int depth() {
            return this.key.depth;
        }

        @Override
        public Image icon() {
            return new ItemSprite(this.key);
        }

        @Override
        public Visual secondIcon() {
            if (this.quantity() > 1) {
                BitmapText text = new BitmapText(Integer.toString(this.quantity()), PixelScene.pixelFont);
                text.measure();
                return text;
            }
            return null;
        }

        @Override
        public String title() {
            return this.key.title();
        }

        @Override
        public String desc() {
            return this.key.desc();
        }

        public Class<? extends Key> type() {
            return this.key.getClass();
        }

        @Override
        protected int order() {
            return 1000 + Generator.Category.order(this.key);
        }

        @Override
        public int quantity() {
            return this.key.quantity();
        }

        public void quantity(int num) {
            this.key.quantity(num);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof KeyRecord && this.key.isSimilar(((KeyRecord)obj).key);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.key = (Key)bundle.get(KEY);
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(KEY, this.key);
        }
    }

    public static class CustomRecord
    extends Record {
        protected CustomType type;
        protected int ID = -1;
        protected Class itemClass;
        protected String title;
        protected String body;
        private static final String TYPE = "type";
        private static final String ID_NUMBER = "id_number";
        private static final String ITEM_CLASS = "item_class";
        private static final String TITLE = "title";
        private static final String BODY = "body";

        public CustomRecord() {
        }

        public CustomRecord(String title, String desc) {
            this.type = CustomType.TEXT;
            this.title = title;
            this.body = desc;
        }

        public CustomRecord(int depth, String title, String desc) {
            this.type = CustomType.DEPTH;
            this.depth = depth;
            this.title = title;
            this.body = desc;
        }

        public CustomRecord(Class itemCls, String title, String desc) {
            this.type = CustomType.ITEM_TYPE;
            this.itemClass = itemCls;
            this.title = title;
            this.body = desc;
        }

        public CustomRecord(Item item, String title, String desc) {
            this.type = CustomType.SPECIFIC_ITEM;
            this.itemClass = item.getClass();
            this.title = title;
            this.body = desc;
        }

        public void assignID() {
            if (this.ID == -1) {
                this.ID = nextCustomID++;
            }
        }

        public int ID() {
            return this.ID;
        }

        @Override
        public int depth() {
            if (this.type == CustomType.DEPTH) {
                return this.depth;
            }
            return 0;
        }

        @Override
        public Image icon() {
            switch (this.type.ordinal()) {
                default: {
                    return Icons.SCROLL_COLOR.get();
                }
                case 1: {
                    return Icons.STAIRS.get();
                }
                case 2: 
                case 3: 
            }
            Item i = (Item)Reflection.newInstance(this.itemClass);
            return new ItemSprite(i);
        }

        @Override
        public Visual secondIcon() {
            switch (this.type.ordinal()) {
                default: {
                    return null;
                }
                case 1: {
                    BitmapText text = new BitmapText(Integer.toString(this.depth()), PixelScene.pixelFont);
                    text.measure();
                    return text;
                }
                case 2: 
                case 3: 
            }
            Item item = (Item)Reflection.newInstance(this.itemClass);
            if (item.isIdentified() && item.icon != -1) {
                Image secondIcon = new Image("sprites/item_icons.png");
                secondIcon.frame(ItemSpriteSheet.Icons.film.get(item.icon));
                return secondIcon;
            }
            return null;
        }

        @Override
        protected int order() {
            return 2000 + this.ID;
        }

        public void editText(String title, String desc) {
            this.title = title;
            this.body = desc;
        }

        @Override
        public String title() {
            return this.title;
        }

        @Override
        public String desc() {
            return this.body;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof CustomRecord && ((CustomRecord)obj).ID == this.ID;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TYPE, this.type);
            bundle.put(ID_NUMBER, this.ID);
            if (this.itemClass != null) {
                bundle.put(ITEM_CLASS, this.itemClass);
            }
            bundle.put(TITLE, this.title);
            bundle.put(BODY, this.body);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.type = bundle.getEnum(TYPE, CustomType.class);
            this.ID = bundle.getInt(ID_NUMBER);
            if (bundle.contains(ITEM_CLASS)) {
                this.itemClass = bundle.getClass(ITEM_CLASS);
            }
            this.title = bundle.getString(TITLE);
            this.body = bundle.getString(BODY);
        }
    }

    public static enum CustomType {
        TEXT,
        DEPTH,
        ITEM_TYPE,
        SPECIFIC_ITEM;

    }
}

