/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;

public class Healing
extends Buff {
    private int healingLeft;
    private float percentHealPerTick;
    private int flatHealPerTick;
    private int bloodVialLimitingLevel = -1;
    private static final String LEFT = "left";
    private static final String PERCENT = "percent";
    private static final String FLAT = "flat";
    private static final String VIAL_LEVEL = "vial_level";

    public Healing() {
        this.actPriority = -1;
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean act() {
        if (this.target.HP < this.target.HT) {
            this.target.HP = Math.min(this.target.HT, this.target.HP + this.healingThisTick());
            if (this.target.HP == this.target.HT && this.target instanceof Hero) {
                ((Hero)this.target).resting = false;
            }
        }
        if (!rl.RLMode.headless) {
            this.target.sprite.showStatusWithIcon(65280, Integer.toString(this.healingThisTick()), FloatingText.HEALING, new Object[0]);
        }
        this.healingLeft -= this.healingThisTick();
        if (this.healingLeft <= 0) {
            if (this.target instanceof Hero) {
                ((Hero)this.target).resting = false;
            }
            this.detach();
        }
        this.spend(1.0f);
        return true;
    }

    private int healingThisTick() {
        int heal = (int)GameMath.gate(1.0f, Math.round((float)this.healingLeft * this.percentHealPerTick) + this.flatHealPerTick, this.healingLeft);
        if (this.bloodVialLimitingLevel != -1 && heal > VialOfBlood.maxHealPerTurn(this.bloodVialLimitingLevel)) {
            heal = VialOfBlood.maxHealPerTurn(this.bloodVialLimitingLevel);
        }
        return heal;
    }

    public void setHeal(int amount, float percentPerTick, int flatPerTick) {
        this.setHeal(amount, percentPerTick, flatPerTick, false);
    }

    public void setHeal(int amount, float percentPerTick, int flatPerTick, boolean applyVialEffect) {
        if (applyVialEffect && VialOfBlood.delayBurstHealing()) {
            amount = Math.round((float)amount * VialOfBlood.totalHealMultiplier());
            this.bloodVialLimitingLevel = VialOfBlood.bloodVialLevel();
        } else {
            this.bloodVialLimitingLevel = -1;
        }
        this.healingLeft = Math.max(this.healingLeft, amount);
        this.percentHealPerTick = Math.max(this.percentHealPerTick, percentPerTick);
        this.flatHealPerTick = Math.max(this.flatHealPerTick, flatPerTick);
    }

    public void increaseHeal(int amount) {
        this.healingLeft += amount;
    }

    @Override
    public void fx(boolean on) {
        if (rl.RLMode.headless) return;
        if (on) {
            this.target.sprite.add(CharSprite.State.HEALING);
        } else {
            this.target.sprite.remove(CharSprite.State.HEALING);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.healingLeft);
        bundle.put(PERCENT, this.percentHealPerTick);
        bundle.put(FLAT, this.flatHealPerTick);
        bundle.put(VIAL_LEVEL, this.bloodVialLimitingLevel);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.healingLeft = bundle.getInt(LEFT);
        this.percentHealPerTick = bundle.getFloat(PERCENT);
        this.flatHealPerTick = bundle.getInt(FLAT);
        if (bundle.contains(VIAL_LEVEL)) {
            this.bloodVialLimitingLevel = bundle.getInt(VIAL_LEVEL);
        }
    }

    @Override
    public int icon() {
        return 44;
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.healingLeft);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.healingThisTick(), this.healingLeft);
    }
}

