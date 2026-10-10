/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ScrollEmpower
extends Buff {
    private int left;
    private static final String LEFT = "left";

    public ScrollEmpower() {
        this.type = Buff.buffType.POSITIVE;
        this.left = 0;
    }

    public void reset(int left) {
        this.left = Math.max(this.left, left);
        Item.updateQuickslot();
    }

    public void use() {
        --this.left;
        if (this.left <= 0) {
            this.detach();
        }
    }

    @Override
    public void detach() {
        super.detach();
        Item.updateQuickslot();
    }

    @Override
    public int icon() {
        return 72;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.84f, 0.79f, 0.65f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (3.0f - (float)this.left) / 3.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 2, this.left);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getInt(LEFT);
    }
}

