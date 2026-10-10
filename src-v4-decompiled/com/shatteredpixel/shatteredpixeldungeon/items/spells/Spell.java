/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import java.util.ArrayList;

public abstract class Spell
extends Item {
    public static final String AC_CAST = "CAST";
    protected float talentFactor = 1.0f;
    protected float talentChance = 1.0f;

    public Spell() {
        this.stackable = true;
        this.defaultAction = AC_CAST;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_CAST);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_CAST)) {
            if (curUser.buff(MagicImmune.class) != null) {
                GLog.w(Messages.get(this, "no_magic", new Object[0]), new Object[0]);
                return;
            }
            this.onCast(hero);
        }
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    protected abstract void onCast(Hero var1);
}

