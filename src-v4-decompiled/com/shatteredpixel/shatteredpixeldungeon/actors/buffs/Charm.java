/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.watabou.utils.Bundle;

public class Charm
extends FlavourBuff {
    public int object = 0;
    public boolean ignoreHeroAllies = false;
    public static final float DURATION = 10.0f;
    private static final String OBJECT = "object";
    private static final String IGNORE_ALLIES = "ignore_allies";
    public boolean ignoreNextHit;

    public Charm() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.ignoreNextHit = false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(OBJECT, this.object);
        bundle.put(IGNORE_ALLIES, this.ignoreHeroAllies);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.object = bundle.getInt(OBJECT);
        this.ignoreHeroAllies = bundle.getBoolean(IGNORE_ALLIES);
    }

    @Override
    public int icon() {
        return 21;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (10.0f - this.visualcooldown()) / 10.0f);
    }

    public void recover(Object src) {
        if (this.ignoreHeroAllies && src instanceof Char && src != Dungeon.hero && ((Char)src).alignment == Char.Alignment.ALLY) {
            return;
        }
        if (this.ignoreNextHit) {
            this.ignoreNextHit = false;
            return;
        }
        this.spend(-5.0f);
        if (this.cooldown() <= 0.0f) {
            this.detach();
        }
    }
}

