/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;

public class Degrade
extends FlavourBuff {
    public static final float DURATION = 30.0f;

    public Degrade() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            Item.updateQuickslot();
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
        Item.updateQuickslot();
    }

    public static int reduceLevel(int level) {
        if (level <= 0) {
            return level;
        }
        return (int)Math.round(Math.sqrt(2 * (level - 1)) + 1.0);
    }

    @Override
    public int icon() {
        return 48;
    }

    @Override
    public float iconFadePercent() {
        return (30.0f - this.visualcooldown()) / 30.0f;
    }
}

