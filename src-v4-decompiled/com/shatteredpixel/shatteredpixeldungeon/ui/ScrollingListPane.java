/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;
import java.util.Iterator;

public class ScrollingListPane
extends ScrollPane {
    private ArrayList<Component> items = new ArrayList();
    private static final int ITEM_HEIGHT = 18;

    public ScrollingListPane() {
        super(new Component());
    }

    @Override
    public void onClick(float x, float y) {
        Component item;
        Iterator<Component> iterator = this.items.iterator();
        while (!(!iterator.hasNext() || (item = iterator.next()) instanceof ListItem && ((ListItem)item).onClick(x, y))) {
        }
    }

    public void addItem(Image icon, String iconText, String text) {
        this.addItem(new ListItem(icon, iconText, text));
    }

    public void addItem(ListItem item) {
        this.content.add(item);
        this.items.add(item);
        this.layout();
    }

    public void addTitle(String text) {
        ListTitle title = new ListTitle(text);
        this.content.add(title);
        this.items.add(title);
        this.layout();
    }

    @Override
    public synchronized void clear() {
        this.content.clear();
        this.items.clear();
    }

    @Override
    protected void layout() {
        super.layout();
        float pos = 0.0f;
        for (Component item : this.items) {
            item.setRect(0.0f, pos, this.width, 18.0f);
            pos += item.height();
        }
        this.content.setSize(this.width, pos);
    }

    public static class ListItem
    extends Component {
        protected Image icon;
        protected BitmapText iconLabel;
        protected RenderedTextBlock label;
        protected ColorBlock line;

        public ListItem(Image icon, String text) {
            this(icon, null, text);
        }

        public ListItem(Image icon, String iconText, String text) {
            if (icon != null) {
                this.icon.copy(icon);
            } else {
                this.remove(this.label);
                this.label = PixelScene.renderTextBlock(9);
                this.add(this.label);
            }
            this.label.text(text);
            if (iconText != null) {
                this.iconLabel.text(iconText);
                this.iconLabel.measure();
            }
        }

        public boolean onClick(float x, float y) {
            return false;
        }

        public void hardlight(int color) {
            this.iconLabel.hardlight(color);
            this.label.hardlight(color);
        }

        public void hardlightIcon(int color) {
            this.icon.hardlight(color);
        }

        @Override
        protected void createChildren() {
            this.icon = new Image();
            this.add(this.icon);
            this.iconLabel = new BitmapText(PixelScene.pixelFont);
            this.add(this.iconLabel);
            this.label = PixelScene.renderTextBlock(7);
            this.add(this.label);
            this.line = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.line);
        }

        @Override
        protected void layout() {
            this.icon.y = this.y + 1.0f + (this.height() - 1.0f - this.icon.height()) / 2.0f;
            this.icon.x = this.x + (16.0f - this.icon.width()) / 2.0f;
            PixelScene.align(this.icon);
            this.iconLabel.x = this.icon.x + (this.icon.width - this.iconLabel.width()) / 2.0f;
            this.iconLabel.y = this.icon.y + (this.icon.height - this.iconLabel.height()) / 2.0f + 0.5f;
            PixelScene.align(this.iconLabel);
            this.line.size(this.width, 1.0f);
            this.line.x = this.x;
            this.line.y = this.y;
            this.label.maxWidth((int)(this.width - 16.0f - 1.0f));
            this.label.setPos(this.x + 17.0f, this.y + (this.height() - this.label.height()) / 2.0f);
            PixelScene.align(this.label);
        }
    }

    public static class ListTitle
    extends Component {
        protected RenderedTextBlock label;
        protected ColorBlock line;

        public ListTitle(String title) {
            this.label.text(title);
        }

        @Override
        protected void createChildren() {
            this.label = PixelScene.renderTextBlock(9);
            this.label.hardlight(0xFFFF44);
            this.add(this.label);
            this.line = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.line);
        }

        @Override
        protected void layout() {
            this.line.size(this.width, 1.0f);
            this.line.x = this.x;
            this.line.y = this.y;
            this.label.maxWidth((int)(this.width - 1.0f));
            this.label.setPos((this.width - this.label.width()) / 2.0f, this.y + (this.height() - this.label.height()) / 2.0f);
            PixelScene.align(this.label);
        }
    }
}

