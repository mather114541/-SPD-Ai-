/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Chill
extends FlavourBuff {
    public static final float DURATION = 10.0f;

    public Chill() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        Buff.detach(target, Burning.class);
        return super.attachTo(target);
    }

    public float speedFactor() {
        return Math.max(0.5f, 1.0f - this.cooldown() * 0.1f);
    }

    @Override
    public int icon() {
        return 15;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.CHILLED);
        } else {
            this.target.sprite.remove(CharSprite.State.CHILLED);
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(), Messages.decimalFormat("#.##", (1.0f - this.speedFactor()) * 100.0f));
    }
}

