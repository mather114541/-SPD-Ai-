/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class MagicalSleep
extends Buff {
    private static final float STEP = 1.0f;

    @Override
    public boolean attachTo(Char target) {
        if (!target.isImmune(Sleep.class) && super.attachTo(target)) {
            ++target.paralysed;
            if (target.alignment == Char.Alignment.ALLY) {
                if (target.HP == target.HT) {
                    if (target instanceof Hero) {
                        GLog.i(Messages.get(this, "toohealthy", new Object[0]), new Object[0]);
                    }
                    this.detach();
                    return true;
                }
                if (target instanceof Hero) {
                    GLog.i(Messages.get(this, "fallasleep", new Object[0]), new Object[0]);
                }
            }
            if (target instanceof Mob) {
                ((Mob)target).state = ((Mob)target).SLEEPING;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean act() {
        if (this.target instanceof Mob && ((Mob)this.target).state != ((Mob)this.target).SLEEPING) {
            this.detach();
            return true;
        }
        if (this.target.alignment == Char.Alignment.ALLY) {
            this.target.HP = Math.min(this.target.HP + 1, this.target.HT);
            if (this.target instanceof Hero) {
                ((Hero)this.target).resting = true;
            }
            if (this.target.HP == this.target.HT) {
                if (this.target instanceof Hero) {
                    GLog.p(Messages.get(this, "wakeup", new Object[0]), new Object[0]);
                }
                this.detach();
            }
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public void detach() {
        if (this.target.paralysed > 0) {
            --this.target.paralysed;
        }
        if (this.target instanceof Hero) {
            ((Hero)this.target).resting = false;
        } else if (this.target instanceof Mob && this.target.alignment == Char.Alignment.ALLY && ((Mob)this.target).state == ((Mob)this.target).SLEEPING) {
            ((Mob)this.target).state = ((Mob)this.target).WANDERING;
        }
        super.detach();
    }

    @Override
    public int icon() {
        return 30;
    }

    @Override
    public void fx(boolean on) {
        if (!on && this.target.paralysed <= 1) {
            this.target.sprite.remove(CharSprite.State.PARALYSED);
        }
    }
}

