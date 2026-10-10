/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

public abstract class Runestone
extends Item {
    protected boolean anonymous;

    public Runestone() {
        this.stackable = true;
        this.defaultAction = "THROW";
        this.anonymous = false;
    }

    public void anonymize() {
        this.image = ItemSpriteSheet.STONE_HOLDER;
        this.anonymous = true;
    }

    @Override
    protected void onThrow(int cell) {
        if (this instanceof InventoryStone || Dungeon.hero.buff(MagicImmune.class) != null || Dungeon.level.pit[cell] && Actor.findChar(cell) == null) {
            if (!this.anonymous) {
                super.onThrow(cell);
            }
        } else {
            if (!this.anonymous) {
                Catalog.countUse(this.getClass());
                Talent.onRunestoneUsed(curUser, cell, this.getClass());
            }
            this.activate(cell);
            if (Actor.findChar(cell) == null) {
                Dungeon.level.pressCell(cell);
            }
            Invisibility.dispel();
        }
    }

    protected abstract void activate(int var1);

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public boolean isIdentified() {
        return true;
    }

    @Override
    public int value() {
        return 15 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 3 * this.quantity;
    }

    public static class PlaceHolder
    extends Runestone {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.STONE_HOLDER;
        }

        @Override
        protected void activate(int cell) {
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof Runestone;
        }

        @Override
        public String info() {
            return "";
        }
    }
}

