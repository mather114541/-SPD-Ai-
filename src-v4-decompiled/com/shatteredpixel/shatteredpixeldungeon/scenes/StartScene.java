/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGameInProgress;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.utils.RectF;
import java.util.ArrayList;

public class StartScene
extends PixelScene {
    private static final int SLOT_WIDTH = 120;
    private static final int SLOT_HEIGHT = 22;

    @Override
    public void create() {
        super.create();
        Badges.loadGlobal();
        Journal.loadGlobal();
        StartScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        w = (int)((float)w - (insets.left + insets.right));
        h = (int)((float)h - (insets.top + insets.bottom));
        ExitButton btnExit = new ExitButton();
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        IconTitle title = new IconTitle(Icons.ENTER.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        StartScene.align(title);
        this.add(title);
        ArrayList<GamesInProgress.Info> games = GamesInProgress.checkAll();
        int slotCount = Math.min(GamesInProgress.MAX_SLOTS, games.size() + 1);
        int slotGap = 10 - slotCount;
        int slotsHeight = slotCount * 22 + (slotCount - 1) * slotGap;
        slotsHeight += 14;
        while (slotGap >= 2 && (float)slotsHeight > (float)h - title.bottom() - 2.0f) {
            --slotGap;
            slotsHeight -= slotCount - 1;
        }
        float yPos = insets.top + ((float)(h - slotsHeight) + title.bottom() + 2.0f) / 2.0f - 4.0f;
        yPos = Math.max(yPos, title.bottom() + 2.0f);
        float slotLeft = insets.left + (float)(w - 120) / 2.0f;
        for (GamesInProgress.Info info : games) {
            SaveSlotButton existingGame = new SaveSlotButton();
            existingGame.set(info.slot);
            existingGame.setRect(slotLeft, yPos, 120.0f, 22.0f);
            yPos += (float)(22 + slotGap);
            StartScene.align(existingGame);
            this.add(existingGame);
        }
        if (games.size() < GamesInProgress.MAX_SLOTS) {
            SaveSlotButton newGame = new SaveSlotButton();
            newGame.set(GamesInProgress.firstEmpty());
            newGame.setRect(slotLeft, yPos, 120.0f, 22.0f);
            yPos += (float)(22 + slotGap);
            StartScene.align(newGame);
            this.add(newGame);
        }
        GamesInProgress.curSlot = 0;
        String sortText = "";
        switch (SPDSettings.gamesInProgressSort()) {
            case "level": {
                sortText = Messages.get(this, "sort_level", new Object[0]);
                break;
            }
            case "last_played": {
                sortText = Messages.get(this, "sort_recent", new Object[0]);
            }
        }
        StyledButton styledButton = new StyledButton(Chrome.Type.TOAST_TR, sortText, 6){

            @Override
            protected void onClick() {
                super.onClick();
                if (SPDSettings.gamesInProgressSort().equals("level")) {
                    SPDSettings.gamesInProgressSort("last_played");
                } else {
                    SPDSettings.gamesInProgressSort("level");
                }
                ShatteredPixelDungeon.seamlessResetScene();
            }
        };
        styledButton.textColor(0xCCCCCC);
        if (yPos + 10.0f > (float)Camera.main.height) {
            styledButton.setRect(slotLeft - styledButton.reqWidth() - 6.0f, Camera.main.height - 14, styledButton.reqWidth() + 4.0f, 12.0f);
        } else {
            styledButton.setRect(slotLeft, yPos, styledButton.reqWidth() + 4.0f, 12.0f);
        }
        if (games.size() >= 2) {
            this.add(styledButton);
        }
        this.fadeIn();
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    private static class SaveSlotButton
    extends Button {
        private NinePatch bg;
        private Image hero;
        private RenderedTextBlock name;
        private RenderedTextBlock lastPlayed;
        private Image steps;
        private BitmapText depth;
        private Image classIcon;
        private BitmapText level;
        private int slot;
        private boolean newGame;

        private SaveSlotButton() {
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.bg = Chrome.get(Chrome.Type.TOAST_TR);
            this.add(this.bg);
            this.name = PixelScene.renderTextBlock(9);
            this.add(this.name);
            this.lastPlayed = PixelScene.renderTextBlock(6);
            this.add(this.lastPlayed);
        }

        public void set(int slot) {
            this.slot = slot;
            GamesInProgress.Info info = GamesInProgress.check(slot);
            boolean bl = this.newGame = info == null;
            if (this.newGame) {
                this.name.text(Messages.get(StartScene.class, "new", new Object[0]));
                if (this.hero != null) {
                    this.remove(this.hero);
                    this.hero = null;
                    this.remove(this.steps);
                    this.steps = null;
                    this.remove(this.depth);
                    this.depth = null;
                    this.remove(this.classIcon);
                    this.classIcon = null;
                    this.remove(this.level);
                    this.level = null;
                }
            } else {
                if (info.subClass != HeroSubClass.NONE) {
                    this.name.text(Messages.titleCase(info.subClass.title()));
                } else {
                    this.name.text(Messages.titleCase(info.heroClass.title()));
                }
                if (this.hero == null) {
                    this.hero = new Image(info.heroClass.spritesheet(), 0, 15 * info.armorTier, 12, 15);
                    this.add(this.hero);
                    this.steps = new Image(Icons.get(Icons.STAIRS));
                    this.add(this.steps);
                    this.depth = new BitmapText(PixelScene.pixelFont);
                    this.add(this.depth);
                    this.classIcon = new Image(Icons.get(info.heroClass));
                    this.add(this.classIcon);
                    this.level = new BitmapText(PixelScene.pixelFont);
                    this.add(this.level);
                } else {
                    this.hero.copy(new Image(info.heroClass.spritesheet(), 0, 15 * info.armorTier, 12, 15));
                    this.classIcon.copy(Icons.get(info.heroClass));
                }
                long diff = Game.realTime - info.lastPlayed;
                if (diff > 256608000000L) {
                    this.lastPlayed.text(" ");
                } else if (diff < 60000L) {
                    this.lastPlayed.text(Messages.get(StartScene.class, "one_minute_ago", new Object[0]));
                } else if (diff < 0x6DDD00L) {
                    this.lastPlayed.text(Messages.get(StartScene.class, "minutes_ago", diff / 60000L));
                } else if (diff < 172800000L) {
                    this.lastPlayed.text(Messages.get(StartScene.class, "hours_ago", diff / 3600000L));
                } else if (diff < 5184000000L) {
                    this.lastPlayed.text(Messages.get(StartScene.class, "days_ago", diff / 86400000L));
                } else {
                    this.lastPlayed.text(Messages.get(StartScene.class, "months_ago", diff / 2592000000L));
                }
                this.depth.text(Integer.toString(info.depth));
                this.depth.measure();
                this.level.text(Integer.toString(info.level));
                this.level.measure();
                if (info.challenges > 0) {
                    this.name.hardlight(0xFFFF44);
                    this.lastPlayed.hardlight(0xFFFF44);
                    this.depth.hardlight(0xFFFF44);
                    this.level.hardlight(0xFFFF44);
                } else {
                    this.name.resetColor();
                    this.lastPlayed.resetColor();
                    this.depth.resetColor();
                    this.level.resetColor();
                }
                if (info.daily) {
                    if (info.dailyReplay) {
                        this.steps.hardlight(1.0f, 0.5f, 2.0f);
                    } else {
                        this.steps.hardlight(0.5f, 1.0f, 2.0f);
                    }
                } else if (!info.customSeed.isEmpty()) {
                    this.steps.hardlight(1.0f, 1.5f, 0.67f);
                }
            }
            this.layout();
        }

        @Override
        protected void layout() {
            super.layout();
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width, this.height);
            if (this.hero != null) {
                this.hero.x = this.x + 8.0f;
                this.hero.y = this.y + (this.height - this.hero.height()) / 2.0f;
                PixelScene.align(this.hero);
                this.name.setPos(this.hero.x + this.hero.width() + 6.0f, this.y + (this.height - this.name.height() - this.lastPlayed.height() - 2.0f) / 2.0f);
                PixelScene.align(this.name);
                this.lastPlayed.setPos(this.hero.x + this.hero.width() + 6.0f, this.name.bottom() + 2.0f);
                this.classIcon.x = this.x + this.width - 24.0f + (16.0f - this.classIcon.width()) / 2.0f;
                this.classIcon.y = this.y + (this.height - this.classIcon.height()) / 2.0f;
                PixelScene.align(this.classIcon);
                this.level.x = this.classIcon.x + (this.classIcon.width() - this.level.width()) / 2.0f;
                this.level.y = this.classIcon.y + (this.classIcon.height() - this.level.height()) / 2.0f + 1.0f;
                PixelScene.align(this.level);
                this.steps.x = this.x + this.width - 40.0f + (16.0f - this.steps.width()) / 2.0f;
                this.steps.y = this.y + (this.height - this.steps.height()) / 2.0f;
                PixelScene.align(this.steps);
                this.depth.x = this.steps.x + (this.steps.width() - this.depth.width()) / 2.0f;
                this.depth.y = this.steps.y + (this.steps.height() - this.depth.height()) / 2.0f + 1.0f;
                PixelScene.align(this.depth);
            } else {
                this.name.setPos(this.x + (this.width - this.name.width()) / 2.0f, this.y + (this.height - this.name.height()) / 2.0f);
                PixelScene.align(this.name);
            }
        }

        @Override
        protected void onClick() {
            if (this.newGame) {
                GamesInProgress.selectedClass = null;
                GamesInProgress.curSlot = this.slot;
                ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
            } else {
                ShatteredPixelDungeon.scene().add(new WndGameInProgress(this.slot));
            }
        }
    }
}

