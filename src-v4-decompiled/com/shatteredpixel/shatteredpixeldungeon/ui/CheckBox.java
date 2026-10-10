/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;

public class CheckBox
extends RedButton {
    private boolean checked = false;

    public CheckBox(String label) {
        super(label);
        this.icon(Icons.get(Icons.UNCHECKED));
    }

    @Override
    protected void layout() {
        super.layout();
        float margin = (this.height - this.text.height()) / 2.0f;
        this.text.setPos(this.x + margin, this.y + margin);
        PixelScene.align(this.text);
        margin = (this.height - this.icon.height) / 2.0f;
        this.icon.x = this.x + this.width - margin - this.icon.width;
        this.icon.y = this.y + margin;
        PixelScene.align(this.icon);
        int size = 9;
        while (this.width > 0.0f && this.text.right() > this.icon.x) {
            this.remove(this.text);
            this.text = PixelScene.renderTextBlock(this.text.text, --size);
            margin = (this.height - this.text.height()) / 2.0f;
            this.text.setPos(this.x + margin, this.y + margin);
            PixelScene.align(this.text);
            this.add(this.text);
        }
    }

    public boolean checked() {
        return this.checked;
    }

    public void checked(boolean value) {
        if (this.checked != value) {
            this.checked = value;
            this.icon.copy(Icons.get(this.checked ? Icons.CHECKED : Icons.UNCHECKED));
        }
    }

    @Override
    protected void onClick() {
        super.onClick();
        this.checked(!this.checked);
    }
}

