/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.stones;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.InventoryStone;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

public class StoneOfDetectMagic
extends InventoryStone {
    public StoneOfDetectMagic() {
        this.preferredBag = Belongings.Backpack.class;
        this.image = ItemSpriteSheet.STONE_DETECT;
    }

    @Override
    public boolean usableOnItem(Item item) {
        return !(!(item instanceof EquipableItem) && !(item instanceof Wand) || item.isIdentified() && item.cursedKnown);
    }

    @Override
    protected void onItemSelected(Item item) {
        item.cursedKnown = true;
        this.useAnimation();
        boolean negativeMagic = false;
        boolean positiveMagic = false;
        negativeMagic = item.cursed;
        if (!negativeMagic) {
            if (item instanceof Weapon && ((Weapon)item).hasCurseEnchant()) {
                negativeMagic = true;
            } else if (item instanceof Armor && ((Armor)item).hasCurseGlyph()) {
                negativeMagic = true;
            }
        }
        boolean bl = positiveMagic = item.trueLevel() > 0;
        if (!positiveMagic) {
            if (item instanceof Weapon && ((Weapon)item).hasGoodEnchant()) {
                positiveMagic = true;
            } else if (item instanceof Armor && ((Armor)item).hasGoodGlyph()) {
                positiveMagic = true;
            }
        }
        if (!positiveMagic && !negativeMagic) {
            GLog.i(Messages.get(this, "detected_none", new Object[0]), new Object[0]);
        } else if (positiveMagic && negativeMagic) {
            GLog.h(Messages.get(this, "detected_both", new Object[0]), new Object[0]);
        } else if (positiveMagic) {
            GLog.p(Messages.get(this, "detected_good", new Object[0]), new Object[0]);
        } else if (negativeMagic) {
            GLog.w(Messages.get(this, "detected_bad", new Object[0]), new Object[0]);
        }
        if (!this.anonymous) {
            curItem.detach(StoneOfDetectMagic.curUser.belongings.backpack);
            Catalog.countUse(this.getClass());
            Talent.onRunestoneUsed(curUser, StoneOfDetectMagic.curUser.pos, this.getClass());
        }
    }
}

