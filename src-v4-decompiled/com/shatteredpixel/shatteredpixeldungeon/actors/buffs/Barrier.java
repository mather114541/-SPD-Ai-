/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.HoldFast;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Barrier
extends ShieldBuff {
    float partialLostShield;
    private static final String PARTIAL_LOST_SHIELD = "partial_lost_shield";

    public Barrier() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public void incShield(int amt) {
        super.incShield(amt);
        this.partialLostShield = 0.0f;
    }

    @Override
    public void setShield(int shield) {
        super.setShield(shield);
        if (this.shielding() == shield) {
            this.partialLostShield = 0.0f;
        }
    }

    @Override
    public boolean act() {
        this.partialLostShield += Math.min(1.0f, (float)this.shielding() / 20.0f) * HoldFast.buffDecayFactor(this.target);
        if (this.partialLostShield >= 1.0f) {
            this.absorbDamage(1);
            this.partialLostShield = 0.0f;
        }
        if (this.shielding() <= 0) {
            this.detach();
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.SHIELDED);
        } else if (this.target.buff(Blocking.BlockBuff.class) == null) {
            this.target.sprite.remove(CharSprite.State.SHIELDED);
        }
    }

    @Override
    public int icon() {
        return 20;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.5f, 1.0f, 2.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString(this.shielding());
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.shielding());
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTIAL_LOST_SHIELD, this.partialLostShield);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.partialLostShield = bundle.getFloat(PARTIAL_LOST_SHIELD);
    }
}

