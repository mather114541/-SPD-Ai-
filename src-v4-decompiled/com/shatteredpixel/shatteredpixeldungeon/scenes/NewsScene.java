/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.news.NewsArticle;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;
import java.util.ArrayList;

public class NewsScene
extends PixelScene {
    boolean displayingNoArticles = false;
    private static final int BTN_HEIGHT = 22;
    private static final int BTN_WIDTH = 100;

    @Override
    public void create() {
        super.create();
        NewsScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        int fullWidth = PixelScene.landscape() ? 202 : 100;
        int left = (int)insets.left + (w - fullWidth) / 2;
        ExitButton btnExit = new ExitButton();
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        IconTitle title = new IconTitle(Icons.NEWS.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        NewsScene.align(title);
        this.add(title);
        float top = 18.0f + insets.top;
        boolean bl = this.displayingNoArticles = !News.articlesAvailable();
        if (this.displayingNoArticles || Messages.lang() != Languages.ENGLISH) {
            NewsInfo newsInfo = new NewsInfo();
            newsInfo.setRect(left, top, fullWidth, 0.0f);
            this.add(newsInfo);
            top = newsInfo.bottom();
        }
        if (!this.displayingNoArticles) {
            ArrayList<NewsArticle> articles = News.articles();
            float articleSpace = (float)h - top - 2.0f + insets.top;
            int rows = articles.size();
            if (PixelScene.landscape()) {
                rows /= 2;
            }
            ++rows;
            while (articleSpace / 22.5f < (float)rows) {
                articles.remove(articles.size() - 1);
                if (PixelScene.landscape()) {
                    articles.remove(articles.size() - 1);
                }
                --rows;
            }
            float gap = (articleSpace - (float)(22 * rows)) / (float)rows;
            boolean rightCol = false;
            for (NewsArticle article : articles) {
                ArticleButton b = new ArticleButton(article);
                b.multiline = true;
                if (!rightCol) {
                    b.setRect(left, top += gap, 100.0f, 22.0f);
                } else {
                    b.setRect(left + fullWidth - 100, top, 100.0f, 22.0f);
                }
                NewsScene.align(b);
                this.add(b);
                if (!PixelScene.landscape()) {
                    top += 22.0f;
                    continue;
                }
                if (rightCol) {
                    top += 22.0f;
                }
                rightCol = !rightCol;
            }
            top += gap;
        } else {
            top += 20.0f;
        }
        StyledButton btnSite = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "read_more", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                ShatteredPixelDungeon.platform.openURI("https://ShatteredPixel.com");
            }
        };
        btnSite.icon(Icons.get(Icons.NEWS));
        btnSite.textColor(0xFFFF44);
        btnSite.setRect(left, top, fullWidth, 22.0f);
        this.add(btnSite);
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    @Override
    public void update() {
        if (this.displayingNoArticles && News.articlesAvailable()) {
            ShatteredPixelDungeon.seamlessResetScene();
        }
        super.update();
    }

    private static class NewsInfo
    extends Component {
        NinePatch bg;
        RenderedTextBlock text;
        RedButton button;

        private NewsInfo() {
        }

        @Override
        protected void createChildren() {
            this.bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
            this.add(this.bg);
            Object message = "";
            if (Messages.lang() != Languages.ENGLISH) {
                message = (String)message + Messages.get(this, "english_warn", new Object[0]);
            }
            if (!News.articlesAvailable()) {
                if (SPDSettings.news()) {
                    if (SPDSettings.WiFi() && !Game.platform.connectedToUnmeteredNetwork()) {
                        message = (String)message + "\n\n" + Messages.get(this, "metered_network", new Object[0]);
                        this.button = new RedButton(Messages.get(this, "enable_data", new Object[0])){

                            @Override
                            protected void onClick() {
                                super.onClick();
                                SPDSettings.WiFi(false);
                                News.checkForNews();
                                ShatteredPixelDungeon.seamlessResetScene();
                            }
                        };
                        this.add(this.button);
                    } else {
                        message = (String)message + "\n\n" + Messages.get(this, "no_internet", new Object[0]);
                    }
                } else {
                    message = (String)message + "\n\n" + Messages.get(this, "news_disabled", new Object[0]);
                    this.button = new RedButton(Messages.get(this, "enable_news", new Object[0])){

                        @Override
                        protected void onClick() {
                            super.onClick();
                            SPDSettings.news(true);
                            News.checkForNews();
                            ShatteredPixelDungeon.seamlessResetScene();
                        }
                    };
                    this.add(this.button);
                }
            }
            if (((String)message).startsWith("\n\n")) {
                message = ((String)message).replaceFirst("\n\n", "");
            }
            this.text = PixelScene.renderTextBlock((String)message, 6);
            this.text.hardlight(0xFF8800);
            this.add(this.text);
        }

        @Override
        protected void layout() {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.text.maxWidth((int)this.width - this.bg.marginHor());
            this.text.setPos(this.x + (float)this.bg.marginLeft(), this.y + (float)this.bg.marginTop() + 1.0f);
            this.height = this.text.bottom() - this.y;
            if (this.button != null) {
                this.height += 4.0f;
                this.button.multiline = true;
                this.button.setSize(this.width - (float)this.bg.marginHor(), 16.0f);
                this.button.setSize(this.width - (float)this.bg.marginHor(), Math.max(this.button.reqHeight(), 16.0f));
                this.button.setPos(this.x + (this.width - this.button.width()) / 2.0f, this.y + this.height);
                this.height = this.button.bottom() - this.y;
            }
            this.height += (float)(this.bg.marginBottom() + 1);
            this.bg.size(this.width, this.height);
        }
    }

    private static class ArticleButton
    extends StyledButton {
        NewsArticle article;
        BitmapText date;

        public ArticleButton(NewsArticle article) {
            super(Chrome.Type.GREY_BUTTON_TR, article.title, 6);
            this.article = article;
            this.icon(News.parseArticleIcon(article));
            long lastRead = SPDSettings.newsLastRead();
            if (lastRead > 0L && article.date.getTime() > lastRead) {
                this.textColor(0x33BB33);
            }
            this.date = new BitmapText(News.parseArticleDate(article), PixelScene.pixelFont);
            this.date.scale.set(PixelScene.align(0.5f));
            this.date.hardlight(0x888888);
            this.date.measure();
            this.add(this.date);
        }

        @Override
        protected void layout() {
            super.layout();
            this.icon.x = this.x + (float)this.bg.marginLeft() + (16.0f - this.icon.width()) / 2.0f;
            PixelScene.align(this.icon);
            this.text.setPos(this.x + (float)this.bg.marginLeft() + 18.0f, this.text.top());
            if (this.date != null) {
                this.date.x = this.x + this.width - (float)this.bg.marginRight() - this.date.width() + 1.0f;
                this.date.y = this.y + this.height - (float)this.bg.marginBottom() - this.date.height() + 2.5f;
                PixelScene.align(this.date);
            }
        }

        @Override
        protected void onClick() {
            super.onClick();
            this.textColor(0xFFFFFF);
            if (this.article.date.getTime() > SPDSettings.newsLastRead()) {
                SPDSettings.newsLastRead(this.article.date.getTime());
            }
            ShatteredPixelDungeon.scene().addToFront(new WndArticle(this.article));
        }
    }

    private static class WndArticle
    extends WndTitledMessage {
        public WndArticle(final NewsArticle article) {
            super(News.parseArticleIcon(article), article.title, article.summary);
            RedButton link = new RedButton(Messages.get(NewsScene.class, "read_more", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.platform.openURI(article.URL);
                }
            };
            link.setRect(0.0f, this.height + 2, this.width, 22.0f);
            this.add(link);
            this.resize(this.width, (int)link.bottom());
        }
    }
}

