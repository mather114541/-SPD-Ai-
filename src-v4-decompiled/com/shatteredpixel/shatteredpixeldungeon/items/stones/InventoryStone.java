/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import java.util.ArrayList;

public abstract class InventoryStone
extends Runestone {
    public static final String AC_USE = "USE";
    protected Class<? extends Bag> preferredBag;
    protected WndBag.ItemSelector itemSelector;

    public InventoryStone() {
        this.defaultAction = AC_USE;
        this.preferredBag = null;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return InventoryStone.this.inventoryTitle();
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return InventoryStone.this.preferredBag;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return InventoryStone.this.usableOnItem(item);
            }

            @Override
            public void onSelect(Item item) {
                if (!(curItem instanceof InventoryStone)) {
                    return;
                }
                if (item != null) {
                    ((InventoryStone)curItem).onItemSelected(item);
                }
            }
        };
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
        if (action.equals(AC_USE) && hero.buff(MagicImmune.class) == null) {
            this.activate(InventoryStone.curUser.pos);
        }
    }

    @Override
    protected void activate(int cell) {
        GameScene.selectItem(this.itemSelector);
    }

    public void directActivate() {
        GameScene.selectItem(this.itemSelector);
    }

    protected void useAnimation() {
        curUser.spend(1.0f);
        curUser.busy();
        InventoryStone.curUser.sprite.operate(InventoryStone.curUser.pos);
        Sample.INSTANCE.play("sounds/read.mp3");
        Invisibility.dispel();
    }

    private String inventoryTitle() {
        return Messages.get(this, "inv_title", new Object[0]);
    }

    protected boolean usableOnItem(Item item) {
        return true;
    }

    protected abstract void onItemSelected(Item var1);
}

