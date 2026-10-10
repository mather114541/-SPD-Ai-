/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Honeypot;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.UnstableBrew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.PotionOfDivineInspiration;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfMetamorphosis;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.UnstableSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.watabou.noosa.Visual;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class RingOfWealth
extends Ring {
    private float triesToDrop;
    private int dropsToRare;
    private static final String TRIES_TO_DROP = "tries_to_drop";
    private static final String DROPS_TO_RARE = "drops_to_rare";
    private static int latestDropTier = 0;

    public RingOfWealth() {
        this.icon = ItemSpriteSheet.Icons.RING_WEALTH;
        this.buffClass = Wealth.class;
        this.triesToDrop = Float.MIN_VALUE;
        this.dropsToRare = Integer.MIN_VALUE;
    }

    @Override
    public String statsInfo() {
        if (this.isIdentified()) {
            Object info = Messages.get(this, "stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2f, this.soloBuffedBonus()) - 1.0)));
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2f, this.combinedBuffedBonus(Dungeon.hero)) - 1.0)));
            }
            return info;
        }
        return Messages.get(this, "typical_stats", Messages.decimalFormat("#.##", 20.0));
    }

    @Override
    public String upgradeStat1(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Messages.decimalFormat("#.##", 100.0 * (Math.pow(1.2f, level + 1) - 1.0)) + "%";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TRIES_TO_DROP, this.triesToDrop);
        bundle.put(DROPS_TO_RARE, this.dropsToRare);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.triesToDrop = bundle.getFloat(TRIES_TO_DROP);
        this.dropsToRare = bundle.getInt(DROPS_TO_RARE);
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Wealth();
    }

    public static float dropChanceMultiplier(Char target) {
        return (float)Math.pow(1.2, RingOfWealth.getBuffedBonus(target, Wealth.class));
    }

    public static ArrayList<Item> tryForBonusDrop(Char target, int tries) {
        CounterBuff dropsToEquip;
        int bonus = RingOfWealth.getBuffedBonus(target, Wealth.class);
        if (bonus <= 0) {
            return null;
        }
        CounterBuff triesToDrop = target.buff(TriesToDropTracker.class);
        if (triesToDrop == null) {
            triesToDrop = Buff.affect(target, TriesToDropTracker.class);
            triesToDrop.countUp(Random.NormalIntRange(0, 20));
        }
        if ((dropsToEquip = (CounterBuff)target.buff(DropsToEquipTracker.class)) == null) {
            dropsToEquip = Buff.affect(target, DropsToEquipTracker.class);
            dropsToEquip.countUp(Random.NormalIntRange(5, 10));
        }
        ArrayList<Item> drops = new ArrayList<Item>();
        triesToDrop.countDown(tries);
        while (triesToDrop.count() <= 0.0f) {
            if (dropsToEquip.count() <= 0.0f) {
                Item i;
                int equipBonus = 0;
                for (Wealth w : target.buffs(Wealth.class)) {
                    if (w.buffedLvl() > equipBonus) {
                        equipBonus = w.buffedLvl() + Math.min(equipBonus, 2);
                        continue;
                    }
                    equipBonus += Math.min(w.buffedLvl(), 2);
                }
                while (Challenges.isItemBlocked(i = RingOfWealth.genEquipmentDrop(equipBonus - 1))) {
                }
                drops.add(i);
                dropsToEquip.countUp(Random.NormalIntRange(5, 10));
            } else {
                Item i;
                while (Challenges.isItemBlocked(i = RingOfWealth.genConsumableDrop(bonus - 1))) {
                }
                drops.add(i);
                dropsToEquip.countDown(1.0f);
            }
            triesToDrop.countUp(Random.NormalIntRange(0, 20));
        }
        return drops;
    }

    public static void showFlareForBonusDrop(Visual vis) {
        if (vis == null || vis.parent == null) {
            return;
        }
        switch (latestDropTier) {
            default: {
                break;
            }
            case 1: {
                new Flare(6, 20.0f).color(65280, true).show(vis, 3.0f);
                break;
            }
            case 2: {
                new Flare(6, 24.0f).color(43775, true).show(vis, 3.33f);
                break;
            }
            case 3: {
                new Flare(6, 28.0f).color(0xAA00FF, true).show(vis, 3.67f);
                break;
            }
            case 4: {
                new Flare(6, 32.0f).color(0xFFAA00, true).show(vis, 4.0f);
            }
        }
        latestDropTier = 0;
    }

    public static Item genConsumableDrop(int level) {
        float roll = Random.Float();
        if (roll < 0.6f - 0.04f * (float)level) {
            latestDropTier = 1;
            return RingOfWealth.genLowValueConsumable();
        }
        if (roll < 0.9f - 0.02f * (float)level) {
            latestDropTier = 2;
            return RingOfWealth.genMidValueConsumable();
        }
        latestDropTier = 3;
        return RingOfWealth.genHighValueConsumable();
    }

    private static Item genLowValueConsumable() {
        switch (Random.Int(4)) {
            default: {
                Item i = new Gold().random();
                return i.quantity(i.quantity() / 2);
            }
            case 1: {
                return Generator.randomUsingDefaults(Generator.Category.STONE);
            }
            case 2: {
                return Generator.randomUsingDefaults(Generator.Category.POTION);
            }
            case 3: 
        }
        return Generator.randomUsingDefaults(Generator.Category.SCROLL);
    }

    private static Item genMidValueConsumable() {
        switch (Random.Int(6)) {
            default: {
                Item i = RingOfWealth.genLowValueConsumable();
                return i.quantity(i.quantity() * 2);
            }
            case 1: {
                Item i = Generator.randomUsingDefaults(Generator.Category.POTION);
                if (!(i instanceof ExoticPotion)) {
                    return Reflection.newInstance(ExoticPotion.regToExo.get(i.getClass()));
                }
                return (Item)Reflection.newInstance(i.getClass());
            }
            case 2: {
                Item i = Generator.randomUsingDefaults(Generator.Category.SCROLL);
                if (!(i instanceof ExoticScroll)) {
                    return Reflection.newInstance(ExoticScroll.regToExo.get(i.getClass()));
                }
                return (Item)Reflection.newInstance(i.getClass());
            }
            case 3: {
                return Random.Int(2) == 0 ? new UnstableBrew() : new UnstableSpell();
            }
            case 4: {
                return new Bomb();
            }
            case 5: 
        }
        return new Honeypot();
    }

    private static Item genHighValueConsumable() {
        switch (Random.Int(4)) {
            default: {
                Item i = RingOfWealth.genMidValueConsumable();
                if (i instanceof Bomb) {
                    return new Bomb.DoubleBomb();
                }
                return i.quantity(i.quantity() * 2);
            }
            case 1: {
                return new StoneOfEnchantment();
            }
            case 2: {
                return Random.Float() < ExoticCrystals.consumableExoticChance() ? new PotionOfDivineInspiration() : new PotionOfExperience();
            }
            case 3: 
        }
        return Random.Float() < ExoticCrystals.consumableExoticChance() ? new ScrollOfMetamorphosis() : new ScrollOfTransmutation();
    }

    private static Item genEquipmentDrop(int level) {
        Item result;
        int floorset = (Dungeon.depth + level) / 5;
        switch (Random.Int(5)) {
            default: {
                MeleeWeapon w = Generator.randomWeapon(floorset, true);
                if (!w.hasGoodEnchant() && Random.Int(10) < level) {
                    w.enchant();
                } else if (w.hasCurseEnchant()) {
                    w.enchant(null);
                }
                result = w;
                break;
            }
            case 2: {
                Armor a = Generator.randomArmor(floorset);
                if (!a.hasGoodGlyph() && Random.Int(10) < level) {
                    a.inscribe();
                } else if (a.hasCurseGlyph()) {
                    a.inscribe(null);
                }
                result = a;
                break;
            }
            case 3: {
                result = Generator.randomUsingDefaults(Generator.Category.RING);
                break;
            }
            case 4: {
                result = Generator.random(Generator.Category.ARTIFACT);
            }
        }
        if (result.isUpgradable()) {
            int minLevel = (level + 1) / 2;
            if (result.level() < minLevel) {
                result.level(minLevel);
            }
        }
        result.cursed = false;
        result.cursedKnown = true;
        latestDropTier = result.level() >= 2 ? 4 : 3;
        return result;
    }

    public class Wealth
    extends Ring.RingBuff {
    }

    public static class TriesToDropTracker
    extends CounterBuff {
        public TriesToDropTracker() {
            this.revivePersists = true;
        }
    }

    public static class DropsToEquipTracker
    extends CounterBuff {
        public DropsToEquipTracker() {
            this.revivePersists = true;
        }
    }
}

