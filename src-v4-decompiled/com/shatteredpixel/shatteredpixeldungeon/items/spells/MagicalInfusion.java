/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.spells;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Recipe;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.InventorySpell;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Random;

public class MagicalInfusion
extends InventorySpell {
    public MagicalInfusion() {
        this.image = ItemSpriteSheet.MAGIC_INFUSE;
        this.unique = true;
        this.talentFactor = 2.0f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item.isUpgradable();
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndUpgrade(this, item, false));
    }

    public void reShowSelector() {
        curItem = this;
        GameScene.selectItem(this.itemSelector);
    }

    public WndBag.ItemSelector getSelector() {
        curItem = this;
        return this.itemSelector;
    }

    public void useAnimation() {
        curUser.spend(1.0f);
        curUser.busy();
        MagicalInfusion.curUser.sprite.operate(MagicalInfusion.curUser.pos);
        Sample.INSTANCE.play("sounds/read.mp3");
        Invisibility.dispel();
        Catalog.countUse(curItem.getClass());
        if (Random.Float() < ((Spell)MagicalInfusion.curItem).talentChance) {
            Talent.onScrollUsed(curUser, MagicalInfusion.curUser.pos, ((Spell)MagicalInfusion.curItem).talentFactor, this.getClass());
        }
    }

    public Item upgradeItem(Item item) {
        ScrollOfUpgrade.upgrade(curUser);
        Degrade.detach(curUser, Degrade.class);
        if (item instanceof Weapon && ((Weapon)item).enchantment != null) {
            item = ((Weapon)item).upgrade(true);
        } else if (item instanceof Armor && ((Armor)item).glyph != null) {
            item = ((Armor)item).upgrade(true);
        } else {
            boolean wasCursed = item.cursed;
            boolean wasCurseInfused = item instanceof Wand && ((Wand)item).curseInfusionBonus;
            item = item.upgrade();
            if (wasCursed) {
                item.cursed = true;
            }
            if (wasCurseInfused) {
                ((Wand)item).curseInfusionBonus = true;
            }
        }
        GLog.p(Messages.get(this, "infuse", new Object[0]), new Object[0]);
        Badges.validateItemLevelAquired(item);
        Catalog.countUse(item.getClass());
        ++Statistics.upgradesUsed;
        return item;
    }

    @Override
    public int value() {
        return 60 * this.quantity;
    }

    @Override
    public int energyVal() {
        return 12 * this.quantity;
    }

    public static class Recipe
    extends Recipe.SimpleRecipe {
        public Recipe() {
            this.inputs = new Class[]{ScrollOfUpgrade.class};
            this.inQuantity = new int[]{1};
            this.cost = 12;
            this.output = MagicalInfusion.class;
            this.outQuantity = 1;
        }
    }
}

