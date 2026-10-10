/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class WndTitledMessage
extends Window {
    protected static final int WIDTH_MIN = 120;
    protected static final int WIDTH_MAX = 220;
    protected static final int GAP = 2;

    public WndTitledMessage(Image icon, String title, String message) {
        this(new IconTitle(icon, title), message);
    }

    public WndTitledMessage(Component titlebar, String message) {
        int width = 120;
        titlebar.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(titlebar);
        RenderedTextBlock text = PixelScene.renderTextBlock(6);
        if (!this.useHighlighting()) {
            text.setHightlighting(false);
        }
        text.text(message, width);
        text.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
        this.add(text);
        while (PixelScene.landscape() && text.bottom() > this.targetHeight() && width < 220) {
            titlebar.setRect(0.0f, 0.0f, width += 20, 0.0f);
            text.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
            text.maxWidth(width);
        }
        this.bringToFront(titlebar);
        this.resize(width, (int)text.bottom() + 2);
    }

    protected boolean useHighlighting() {
        return true;
    }

    protected float targetHeight() {
        return 150.0f;
    }
}

