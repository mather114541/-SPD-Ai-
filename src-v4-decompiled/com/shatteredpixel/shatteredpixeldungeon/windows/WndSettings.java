/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.services.news.News;
import com.shatteredpixel.shatteredpixeldungeon.services.updates.Updates;
import com.shatteredpixel.shatteredpixeldungeon.ui.CheckBox;
import com.shatteredpixel.shatteredpixeldungeon.ui.GameLog;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.OptionSlider;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTabbed;
import com.watabou.input.ControllerHandler;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class WndSettings
extends WndTabbed {
    private static final int WIDTH_P = 122;
    private static final int WIDTH_L = 223;
    private static final int SLIDER_HEIGHT = 21;
    private static final int BTN_HEIGHT = 16;
    private static final float GAP = 1.0f;
    private DisplayTab display;
    private UITab ui;
    private InputTab input;
    private DataTab data;
    private AudioTab audio;
    private LangsTab langs;
    public static int last_index = 0;

    public WndSettings() {
        int width = PixelScene.landscape() ? 223 : 122;
        this.display = new DisplayTab();
        this.display.setSize(width, 0.0f);
        float height = this.display.height();
        this.add(this.display);
        this.add(new WndTabbed.IconTab(Icons.get(Icons.DISPLAY)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndSettings.this.display.visible = WndSettings.this.display.active = value;
                if (value) {
                    last_index = 0;
                }
            }
        });
        this.ui = new UITab();
        this.ui.setSize(width, 0.0f);
        height = Math.max(height, this.ui.height());
        this.add(this.ui);
        this.add(new WndTabbed.IconTab(Icons.get(Icons.PREFS)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndSettings.this.ui.visible = WndSettings.this.ui.active = value;
                if (value) {
                    last_index = 1;
                }
            }
        });
        this.input = new InputTab();
        this.input.setSize(width, 0.0f);
        height = Math.max(height, this.input.height());
        if (DeviceCompat.hasHardKeyboard() || ControllerHandler.isControllerConnected()) {
            this.add(this.input);
            Image icon = ControllerHandler.controllerActive || !DeviceCompat.hasHardKeyboard() ? Icons.get(Icons.CONTROLLER) : Icons.get(Icons.KEYBOARD);
            this.add(new WndTabbed.IconTab(icon){

                @Override
                protected void select(boolean value) {
                    super.select(value);
                    WndSettings.this.input.visible = WndSettings.this.input.active = value;
                    if (value) {
                        last_index = 2;
                    }
                }
            });
        }
        this.data = new DataTab();
        this.data.setSize(width, 0.0f);
        height = Math.max(height, this.data.height());
        this.add(this.data);
        this.add(new WndTabbed.IconTab(Icons.get(Icons.DATA)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndSettings.this.data.visible = WndSettings.this.data.active = value;
                if (value) {
                    last_index = 3;
                }
            }
        });
        this.audio = new AudioTab();
        this.audio.setSize(width, 0.0f);
        height = Math.max(height, this.audio.height());
        this.add(this.audio);
        this.add(new WndTabbed.IconTab(Icons.get(Icons.AUDIO)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndSettings.this.audio.visible = WndSettings.this.audio.active = value;
                if (value) {
                    last_index = 4;
                }
            }
        });
        this.langs = new LangsTab();
        this.langs.setSize(width, 0.0f);
        height = Math.max(height, this.langs.height());
        this.add(this.langs);
        WndTabbed.IconTab langsTab = new WndTabbed.IconTab(Icons.get(Icons.LANGS)){

            @Override
            protected void select(boolean value) {
                super.select(value);
                WndSettings.this.langs.visible = WndSettings.this.langs.active = value;
                if (value) {
                    last_index = 5;
                }
            }

            @Override
            protected void createChildren() {
                super.createChildren();
                switch (Messages.lang().status()) {
                    case X_UNFINISH: {
                        this.icon.hardlight(1.5f, 0.0f, 0.0f);
                        break;
                    }
                    case __UNREVIEW: {
                        this.icon.hardlight(1.5f, 0.75f, 0.0f);
                    }
                }
            }
        };
        this.add(langsTab);
        this.resize(width, (int)Math.ceil(height));
        this.layoutTabs();
        if (this.tabs.size() == 5 && last_index >= 3) {
            this.select(last_index - 1);
        } else {
            this.select(last_index);
        }
    }

    @Override
    public void hide() {
        super.hide();
        ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback(){

            @Override
            public void beforeCreate() {
                Game.platform.resetGenerators();
            }

            @Override
            public void afterCreate() {
            }
        });
    }

    private static class DisplayTab
    extends Component {
        RenderedTextBlock title;
        ColorBlock sep1;
        CheckBox chkFullscreen;
        CheckBox chkLandscape;
        ColorBlock sep2;
        OptionSlider optBrightness;
        OptionSlider optVisGrid;
        OptionSlider optFollowIntensity;
        OptionSlider optScreenShake;

        private DisplayTab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            String fullscreenText = Messages.get(this, "fullscreen", new Object[0]);
            if (DeviceCompat.isAndroid()) {
                fullscreenText = Messages.get(this, "hide_navigation", new Object[0]);
            } else if (DeviceCompat.isiOS()) {
                fullscreenText = Messages.get(this, "hide_gesture", new Object[0]);
            }
            this.chkFullscreen = new CheckBox(fullscreenText){

                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.fullscreen(this.checked());
                }
            };
            if (Game.platform.supportsFullScreen()) {
                this.chkFullscreen.checked(SPDSettings.fullscreen());
            } else {
                this.chkFullscreen.checked(true);
                this.chkFullscreen.enable(false);
            }
            this.add(this.chkFullscreen);
            if (DeviceCompat.isAndroid()) {
                this.chkLandscape = new CheckBox(Messages.get(this, "landscape", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.landscape(this.checked());
                    }
                };
                this.chkLandscape.checked(SPDSettings.landscape());
                this.add(this.chkLandscape);
            }
            this.sep2 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep2);
            this.optBrightness = new OptionSlider(Messages.get(this, "brightness", new Object[0]), Messages.get(this, "dark", new Object[0]), Messages.get(this, "bright", new Object[0]), -1, 1){

                @Override
                protected void onChange() {
                    SPDSettings.brightness(this.getSelectedValue());
                }
            };
            this.optBrightness.setSelectedValue(SPDSettings.brightness());
            this.add(this.optBrightness);
            this.optVisGrid = new OptionSlider(Messages.get(this, "visual_grid", new Object[0]), Messages.get(this, "off", new Object[0]), Messages.get(this, "high", new Object[0]), -1, 2){

                @Override
                protected void onChange() {
                    SPDSettings.visualGrid(this.getSelectedValue());
                }
            };
            this.optVisGrid.setSelectedValue(SPDSettings.visualGrid());
            this.add(this.optVisGrid);
            this.optFollowIntensity = new OptionSlider(Messages.get(this, "camera_follow", new Object[0]), Messages.get(this, "low", new Object[0]), Messages.get(this, "high", new Object[0]), 1, 4){

                @Override
                protected void onChange() {
                    SPDSettings.cameraFollow(this.getSelectedValue());
                }
            };
            this.optFollowIntensity.setSelectedValue(SPDSettings.cameraFollow());
            this.add(this.optFollowIntensity);
            this.optScreenShake = new OptionSlider(Messages.get(this, "screenshake", new Object[0]), Messages.get(this, "off", new Object[0]), Messages.get(this, "high", new Object[0]), 0, 4){

                @Override
                protected void onChange() {
                    SPDSettings.screenShake(this.getSelectedValue());
                }
            };
            this.optScreenShake.setSelectedValue(SPDSettings.screenShake());
            this.add(this.optScreenShake);
        }

        @Override
        protected void layout() {
            float bottom = this.y;
            this.title.setPos((this.width - this.title.width()) / 2.0f, bottom + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            bottom = this.sep1.y + 1.0f;
            this.chkFullscreen.setRect(0.0f, bottom + 1.0f, this.width, 16.0f);
            bottom = this.chkFullscreen.bottom();
            if (this.chkLandscape != null) {
                this.chkLandscape.setRect(0.0f, bottom + 1.0f, this.width, 16.0f);
                bottom = this.chkLandscape.bottom();
            }
            this.sep2.size(this.width, 1.0f);
            this.sep2.y = bottom + 1.0f;
            bottom = this.sep2.y + 1.0f;
            if (this.width > 200.0f) {
                this.optBrightness.setRect(0.0f, bottom + 1.0f, this.width / 2.0f - 0.5f, 21.0f);
                this.optVisGrid.setRect(this.optBrightness.right() + 1.0f, this.optBrightness.top(), this.width / 2.0f - 0.5f, 21.0f);
                this.optFollowIntensity.setRect(0.0f, this.optVisGrid.bottom() + 1.0f, this.width / 2.0f - 0.5f, 21.0f);
                this.optScreenShake.setRect(this.optFollowIntensity.right() + 1.0f, this.optFollowIntensity.top(), this.width / 2.0f - 0.5f, 21.0f);
            } else {
                this.optBrightness.setRect(0.0f, bottom + 1.0f, this.width, 21.0f);
                this.optVisGrid.setRect(0.0f, this.optBrightness.bottom() + 1.0f, this.width, 21.0f);
                this.optFollowIntensity.setRect(0.0f, this.optVisGrid.bottom() + 1.0f, this.width, 21.0f);
                this.optScreenShake.setRect(0.0f, this.optFollowIntensity.bottom() + 1.0f, this.width, 21.0f);
            }
            this.height = this.optScreenShake.bottom();
        }
    }

    private static class UITab
    extends Component {
        RenderedTextBlock title;
        ColorBlock sep1;
        OptionSlider optUIMode;
        OptionSlider optUIScale;
        RedButton btnToolbarSettings;
        CheckBox chkFlipTags;
        ColorBlock sep2;
        CheckBox chkFont;
        CheckBox chkVibrate;

        private UITab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            float wMin = (float)Game.width / 360.0f;
            float hMin = (float)Game.height / 200.0f;
            if (Math.min(wMin, hMin) >= 2.0f * Game.density) {
                this.optUIMode = new OptionSlider(Messages.get(this, "ui_mode", new Object[0]), Messages.get(this, "mobile", new Object[0]), Messages.get(this, "full", new Object[0]), 0, 2){

                    @Override
                    protected void onChange() {
                        SPDSettings.interfaceSize(this.getSelectedValue());
                        ShatteredPixelDungeon.seamlessResetScene();
                    }
                };
                this.optUIMode.setSelectedValue(SPDSettings.interfaceSize());
                this.add(this.optUIMode);
            }
            if ((int)Math.ceil(2.0f * Game.density) < PixelScene.maxDefaultZoom) {
                this.optUIScale = new OptionSlider(Messages.get(this, "scale", new Object[0]), (int)Math.ceil(2.0f * Game.density) + "X", PixelScene.maxDefaultZoom + "X", (int)Math.ceil(2.0f * Game.density), PixelScene.maxDefaultZoom){

                    @Override
                    protected void onChange() {
                        if (this.getSelectedValue() != SPDSettings.scale()) {
                            SPDSettings.scale(this.getSelectedValue());
                            ShatteredPixelDungeon.seamlessResetScene();
                        }
                    }
                };
                this.optUIScale.setSelectedValue(PixelScene.defaultZoom);
                this.add(this.optUIScale);
            }
            if (SPDSettings.interfaceSize() == 0) {
                this.btnToolbarSettings = new RedButton(Messages.get(this, "toolbar_settings", new Object[0]), 9){

                    @Override
                    protected void onClick() {
                        ShatteredPixelDungeon.scene().addToFront(new Window(){
                            RenderedTextBlock barDesc;
                            RedButton btnSplit;
                            RedButton btnGrouped;
                            RedButton btnCentered;
                            CheckBox chkQuickSwapper;
                            RenderedTextBlock swapperDesc;
                            CheckBox chkFlipToolbar;
                            CheckBox chkFlipTags;
                            {
                                this.barDesc = PixelScene.renderTextBlock(Messages.get(this, "mode", new Object[0]), 9);
                                this.add(this.barDesc);
                                this.btnSplit = new RedButton(Messages.get(this, "split", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        this.textColor(0xFFFF44);
                                        btnGrouped.textColor(0xFFFFFF);
                                        btnCentered.textColor(0xFFFFFF);
                                        SPDSettings.toolbarMode(Toolbar.Mode.SPLIT.name());
                                        Toolbar.updateLayout();
                                    }
                                };
                                if (SPDSettings.toolbarMode().equals(Toolbar.Mode.SPLIT.name())) {
                                    this.btnSplit.textColor(0xFFFF44);
                                }
                                this.add(this.btnSplit);
                                this.btnGrouped = new RedButton(Messages.get(this, "group", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        btnSplit.textColor(0xFFFFFF);
                                        this.textColor(0xFFFF44);
                                        btnCentered.textColor(0xFFFFFF);
                                        SPDSettings.toolbarMode(Toolbar.Mode.GROUP.name());
                                        Toolbar.updateLayout();
                                    }
                                };
                                if (SPDSettings.toolbarMode().equals(Toolbar.Mode.GROUP.name())) {
                                    this.btnGrouped.textColor(0xFFFF44);
                                }
                                this.add(this.btnGrouped);
                                this.btnCentered = new RedButton(Messages.get(this, "center", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        btnSplit.textColor(0xFFFFFF);
                                        btnGrouped.textColor(0xFFFFFF);
                                        this.textColor(0xFFFF44);
                                        SPDSettings.toolbarMode(Toolbar.Mode.CENTER.name());
                                        Toolbar.updateLayout();
                                    }
                                };
                                if (SPDSettings.toolbarMode().equals(Toolbar.Mode.CENTER.name())) {
                                    this.btnCentered.textColor(0xFFFF44);
                                }
                                this.add(this.btnCentered);
                                this.chkQuickSwapper = new CheckBox(Messages.get(this, "quickslot_swapper", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        super.onClick();
                                        SPDSettings.quickSwapper(this.checked());
                                        Toolbar.updateLayout();
                                    }
                                };
                                this.chkQuickSwapper.checked(SPDSettings.quickSwapper());
                                this.add(this.chkQuickSwapper);
                                this.swapperDesc = PixelScene.renderTextBlock(Messages.get(this, "swapper_desc", new Object[0]), 5);
                                this.swapperDesc.hardlight(0x888888);
                                this.add(this.swapperDesc);
                                this.chkFlipToolbar = new CheckBox(Messages.get(this, "flip_toolbar", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        super.onClick();
                                        SPDSettings.flipToolbar(this.checked());
                                        Toolbar.updateLayout();
                                    }
                                };
                                this.chkFlipToolbar.checked(SPDSettings.flipToolbar());
                                this.add(this.chkFlipToolbar);
                                this.chkFlipTags = new CheckBox(Messages.get(this, "flip_indicators", new Object[0])){

                                    @Override
                                    protected void onClick() {
                                        super.onClick();
                                        SPDSettings.flipTags(this.checked());
                                        GameScene.layoutTags();
                                    }
                                };
                                this.chkFlipTags.checked(SPDSettings.flipTags());
                                this.add(this.chkFlipTags);
                                this.resize(122, 0);
                                this.barDesc.setPos(((float)this.width - this.barDesc.width()) / 2.0f, 1.0f);
                                PixelScene.align(this.barDesc);
                                int btnWidth = (int)((float)this.width - 2.0f) / 3;
                                this.btnSplit.setRect(0.0f, this.barDesc.bottom() + 1.0f, btnWidth, 14.0f);
                                this.btnGrouped.setRect(this.btnSplit.right() + 1.0f, this.btnSplit.top(), btnWidth, 14.0f);
                                this.btnCentered.setRect(this.btnGrouped.right() + 1.0f, this.btnSplit.top(), btnWidth, 14.0f);
                                this.chkQuickSwapper.setRect(0.0f, this.btnGrouped.bottom() + 1.0f, this.width, 16.0f);
                                this.swapperDesc.maxWidth(this.width);
                                this.swapperDesc.setPos(0.0f, this.chkQuickSwapper.bottom() + 1.0f);
                                if (this.width > 200) {
                                    this.chkFlipToolbar.setRect(0.0f, this.swapperDesc.bottom() + 1.0f, this.width / 2 - 1, 16.0f);
                                    this.chkFlipTags.setRect(this.chkFlipToolbar.right() + 1.0f, this.chkFlipToolbar.top(), this.width / 2 - 1, 16.0f);
                                } else {
                                    this.chkFlipToolbar.setRect(0.0f, this.swapperDesc.bottom() + 1.0f, this.width, 16.0f);
                                    this.chkFlipTags.setRect(0.0f, this.chkFlipToolbar.bottom() + 1.0f, this.width, 16.0f);
                                }
                                this.resize(122, (int)this.chkFlipTags.bottom());
                            }
                        });
                    }
                };
                this.add(this.btnToolbarSettings);
            } else {
                this.chkFlipTags = new CheckBox(Messages.get(this, "flip_indicators", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.flipTags(this.checked());
                        GameScene.layoutTags();
                    }
                };
                this.chkFlipTags.checked(SPDSettings.flipTags());
                this.add(this.chkFlipTags);
            }
            this.sep2 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep2);
            this.chkFont = new CheckBox(Messages.get(this, "system_font", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback(){

                        @Override
                        public void beforeCreate() {
                            SPDSettings.systemFont(this.checked());
                        }

                        @Override
                        public void afterCreate() {
                        }
                    });
                }
            };
            this.chkFont.checked(SPDSettings.systemFont());
            this.add(this.chkFont);
            this.chkVibrate = new CheckBox(Messages.get(this, "vibration", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.vibration(this.checked());
                    if (this.checked()) {
                        Game.vibrate(250);
                    }
                }
            };
            this.chkVibrate.enable(Game.platform.supportsVibration());
            if (this.chkVibrate.active) {
                this.chkVibrate.checked(SPDSettings.vibration());
            }
            this.add(this.chkVibrate);
        }

        @Override
        protected void layout() {
            this.title.setPos((this.width - this.title.width()) / 2.0f, this.y + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            this.height = this.sep1.y + 1.0f;
            if (this.optUIMode != null && this.optUIScale != null && this.width > 200.0f) {
                this.optUIMode.setRect(0.0f, this.height + 1.0f, this.width / 2.0f - 1.0f, 21.0f);
                this.optUIScale.setRect(this.width / 2.0f + 1.0f, this.height + 1.0f, this.width / 2.0f - 1.0f, 21.0f);
                this.height = this.optUIScale.bottom();
            } else {
                if (this.optUIMode != null) {
                    this.optUIMode.setRect(0.0f, this.height + 1.0f, this.width, 21.0f);
                    this.height = this.optUIMode.bottom();
                }
                if (this.optUIScale != null) {
                    this.optUIScale.setRect(0.0f, this.height + 1.0f, this.width, 21.0f);
                    this.height = this.optUIScale.bottom();
                }
            }
            if (this.btnToolbarSettings != null) {
                this.btnToolbarSettings.setRect(0.0f, this.height + 1.0f, this.width, 16.0f);
                this.height = this.btnToolbarSettings.bottom();
            } else {
                this.chkFlipTags.setRect(0.0f, this.height + 1.0f, this.width, 16.0f);
                this.height = this.chkFlipTags.bottom();
            }
            this.sep2.size(this.width, 1.0f);
            this.sep2.y = this.height + 1.0f;
            if (this.width > 200.0f) {
                this.chkFont.setRect(0.0f, this.sep2.y + 1.0f + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
                this.chkVibrate.setRect(this.chkFont.right() + 2.0f, this.chkFont.top(), this.width / 2.0f - 1.0f, 16.0f);
                this.height = this.chkVibrate.bottom();
            } else {
                this.chkFont.setRect(0.0f, this.sep2.y + 1.0f + 1.0f, this.width, 16.0f);
                this.chkVibrate.setRect(0.0f, this.chkFont.bottom() + 1.0f, this.width, 16.0f);
                this.height = this.chkVibrate.bottom();
            }
        }
    }

    private static class InputTab
    extends Component {
        RenderedTextBlock title;
        ColorBlock sep1;
        RedButton btnKeyBindings;
        RedButton btnControllerBindings;
        ColorBlock sep2;
        OptionSlider optControlSens;
        OptionSlider optHoldMoveSens;

        private InputTab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            if (DeviceCompat.hasHardKeyboard()) {
                this.btnKeyBindings = new RedButton(Messages.get(this, "key_bindings", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        ShatteredPixelDungeon.scene().addToFront(new WndKeyBindings(false));
                    }
                };
                this.add(this.btnKeyBindings);
            }
            if (ControllerHandler.isControllerConnected()) {
                this.btnControllerBindings = new RedButton(Messages.get(this, "controller_bindings", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        ShatteredPixelDungeon.scene().addToFront(new WndKeyBindings(true));
                    }
                };
                this.add(this.btnControllerBindings);
            }
            this.sep2 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep2);
            this.optControlSens = new OptionSlider(Messages.get(this, "controller_sensitivity", new Object[0]), "1", "10", 1, 10){

                @Override
                protected void onChange() {
                    SPDSettings.controllerPointerSensitivity(this.getSelectedValue());
                }
            };
            this.optControlSens.setSelectedValue(SPDSettings.controllerPointerSensitivity());
            this.add(this.optControlSens);
            this.optHoldMoveSens = new OptionSlider(Messages.get(this, "movement_sensitivity", new Object[0]), Messages.get(this, "off", new Object[0]), Messages.get(this, "high", new Object[0]), 0, 4){

                @Override
                protected void onChange() {
                    SPDSettings.movementHoldSensitivity(this.getSelectedValue());
                }
            };
            this.optHoldMoveSens.setSelectedValue(SPDSettings.movementHoldSensitivity());
            this.add(this.optHoldMoveSens);
        }

        @Override
        protected void layout() {
            this.title.setPos((this.width - this.title.width()) / 2.0f, this.y + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            this.height = this.sep1.y + 1.0f;
            if (this.width > 200.0f && this.btnKeyBindings != null && this.btnControllerBindings != null) {
                this.btnKeyBindings.setRect(0.0f, this.height + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
                this.btnControllerBindings.setRect(this.width / 2.0f + 1.0f, this.height + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
                this.height = this.btnControllerBindings.bottom();
            } else {
                if (this.btnKeyBindings != null) {
                    this.btnKeyBindings.setRect(0.0f, this.height + 1.0f, this.width, 16.0f);
                    this.height = this.btnKeyBindings.bottom();
                }
                if (this.btnControllerBindings != null) {
                    this.btnControllerBindings.setRect(0.0f, this.height + 1.0f, this.width, 16.0f);
                    this.height = this.btnControllerBindings.bottom();
                }
            }
            this.sep2.size(this.width, 1.0f);
            this.sep2.y = this.height + 1.0f;
            if (this.width > 200.0f) {
                this.optControlSens.setRect(0.0f, this.sep2.y + 1.0f + 1.0f, this.width / 2.0f - 1.0f, 21.0f);
                this.optHoldMoveSens.setRect(this.width / 2.0f + 1.0f, this.optControlSens.top(), this.width / 2.0f - 1.0f, 21.0f);
            } else {
                this.optControlSens.setRect(0.0f, this.sep2.y + 1.0f + 1.0f, this.width, 21.0f);
                this.optHoldMoveSens.setRect(0.0f, this.optControlSens.bottom() + 1.0f, this.width, 21.0f);
            }
            this.height = this.optHoldMoveSens.bottom();
        }
    }

    private static class DataTab
    extends Component {
        RenderedTextBlock title;
        ColorBlock sep1;
        CheckBox chkNews;
        CheckBox chkUpdates;
        CheckBox chkBetas;
        CheckBox chkWifi;

        private DataTab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            this.chkNews = new CheckBox(Messages.get(this, "news", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.news(this.checked());
                    News.clearArticles();
                }
            };
            this.chkNews.checked(SPDSettings.news());
            this.add(this.chkNews);
            if (Updates.supportsUpdates() && Updates.supportsUpdatePrompts()) {
                this.chkUpdates = new CheckBox(Messages.get(this, "updates", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.updates(this.checked());
                        Updates.clearUpdate();
                    }
                };
                this.chkUpdates.checked(SPDSettings.updates());
                this.add(this.chkUpdates);
                if (Updates.supportsBetaChannel()) {
                    this.chkBetas = new CheckBox(Messages.get(this, "betas", new Object[0])){

                        @Override
                        protected void onClick() {
                            super.onClick();
                            SPDSettings.betas(this.checked());
                            Updates.clearUpdate();
                        }
                    };
                    this.chkBetas.checked(SPDSettings.betas());
                    this.add(this.chkBetas);
                }
            }
            if (!DeviceCompat.isDesktop()) {
                this.chkWifi = new CheckBox(Messages.get(this, "wifi", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.WiFi(this.checked());
                    }
                };
                this.chkWifi.checked(SPDSettings.WiFi());
                this.add(this.chkWifi);
            }
        }

        @Override
        protected void layout() {
            float pos;
            this.title.setPos((this.width - this.title.width()) / 2.0f, this.y + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            if (this.width > 200.0f && this.chkUpdates != null) {
                this.chkNews.setRect(0.0f, this.sep1.y + 1.0f + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
                this.chkUpdates.setRect(this.chkNews.right() + 1.0f, this.chkNews.top(), this.width / 2.0f - 1.0f, 16.0f);
                pos = this.chkUpdates.bottom();
            } else {
                this.chkNews.setRect(0.0f, this.sep1.y + 1.0f + 1.0f, this.width, 16.0f);
                pos = this.chkNews.bottom();
                if (this.chkUpdates != null) {
                    this.chkUpdates.setRect(0.0f, this.chkNews.bottom() + 1.0f, this.width, 16.0f);
                    pos = this.chkUpdates.bottom();
                }
            }
            if (this.chkBetas != null) {
                this.chkBetas.setRect(0.0f, pos + 1.0f, this.width, 16.0f);
                pos = this.chkBetas.bottom();
            }
            if (this.chkWifi != null) {
                this.chkWifi.setRect(0.0f, pos + 1.0f, this.width, 16.0f);
                pos = this.chkWifi.bottom();
            }
            this.height = pos;
        }
    }

    private static class AudioTab
    extends Component {
        RenderedTextBlock title;
        ColorBlock sep1;
        OptionSlider optMusic;
        CheckBox chkMusicMute;
        ColorBlock sep2;
        OptionSlider optSFX;
        CheckBox chkMuteSFX;
        ColorBlock sep3;
        CheckBox chkIgnoreSilent;
        CheckBox chkMusicBG;

        private AudioTab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            this.optMusic = new OptionSlider(Messages.get(this, "music_vol", new Object[0]), "0", "10", 0, 10){

                @Override
                protected void onChange() {
                    SPDSettings.musicVol(this.getSelectedValue());
                }
            };
            this.optMusic.setSelectedValue(SPDSettings.musicVol());
            this.add(this.optMusic);
            this.chkMusicMute = new CheckBox(Messages.get(this, "music_mute", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.music(!this.checked());
                }
            };
            this.chkMusicMute.checked(!SPDSettings.music());
            this.add(this.chkMusicMute);
            this.sep2 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep2);
            this.optSFX = new OptionSlider(Messages.get(this, "sfx_vol", new Object[0]), "0", "10", 0, 10){

                @Override
                protected void onChange() {
                    SPDSettings.SFXVol(this.getSelectedValue());
                    if (Random.Int(100) == 0) {
                        Sample.INSTANCE.play("sounds/mimic.mp3");
                    } else {
                        Sample.INSTANCE.play(Random.oneOf("sounds/gold.mp3", "sounds/hit.mp3", "sounds/item.mp3", "sounds/shatter.mp3", "sounds/evoke.mp3", "sounds/secret.mp3"));
                    }
                }
            };
            this.optSFX.setSelectedValue(SPDSettings.SFXVol());
            this.add(this.optSFX);
            this.chkMuteSFX = new CheckBox(Messages.get(this, "sfx_mute", new Object[0])){

                @Override
                protected void onClick() {
                    super.onClick();
                    SPDSettings.soundFx(!this.checked());
                    Sample.INSTANCE.play("sounds/click.mp3");
                }
            };
            this.chkMuteSFX.checked(!SPDSettings.soundFx());
            this.add(this.chkMuteSFX);
            if (DeviceCompat.isiOS()) {
                this.sep3 = new ColorBlock(1.0f, 1.0f, -16777216);
                this.add(this.sep3);
                this.chkIgnoreSilent = new CheckBox(Messages.get(this, "ignore_silent", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.ignoreSilentMode(this.checked());
                    }
                };
                this.chkIgnoreSilent.checked(SPDSettings.ignoreSilentMode());
                this.add(this.chkIgnoreSilent);
            } else if (DeviceCompat.isDesktop()) {
                this.sep3 = new ColorBlock(1.0f, 1.0f, -16777216);
                this.add(this.sep3);
                this.chkMusicBG = new CheckBox(Messages.get(this, "music_bg", new Object[0])){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        SPDSettings.playMusicInBackground(this.checked());
                    }
                };
                this.chkMusicBG.checked(SPDSettings.playMusicInBackground());
                this.add(this.chkMusicBG);
            }
        }

        @Override
        protected void layout() {
            this.title.setPos((this.width - this.title.width()) / 2.0f, this.y + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            if (this.width > 200.0f) {
                this.optMusic.setRect(0.0f, this.sep1.y + 1.0f + 1.0f, this.width / 2.0f - 1.0f, 21.0f);
                this.chkMusicMute.setRect(0.0f, this.optMusic.bottom() + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
                this.sep2.size(this.width, 1.0f);
                this.sep2.y = this.sep1.y;
                this.optSFX.setRect(this.optMusic.right() + 2.0f, this.sep2.y + 1.0f + 1.0f, this.width / 2.0f - 1.0f, 21.0f);
                this.chkMuteSFX.setRect(this.chkMusicMute.right() + 2.0f, this.optSFX.bottom() + 1.0f, this.width / 2.0f - 1.0f, 16.0f);
            } else {
                this.optMusic.setRect(0.0f, this.sep1.y + 1.0f + 1.0f, this.width, 21.0f);
                this.chkMusicMute.setRect(0.0f, this.optMusic.bottom() + 1.0f, this.width, 16.0f);
                this.sep2.size(this.width, 1.0f);
                this.sep2.y = this.chkMusicMute.bottom() + 1.0f;
                this.optSFX.setRect(0.0f, this.sep2.y + 1.0f + 1.0f, this.width, 21.0f);
                this.chkMuteSFX.setRect(0.0f, this.optSFX.bottom() + 1.0f, this.width, 16.0f);
            }
            this.height = this.chkMuteSFX.bottom();
            if (this.chkIgnoreSilent != null) {
                this.sep3.size(this.width, 1.0f);
                this.sep3.y = this.chkMuteSFX.bottom() + 1.0f;
                this.chkIgnoreSilent.setRect(0.0f, this.sep3.y + 1.0f + 1.0f, this.width, 16.0f);
                this.height = this.chkIgnoreSilent.bottom();
            } else if (this.chkMusicBG != null) {
                this.sep3.size(this.width, 1.0f);
                this.sep3.y = this.chkMuteSFX.bottom() + 1.0f;
                this.chkMusicBG.setRect(0.0f, this.sep3.y + 1.0f + 1.0f, this.width, 16.0f);
                this.height = this.chkMusicBG.bottom();
            }
        }
    }

    private static class LangsTab
    extends Component {
        static final int COLS_P = 3;
        static final int COLS_L = 6;
        static final int BTN_HEIGHT = 11;
        RenderedTextBlock title;
        ColorBlock sep1;
        RenderedTextBlock txtLangInfo;
        ColorBlock sep2;
        RedButton[] lanBtns;
        ColorBlock sep3;
        RenderedTextBlock txtTranifex;
        RedButton btnCredits;

        private LangsTab() {
        }

        @Override
        protected void createChildren() {
            this.title = PixelScene.renderTextBlock(Messages.get(this, "title", new Object[0]), 9);
            this.title.hardlight(0xFFFF44);
            this.add(this.title);
            this.sep1 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep1);
            final ArrayList<Languages> langs = new ArrayList<Languages>(Arrays.asList(Languages.values()));
            Languages nativeLang = Languages.matchLocale(Locale.getDefault());
            langs.remove((Object)nativeLang);
            langs.add(0, nativeLang);
            final Languages currLang = Messages.lang();
            this.txtLangInfo = PixelScene.renderTextBlock(6);
            String info = "_" + Messages.titleCase(currLang.nativeName()) + "_ - ";
            if (currLang == Languages.ENGLISH) {
                info = info + "This is the source language, written by the developer.";
            } else if (currLang.status() == Languages.Status.O_COMPLETE) {
                info = info + Messages.get(this, "completed", new Object[0]);
            } else if (currLang.status() == Languages.Status.__UNREVIEW) {
                info = info + Messages.get(this, "unreviewed", new Object[0]);
            } else if (currLang.status() == Languages.Status.X_UNFINISH) {
                info = info + Messages.get(this, "unfinished", new Object[0]);
            }
            this.txtLangInfo.text(info);
            if (currLang.status() == Languages.Status.__UNREVIEW) {
                this.txtLangInfo.setHightlighting(true, 0xFF8800);
            } else if (currLang.status() == Languages.Status.X_UNFINISH) {
                this.txtLangInfo.setHightlighting(true, 0xFF0000);
            }
            this.add(this.txtLangInfo);
            this.sep2 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep2);
            this.lanBtns = new RedButton[langs.size()];
            for (int i = 0; i < langs.size(); ++i) {
                final int langIndex = i;
                RedButton btn = new RedButton(Messages.titleCase(langs.get(i).nativeName()), 6){

                    @Override
                    protected void onClick() {
                        super.onClick();
                        Messages.setup((Languages)((Object)langs.get(langIndex)));
                        ShatteredPixelDungeon.seamlessResetScene(new Game.SceneChangeCallback(){

                            @Override
                            public void beforeCreate() {
                                SPDSettings.language((Languages)((Object)langs.get(langIndex)));
                                GameLog.wipe();
                                Game.platform.resetGenerators();
                            }

                            @Override
                            public void afterCreate() {
                            }
                        });
                    }
                };
                if (currLang == langs.get(i)) {
                    btn.textColor(0xFFFF44);
                } else {
                    switch (langs.get(i).status()) {
                        case X_UNFINISH: {
                            btn.textColor(0x888888);
                            break;
                        }
                        case __UNREVIEW: {
                            btn.textColor(0xBBBBBB);
                        }
                    }
                }
                this.lanBtns[i] = btn;
                this.add(btn);
            }
            this.sep3 = new ColorBlock(1.0f, 1.0f, -16777216);
            this.add(this.sep3);
            this.txtTranifex = PixelScene.renderTextBlock(5);
            this.txtTranifex.text(Messages.get(this, "transifex", new Object[0]));
            this.add(this.txtTranifex);
            if (currLang != Languages.ENGLISH) {
                String credText;
                this.btnCredits = new RedButton(credText, (credText = Messages.titleCase(Messages.get(this, "credits", new Object[0]))).length() > 9 ? 6 : 9){

                    @Override
                    protected void onClick() {
                        int w;
                        int i;
                        super.onClick();
                        String[] reviewers = currLang.reviewers();
                        String[] translators = currLang.translators();
                        int totalCredits = 2 * reviewers.length + translators.length;
                        int totalTokens = 2 * totalCredits;
                        if (reviewers.length > 0) {
                            totalTokens += 6;
                        }
                        String[] entries = new String[totalTokens += 4];
                        int index = 0;
                        if (reviewers.length > 0) {
                            entries[0] = "_";
                            entries[1] = Messages.titleCase(Messages.get(this, "reviewers", new Object[0]));
                            entries[2] = "_";
                            entries[3] = "\n";
                            index = 4;
                            for (i = 0; i < reviewers.length; ++i) {
                                entries[index] = reviewers[i];
                                if (i < reviewers.length - 1) {
                                    int n = index;
                                    entries[n] = entries[n] + ", ";
                                }
                                entries[index + 1] = " ";
                                index += 2;
                            }
                            entries[index] = "\n";
                            entries[index + 1] = "\n";
                            index += 2;
                        }
                        entries[index] = "_";
                        entries[index + 1] = Messages.titleCase(Messages.get(this, "translators", new Object[0]));
                        entries[index + 2] = "_";
                        entries[index + 3] = "\n";
                        index += 4;
                        for (i = 0; i < reviewers.length; ++i) {
                            entries[index] = reviewers[i];
                            if (i < reviewers.length - 1 || translators.length > 0) {
                                int n = index;
                                entries[n] = entries[n] + ", ";
                            }
                            entries[index + 1] = " ";
                            index += 2;
                        }
                        for (i = 0; i < translators.length; ++i) {
                            entries[index] = translators[i];
                            if (i < translators.length - 1) {
                                int n = index;
                                entries[n] = entries[n] + ", ";
                            }
                            entries[index + 1] = " ";
                            index += 2;
                        }
                        Window credits = new Window(0, 0, Chrome.get(Chrome.Type.TOAST));
                        int n = w = PixelScene.landscape() ? 120 : 80;
                        if (totalCredits >= 25) {
                            w = (int)((float)w * 1.5f);
                        }
                        RenderedTextBlock title = PixelScene.renderTextBlock(9);
                        title.text(Messages.titleCase(Messages.get(this, "credits", new Object[0])), w);
                        title.hardlight(0xFFFF44);
                        title.setPos(((float)w - title.width()) / 2.0f, 0.0f);
                        credits.add(title);
                        RenderedTextBlock text = PixelScene.renderTextBlock(7);
                        text.maxWidth(w);
                        text.tokens(entries);
                        text.setPos(0.0f, title.bottom() + 4.0f);
                        credits.add(text);
                        credits.resize(w, (int)text.bottom() + 2);
                        ShatteredPixelDungeon.scene().addToFront(credits);
                    }
                };
                this.add(this.btnCredits);
            }
        }

        @Override
        protected void layout() {
            this.title.setPos((this.width - this.title.width()) / 2.0f, this.y + 1.0f);
            this.sep1.size(this.width, 1.0f);
            this.sep1.y = this.title.bottom() + 3.0f;
            this.txtLangInfo.setPos(0.0f, this.sep1.y + 1.0f + 1.0f);
            this.txtLangInfo.maxWidth((int)this.width);
            this.y = this.txtLangInfo.bottom() + 2.0f;
            int x = 0;
            this.sep2.size(this.width, 1.0f);
            this.sep2.y = this.y;
            this.y += 2.0f;
            int cols = PixelScene.landscape() ? 6 : 3;
            int btnWidth = (int)Math.floor((this.width - (float)(cols - 1)) / (float)cols);
            for (RedButton btn : this.lanBtns) {
                btn.setRect(x, this.y, btnWidth, 11.0f);
                btn.setPos(x, this.y);
                x += btnWidth + 1;
                if (!((float)(x + btnWidth) > this.width)) continue;
                x = 0;
                this.y += 12.0f;
            }
            if (x > 0) {
                this.y += 12.0f;
            }
            this.sep3.size(this.width, 1.0f);
            this.sep3.y = this.y;
            this.y += 2.0f;
            if (this.btnCredits != null) {
                this.btnCredits.setSize(this.btnCredits.reqWidth() + 2.0f, 16.0f);
                this.btnCredits.setPos(this.width - this.btnCredits.width(), this.y);
                this.txtTranifex.setPos(0.0f, this.y);
                this.txtTranifex.maxWidth((int)this.btnCredits.left());
                this.height = Math.max(this.btnCredits.bottom(), this.txtTranifex.bottom());
            } else {
                this.txtTranifex.setPos(0.0f, this.y);
                this.txtTranifex.maxWidth((int)this.width);
                this.height = this.txtTranifex.bottom();
            }
        }
    }
}

