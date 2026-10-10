/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUpgrade;

public class ScrollOfUpgrade
extends InventoryScroll {
    public ScrollOfUpgrade() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_UPGRADE;
        this.preferredBag = Belongings.Backpack.class;
        this.unique = true;
        this.talentFactor = 2.0f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return item.isUpgradable();
    }

    @Override
    protected void onItemSelected(Item item) {
        GameScene.show(new WndUpgrade(this, item, identifiedByUse));
    }

    public void reShowSelector(boolean force) {
        identifiedByUse = force;
        curItem = this;
        GameScene.selectItem(this.itemSelector);
    }

    public WndBag.ItemSelector getSelector(boolean force) {
        identifiedByUse = force;
        curItem = this;
        return this.itemSelector;
    }

    public Item upgradeItem(Item item) {
        ScrollOfUpgrade.upgrade(curUser);
        Degrade.detach(curUser, Degrade.class);
        if (item instanceof Weapon) {
            Weapon w = (Weapon)item;
            boolean wasCursed = w.cursed;
            boolean wasHardened = w.enchantHardened;
            boolean hadCursedEnchant = w.hasCurseEnchant();
            boolean hadGoodEnchant = w.hasGoodEnchant();
            item = w.upgrade();
            if (w.cursedKnown && hadCursedEnchant && !w.hasCurseEnchant()) {
                ScrollOfUpgrade.removeCurse(Dungeon.hero);
            } else if (w.cursedKnown && wasCursed && !w.cursed) {
                ScrollOfUpgrade.weakenCurse(Dungeon.hero);
            }
            if (wasHardened && !w.enchantHardened) {
                GLog.w(Messages.get(Weapon.class, "hardening_gone", new Object[0]), new Object[0]);
            } else if (hadGoodEnchant && !w.hasGoodEnchant()) {
                GLog.w(Messages.get(Weapon.class, "incompatible", new Object[0]), new Object[0]);
            }
        } else if (item instanceof Armor) {
            Armor a = (Armor)item;
            boolean wasCursed = a.cursed;
            boolean wasHardened = a.glyphHardened;
            boolean hadCursedGlyph = a.hasCurseGlyph();
            boolean hadGoodGlyph = a.hasGoodGlyph();
            item = a.upgrade();
            if (a.cursedKnown && hadCursedGlyph && !a.hasCurseGlyph()) {
                ScrollOfUpgrade.removeCurse(Dungeon.hero);
            } else if (a.cursedKnown && wasCursed && !a.cursed) {
                ScrollOfUpgrade.weakenCurse(Dungeon.hero);
            }
            if (wasHardened && !a.glyphHardened) {
                GLog.w(Messages.get(Armor.class, "hardening_gone", new Object[0]), new Object[0]);
            } else if (hadGoodGlyph && !a.hasGoodGlyph()) {
                GLog.w(Messages.get(Armor.class, "incompatible", new Object[0]), new Object[0]);
            }
        } else if (item instanceof Wand || item instanceof Ring) {
            boolean wasCursed = item.cursed;
            item = item.upgrade();
            if (item.cursedKnown && wasCursed && !item.cursed) {
                ScrollOfUpgrade.removeCurse(Dungeon.hero);
            }
        } else {
            item = item.upgrade();
        }
        Badges.validateItemLevelAquired(item);
        ++Statistics.upgradesUsed;
        Badges.validateMageUnlock();
        Catalog.countUse(item.getClass());
        return item;
    }

    public static void upgrade(Hero hero) {
        if (rl.RLMode.headless) return;
        hero.sprite.emitter().start(Speck.factory(4), 0.2f, 3);
    }

    public static void weakenCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "weaken_curse", new Object[0]), new Object[0]);
        if (rl.RLMode.headless) return;
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 5);
    }

    public static void removeCurse(Hero hero) {
        GLog.p(Messages.get(ScrollOfUpgrade.class, "remove_curse", new Object[0]), new Object[0]);
        if (rl.RLMode.headless) return;
        hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
        Badges.validateClericUnlock();
    }

    @Override
    public int value() {
        return this.isKnown() ? 50 * this.quantity : super.value();
    }

    @Override
    public int energyVal() {
        return this.isKnown() ? 10 * this.quantity : super.energyVal();
    }
}

