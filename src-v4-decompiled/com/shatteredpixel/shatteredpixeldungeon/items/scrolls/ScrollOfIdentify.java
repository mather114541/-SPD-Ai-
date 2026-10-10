/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.effects.Identification;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class ScrollOfIdentify
extends InventoryScroll {
    public ScrollOfIdentify() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_IDENTIFY;
        this.bones = true;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return !item.isIdentified();
    }

    @Override
    protected void onItemSelected(Item item) {
        ScrollOfIdentify.curUser.sprite.parent.add(new Identification(ScrollOfIdentify.curUser.sprite.center().offset(0.0f, -16.0f)));
        ScrollOfIdentify.IDItem(item);
    }

    public static void IDItem(Item item) {
        if (ShardOfOblivion.passiveIDDisabled()) {
            if (item instanceof Weapon) {
                ((Weapon)item).setIDReady();
                GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), item.name());
                return;
            }
            if (item instanceof Armor) {
                ((Armor)item).setIDReady();
                GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), item.name());
                return;
            }
            if (item instanceof Ring) {
                ((Ring)item).setIDReady();
                GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), item.name());
                return;
            }
            if (item instanceof Wand) {
                ((Wand)item).setIDReady();
                GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), item.name());
                return;
            }
        }
        item.identify();
        GLog.i(Messages.get(ScrollOfIdentify.class, "it_is", item.title()), new Object[0]);
        Badges.validateItemLevelAquired(item);
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

