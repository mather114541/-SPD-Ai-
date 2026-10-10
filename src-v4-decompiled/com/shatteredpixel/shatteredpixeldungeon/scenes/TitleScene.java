/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AboutScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.JournalScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.NewsScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SupporterScene;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.AvailableUpdateData;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndSettings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.glwrap.Blending;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.utils.ColorMath;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.RectF;
import java.util.Date;

public class TitleScene
extends PixelScene {
    private Image title;
    private Fireball leftFB;
    private Fireball rightFB;
    private Image signs;
    private StyledButton btnPlay;
    private StyledButton btnSupport;
    private StyledButton btnRankings;
    private StyledButton btnJournal;
    private StyledButton btnNews;
    private StyledButton btnChanges;
    private StyledButton btnSettings;
    private StyledButton btnAbout;
    private BitmapText version;
    private IconButton btnFade;
    private ExitButton btnExit;
    private float uiAlpha;

    @Override
    public void create() {
        super.create();
        Music.INSTANCE.playTracks(new String[]{"music/theme_1.ogg", "music/theme_2.ogg"}, new float[]{1.0f, 1.0f}, false);
        TitleScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        this.title = BannerSprites.get(TitleScene.landscape() ? BannerSprites.Type.TITLE_LAND : BannerSprites.Type.TITLE_PORT);
        this.add(this.title);
        float topRegion = Math.max(this.title.height - 6.0f, (float)h * 0.45f);
        this.title.x = insets.left + ((float)w - this.title.width()) / 2.0f;
        this.title.y = insets.top + 2.0f + (topRegion - this.title.height()) / 2.0f;
        TitleScene.align(this.title);
        if (TitleScene.landscape()) {
            this.leftFB = this.placeTorch(this.title.x + 30.0f, this.title.y + 35.0f);
            this.rightFB = this.placeTorch(this.title.x + this.title.width - 30.0f, this.title.y + 35.0f);
        } else {
            this.leftFB = this.placeTorch(this.title.x + 16.0f, this.title.y + 70.0f);
            this.rightFB = this.placeTorch(this.title.x + this.title.width - 16.0f, this.title.y + 70.0f);
        }
        this.signs = new Image(BannerSprites.get(TitleScene.landscape() ? BannerSprites.Type.TITLE_GLOW_LAND : BannerSprites.Type.TITLE_GLOW_PORT)){
            private float time;
            {
                this.time = 0.0f;
            }

            @Override
            public void update() {
                super.update();
                this.am = Math.max(0.0f, (float)Math.sin(this.time += Game.elapsed));
                this.am = Math.min(this.am, TitleScene.this.title.am);
                if ((double)this.time >= 4.71238898038469) {
                    this.time = 0.0f;
                }
            }

            @Override
            public void draw() {
                Blending.setLightMode();
                super.draw();
                Blending.setNormalMode();
            }
        };
        this.signs.x = this.title.x + (this.title.width() - this.signs.width()) / 2.0f;
        this.signs.y = this.title.y;
        this.add(this.signs);
        Chrome.Type GREY_TR = Chrome.Type.GREY_BUTTON_TR;
        this.btnPlay = new StyledButton(GREY_TR, Messages.get(this, "enter", new Object[0])){

            @Override
            protected void onClick() {
                if (GamesInProgress.checkAll().size() == 0) {
                    GamesInProgress.selectedClass = null;
                    GamesInProgress.curSlot = 1;
                    ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                } else {
                    ShatteredPixelDungeon.switchNoFade(StartScene.class);
                }
            }

            @Override
            protected boolean onLongClick() {
                if (DeviceCompat.isDebug()) {
                    GamesInProgress.selectedClass = null;
                    GamesInProgress.curSlot = 1;
                    ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                    return true;
                }
                return super.onLongClick();
            }
        };
        this.btnPlay.icon(Icons.get(Icons.ENTER));
        this.add(this.btnPlay);
        this.btnSupport = new SupportButton(GREY_TR, Messages.get(this, "support", new Object[0]));
        this.add(this.btnSupport);
        this.btnRankings = new StyledButton(GREY_TR, Messages.get(this, "rankings", new Object[0])){

            @Override
            protected void onClick() {
                ShatteredPixelDungeon.switchNoFade(RankingsScene.class);
            }
        };
        this.btnRankings.icon(Icons.get(Icons.RANKINGS));
        this.add(this.btnRankings);
        Dungeon.dailyReplay = false;
        Dungeon.daily = false;
        this.btnJournal = new StyledButton(GREY_TR, Messages.get(this, "journal", new Object[0])){

            @Override
            protected void onClick() {
                ShatteredPixelDungeon.switchNoFade(JournalScene.class);
            }
        };
        this.btnJournal.icon(Icons.get(Icons.JOURNAL));
        this.add(this.btnJournal);
        this.btnNews = new NewsButton(GREY_TR, Messages.get(this, "news", new Object[0]));
        this.btnNews.icon(Icons.get(Icons.NEWS));
        this.add(this.btnNews);
        this.btnChanges = new ChangesButton(GREY_TR, Messages.get(this, "changes", new Object[0]));
        this.btnChanges.icon(Icons.get(Icons.CHANGES));
        this.add(this.btnChanges);
        this.btnSettings = new SettingsButton(GREY_TR, Messages.get(this, "settings", new Object[0]));
        this.add(this.btnSettings);
        this.btnAbout = new StyledButton(GREY_TR, Messages.get(this, "about", new Object[0])){

            @Override
            protected void onClick() {
                ShatteredPixelDungeon.switchScene(AboutScene.class);
            }
        };
        this.btnAbout.icon(Icons.get(Icons.SHPX));
        this.add(this.btnAbout);
        int BTN_HEIGHT = 20;
        int GAP = (int)((float)h - topRegion - (float)((TitleScene.landscape() ? 3 : 4) * 20)) / 3;
        GAP /= TitleScene.landscape() ? 3 : 5;
        GAP = Math.max(GAP, 2);
        float buttonAreaWidth = TitleScene.landscape() ? 234.0f : 133.0f;
        float btnAreaLeft = insets.left + ((float)w - buttonAreaWidth) / 2.0f;
        if (TitleScene.landscape()) {
            this.btnPlay.setRect(btnAreaLeft, insets.top + topRegion + (float)GAP, buttonAreaWidth / 2.0f - 1.0f, 20.0f);
            TitleScene.align(this.btnPlay);
            this.btnSupport.setRect(this.btnPlay.right() + 2.0f, this.btnPlay.top(), this.btnPlay.width(), 20.0f);
            this.btnRankings.setRect(this.btnPlay.left(), this.btnPlay.bottom() + (float)GAP, (float)(Math.floor(buttonAreaWidth / 3.0f) - 1.0), 20.0f);
            this.btnJournal.setRect(this.btnRankings.right() + 2.0f, this.btnRankings.top(), this.btnRankings.width(), 20.0f);
            this.btnNews.setRect(this.btnJournal.right() + 2.0f, this.btnJournal.top(), this.btnRankings.width(), 20.0f);
            this.btnSettings.setRect(this.btnRankings.left(), this.btnRankings.bottom() + (float)GAP, this.btnRankings.width(), 20.0f);
            this.btnChanges.setRect(this.btnSettings.right() + 2.0f, this.btnSettings.top(), this.btnRankings.width(), 20.0f);
            this.btnAbout.setRect(this.btnChanges.right() + 2.0f, this.btnSettings.top(), this.btnRankings.width(), 20.0f);
        } else {
            this.btnPlay.setRect(btnAreaLeft, insets.top + topRegion + (float)GAP, buttonAreaWidth, 20.0f);
            TitleScene.align(this.btnPlay);
            this.btnSupport.setRect(this.btnPlay.left(), this.btnPlay.bottom() + (float)GAP, this.btnPlay.width(), 20.0f);
            this.btnRankings.setRect(this.btnPlay.left(), this.btnSupport.bottom() + (float)GAP, this.btnPlay.width() / 2.0f - 1.0f, 20.0f);
            this.btnJournal.setRect(this.btnRankings.right() + 2.0f, this.btnRankings.top(), this.btnRankings.width(), 20.0f);
            this.btnNews.setRect(this.btnRankings.left(), this.btnRankings.bottom() + (float)GAP, this.btnRankings.width(), 20.0f);
            this.btnChanges.setRect(this.btnNews.right() + 2.0f, this.btnNews.top(), this.btnNews.width(), 20.0f);
            this.btnSettings.setRect(this.btnNews.left(), this.btnNews.bottom() + (float)GAP, this.btnRankings.width(), 20.0f);
            this.btnAbout.setRect(this.btnSettings.right() + 2.0f, this.btnSettings.top(), this.btnSettings.width(), 20.0f);
        }
        this.version = new BitmapText("v" + Game.version, pixelFont);
        this.version.measure();
        this.version.hardlight(0x888888);
        this.version.x = insets.left + (float)w - this.version.width() - (float)(DeviceCompat.isDesktop() ? 4 : 8);
        this.version.y = insets.top + (float)h - this.version.height() - (float)(DeviceCompat.isDesktop() ? 2 : 4);
        this.add(this.version);
        this.btnFade = new IconButton(Icons.CHEVRON.get()){

            @Override
            protected void onClick() {
                this.enable(false);
                this.parent.add(new Tweener(this.parent, 0.5f){

                    @Override
                    protected void updateValues(float progress) {
                        if (!TitleScene.this.btnFade.active) {
                            TitleScene.this.uiAlpha = 1.0f - progress;
                            TitleScene.this.updateFade();
                        }
                    }
                });
            }
        };
        this.btnFade.icon().originToCenter();
        this.btnFade.icon().angle = 180.0f;
        this.btnFade.setRect(btnAreaLeft + (buttonAreaWidth - 16.0f) / 2.0f, (float)(Camera.main.height - 16) - insets.bottom, 16.0f, 16.0f);
        this.add(this.btnFade);
        PointerArea fadeResetter = new PointerArea(0.0f, 0.0f, Camera.main.width, Camera.main.height){

            @Override
            public boolean onSignal(PointerEvent event) {
                if (event != null && event.type == PointerEvent.Type.UP && !TitleScene.this.btnPlay.active) {
                    this.parent.add(new Tweener(this.parent, 0.5f){

                        @Override
                        protected void updateValues(float progress) {
                            TitleScene.this.uiAlpha = progress;
                            TitleScene.this.updateFade();
                            if (progress >= 1.0f) {
                                TitleScene.this.btnFade.enable(true);
                            }
                        }
                    });
                }
                return false;
            }
        };
        this.add(fadeResetter);
        if (DeviceCompat.isDesktop()) {
            this.btnExit = new ExitButton();
            this.btnExit.setPos((float)w - this.btnExit.width(), 0.0f);
            this.add(this.btnExit);
        }
        Badges.loadGlobal();
        if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
            SPDSettings.victoryNagged(true);
            this.add(new WndVictoryCongrats());
        }
        this.fadeIn();
    }

    public void updateFade() {
        float alpha;
        this.title.am = alpha = GameMath.gate(0.0f, this.uiAlpha, 1.0f);
        this.leftFB.am = alpha;
        this.rightFB.am = alpha;
        this.btnPlay.enable(alpha != 0.0f);
        this.btnSupport.enable(alpha != 0.0f);
        this.btnRankings.enable(alpha != 0.0f);
        this.btnJournal.enable(alpha != 0.0f);
        this.btnNews.enable(alpha != 0.0f);
        this.btnChanges.enable(alpha != 0.0f);
        this.btnSettings.enable(alpha != 0.0f);
        this.btnAbout.enable(alpha != 0.0f);
        this.btnPlay.alpha(alpha);
        this.btnSupport.alpha(alpha);
        this.btnRankings.alpha(alpha);
        this.btnJournal.alpha(alpha);
        this.btnNews.alpha(alpha);
        this.btnChanges.alpha(alpha);
        this.btnSettings.alpha(alpha);
        this.btnAbout.alpha(alpha);
        this.version.alpha(alpha);
        this.btnFade.icon().alpha(alpha);
        if (this.btnExit != null) {
            this.btnExit.enable(alpha != 0.0f);
            this.btnExit.icon().alpha(alpha);
        }
    }

    private Fireball placeTorch(float x, float y) {
        Fireball fb = new Fireball();
        fb.x = x - fb.width() / 2.0f;
        fb.y = y - fb.height();
        TitleScene.align(fb);
        this.add(fb);
        return fb;
    }

    private static class SupportButton
    extends StyledButton {
        public SupportButton(Chrome.Type type, String label) {
            super(type, label);
            this.icon(Icons.get(Icons.GOLD));
            this.textColor(0xFFFF44);
        }

        @Override
        protected void onClick() {
            ShatteredPixelDungeon.switchNoFade(SupporterScene.class);
        }
    }

    private static class NewsButton
    extends StyledButton {
        int unreadCount = -1;

        public NewsButton(Chrome.Type type, String label) {
            super(type, label);
            if (SPDSettings.news()) {
                News.checkForNews();
            }
        }

        @Override
        public void update() {
            super.update();
            if (this.unreadCount == -1 && News.articlesAvailable()) {
                long lastRead = SPDSettings.newsLastRead();
                if (lastRead == 0L) {
                    if (News.articles().get(0) != null) {
                        SPDSettings.newsLastRead(News.articles().get((int)0).date.getTime());
                    }
                } else {
                    this.unreadCount = News.unreadArticles(new Date(SPDSettings.newsLastRead()));
                    if (this.unreadCount > 0) {
                        this.unreadCount = Math.min(this.unreadCount, 9);
                        this.text(this.text() + "(" + this.unreadCount + ")");
                    }
                }
            }
            if (this.unreadCount > 0) {
                this.textColor(ColorMath.interpolate(0xFFFFFF, 0x33BB33, 0.5f + (float)Math.sin(Game.timeTotal * 5.0f) / 2.0f));
            }
        }

        @Override
        protected void onClick() {
            super.onClick();
            ShatteredPixelDungeon.switchNoFade(NewsScene.class);
        }
    }

    private static class ChangesButton
    extends StyledButton {
        boolean updateShown = false;

        public ChangesButton(Chrome.Type type, String label) {
            super(type, label);
            if (SPDSettings.updates()) {
                Updates.checkForUpdate();
            }
        }

        @Override
        public void update() {
            super.update();
            if (!this.updateShown && Updates.updateAvailable()) {
                this.updateShown = true;
                this.text(Messages.get(TitleScene.class, "update", new Object[0]));
            }
            if (this.updateShown) {
                this.textColor(ColorMath.interpolate(0xFFFFFF, 0x33BB33, 0.5f + (float)Math.sin(Game.timeTotal * 5.0f) / 2.0f));
            }
        }

        @Override
        protected void onClick() {
            if (Updates.updateAvailable()) {
                AvailableUpdateData update = Updates.updateData();
                ShatteredPixelDungeon.scene().addToFront(new WndOptions(Icons.get(Icons.CHANGES), update.versionName == null ? Messages.get(this, "title", new Object[0]) : Messages.get(this, "versioned_title", update.versionName), update.desc == null ? Messages.get(this, "desc", new Object[0]) : update.desc, new String[]{Messages.get(this, "update", new Object[0]), Messages.get(this, "changes", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            Updates.launchUpdate(Updates.updateData());
                        } else if (index == 1) {
                            ChangesScene.changesSelected = 0;
                            ShatteredPixelDungeon.switchNoFade(ChangesScene.class);
                        }
                    }
                });
            } else {
                ChangesScene.changesSelected = 0;
                ShatteredPixelDungeon.switchNoFade(ChangesScene.class);
            }
        }
    }

    private static class SettingsButton
    extends StyledButton {
        public SettingsButton(Chrome.Type type, String label) {
            super(type, label);
            if (Messages.lang().status() == Languages.Status.X_UNFINISH) {
                this.icon(Icons.get(Icons.LANGS));
                this.icon.hardlight(1.5f, 0.0f, 0.0f);
            } else {
                this.icon(Icons.get(Icons.PREFS));
            }
        }

        @Override
        public void update() {
            super.update();
            if (Messages.lang().status() == Languages.Status.X_UNFINISH) {
                this.textColor(ColorMath.interpolate(0xFFFFFF, 0xFF0000, 0.5f + (float)Math.sin(Game.timeTotal * 5.0f) / 2.0f));
            }
        }

        @Override
        protected void onClick() {
            if (Messages.lang().status() == Languages.Status.X_UNFINISH) {
                WndSettings.last_index = 5;
            }
            ShatteredPixelDungeon.scene().add(new WndSettings());
        }
    }
}

