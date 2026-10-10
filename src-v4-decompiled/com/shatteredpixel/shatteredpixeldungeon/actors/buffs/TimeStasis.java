/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class TimeStasis
extends FlavourBuff {
    public TimeStasis() {
        this.type = Buff.buffType.POSITIVE;
        this.actPriority = -33;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            ++target.invisible;
            ++target.paralysed;
            target.next();
            if (Dungeon.hero != null) {
                Dungeon.observe();
            }
            return true;
        }
        return false;
    }

    @Override
    protected void spend(float time) {
        super.spend(time);
        Hunger hunger = Buff.affect(this.target, Hunger.class);
        if (hunger != null && !hunger.isStarving()) {
            hunger.affectHunger(this.cooldown(), true);
        }
    }

    @Override
    public void detach() {
        if (this.target.invisible > 0) {
            --this.target.invisible;
        }
        if (this.target.paralysed > 0) {
            --this.target.paralysed;
        }
        super.detach();
        Dungeon.observe();
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.PARALYSED);
        } else {
            if (this.target.paralysed == 0) {
                this.target.sprite.remove(CharSprite.State.PARALYSED);
            }
            if (this.target.invisible == 0) {
                this.target.sprite.remove(CharSprite.State.INVISIBLE);
            }
        }
    }
}

