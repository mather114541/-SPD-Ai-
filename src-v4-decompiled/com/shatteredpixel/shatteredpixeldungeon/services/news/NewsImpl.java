/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.news;

import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsService;
import com.shatteredpixel.shatteredpixeldungeon.services.news.ShatteredNews;

public class NewsImpl {
    private static NewsService newsChecker = new ShatteredNews();

    public static NewsService getNewsService() {
        return newsChecker;
    }

    public static boolean supportsNews() {
        return true;
    }
}

