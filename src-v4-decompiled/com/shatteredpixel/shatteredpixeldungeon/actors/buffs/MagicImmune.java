/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.watabou.noosa.Image;

public class MagicImmune
extends FlavourBuff {
    public static final float DURATION = 20.0f;

    public MagicImmune() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
        this.immunities.addAll(AntiMagic.RESISTS);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            block0: for (Buff b : target.buffs()) {
                for (Class immunity : this.immunities) {
                    if (!b.getClass().isAssignableFrom(immunity)) continue;
                    b.detach();
                    continue block0;
                }
            }
            if (target instanceof Hero) {
                ((Hero)target).updateHT(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        if (this.target instanceof Hero) {
            ((Hero)this.target).updateHT(false);
        }
    }

    @Override
    public int icon() {
        return 17;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 1.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
    }
}

