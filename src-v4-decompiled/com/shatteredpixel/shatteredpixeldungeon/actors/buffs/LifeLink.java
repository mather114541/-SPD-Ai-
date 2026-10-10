/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class LifeLink
extends FlavourBuff {
    public int object = 0;
    private static final String OBJECT = "object";

    public LifeLink() {
        this.type = Buff.buffType.POSITIVE;
        this.announced = true;
    }

    @Override
    public void detach() {
        super.detach();
        Char ch = (Char)Actor.findById(this.object);
        if (!this.target.isActive() && ch != null) {
            for (LifeLink l : ch.buffs(LifeLink.class)) {
                if (l.object != this.target.id()) continue;
                l.detach();
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, this.object);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.object = bundle.getInt(OBJECT);
    }

    @Override
    public int icon() {
        return 19;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(1.0f, 0.0f, 1.0f);
    }

    @Override
    public float iconFadePercent() {
        int duration = Math.round(6.67f + 3.33f * (float)Dungeon.hero.pointsInTalent(Talent.LIFE_LINK));
        return Math.max(0.0f, ((float)duration - this.visualcooldown()) / (float)duration);
    }
}

