/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;

public abstract class InventoryClericSpell
extends ClericSpell {
    @Override
    public void onCast(final HolyTome tome, final Hero hero) {
        GameScene.selectItem(new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return InventoryClericSpell.this.inventoryPrompt();
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return InventoryClericSpell.this.preferredBag();
            }

            @Override
            public boolean itemSelectable(Item item) {
                return InventoryClericSpell.this.usableOnItem(item);
            }

            @Override
            public void onSelect(Item item) {
                InventoryClericSpell.this.onItemSelected(tome, hero, item);
            }
        });
    }

    protected String inventoryPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    protected Class<? extends Bag> preferredBag() {
        return null;
    }

    protected boolean usableOnItem(Item item) {
        return true;
    }

    protected abstract void onItemSelected(HolyTome var1, Hero var2, Item var3);
}

