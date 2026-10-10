/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.remains;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BowFragment;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenHilt;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.BrokenStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.CloakScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.SealShard;
import com.shatteredpixel.shatteredpixeldungeon.items.remains.TornPage;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import java.util.ArrayList;

public abstract class RemainsItem
extends Item {
    public static final String AC_USE = "USE";

    public RemainsItem() {
        this.bones = false;
        this.defaultAction = AC_USE;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_USE);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_USE)) {
            hero.sprite.operate(hero.pos);
            Catalog.countUse(this.getClass());
            this.doEffect(hero);
            hero.spendAndNext(1.0f);
            this.detach(hero.belongings.backpack);
        }
    }

    protected abstract void doEffect(Hero var1);

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int value() {
        return 50;
    }

    public static RemainsItem get(HeroClass cls) {
        switch (cls) {
            default: {
                return new SealShard();
            }
            case MAGE: {
                return new BrokenStaff();
            }
            case ROGUE: {
                return new CloakScrap();
            }
            case HUNTRESS: {
                return new BowFragment();
            }
            case DUELIST: {
                return new BrokenHilt();
            }
            case CLERIC: 
        }
        return new TornPage();
    }
}

