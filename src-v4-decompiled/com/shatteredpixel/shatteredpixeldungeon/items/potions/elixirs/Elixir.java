/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;

public abstract class Elixir
extends Potion {
    @Override
    public abstract void apply(Hero var1);

    @Override
    public boolean isKnown() {
        return true;
    }

    @Override
    public int value() {
        return this.quantity * 60;
    }

    @Override
    public int energyVal() {
        return this.quantity * 12;
    }
}

