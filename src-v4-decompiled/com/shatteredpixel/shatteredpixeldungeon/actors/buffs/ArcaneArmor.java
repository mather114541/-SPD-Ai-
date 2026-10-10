/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class ArcaneArmor
extends Buff {
    private int level;
    private int interval;
    private static final String LEVEL = "level";
    private static final String INTERVAL = "interval";

    public ArcaneArmor() {
        this.type = Buff.buffType.POSITIVE;
        this.level = 0;
        this.interval = 1;
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            this.spend(this.interval);
            if (--this.level <= 0) {
                this.detach();
            }
        } else {
            this.detach();
        }
        return true;
    }

    public int level() {
        return this.level;
    }

    public void set(int value, int time) {
        if (Math.sqrt(this.interval) * (double)this.level < Math.sqrt(time) * (double)value) {
            this.level = value;
            this.interval = time;
            this.spend((float)time - this.cooldown() - 1.0f);
        }
    }

    public void delay(float value) {
        this.spend(value);
    }

    @Override
    public int icon() {
        return 20;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.5f, 2.0f);
    }

    @Override
    public float iconFadePercent() {
        if (this.target instanceof Hero) {
            float max = ((Hero)this.target).lvl / 2 + 5;
            return (max - (float)this.level) / max;
        }
        return 0.0f;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.level);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.level, this.dispTurns(this.visualcooldown()));
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(INTERVAL, this.interval);
        bundle.put(LEVEL, this.level);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.interval = bundle.getInt(INTERVAL);
        this.level = bundle.getInt(LEVEL);
    }
}

