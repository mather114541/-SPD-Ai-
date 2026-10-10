/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;

public class Ooze
extends Buff
implements Buff.DOTbuff {
    public static final float DURATION = 20.0f;
    private float left;
    private boolean acted;
    private static final String LEFT = "left";
    private static final String ACTED = "acted";

    public Ooze() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
        this.acted = false;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEFT, this.left);
        bundle.put(ACTED, this.acted);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat(LEFT);
        this.acted = bundle.getBoolean(ACTED);
    }

    @Override
    public int icon() {
        return 8;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (20.0f - this.left) / 20.0f);
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.left);
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns(this.left));
    }

    public void set(float left) {
        this.left = left;
        this.acted = false;
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

    @Override
    public boolean act() {
        if (this.acted && Dungeon.level.water[this.target.pos] && !this.target.flying) {
            this.detach();
        } else if (this.target.isAlive()) {
            this.acted = true;
            if (Dungeon.scalingDepth() > 5) {
                this.target.damage(1 + Dungeon.scalingDepth() / 5, this);
            } else if (Dungeon.scalingDepth() == 5) {
                this.target.damage(1, this);
            } else if ((int)this.left % 2 == 0) {
                this.target.damage(1, this);
            }
            if (!this.target.isAlive() && this.target == Dungeon.hero) {
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "ondeath", new Object[0]), new Object[0]);
            }
            this.spend(1.0f);
            this.left -= 1.0f;
            if (this.left <= 0.0f) {
                this.detach();
            }
        } else {
            this.detach();
        }
        if (Dungeon.level.water[this.target.pos] && !this.target.flying) {
            this.detach();
        }
        this.target.needsIncomingDOTUpdate = true;
        return true;
    }

    @Override
    public void detach() {
        this.target.needsIncomingDOTUpdate = true;
        super.detach();
    }

    @Override
    public int totalIncomingDMG() {
        if (Dungeon.scalingDepth() > 5) {
            int dmg = 1 + Dungeon.scalingDepth() / 5;
            return (int)(Math.ceil(this.left) * (double)dmg);
        }
        if (Dungeon.scalingDepth() == 5) {
            return (int)Math.ceil(this.left);
        }
        return (int)(Math.ceil(this.left) / 2.0);
    }
}

