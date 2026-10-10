/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.quest.vault.VaultBossElemental;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.InventoryPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class MissileWeapon
extends Weapon {
    public long setID;
    public boolean spawnedForEffect;
    protected boolean sticky;
    public static final float MAX_DURABILITY = 100.0f;
    protected float durability;
    protected float baseUses;
    public boolean holster;
    public MissileWeapon parent;
    public int tier;
    public boolean extraThrownLeft;
    protected boolean useRoundingInDurabilityCalc;
    private static final String SET_ID = "set_id";
    private static final String SPAWNED = "spawned";
    private static final String DURABILITY = "durability";
    private static final String EXTRA_LEFT = "extra_left";
    private static boolean bundleRestoring = false;

    public MissileWeapon() {
        this.stackable = true;
        this.quantity = this.defaultQuantity();
        this.bones = true;
        this.defaultAction = "THROW";
        this.usesTargeting = true;
        this.setID = new SecureRandom().nextLong();
        this.spawnedForEffect = false;
        this.sticky = true;
        this.durability = 100.0f;
        this.baseUses = 8.0f;
        this.extraThrownLeft = false;
        this.useRoundingInDurabilityCalc = true;
    }

    @Override
    protected int usesToID() {
        return 10;
    }

    @Override
    public int min() {
        if (Dungeon.hero != null) {
            return Math.max(0, this.min(this.buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)));
        }
        return Math.max(0, this.min(this.buffedLvl()));
    }

    @Override
    public int min(int lvl) {
        return 2 * this.tier + lvl;
    }

    @Override
    public int max() {
        if (Dungeon.hero != null) {
            return Math.max(0, this.max(this.buffedLvl() + RingOfSharpshooting.levelDamageBonus(Dungeon.hero)));
        }
        return Math.max(0, this.max(this.buffedLvl()));
    }

    @Override
    public int max(int lvl) {
        return 5 * this.tier + this.tier * lvl;
    }

    @Override
    public int STRReq(int lvl) {
        int req = MissileWeapon.STRReq(this.tier, lvl) - 1;
        if (this.masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }

    @Override
    public int buffedLvl() {
        if (this.parent != null) {
            return this.parent.buffedLvl();
        }
        return super.buffedLvl();
    }

    @Override
    public Item upgrade(boolean enchant) {
        if (!bundleRestoring) {
            this.durability = 100.0f;
            this.extraThrownLeft = false;
            this.quantity = this.defaultQuantity();
            Buff.affect((Char)Dungeon.hero, UpgradedSetTracker.class).levelThresholds.put(this.setID, this.trueLevel() + 1);
        }
        boolean wasCursed = this.cursed;
        super.upgrade(enchant);
        if (wasCursed && this.hasCurseEnchant()) {
            this.cursed = wasCursed;
        }
        return this;
    }

    @Override
    public Item upgrade() {
        if (!bundleRestoring) {
            this.durability = 100.0f;
            this.extraThrownLeft = false;
            this.quantity = this.defaultQuantity();
            Buff.affect((Char)Dungeon.hero, UpgradedSetTracker.class).levelThresholds.put(this.setID, this.trueLevel() + 1);
        }
        return super.upgrade();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove("EQUIP");
        return actions;
    }

    @Override
    public boolean collect(Bag container) {
        if (container instanceof MagicalHolster) {
            this.holster = true;
        }
        return super.collect(container);
    }

    @Override
    public boolean isSimilar(Item item) {
        return this.trueLevel() == item.trueLevel() && this.getClass() == item.getClass() && this.setID == ((MissileWeapon)item).setID;
    }

    @Override
    public int throwPos(Hero user, int dst) {
        SpiritBow bow;
        int projecting = 0;
        if (this.hasEnchant(Projecting.class, user)) {
            projecting += 4;
        }
        if (!(this instanceof SpiritBow.SpiritArrow) && Random.Int(3) < user.pointsInTalent(Talent.SHARED_ENCHANTMENT) && (bow = Dungeon.hero.belongings.getItem(SpiritBow.class)) != null && bow.hasEnchant(Projecting.class, user)) {
            projecting += 4;
        }
        if (projecting > 0 && (Dungeon.level.passable[dst] || Dungeon.level.avoid[dst] || Actor.findChar(dst) != null) && Dungeon.level.distance(user.pos, dst) <= Math.round((float)projecting * Weapon.Enchantment.genericProcChanceMultiplier(user))) {
            return dst;
        }
        return super.throwPos(user, dst);
    }

    @Override
    public float accuracyFactor(Char owner, Char target) {
        float accFactor = super.accuracyFactor(owner, target);
        return accFactor *= this.adjacentAccFactor(owner, target);
    }

    protected float adjacentAccFactor(Char owner, Char target) {
        if (Dungeon.level.adjacent(owner.pos, target.pos)) {
            if (owner instanceof Hero) {
                return 0.5f + 0.25f * (float)((Hero)owner).pointsInTalent(Talent.POINT_BLANK);
            }
            return 0.5f;
        }
        return 1.5f;
    }

    @Override
    public void doThrow(final Hero hero) {
        this.parent = null;
        if ((this.levelKnown && this.level() > 0 || this.hasGoodEnchant() || this.masteryPotionBonus || this.enchantHardened) && !this.extraThrownLeft && this.quantity() == 1 && this.durabilityLeft() <= this.durabilityPerUse()) {
            GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.title()), Messages.get(MissileWeapon.class, "break_upgraded_warn_desc", new Object[0]), new String[]{Messages.get(MissileWeapon.class, "break_upgraded_warn_yes", new Object[0]), Messages.get(MissileWeapon.class, "break_upgraded_warn_no", new Object[0])}){

                @Override
                protected void onSelect(int index) {
                    if (index == 0) {
                        MissileWeapon.super.doThrow(hero);
                    } else {
                        QuickSlotButton.cancel();
                        InventoryPane.cancelTargeting();
                    }
                }

                @Override
                public void onBackPressed() {
                    super.onBackPressed();
                    QuickSlotButton.cancel();
                    InventoryPane.cancelTargeting();
                }
            });
        } else {
            super.doThrow(hero);
        }
    }

    @Override
    protected void onThrow(int cell) {
        Char enemy = Actor.findChar(cell);
        if (enemy == null || enemy == curUser) {
            this.parent = null;
            if (curUser.hasTalent(Talent.SEER_SHOT) && MissileWeapon.curUser.heroClass != HeroClass.HUNTRESS && curUser.buff(Talent.SeerShotCooldown.class) == null && Actor.findChar(cell) == null) {
                RevealedArea a = Buff.affect(curUser, RevealedArea.class, 5 * curUser.pointsInTalent(Talent.SEER_SHOT));
                a.depth = Dungeon.depth;
                a.pos = cell;
                Buff.affect(curUser, Talent.SeerShotCooldown.class, 20.0f);
            }
            if (!this.spawnedForEffect) {
                super.onThrow(cell);
            }
        } else if (!curUser.shoot(enemy, this)) {
            this.rangedMiss(cell);
        } else {
            this.rangedHit(enemy, cell);
        }
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        SpiritBow bow;
        if (attacker == Dungeon.hero && Random.Int(3) < Dungeon.hero.pointsInTalent(Talent.SHARED_ENCHANTMENT) && (bow = Dungeon.hero.belongings.getItem(SpiritBow.class)) != null && bow.enchantment != null && Dungeon.hero.buff(MagicImmune.class) == null) {
            damage = bow.enchantment instanceof Crystal ? bow.enchantment.proc(bow, attacker, defender, damage) : bow.enchantment.proc(this, attacker, defender, damage);
        }
        if ((this.cursed || this.hasCurseEnchant()) && !this.cursedKnown) {
            GLog.n(Messages.get(this, "curse_discover", new Object[0]), new Object[0]);
        }
        this.cursedKnown = true;
        if (this.parent != null) {
            this.parent.cursedKnown = true;
        }
        if (attacker == Dungeon.hero && Dungeon.hero.pointsInTalent(Talent.SURVIVALISTS_INTUITION) == 2) {
            this.usesLeftToID = Math.min(this.usesLeftToID, 0.0f);
            this.availableUsesToID = Math.max(this.usesLeftToID, 0.0f);
        }
        int result = super.proc(attacker, defender, damage);
        if (this.parent != null && this.parent.usesLeftToID > this.usesLeftToID) {
            float diff = this.parent.usesLeftToID - this.usesLeftToID;
            this.parent.usesLeftToID -= diff;
            this.parent.availableUsesToID -= diff;
            if (this.usesLeftToID <= 0.0f) {
                if (ShardOfOblivion.passiveIDDisabled()) {
                    this.parent.setIDReady();
                } else {
                    this.parent.identify();
                }
            }
        } else if (this.parent != null && this.isIdentified() && !this.parent.isIdentified()) {
            this.parent.identify();
        }
        if (attacker == Dungeon.hero && !this.isIdentified() && ShardOfOblivion.passiveIDDisabled()) {
            Buff.prolong(Dungeon.hero, ShardOfOblivion.ThrownUseTracker.class, 50.0f);
        }
        return result;
    }

    @Override
    public Item virtual() {
        Item item = super.virtual();
        ((MissileWeapon)item).setID = this.setID;
        return item;
    }

    public int defaultQuantity() {
        return 3;
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
            this.enchant(Weapon.Enchantment.randomCurse(new Class[0]));
            this.cursed = true;
        } else if (effectRoll >= 1.0f - 0.1f * ParchmentScrap.enchantChanceMultiplier()) {
            this.enchant();
        }
        Random.popGenerator();
        return this;
    }

    @Override
    public String status() {
        return Integer.toString(this.quantity);
    }

    @Override
    public float castDelay(Char user, int cell) {
        if (Actor.findChar(cell) != null && Actor.findChar(cell) != user) {
            return this.delayFactor(user);
        }
        return super.castDelay(user, cell);
    }

    protected void rangedHit(Char enemy, int cell) {
        this.decrementDurability();
        if (this.durability > 0.0f && !this.spawnedForEffect) {
            boolean doesStick;
            boolean bl = doesStick = this.sticky || enemy instanceof VaultBossElemental;
            if (doesStick && enemy != null && enemy.isActive() && enemy.alignment != Char.Alignment.ALLY) {
                PinCushion p = Buff.affect(enemy, PinCushion.class);
                if (p.target == enemy) {
                    p.stick(this);
                    return;
                }
            }
            Dungeon.level.drop((Item)this, (int)cell).sprite.drop();
        }
    }

    protected void rangedMiss(int cell) {
        this.parent = null;
        if (!this.spawnedForEffect) {
            super.onThrow(cell);
        }
    }

    public float durabilityLeft() {
        return this.durability;
    }

    public void repair(float amount) {
        this.durability += amount;
        this.durability = Math.min(this.durability, 100.0f);
    }

    public void damage(float amount) {
        this.durability -= amount;
        this.durability = Math.max(this.durability, 1.0f);
    }

    public final float durabilityPerUse() {
        return this.durabilityPerUse(this.level());
    }

    public float durabilityPerUse(int level) {
        float usages = this.baseUses * (float)Math.pow(1.5, level);
        if (Dungeon.hero != null && Dungeon.hero.hasTalent(Talent.DURABLE_PROJECTILES)) {
            usages *= 1.25f + 0.25f * (float)Dungeon.hero.pointsInTalent(Talent.DURABLE_PROJECTILES);
        }
        if (this.holster) {
            usages *= 1.2f;
        }
        usages /= this.augment.delayFactor(1.0f);
        if (Dungeon.hero != null) {
            usages *= RingOfSharpshooting.durabilityMultiplier(Dungeon.hero);
        }
        if (this.enchantment instanceof Crystal) {
            usages = Math.min(usages / 2.0f, 50.0f);
        }
        if (usages >= 100.0f) {
            return 0.0f;
        }
        if (this.useRoundingInDurabilityCalc) {
            usages = Math.round(usages);
            return 100.0f / usages + 0.001f;
        }
        return 100.0f / usages;
    }

    @Override
    public Weapon enchant(Weapon.Enchantment ench) {
        if (ench instanceof Crystal) {
            ((Crystal)ench).setThrownWep();
            if (this.durability < 100.0f) {
                Buff.affect(Dungeon.hero, Crystal.CrystalRepair.class);
            }
        }
        if (ench == null) {
            Buff.affect((Char)Dungeon.hero, UpgradedSetTracker.class).appliedEnchants.remove(this.setID);
        } else {
            Buff.affect((Char)Dungeon.hero, UpgradedSetTracker.class).appliedEnchants.put(this.setID, ench.getClass());
        }
        return super.enchant(ench);
    }

    protected void decrementDurability() {
        if (this.parent != null) {
            if (this.parent.durability <= this.parent.durabilityPerUse()) {
                this.durability = 0.0f;
                this.parent.durability = 100.0f;
                this.parent.extraThrownLeft = false;
                if (this.parent.durabilityPerUse() < 100.0f) {
                    GLog.n(Messages.get(this, "has_broken", new Object[0]), new Object[0]);
                }
            } else {
                this.parent.durability -= this.parent.durabilityPerUse();
                if (this.parent.durability > 0.0f && this.parent.durability <= this.parent.durabilityPerUse()) {
                    GLog.w(Messages.get(this, "about_to_break", new Object[0]), new Object[0]);
                }
            }
            this.parent = null;
        } else {
            this.durability -= this.durabilityPerUse();
            if (this.durability > 0.0f && this.durability <= this.durabilityPerUse()) {
                GLog.w(Messages.get(this, "about_to_break", new Object[0]), new Object[0]);
            } else if (this.durabilityPerUse() < 100.0f && this.durability <= 0.0f) {
                GLog.n(Messages.get(this, "has_broken", new Object[0]), new Object[0]);
            }
        }
    }

    @Override
    public int damageRoll(Char owner) {
        int damage = this.augment.damageFactor(super.damageRoll(owner));
        if (owner instanceof Hero) {
            int exStr = ((Hero)owner).STR() - this.STRReq();
            if (exStr > 0) {
                damage += Hero.heroDamageIntRange(0, exStr);
            }
            if (owner.buff(Momentum.class) != null && owner.buff(Momentum.class).freerunning()) {
                damage = Math.round((float)damage * (1.0f + 0.15f * (float)((Hero)owner).pointsInTalent(Talent.PROJECTILE_MOMENTUM)));
            }
        }
        return damage;
    }

    @Override
    public void reset() {
        super.reset();
        this.durability = 100.0f;
    }

    @Override
    public Item merge(Item other) {
        super.merge(other);
        if (this.isSimilar(other)) {
            this.extraThrownLeft = false;
            this.durability += ((MissileWeapon)other).durability;
            this.durability -= 100.0f;
            while (this.durability <= 0.0f) {
                --this.quantity;
                this.durability += 100.0f;
            }
            if (this.quantity > this.defaultQuantity() && this.setID != 0L && this.setID != (long)this.getClass().getSimpleName().hashCode()) {
                this.quantity = this.defaultQuantity();
                this.durability = 100.0f;
            }
            this.levelKnown = this.levelKnown || other.levelKnown;
            boolean bl = this.cursedKnown = this.cursedKnown || other.cursedKnown;
            if (((Weapon)other).readyToIdentify()) {
                this.setIDReady();
            }
            this.masteryPotionBonus = this.masteryPotionBonus || ((MissileWeapon)other).masteryPotionBonus;
            boolean bl2 = this.enchantHardened = this.enchantHardened || ((MissileWeapon)other).enchantHardened;
            if (!this.curseInfusionBonus && ((MissileWeapon)other).curseInfusionBonus && ((MissileWeapon)other).hasCurseEnchant()) {
                this.enchantment = ((MissileWeapon)other).enchantment;
                this.curseInfusionBonus = true;
                this.cursed = this.cursed || other.cursed;
            } else if (!this.curseInfusionBonus && !this.hasGoodEnchant() && ((MissileWeapon)other).hasGoodEnchant()) {
                this.enchantment = ((MissileWeapon)other).enchantment;
                this.cursed = other.cursed;
            } else if (!this.curseInfusionBonus && this.hasCurseEnchant() && !((MissileWeapon)other).hasCurseEnchant()) {
                this.enchantment = ((MissileWeapon)other).enchantment;
                this.cursed = other.cursed;
            }
            if (((MissileWeapon)other).enchantment instanceof Explosive && this.enchantment instanceof Explosive) {
                ((Explosive)this.enchantment).merge((Explosive)((MissileWeapon)other).enchantment);
            }
        }
        return this;
    }

    @Override
    public Item split(int amount) {
        bundleRestoring = true;
        Item split = super.split(amount);
        bundleRestoring = false;
        if (split != null) {
            MissileWeapon m = (MissileWeapon)split;
            m.durability = 100.0f;
            m.parent = this;
            m.extraThrownLeft = true;
            this.extraThrownLeft = true;
            if (m.enchantment instanceof Explosive) {
                ((Explosive)m.enchantment).clear();
            }
        }
        return split;
    }

    @Override
    public boolean doPickUp(Hero hero, int pos) {
        this.parent = null;
        if (!UpgradedSetTracker.pickupValid(hero, this)) {
            Sample.INSTANCE.play("sounds/item.mp3");
            hero.spendAndNext(this.pickupDelay());
            GLog.w(Messages.get(this, "dust", new Object[0]), new Object[0]);
            this.quantity(0);
            return true;
        }
        this.extraThrownLeft = false;
        UpgradedSetTracker.filterEnchantOnPickup(hero, this);
        return super.doPickUp(hero, pos);
    }

    @Override
    public boolean isIdentified() {
        return this.levelKnown && this.cursedKnown;
    }

    @Override
    public String info() {
        Object info = super.info();
        if (this.levelKnown) {
            info = (String)info + "\n\n" + Messages.get(MissileWeapon.class, "stats_known", this.tier, this.augment.damageFactor(this.min()), this.augment.damageFactor(this.max()), this.STRReq());
            if (Dungeon.hero != null) {
                if (this.STRReq() > Dungeon.hero.STR()) {
                    info = (String)info + " " + Messages.get(Weapon.class, "too_heavy", new Object[0]);
                } else if (Dungeon.hero.STR() > this.STRReq()) {
                    info = (String)info + " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - this.STRReq());
                }
            }
        } else {
            info = (String)info + "\n\n" + Messages.get(MissileWeapon.class, "stats_unknown", this.tier, this.min(0), this.max(0), this.STRReq(0));
            if (Dungeon.hero != null && this.STRReq(0) > Dungeon.hero.STR()) {
                info = (String)info + " " + Messages.get(MissileWeapon.class, "probably_too_heavy", new Object[0]);
            }
        }
        if (this.enchantment != null && (this.cursedKnown || !this.enchantment.curse())) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", this.enchantment.name()));
            if (this.enchantHardened) {
                info = (String)info + " " + Messages.get(Weapon.class, "enchant_hardened", new Object[0]);
            }
            info = (String)info + " " + this.enchantment.desc();
        } else if (this.enchantHardened) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant", new Object[0]);
        }
        if (this.cursedKnown && this.cursed) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "cursed", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "not_cursed", new Object[0]);
        }
        info = (String)info + "\n\n";
        String statsInfo = this.statsInfo();
        if (!statsInfo.equals("")) {
            info = (String)info + statsInfo + " ";
        }
        info = (String)info + Messages.get(MissileWeapon.class, "distance", new Object[0]);
        switch (this.augment) {
            case SPEED: {
                info = (String)info + " " + Messages.get(Weapon.class, "faster", new Object[0]);
                break;
            }
            case DAMAGE: {
                info = (String)info + " " + Messages.get(Weapon.class, "stronger", new Object[0]);
                break;
            }
        }
        info = this.levelKnown ? (this.durabilityPerUse() > 0.0f ? (String)info + "\n\n" + Messages.get(this, "uses_left", (int)Math.ceil(this.durability / this.durabilityPerUse()), (int)Math.ceil(100.0f / this.durabilityPerUse())) : (String)info + "\n\n" + Messages.get(this, "unlimited_uses", new Object[0])) : (this.durabilityPerUse(0) > 0.0f ? (String)info + "\n\n" + Messages.get(this, "unknown_uses", (int)Math.ceil(100.0f / this.durabilityPerUse(0))) : (String)info + "\n\n" + Messages.get(this, "unlimited_uses", new Object[0]));
        return info;
    }

    public String statsInfo() {
        return Messages.get(this, "stats_desc", new Object[0]);
    }

    @Override
    public int value() {
        int price = 5 * this.tier * this.quantity;
        if (this.hasGoodEnchant()) {
            price = (int)((double)price * 1.5);
        }
        if (this.cursedKnown && (this.cursed || this.hasCurseEnchant())) {
            price /= 2;
        }
        if (this.levelKnown && this.level() > 0) {
            price *= this.level() + 1;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SET_ID, this.setID);
        bundle.put(SPAWNED, this.spawnedForEffect);
        bundle.put(DURABILITY, this.durability);
        bundle.put(EXTRA_LEFT, this.extraThrownLeft);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        bundleRestoring = true;
        super.restoreFromBundle(bundle);
        bundleRestoring = false;
        if (bundle.contains(SET_ID)) {
            this.setID = bundle.getLong(SET_ID);
        } else if (this.level() > 0) {
            this.quantity = this.defaultQuantity();
        } else if (!(this instanceof Dart)) {
            this.cursedKnown = true;
            this.levelKnown = true;
            this.setID = this.getClass().getSimpleName().hashCode();
        }
        this.spawnedForEffect = bundle.getBoolean(SPAWNED);
        this.durability = bundle.getFloat(DURABILITY);
        this.extraThrownLeft = bundle.getBoolean(EXTRA_LEFT);
    }

    public static class UpgradedSetTracker
    extends Buff {
        public HashMap<Long, Integer> levelThresholds;
        public HashMap<Long, Class<? extends Weapon.Enchantment>> appliedEnchants;
        public static final String SET_IDS = "set_ids";
        public static final String SET_LEVELS = "set_levels";
        public static final String SET_IDS_ENCHANTS = "set_ids_enchants";
        public static final String SET_ENCHANTS = "set_enchants";

        public UpgradedSetTracker() {
            this.revivePersists = true;
            this.levelThresholds = new HashMap();
            this.appliedEnchants = new HashMap();
        }

        public static boolean pickupValid(Hero h, MissileWeapon w) {
            if (h.buff(UpgradedSetTracker.class) != null) {
                HashMap<Long, Integer> levelThresholds = h.buff(UpgradedSetTracker.class).levelThresholds;
                if (levelThresholds.containsKey(w.setID)) {
                    return w.trueLevel() >= levelThresholds.get(w.setID);
                }
                return true;
            }
            return true;
        }

        public static void filterEnchantOnPickup(Hero h, MissileWeapon w) {
            Class<? extends Weapon.Enchantment> enchantCLS;
            if (h.buff(UpgradedSetTracker.class) != null && (enchantCLS = h.buff(UpgradedSetTracker.class).appliedEnchants.get(w.setID)) != null && w.enchantment != null && w.enchantment.getClass() != enchantCLS) {
                w.enchantment = null;
                w.curseInfusionBonus = false;
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            long[] IDs = new long[this.levelThresholds.size()];
            int[] levels = new int[this.levelThresholds.size()];
            int i = 0;
            for (Long ID : this.levelThresholds.keySet()) {
                IDs[i] = ID;
                levels[i] = this.levelThresholds.get(ID);
                ++i;
            }
            bundle.put(SET_IDS, IDs);
            bundle.put(SET_LEVELS, levels);
            IDs = new long[this.appliedEnchants.size()];
            Class[] enchants = new Class[this.appliedEnchants.size()];
            i = 0;
            for (Long ID : this.appliedEnchants.keySet()) {
                IDs[i] = ID;
                enchants[i] = this.appliedEnchants.get(ID);
                ++i;
            }
            bundle.put(SET_IDS_ENCHANTS, IDs);
            bundle.put(SET_ENCHANTS, enchants);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            long[] IDs = bundle.getLongArray(SET_IDS);
            int[] levels = bundle.getIntArray(SET_LEVELS);
            this.levelThresholds.clear();
            for (int i = 0; i < IDs.length; ++i) {
                this.levelThresholds.put(IDs[i], levels[i]);
            }
            if (!bundle.contains(SET_IDS_ENCHANTS)) {
                this.appliedEnchants = new HashMap();
            } else {
                IDs = bundle.getLongArray(SET_IDS_ENCHANTS);
                Class[] enchants = bundle.getClassArray(SET_ENCHANTS);
                this.appliedEnchants.clear();
                for (int i = 0; i < IDs.length; ++i) {
                    this.appliedEnchants.put(IDs[i], enchants[i]);
                }
            }
        }
    }

    public static class PlaceHolder
    extends MissileWeapon {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.MISSILE_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof MissileWeapon && !(item instanceof Dart);
        }

        @Override
        public String status() {
            return null;
        }

        @Override
        public String info() {
            return "";
        }
    }
}

