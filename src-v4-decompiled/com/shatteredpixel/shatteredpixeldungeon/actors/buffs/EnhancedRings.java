/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.noosa.Image;

public class EnhancedRings
extends FlavourBuff {
    public EnhancedRings() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (target == Dungeon.hero) {
                ((Hero)target).updateHT(false);
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        super.detach();
        if (this.target == Dungeon.hero) {
            ((Hero)this.target).updateHT(false);
        }
    }

    @Override
    public int icon() {
        return 50;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.0f, 1.0f, 0.0f);
    }

    @Override
    public float iconFadePercent() {
        float max = 3 * Dungeon.hero.pointsInTalent(Talent.ENHANCED_RINGS);
        return Math.max(0.0f, (max - this.visualcooldown()) / max);
    }
}

