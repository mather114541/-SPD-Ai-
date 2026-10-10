/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.scrolls;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Transmuting;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.InventoryScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.TippedDart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Reflection;

public class ScrollOfTransmutation
extends InventoryScroll {
    public ScrollOfTransmutation() {
        this.icon = ItemSpriteSheet.Icons.SCROLL_TRANSMUTE;
        this.bones = true;
        this.talentFactor = 2.0f;
    }

    @Override
    protected boolean usableOnItem(Item item) {
        if (item instanceof MeleeWeapon) {
            return !(item instanceof Pickaxe) || !(Dungeon.level instanceof MiningLevel);
        }
        if (item instanceof MissileWeapon) {
            return item.getClass() != Dart.class;
        }
        if (item instanceof Potion) {
            return !(item instanceof Elixir) && !(item instanceof Brew);
        }
        if (item instanceof Scroll) {
            return item != this || item.quantity() > 1 || identifiedByUse;
        }
        if (item instanceof Artifact) {
            return !item.unique;
        }
        return item instanceof Ring || item instanceof Wand || item instanceof Trinket || item instanceof Plant.Seed || item instanceof Runestone;
    }

    @Override
    protected void onItemSelected(Item item) {
        Item result = ScrollOfTransmutation.changeItem(item);
        if (result == null) {
            GLog.n(Messages.get(this, "nothing", new Object[0]), new Object[0]);
            curItem.collect(ScrollOfTransmutation.curUser.belongings.backpack);
        } else {
            if (result != item) {
                int slot = Dungeon.quickslot.getSlot(item);
                if (item.isEquipped(Dungeon.hero)) {
                    item.cursed = false;
                    if (item instanceof Artifact && result instanceof Ring) {
                        ((EquipableItem)item).doUnequip(Dungeon.hero, false);
                        if (!result.collect()) {
                            Dungeon.level.drop((Item)result, (int)ScrollOfTransmutation.curUser.pos).sprite.drop();
                        }
                    } else if (item instanceof KindOfWeapon && Dungeon.hero.belongings.secondWep() == item) {
                        ((EquipableItem)item).doUnequip(Dungeon.hero, false);
                        ((KindOfWeapon)result).equipSecondary(Dungeon.hero);
                    } else {
                        ((EquipableItem)item).doUnequip(Dungeon.hero, false);
                        ((EquipableItem)result).doEquip(Dungeon.hero);
                    }
                    Dungeon.hero.spend(-Dungeon.hero.cooldown());
                } else {
                    if (item instanceof MissileWeapon && !(item instanceof TippedDart)) {
                        item.detachAll(Dungeon.hero.belongings.backpack);
                    } else {
                        item.detach(Dungeon.hero.belongings.backpack);
                    }
                    if (!result.collect()) {
                        Dungeon.level.drop((Item)result, (int)ScrollOfTransmutation.curUser.pos).sprite.drop();
                    } else if (result.stackable && Dungeon.hero.belongings.getSimilar(result) != null) {
                        result = Dungeon.hero.belongings.getSimilar(result);
                    }
                }
                if (slot != -1 && result.defaultAction() != null && !Dungeon.quickslot.isNonePlaceholder(slot).booleanValue() && Dungeon.hero.belongings.contains(result)) {
                    Dungeon.quickslot.setSlot(slot, result);
                }
            }
            if (result.isIdentified()) {
                Catalog.setSeen(result.getClass());
                Statistics.itemTypesDiscovered.add(result.getClass());
            }
            Transmuting.show((Char)curUser, item, result);
            ScrollOfTransmutation.curUser.sprite.emitter().start(Speck.factory(10), 0.2f, 10);
            GLog.p(Messages.get(this, "morph", new Object[0]), new Object[0]);
        }
    }

    public static Item changeItem(Item item) {
        if (item instanceof MagesStaff) {
            return ScrollOfTransmutation.changeStaff((MagesStaff)item);
        }
        if (item instanceof TippedDart) {
            return ScrollOfTransmutation.changeTippedDart((TippedDart)item);
        }
        if (item instanceof MeleeWeapon || item instanceof MissileWeapon) {
            return ScrollOfTransmutation.changeWeapon((Weapon)item);
        }
        if (item instanceof Scroll) {
            return ScrollOfTransmutation.changeScroll((Scroll)item);
        }
        if (item instanceof Potion) {
            return ScrollOfTransmutation.changePotion((Potion)item);
        }
        if (item instanceof Ring) {
            return ScrollOfTransmutation.changeRing((Ring)item);
        }
        if (item instanceof Wand) {
            return ScrollOfTransmutation.changeWand((Wand)item);
        }
        if (item instanceof Plant.Seed) {
            return ScrollOfTransmutation.changeSeed((Plant.Seed)item);
        }
        if (item instanceof Runestone) {
            return ScrollOfTransmutation.changeStone((Runestone)item);
        }
        if (item instanceof Artifact) {
            Artifact a = ScrollOfTransmutation.changeArtifact((Artifact)item);
            if (a == null) {
                Item result = Generator.randomUsingDefaults(Generator.Category.RING);
                result.levelKnown = item.levelKnown;
                result.cursed = item.cursed;
                result.cursedKnown = item.cursedKnown;
                if (item.visiblyUpgraded() == 10) {
                    result.level(2);
                } else if (item.visiblyUpgraded() >= 5) {
                    result.level(1);
                } else {
                    result.level(0);
                }
                return result;
            }
            return a;
        }
        if (item instanceof Trinket) {
            return ScrollOfTransmutation.changeTrinket((Trinket)item);
        }
        return null;
    }

    private static MagesStaff changeStaff(MagesStaff staff) {
        Wand n;
        Class<? extends Wand> wandClass = staff.wandClass();
        if (wandClass == null) {
            return null;
        }
        while (Challenges.isItemBlocked(n = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND)) || n.getClass() == wandClass) {
        }
        n.cursed = false;
        n.level(0);
        n.identify();
        staff.imbueWand(n, null);
        return staff;
    }

    private static TippedDart changeTippedDart(TippedDart dart) {
        TippedDart n;
        while ((n = TippedDart.randomTipped(1)).getClass() == dart.getClass()) {
        }
        return n;
    }

    private static Weapon changeWeapon(Weapon w) {
        Weapon n;
        Generator.Category c = w instanceof MeleeWeapon ? Generator.wepTiers[((MeleeWeapon)w).tier - 1] : Generator.misTiers[((MissileWeapon)w).tier - 1];
        while (Challenges.isItemBlocked(n = (Weapon)Generator.randomUsingDefaults(c)) || n.getClass() == w.getClass()) {
        }
        n.level(0);
        n.quantity(w.quantity());
        int level = w.trueLevel();
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }
        n.enchantment = w.enchantment;
        n.curseInfusionBonus = w.curseInfusionBonus;
        n.masteryPotionBonus = w.masteryPotionBonus;
        n.levelKnown = w.levelKnown;
        n.cursedKnown = w.cursedKnown;
        n.cursed = w.cursed;
        n.augment = w.augment;
        n.enchantHardened = w.enchantHardened;
        if (w instanceof MissileWeapon && w.isUpgradable()) {
            Buff.affect((Char)Dungeon.hero, MissileWeapon.UpgradedSetTracker.class).levelThresholds.put(((MissileWeapon)w).setID, Integer.MAX_VALUE);
            ((MissileWeapon)n).damage(100.0f - ((MissileWeapon)w).durabilityLeft());
        }
        return n;
    }

    private static Ring changeRing(Ring r) {
        Ring n;
        while (Challenges.isItemBlocked(n = (Ring)Generator.randomUsingDefaults(Generator.Category.RING)) || n.getClass() == r.getClass()) {
        }
        n.level(0);
        int level = r.level();
        if (level > 0) {
            n.upgrade(level);
        } else if (level < 0) {
            n.degrade(-level);
        }
        n.levelKnown = r.levelKnown;
        n.cursedKnown = r.cursedKnown;
        n.cursed = r.cursed;
        return n;
    }

    private static Artifact changeArtifact(Artifact a) {
        Artifact n;
        while ((n = Generator.randomArtifact()) != null && (Challenges.isItemBlocked(n) || n.getClass() == a.getClass())) {
        }
        if (n != null) {
            if (a instanceof DriedRose) {
                if (((DriedRose)a).ghostWeapon() != null) {
                    Dungeon.level.drop(((DriedRose)a).ghostWeapon(), Dungeon.hero.pos);
                }
                if (((DriedRose)a).ghostArmor() != null) {
                    Dungeon.level.drop(((DriedRose)a).ghostArmor(), Dungeon.hero.pos);
                }
            }
            n.cursedKnown = a.cursedKnown;
            n.cursed = a.cursed;
            n.levelKnown = a.levelKnown;
            n.transferUpgrade(a.visiblyUpgraded());
            return n;
        }
        return null;
    }

    private static Trinket changeTrinket(Trinket t) {
        Trinket n;
        while (Challenges.isItemBlocked(n = (Trinket)Generator.random(Generator.Category.TRINKET)) || n.getClass() == t.getClass()) {
        }
        n.level(t.trueLevel());
        n.levelKnown = t.levelKnown;
        n.cursedKnown = t.cursedKnown;
        n.cursed = t.cursed;
        return n;
    }

    private static Wand changeWand(Wand w) {
        Wand n;
        while (Challenges.isItemBlocked(n = (Wand)Generator.randomUsingDefaults(Generator.Category.WAND)) || n.getClass() == w.getClass()) {
        }
        n.level(0);
        int level = w.trueLevel();
        n.upgrade(level);
        n.levelKnown = w.levelKnown;
        n.curChargeKnown = w.curChargeKnown;
        n.cursedKnown = w.cursedKnown;
        n.cursed = w.cursed;
        n.curseInfusionBonus = w.curseInfusionBonus;
        n.resinBonus = w.resinBonus;
        n.curCharges = w.curCharges;
        n.updateLevel();
        return n;
    }

    private static Plant.Seed changeSeed(Plant.Seed s) {
        Plant.Seed n;
        while ((n = (Plant.Seed)Generator.randomUsingDefaults(Generator.Category.SEED)).getClass() == s.getClass()) {
        }
        return n;
    }

    private static Runestone changeStone(Runestone r) {
        Runestone n;
        while ((n = (Runestone)Generator.randomUsingDefaults(Generator.Category.STONE)).getClass() == r.getClass()) {
        }
        return n;
    }

    private static Scroll changeScroll(Scroll s) {
        if (s instanceof ExoticScroll) {
            return Reflection.newInstance(ExoticScroll.exoToReg.get(s.getClass()));
        }
        return Reflection.newInstance(ExoticScroll.regToExo.get(s.getClass()));
    }

    private static Potion changePotion(Potion p) {
        if (p instanceof ExoticPotion) {
            return Reflection.newInstance(ExoticPotion.exoToReg.get(p.getClass()));
        }
        return Reflection.newInstance(ExoticPotion.regToExo.get(p.getClass()));
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

