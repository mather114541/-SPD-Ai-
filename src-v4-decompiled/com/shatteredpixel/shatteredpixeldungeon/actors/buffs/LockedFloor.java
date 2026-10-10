/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.watabou.utils.Bundle;

public class LockedFloor
extends Buff {
    private float left = Dungeon.isChallenged(256) ? 20.0f : 50.0f;
    private final String LEFT = "left";

    @Override
    public boolean act() {
        this.spend(1.0f);
        if (!Dungeon.level.locked) {
            this.detach();
        }
        if (this.left >= 1.0f) {
            this.left -= 1.0f;
        }
        return true;
    }

    public void addTime(float time) {
        this.left += time;
        this.left = Math.min(this.left, 50.0f);
    }

    public void removeTime(float time) {
        this.left -= time;
    }

    public boolean regenOn() {
        return this.left >= 1.0f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("left", this.left);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.left = bundle.getFloat("left");
    }

    @Override
    public int icon() {
        return 35;
    }
}

