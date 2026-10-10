/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class WandEmpower
extends Buff {
    public int dmgBoost;
    public int left;
    private static final String BOOST = "boost";
    private static final String LEFT = "left";

    public WandEmpower() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 72;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 1.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (float)(3 - this.left) / 3.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dmgBoost, this.left);
    }

    public void set(int dmg, int shots) {
        this.dmgBoost = dmg;
        this.left = Math.max(this.left, shots);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BOOST, this.dmgBoost);
        bundle.put(LEFT, this.left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.dmgBoost = bundle.getInt(BOOST);
        this.left = bundle.getInt(LEFT);
    }
}

