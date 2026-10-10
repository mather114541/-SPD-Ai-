/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.TormentedSpirit;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.PathFinder;

public class ScrollOfRemoveCurse
extends InventoryScroll {
    public ScrollOfRemoveCurse() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_REMCURSE;
        this.preferredBag = Belongings.Backpack.class;
    }

    @Override
    public void doRead() {
        TormentedSpirit spirit = null;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!(Actor.findChar(ScrollOfRemoveCurse.curUser.pos + i) instanceof TormentedSpirit)) continue;
            spirit = (TormentedSpirit)Actor.findChar(ScrollOfRemoveCurse.curUser.pos + i);
        }
        if (spirit != null) {
            this.identify();
            Sample.INSTANCE.play("sounds/read.mp3");
            this.readAnimation();
            new Flare(6, 32.0f).show(ScrollOfRemoveCurse.curUser.sprite, 2.0f);
            if (curUser.buff(Degrade.class) != null) {
                Degrade.detach(curUser, Degrade.class);
            }
            this.detach(ScrollOfRemoveCurse.curUser.belongings.backpack);
            GLog.p(Messages.get(this, "spirit", new Object[0]), new Object[0]);
            spirit.cleanse();
        } else {
            super.doRead();
        }
    }

    @Override
    protected boolean usableOnItem(Item item) {
        return ScrollOfRemoveCurse.uncursable(item);
    }

    public static boolean uncursable(Item item) {
        if (item.isEquipped(Dungeon.hero) && Dungeon.hero.buff(Degrade.class) != null) {
            return true;
        }
        if ((item instanceof EquipableItem || item instanceof Wand) && (!item.isIdentified() && !item.cursedKnown || item.cursed)) {
            return true;
        }
        if (item instanceof Weapon) {
            return ((Weapon)item).hasCurseEnchant();
        }
        if (item instanceof Armor) {
            return ((Armor)item).hasCurseGlyph();
        }
        return false;
    }

    @Override
    protected void onItemSelected(Item item) {
        new Flare(6, 32.0f).show(ScrollOfRemoveCurse.curUser.sprite, 2.0f);
        boolean procced = ScrollOfRemoveCurse.uncurse(curUser, item);
        if (curUser.buff(Degrade.class) != null) {
            Degrade.detach(curUser, Degrade.class);
            procced = true;
        }
        if (procced) {
            GLog.p(Messages.get(this, "cleansed", new Object[0]), new Object[0]);
        } else {
            GLog.i(Messages.get(this, "not_cleansed", new Object[0]), new Object[0]);
        }
    }

    public static boolean uncurse(Hero hero, Item ... items) {
        boolean procced = false;
        for (Item item : items) {
            Armor a;
            Weapon w;
            if (item != null) {
                item.cursedKnown = true;
                if (item.cursed) {
                    procced = true;
                    item.cursed = false;
                }
            }
            if (item instanceof Weapon && (w = (Weapon)item).hasCurseEnchant()) {
                w.enchant(null);
                procced = true;
            }
            if (item instanceof Armor && (a = (Armor)item).hasCurseGlyph()) {
                a.inscribe(null);
                procced = true;
            }
            if (!(item instanceof Wand)) continue;
            ((Wand)item).updateLevel();
        }
        if (procced) {
            if (hero != null) {
                hero.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                hero.updateHT(false);
                ScrollOfRemoveCurse.updateQuickslot();
            }
            Badges.validateClericUnlock();
        }
        return procced;
    }

    @Override
    public int value() {
        return this.isKnown() ? 30 * this.quantity : super.value();
    }
}

