/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.QuickSlot;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Challenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpectralBlades;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.ElementalBlast;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WarpBeacon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.DeathMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.SmokeBomb;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Endure;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.HeroicLeap;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior.Shockwave;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Waterskin;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.CloakOfShadows;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.food.Food;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfHealing;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfInvisibility;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfLiquidFlame;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfMindVision;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfPurity;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfIdentify;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfLullaby;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRage;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRemoveCurse;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Cudgel;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Dagger;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Rapier;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.WornShortsword;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingKnife;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingSpike;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.ThrowingStone;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.watabou.utils.DeviceCompat;

public enum HeroClass {
    WARRIOR(HeroSubClass.BERSERKER, HeroSubClass.GLADIATOR),
    MAGE(HeroSubClass.BATTLEMAGE, HeroSubClass.WARLOCK),
    ROGUE(HeroSubClass.ASSASSIN, HeroSubClass.FREERUNNER),
    HUNTRESS(HeroSubClass.SNIPER, HeroSubClass.WARDEN),
    DUELIST(HeroSubClass.CHAMPION, HeroSubClass.MONK),
    CLERIC(HeroSubClass.PRIEST, HeroSubClass.PALADIN);

    private HeroSubClass[] subClasses;

    private HeroClass(HeroSubClass ... subClasses) {
        this.subClasses = subClasses;
    }

    public void initHero(Hero hero) {
        hero.heroClass = this;
        Talent.initClassTalents(hero);
        Item i = new ClothArmor().identify();
        if (!Challenges.isItemBlocked(i)) {
            hero.belongings.armor = (ClothArmor)i;
        }
        if (!Challenges.isItemBlocked(i = new Food())) {
            i.collect();
        }
        new VelvetPouch().collect();
        Dungeon.LimitedDrops.VELVET_POUCH.drop();
        Waterskin waterskin = new Waterskin();
        waterskin.collect();
        new ScrollOfIdentify().identify();
        switch (this.ordinal()) {
            case 0: {
                HeroClass.initWarrior(hero);
                break;
            }
            case 1: {
                HeroClass.initMage(hero);
                break;
            }
            case 2: {
                HeroClass.initRogue(hero);
                break;
            }
            case 3: {
                HeroClass.initHuntress(hero);
                break;
            }
            case 4: {
                HeroClass.initDuelist(hero);
                break;
            }
            case 5: {
                HeroClass.initCleric(hero);
            }
        }
        if (SPDSettings.quickslotWaterskin()) {
            for (int s = 0; s < QuickSlot.SIZE; ++s) {
                if (Dungeon.quickslot.getItem(s) != null) continue;
                Dungeon.quickslot.setSlot(s, waterskin);
                break;
            }
        }
    }

    public Badges.Badge masteryBadge() {
        switch (this.ordinal()) {
            case 0: {
                return Badges.Badge.MASTERY_WARRIOR;
            }
            case 1: {
                return Badges.Badge.MASTERY_MAGE;
            }
            case 2: {
                return Badges.Badge.MASTERY_ROGUE;
            }
            case 3: {
                return Badges.Badge.MASTERY_HUNTRESS;
            }
            case 4: {
                return Badges.Badge.MASTERY_DUELIST;
            }
            case 5: {
                return Badges.Badge.MASTERY_CLERIC;
            }
        }
        return null;
    }

    private static void initWarrior(Hero hero) {
        hero.belongings.weapon = new WornShortsword();
        hero.belongings.weapon.identify();
        ThrowingStone stones = new ThrowingStone();
        stones.identify().collect();
        Dungeon.quickslot.setSlot(0, stones);
        if (hero.belongings.armor != null) {
            hero.belongings.armor.affixSeal(new BrokenSeal());
            Catalog.setSeen(BrokenSeal.class);
        }
        new PotionOfHealing().identify();
        new ScrollOfRage().identify();
    }

    private static void initMage(Hero hero) {
        MagesStaff staff = new MagesStaff(new WandOfMagicMissile());
        hero.belongings.weapon = staff;
        hero.belongings.weapon.identify();
        hero.belongings.weapon.activate(hero);
        Dungeon.quickslot.setSlot(0, staff);
        new ScrollOfUpgrade().identify();
        new PotionOfLiquidFlame().identify();
    }

    private static void initRogue(Hero hero) {
        hero.belongings.weapon = new Dagger();
        hero.belongings.weapon.identify();
        CloakOfShadows cloak = new CloakOfShadows();
        hero.belongings.artifact = cloak;
        hero.belongings.artifact.identify();
        hero.belongings.artifact.activate(hero);
        ThrowingKnife knives = new ThrowingKnife();
        knives.identify().collect();
        Dungeon.quickslot.setSlot(0, cloak);
        Dungeon.quickslot.setSlot(1, knives);
        new ScrollOfMagicMapping().identify();
        new PotionOfInvisibility().identify();
    }

    private static void initHuntress(Hero hero) {
        hero.belongings.weapon = new Gloves();
        hero.belongings.weapon.identify();
        SpiritBow bow = new SpiritBow();
        bow.identify().collect();
        Dungeon.quickslot.setSlot(0, bow);
        new PotionOfMindVision().identify();
        new ScrollOfLullaby().identify();
    }

    private static void initDuelist(Hero hero) {
        hero.belongings.weapon = new Rapier();
        hero.belongings.weapon.identify();
        hero.belongings.weapon.activate(hero);
        ThrowingSpike spikes = new ThrowingSpike();
        spikes.quantity(2).identify().collect();
        Dungeon.quickslot.setSlot(0, hero.belongings.weapon);
        Dungeon.quickslot.setSlot(1, spikes);
        new PotionOfStrength().identify();
        new ScrollOfMirrorImage().identify();
    }

    private static void initCleric(Hero hero) {
        hero.belongings.weapon = new Cudgel();
        hero.belongings.weapon.identify();
        hero.belongings.weapon.activate(hero);
        HolyTome tome = new HolyTome();
        hero.belongings.artifact = tome;
        hero.belongings.artifact.identify();
        hero.belongings.artifact.activate(hero);
        Dungeon.quickslot.setSlot(0, tome);
        new PotionOfPurity().identify();
        new ScrollOfRemoveCurse().identify();
    }

    public String title() {
        return Messages.get(HeroClass.class, this.name(), new Object[0]);
    }

    public String desc() {
        return Messages.get(HeroClass.class, this.name() + "_desc", new Object[0]);
    }

    public String shortDesc() {
        return Messages.get(HeroClass.class, this.name() + "_desc_short", new Object[0]);
    }

    public HeroSubClass[] subClasses() {
        return this.subClasses;
    }

    public ArmorAbility[] armorAbilities() {
        switch (this.ordinal()) {
            default: {
                return new ArmorAbility[]{new HeroicLeap(), new Shockwave(), new Endure()};
            }
            case 1: {
                return new ArmorAbility[]{new ElementalBlast(), new WildMagic(), new WarpBeacon()};
            }
            case 2: {
                return new ArmorAbility[]{new SmokeBomb(), new DeathMark(), new ShadowClone()};
            }
            case 3: {
                return new ArmorAbility[]{new SpectralBlades(), new NaturesPower(), new SpiritHawk()};
            }
            case 4: {
                return new ArmorAbility[]{new Challenge(), new ElementalStrike(), new Feint()};
            }
            case 5: 
        }
        return new ArmorAbility[]{new AscendedForm(), new Trinity(), new PowerOfMany()};
    }

    public String spritesheet() {
        switch (this.ordinal()) {
            default: {
                return "sprites/warrior.png";
            }
            case 1: {
                return "sprites/mage.png";
            }
            case 2: {
                return "sprites/rogue.png";
            }
            case 3: {
                return "sprites/huntress.png";
            }
            case 4: {
                return "sprites/duelist.png";
            }
            case 5: 
        }
        return "sprites/cleric.png";
    }

    public String splashArt() {
        switch (this.ordinal()) {
            default: {
                return "splashes/warrior.jpg";
            }
            case 1: {
                return "splashes/mage.jpg";
            }
            case 2: {
                return "splashes/rogue.jpg";
            }
            case 3: {
                return "splashes/huntress.jpg";
            }
            case 4: {
                return "splashes/duelist.jpg";
            }
            case 5: 
        }
        return "splashes/cleric.jpg";
    }

    public boolean isUnlocked() {
        if (DeviceCompat.isDebug()) {
            return true;
        }
        switch (this.ordinal()) {
            default: {
                return true;
            }
            case 1: {
                return Badges.isUnlocked(Badges.Badge.UNLOCK_MAGE);
            }
            case 2: {
                return Badges.isUnlocked(Badges.Badge.UNLOCK_ROGUE);
            }
            case 3: {
                return Badges.isUnlocked(Badges.Badge.UNLOCK_HUNTRESS);
            }
            case 4: {
                return Badges.isUnlocked(Badges.Badge.UNLOCK_DUELIST);
            }
            case 5: 
        }
        return Badges.isUnlocked(Badges.Badge.UNLOCK_CLERIC);
    }

    public String unlockMsg() {
        return this.shortDesc() + "\n\n" + Messages.get(HeroClass.class, this.name() + "_unlock", new Object[0]);
    }
}

