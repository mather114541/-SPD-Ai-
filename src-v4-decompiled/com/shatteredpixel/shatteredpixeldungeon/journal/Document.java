/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;
import com.watabou.utils.DeviceCompat;
import java.util.Collection;
import java.util.LinkedHashMap;

public enum Document {
    ADVENTURERS_GUIDE(ItemSpriteSheet.GUIDE_PAGE, false),
    ALCHEMY_GUIDE(ItemSpriteSheet.ALCH_PAGE, false),
    INTROS(Icons.STAIRS, true),
    SEWERS_GUARD(ItemSpriteSheet.SEWER_PAGE, true),
    PRISON_WARDEN(ItemSpriteSheet.PRISON_PAGE, true),
    CAVES_EXPLORER(ItemSpriteSheet.CAVES_PAGE, true),
    CITY_WARLOCK(ItemSpriteSheet.CITY_PAGE, true),
    HALLS_KING(ItemSpriteSheet.HALLS_PAGE, true);

    public static final int NOT_FOUND = 0;
    public static final int FOUND = 1;
    public static final int READ = 2;
    private LinkedHashMap<String, Integer> pagesStates = new LinkedHashMap();
    private int pageSprite;
    private Icons pageIcon;
    private boolean loreDocument;
    public static final String GUIDE_INTRO = "Intro";
    public static final String GUIDE_EXAMINING = "Examining";
    public static final String GUIDE_SURPRISE_ATKS = "Surprise_Attacks";
    public static final String GUIDE_IDING = "Identifying";
    public static final String GUIDE_FOOD = "Food";
    public static final String GUIDE_ALCHEMY = "Alchemy";
    public static final String GUIDE_DIEING = "Dieing";
    public static final String GUIDE_SEARCHING = "Searching";
    public static final String KING_ATTRITION = "attrition";
    private static final String DOCUMENTS = "documents";

    private Document(int sprite, boolean lore) {
        this.pageIcon = null;
        this.pageSprite = sprite;
        this.loreDocument = lore;
    }

    private Document(Icons icon, boolean lore) {
        this.pageIcon = icon;
        this.pageSprite = 0;
        this.loreDocument = lore;
    }

    public boolean findPage(String page) {
        if (this.pagesStates.containsKey(page) && this.pagesStates.get(page) == 0) {
            this.pagesStates.put(page, 1);
            Journal.saveNeeded = true;
            Badges.validateCatalogBadges();
            return true;
        }
        return false;
    }

    public boolean findPage(int pageIdx) {
        return this.findPage(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public boolean deletePage(String page) {
        if (this.pagesStates.containsKey(page) && this.pagesStates.get(page) != 0) {
            this.pagesStates.put(page, 0);
            Journal.saveNeeded = true;
            return true;
        }
        return false;
    }

    public boolean deletePage(int pageIdx) {
        return this.deletePage(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public boolean unreadPage(String page) {
        if (this.pagesStates.containsKey(page) && this.pagesStates.get(page) == 2) {
            this.pagesStates.put(page, 1);
            Journal.saveNeeded = true;
            return true;
        }
        return false;
    }

    public boolean unreadPage(int pageIdx) {
        return this.deletePage(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public boolean isPageFound(String page) {
        return this.pagesStates.containsKey(page) && this.pagesStates.get(page) > 0;
    }

    public boolean isPageFound(int pageIdx) {
        return this.isPageFound(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public boolean anyPagesFound() {
        for (Integer val : this.pagesStates.values()) {
            if (val == 0) continue;
            return true;
        }
        return false;
    }

    public boolean allPagesFound() {
        for (Integer val : this.pagesStates.values()) {
            if (val != 0) continue;
            return false;
        }
        return true;
    }

    public boolean readPage(String page) {
        if (this.pagesStates.containsKey(page)) {
            this.pagesStates.put(page, 2);
            Journal.saveNeeded = true;
            Badges.validateCatalogBadges();
            return true;
        }
        return false;
    }

    public boolean readPage(int pageIdx) {
        return this.readPage(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public boolean isPageRead(String page) {
        return this.pagesStates.containsKey(page) && this.pagesStates.get(page) == 2;
    }

    public boolean isPageRead(int pageIdx) {
        return this.isPageRead(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public Collection<String> pageNames() {
        return this.pagesStates.keySet();
    }

    public int pageIdx(String name) {
        int i = 0;
        for (String page : this.pagesStates.keySet()) {
            if (page.equals(name)) {
                return i;
            }
            ++i;
        }
        return -1;
    }

    public Image pageSprite() {
        return this.pageSprite("");
    }

    public Image pageSprite(String page) {
        if (page.isEmpty() || !this.isPageFound(page) || this != ADVENTURERS_GUIDE) {
            if (this.pageIcon != null) {
                return Icons.get(this.pageIcon);
            }
            return new ItemSprite(this.pageSprite);
        }
        switch (page) {
            default: {
                return new ItemSprite(ItemSpriteSheet.MASTERY);
            }
            case "Examining": {
                return Icons.get(Icons.MAGNIFY);
            }
            case "Surprise_Attacks": {
                return Icons.get(Icons.SNAKE);
            }
            case "Identifying": {
                return new ItemSprite(new ScrollOfIdentify());
            }
            case "Food": {
                return new ItemSprite(ItemSpriteSheet.PASTY);
            }
            case "Alchemy": {
                return new ItemSprite(ItemSpriteSheet.TRINKET_CATA);
            }
            case "Dieing": {
                return new ItemSprite(ItemSpriteSheet.TOMB);
            }
            case "Searching": {
                return Icons.get(Icons.MAGNIFY);
            }
            case "Strength": {
                return new ItemSprite(ItemSpriteSheet.GREATAXE);
            }
            case "Upgrades": {
                return new ItemSprite(ItemSpriteSheet.RING_EMERALD);
            }
            case "Looting": {
                return new ItemSprite(ItemSpriteSheet.CRYSTAL_KEY);
            }
            case "Levelling": {
                return Icons.get(Icons.TALENT);
            }
            case "Positioning": {
                return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW);
            }
            case "Magic": 
        }
        return new ItemSprite(ItemSpriteSheet.WAND_FIREBOLT);
    }

    public boolean isLoreDoc() {
        return this.loreDocument;
    }

    public String title() {
        return Messages.get((Object)this, this.name() + ".title", new Object[0]);
    }

    public String discoverHint() {
        return Messages.get((Object)this, this.name() + ".discover_hint", new Object[0]);
    }

    public String pageTitle(String page) {
        return Messages.get((Object)this, this.name() + "." + page + ".title", new Object[0]);
    }

    public String pageTitle(int pageIdx) {
        return this.pageTitle(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public String pageBody(String page) {
        return Messages.get((Object)this, this.name() + "." + page + ".body", new Object[0]);
    }

    public String pageBody(int pageIdx) {
        return this.pageBody(this.pagesStates.keySet().toArray(new String[0])[pageIdx]);
    }

    public static void store(Bundle bundle) {
        Bundle docsBundle = new Bundle();
        for (Document doc : Document.values()) {
            Bundle pagesBundle = new Bundle();
            boolean empty = true;
            for (String page : doc.pageNames()) {
                if (doc.pagesStates.get(page) == 0) continue;
                pagesBundle.put(page, doc.pagesStates.get(page));
                empty = false;
            }
            if (empty) continue;
            docsBundle.put(doc.name(), pagesBundle);
        }
        bundle.put(DOCUMENTS, docsBundle);
    }

    public static void restore(Bundle bundle) {
        if (!bundle.contains(DOCUMENTS)) {
            return;
        }
        Bundle docsBundle = bundle.getBundle(DOCUMENTS);
        for (Document doc : Document.values()) {
            if (!docsBundle.contains(doc.name())) continue;
            Bundle pagesBundle = docsBundle.getBundle(doc.name());
            for (String page : doc.pageNames()) {
                if (!pagesBundle.contains(page)) continue;
                doc.pagesStates.put(page, pagesBundle.getInt(page));
            }
        }
    }

    static {
        boolean debug = DeviceCompat.isDebug();
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_INTRO, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_EXAMINING, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_SURPRISE_ATKS, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_IDING, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_FOOD, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_ALCHEMY, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_DIEING, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put(GUIDE_SEARCHING, debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Strength", debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Upgrades", debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Looting", debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Levelling", debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Positioning", debug ? 2 : 0);
        Document.ADVENTURERS_GUIDE.pagesStates.put("Magic", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Potions", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Stones", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Energy_Food", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Exotic_Potions", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Exotic_Scrolls", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Bombs", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Weapons", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Brews_Elixirs", debug ? 2 : 0);
        Document.ALCHEMY_GUIDE.pagesStates.put("Spells", debug ? 2 : 0);
        Document.INTROS.pagesStates.put("Dungeon", 2);
        Document.INTROS.pagesStates.put("Sewers", debug ? 2 : 0);
        Document.INTROS.pagesStates.put("Prison", debug ? 2 : 0);
        Document.INTROS.pagesStates.put("Caves", debug ? 2 : 0);
        Document.INTROS.pagesStates.put("City", debug ? 2 : 0);
        Document.INTROS.pagesStates.put("Halls", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("new_position", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("dangerous", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("crabs", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("guild", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("lost", debug ? 2 : 0);
        Document.SEWERS_GUARD.pagesStates.put("not_worth", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("journal", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("recruits", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("mines", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("rotberry", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("no_support", debug ? 2 : 0);
        Document.PRISON_WARDEN.pagesStates.put("letter", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("expedition", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("gold", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("troll", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("city", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("alive", debug ? 2 : 0);
        Document.CAVES_EXPLORER.pagesStates.put("report", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("old_king", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("resistance", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("failure", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("more_powerful", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("new_power", debug ? 2 : 0);
        Document.CITY_WARLOCK.pagesStates.put("seen_it", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put("Rejection", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put("amulet", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put("ritual", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put("new_king", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put("thing", debug ? 2 : 0);
        Document.HALLS_KING.pagesStates.put(KING_ATTRITION, debug ? 0 : 0);
    }
}

