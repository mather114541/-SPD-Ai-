/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SupporterScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;

public class WndSupportPrompt
extends Window {
    protected static final int WIDTH_P = 120;
    protected static final int WIDTH_L = 200;

    public WndSupportPrompt() {
        int width = PixelScene.landscape() ? 200 : 120;
        IconTitle title = new IconTitle(Icons.get(Icons.SHPX), Messages.get(WndSupportPrompt.class, "title", new Object[0]));
        title.setRect(0.0f, 0.0f, width, 0.0f);
        this.add(title);
        Object message = Messages.get(WndSupportPrompt.class, "intro", new Object[0]);
        message = (String)message + "\n\n" + Messages.get(SupporterScene.class, "patreon_msg", new Object[0]);
        if (Messages.lang() != Languages.ENGLISH) {
            message = (String)message + "\n" + Messages.get(SupporterScene.class, "patreon_english", new Object[0]);
        }
        message = (String)message + "\n- Evan";
        RenderedTextBlock text = PixelScene.renderTextBlock(6);
        text.text((String)message, width);
        text.setPos(title.left(), title.bottom() + 4.0f);
        this.add(text);
        RedButton link = new RedButton(Messages.get(SupporterScene.class, "supporter_link", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                Object link = "https://www.patreon.com/ShatteredPixel";
                link = (String)link + "?utm_source=shatteredpd";
                link = (String)link + "&utm_medium=supporter_prompt";
                link = (String)link + "&utm_campaign=ingame_link";
                ShatteredPixelDungeon.platform.openURI((String)link);
                SPDSettings.supportNagged(true);
                WndSupportPrompt.super.hide();
            }
        };
        link.setRect(0.0f, text.bottom() + 4.0f, width, 18.0f);
        this.add(link);
        RedButton close = new RedButton(Messages.get(this, "close", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                SPDSettings.supportNagged(true);
                WndSupportPrompt.super.hide();
            }
        };
        close.setRect(0.0f, link.bottom() + 2.0f, width, 18.0f);
        this.add(close);
        this.resize(width, (int)close.bottom());
    }

    @Override
    public void hide() {
    }
}

