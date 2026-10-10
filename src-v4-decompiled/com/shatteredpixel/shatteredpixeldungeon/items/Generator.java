/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.ArcaneResin;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.LiquidMetal;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClericArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.DuelistArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.HuntressArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.LeatherArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MageArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.MailArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.PlateArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.RogueArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ScaleArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.WarriorArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.ChaliceOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Pasty;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.Potion;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfExperience;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLevitation;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfParalyticGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.brews.Brew;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.Elixir;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfAccuracy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfElements;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEvasion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfHaste;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfTenacity;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.Scroll;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTerror;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTransmutation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.spells.Spell;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.Runestone;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAugmentation;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlast;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfBlink;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfClairvoyance;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDeepSleep;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfDetectMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFear;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfFlock;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfShock;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ChaoticCenser;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.CrackedSpyglass;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.FerretTuft;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.PetrifiedSeed;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.SaltCube;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ThirteenLeafClover;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.Trinket;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.VialOfBlood;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.AssassinsBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.BattleAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Crossbow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dirk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Flail;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Glaive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greataxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatshield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Greatsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.HandAxe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Katana;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Longsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Quarterstaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RoundShield;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Shortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sickle;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Sword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WarScythe;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Whip;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Bolas;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.FishingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ForceCube;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Javelin;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Kunai;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingClub;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingHammer;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpear;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Tomahawk;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Trident;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Generator {
    private static final float[][] floorSetTierProbs = new float[][]{{0.0f, 75.0f, 20.0f, 4.0f, 1.0f}, {0.0f, 25.0f, 50.0f, 20.0f, 5.0f}, {0.0f, 0.0f, 40.0f, 50.0f, 10.0f}, {0.0f, 0.0f, 20.0f, 40.0f, 40.0f}, {0.0f, 0.0f, 0.0f, 20.0f, 80.0f}};
    private static boolean usingFirstDeck = false;
    private static HashMap<Category, Float> defaultCatProbs = new LinkedHashMap<Category, Float>();
    private static HashMap<Category, Float> categoryProbs = new LinkedHashMap<Category, Float>();
    public static final Category[] wepTiers = new Category[]{Category.WEP_T1, Category.WEP_T2, Category.WEP_T3, Category.WEP_T4, Category.WEP_T5};
    public static final Category[] misTiers = new Category[]{Category.MIS_T1, Category.MIS_T2, Category.MIS_T3, Category.MIS_T4, Category.MIS_T5};
    private static final String FIRST_DECK = "first_deck";
    private static final String GENERAL_PROBS = "general_probs";
    private static final String CATEGORY_PROBS = "_probs";
    private static final String CATEGORY_USING_PROBS2 = "_using_probs2";
    private static final String CATEGORY_SEED = "_seed";
    private static final String CATEGORY_DROPPED = "_dropped";

    public static void fullReset() {
        usingFirstDeck = Random.Int(2) == 0;
        Generator.generalReset();
        for (Category cat : Category.values()) {
            cat.using2ndProbs = cat.defaultProbs2 != null && Random.Int(2) == 0;
            Generator.reset(cat);
            if (cat.defaultProbs == null) continue;
            cat.seed = Random.Long();
            cat.dropped = 0;
        }
    }

    public static void generalReset() {
        for (Category cat : Category.values()) {
            categoryProbs.put(cat, Float.valueOf(usingFirstDeck ? cat.firstProb : cat.secondProb));
            defaultCatProbs.put(cat, Float.valueOf(cat.firstProb + cat.secondProb));
        }
    }

    public static void reset(Category cat) {
        if (cat.defaultProbs != null) {
            if (cat.defaultProbs2 != null) {
                cat.using2ndProbs = !cat.using2ndProbs;
                cat.probs = cat.using2ndProbs ? (float[])cat.defaultProbs2.clone() : (float[])cat.defaultProbs.clone();
            } else {
                cat.probs = (float[])cat.defaultProbs.clone();
            }
        }
    }

    public static void undoDrop(Item item) {
        Generator.undoDrop(item.getClass());
    }

    public static void undoDrop(Class cls) {
        for (Category cat : Category.values()) {
            if (!cls.isAssignableFrom(cat.superClass) || cat.defaultProbs == null) continue;
            for (int i = 0; i < cat.classes.length; ++i) {
                if (cls != cat.classes[i]) continue;
                int n = i;
                cat.probs[n] = cat.probs[n] + 1.0f;
            }
        }
    }

    public static Item random() {
        Category cat = Random.chances(categoryProbs);
        if (cat == null) {
            usingFirstDeck = !usingFirstDeck;
            Generator.generalReset();
            cat = Random.chances(categoryProbs);
        }
        categoryProbs.put(cat, Float.valueOf(categoryProbs.get((Object)cat).floatValue() - 1.0f));
        if (cat == Category.SEED) {
            return Generator.randomUsingDefaults(cat);
        }
        return Generator.random(cat);
    }

    public static Item randomUsingDefaults() {
        return Generator.randomUsingDefaults(Random.chances(defaultCatProbs));
    }

    public static Item random(Category cat) {
        int i;
        switch (cat.ordinal()) {
            case 7: {
                return Generator.randomArmor();
            }
            case 1: {
                return Generator.randomWeapon();
            }
            case 8: {
                return Generator.randomMissile();
            }
            case 16: {
                Artifact item = Generator.randomArtifact();
                return item != null ? item : Generator.randomUsingDefaults(Category.RING);
            }
        }
        if (cat.defaultProbs != null && cat.seed != null) {
            Random.pushGenerator(cat.seed);
            for (i = 0; i < cat.dropped; ++i) {
                Random.Long();
            }
        }
        if ((i = Random.chances(cat.probs)) == -1) {
            Generator.reset(cat);
            i = Random.chances(cat.probs);
        }
        if (cat.defaultProbs != null) {
            int n = i;
            cat.probs[n] = cat.probs[n] - 1.0f;
        }
        Class<Object> itemCls = cat.classes[i];
        if (cat.defaultProbs != null && cat.seed != null) {
            Random.popGenerator();
            ++cat.dropped;
        }
        if (ExoticPotion.regToExo.containsKey(itemCls)) {
            if (Random.Float() < ExoticCrystals.consumableExoticChance()) {
                itemCls = ExoticPotion.regToExo.get(itemCls);
            }
        } else if (ExoticScroll.regToExo.containsKey(itemCls) && Random.Float() < ExoticCrystals.consumableExoticChance()) {
            itemCls = ExoticScroll.regToExo.get(itemCls);
        }
        return ((Item)Reflection.newInstance(itemCls)).random();
    }

    public static Item randomUsingDefaults(Category cat) {
        if (cat == Category.WEAPON) {
            return Generator.randomWeapon(true);
        }
        if (cat == Category.MISSILE) {
            return Generator.randomMissile(true);
        }
        if (cat.defaultProbs == null || cat == Category.ARTIFACT) {
            return Generator.random(cat);
        }
        if (cat.defaultProbsTotal != null) {
            return ((Item)Reflection.newInstance(cat.classes[Random.chances(cat.defaultProbsTotal)])).random();
        }
        Class<Object> itemCls = cat.classes[Random.chances(cat.defaultProbs)];
        if (ExoticPotion.regToExo.containsKey(itemCls)) {
            if (Random.Float() < ExoticCrystals.consumableExoticChance()) {
                itemCls = ExoticPotion.regToExo.get(itemCls);
            }
        } else if (ExoticScroll.regToExo.containsKey(itemCls) && Random.Float() < ExoticCrystals.consumableExoticChance()) {
            itemCls = ExoticScroll.regToExo.get(itemCls);
        }
        return ((Item)Reflection.newInstance(itemCls)).random();
    }

    public static Item random(Class<? extends Item> cl) {
        return Reflection.newInstance(cl).random();
    }

    public static Armor randomArmor() {
        return Generator.randomArmor(Dungeon.depth / 5);
    }

    public static Armor randomArmor(int floorSet) {
        floorSet = (int)GameMath.gate(0.0f, floorSet, floorSetTierProbs.length - 1);
        Armor a = (Armor)Reflection.newInstance(Category.ARMOR.classes[Random.chances(floorSetTierProbs[floorSet])]);
        a.random();
        return a;
    }

    public static MeleeWeapon randomWeapon() {
        return Generator.randomWeapon(Dungeon.depth / 5);
    }

    public static MeleeWeapon randomWeapon(int floorSet) {
        return Generator.randomWeapon(floorSet, false);
    }

    public static MeleeWeapon randomWeapon(boolean useDefaults) {
        return Generator.randomWeapon(Dungeon.depth / 5, useDefaults);
    }

    public static MeleeWeapon randomWeapon(int floorSet, boolean useDefaults) {
        floorSet = (int)GameMath.gate(0.0f, floorSet, floorSetTierProbs.length - 1);
        MeleeWeapon w = useDefaults ? (MeleeWeapon)Generator.randomUsingDefaults(wepTiers[Random.chances(floorSetTierProbs[floorSet])]) : (MeleeWeapon)Generator.random(wepTiers[Random.chances(floorSetTierProbs[floorSet])]);
        return w;
    }

    public static MissileWeapon randomMissile() {
        return Generator.randomMissile(Dungeon.depth / 5);
    }

    public static MissileWeapon randomMissile(int floorSet) {
        return Generator.randomMissile(floorSet, false);
    }

    public static MissileWeapon randomMissile(boolean useDefaults) {
        return Generator.randomMissile(Dungeon.depth / 5, useDefaults);
    }

    public static MissileWeapon randomMissile(int floorSet, boolean useDefaults) {
        floorSet = (int)GameMath.gate(0.0f, floorSet, floorSetTierProbs.length - 1);
        MissileWeapon w = useDefaults ? (MissileWeapon)Generator.randomUsingDefaults(misTiers[Random.chances(floorSetTierProbs[floorSet])]) : (MissileWeapon)Generator.random(misTiers[Random.chances(floorSetTierProbs[floorSet])]);
        return w;
    }

    public static Artifact randomArtifact() {
        int i;
        Category cat = Category.ARTIFACT;
        if (cat.defaultProbs != null && cat.seed != null) {
            Random.pushGenerator(cat.seed);
            for (i = 0; i < cat.dropped; ++i) {
                Random.Long();
            }
        }
        i = Random.chances(cat.probs);
        if (cat.defaultProbs != null && cat.seed != null) {
            Random.popGenerator();
            ++cat.dropped;
        }
        if (i == -1) {
            return null;
        }
        int n = i;
        cat.probs[n] = cat.probs[n] - 1.0f;
        return (Artifact)((Artifact)Reflection.newInstance(cat.classes[i])).random();
    }

    public static boolean removeArtifact(Class<? extends Artifact> artifact) {
        Category cat = Category.ARTIFACT;
        for (int i = 0; i < cat.classes.length; ++i) {
            if (!cat.classes[i].equals(artifact) || !(cat.probs[i] > 0.0f)) continue;
            cat.probs[i] = 0.0f;
            return true;
        }
        return false;
    }

    public static void storeInBundle(Bundle bundle) {
        bundle.put(FIRST_DECK, usingFirstDeck);
        Float[] genProbs = categoryProbs.values().toArray(new Float[0]);
        float[] storeProbs = new float[genProbs.length];
        for (int i = 0; i < storeProbs.length; ++i) {
            storeProbs[i] = genProbs[i].floatValue();
        }
        bundle.put(GENERAL_PROBS, storeProbs);
        for (Category cat : Category.values()) {
            if (cat.defaultProbs == null) continue;
            bundle.put(cat.name().toLowerCase() + CATEGORY_PROBS, cat.probs);
            if (cat.defaultProbs2 != null) {
                bundle.put(cat.name().toLowerCase() + CATEGORY_USING_PROBS2, cat.using2ndProbs);
            }
            if (cat.seed == null) continue;
            bundle.put(cat.name().toLowerCase() + CATEGORY_SEED, cat.seed);
            bundle.put(cat.name().toLowerCase() + CATEGORY_DROPPED, cat.dropped);
        }
    }

    public static void restoreFromBundle(Bundle bundle) {
        float[] probs;
        Generator.fullReset();
        usingFirstDeck = bundle.getBoolean(FIRST_DECK);
        if (bundle.contains(GENERAL_PROBS) && (probs = bundle.getFloatArray(GENERAL_PROBS)).length == Category.values().length) {
            for (int i = 0; i < probs.length; ++i) {
                categoryProbs.put(Category.values()[i], Float.valueOf(probs[i]));
            }
        }
        for (Category cat : Category.values()) {
            if (!bundle.contains(cat.name().toLowerCase() + CATEGORY_PROBS)) continue;
            float[] probs2 = bundle.getFloatArray(cat.name().toLowerCase() + CATEGORY_PROBS);
            if (cat.defaultProbs != null && probs2.length == cat.defaultProbs.length) {
                cat.probs = probs2;
            }
            cat.using2ndProbs = bundle.contains(cat.name().toLowerCase() + CATEGORY_USING_PROBS2) ? bundle.getBoolean(cat.name().toLowerCase() + CATEGORY_USING_PROBS2) : false;
            if (bundle.contains(cat.name().toLowerCase() + CATEGORY_SEED)) {
                cat.seed = bundle.getLong(cat.name().toLowerCase() + CATEGORY_SEED);
                cat.dropped = bundle.getInt(cat.name().toLowerCase() + CATEGORY_DROPPED);
            }
            if (cat != Category.ARTIFACT || probs2.length == cat.defaultProbs.length) continue;
            int keyIDX = 9;
            int j = 0;
            for (int i = 0; i < probs2.length; ++i) {
                if (j == keyIDX) {
                    cat.probs[j] = 1.0f;
                    ++j;
                }
                cat.probs[j] = probs2[i];
                ++j;
            }
        }
    }

    public static enum Category {
        TRINKET(0.0f, 0.0f, Trinket.class),
        WEAPON(2.0f, 2.0f, MeleeWeapon.class),
        WEP_T1(0.0f, 0.0f, MeleeWeapon.class),
        WEP_T2(0.0f, 0.0f, MeleeWeapon.class),
        WEP_T3(0.0f, 0.0f, MeleeWeapon.class),
        WEP_T4(0.0f, 0.0f, MeleeWeapon.class),
        WEP_T5(0.0f, 0.0f, MeleeWeapon.class),
        ARMOR(2.0f, 1.0f, Armor.class),
        MISSILE(1.0f, 2.0f, MissileWeapon.class),
        MIS_T1(0.0f, 0.0f, MissileWeapon.class),
        MIS_T2(0.0f, 0.0f, MissileWeapon.class),
        MIS_T3(0.0f, 0.0f, MissileWeapon.class),
        MIS_T4(0.0f, 0.0f, MissileWeapon.class),
        MIS_T5(0.0f, 0.0f, MissileWeapon.class),
        WAND(1.0f, 1.0f, Wand.class),
        RING(1.0f, 0.0f, Ring.class),
        ARTIFACT(0.0f, 1.0f, Artifact.class),
        FOOD(0.0f, 0.0f, Food.class),
        POTION(8.0f, 8.0f, Potion.class),
        SEED(1.0f, 1.0f, Plant.Seed.class),
        SCROLL(8.0f, 8.0f, Scroll.class),
        STONE(1.0f, 1.0f, Runestone.class),
        GOLD(10.0f, 10.0f, Gold.class);

        public Class<?>[] classes;
        public float[] probs;
        public float[] defaultProbs = null;
        public float[] defaultProbs2 = null;
        public boolean using2ndProbs = false;
        public float[] defaultProbsTotal = null;
        public Long seed = null;
        public int dropped = 0;
        public float firstProb;
        public float secondProb;
        public Class<? extends Item> superClass;
        private static HashMap<Class, ArrayList<Class>> subOrderings;

        private Category(float firstProb, float secondProb, Class<? extends Item> superClass) {
            this.firstProb = firstProb;
            this.secondProb = secondProb;
            this.superClass = superClass;
        }

        public static int order(Item item) {
            int catResult = -1;
            int subResult = 0;
            for (int i = 0; i < Category.values().length; ++i) {
                ArrayList<Class> subOrdering = subOrderings.get(Category.values()[i].superClass);
                if (subOrdering != null) {
                    for (int j = 0; j < subOrdering.size(); ++j) {
                        if (!subOrdering.get(j).isInstance(item)) continue;
                        catResult = i;
                        subResult = j;
                    }
                    continue;
                }
                if (!Category.values()[i].superClass.isInstance(item)) continue;
                catResult = i;
                subResult = 0;
            }
            if (catResult != -1) {
                return catResult * 100 + subResult;
            }
            return Short.MAX_VALUE + item.image();
        }

        static {
            subOrderings = new HashMap();
            subOrderings.put(Trinket.class, new ArrayList<Class>(Arrays.asList(Trinket.class, TrinketCatalyst.class)));
            subOrderings.put(MissileWeapon.class, new ArrayList<Class>(Arrays.asList(MissileWeapon.class, Bomb.class)));
            subOrderings.put(Potion.class, new ArrayList<Class>(Arrays.asList(Waterskin.class, Potion.class, ExoticPotion.class, Brew.class, Elixir.class, LiquidMetal.class)));
            subOrderings.put(Scroll.class, new ArrayList<Class>(Arrays.asList(Scroll.class, ExoticScroll.class, Spell.class, ArcaneResin.class)));
            Category.GOLD.classes = new Class[]{Gold.class};
            Category.GOLD.probs = new float[]{1.0f};
            Category.POTION.classes = new Class[]{PotionOfStrength.class, PotionOfHealing.class, PotionOfMindVision.class, PotionOfFrost.class, PotionOfLiquidFlame.class, PotionOfToxicGas.class, PotionOfHaste.class, PotionOfInvisibility.class, PotionOfLevitation.class, PotionOfParalyticGas.class, PotionOfPurity.class, PotionOfExperience.class};
            Category.POTION.defaultProbs = new float[]{0.0f, 3.0f, 2.0f, 1.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Category.POTION.defaultProbs2 = new float[]{0.0f, 3.0f, 2.0f, 2.0f, 1.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
            Category.POTION.probs = (float[])Category.POTION.defaultProbs.clone();
            Category.SEED.classes = new Class[]{Rotberry.Seed.class, Sungrass.Seed.class, Fadeleaf.Seed.class, Icecap.Seed.class, Firebloom.Seed.class, Sorrowmoss.Seed.class, Swiftthistle.Seed.class, Blindweed.Seed.class, Stormvine.Seed.class, Earthroot.Seed.class, Mageroyal.Seed.class, Starflower.Seed.class};
            Category.SEED.defaultProbs = new float[]{0.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f};
            Category.SEED.probs = (float[])Category.SEED.defaultProbs.clone();
            Category.SCROLL.classes = new Class[]{ScrollOfUpgrade.class, ScrollOfIdentify.class, ScrollOfRemoveCurse.class, ScrollOfMirrorImage.class, ScrollOfRecharging.class, ScrollOfTeleportation.class, ScrollOfLullaby.class, ScrollOfMagicMapping.class, ScrollOfRage.class, ScrollOfRetribution.class, ScrollOfTerror.class, ScrollOfTransmutation.class};
            Category.SCROLL.defaultProbs = new float[]{0.0f, 3.0f, 2.0f, 1.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Category.SCROLL.defaultProbs2 = new float[]{0.0f, 3.0f, 2.0f, 2.0f, 1.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f};
            Category.SCROLL.probs = (float[])Category.SCROLL.defaultProbs.clone();
            Category.STONE.classes = new Class[]{StoneOfEnchantment.class, StoneOfIntuition.class, StoneOfDetectMagic.class, StoneOfFlock.class, StoneOfShock.class, StoneOfBlink.class, StoneOfDeepSleep.class, StoneOfClairvoyance.class, StoneOfAggression.class, StoneOfBlast.class, StoneOfFear.class, StoneOfAugmentation.class};
            Category.STONE.defaultProbs = new float[]{0.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.0f};
            Category.STONE.probs = (float[])Category.STONE.defaultProbs.clone();
            Category.WAND.classes = new Class[]{WandOfMagicMissile.class, WandOfLightning.class, WandOfDisintegration.class, WandOfFireblast.class, WandOfCorrosion.class, WandOfBlastWave.class, WandOfLivingEarth.class, WandOfFrost.class, WandOfPrismaticLight.class, WandOfWarding.class, WandOfTransfusion.class, WandOfCorruption.class, WandOfRegrowth.class};
            Category.WAND.defaultProbs = new float[]{3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};
            Category.WAND.probs = (float[])Category.WAND.defaultProbs.clone();
            Category.WEAPON.classes = new Class[0];
            Category.WEAPON.probs = new float[0];
            Category.WEP_T1.classes = new Class[]{WornShortsword.class, MagesStaff.class, Dagger.class, Gloves.class, Rapier.class, Cudgel.class};
            Category.WEP_T1.defaultProbs = new float[]{2.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f};
            Category.WEP_T1.probs = (float[])Category.WEP_T1.defaultProbs.clone();
            Category.WEP_T2.classes = new Class[]{Shortsword.class, HandAxe.class, Spear.class, Quarterstaff.class, Dirk.class, Sickle.class, Pickaxe.class};
            Category.WEP_T2.defaultProbs = new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 0.0f};
            Category.WEP_T2.probs = (float[])Category.WEP_T2.defaultProbs.clone();
            Category.WEP_T3.classes = new Class[]{Sword.class, Mace.class, Scimitar.class, RoundShield.class, Sai.class, Whip.class};
            Category.WEP_T3.defaultProbs = new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
            Category.WEP_T3.probs = (float[])Category.WEP_T3.defaultProbs.clone();
            Category.WEP_T4.classes = new Class[]{Longsword.class, BattleAxe.class, Flail.class, RunicBlade.class, AssassinsBlade.class, Crossbow.class, Katana.class};
            Category.WEP_T4.defaultProbs = new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
            Category.WEP_T4.probs = (float[])Category.WEP_T4.defaultProbs.clone();
            Category.WEP_T5.classes = new Class[]{Greatsword.class, WarHammer.class, Glaive.class, Greataxe.class, Greatshield.class, Gauntlet.class, WarScythe.class};
            Category.WEP_T5.defaultProbs = new float[]{2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f, 2.0f};
            Category.WEP_T5.probs = (float[])Category.WEP_T5.defaultProbs.clone();
            Category.ARMOR.classes = new Class[]{ClothArmor.class, LeatherArmor.class, MailArmor.class, ScaleArmor.class, PlateArmor.class, WarriorArmor.class, MageArmor.class, RogueArmor.class, HuntressArmor.class, DuelistArmor.class, ClericArmor.class};
            Category.ARMOR.probs = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
            Category.MISSILE.classes = new Class[0];
            Category.MISSILE.probs = new float[0];
            Category.MIS_T1.classes = new Class[]{ThrowingStone.class, ThrowingKnife.class, ThrowingSpike.class, Dart.class};
            Category.MIS_T1.defaultProbs = new float[]{3.0f, 3.0f, 3.0f, 0.0f};
            Category.MIS_T1.probs = (float[])Category.MIS_T1.defaultProbs.clone();
            Category.MIS_T2.classes = new Class[]{FishingSpear.class, ThrowingClub.class, Shuriken.class};
            Category.MIS_T2.defaultProbs = new float[]{3.0f, 3.0f, 3.0f};
            Category.MIS_T2.probs = (float[])Category.MIS_T2.defaultProbs.clone();
            Category.MIS_T3.classes = new Class[]{ThrowingSpear.class, Kunai.class, Bolas.class};
            Category.MIS_T3.defaultProbs = new float[]{3.0f, 3.0f, 3.0f};
            Category.MIS_T3.probs = (float[])Category.MIS_T3.defaultProbs.clone();
            Category.MIS_T4.classes = new Class[]{Javelin.class, Tomahawk.class, HeavyBoomerang.class};
            Category.MIS_T4.defaultProbs = new float[]{3.0f, 3.0f, 3.0f};
            Category.MIS_T4.probs = (float[])Category.MIS_T4.defaultProbs.clone();
            Category.MIS_T5.classes = new Class[]{Trident.class, ThrowingHammer.class, ForceCube.class};
            Category.MIS_T5.defaultProbs = new float[]{3.0f, 3.0f, 3.0f};
            Category.MIS_T5.probs = (float[])Category.MIS_T5.defaultProbs.clone();
            Category.FOOD.classes = new Class[]{Food.class, Pasty.class, MysteryMeat.class};
            Category.FOOD.defaultProbs = new float[]{4.0f, 1.0f, 0.0f};
            Category.FOOD.probs = (float[])Category.FOOD.defaultProbs.clone();
            Category.RING.classes = new Class[]{RingOfAccuracy.class, RingOfArcana.class, RingOfElements.class, RingOfEnergy.class, RingOfEvasion.class, RingOfForce.class, RingOfFuror.class, RingOfHaste.class, RingOfMight.class, RingOfSharpshooting.class, RingOfTenacity.class, RingOfWealth.class};
            Category.RING.defaultProbs = new float[]{3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f, 3.0f};
            Category.RING.probs = (float[])Category.RING.defaultProbs.clone();
            Category.ARTIFACT.classes = new Class[]{AlchemistsToolkit.class, ChaliceOfBlood.class, CloakOfShadows.class, DriedRose.class, EtherealChains.class, HolyTome.class, HornOfPlenty.class, MasterThievesArmband.class, SandalsOfNature.class, SkeletonKey.class, TalismanOfForesight.class, TimekeepersHourglass.class, UnstableSpellbook.class};
            Category.ARTIFACT.defaultProbs = new float[]{1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Category.ARTIFACT.probs = (float[])Category.ARTIFACT.defaultProbs.clone();
            Category.TRINKET.classes = new Class[]{RatSkull.class, ParchmentScrap.class, PetrifiedSeed.class, ExoticCrystals.class, MossyClump.class, DimensionalSundial.class, ThirteenLeafClover.class, TrapMechanism.class, MimicTooth.class, WondrousResin.class, EyeOfNewt.class, SaltCube.class, VialOfBlood.class, ShardOfOblivion.class, ChaoticCenser.class, FerretTuft.class, CrackedSpyglass.class};
            Category.TRINKET.defaultProbs = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
            Category.TRINKET.probs = (float[])Category.TRINKET.defaultProbs.clone();
            for (Category cat : Category.values()) {
                if (cat.defaultProbs2 == null) continue;
                cat.defaultProbsTotal = new float[cat.defaultProbs.length];
                for (int i = 0; i < cat.defaultProbs.length; ++i) {
                    cat.defaultProbsTotal[i] = cat.defaultProbs[i] + cat.defaultProbs2[i];
                }
            }
        }
    }
}

