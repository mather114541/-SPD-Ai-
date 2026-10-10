/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class SwarmIntelTracker
extends Buff {
    private int alertRange = 0;
    private float leftAtZero;
    public static final String ALERT_RANGE = "alert_range";
    public static final String LEFT = "left";

    @Override
    public boolean act() {
        this.alertRange = 0;
        float lowestCooldown = 1.0f;
        for (Mob m : Dungeon.level.mobs) {
            if (this.target.fieldOfView != null && this.target.fieldOfView.length == Dungeon.level.length() && this.target.fieldOfView[m.pos]) {
                this.alertRange = Math.max(this.alertRange, m.swarmAlertRange());
            }
            if (!(m.cooldown() < lowestCooldown)) continue;
            lowestCooldown = m.cooldown();
        }
        this.leftAtZero = this.alertRange > 0 ? 5.0f : (this.leftAtZero -= this.target.cooldown());
        this.spend(lowestCooldown);
        return true;
    }

    @Override
    public int icon() {
        if (this.alertRange == 0 && this.leftAtZero <= 0.0f) {
            return 127;
        }
        return 54;
    }

    @Override
    public float iconFadePercent() {
        return (float)(12 - this.alertRange) / 12.0f;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.alertRange == 0) {
            icon.brightness(0.5f);
        } else {
            icon.resetColor();
        }
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.alertRange);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.alertRange);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ALERT_RANGE, this.alertRange);
        bundle.put(LEFT, this.leftAtZero);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.alertRange = bundle.getInt(ALERT_RANGE);
        this.leftAtZero = bundle.getInt(LEFT);
    }
}

