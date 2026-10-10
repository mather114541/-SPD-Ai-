/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.services.news;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsArticle;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsService;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.Image;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class News {
    public static NewsService service;
    private static Date lastCheck;
    private static final long CHECK_DELAY = 3600000L;
    private static ArrayList<NewsArticle> articles;

    public static boolean supportsNews() {
        return service != null;
    }

    public static void checkForNews() {
        if (!News.supportsNews()) {
            return;
        }
        if (lastCheck != null && new Date().getTime() - lastCheck.getTime() < 3600000L) {
            return;
        }
        boolean useHTTPS = true;
        if (Gdx.app.getType() == Application.ApplicationType.Android && Gdx.app.getVersion() < 20) {
            useHTTPS = false;
        }
        service.checkForArticles(!SPDSettings.WiFi(), useHTTPS, new NewsService.NewsResultCallback(){

            @Override
            public void onArticlesFound(ArrayList<NewsArticle> articles) {
                lastCheck = new Date();
                News.articles = articles;
            }

            @Override
            public void onConnectionFailed() {
                lastCheck = null;
                articles = null;
            }
        });
    }

    public static synchronized boolean articlesAvailable() {
        return articles != null && !articles.isEmpty();
    }

    public static synchronized ArrayList<NewsArticle> articles() {
        return new ArrayList<NewsArticle>(articles);
    }

    public static synchronized int unreadArticles(Date lastRead) {
        int unread = 0;
        if (articles != null) {
            for (NewsArticle article : articles) {
                if (!article.date.after(lastRead)) continue;
                ++unread;
            }
        }
        return unread;
    }

    public static synchronized void clearArticles() {
        articles = null;
        lastCheck = null;
    }

    public static Image parseArticleIcon(NewsArticle article) {
        try {
            if (article.icon.startsWith("ICON: ")) {
                return Icons.get(Icons.valueOf(article.icon.replace("ICON: ", "")));
            }
            if (article.icon.startsWith("ITEM: ")) {
                return new ItemSprite(Integer.parseInt(article.icon.replace("ITEM: ", "")));
            }
            String[] split = article.icon.split(", ");
            return new Image(split[0], Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]), Integer.parseInt(split[4]));
        }
        catch (Exception e) {
            if (article.icon != null) {
                ShatteredPixelDungeon.reportException(e);
            }
            return Icons.get(Icons.NEWS);
        }
    }

    public static String parseArticleDate(NewsArticle article) {
        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(article.date);
        return cal.get(1) + "-" + String.format("%02d", cal.get(2) + 1) + "-" + String.format("%02d", cal.get(5));
    }

    static {
        lastCheck = null;
    }
}

