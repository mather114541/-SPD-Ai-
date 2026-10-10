/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;

public class FlavourBuff
extends Buff {
    @Override
    public boolean act() {
        this.detach();
        return true;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", this.dispTurns());
    }

    protected String dispTurns() {
        return this.dispTurns(this.visualcooldown());
    }

    @Override
    public String iconTextDisplay() {
        return Integer.toString((int)this.visualcooldown());
    }
}

