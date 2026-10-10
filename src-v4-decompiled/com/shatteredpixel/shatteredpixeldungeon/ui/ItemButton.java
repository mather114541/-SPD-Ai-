/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.ui.Component;

public class ItemButton
extends Component {
    protected NinePatch bg;
    protected ItemSlot slot;

    @Override
    protected void createChildren() {
        super.createChildren();
        this.bg = Chrome.get(Chrome.Type.RED_BUTTON);
        this.add(this.bg);
        this.slot = new ItemSlot(){

            @Override
            protected void onPointerDown() {
                ItemButton.this.bg.brightness(1.2f);
                Sample.INSTANCE.play("sounds/click.mp3");
            }

            @Override
            protected void onPointerUp() {
                ItemButton.this.bg.resetColor();
            }

            @Override
            protected void onClick() {
                ItemButton.this.onClick();
            }

            @Override
            protected boolean onLongClick() {
                return ItemButton.this.onLongClick();
            }
        };
        this.slot.enable(true);
        this.add(this.slot);
    }

    protected void onClick() {
    }

    protected boolean onLongClick() {
        return false;
    }

    @Override
    protected void layout() {
        super.layout();
        this.bg.x = this.x;
        this.bg.y = this.y;
        this.bg.size(this.width, this.height);
        this.slot.setRect(this.x, this.y, this.width, this.height);
        if (this.width() >= 24.0f || this.height >= 24.0f) {
            this.slot.setMargins(2, 2, 2, 2);
        } else {
            this.slot.setMargins(1, 1, 1, 1);
        }
    }

    public Item item() {
        return this.slot.item;
    }

    public void item(Item item) {
        this.slot.item(item);
    }

    @Override
    public void clear() {
        this.slot.clear();
    }

    public ItemSlot slot() {
        return this.slot;
    }
}

