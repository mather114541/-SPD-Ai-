/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class WndInfoBuff
extends Window {
    private static final float GAP = 2.0f;
    private static final int WIDTH = 120;

    public WndInfoBuff(Buff buff) {
        IconTitle titlebar = new IconTitle();
        BuffIcon buffIcon = new BuffIcon(buff, true);
        titlebar.icon(buffIcon);
        titlebar.label(Messages.titleCase(buff.name()), 0xFFFF44);
        titlebar.setRect(0.0f, 0.0f, 120.0f, 0.0f);
        this.add(titlebar);
        RenderedTextBlock txtInfo = PixelScene.renderTextBlock(buff.desc(), 6);
        txtInfo.maxWidth(120);
        txtInfo.setPos(titlebar.left(), titlebar.bottom() + 4.0f);
        this.add(txtInfo);
        this.resize(120, (int)txtInfo.bottom() + 2);
    }
}

