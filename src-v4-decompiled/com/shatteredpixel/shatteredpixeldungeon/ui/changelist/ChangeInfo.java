/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui.changelist;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.changelist.ChangeButton;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.ui.Component;
import java.util.ArrayList;

public class ChangeInfo
extends Component {
    protected ColorBlock line;
    private RenderedTextBlock title;
    public boolean major;
    private RenderedTextBlock text;
    private ArrayList<ChangeButton> buttons = new ArrayList();

    public ChangeInfo(String title, boolean majorTitle, String text) {
        if (majorTitle) {
            this.title = PixelScene.renderTextBlock(title, 9);
            this.line = new ColorBlock(1.0f, 1.0f, -14540254);
            this.add(this.line);
        } else {
            this.title = PixelScene.renderTextBlock(title, 6);
            this.line = new ColorBlock(1.0f, 1.0f, -13421773);
            this.add(this.line);
        }
        this.major = majorTitle;
        this.add(this.title);
        if (text != null && !text.equals("")) {
            this.text = PixelScene.renderTextBlock(text, 6);
            this.add(this.text);
        }
    }

    public void hardlight(int color) {
        this.title.hardlight(color);
        if (this.text != null) {
            this.text.hardlight(color);
        }
    }

    public void addButton(ChangeButton button) {
        this.buttons.add(button);
        this.add(button);
        button.setSize(16.0f, 16.0f);
        this.layout();
    }

    public boolean onClick(float x, float y) {
        for (ChangeButton button : this.buttons) {
            if (!button.inside(x, y)) continue;
            button.onClick();
            return true;
        }
        return false;
    }

    @Override
    protected void layout() {
        float posY = this.y + 3.0f;
        if (this.major) {
            posY += 2.0f;
        }
        this.title.setPos(this.x + (this.width - this.title.width()) / 2.0f, posY);
        PixelScene.align(this.title);
        posY += this.title.height() + 2.0f;
        if (this.text != null) {
            this.text.maxWidth((int)this.width());
            this.text.setPos(this.x, posY);
            posY += this.text.height();
        }
        float posX = this.x;
        float tallest = 0.0f;
        for (ChangeButton change : this.buttons) {
            if (posX + change.width() >= this.right()) {
                posX = this.x;
                posY += tallest;
                tallest = 0.0f;
            }
            if (posX == this.x) {
                float offset = this.width;
                for (ChangeButton b : this.buttons) {
                    if (!((offset -= b.width()) <= 0.0f)) continue;
                    offset += b.width();
                    break;
                }
                posX += offset / 2.0f;
            }
            change.setPos(posX, posY);
            posX += change.width();
            if (!(tallest < change.height())) continue;
            tallest = change.height();
        }
        this.height = (posY += tallest + 2.0f) - this.y;
        if (this.major) {
            this.line.size(this.width(), 1.0f);
            this.line.x = this.x;
            this.line.y = this.y + 2.0f;
        } else if (this.x == 0.0f) {
            this.line.size(1.0f, this.height());
            this.line.x = this.width;
            this.line.y = this.y;
        } else {
            this.line.size(1.0f, this.height());
            this.line.x = this.x;
            this.line.y = this.y;
        }
    }
}

