/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.DangerIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.KeyDisplay;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndChallenges;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndGame;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.input.GameAction;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;

public class MenuPane
extends Component {
    private Image bg;
    private Image depthIcon;
    private BitmapText depthText;
    private Button depthButton;
    private Image challengeIcon;
    private BitmapText challengeText;
    private Button challengeButton;
    private JournalButton btnJournal;
    private MenuButton btnMenu;
    private Toolbar.PickedUpItem pickedUp;
    private BitmapText version;
    private NinePatch versionOverflowBG;
    private DangerIndicator danger;
    public static final int WIDTH = 31;

    @Override
    protected void createChildren() {
        super.createChildren();
        this.bg = new Image("interfaces/menu_pane.png", 1, 0, 31, 21);
        this.add(this.bg);
        this.versionOverflowBG = new NinePatch(this.bg.texture, 1, 22, 6, 8, 3, 0, 2, 0);
        this.add(this.versionOverflowBG);
        this.version = new BitmapText("v" + Game.version, PixelScene.pixelFont);
        this.version.hardlight(13291458);
        this.add(this.version);
        this.depthIcon = Icons.get(Dungeon.level.feeling);
        this.add(this.depthIcon);
        this.depthText = new BitmapText(Integer.toString(Dungeon.depth), PixelScene.pixelFont);
        this.depthText.hardlight(13291458);
        this.depthText.measure();
        this.add(this.depthText);
        this.depthButton = new Button(){

            @Override
            protected String hoverText() {
                if (Dungeon.level.feeling != Level.Feeling.NONE) {
                    return Dungeon.level.feeling.desc();
                }
                return null;
            }

            @Override
            protected void onClick() {
                super.onClick();
                if (Dungeon.level.feeling == Level.Feeling.NONE) {
                    GameScene.show(new WndJournal());
                } else {
                    GameScene.show(new WndTitledMessage(Icons.getLarge(Dungeon.level.feeling), Messages.titleCase(Dungeon.level.feeling.title()), Dungeon.level.feeling.desc()));
                }
            }
        };
        this.add(this.depthButton);
        if (Challenges.activeChallenges() > 0) {
            this.challengeIcon = Icons.get(Icons.CHAL_COUNT);
            this.add(this.challengeIcon);
            this.challengeText = new BitmapText(Integer.toString(Challenges.activeChallenges()), PixelScene.pixelFont);
            this.challengeText.hardlight(13291458);
            this.challengeText.measure();
            this.add(this.challengeText);
            this.challengeButton = new Button(){

                @Override
                protected void onClick() {
                    GameScene.show(new WndChallenges(Dungeon.challenges, false));
                }

                @Override
                protected String hoverText() {
                    return Messages.get(WndChallenges.class, "title", new Object[0]);
                }
            };
            this.add(this.challengeButton);
        }
        this.btnJournal = new JournalButton();
        this.add(this.btnJournal);
        this.btnMenu = new MenuButton();
        this.add(this.btnMenu);
        this.danger = new DangerIndicator();
        this.add(this.danger);
        this.pickedUp = new Toolbar.PickedUpItem();
        this.add(this.pickedUp);
    }

    @Override
    protected void layout() {
        float overFlow;
        float rightMargin;
        super.layout();
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.version.scale.set(PixelScene.align(0.5f));
        this.version.measure();
        float f = rightMargin = DeviceCompat.isDesktop() ? 1.0f : 8.0f;
        if (DeviceCompat.isDebug()) {
            rightMargin = 1.0f;
        }
        if ((overFlow = this.version.width() - (this.bg.width() - 4.0f - rightMargin)) >= 1.0f) {
            this.version.x = this.x + 2.0f - overFlow;
            this.versionOverflowBG.size(overFlow + 3.0f, 8.0f);
            this.versionOverflowBG.x = this.version.x - 3.0f;
            this.versionOverflowBG.y = this.y;
        } else {
            this.version.x = this.x + 3.0f;
            this.versionOverflowBG.visible = false;
        }
        this.version.y = this.y + 3.0f - this.version.baseLine() * this.version.scale.y / 2.0f;
        this.version.y -= 0.001f;
        PixelScene.align(this.version);
        this.btnMenu.setPos(this.x + 31.0f - this.btnMenu.width(), this.y);
        this.btnJournal.setPos(this.btnMenu.left() - this.btnJournal.width() + 2.0f, this.y);
        this.depthIcon.x = this.btnJournal.left() - 7.0f + (7.0f - this.depthIcon.width()) / 2.0f - 0.1f;
        this.depthIcon.y = this.y + 8.0f;
        PixelScene.align(this.depthIcon);
        this.depthText.scale.set(PixelScene.align(0.67f));
        this.depthText.x = this.depthIcon.x + (this.depthIcon.width() - this.depthText.width()) / 2.0f;
        this.depthText.y = this.depthIcon.y + this.depthIcon.height();
        PixelScene.align(this.depthText);
        this.depthButton.setRect(this.depthIcon.x, this.depthIcon.y, this.depthIcon.width(), this.depthIcon.height() + this.depthText.height());
        if (this.challengeIcon != null) {
            this.challengeIcon.x = this.btnJournal.left() - 14.0f + (7.0f - this.challengeIcon.width()) / 2.0f - 0.1f;
            this.challengeIcon.y = this.depthIcon.y;
            PixelScene.align(this.challengeIcon);
            this.challengeText.scale.set(PixelScene.align(0.67f));
            this.challengeText.x = this.challengeIcon.x + (this.challengeIcon.width() - this.challengeText.width()) / 2.0f;
            this.challengeText.y = this.challengeIcon.y + this.challengeIcon.height();
            PixelScene.align(this.challengeText);
            this.challengeButton.setRect(this.challengeIcon.x, this.challengeIcon.y, this.challengeIcon.width(), this.challengeIcon.height() + this.challengeText.height());
        }
        this.danger.setPos(this.x + 31.0f - this.danger.width(), this.y + this.bg.height + 1.0f);
        this.danger.setSize((float)this.camera.width - this.danger.width(), this.danger.height());
    }

    public void pickup(Item item, int cell) {
        this.pickedUp.reset(item, cell, this.btnJournal.centerX(), this.btnJournal.centerY());
    }

    public void flashForPage(Document doc, String page) {
        this.btnJournal.flashingDoc = doc;
        this.btnJournal.flashingPage = page;
    }

    public void updateKeys() {
        this.btnJournal.updateKeyDisplay();
    }

    private static class JournalButton
    extends Button {
        private Image bg;
        private Image journalIcon;
        private KeyDisplay keyIcon;
        private Document flashingDoc = null;
        private String flashingPage = null;
        private float time;

        public JournalButton() {
            this.width = this.bg.width + 4.0f;
            this.height = this.bg.height + 10.0f;
        }

        @Override
        public GameAction keyAction() {
            return SPDAction.JOURNAL;
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.bg = new Image("interfaces/menu_button.png", 2, 2, 13, 11);
            this.add(this.bg);
            this.journalIcon = new Image("interfaces/menu_button.png", 31, 0, 11, 6);
            this.add(this.journalIcon);
            this.keyIcon = new KeyDisplay();
            this.add(this.keyIcon);
            this.updateKeyDisplay();
        }

        @Override
        protected void layout() {
            super.layout();
            this.bg.x = this.x + 2.0f;
            this.bg.y = this.y + 8.0f;
            this.journalIcon.x = this.bg.x + (this.bg.width() - this.journalIcon.width()) / 2.0f;
            this.journalIcon.y = this.bg.y + (this.bg.height() - this.journalIcon.height()) / 2.0f;
            PixelScene.align(this.journalIcon);
            this.keyIcon.x = this.bg.x + 1.0f;
            this.keyIcon.y = this.bg.y + 1.0f;
            this.keyIcon.width = this.bg.width - 2.0f;
            this.keyIcon.height = this.bg.height - 2.0f;
            PixelScene.align(this.keyIcon);
        }

        @Override
        public void update() {
            super.update();
            if (this.flashingPage != null) {
                this.keyIcon.am = this.journalIcon.am = (float)Math.abs(Math.cos(4.712389f * (this.time += Game.elapsed)));
                this.bg.brightness(0.5f + this.journalIcon.am);
                if ((double)this.time >= 0.6666666649796411) {
                    this.time = 0.0f;
                }
            }
        }

        public void updateKeyDisplay() {
            this.keyIcon.updateKeys();
            this.keyIcon.visible = this.keyIcon.keyCount() > 0;
            boolean bl = this.journalIcon.visible = !this.keyIcon.visible;
            if (this.keyIcon.keyCount() > 0) {
                this.bg.brightness(0.8f - (float)Math.min(6, this.keyIcon.keyCount()) / 20.0f);
            } else {
                this.bg.resetColor();
            }
        }

        @Override
        protected void onPointerDown() {
            this.bg.brightness(1.5f);
            Sample.INSTANCE.play("sounds/click.mp3");
        }

        @Override
        protected void onPointerUp() {
            if (this.keyIcon.keyCount() > 0) {
                this.bg.brightness(0.8f - (float)Math.min(6, this.keyIcon.keyCount()) / 20.0f);
            } else {
                this.bg.resetColor();
            }
        }

        @Override
        protected void onClick() {
            this.time = 0.0f;
            this.journalIcon.am = 1.0f;
            this.keyIcon.am = 1.0f;
            if (this.flashingPage != null) {
                if (this.flashingDoc == Document.ALCHEMY_GUIDE) {
                    WndJournal.last_index = 2;
                    GameScene.show(new WndJournal());
                } else if (this.flashingDoc.pageNames().contains(this.flashingPage)) {
                    if (this.flashingDoc == Document.ADVENTURERS_GUIDE) {
                        WndJournal.last_index = 1;
                    } else if (this.flashingDoc.isLoreDoc()) {
                        WndJournal.last_index = 3;
                        WndJournal.CatalogTab.currentItemIdx = 3;
                    }
                    GameScene.show(new WndStory(this.flashingDoc.pageSprite(this.flashingPage), this.flashingDoc.pageTitle(this.flashingPage), this.flashingDoc.pageBody(this.flashingPage)){

                        @Override
                        public void hide() {
                            super.hide();
                            if (SPDSettings.intro()) {
                                GameScene.endIntro();
                            }
                        }
                    });
                    this.flashingDoc.readPage(this.flashingPage);
                } else {
                    GameScene.show(new WndJournal());
                }
                this.flashingPage = null;
            } else {
                GameScene.show(new WndJournal());
            }
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "journal", new Object[0]));
        }
    }

    private static class MenuButton
    extends Button {
        private Image image;

        public MenuButton() {
            this.width = this.image.width + 4.0f;
            this.height = this.image.height + 10.0f;
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.image = new Image("interfaces/menu_button.png", 17, 2, 12, 11);
            this.add(this.image);
        }

        @Override
        protected void layout() {
            super.layout();
            this.image.x = this.x + 2.0f;
            this.image.y = this.y + 8.0f;
        }

        @Override
        protected void onPointerDown() {
            this.image.brightness(1.5f);
            Sample.INSTANCE.play("sounds/click.mp3");
        }

        @Override
        protected void onPointerUp() {
            this.image.resetColor();
        }

        @Override
        protected void onClick() {
            GameScene.show(new WndGame());
        }

        @Override
        public GameAction keyAction() {
            return GameAction.BACK;
        }

        @Override
        protected String hoverText() {
            return Messages.titleCase(Messages.get(WndKeyBindings.class, "menu", new Object[0]));
        }
    }
}

