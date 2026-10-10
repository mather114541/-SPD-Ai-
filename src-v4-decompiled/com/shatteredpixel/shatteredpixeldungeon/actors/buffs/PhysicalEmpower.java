/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class PhysicalEmpower
extends Buff {
    public int dmgBoost;
    public int left;
    private static final String BOOST = "boost";
    private static final String LEFT = "left";

    public PhysicalEmpower() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public int icon() {
        return 50;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.5f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        float max = 1 + Dungeon.hero.pointsInTalent(Talent.STRENGTHENING_MEAL);
        return Math.max(0.0f, (max - (float)this.left) / max);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dmgBoost, this.left);
    }

    public void set(int dmg, int hits) {
        if (dmg * hits > this.dmgBoost * this.left) {
            this.dmgBoost = dmg;
            this.left = hits;
        }
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

