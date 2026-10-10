/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Berserk;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.ElementalStrike;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Smite;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.MirrorImage;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfFuror;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Dazzling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Pressurized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wondrous;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Eldritch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Venomous;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vorpal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.RunicBlade;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Scimitar;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class Weapon
extends KindOfWeapon {
    public float ACC = 1.0f;
    public float DLY = 1.0f;
    public int RCH = 1;
    public Augment augment = Augment.NONE;
    protected float usesLeftToID = this.usesToID();
    protected float availableUsesToID = (float)this.usesToID() / 2.0f;
    public Enchantment enchantment;
    public boolean enchantHardened = false;
    public boolean curseInfusionBonus = false;
    public boolean masteryPotionBonus = false;
    private static final String USES_LEFT_TO_ID = "uses_left_to_id";
    private static final String AVAILABLE_USES = "available_uses";
    private static final String ENCHANTMENT = "enchantment";
    private static final String ENCHANT_HARDENED = "enchant_hardened";
    private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
    private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";
    private static final String AUGMENT = "augment";
    private static ItemSprite.Glowing HOLY = new ItemSprite.Glowing(0xFFFF00);

    protected int usesToID() {
        return 20;
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        if (attacker.buff(MagicImmune.class) == null) {
            Enchantment trinityEnchant = null;
            if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null && this instanceof MeleeWeapon && (attacker == Dungeon.hero || attacker instanceof MirrorImage || attacker instanceof ShadowClone.ShadowAlly)) {
                trinityEnchant = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).enchant();
                if (this.enchantment != null && trinityEnchant != null && trinityEnchant.getClass() == this.enchantment.getClass()) {
                    trinityEnchant = null;
                }
            }
            if (attacker instanceof Hero && this.isEquipped((Hero)attacker) && attacker.buff(HolyWeapon.HolyWepBuff.class) != null) {
                if (this.enchantment != null && (((Hero)attacker).subClass == HeroSubClass.PALADIN || this.hasCurseEnchant())) {
                    damage = this.enchantment.proc(this, attacker, defender, damage);
                }
                if (defender.isAlive() && trinityEnchant != null) {
                    damage = trinityEnchant.proc(this, attacker, defender, damage);
                }
                if (defender.isAlive()) {
                    int dmg = ((Hero)attacker).subClass == HeroSubClass.PALADIN ? 6 : 2;
                    defender.damage(Math.round((float)dmg * Enchantment.genericProcChanceMultiplier(attacker)), HolyWeapon.INSTANCE);
                }
            } else {
                if (this.enchantment != null) {
                    damage = this.enchantment.proc(this, attacker, defender, damage);
                }
                if (defender.isAlive() && trinityEnchant != null) {
                    damage = trinityEnchant.proc(this, attacker, defender, damage);
                }
            }
            if (attacker instanceof Hero && this.isEquipped((Hero)attacker) && attacker.buff(Smite.SmiteTracker.class) != null && defender.isAlive()) {
                defender.damage(Smite.bonusDmg((Hero)attacker, defender), Smite.INSTANCE);
            }
        }
        if (this instanceof MissileWeapon && ((MissileWeapon)this).durabilityLeft() <= ((MissileWeapon)this).durabilityPerUse() && ((MissileWeapon)this).parent == null) {
            return damage;
        }
        if (!this.levelKnown && attacker == Dungeon.hero) {
            float uses = Math.min(this.availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this));
            this.availableUsesToID -= uses;
            this.usesLeftToID -= uses;
            if (this.usesLeftToID <= 0.0f) {
                if (ShardOfOblivion.passiveIDDisabled()) {
                    if (this.usesLeftToID > -1.0f) {
                        GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), this.name());
                    }
                    this.setIDReady();
                } else {
                    this.identify();
                    GLog.p(Messages.get(Weapon.class, "identify", new Object[0]), new Object[0]);
                    Badges.validateItemLevelAquired(this);
                }
            }
        }
        return damage;
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        levelPercent *= Talent.itemIDSpeedFactor(hero, this);
        if (!this.levelKnown && (this.isEquipped(hero) || this instanceof MissileWeapon) && this.availableUsesToID <= (float)this.usesToID() / 2.0f) {
            this.availableUsesToID = Math.min((float)this.usesToID() / 2.0f, this.availableUsesToID + levelPercent * (float)this.usesToID());
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(USES_LEFT_TO_ID, this.usesLeftToID);
        bundle.put(AVAILABLE_USES, this.availableUsesToID);
        bundle.put(ENCHANTMENT, this.enchantment);
        bundle.put(ENCHANT_HARDENED, this.enchantHardened);
        bundle.put(CURSE_INFUSION_BONUS, this.curseInfusionBonus);
        bundle.put(MASTERY_POTION_BONUS, this.masteryPotionBonus);
        bundle.put(AUGMENT, this.augment);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.usesLeftToID = bundle.getFloat(USES_LEFT_TO_ID);
        this.availableUsesToID = bundle.getFloat(AVAILABLE_USES);
        this.enchantment = (Enchantment)bundle.get(ENCHANTMENT);
        this.enchantHardened = bundle.getBoolean(ENCHANT_HARDENED);
        this.curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
        this.masteryPotionBonus = bundle.getBoolean(MASTERY_POTION_BONUS);
        this.augment = bundle.getEnum(AUGMENT, Augment.class);
    }

    @Override
    public void reset() {
        super.reset();
        this.usesLeftToID = this.usesToID();
        this.availableUsesToID = (float)this.usesToID() / 2.0f;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (Dungeon.hero != null && Dungeon.hero.isAlive() && this.isIdentified() && this.enchantment != null) {
                Catalog.setSeen(this.enchantment.getClass());
                Statistics.itemTypesDiscovered.add(this.enchantment.getClass());
            }
            return true;
        }
        return false;
    }

    @Override
    public Item identify(boolean byHero) {
        if (this.enchantment != null && byHero && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Catalog.setSeen(this.enchantment.getClass());
            Statistics.itemTypesDiscovered.add(this.enchantment.getClass());
        }
        return super.identify(byHero);
    }

    public void setIDReady() {
        this.usesLeftToID = -1.0f;
    }

    public boolean readyToIdentify() {
        return !this.isIdentified() && this.usesLeftToID <= 0.0f;
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        int encumbrance = 0;
        if (owner instanceof Hero) {
            encumbrance = this.STRReq() - ((Hero)owner).STR();
        }
        float ACC = this.ACC;
        if (owner.buff(Wayward.WaywardBuff.class) != null && this.enchantment instanceof Wayward) {
            ACC /= 5.0f;
        }
        return encumbrance > 0 ? (float)((double)ACC / Math.pow(1.5, encumbrance)) : ACC;
    }

    @Override
    public float delayFactor(Char owner) {
        return this.baseDelay(owner) * (1.0f / this.speedMultiplier(owner));
    }

    protected float baseDelay(Char owner) {
        int encumbrance;
        float delay = this.augment.delayFactor(this.DLY);
        if (owner instanceof Hero && (encumbrance = this.STRReq() - ((Hero)owner).STR()) > 0) {
            delay = (float)((double)delay * Math.pow(1.2, encumbrance));
        }
        return delay;
    }

    protected float speedMultiplier(Char owner) {
        float multi = RingOfFuror.attackSpeedMultiplier(owner);
        if (owner.buff(Scimitar.SwordDance.class) != null) {
            multi += 0.6f;
        }
        return multi;
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = this.RCH;
        if (owner instanceof Hero && RingOfForce.fightingUnarmed((Hero)owner)) {
            reach = 1;
            if (!RingOfForce.unarmedGetsWeaponEnchantment((Hero)owner)) {
                return reach;
            }
        }
        if (owner instanceof Hero && owner.buff(AscendedForm.AscendBuff.class) != null) {
            reach += 2;
        }
        if (this.hasEnchant(Projecting.class, owner)) {
            return reach + Math.round(Enchantment.genericProcChanceMultiplier(owner));
        }
        return reach;
    }

    public int STRReq() {
        return this.STRReq(this.level());
    }

    public abstract int STRReq(int var1);

    protected static int STRReq(int tier, int lvl) {
        lvl = Math.max(0, lvl);
        return 8 + tier * 2 - (int)(Math.sqrt(8 * lvl + 1) - 1.0) / 2;
    }

    @Override
    public int level() {
        int level = super.level();
        if (this.curseInfusionBonus) {
            level += 1 + level / 6;
        }
        return level;
    }

    @Override
    public Item upgrade() {
        return this.upgrade(false);
    }

    public Item upgrade(boolean enchant) {
        if (enchant) {
            if (this.enchantment == null) {
                this.enchant(Enchantment.random(new Class[0]));
            }
        } else if (this.enchantment != null) {
            if (this.enchantHardened) {
                if (this.level() >= 6 && (double)Random.Float(10.0f) < Math.pow(2.0, this.level() - 6)) {
                    this.enchantHardened = false;
                }
            } else if (this.hasCurseEnchant()) {
                if (Random.Int(3) == 0) {
                    this.enchant(null);
                }
            } else if (this.level() >= 4 && (double)Random.Float(10.0f) < Math.pow(2.0, this.level() - 4)) {
                this.enchant(null);
            }
            if (this.enchantment instanceof Crystal) {
                ((Crystal)this.enchantment).repair(this, false, 100.0f);
            }
        }
        this.cursed = false;
        return super.upgrade();
    }

    @Override
    public String name() {
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.enchantment == null)) {
            return Messages.get(HolyWeapon.class, "ench_name", super.name());
        }
        return this.enchantment != null && (this.cursedKnown || !this.enchantment.curse()) ? this.enchantment.name(super.name()) : super.name();
    }

    @Override
    public Item random() {
        int n = 0;
        if (Random.Int(4) == 0) {
            ++n;
            if (Random.Int(5) == 0) {
                ++n;
            }
        }
        this.level(n);
        Random.pushGenerator(Random.Long());
        float effectRoll = Random.Float();
        if (effectRoll < 0.3f * ParchmentScrap.curseChanceMultiplier()) {
            this.enchant(Enchantment.randomCurse(new Class[0]));
            this.cursed = true;
        } else if (effectRoll >= 1.0f - 0.1f * ParchmentScrap.enchantChanceMultiplier()) {
            this.enchant();
        }
        Random.popGenerator();
        return this;
    }

    public Weapon enchant(Enchantment ench) {
        if (ench == null || !ench.curse()) {
            this.curseInfusionBonus = false;
        }
        this.enchantment = ench;
        Weapon.updateQuickslot();
        if (ench != null && this.isIdentified() && Dungeon.hero != null && Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)) {
            Catalog.setSeen(ench.getClass());
            Statistics.itemTypesDiscovered.add(ench.getClass());
        }
        return this;
    }

    public Weapon enchant() {
        Class<?> oldEnchantment = this.enchantment != null ? this.enchantment.getClass() : null;
        Enchantment ench = Enchantment.random(oldEnchantment);
        return this.enchant(ench);
    }

    public boolean hasEnchant(Class<? extends Enchantment> type, Char owner) {
        if (owner.buff(MagicImmune.class) != null) {
            return false;
        }
        if (this.enchantment != null && !this.enchantment.curse() && owner instanceof Hero && this.isEquipped((Hero)owner) && owner.buff(HolyWeapon.HolyWepBuff.class) != null && ((Hero)owner).subClass != HeroSubClass.PALADIN) {
            return false;
        }
        if (owner.buff(BodyForm.BodyFormBuff.class) != null && owner.buff(BodyForm.BodyFormBuff.class).enchant() != null && owner.buff(BodyForm.BodyFormBuff.class).enchant().getClass().equals(type)) {
            return true;
        }
        if (this.enchantment != null) {
            return this.enchantment.getClass() == type;
        }
        return false;
    }

    public boolean hasGoodEnchant() {
        return this.enchantment != null && !this.enchantment.curse();
    }

    public boolean hasCurseEnchant() {
        return this.enchantment != null && this.enchantment.curse();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.enchantment == null)) {
            return HOLY;
        }
        return this.enchantment != null && (this.cursedKnown || !this.enchantment.curse()) ? this.enchantment.glowing() : null;
    }

    public static enum Augment {
        SPEED(0.7f, 0.6666667f),
        DAMAGE(1.5f, 1.6666666f),
        NONE(1.0f, 1.0f);

        private float damageFactor;
        private float delayFactor;

        private Augment(float dmg, float dly) {
            this.damageFactor = dmg;
            this.delayFactor = dly;
        }

        public int damageFactor(int dmg) {
            return Math.round((float)dmg * this.damageFactor);
        }

        public float damageFactor(float dmg) {
            return dmg * this.damageFactor;
        }

        public float delayFactor(float dly) {
            return dly * this.delayFactor;
        }
    }

    public static abstract class Enchantment
    implements Bundlable {
        public static final Class<?>[] common = new Class[]{Blazing.class, Chilling.class, Kinetic.class, Shocking.class, Venomous.class};
        public static final Class<?>[] uncommon = new Class[]{Blocking.class, Blooming.class, Eldritch.class, Elastic.class, Lucky.class, Projecting.class, Unstable.class, Vorpal.class};
        public static final Class<?>[] rare = new Class[]{Corrupting.class, Crystal.class, Grim.class, Vampiric.class};
        public static final float[] typeChances = new float[]{50.0f, 40.0f, 10.0f};
        public static final Class<?>[] curses = new Class[]{Annoying.class, Displacing.class, Dazzling.class, Explosive.class, Friendly.class, Polarized.class, Pressurized.class, Sacrificial.class, Wayward.class, Wondrous.class};

        public abstract int proc(Weapon var1, Char var2, Char var3, int var4);

        protected float procChanceMultiplier(Char attacker) {
            return Enchantment.genericProcChanceMultiplier(attacker);
        }

        public static float genericProcChanceMultiplier(Char attacker) {
            float multi = RingOfArcana.enchantPowerMultiplier(attacker);
            Berserk rage = attacker.buff(Berserk.class);
            if (rage != null) {
                multi = rage.enchantFactor(multi);
            }
            if (attacker.buff(RunicBlade.RunicSlashTracker.class) != null) {
                multi += attacker.buff(RunicBlade.RunicSlashTracker.class).boost;
                attacker.buff(RunicBlade.RunicSlashTracker.class).detach();
            }
            if (attacker.buff(Smite.SmiteTracker.class) != null) {
                multi += 3.0f;
            }
            if (attacker.buff(ElementalStrike.DirectedPowerTracker.class) != null) {
                multi += attacker.buff(ElementalStrike.DirectedPowerTracker.class).enchBoost;
                attacker.buff(ElementalStrike.DirectedPowerTracker.class).detach();
            }
            if (attacker.buff(Talent.SpiritBladesTracker.class) != null && ((Hero)attacker).pointsInTalent(Talent.SPIRIT_BLADES) == 4) {
                multi += 0.1f;
            }
            if (attacker.buff(Talent.StrikingWaveTracker.class) != null && ((Hero)attacker).pointsInTalent(Talent.STRIKING_WAVE) == 4) {
                multi += 0.2f;
            }
            return multi;
        }

        public String name() {
            if (!this.curse()) {
                return this.name(Messages.get(this, "enchant", new Object[0]));
            }
            return this.name(Messages.get(Item.class, "curse", new Object[0]));
        }

        public String name(String weaponName) {
            return Messages.get(this, "name", weaponName);
        }

        public String desc() {
            return Messages.get(this, "desc", new Object[0]);
        }

        public boolean curse() {
            return false;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
        }

        @Override
        public void storeInBundle(Bundle bundle) {
        }

        public abstract ItemSprite.Glowing glowing();

        public static Enchantment random(Class<? extends Enchantment> ... toIgnore) {
            switch (Random.chances(typeChances)) {
                default: {
                    return Enchantment.randomCommon(toIgnore);
                }
                case 1: {
                    return Enchantment.randomUncommon(toIgnore);
                }
                case 2: 
            }
            return Enchantment.randomRare(toIgnore);
        }

        public static Enchantment randomCommon(Class<? extends Enchantment> ... toIgnore) {
            ArrayList<Class<Class<? extends Enchantment>>> enchants = new ArrayList<Class<Class<? extends Enchantment>>>(Arrays.asList(common));
            enchants.removeAll(Arrays.asList(toIgnore));
            if (enchants.isEmpty()) {
                return Enchantment.random(new Class[0]);
            }
            return (Enchantment)Reflection.newInstance(Random.element(enchants));
        }

        public static Enchantment randomUncommon(Class<? extends Enchantment> ... toIgnore) {
            ArrayList<Class<Class<? extends Enchantment>>> enchants = new ArrayList<Class<Class<? extends Enchantment>>>(Arrays.asList(uncommon));
            enchants.removeAll(Arrays.asList(toIgnore));
            if (enchants.isEmpty()) {
                return Enchantment.random(new Class[0]);
            }
            return (Enchantment)Reflection.newInstance(Random.element(enchants));
        }

        public static Enchantment randomRare(Class<? extends Enchantment> ... toIgnore) {
            ArrayList<Class<Class<? extends Enchantment>>> enchants = new ArrayList<Class<Class<? extends Enchantment>>>(Arrays.asList(rare));
            enchants.removeAll(Arrays.asList(toIgnore));
            if (enchants.isEmpty()) {
                return Enchantment.random(new Class[0]);
            }
            return (Enchantment)Reflection.newInstance(Random.element(enchants));
        }

        public static Enchantment randomCurse(Class<? extends Enchantment> ... toIgnore) {
            ArrayList<Class<Class<? extends Enchantment>>> enchants = new ArrayList<Class<Class<? extends Enchantment>>>(Arrays.asList(curses));
            enchants.removeAll(Arrays.asList(toIgnore));
            if (enchants.isEmpty()) {
                return Enchantment.random(new Class[0]);
            }
            return (Enchantment)Reflection.newInstance(Random.element(enchants));
        }
    }
}

