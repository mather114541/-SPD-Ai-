/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.input.GameAction;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;

public class Toast
extends Component {
    private static final float MARGIN_HOR = 2.0f;
    private static final float MARGIN_VER = 2.0f;
    protected NinePatch bg;
    protected IconButton close;
    protected RenderedTextBlock text;

    public Toast(String text) {
        this.text(text);
        this.width = this.text.width() + this.close.width() + (float)this.bg.marginHor() + 6.0f;
        this.height = Math.max(this.text.height(), this.close.height()) + (float)this.bg.marginVer() + 4.0f;
    }

    @Override
    protected void createChildren() {
        super.createChildren();
        this.bg = Chrome.get(Chrome.Type.TOAST_TR);
        this.add(this.bg);
        this.close = new IconButton(Icons.get(Icons.CLOSE)){

            @Override
            protected void onClick() {
                Toast.this.onClose();
            }

            @Override
            public GameAction keyAction() {
                return GameAction.BACK;
            }
        };
        this.close.setSize(this.close.icon.width(), this.close.icon.height());
        this.add(this.close);
        this.text = PixelScene.renderTextBlock(8);
        this.add(this.text);
    }

    @Override
    protected void layout() {
        super.layout();
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.width, this.height);
        this.close.setPos(this.bg.x + this.bg.width() - (float)this.bg.marginHor() / 2.0f - 2.0f - this.close.width(), this.y + (this.height - this.close.height()) / 2.0f);
        PixelScene.align(this.close);
        this.text.setPos(this.close.left() - 2.0f - this.text.width(), this.y + (this.height - this.text.height()) / 2.0f);
        PixelScene.align(this.text);
    }

    public void text(String txt) {
        this.text.text(txt);
    }

    protected void onClose() {
    }
}

