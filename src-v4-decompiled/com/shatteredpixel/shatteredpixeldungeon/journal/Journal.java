/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.journal;

import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.watabou.utils.Bundle;
import com.watabou.utils.FileUtils;
import java.io.IOException;

public class Journal {
    public static final String JOURNAL_FILE = "journal.dat";
    private static boolean loaded = false;
    static boolean saveNeeded = false;

    public static void loadGlobal() {
        Bundle bundle;
        if (loaded) {
            return;
        }
        try {
            bundle = FileUtils.bundleFromFile(JOURNAL_FILE);
        }
        catch (IOException e) {
            bundle = new Bundle();
        }
        Catalog.restore(bundle);
        Bestiary.restore(bundle);
        Document.restore(bundle);
        loaded = true;
    }

    public static void saveGlobal() {
        Journal.saveGlobal(false);
    }

    public static void saveGlobal(boolean force) {
        if (!force && !saveNeeded) {
            return;
        }
        Bundle bundle = new Bundle();
        Catalog.store(bundle);
        Bestiary.store(bundle);
        Document.store(bundle);
        try {
            FileUtils.bundleToFile(JOURNAL_FILE, bundle);
            saveNeeded = false;
        }
        catch (IOException e) {
            ShatteredPixelDungeon.reportException(e);
        }
    }
}

