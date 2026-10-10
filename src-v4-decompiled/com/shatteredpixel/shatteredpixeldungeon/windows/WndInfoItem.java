/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;

public class WndInfoItem
extends Window {
    private static final float GAP = 2.0f;
    private static final int WIDTH_MIN = 120;
    private static final int WIDTH_MAX = 220;
    private static WndInfoItem INSTANCE;

    public WndInfoItem(Heap heap) {
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
        INSTANCE = this;
        if (heap.type == Heap.Type.HEAP) {
            this.fillFields(heap.peek());
        } else {
            this.fillFields(heap);
        }
    }

    public WndInfoItem(Item item) {
        if (INSTANCE != null) {
            INSTANCE.hide();
        }
        INSTANCE = this;
        this.fillFields(item);
    }

    @Override
    public void hide() {
        super.hide();
        if (INSTANCE == this) {
            INSTANCE = null;
        }
    }

    private void fillFields(Heap heap) {
        IconTitle titlebar = new IconTitle(heap);
        titlebar.color(0xFFFF44);
        RenderedTextBlock txtInfo = PixelScene.renderTextBlock(heap.info(), 6);
        this.layoutFields(titlebar, txtInfo);
    }

    private void fillFields(Item item) {
        int color = 0xFFFF44;
        if (item.levelKnown && item.level() > 0) {
            color = 0x44FF44;
        } else if (item.levelKnown && item.level() < 0) {
            color = 0xFF4444;
        }
        IconTitle titlebar = new IconTitle(item);
        titlebar.color(color);
        RenderedTextBlock txtInfo = PixelScene.renderTextBlock(item.info(), 6);
        this.layoutFields(titlebar, txtInfo);
    }

    private void layoutFields(IconTitle title, RenderedTextBlock info) {
        int width = 120;
        info.maxWidth(width);
        while (PixelScene.landscape() && info.height() > 100.0f && width < 220) {
            info.maxWidth(width += 20);
        }
        if (this instanceof WndUseItem) {
            title.setRect(0.0f, 0.0f, width - 16, 0.0f);
        } else {
            title.setRect(0.0f, 0.0f, width, 0.0f);
        }
        this.add(title);
        info.setPos(title.left(), title.bottom() + 2.0f);
        this.add(info);
        this.resize(width, (int)(info.bottom() + 2.0f));
    }
}

