/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.MagicalInfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public abstract class InventorySpell
extends Spell {
    protected Class<? extends Bag> preferredBag = null;
    protected WndBag.ItemSelector itemSelector = new WndBag.ItemSelector(){

        @Override
        public String textPrompt() {
            return InventorySpell.this.inventoryTitle();
        }

        @Override
        public Class<? extends Bag> preferredBag() {
            return InventorySpell.this.preferredBag;
        }

        @Override
        public boolean itemSelectable(Item item) {
            return InventorySpell.this.usableOnItem(item);
        }

        @Override
        public void onSelect(Item item) {
            if (!(curItem instanceof InventorySpell)) {
                return;
            }
            if (item != null) {
                if (!(curItem instanceof MagicalInfusion)) {
                    curItem = InventorySpell.this.detach(curUser.belongings.backpack);
                }
                ((InventorySpell)curItem).onItemSelected(item);
                if (!(curItem instanceof MagicalInfusion)) {
                    curUser.spend(1.0f);
                    curUser.busy();
                    curUser.sprite.operate(curUser.pos);
                    Sample.INSTANCE.play("sounds/read.mp3");
                    Invisibility.dispel();
                    Catalog.countUse(curItem.getClass());
                    if (Random.Float() < ((Spell)curItem).talentChance) {
                        Talent.onScrollUsed(curUser, curUser.pos, ((Spell)curItem).talentFactor, curItem.getClass());
                    }
                }
            }
        }
    };

    @Override
    protected void onCast(Hero hero) {
        GameScene.selectItem(this.itemSelector);
    }

    private String inventoryTitle() {
        return Messages.get(this, "inv_title", new Object[0]);
    }

    protected boolean usableOnItem(Item item) {
        return true;
    }

    protected abstract void onItemSelected(Item var1);
}

