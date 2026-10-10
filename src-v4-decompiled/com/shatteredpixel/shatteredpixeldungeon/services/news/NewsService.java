/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.news;

import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsArticle;
import java.util.ArrayList;

public abstract class NewsService {
    public abstract void checkForArticles(boolean var1, boolean var2, NewsResultCallback var3);

    public static abstract class NewsResultCallback {
        public abstract void onArticlesFound(ArrayList<NewsArticle> var1);

        public abstract void onConnectionFailed();
    }
}

