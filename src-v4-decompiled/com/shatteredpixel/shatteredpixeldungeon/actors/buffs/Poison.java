/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PoisonParticle;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class Poison
extends Buff
implements Hero.Doom,
Buff.DOTbuff {
    protected float left;
    private static final String LEFT = "left";

    public Poison() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat(LEFT);
    }

    public void set(float duration) {
        this.left = Math.max(duration, this.left);
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    public void extend(float duration) {
        this.left += duration;
        if (this.target != null) {
            this.target.needsIncomingDOTUpdate = true;
        }
    }

    public void delay(float turns) {
        this.spend(turns);
    }

    @Override
    public int icon() {
        return 3;
    }

    @Override
    public void tintIcon(Image icon) {
        icon.hardlight(0.6f, 0.2f, 0.6f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left));
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target) && target.sprite != null) {
            CellEmitter.center(target.pos).burst(PoisonParticle.SPLASH, 5);
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        this.target.needsIncomingDOTUpdate = true;
        super.detach();
    }

    @Override
    public boolean act() {
        if (this.target.isAlive()) {
            float f;
            this.target.damage((int)(this.left / 3.0f) + 1, this);
            this.spend(1.0f);
            this.left -= 1.0f;
            if (f <= 0.0f) {
                this.detach();
            }
            this.target.needsIncomingDOTUpdate = true;
        } else {
            this.detach();
        }
        return true;
    }

    @Override
    public int totalIncomingDMG() {
        int total = 0;
        for (int i = (int)Math.ceil(this.left); i > 0; --i) {
            total += i / 3 + 1;
        }
        return total;
    }

    @Override
    public void onDeath() {
        Badges.validateDeathFromPoison();
        Dungeon.fail(this);
        GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
    }
}

