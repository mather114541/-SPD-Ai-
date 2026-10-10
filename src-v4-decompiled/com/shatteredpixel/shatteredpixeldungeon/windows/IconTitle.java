/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HealthBar;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;

public class IconTitle
extends Component {
    private static final float FONT_SIZE = 9.0f;
    private static final float GAP = 2.0f;
    protected Image imIcon;
    protected RenderedTextBlock tfLabel;
    protected HealthBar health;
    private float healthLvl = Float.NaN;

    public IconTitle() {
    }

    public IconTitle(Item item) {
        ItemSprite icon = new ItemSprite();
        this.icon(icon);
        this.label(Messages.titleCase(item.title()));
        icon.view(item);
        this.layout();
    }

    public IconTitle(Heap heap) {
        ItemSprite icon = new ItemSprite();
        this.icon(icon);
        this.label(Messages.titleCase(heap.title()));
        icon.view(heap);
        this.layout();
    }

    public IconTitle(Image icon, String label) {
        this.icon(icon);
        this.label(label);
        this.layout();
    }

    @Override
    protected void createChildren() {
        this.imIcon = new Image();
        this.add(this.imIcon);
        this.tfLabel = PixelScene.renderTextBlock(9);
        this.tfLabel.hardlight(0xFFFF44);
        this.tfLabel.setHightlighting(false);
        this.add(this.tfLabel);
        this.health = new HealthBar();
        this.add(this.health);
    }

    @Override
    protected void layout() {
        this.health.visible = !Float.isNaN(this.healthLvl);
        this.imIcon.x = this.x + Math.max(0.0f, 8.0f - this.imIcon.width() / 2.0f);
        this.imIcon.y = this.y + Math.max(0.0f, 8.0f - this.imIcon.height() / 2.0f);
        PixelScene.align(this.imIcon);
        int imWidth = (int)Math.max(this.imIcon.width(), 16.0f);
        int imHeight = (int)Math.max(this.imIcon.height(), 16.0f);
        this.tfLabel.maxWidth((int)(this.width - ((float)imWidth + 2.0f)));
        this.tfLabel.setPos(this.x + (float)imWidth + 2.0f, (float)imHeight > this.tfLabel.height() ? this.y + ((float)imHeight - this.tfLabel.height()) / 2.0f : this.y);
        PixelScene.align(this.tfLabel);
        if (this.health.visible) {
            this.health.setRect(this.tfLabel.left(), this.tfLabel.bottom(), this.tfLabel.maxWidth(), 0.0f);
            this.height = Math.max((float)imHeight, this.health.bottom());
        } else {
            this.height = Math.max((float)imHeight, this.tfLabel.height());
        }
    }

    public float reqWidth() {
        return this.imIcon.width() + this.tfLabel.width() + 2.0f;
    }

    public void icon(Image icon) {
        if (icon != null) {
            this.remove(this.imIcon);
            this.imIcon = icon;
            this.add(this.imIcon);
        }
    }

    public void label(String label) {
        this.tfLabel.text(label);
    }

    public void label(String label, int color) {
        this.tfLabel.text(label);
        this.tfLabel.hardlight(color);
    }

    public void color(int color) {
        this.tfLabel.hardlight(color);
    }

    public float alpha() {
        return this.imIcon.alpha();
    }

    public void alpha(float value) {
        this.tfLabel.alpha(value);
        this.imIcon.alpha(value);
    }

    public void health(float value) {
        this.healthLvl = value;
        this.health.level(this.healthLvl);
        this.layout();
    }
}

