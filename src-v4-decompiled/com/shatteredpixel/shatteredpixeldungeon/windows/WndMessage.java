/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;

public class WndMessage
extends Window {
    private static final int WIDTH_MIN = 120;
    private static final int WIDTH_MAX = 220;
    private static final int MARGIN = 4;

    public WndMessage(String text) {
        int width = 120;
        RenderedTextBlock info = PixelScene.renderTextBlock(text, 6);
        info.maxWidth(width - 8);
        info.setPos(4.0f, 4.0f);
        this.add(info);
        while (PixelScene.landscape() && info.height() > 120.0f && width < 220) {
            info.maxWidth((width += 20) - 8);
        }
        this.resize((int)info.width() + 8, (int)info.height() + 8);
    }
}

