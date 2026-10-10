/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;

public class StyledButton
extends Button {
    protected NinePatch bg;
    protected RenderedTextBlock text;
    protected Image icon;
    public boolean leftJustify = false;
    public boolean multiline;

    public StyledButton(Chrome.Type type, String label) {
        this(type, label, 9);
    }

    public StyledButton(Chrome.Type type, String label, int size) {
        this.bg = Chrome.get(type);
        this.addToBack(this.bg);
        this.text = PixelScene.renderTextBlock(size);
        this.text.text(label);
        this.add(this.text);
    }

    @Override
    protected void layout() {
        super.layout();
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.width, this.height);
        float componentWidth = 0.0f;
        if (this.icon != null) {
            componentWidth += this.icon.width() + 2.0f;
        }
        if (this.text != null && !this.text.text().equals("")) {
            if (this.multiline) {
                this.text.maxWidth((int)(this.width - componentWidth - (float)this.bg.marginHor() - 2.0f));
            }
            this.text.setPos(this.x + (this.width() + (componentWidth += this.text.width() + 2.0f)) / 2.0f - this.text.width() - 1.0f, this.y + (this.height() - this.text.height()) / 2.0f);
            PixelScene.align(this.text);
        }
        if (this.icon != null) {
            this.icon.x = this.x + (this.width() - componentWidth) / 2.0f + 1.0f;
            this.icon.y = this.y + (this.height() - this.icon.height()) / 2.0f;
            PixelScene.align(this.icon);
        }
        if (this.leftJustify) {
            if (this.icon != null) {
                this.icon.x = this.x + (float)this.bg.marginLeft() + 1.0f;
                PixelScene.align(this.icon);
                this.text.setPos(this.icon.x + this.icon.width() + 1.0f, this.text.top());
                PixelScene.align(this.text);
            } else if (this.text != null) {
                this.text.setPos(this.x + (float)this.bg.marginLeft() + 1.0f, this.text.top());
                PixelScene.align(this.text);
            }
        }
    }

    @Override
    protected void onPointerDown() {
        this.bg.brightness(1.2f);
        Sample.INSTANCE.play("sounds/click.mp3");
    }

    @Override
    protected void onPointerUp() {
        this.bg.resetColor();
    }

    public void enable(boolean value) {
        this.active = value;
        this.text.alpha(value ? 1.0f : 0.3f);
        if (this.icon != null) {
            this.icon.alpha(value ? 1.0f : 0.3f);
        }
    }

    public void text(String value) {
        this.text.text(value);
        this.layout();
    }

    public String text() {
        return this.text.text();
    }

    public void textColor(int value) {
        this.text.hardlight(value);
    }

    public void icon(Image icon) {
        if (this.icon != null) {
            this.remove(this.icon);
        }
        this.icon = icon;
        if (this.icon != null) {
            this.add(this.icon);
            this.layout();
        }
    }

    public Image icon() {
        return this.icon;
    }

    public void alpha(float value) {
        if (this.icon != null) {
            this.icon.alpha(value);
        }
        if (this.bg != null) {
            this.bg.alpha(value);
        }
        if (this.text != null) {
            this.text.alpha(value);
        }
    }

    public float alpha() {
        if (this.icon != null) {
            return this.icon.alpha();
        }
        return this.bg.alpha();
    }

    public float reqWidth() {
        float reqWidth = 0.0f;
        if (this.icon != null) {
            reqWidth += this.icon.width() + 2.0f;
        }
        if (this.text != null && !this.text.text().equals("")) {
            reqWidth += this.text.width() + 2.0f;
        }
        return reqWidth;
    }

    public float reqHeight() {
        float reqHeight = 0.0f;
        if (this.icon != null) {
            reqHeight = Math.max(this.icon.height() + 4.0f, reqHeight);
        }
        if (this.text != null && !this.text.text().equals("")) {
            reqHeight = Math.max(this.text.height() + 4.0f, reqHeight);
        }
        return reqHeight;
    }
}

