/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndList
extends Window {
    private static final int WIDTH = 120;
    private static final int MARGIN = 4;
    private static final int GAP = 4;

    public WndList(String[] items) {
        float pos = 4.0f;
        float dotWidth = 0.0f;
        float maxWidth = 0.0f;
        for (int i = 0; i < items.length; ++i) {
            if (i > 0) {
                pos += 4.0f;
            }
            RenderedTextBlock item = PixelScene.renderTextBlock("-" + items[i], 6);
            item.setPos(4.0f, pos);
            item.maxWidth(112);
            this.add(item);
            pos += item.height();
            float w = item.width();
            if (!(w > maxWidth)) continue;
            maxWidth = w;
        }
        this.resize((int)(maxWidth + dotWidth + 8.0f), (int)(pos + 4.0f));
    }
}

