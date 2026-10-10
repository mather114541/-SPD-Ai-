/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.RectF;
import com.watabou.utils.Signal;
import java.util.ArrayList;

public class WndTabbed
extends Window {
    protected ArrayList<Tab> tabs = new ArrayList();
    protected Tab selected;
    private Signal.Listener<KeyEvent> tabListener = new Signal.Listener<KeyEvent>(){

        @Override
        public boolean onSignal(KeyEvent keyEvent) {
            if (!keyEvent.pressed && KeyBindings.getActionForKey(keyEvent) == SPDAction.CYCLE) {
                int idx = WndTabbed.this.tabs.indexOf(WndTabbed.this.selected);
                if (++idx >= WndTabbed.this.tabs.size()) {
                    idx = 0;
                }
                WndTabbed.this.tabs.get(idx).onClick();
                return true;
            }
            return false;
        }
    };

    public WndTabbed() {
        super(0, 0, Chrome.get(Chrome.Type.TAB_SET));
        KeyEvent.addKeyListener(this.tabListener);
    }

    @Override
    public void destroy() {
        super.destroy();
        KeyEvent.removeKeyListener(this.tabListener);
    }

    protected Tab add(Tab tab) {
        tab.setPos(this.tabs.size() == 0 ? (float)(-this.chrome.marginLeft() + 1) : this.tabs.get(this.tabs.size() - 1).right(), this.height);
        tab.select(tab.selected);
        super.add(tab);
        this.tabs.add(tab);
        return tab;
    }

    public void select(int index) {
        this.select(this.tabs.get(index));
    }

    public void select(Tab tab) {
        if (tab != this.selected) {
            for (Tab t : this.tabs) {
                if (t == this.selected) {
                    t.select(false);
                    continue;
                }
                if (t != tab) continue;
                t.select(true);
            }
            this.selected = tab;
        }
    }

    @Override
    public void resize(int w, int h) {
        this.width = w;
        this.height = h;
        this.chrome.size(this.width + this.chrome.marginHor(), this.height + this.chrome.marginVer());
        this.camera.resize((int)this.chrome.width, this.chrome.marginTop() + this.height + this.tabHeight());
        this.camera.x = (int)((float)Game.width - this.camera.screenWidth()) / 2;
        this.camera.y = (int)((float)Game.height - this.camera.screenHeight()) / 2;
        this.camera.y = (int)((float)this.camera.y + (float)this.yOffset * this.camera.zoom);
        this.shadow.boxRect((float)this.camera.x / this.camera.zoom, (float)this.camera.y / this.camera.zoom, this.chrome.width(), this.chrome.height);
        for (Tab tab : this.tabs) {
            this.remove(tab);
        }
        ArrayList<Tab> tabs = new ArrayList<Tab>(this.tabs);
        this.tabs.clear();
        for (Tab tab : tabs) {
            this.add(tab);
        }
    }

    public void layoutTabs() {
        int fullWidth = this.width + this.chrome.marginHor() - 2;
        float numTabs = this.tabs.size();
        float tabWidth = ((float)fullWidth - (numTabs - 1.0f)) / numTabs;
        float pos = -this.chrome.marginLeft() + 1;
        for (Tab tab : this.tabs) {
            tab.setSize(tabWidth, this.tabHeight());
            tab.setPos(pos, this.height);
            pos = tab.right() + 1.0f;
            PixelScene.align(tab);
        }
    }

    protected int tabHeight() {
        return 25;
    }

    protected void onClick(Tab tab) {
        this.select(tab);
    }

    protected class Tab
    extends Button {
        protected final int CUT = 5;
        protected boolean selected;
        protected NinePatch bg;

        protected Tab() {
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.bg != null) {
                this.bg.x = this.x;
                this.bg.y = this.y;
                this.bg.size(this.width, this.height);
            }
        }

        protected void select(boolean value) {
            this.selected = value;
            if (this.bg != null) {
                this.remove(this.bg);
            }
            this.bg = Chrome.get(this.selected ? Chrome.Type.TAB_SELECTED : Chrome.Type.TAB_UNSELECTED);
            this.addToBack(this.bg);
            this.layout();
        }

        @Override
        protected void onClick() {
            if (!this.selected) {
                Sample.INSTANCE.play("sounds/click.mp3", 0.7f, 0.7f, 1.2f);
                WndTabbed.this.onClick(this);
            }
        }
    }

    protected class IconTab
    extends Tab {
        protected Image icon;
        private RectF defaultFrame;

        public IconTab(Image icon) {
            this.icon.copy(icon);
            this.defaultFrame = icon.frame();
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.icon = new Image();
            this.add(this.icon);
        }

        @Override
        protected void layout() {
            super.layout();
            this.icon.frame(this.defaultFrame);
            this.icon.x = this.x + (this.width - this.icon.width) / 2.0f;
            this.icon.y = this.y + (this.height - this.icon.height) / 2.0f - 1.0f;
            if (!this.selected) {
                this.icon.y -= 2.0f;
                if (this.icon.y < this.y + 5.0f) {
                    RectF frame = this.icon.frame();
                    frame.top += (this.y + 5.0f - this.icon.y) / (float)this.icon.texture.height;
                    this.icon.frame(frame);
                    this.icon.y = this.y + 5.0f;
                }
            }
            PixelScene.align(this.icon);
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            this.icon.am = this.selected ? 1.0f : 0.6f;
        }
    }

    protected class LabeledTab
    extends Tab {
        private RenderedTextBlock btLabel;

        public LabeledTab(String label) {
            this.btLabel.text(label);
        }

        @Override
        protected void createChildren() {
            super.createChildren();
            this.btLabel = PixelScene.renderTextBlock(9);
            this.add(this.btLabel);
        }

        @Override
        protected void layout() {
            super.layout();
            this.btLabel.setPos(this.x + (this.width - this.btLabel.width()) / 2.0f, this.y + (this.height - this.btLabel.height()) / 2.0f - (float)(this.selected ? 1 : 3));
            PixelScene.align(this.btLabel);
        }

        @Override
        protected void select(boolean value) {
            super.select(value);
            this.btLabel.alpha(this.selected ? 1.0f : 0.6f);
        }
    }
}

