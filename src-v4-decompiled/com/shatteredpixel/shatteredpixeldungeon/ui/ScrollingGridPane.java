/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;
import java.util.Iterator;

public class ScrollingGridPane
extends ScrollPane {
    private ArrayList<Component> items = new ArrayList();
    private ArrayList<ColorBlock> separators = new ArrayList();
    private static final int ITEM_SIZE = 17;
    private static final int MIN_GROUP_SIZE = 54;

    public ScrollingGridPane() {
        super(new Component());
    }

    @Override
    public void onClick(float x, float y) {
        Component item;
        Iterator<Component> iterator = this.items.iterator();
        while (!(!iterator.hasNext() || (item = iterator.next()) instanceof GridItem && ((GridItem)item).onClick(x, y))) {
        }
    }

    public void addItem(GridItem item) {
        this.content.add(item);
        this.items.add(item);
    }

    public void addHeader(String text) {
        this.addHeader(text, 7, false);
    }

    public void addHeader(String text, int size, boolean center) {
        GridHeader header = new GridHeader(text, size, center);
        this.content.add(header);
        this.items.add(header);
    }

    @Override
    public synchronized void clear() {
        this.content.clear();
        this.items.clear();
        this.separators.clear();
    }

    @Override
    protected void layout() {
        float left = 0.0f;
        float top = 0.0f;
        int sepsUsed = 0;
        boolean freshRow = true;
        boolean lastWasSmallheader = false;
        float widthThisGroup = 0.0f;
        for (int i = 0; i < this.items.size(); ++i) {
            Component item = this.items.get(i);
            if (item instanceof GridHeader) {
                if (left > 0.0f || lastWasSmallheader) {
                    float spacing = Math.max(0.0f, 54.0f - widthThisGroup);
                    float spaceLeft = this.width() - (left + spacing);
                    int spaceReq = 0;
                    for (int j = i + 1; j < this.items.size() && this.items.get(j) instanceof GridItem; ++j) {
                        spaceReq += 18;
                    }
                    spaceReq = Math.max(spaceReq, 54);
                    if (!((GridHeader)item).center && freshRow && spaceLeft >= (float)spaceReq) {
                        ColorBlock sep;
                        left += spacing;
                        top -= item.height() + 1.0f;
                        if (this.separators.size() > sepsUsed) {
                            sep = this.separators.get(sepsUsed++);
                        } else {
                            sep = new ColorBlock(1.0f, 1.0f, -14540254);
                            this.separators.add(sep);
                            this.content.add(sep);
                            ++sepsUsed;
                        }
                        sep.size(1.0f, item.height() + 1.0f + 17.0f);
                        sep.x = left - 1.0f;
                        sep.y = top;
                    } else {
                        left = 0.0f;
                        top += 19.0f;
                        freshRow = true;
                    }
                }
                item.setRect(left, top, this.width(), item.height());
                top += item.height() + 1.0f;
                widthThisGroup = 0.0f;
                lastWasSmallheader = !((GridHeader)item).center;
            }
            if (!(item instanceof GridItem)) continue;
            if (left + 17.0f > this.width()) {
                left = 0.0f;
                widthThisGroup = 0.0f;
                top += 18.0f;
                freshRow = false;
            }
            item.setRect(left, top, 17.0f, 17.0f);
            left += 18.0f;
            widthThisGroup += 18.0f;
            lastWasSmallheader = false;
        }
        if (left > 0.0f) {
            left = 0.0f;
            top += 18.0f;
        }
        while (this.separators.size() > sepsUsed) {
            ColorBlock sep = this.separators.remove(sepsUsed);
            this.content.remove(sep);
        }
        this.content.setSize(this.width, top);
        super.layout();
    }

    public static class GridItem
    extends Component {
        protected Image icon;
        protected Visual secondIcon;
        protected ColorBlock bg;

        public GridItem(Image icon) {
            this.icon = icon instanceof ItemSprite ? new ItemSprite() : new Image();
            this.icon.copy(icon);
            this.add(this.icon);
        }

        public void addSecondIcon(Visual icon) {
            this.secondIcon = icon;
            this.add(this.secondIcon);
            this.layout();
        }

        public void hardLightBG(float r, float g, float b) {
            this.bg.hardlight(r, g, b);
        }

        public boolean onClick(float x, float y) {
            return false;
        }

        @Override
        protected void createChildren() {
            this.bg = new ColorBlock(1.0f, 1.0f, -1722591667);
            this.add(this.bg);
        }

        @Override
        protected void layout() {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.bg.size(this.width(), this.height());
            this.icon.y = this.y + (this.height() - this.icon.height()) / 2.0f;
            this.icon.x = this.x + (this.width() - this.icon.width()) / 2.0f;
            PixelScene.align(this.icon);
            if (this.secondIcon != null) {
                this.secondIcon.x = this.x + this.width() - this.secondIcon.width();
                this.secondIcon.y = this.y;
            }
        }
    }

    public static class GridHeader
    extends Component {
        protected RenderedTextBlock text;
        boolean center;

        public GridHeader(String text) {
            this(text, 7, false);
        }

        public GridHeader(String text, int size, boolean center) {
            this.center = center;
            this.text = PixelScene.renderTextBlock(text, size);
            this.add(this.text);
        }

        @Override
        protected void createChildren() {
            super.createChildren();
        }

        @Override
        protected void layout() {
            super.layout();
            if (this.center) {
                this.text.align(2);
                this.text.maxWidth((int)this.width());
                this.text.setPos(this.x + (this.width() - this.text.width()) / 2.0f, this.y + 1.0f);
            } else {
                this.text.maxWidth((int)this.width());
                this.text.setPos(this.x, this.y + 1.0f);
            }
        }

        @Override
        public float height() {
            if (this.center) {
                return this.text.height() + 3.0f;
            }
            return this.text.height() + 2.0f;
        }
    }
}

