/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SnowParticle;
import com.watabou.noosa.Image;

public class FrostImbue
extends FlavourBuff {
    public static final float DURATION = 50.0f;

    public FrostImbue() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
        this.immunities.add(Frost.class);
        this.immunities.add(Chill.class);
    }

    public void proc(Char enemy) {
        Buff.affect(enemy, Chill.class, 3.0f);
        enemy.sprite.emitter().burst(SnowParticle.FACTORY, 3);
    }

    @Override
    public int icon() {
        return 55;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 2.0f, 3.0f);
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (50.0f - this.visualcooldown()) / 50.0f);
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Buff.detach(target, Frost.class);
            Buff.detach(target, Chill.class);
            return true;
        }
        return false;
    }
}

