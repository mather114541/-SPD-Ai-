/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.potions.brews;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import java.util.ArrayList;

public abstract class Brew
extends Potion {
    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove("DRINK");
        return actions;
    }

    @Override
    public String defaultAction() {
        return "THROW";
    }

    @Override
    public void doThrow(Hero hero) {
        GameScene.selectCell(thrower);
    }

    @Override
    public abstract void shatter(int var1);

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

