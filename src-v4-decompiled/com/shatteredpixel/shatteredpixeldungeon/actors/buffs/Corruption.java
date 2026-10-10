/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.watabou.utils.Bundle;

public class Corruption
extends AllyBuff
implements Buff.DOTbuff {
    private float partialDamage;
    public static final String PARTIAL_DAMAGE = "partial_damage";

    public Corruption() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.partialDamage = 0.0f;
    }

    public static void corruptionHeal(Char target) {
        target.HP = target.HT;
        target.sprite.showStatusWithIcon(65280, Integer.toString(target.HT), FloatingText.HEALING, new Object[0]);
        for (Buff buff : target.buffs()) {
            if (buff.type != Buff.buffType.NEGATIVE || buff instanceof SoulMark) continue;
            buff.detach();
        }
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            target.needsIncomingDOTUpdate = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        this.partialDamage += (float)this.target.HT / 100.0f;
        int damage = (int)this.partialDamage;
        this.partialDamage -= (float)damage;
        if (damage > 0) {
            this.target.damage(damage, this);
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.DARKENED);
        } else if (this.target.invisible == 0) {
            this.target.sprite.remove(CharSprite.State.DARKENED);
        }
    }

    @Override
    public int icon() {
        return 36;
    }

    @Override
    public int totalIncomingDMG() {
        return this.target.HT;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PARTIAL_DAMAGE, this.partialDamage);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.partialDamage = bundle.getInt(PARTIAL_DAMAGE);
    }
}

