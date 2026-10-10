/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.Enchanting;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class StoneOfEnchantment
extends InventoryStone {
    public StoneOfEnchantment() {
        this.preferredBag = Belongings.Backpack.class;
        this.image = ItemSpriteSheet.STONE_ENCHANT;
        this.unique = true;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return ScrollOfEnchantment.enchantable(item);
    }

    @Override
    protected void onItemSelected(Item item) {
        if (!this.anonymous) {
            curItem.detach(StoneOfEnchantment.curUser.belongings.backpack);
            Catalog.countUse(this.getClass());
            Talent.onRunestoneUsed(curUser, StoneOfEnchantment.curUser.pos, this.getClass());
        }
        if (item instanceof Weapon) {
            ((Weapon)item).enchant();
        } else {
            ((Armor)item).inscribe();
        }
        StoneOfEnchantment.curUser.sprite.emitter().start(Speck.factory(2), 0.1f, 5);
        Enchanting.show(curUser, item);
        if (item instanceof Weapon) {
            GLog.p(Messages.get(this, "weapon", new Object[0]), new Object[0]);
        } else {
            GLog.p(Messages.get(this, "armor", new Object[0]), new Object[0]);
        }
        this.useAnimation();
    }

    @Override
    public int value() {
        return 30 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 5 * this.quantity;
    }
}

