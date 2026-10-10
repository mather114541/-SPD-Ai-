/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.Bundle;

public class WellFed
extends Buff {
    int left;
    private static final String LEFT = "left";

    public WellFed() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public boolean act() {
        --this.left;
        if (this.left < 0) {
            this.detach();
            if (this.target instanceof Hero) {
                ((Hero)this.target).resting = false;
            }
            return true;
        }
        if (this.left % 18 == 0 && this.target.HP < this.target.HT) {
            ++this.target.HP;
            this.target.sprite.showStatusWithIcon(65280, "1", FloatingText.HEALING, new Object[0]);
            if (this.target.HP == this.target.HT && this.target instanceof Hero) {
                ((Hero)this.target).resting = false;
            }
        }
        this.spend(1.0f / SaltCube.hungerGainMultiplier());
        return true;
    }

    public void reset() {
        this.left = 450;
        if (Dungeon.isChallenged(1)) {
            this.left /= 3;
        }
    }

    public void extend(float duration) {
        this.left = (int)((float)this.left + duration);
    }

    @Override
    public int icon() {
        return 43;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (450.0f - (float)this.left) / 450.0f);
    }

    @Override
    public String iconTextDisplay() {
        int visualLeft = (int)((float)this.left / SaltCube.hungerGainMultiplier());
        return Integer.toString(visualLeft + 1);
    }

    @Override
    public String desc() {
        int visualLeft = (int)((float)this.left / SaltCube.hungerGainMultiplier());
        return Messages.get(this, "desc", visualLeft + 1);
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

