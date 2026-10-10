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

public class GreaterHaste
extends Buff {
    private int left;
    private static final String LEFT = "left";

    public GreaterHaste() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean act() {
        this.spendMove();
        this.spend(1.0f);
        return true;
    }

    public void spendMove() {
        --this.left;
        if (this.left <= 0) {
            this.detach();
        }
    }

    public void set(int time) {
        this.left = time;
    }

    public void extend(float duration) {
        this.left = (int)((float)this.left + duration);
    }

    @Override
    public int icon() {
        return 41;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.3f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        float duration = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.LETHAL_HASTE);
        return Math.max(0.0f, (duration - (float)this.left) / duration);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.left);
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

