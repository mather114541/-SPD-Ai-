/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class AdrenalineSurge
extends Buff {
    public static float DURATION = 200.0f;
    private int boost;
    private float interval;
    private static final String BOOST = "boost";
    private static final String INTERVAL = "interval";

    public AdrenalineSurge() {
        this.type = Buff.buffType.POSITIVE;
    }

    public void reset(int boost, float interval) {
        this.boost = boost;
        this.interval = interval;
        this.spend(interval - this.cooldown());
    }

    public void delay(float value) {
        this.spend(value);
    }

    public int boost() {
        return this.boost;
    }

    @Override
    public boolean act() {
        --this.boost;
        if (this.boost > 0) {
            this.spend(this.interval);
        } else {
            this.detach();
        }
        return true;
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
        return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.visualcooldown());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.boost, this.dispTurns(this.visualcooldown()));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(BOOST, this.boost);
        bundle.put(INTERVAL, this.interval);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.boost = bundle.getInt(BOOST);
        this.interval = bundle.getFloat(INTERVAL);
    }
}

