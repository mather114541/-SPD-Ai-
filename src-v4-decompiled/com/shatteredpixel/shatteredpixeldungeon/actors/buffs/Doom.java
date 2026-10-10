/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;

public class Doom
extends Buff {
    public Doom() {
        this.type = Buff.buffType.NEGATIVE;
        this.announced = true;
    }

    @Override
    public void fx(boolean on) {
        if (on) {
            this.target.sprite.add(CharSprite.State.DARKENED);
        } else if (this.target.invisible == 0) {
            this.target.sprite.remove(CharSprite.State.DARKENED);
        }
    }

    @Override
    public int icon() {
        return 36;
    }
}

