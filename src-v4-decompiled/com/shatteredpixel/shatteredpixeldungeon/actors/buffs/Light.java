/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Light
extends FlavourBuff {
    public static final float DURATION = 250.0f;
    public static final int DISTANCE = 6;

    public Light() {
        this.type = Buff.buffType.POSITIVE;
    }

    @Override
    public boolean attachTo(Char target) {
        if (super.attachTo(target)) {
            if (Dungeon.level != null) {
                target.viewDistance = Math.max(Dungeon.level.viewDistance, 6);
                Dungeon.observe();
            }
            return true;
        }
        return false;
    }

    @Override
    public void detach() {
        this.target.viewDistance = Dungeon.level.viewDistance;
        Dungeon.observe();
        super.detach();
    }

    public void weaken(int amount) {
        this.spend(-amount);
    }

    @Override
    public int icon() {
        return 22;
    }

    @Override
    public float iconFadePercent() {
        return Math.max(0.0f, (250.0f - this.visualcooldown()) / 250.0f);
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.ILLUMINATED);
        } else {
            this.target.sprite.remove(CharSprite.State.ILLUMINATED);
        }
    }
}

