/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Languages;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.TitleScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TitleBackground;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.RectF;

public class SupporterScene
extends PixelScene {
    private static final int BTN_HEIGHT = 22;
    private static final int GAP = 2;

    @Override
    public void create() {
        super.create();
        SupporterScene.uiCamera.visible = false;
        int w = Camera.main.width;
        int h = Camera.main.height;
        RectF insets = this.getCommonInsets();
        int elementWidth = PixelScene.landscape() ? 202 : 120;
        TitleBackground BG = new TitleBackground(w, h);
        this.add(BG);
        w = (int)((float)w - (insets.right + insets.left));
        h = (int)((float)h - (insets.top + insets.bottom));
        ExitButton btnExit = new ExitButton();
        btnExit.setPos(insets.left + (float)w - btnExit.width(), insets.top);
        this.add(btnExit);
        IconTitle title = new IconTitle(Icons.GOLD.get(), Messages.get(this, "title", new Object[0]));
        title.setSize(200.0f, 0.0f);
        title.setPos(insets.left + ((float)w - title.reqWidth()) / 2.0f, insets.top + (20.0f - title.height()) / 2.0f);
        SupporterScene.align(title);
        this.add(title);
        SupporterMessage msg = new SupporterMessage();
        msg.setSize(elementWidth, 0.0f);
        this.add(msg);
        StyledButton link = new StyledButton(Chrome.Type.GREY_BUTTON_TR, Messages.get(this, "supporter_link", new Object[0])){

            @Override
            protected void onClick() {
                super.onClick();
                Object link = "https://www.patreon.com/ShatteredPixel";
                link = (String)link + "?utm_source=shatteredpd";
                link = (String)link + "&utm_medium=supporter_page";
                link = (String)link + "&utm_campaign=ingame_link";
                ShatteredPixelDungeon.platform.openURI((String)link);
            }
        };
        link.icon(Icons.get(Icons.GOLD));
        link.textColor(0xFFFF44);
        link.setSize(elementWidth, 22.0f);
        this.add(link);
        float elementHeight = msg.height() + 22.0f + 2.0f;
        float top = insets.top + 16.0f + ((float)(h - 16) - elementHeight) / 2.0f;
        float left = insets.left + (float)(w - elementWidth) / 2.0f;
        msg.setPos(left, top);
        SupporterScene.align(msg);
        link.setPos(left, msg.bottom() + 2.0f);
        SupporterScene.align(link);
    }

    @Override
    protected void onBackPressed() {
        ShatteredPixelDungeon.switchNoFade(TitleScene.class);
    }

    private static class SupporterMessage
    extends Component {
        NinePatch bg;
        RenderedTextBlock text;
        Image icon;

        private SupporterMessage() {
        }

        @Override
        protected void createChildren() {
            this.bg = Chrome.get(Chrome.Type.GREY_BUTTON_TR);
            this.add(this.bg);
            Object message = Messages.get(SupporterScene.class, "intro", new Object[0]);
            message = (String)message + "\n\n" + Messages.get(SupporterScene.class, "patreon_msg", new Object[0]);
            if (Messages.lang() != Languages.ENGLISH) {
                message = (String)message + "\n" + Messages.get(SupporterScene.class, "patreon_english", new Object[0]);
            }
            message = (String)message + "\n\n- Evan";
            this.text = PixelScene.renderTextBlock((String)message, 6);
            this.add(this.text);
            this.icon = Icons.get(Icons.SHPX);
            this.add(this.icon);
        }

        @Override
        protected void layout() {
            this.bg.x = this.x;
            this.bg.y = this.y;
            this.text.maxWidth((int)this.width - this.bg.marginHor());
            this.text.setPos(this.x + (float)this.bg.marginLeft(), this.y + (float)this.bg.marginTop() + 1.0f);
            this.icon.y = this.text.bottom() - this.icon.height() + 4.0f;
            this.icon.x = this.x + 25.0f;
            this.height = this.text.bottom() + 3.0f - this.y;
            this.height += (float)this.bg.marginBottom();
            this.bg.size(this.width, this.height);
        }
    }
}

