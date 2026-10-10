/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.journal.Journal;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndHeroInfo;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTextInput;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndVictoryCongrats;
import com.watabou.gltextures.TextureCache;
import com.watabou.input.PointerEvent;
import com.watabou.noosa.Camera;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.PointerArea;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.tweeners.Tweener;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.RectF;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class HeroSelectScene
extends PixelScene {
    private Image background;
    private Image fadeLeft;
    private Image fadeRight;
    private IconButton btnFade;
    private RenderedTextBlock title;
    private ArrayList<StyledButton> heroBtns = new ArrayList();
    private RenderedTextBlock heroName;
    private RenderedTextBlock heroDesc;
    private StyledButton startBtn;
    private IconButton infoButton;
    private IconButton btnOptions;
    private GameOptions optionsPane;
    private IconButton btnExit;
    private RectF insets;
    private static boolean heroWasRandomized = true;
    private static boolean chalWasRandomized = false;
    private float uiAlpha;

    @Override
    public void create() {
        super.create();
        Dungeon.hero = null;
        Badges.loadGlobal();
        Journal.loadGlobal();
        this.insets = Game.platform.getSafeInsets(1).scale(1.0f / (float)defaultZoom);
        final float w = (float)Camera.main.width - this.insets.left - this.insets.right;
        float h = (float)Camera.main.height - this.insets.top - this.insets.bottom;
        this.background = new Image(TextureCache.createSolid(-13816015), 0, 0, 800, 450){

            @Override
            public void update() {
                if (GamesInProgress.selectedClass != null) {
                    if (this.rm > 1.0f) {
                        this.rm -= Game.elapsed;
                        this.gm = this.bm = this.rm;
                    } else {
                        this.bm = 1.0f;
                        this.gm = 1.0f;
                        this.rm = 1.0f;
                    }
                }
            }
        };
        this.background.scale.set((float)Camera.main.height / this.background.height);
        this.background.x = ((float)Camera.main.width - this.background.width()) / 2.0f;
        this.background.y = ((float)Camera.main.height - this.background.height()) / 2.0f;
        PixelScene.align(this.background);
        this.add(this.background);
        this.fadeLeft = new Image(TextureCache.createGradient(-16777216, -16777216, 0));
        this.fadeLeft.x = this.background.x - 2.0f;
        this.fadeLeft.scale.set(3.0f, this.background.height());
        this.add(this.fadeLeft);
        this.fadeRight = new Image(this.fadeLeft);
        this.fadeRight.x = this.background.x + this.background.width() + 2.0f;
        this.fadeRight.y = this.background.y + this.background.height();
        this.fadeRight.angle = 180.0f;
        this.add(this.fadeRight);
        this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 12);
        this.title.hardlight(0xFFFF44);
        PixelScene.align(this.title);
        this.add(this.title);
        this.startBtn = new StyledButton(Chrome.Type.GREY_BUTTON_TR, ""){

            @Override
            protected void onClick() {
                super.onClick();
                if (GamesInProgress.selectedClass == null) {
                    return;
                }
                Dungeon.hero = null;
                Dungeon.dailyReplay = false;
                Dungeon.daily = false;
                Dungeon.initSeed();
                ActionIndicator.clearAction();
                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                Game.switchScene(InterlevelScene.class);
            }
        };
        this.startBtn.icon(Icons.get(Icons.ENTER));
        this.startBtn.setSize(80.0f, 21.0f);
        this.startBtn.textColor(0xFFFF44);
        this.add(this.startBtn);
        this.startBtn.active = false;
        this.startBtn.visible = false;
        this.infoButton = new IconButton(Icons.get(Icons.INFO)){

            @Override
            protected void onClick() {
                super.onClick();
                HeroClass cls = GamesInProgress.selectedClass;
                if (cls != null) {
                    WndHeroInfo info = new WndHeroInfo(GamesInProgress.selectedClass);
                    if (PixelScene.landscape()) {
                        ((Window)info).offset((int)(w / 6.0f), 0);
                    }
                    ShatteredPixelDungeon.scene().addToFront(info);
                }
            }

            @Override
            protected String hoverText() {
                return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info", new Object[0]));
            }
        };
        this.infoButton.active = false;
        this.infoButton.visible = false;
        this.infoButton.setSize(20.0f, 21.0f);
        this.add(this.infoButton);
        for (HeroClass cl : HeroClass.values()) {
            StyledButton button = new HeroBtn(cl);
            this.add(button);
            this.heroBtns.add(button);
        }
        this.optionsPane = new GameOptions();
        this.optionsPane.active = false;
        this.optionsPane.visible = false;
        this.optionsPane.layout();
        this.add(this.optionsPane);
        this.btnOptions = new IconButton(Icons.get(Icons.PREFS)){

            @Override
            protected void onClick() {
                super.onClick();
                HeroSelectScene.this.optionsPane.visible = !HeroSelectScene.this.optionsPane.visible;
                HeroSelectScene.this.optionsPane.active = !HeroSelectScene.this.optionsPane.active;
            }

            @Override
            protected void onPointerDown() {
                super.onPointerDown();
            }

            @Override
            protected void onPointerUp() {
                HeroSelectScene.this.updateOptionsColor();
            }

            @Override
            protected String hoverText() {
                return Messages.get(HeroSelectScene.class, "options", new Object[0]);
            }
        };
        this.updateOptionsColor();
        this.btnOptions.visible = false;
        if (!SPDSettings.intro()) {
            this.add(this.btnOptions);
        }
        if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()) {
            Dungeon.challenges = 0;
            SPDSettings.challenges(0);
            SPDSettings.customSeed("");
        }
        if (HeroSelectScene.landscape()) {
            float leftArea = Math.max(100.0f, w / 3.0f);
            float uiHeight = Math.min(h - 20.0f, 300.0f);
            float uiSpacing = (uiHeight - 120.0f) / 2.0f;
            if (uiHeight >= 160.0f) {
                uiSpacing -= 5.0f;
            }
            if (uiHeight >= 180.0f) {
                uiSpacing -= 6.0f;
            }
            this.background.x += this.insets.left + leftArea / 6.0f;
            float fadeLeftScale = 47.0f * (leftArea - this.background.x) / leftArea;
            this.fadeLeft.scale = new PointF(3.0f + Math.max(0.0f, fadeLeftScale), this.background.height());
            this.title.setPos(this.insets.left + (leftArea - this.title.width()) / 2.0f, (h - uiHeight) / 2.0f);
            HeroSelectScene.align(this.title);
            int btnWidth = 35;
            int btnHeight = 24;
            if (uiHeight >= 180.0f) {
                btnHeight += 6;
            }
            int cols = (int)Math.ceil((float)this.heroBtns.size() / 2.0f);
            float curX = this.insets.left + (leftArea - (float)(btnWidth * cols) + (float)(cols - 1)) / 2.0f;
            float curY = this.title.bottom() + uiSpacing;
            int count = 0;
            for (StyledButton button : this.heroBtns) {
                button.setRect(curX, curY, btnWidth, btnHeight);
                HeroSelectScene.align(button);
                curX += (float)(btnWidth + 1);
                if (++count < (1 + this.heroBtns.size()) / 2) continue;
                curX -= (float)(btnWidth * count + count);
                curY += (float)(btnHeight + 1);
                if (this.heroBtns.size() % 2 != 0) {
                    curX += (float)btnWidth / 2.0f;
                }
                count = 0;
            }
            this.heroName = HeroSelectScene.renderTextBlock(9);
            this.heroName.setPos(this.insets.left, this.heroBtns.get(this.heroBtns.size() - 1).bottom() + 5.0f);
            this.add(this.heroName);
            this.heroDesc = uiHeight >= 160.0f ? HeroSelectScene.renderTextBlock(6) : HeroSelectScene.renderTextBlock(5);
            this.heroDesc.align(2);
            this.heroDesc.setPos(this.insets.left, this.heroName.bottom() + 5.0f);
            this.add(this.heroDesc);
            this.startBtn.text(Messages.titleCase(Messages.get(this, "start", new Object[0])));
            this.startBtn.setSize(this.startBtn.reqWidth() + 8.0f, 21.0f);
            this.startBtn.setPos(this.insets.left + (leftArea - this.startBtn.width()) / 2.0f, this.title.top() + uiHeight - this.startBtn.height());
            HeroSelectScene.align(this.startBtn);
            this.btnFade = new IconButton(Icons.CHEVRON.get()){

                @Override
                protected void onClick() {
                    this.enable(false);
                    this.parent.add(new Tweener(this.parent, 0.5f){

                        @Override
                        protected void updateValues(float progress) {
                            HeroSelectScene.this.uiAlpha = 1.0f - progress;
                            HeroSelectScene.this.updateFade();
                        }
                    });
                }
            };
            this.btnFade.icon().originToCenter();
            this.btnFade.icon().angle = 270.0f;
            this.btnFade.active = false;
            this.btnFade.visible = false;
            this.btnFade.setRect(this.startBtn.left() - 20.0f, this.startBtn.top(), 20.0f, 21.0f);
            HeroSelectScene.align(this.btnFade);
            this.add(this.btnFade);
            this.btnOptions.setRect(this.startBtn.right(), this.startBtn.top(), 20.0f, 21.0f);
            this.optionsPane.setPos(this.btnOptions.right(), this.btnOptions.top() - this.optionsPane.height() - 2.0f);
            HeroSelectScene.align(this.optionsPane);
        } else {
            this.background.visible = false;
            int btnWidth = 20;
            float curX = this.insets.left + (w - (float)(btnWidth * this.heroBtns.size())) / 2.0f;
            if (curX > 0.0f) {
                btnWidth = (int)((float)btnWidth + Math.min(curX / ((float)this.heroBtns.size() / 2.0f), 15.0f));
                curX = this.insets.left + (w - (float)(btnWidth * this.heroBtns.size())) / 2.0f;
            }
            float curY = this.insets.top + h - 24.0f + 3.0f;
            for (StyledButton button : this.heroBtns) {
                button.setRect(curX, curY, btnWidth, 24.0f + this.insets.bottom);
                curX += (float)btnWidth;
            }
            if (this.insets.bottom > 0.0f) {
                SkinnedBlock bar = new SkinnedBlock(Camera.main.width, this.insets.bottom, TextureCache.createSolid(-1442840576));
                bar.y = h + this.insets.top;
                this.add(bar);
                PointerArea blocker = new PointerArea(0.0f, (float)Camera.main.width - this.insets.bottom, Camera.main.width, this.insets.bottom);
                this.add(blocker);
            }
            this.title.setPos(this.insets.left + (w - this.title.width()) / 2.0f, this.insets.top + (h - 24.0f - this.title.height() - 4.0f));
            this.btnOptions.setRect(this.heroBtns.get(0).left() + 16.0f, Camera.main.height - 24 - 16, 20.0f, 21.0f);
            this.optionsPane.setPos(this.heroBtns.get(0).left(), 0.0f);
        }
        this.btnExit = new ExitButton();
        int ofs = PixelScene.landscape() ? 0 : 4;
        this.btnExit.setPos((float)Camera.main.width - this.btnExit.width() - (float)ofs, ofs);
        this.add(this.btnExit);
        this.btnExit.active = !SPDSettings.intro();
        this.btnExit.visible = this.btnExit.active;
        PointerArea fadeResetter = new PointerArea(0.0f, 0.0f, Camera.main.width, Camera.main.height){

            @Override
            public boolean onSignal(PointerEvent event) {
                if (event != null && event.type == PointerEvent.Type.UP) {
                    if (HeroSelectScene.this.uiAlpha == 0.0f && PixelScene.landscape()) {
                        this.parent.add(new Tweener(this.parent, 0.5f){

                            @Override
                            protected void updateValues(float progress) {
                                HeroSelectScene.this.uiAlpha = progress;
                                HeroSelectScene.this.updateFade();
                            }

                            @Override
                            protected void onComplete() {
                                HeroSelectScene.this.resetFade();
                            }
                        });
                    } else {
                        HeroSelectScene.this.resetFade();
                    }
                }
                return false;
            }
        };
        this.add(fadeResetter);
        this.resetFade();
        if (GamesInProgress.selectedClass != null) {
            this.setSelectedHero(GamesInProgress.selectedClass);
        }
        if (Badges.isUnlocked(Badges.Badge.VICTORY) && !SPDSettings.victoryNagged()) {
            SPDSettings.victoryNagged(true);
            this.add(new WndVictoryCongrats());
        }
        this.fadeIn();
    }

    private void updateOptionsColor() {
        if (!SPDSettings.customSeed().isEmpty()) {
            this.btnOptions.icon().hardlight(1.0f, 1.5f, 0.67f);
        } else if (SPDSettings.challenges() != 0) {
            this.btnOptions.icon().hardlight(2.0f, 1.33f, 0.5f);
        } else {
            this.btnOptions.icon().resetColor();
        }
    }

    private void setSelectedHero(HeroClass cl) {
        GamesInProgress.selectedClass = cl;
        GamesInProgress.randomizedClass = false;
        try {
            this.background.texture(cl.splashArt());
        }
        catch (Exception e) {
            Game.reportException(e);
            this.background.texture(TextureCache.createSolid(-13816015));
            this.background.frame(0, 0, 800, 450);
        }
        this.background.visible = true;
        this.background.hardlight(1.5f, 1.5f, 1.5f);
        float leftPortion = Math.max(100.0f, ((float)Camera.main.width - this.insets.left - this.insets.right) / 3.0f);
        if (HeroSelectScene.landscape()) {
            this.heroName.text(Messages.titleCase(cl.title()));
            this.heroName.hardlight(0xFFFF44);
            this.heroName.setPos(this.insets.left + (leftPortion - this.heroName.width() - 20.0f) / 2.0f, this.heroName.top());
            HeroSelectScene.align(this.heroName);
            this.heroDesc.text(cl.shortDesc());
            this.heroDesc.maxWidth(80);
            this.heroDesc.setPos(this.insets.left + (leftPortion - this.heroDesc.width()) / 2.0f, this.heroName.bottom() + 5.0f);
            HeroSelectScene.align(this.heroDesc);
            while (this.startBtn.top() < this.heroDesc.bottom()) {
                this.heroDesc.maxWidth(this.heroDesc.maxWidth() + 10);
                this.heroDesc.setPos(Math.max(this.insets.left, (leftPortion - this.heroDesc.width()) / 2.0f), this.heroName.bottom() + 5.0f);
                HeroSelectScene.align(this.heroDesc);
            }
            this.btnFade.active = true;
            this.btnFade.visible = true;
            this.startBtn.active = true;
            this.startBtn.visible = true;
            this.infoButton.active = true;
            this.infoButton.visible = true;
            this.infoButton.setPos(this.heroName.right(), this.heroName.top() + (this.heroName.height() - this.infoButton.height()) / 2.0f);
            HeroSelectScene.align(this.infoButton);
            this.btnOptions.active = !SPDSettings.intro();
            this.btnOptions.visible = this.btnOptions.active;
        } else {
            this.title.visible = false;
            this.startBtn.active = true;
            this.startBtn.visible = true;
            this.startBtn.text(Messages.titleCase(cl.title()));
            this.startBtn.setSize(this.startBtn.reqWidth() + 8.0f, 21.0f);
            this.startBtn.setPos(((float)Camera.main.width - this.startBtn.width()) / 2.0f, (float)Camera.main.height - this.insets.bottom - 24.0f + 2.0f - this.startBtn.height());
            PixelScene.align(this.startBtn);
            this.infoButton.active = true;
            this.infoButton.visible = true;
            this.infoButton.setPos(this.startBtn.right(), this.startBtn.top());
            this.btnOptions.active = !SPDSettings.intro();
            this.btnOptions.visible = this.btnOptions.active;
            this.btnOptions.setPos(this.startBtn.left() - this.btnOptions.width(), this.startBtn.top());
            this.optionsPane.setPos(this.heroBtns.get(0).left(), this.startBtn.top() - this.optionsPane.height() - 2.0f);
            HeroSelectScene.align(this.optionsPane);
        }
        this.updateOptionsColor();
    }

    @Override
    public void update() {
        super.update();
        if (SPDSettings.intro() && Rankings.INSTANCE.totalNumber > 0) {
            SPDSettings.intro(false);
        }
        this.btnExit.active = !SPDSettings.intro();
        this.btnExit.visible = this.btnExit.active;
        for (Object v : this.members) {
            if (!(v instanceof Window)) continue;
            this.resetFade();
        }
        if (!PixelScene.landscape() && GamesInProgress.selectedClass != null) {
            if (this.uiAlpha > 0.0f) {
                this.uiAlpha -= Game.elapsed / 4.0f;
            }
            this.updateFade();
        }
    }

    private void updateFade() {
        float alpha = GameMath.gate(0.0f, this.uiAlpha, 1.0f);
        this.title.alpha(alpha);
        for (StyledButton b : this.heroBtns) {
            b.enable(alpha != 0.0f);
            b.alpha(alpha);
        }
        if (this.heroName != null) {
            this.heroName.alpha(alpha);
            this.heroDesc.alpha(alpha);
            this.btnFade.enable(alpha != 0.0f);
            this.btnFade.icon().alpha(alpha);
        }
        this.startBtn.enable(alpha != 0.0f);
        this.startBtn.alpha(alpha);
        this.btnExit.enable(this.btnExit.visible && alpha != 0.0f);
        this.btnExit.icon().alpha(alpha);
        this.optionsPane.active = this.optionsPane.visible && alpha != 0.0f;
        this.optionsPane.alpha(alpha);
        this.btnOptions.enable(alpha != 0.0f);
        this.btnOptions.icon().alpha(alpha);
        this.infoButton.enable(alpha != 0.0f);
        this.infoButton.icon().alpha(alpha);
        if (HeroSelectScene.landscape()) {
            int w = (int)((float)Camera.main.width - this.insets.left - this.insets.right);
            this.background.x = this.insets.left + ((float)w - this.background.width()) / 2.0f;
            float leftPortion = Math.max(100.0f, (float)w / 3.0f);
            this.background.x += leftPortion / 2.0f * alpha;
            float fadeLeftScale = 47.0f * (leftPortion - (this.background.x - this.insets.left)) / leftPortion;
            this.fadeLeft.scale.x = 3.0f + Math.max(fadeLeftScale, 0.0f) * alpha;
            this.fadeLeft.x = this.background.x - 4.0f;
            this.fadeRight.x = this.background.x + this.background.width() + 4.0f;
        }
        this.fadeLeft.x = this.background.x - 5.0f;
        this.fadeRight.x = this.background.x + this.background.width() + 5.0f;
        this.fadeLeft.visible = this.background.x > 0.0f || alpha > 0.0f && HeroSelectScene.landscape();
        this.fadeRight.visible = this.background.x + this.background.width() < (float)Camera.main.width;
    }

    private void resetFade() {
        this.uiAlpha = 2.0f;
        this.updateFade();
    }

    @Override
    protected void onBackPressed() {
        if (this.btnExit.active) {
            ShatteredPixelDungeon.switchScene(TitleScene.class);
        } else {
            super.onBackPressed();
        }
    }

    private class HeroBtn
    extends StyledButton {
        private HeroClass cl;
        private static final int MIN_WIDTH = 20;
        private static final int HEIGHT = 24;

        HeroBtn(HeroClass cl) {
            super(Chrome.Type.GREY_BUTTON_TR, "");
            this.cl = cl;
            this.icon(new Image(cl.spritesheet(), 0, 90, 12, 15));
        }

        @Override
        public void update() {
            super.update();
            if (this.cl != GamesInProgress.selectedClass) {
                if (!this.cl.isUnlocked()) {
                    this.icon.brightness(0.1f);
                } else {
                    this.icon.brightness(0.6f);
                }
            } else {
                this.icon.brightness(1.0f);
            }
        }

        @Override
        protected void onClick() {
            super.onClick();
            if (!this.cl.isUnlocked()) {
                ShatteredPixelDungeon.scene().addToFront(new WndMessage(this.cl.unlockMsg()));
            } else if (GamesInProgress.selectedClass == this.cl) {
                WndHeroInfo w = new WndHeroInfo(this.cl);
                if (PixelScene.landscape()) {
                    ((Window)w).offset(Camera.main.width / 6, 0);
                }
                ShatteredPixelDungeon.scene().addToFront(w);
            } else {
                HeroSelectScene.this.setSelectedHero(this.cl);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.height > 30.0f) {
                this.icon.y = this.y + (24.0f - this.icon.height()) / 2.0f;
            }
        }
    }

    private class GameOptions
    extends Component {
        private NinePatch bg;
        private ArrayList<StyledButton> buttons;
        private ArrayList<ColorBlock> spacers;
        protected StyledButton challengeButton;

        private GameOptions() {
        }

        @Override
        protected void createChildren() {
            this.bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
            this.add(this.bg);
            this.buttons = new ArrayList();
            this.spacers = new ArrayList();
            StyledButton seedButton = new StyledButton(Chrome.Type.BLANK, Messages.get(HeroSelectScene.class, "custom_seed", new Object[0]), 6){

                @Override
                protected void onClick() {
                    if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()) {
                        ShatteredPixelDungeon.scene().addToFront(new WndTitledMessage(Icons.get(Icons.SEED), Messages.get(HeroSelectScene.class, "custom_seed", new Object[0]), Messages.get(HeroSelectScene.class, "custom_seed_nowin", new Object[0])));
                        return;
                    }
                    String existingSeedtext = SPDSettings.customSeed();
                    ShatteredPixelDungeon.scene().addToFront(new WndTextInput(Messages.get(HeroSelectScene.class, "custom_seed_title", new Object[0]), Messages.get(HeroSelectScene.class, "custom_seed_desc", new Object[0]), existingSeedtext, 20, false, Messages.get(HeroSelectScene.class, "custom_seed_set", new Object[0]), Messages.get(HeroSelectScene.class, "custom_seed_clear", new Object[0])){

                        @Override
                        public void onSelect(boolean positive, String text) {
                            text = DungeonSeed.formatText(text);
                            long seed = DungeonSeed.convertFromText(text);
                            if (positive && seed != -1L) {
                                for (GamesInProgress.Info info : GamesInProgress.checkAll()) {
                                    if (!info.customSeed.isEmpty() || info.seed != seed) continue;
                                    SPDSettings.customSeed("");
                                    icon.resetColor();
                                    ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "custom_seed_duplicate", new Object[0])));
                                    return;
                                }
                                SPDSettings.customSeed(text);
                                icon.hardlight(1.0f, 1.5f, 0.67f);
                            } else {
                                SPDSettings.customSeed("");
                                icon.resetColor();
                            }
                            HeroSelectScene.this.updateOptionsColor();
                        }
                    });
                }
            };
            seedButton.leftJustify = true;
            seedButton.icon(Icons.get(Icons.SEED));
            if (!SPDSettings.customSeed().isEmpty()) {
                seedButton.icon().hardlight(1.0f, 1.5f, 0.67f);
            }
            this.buttons.add(seedButton);
            this.add(seedButton);
            StyledButton dailyButton = new StyledButton(Chrome.Type.BLANK, Messages.get(HeroSelectScene.class, "daily", new Object[0]), 6){
                private static final long SECOND = 1000L;
                private static final long MINUTE = 60000L;
                private static final long HOUR = 3600000L;
                private static final long DAY = 86400000L;
                private long timeToUpdate;
                private final SimpleDateFormat dateFormat;
                {
                    this.timeToUpdate = 0L;
                    this.dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
                    this.dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                }

                @Override
                protected void onClick() {
                    super.onClick();
                    if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()) {
                        ShatteredPixelDungeon.scene().addToFront(new WndTitledMessage(Icons.get(Icons.CALENDAR), Messages.get(HeroSelectScene.class, "daily", new Object[0]), Messages.get(HeroSelectScene.class, "daily_nowin", new Object[0])));
                        return;
                    }
                    final long diff = SPDSettings.lastDaily() + 86400000L - Game.realTime;
                    if (diff > 86400000L) {
                        ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "daily_unavailable_long", diff / 86400000L + 1L)));
                        return;
                    }
                    for (GamesInProgress.Info game : GamesInProgress.checkAll()) {
                        if (!game.daily) continue;
                        ShatteredPixelDungeon.scene().addToFront(new WndMessage(Messages.get(HeroSelectScene.class, "daily_existing", new Object[0])));
                        return;
                    }
                    Image icon = Icons.get(Icons.CALENDAR);
                    if (diff <= 0L) {
                        icon.hardlight(0.5f, 1.0f, 2.0f);
                    } else {
                        icon.hardlight(1.0f, 0.5f, 2.0f);
                    }
                    ShatteredPixelDungeon.scene().addToFront(new WndOptions(icon, Messages.get(HeroSelectScene.class, "daily", new Object[0]), diff > 0L ? Messages.get(HeroSelectScene.class, "daily_repeat", new Object[0]) : Messages.get(HeroSelectScene.class, "daily_desc", new Object[0]), new String[]{Messages.get(HeroSelectScene.class, "daily_yes", new Object[0]), Messages.get(HeroSelectScene.class, "daily_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                if (diff <= 0L) {
                                    long time = Game.realTime - Game.realTime % 86400000L;
                                    time = Math.max(time, 1775001600000L);
                                    SPDSettings.lastDaily(time);
                                    Dungeon.dailyReplay = false;
                                } else {
                                    Dungeon.dailyReplay = true;
                                }
                                Dungeon.hero = null;
                                Dungeon.daily = true;
                                Dungeon.initSeed();
                                ActionIndicator.clearAction();
                                InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
                                Game.switchScene(InterlevelScene.class);
                            }
                        }
                    });
                }

                @Override
                public void update() {
                    super.update();
                    if (Game.realTime > this.timeToUpdate && this.visible) {
                        long diff = SPDSettings.lastDaily() + 86400000L - Game.realTime;
                        if (diff > 0L) {
                            if (diff > 108000000L) {
                                this.text("30:00:00+");
                            } else {
                                this.text(this.dateFormat.format(new Date(diff)));
                            }
                            this.timeToUpdate = Game.realTime + 1000L;
                        } else {
                            this.text(Messages.get(HeroSelectScene.class, "daily", new Object[0]));
                            this.timeToUpdate = Long.MAX_VALUE;
                        }
                    }
                }
            };
            dailyButton.leftJustify = true;
            dailyButton.icon(Icons.get(Icons.CALENDAR));
            this.add(dailyButton);
            this.buttons.add(dailyButton);
            this.challengeButton = new StyledButton(Chrome.Type.BLANK, Messages.get(WndChallenges.class, "title", new Object[0]), 6){

                @Override
                protected void onClick() {
                    if (!Badges.isUnlocked(Badges.Badge.VICTORY) && !DeviceCompat.isDebug()) {
                        ShatteredPixelDungeon.scene().addToFront(new WndTitledMessage(Icons.get(Icons.CHALLENGE_GREY), Messages.get(WndChallenges.class, "title", new Object[0]), Messages.get(HeroSelectScene.class, "challenges_nowin", new Object[0])));
                        return;
                    }
                    ShatteredPixelDungeon.scene().addToFront(new WndChallenges(SPDSettings.challenges(), true){

                        @Override
                        public void onBackPressed() {
                            super.onBackPressed();
                            this.icon(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY));
                            HeroSelectScene.this.updateOptionsColor();
                        }
                    });
                }
            };
            this.challengeButton.leftJustify = true;
            this.challengeButton.icon(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY));
            this.add(this.challengeButton);
            this.buttons.add(this.challengeButton);
            int unlockedCount = 0;
            for (HeroClass cls : HeroClass.values()) {
                if (!cls.isUnlocked()) continue;
                ++unlockedCount;
            }
            if (unlockedCount >= 2) {
                StyledButton randomButton = new StyledButton(Chrome.Type.BLANK, Messages.get(HeroSelectScene.class, "randomize", new Object[0]), 6){

                    @Override
                    protected void onClick() {
                        if (Badges.isUnlocked(Badges.Badge.VICTORY) || DeviceCompat.isDebug()) {
                            ShatteredPixelDungeon.scene().addToFront(new WndRandomize());
                        } else {
                            HeroClass randomCls;
                            while (!(randomCls = Random.oneOf(HeroClass.values())).isUnlocked()) {
                            }
                            HeroSelectScene.this.setSelectedHero(randomCls);
                            GamesInProgress.randomizedClass = true;
                        }
                    }
                };
                randomButton.leftJustify = true;
                randomButton.icon(Icons.SHUFFLE.get());
                this.buttons.add(randomButton);
                this.add(randomButton);
            }
            for (int i = 1; i < this.buttons.size(); ++i) {
                ColorBlock spc = new ColorBlock(1.0f, 1.0f, -16777216);
                this.add(spc);
                this.spacers.add(spc);
            }
        }

        @Override
        protected void layout() {
            super.layout();
            this.bg.x = this.x;
            this.bg.y = this.y;
            int width = 0;
            for (StyledButton btn : this.buttons) {
                if (!((float)width < btn.reqWidth())) continue;
                width = (int)btn.reqWidth();
            }
            width += this.bg.marginHor();
            int top = (int)this.y + this.bg.marginTop() - 1;
            int i = 0;
            for (StyledButton btn : this.buttons) {
                btn.setRect(this.x + (float)this.bg.marginLeft(), top, width - this.bg.marginHor(), 16.0f);
                top = (int)btn.bottom();
                if (i >= this.spacers.size()) continue;
                this.spacers.get(i).size(btn.width(), 1.0f);
                this.spacers.get((int)i).x = btn.left();
                this.spacers.get((int)i).y = PixelScene.align(btn.bottom() - 0.5f);
                ++i;
            }
            this.width = width;
            this.height = (float)(top + this.bg.marginBottom()) - this.y - 1.0f;
            this.bg.size(this.width, this.height);
        }

        private void alpha(float value) {
            this.bg.alpha(value);
            for (StyledButton btn : this.buttons) {
                btn.alpha(value);
            }
            for (ColorBlock spc : this.spacers) {
                spc.alpha(value);
            }
        }

        private class WndRandomize
        extends Window {
            CheckBox chkHero;
            CheckBox chkChals;
            OptionSlider optChals;

            public WndRandomize() {
                this.chkHero = new CheckBox(Messages.get(HeroSelectScene.class, "randomize_hero", new Object[0])){

                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        heroWasRandomized = value;
                    }
                };
                this.chkHero.setRect(0.0f, 0.0f, 120.0f, 16.0f);
                this.chkHero.checked(heroWasRandomized);
                this.add(this.chkHero);
                this.chkChals = new CheckBox(Messages.get(HeroSelectScene.class, "randomize_chals", new Object[0])){

                    @Override
                    public void checked(boolean value) {
                        super.checked(value);
                        WndRandomize.this.optChals.enable(value);
                        chalWasRandomized = value;
                    }
                };
                this.chkChals.setRect(0.0f, 20.0f, 120.0f, 16.0f);
                this.add(this.chkChals);
                int max = 9;
                this.optChals = new OptionSlider(Messages.get(HeroSelectScene.class, "randomize_chals_title", new Object[0]), "0", Integer.toString(max), 0, max){

                    @Override
                    protected void onChange() {
                    }
                };
                this.optChals.enable(false);
                this.optChals.setSelectedValue(Challenges.activeChallenges(SPDSettings.challenges()));
                this.optChals.setRect(0.0f, 38.0f, 120.0f, 22.0f);
                this.add(this.optChals);
                this.chkChals.checked(chalWasRandomized);
                RedButton btnCancel = new RedButton(Messages.get(HeroSelectScene.class, "randomize_cancel", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        WndRandomize.this.hide();
                    }
                };
                btnCancel.setRect(61.0f, 64.0f, 60.0f, 16.0f);
                this.add(btnCancel);
                RedButton btnConfirm = new RedButton(Messages.get(HeroSelectScene.class, "randomize_confirm", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        WndRandomize.this.hide();
                        if (WndRandomize.this.chkChals.checked()) {
                            int chals = WndRandomize.this.optChals.getSelectedValue();
                            ArrayList<Integer> chalMasks = new ArrayList<Integer>();
                            for (int i = 0; i < 9; ++i) {
                                chalMasks.add((int)Math.pow(2.0, i));
                            }
                            Random.shuffle(chalMasks);
                            int mask = 0;
                            for (int i = 0; i < chals; ++i) {
                                mask += ((Integer)chalMasks.remove(0)).intValue();
                            }
                            SPDSettings.challenges(mask);
                            GameOptions.this.challengeButton.icon(Icons.get(SPDSettings.challenges() > 0 ? Icons.CHALLENGE_COLOR : Icons.CHALLENGE_GREY));
                            ShatteredPixelDungeon.scene().addToFront(new WndChallenges(mask, false));
                        }
                        if (WndRandomize.this.chkHero.checked()) {
                            HeroClass randomCls;
                            while (!(randomCls = Random.oneOf(HeroClass.values())).isUnlocked()) {
                            }
                            HeroSelectScene.this.setSelectedHero(randomCls);
                            GamesInProgress.randomizedClass = true;
                        } else {
                            HeroSelectScene.this.setSelectedHero(GamesInProgress.selectedClass);
                        }
                    }
                };
                btnConfirm.setRect(0.0f, 64.0f, 60.0f, 16.0f);
                this.add(btnConfirm);
                this.resize(120, (int)btnConfirm.bottom());
            }
        }
    }
}

