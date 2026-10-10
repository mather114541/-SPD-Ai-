/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;

public class IconButton
extends Button {
    protected Image icon;

    public IconButton() {
    }

    public IconButton(Image icon) {
        this.icon(icon);
    }

    @Override
    protected void layout() {
        super.layout();
        if (this.icon != null) {
            this.icon.x = this.x + (this.width - this.icon.width()) / 2.0f;
            this.icon.y = this.y + (this.height - this.icon.height()) / 2.0f;
            PixelScene.align(this.icon);
        }
    }

    @Override
    protected void onPointerDown() {
        if (this.icon != null) {
            this.icon.brightness(1.5f);
        }
        Sample.INSTANCE.play("sounds/click.mp3");
    }

    @Override
    protected void onPointerUp() {
        if (this.icon != null) {
            this.icon.resetColor();
        }
    }

    public void enable(boolean value) {
        this.active = value;
        if (this.icon != null) {
            this.icon.alpha(value ? 1.0f : 0.3f);
        }
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
}

