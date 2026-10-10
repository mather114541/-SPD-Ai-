/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BannerSprites;
import com.shatteredpixel.shatteredpixeldungeon.effects.Fireball;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.ChangesScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndError;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHardNotification;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.FileUtils;
import com.watabou.utils.RectF;
import java.util.Collections;

public class WelcomeScene
extends PixelScene {
    private static final int LATEST_UPDATE = 909;
    private static boolean triedCleaningTemp = false;

    @Override
    public void create() {
        Object message;
        super.create();
        final int previousVersion = SPDSettings.version();
        if (!triedCleaningTemp && FileUtils.cleanTempFiles()) {
            this.add(new WndHardNotification(Icons.get(Icons.WARNING), Messages.get(WndError.class, "title", new Object[0]), Messages.get(this, "save_warning", new Object[0]), Messages.get(this, "continue", new Object[0]), 5){

                @Override
                public void hide() {
                    super.hide();
                    triedCleaningTemp = true;
                    ShatteredPixelDungeon.resetScene();
                }
            });
            return;
        }
        if (ShatteredPixelDungeon.versionCode == previousVersion && !SPDSettings.intro()) {
            ShatteredPixelDungeon.switchNoFade(TitleScene.class);
            return;
        }
        Music.INSTANCE.playTracks(new String[]{"music/theme_1.ogg", "music/theme_2.ogg"}, new float[]{1.0f, 1.0f}, false);
        WelcomeScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        this.add(new ColorBlock(w, h, 0x44000000));
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        Image title = BannerSprites.get(WelcomeScene.landscape() ? BannerSprites.Type.TITLE_LAND : BannerSprites.Type.TITLE_PORT);
        this.add(title);
        float topRegion = Math.max(title.height - 6.0f, (float)h * 0.45f);
        title.x = insets.left + ((float)w - title.width()) / 2.0f;
        title.y = insets.top + 2.0f + (topRegion - title.height()) / 2.0f;
        WelcomeScene.align(title);
        if (WelcomeScene.landscape()) {
            this.placeTorch(title.x + 30.0f, title.y + 35.0f);
            this.placeTorch(title.x + title.width - 30.0f, title.y + 35.0f);
        } else {
            this.placeTorch(title.x + 16.0f, title.y + 70.0f);
            this.placeTorch(title.x + title.width - 16.0f, title.y + 70.0f);
        }
        Image signs = new Image(BannerSprites.get(WelcomeScene.landscape() ? BannerSprites.Type.TITLE_GLOW_LAND : BannerSprites.Type.TITLE_GLOW_PORT)){
            private float time;
            {
                this.time = 0.0f;
            }

            @Override
            public void update() {
                super.update();
                this.am = Math.max(0.0f, (float)Math.sin(this.time += Game.elapsed));
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
        signs.x = title.x + (title.width() - signs.width()) / 2.0f;
        signs.y = title.y;
        this.add(signs);
        StyledButton okay = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "continue", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                if (previousVersion == 0 || SPDSettings.intro()) {
                    if (previousVersion > 0) {
                        WelcomeScene.this.updateVersion(previousVersion);
                    }
                    SPDSettings.version(ShatteredPixelDungeon.versionCode);
                    GamesInProgress.selectedClass = null;
                    GamesInProgress.curSlot = GamesInProgress.firstEmpty();
                    if (GamesInProgress.curSlot == -1 || Rankings.INSTANCE.totalNumber > 0) {
                        SPDSettings.intro(false);
                        ShatteredPixelDungeon.switchScene(TitleScene.class);
                    } else {
                        ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
                    }
                } else {
                    WelcomeScene.this.updateVersion(previousVersion);
                    ShatteredPixelDungeon.switchScene(TitleScene.class);
                }
            }
        };
        float buttonY = insets.top + Math.min(topRegion + (float)(PixelScene.landscape() ? 60 : 120), (float)(h - 24));
        float buttonAreaWidth = WelcomeScene.landscape() ? 234.0f : 133.0f;
        float btnAreaLeft = insets.left + ((float)w - buttonAreaWidth) / 2.0f;
        if (previousVersion != 0 && !SPDSettings.intro()) {
            StyledButton changes = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(TitleScene.class, "changes", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    WelcomeScene.this.updateVersion(previousVersion);
                    ShatteredPixelDungeon.switchScene(ChangesScene.class);
                }
            };
            okay.setRect(btnAreaLeft, buttonY, buttonAreaWidth / 2.0f - 1.0f, 20.0f);
            this.add(okay);
            changes.setRect(okay.right() + 1.0f, buttonY, okay.width(), 20.0f);
            changes.icon(Icons.get(Icons.CHANGES));
            this.add(changes);
        } else {
            okay.text(Messages.get(TitleScene.class, "enter", new Object[0]));
            okay.setRect(btnAreaLeft, buttonY, buttonAreaWidth, 20.0f);
            okay.icon(Icons.get(Icons.ENTER));
            this.add(okay);
        }
        RenderedTextBlock text = PixelScene.renderTextBlock(6);
        if (previousVersion == 0 || SPDSettings.intro()) {
            message = Document.INTROS.pageBody(0);
        } else if (previousVersion <= ShatteredPixelDungeon.versionCode) {
            if (previousVersion < 909) {
                message = Messages.get(this, "update_intro", new Object[0]);
                message = (String)message + "\n\n" + Messages.get(this, "update_msg", new Object[0]);
            } else {
                message = Messages.get(this, "patch_intro", new Object[0]);
                message = (String)message + "\n";
                message = (String)message + "\n" + Messages.get(this, "patch_bugfixes", new Object[0]);
                message = (String)message + "\n" + Messages.get(this, "patch_translations", new Object[0]);
            }
        } else {
            message = Messages.get(this, "what_msg", new Object[0]);
        }
        text.text((String)message, Math.min(w - 20, 300));
        float titleBottom = title.y + title.height();
        float textSpace = okay.top() - titleBottom - 4.0f;
        text.setPos(insets.left + ((float)w - text.width()) / 2.0f, titleBottom + 2.0f + (textSpace - text.height()) / 2.0f);
        this.add(text);
        if (SPDSettings.intro() && ControllerHandler.isControllerConnected()) {
            this.addToFront(new WndHardNotification(Icons.CONTROLLER.get(), Messages.get(WelcomeScene.class, "controller_title", new Object[0]), Messages.get(WelcomeScene.class, "controller_body", new Object[0]), Messages.get(WelcomeScene.class, "controller_okay", new Object[0]), 0){

                @Override
                public void onBackPressed() {
                }
            });
        }
    }

    private void placeTorch(float x, float y) {
        Fireball fb = new Fireball();
        fb.x = x - fb.width() / 2.0f;
        fb.y = y - fb.height();
        WelcomeScene.align(fb);
        this.add(fb);
    }

    private void updateVersion(int previousVersion) {
        if (previousVersion < 909) {
            Badges.loadGlobal();
            Journal.loadGlobal();
            try {
                Rankings.INSTANCE.load();
                for (Rankings.Record rec : Rankings.INSTANCE.records.toArray(new Rankings.Record[0])) {
                    try {
                        Rankings.INSTANCE.loadGameData(rec);
                        Rankings.INSTANCE.saveGameData(rec);
                    }
                    catch (Exception e) {
                        rec.gameData = null;
                        Game.reportException(new RuntimeException("Rankings Updating Failed!", e));
                    }
                }
                if (Rankings.INSTANCE.latestDaily != null) {
                    try {
                        Rankings.INSTANCE.loadGameData(Rankings.INSTANCE.latestDaily);
                        Rankings.INSTANCE.saveGameData(Rankings.INSTANCE.latestDaily);
                    }
                    catch (Exception e) {
                        Rankings.INSTANCE.latestDaily.gameData = null;
                        Game.reportException(new RuntimeException("Rankings Updating Failed!", e));
                    }
                }
                Collections.sort(Rankings.INSTANCE.records, Rankings.scoreComparator);
                Rankings.INSTANCE.save();
            }
            catch (Exception e) {
                FileUtils.deleteFile("rankings.dat");
                Game.reportException(new RuntimeException("Rankings Updating Failed!", e));
            }
            Dungeon.dailyReplay = false;
            Dungeon.daily = false;
            Badges.saveGlobal(true);
            Journal.saveGlobal(true);
        }
        SPDSettings.version(ShatteredPixelDungeon.versionCode);
    }
}

