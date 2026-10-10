/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.journal;

import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public class RegionLorePage {
    public static DocumentPage pageForDoc(Document doc) {
        switch (doc) {
            default: {
                return new Sewers();
            }
            case PRISON_WARDEN: {
                return new Prison();
            }
            case CAVES_EXPLORER: {
                return new Caves();
            }
            case CITY_WARLOCK: {
                return new City();
            }
            case HALLS_KING: 
        }
        return new Halls();
    }

    public static class Sewers
    extends DocumentPage {
        public Sewers() {
            this.image = ItemSpriteSheet.SEWER_PAGE;
        }

        @Override
        public Document document() {
            return Document.SEWERS_GUARD;
        }
    }

    public static class Prison
    extends DocumentPage {
        public Prison() {
            this.image = ItemSpriteSheet.PRISON_PAGE;
        }

        @Override
        public Document document() {
            return Document.PRISON_WARDEN;
        }
    }

    public static class Caves
    extends DocumentPage {
        public Caves() {
            this.image = ItemSpriteSheet.CAVES_PAGE;
        }

        @Override
        public Document document() {
            return Document.CAVES_EXPLORER;
        }
    }

    public static class City
    extends DocumentPage {
        public City() {
            this.image = ItemSpriteSheet.CITY_PAGE;
        }

        @Override
        public Document document() {
            return Document.CITY_WARLOCK;
        }
    }

    public static class Halls
    extends DocumentPage {
        public Halls() {
            this.image = ItemSpriteSheet.HALLS_PAGE;
        }

        @Override
        public Document document() {
            return Document.HALLS_KING;
        }
    }
}

