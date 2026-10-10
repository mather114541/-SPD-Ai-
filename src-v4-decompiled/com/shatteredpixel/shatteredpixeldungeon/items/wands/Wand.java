/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.wands;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ScrollEmpower;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage.WildMagic;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.effects.Flare;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public abstract class Wand
extends Item {
    public static final String AC_ZAP = "ZAP";
    private static final float TIME_TO_ZAP = 1.0f;
    public int maxCharges;
    public int curCharges;
    public float partialCharge;
    protected Charger charger;
    public boolean curChargeKnown;
    public boolean curseInfusionBonus;
    public int resinBonus;
    private static final int USES_TO_ID = 10;
    private float usesLeftToID;
    private float availableUsesToID;
    protected int collisionProperties;
    private static final String USES_LEFT_TO_ID = "uses_left_to_id";
    private static final String AVAILABLE_USES = "available_uses";
    private static final String CUR_CHARGES = "curCharges";
    private static final String CUR_CHARGE_KNOWN = "curChargeKnown";
    private static final String PARTIALCHARGE = "partialCharge";
    private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
    private static final String RESIN_BONUS = "resin_bonus";
    protected static CellSelector.Listener zapper = new CellSelector.Listener(){

        @Override
        public void onSelect(final Integer target) {
            if (target != null) {
                if (!(curItem instanceof Wand)) {
                    return;
                }
                final Wand curWand = (Wand)curItem;
                final Ballistica shot = new Ballistica(curUser.pos, target, curWand.collisionProperties(target));
                int cell = shot.collisionPos;
                if (target == curUser.pos || cell == curUser.pos) {
                    if (target == curUser.pos && curUser.hasTalent(Talent.SHIELD_BATTERY)) {
                        if (curUser.buff(MagicImmune.class) != null) {
                            GLog.w(Messages.get(Wand.class, "no_magic", new Object[0]), new Object[0]);
                            return;
                        }
                        if (curWand.curCharges == 0) {
                            GLog.w(Messages.get(Wand.class, "fizzles", new Object[0]), new Object[0]);
                            return;
                        }
                        float shield = (float)curUser.HT * (0.04f * (float)curWand.curCharges);
                        if (curUser.pointsInTalent(Talent.SHIELD_BATTERY) == 2) {
                            shield *= 1.5f;
                        }
                        Buff.affect(curUser, Barrier.class).setShield(Math.round(shield));
                        curUser.sprite.showStatusWithIcon(65280, Integer.toString(Math.round(shield)), FloatingText.SHIELDING, new Object[0]);
                        curWand.curCharges = 0;
                        curUser.sprite.operate(curUser.pos);
                        Sample.INSTANCE.play("sounds/chargeup.mp3");
                        ScrollOfRecharging.charge(curUser);
                        Item.updateQuickslot();
                        curUser.spendAndNext(1.0f);
                        return;
                    }
                    GLog.i(Messages.get(Wand.class, "self_target", new Object[0]), new Object[0]);
                    return;
                }
                curUser.sprite.zap(cell);
                if (Actor.findChar(target) != null) {
                    QuickSlotButton.target(Actor.findChar(target));
                } else {
                    QuickSlotButton.target(Actor.findChar(cell));
                }
                if (curWand.tryToZap(curUser, target)) {
                    curUser.busy();
                    if (curUser.hasTalent(Talent.BACKUP_BARRIER) && curWand.curCharges == curWand.chargesPerCast() && curWand.charger != null && curWand.charger.target == curUser) {
                        if (curUser.heroClass == HeroClass.MAGE && !curUser.belongings.contains(curWand)) {
                            int shieldToGive = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.BACKUP_BARRIER);
                            Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
                            Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(shieldToGive), FloatingText.SHIELDING, new Object[0]);
                        } else if (curUser.heroClass != HeroClass.MAGE) {
                            boolean highest = true;
                            for (Item item : curUser.belongings.getAllItems(Wand.class)) {
                                if (item.level() <= curWand.level()) continue;
                                highest = false;
                            }
                            if (highest) {
                                int shieldToGive = 1 + 2 * Dungeon.hero.pointsInTalent(Talent.BACKUP_BARRIER);
                                Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
                                Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(shieldToGive), FloatingText.SHIELDING, new Object[0]);
                            }
                        }
                    }
                    if (curWand.cursed) {
                        if (!curWand.cursedKnown) {
                            GLog.n(Messages.get(Wand.class, "curse_discover", curWand.name()), new Object[0]);
                        }
                        CursedWand.cursedZap(curWand, curUser, new Ballistica(curUser.pos, target, 6), new Callback(){

                            @Override
                            public void call() {
                                curWand.wandUsed();
                            }
                        });
                    } else {
                        curWand.fx(shot, new Callback(){

                            @Override
                            public void call() {
                                curWand.onZap(shot);
                                if (Random.Float() < WondrousResin.extraCurseEffectChance()) {
                                    WondrousResin.forcePositive = true;
                                    CursedWand.cursedZap(curWand, curUser, new Ballistica(curUser.pos, target, 6), new Callback(){

                                        @Override
                                        public void call() {
                                            WondrousResin.forcePositive = false;
                                            curWand.wandUsed();
                                        }
                                    });
                                } else {
                                    curWand.wandUsed();
                                }
                            }
                        });
                    }
                    curWand.cursedKnown = true;
                }
            }
        }

        @Override
        public String prompt() {
            return Messages.get(Wand.class, "prompt", new Object[0]);
        }
    };

    public Wand() {
        this.curCharges = this.maxCharges = this.initialCharges();
        this.partialCharge = 0.0f;
        this.curChargeKnown = false;
        this.curseInfusionBonus = false;
        this.resinBonus = 0;
        this.usesLeftToID = 10.0f;
        this.availableUsesToID = 5.0f;
        this.collisionProperties = 6;
        this.defaultAction = AC_ZAP;
        this.usesTargeting = true;
        this.bones = true;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.curCharges > 0 || !this.curChargeKnown) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ZAP)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell(zapper);
        }
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (this.cursed && this.cursedKnown) {
            return new Ballistica((int)user.pos, (int)dst, (int)6).collisionPos;
        }
        return new Ballistica((int)user.pos, (int)dst, (int)this.collisionProperties).collisionPos;
    }

    public abstract void onZap(Ballistica var1);

    public abstract void onHit(MagesStaff var1, Char var2, Char var3, int var4);

    public static float procChanceMultiplier(Char attacker) {
        if (attacker.buff(Talent.EmpoweredStrikeTracker.class) != null) {
            return 1.0f + (float)((Hero)attacker).pointsInTalent(Talent.EMPOWERED_STRIKE) / 2.0f;
        }
        return 1.0f;
    }

    public boolean tryToZap(Hero owner, int target) {
        if (owner.buff(WildMagic.WildMagicTracker.class) == null && owner.buff(MagicImmune.class) != null) {
            GLog.w(Messages.get(this, "no_magic", new Object[0]), new Object[0]);
            return false;
        }
        if (owner.buff(WildMagic.WildMagicTracker.class) != null || this.curCharges >= this.chargesPerCast()) {
            return true;
        }
        GLog.w(Messages.get(this, "fizzles", new Object[0]), new Object[0]);
        return false;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner != null) {
                if (container instanceof MagicalHolster) {
                    MagicalHolster cfr_ignored_0 = (MagicalHolster)container;
                    this.charge(container.owner, 0.85f);
                } else {
                    this.charge(container.owner);
                }
            }
            return true;
        }
        return false;
    }

    public void gainCharge(float amt) {
        this.gainCharge(amt, false);
    }

    public void gainCharge(float amt, boolean overcharge) {
        this.partialCharge += amt;
        while (this.partialCharge >= 1.0f) {
            this.curCharges = overcharge ? Math.min(this.maxCharges + (int)amt, this.curCharges + 1) : Math.min(this.maxCharges, this.curCharges + 1);
            this.partialCharge -= 1.0f;
            Wand.updateQuickslot();
        }
    }

    public void charge(Char owner) {
        if (this.charger == null) {
            this.charger = new Charger();
        }
        this.charger.attachTo(owner);
    }

    public void charge(Char owner, float chargeScaleFactor) {
        this.charge(owner);
        this.charger.setScaleFactor(chargeScaleFactor);
    }

    protected void wandProc(Char target, int chargesUsed) {
        Wand.wandProc(target, this.buffedLvl(), chargesUsed);
    }

    protected static void wandProc(Char target, int wandLevel, int chargesUsed) {
        if (Dungeon.hero.hasTalent(Talent.ARCANE_VISION)) {
            int dur = 5 + 5 * Dungeon.hero.pointsInTalent(Talent.ARCANE_VISION);
            Buff.append((Char)Dungeon.hero, TalismanOfForesight.CharAwareness.class, (float)((float)dur)).charID = target.id();
        }
        if (target != Dungeon.hero && Dungeon.hero.subClass == HeroSubClass.WARLOCK && (double)Random.Float() > Math.pow(0.92f, wandLevel * chargesUsed + 1) - (double)0.07f) {
            SoulMark.prolong(target, SoulMark.class, 10.0f + (float)wandLevel);
        }
        if (Dungeon.hero.subClass == HeroSubClass.PRIEST && target.buff(GuidingLight.Illuminated.class) != null) {
            target.buff(GuidingLight.Illuminated.class).detach();
            target.damage(Dungeon.hero.lvl + 5, GuidingLight.INSTANCE);
        }
        if (target.alignment != Char.Alignment.ALLY && Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.SEARING_LIGHT) && Dungeon.hero.buff(Talent.SearingLightCooldown.class) == null) {
            Buff.affect(target, GuidingLight.Illuminated.class);
            Buff.affect(Dungeon.hero, Talent.SearingLightCooldown.class, 20.0f);
        }
        if (target.alignment != Char.Alignment.ALLY && Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.SUNRAY) && Random.Int(20) < 1 + 2 * Dungeon.hero.pointsInTalent(Talent.SUNRAY)) {
            Buff.prolong(target, Blindness.class, 4.0f);
        }
    }

    @Override
    public void onDetach() {
        this.stopCharging();
    }

    public void stopCharging() {
        if (this.charger != null) {
            this.charger.detach();
            this.charger = null;
        }
    }

    @Override
    public Item level(int value) {
        super.level(value);
        this.updateLevel();
        return this;
    }

    @Override
    public Item identify(boolean byHero) {
        this.curChargeKnown = true;
        super.identify(byHero);
        Wand.updateQuickslot();
        return this;
    }

    public void setIDReady() {
        this.usesLeftToID = -1.0f;
    }

    public boolean readyToIdentify() {
        return !this.isIdentified() && this.usesLeftToID <= 0.0f;
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        levelPercent *= Talent.itemIDSpeedFactor(hero, this);
        if (!this.isIdentified() && this.availableUsesToID <= 5.0f) {
            this.availableUsesToID = Math.min(5.0f, this.availableUsesToID + levelPercent * 10.0f / 2.0f);
        }
    }

    @Override
    public String info() {
        Object desc = super.info();
        desc = (String)desc + "\n\n" + this.statsDesc();
        if (this.resinBonus == 1) {
            desc = (String)desc + "\n\n" + Messages.get(Wand.class, "resin_one", new Object[0]);
        } else if (this.resinBonus > 1) {
            desc = (String)desc + "\n\n" + Messages.get(Wand.class, "resin_many", this.resinBonus);
        }
        if (this.cursed && this.cursedKnown) {
            desc = (String)desc + "\n\n" + Messages.get(Wand.class, "cursed", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            desc = (String)desc + "\n\n" + Messages.get(Wand.class, "not_cursed", new Object[0]);
        }
        if (Dungeon.hero != null && Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE) {
            desc = (String)desc + "\n\n" + Messages.get(this, "bmage_desc", new Object[0]);
        }
        return desc;
    }

    public String statsDesc() {
        return Messages.get(this, "stats_desc", new Object[0]);
    }

    public String upgradeStat1(int level) {
        return null;
    }

    public String upgradeStat2(int level) {
        return null;
    }

    public String upgradeStat3(int level) {
        return null;
    }

    @Override
    public boolean isIdentified() {
        return super.isIdentified() && this.curChargeKnown;
    }

    @Override
    public String status() {
        if (this.levelKnown) {
            return String.valueOf(this.curChargeKnown ? Integer.valueOf(this.curCharges) : "?") + "/" + this.maxCharges;
        }
        return null;
    }

    @Override
    public int level() {
        if (!this.cursed && this.curseInfusionBonus) {
            this.curseInfusionBonus = false;
            this.updateLevel();
        }
        int level = super.level();
        if (this.curseInfusionBonus) {
            level += 1 + level / 6;
        }
        return level += this.resinBonus;
    }

    @Override
    public Item upgrade() {
        super.upgrade();
        if (Random.Int(3) == 0) {
            this.cursed = false;
        }
        if (this.resinBonus > 0) {
            --this.resinBonus;
        }
        this.updateLevel();
        this.curCharges = Math.min(this.curCharges + 1, this.maxCharges);
        Wand.updateQuickslot();
        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();
        this.updateLevel();
        Wand.updateQuickslot();
        return this;
    }

    @Override
    public int buffedLvl() {
        int lvl = super.buffedLvl();
        if (this.charger != null && this.charger.target != null) {
            WandOfMagicMissile.MagicCharge buff;
            if (this.charger.target == Dungeon.hero && !Dungeon.hero.belongings.contains(this) && Dungeon.hero.buff(Degrade.class) != null) {
                lvl = Degrade.reduceLevel(lvl);
            }
            if (this.charger.target.buff(ScrollEmpower.class) != null) {
                lvl += 2;
            }
            if (this.curCharges == 1 && this.charger.target instanceof Hero && ((Hero)this.charger.target).hasTalent(Talent.DESPERATE_POWER)) {
                lvl += ((Hero)this.charger.target).pointsInTalent(Talent.DESPERATE_POWER);
            }
            if (this.charger.target.buff(WildMagic.WildMagicTracker.class) != null) {
                int bonus = 4 + ((Hero)this.charger.target).pointsInTalent(Talent.WILD_POWER);
                if (Random.Int(2) == 0) {
                    ++bonus;
                }
                bonus /= 2;
                int maxBonusLevel = 3 + ((Hero)this.charger.target).pointsInTalent(Talent.WILD_POWER);
                if (lvl < maxBonusLevel) {
                    lvl = Math.min(lvl + bonus, maxBonusLevel);
                }
            }
            if ((buff = this.charger.target.buff(WandOfMagicMissile.MagicCharge.class)) != null && buff.level() > lvl) {
                return buff.level();
            }
        }
        return lvl;
    }

    public void updateLevel() {
        this.maxCharges = Math.min(this.initialCharges() + this.level(), 10);
        this.curCharges = Math.min(this.curCharges, this.maxCharges);
    }

    public int initialCharges() {
        return 2;
    }

    protected int chargesPerCast() {
        return 1;
    }

    public void fx(Ballistica bolt, Callback callback) {
        MagicMissile.boltFromChar(Wand.curUser.sprite.parent, 0, Wand.curUser.sprite, bolt.collisionPos, callback);
        Sample.INSTANCE.play("sounds/zap.mp3");
    }

    public void staffFx(MagesStaff.StaffParticle particle) {
        particle.color(0xFFFFFF);
        particle.am = 0.3f;
        particle.setLifespan(1.0f);
        particle.speed.polar(Random.Float(6.283185f), 2.0f);
        particle.setSize(1.0f, 2.0f);
        particle.radiateXY(0.5f);
    }

    public void wandUsed() {
        if (!this.isIdentified()) {
            float uses = Math.min(this.availableUsesToID, Talent.itemIDSpeedFactor(Dungeon.hero, this));
            this.availableUsesToID -= uses;
            this.usesLeftToID -= uses;
            if (this.usesLeftToID <= 0.0f || Dungeon.hero.pointsInTalent(Talent.SCHOLARS_INTUITION) == 2) {
                if (ShardOfOblivion.passiveIDDisabled()) {
                    if (this.usesLeftToID > -1.0f) {
                        GLog.p(Messages.get(ShardOfOblivion.class, "identify_ready", new Object[0]), this.name());
                    }
                    this.setIDReady();
                } else {
                    this.identify();
                    GLog.p(Messages.get(Wand.class, "identify", new Object[0]), new Object[0]);
                    Badges.validateItemLevelAquired(this);
                }
            }
            if (ShardOfOblivion.passiveIDDisabled()) {
                Buff.prolong(curUser, ShardOfOblivion.WandUseTracker.class, 50.0f);
            }
        }
        if (this.charger != null && this.charger.target == Dungeon.hero && !Dungeon.hero.belongings.contains(this) && Dungeon.hero.hasTalent(Talent.EXCESS_CHARGE) && this.curCharges >= this.maxCharges) {
            int shieldToGive = Math.round((float)this.buffedLvl() * 0.67f * (float)Dungeon.hero.pointsInTalent(Talent.EXCESS_CHARGE));
            Buff.affect(Dungeon.hero, Barrier.class).setShield(shieldToGive);
            Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(shieldToGive), FloatingText.SHIELDING, new Object[0]);
        }
        this.curCharges -= this.cursed ? 1 : this.chargesPerCast();
        WandOfMagicMissile.MagicCharge buff = curUser.buff(WandOfMagicMissile.MagicCharge.class);
        if (buff != null && buff.wandJustApplied() != this && buff.level() == this.buffedLvl() && this.buffedLvl() > super.buffedLvl()) {
            buff.detach();
        } else {
            ScrollEmpower empower = curUser.buff(ScrollEmpower.class);
            if (empower != null) {
                empower.use();
            }
        }
        if (Dungeon.hero.hasTalent(Talent.EMPOWERED_STRIKE) && this.charger != null && this.charger.target == Dungeon.hero && !Dungeon.hero.belongings.contains(this)) {
            Buff.prolong(Dungeon.hero, Talent.EmpoweredStrikeTracker.class, 10.0f);
        }
        if (Dungeon.hero.hasTalent(Talent.LINGERING_MAGIC) && this.charger != null && this.charger.target == Dungeon.hero) {
            Buff.prolong(Dungeon.hero, Talent.LingeringMagicTracker.class, 5.0f);
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.DIVINE_SENSE)) {
            Buff.prolong(Dungeon.hero, DivineSense.DivineSenseTracker.class, Dungeon.hero.cooldown() + 1.0f);
        }
        if (Dungeon.hero.heroClass != HeroClass.CLERIC && Dungeon.hero.hasTalent(Talent.CLEANSE) && Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.CLEANSE)) {
            boolean removed = false;
            for (Buff b : Dungeon.hero.buffs()) {
                if (b.type != Buff.buffType.NEGATIVE || b instanceof LostInventory) continue;
                b.detach();
                removed = true;
            }
            if (removed) {
                new Flare(6, 32.0f).color(16731346, true).show(Dungeon.hero.sprite, 2.0f);
            }
        }
        Invisibility.dispel();
        Wand.updateQuickslot();
        curUser.spendAndNext(1.0f);
    }

    @Override
    public Item random() {
        int n = 0;
        if (Random.Int(3) == 0) {
            ++n;
            if (Random.Int(5) == 0) {
                ++n;
            }
        }
        this.level(n);
        this.curCharges += n;
        if (Random.Float() < 0.3f) {
            this.cursed = true;
        }
        return this;
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.resinBonus == 0) {
            return null;
        }
        return new ItemSprite.Glowing(0xFFFFFF, 1.0f / (float)this.resinBonus);
    }

    @Override
    public int value() {
        int price = 75;
        if (this.cursed && this.cursedKnown) {
            price /= 2;
        }
        if (this.levelKnown) {
            if (this.level() > 0) {
                price *= this.level() + 1;
            } else if (this.level() < 0) {
                price /= 1 - this.level();
            }
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(USES_LEFT_TO_ID, this.usesLeftToID);
        bundle.put(AVAILABLE_USES, this.availableUsesToID);
        bundle.put(CUR_CHARGES, this.curCharges);
        bundle.put(CUR_CHARGE_KNOWN, this.curChargeKnown);
        bundle.put(PARTIALCHARGE, this.partialCharge);
        bundle.put(CURSE_INFUSION_BONUS, this.curseInfusionBonus);
        bundle.put(RESIN_BONUS, this.resinBonus);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.usesLeftToID = bundle.getInt(USES_LEFT_TO_ID);
        this.availableUsesToID = bundle.getInt(AVAILABLE_USES);
        this.curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
        this.resinBonus = bundle.getInt(RESIN_BONUS);
        this.updateLevel();
        this.curCharges = bundle.getInt(CUR_CHARGES);
        this.curChargeKnown = bundle.getBoolean(CUR_CHARGE_KNOWN);
        this.partialCharge = bundle.getFloat(PARTIALCHARGE);
    }

    @Override
    public void reset() {
        super.reset();
        this.usesLeftToID = 10.0f;
        this.availableUsesToID = 5.0f;
    }

    public int collisionProperties(int target) {
        if (this.cursed) {
            return 6;
        }
        return this.collisionProperties;
    }

    public class Charger
    extends Buff {
        private static final float BASE_CHARGE_DELAY = 10.0f;
        private static final float SCALING_CHARGE_ADDITION = 40.0f;
        private static final float NORMAL_SCALE_FACTOR = 0.875f;
        private static final float CHARGE_BUFF_BONUS = 0.25f;
        float scalingFactor = 0.875f;

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (target instanceof Hero && Dungeon.hero == null && this.cooldown() == 0.0f && target.cooldown() > 0.0f) {
                    this.spend(1.0f);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            if (Wand.this.curCharges < Wand.this.maxCharges && this.target.buff(MagicImmune.class) == null) {
                this.recharge();
            }
            while (Wand.this.partialCharge >= 1.0f && Wand.this.curCharges < Wand.this.maxCharges) {
                Wand.this.partialCharge -= 1.0f;
                ++Wand.this.curCharges;
                Item.updateQuickslot();
            }
            if (Wand.this.curCharges == Wand.this.maxCharges) {
                Wand.this.partialCharge = 0.0f;
            }
            this.spend(1.0f);
            return true;
        }

        private void recharge() {
            int missingCharges = Wand.this.maxCharges - Wand.this.curCharges;
            missingCharges = Math.max(0, missingCharges);
            float turnsToCharge = (float)(10.0 + 40.0 * Math.pow(this.scalingFactor, missingCharges));
            if (Regeneration.regenOn()) {
                Wand.this.partialCharge += 1.0f / turnsToCharge * RingOfEnergy.wandChargeMultiplier(this.target);
            }
            for (Recharging bonus : this.target.buffs(Recharging.class)) {
                if (bonus == null || !(bonus.remainder() > 0.0f)) continue;
                Wand.this.partialCharge += 0.25f * bonus.remainder();
            }
        }

        public Wand wand() {
            return Wand.this;
        }

        public void gainCharge(float charge) {
            if (Wand.this.curCharges < Wand.this.maxCharges) {
                Wand.this.partialCharge += charge;
                while (Wand.this.partialCharge >= 1.0f) {
                    ++Wand.this.curCharges;
                    Wand.this.partialCharge -= 1.0f;
                }
                if (Wand.this.curCharges >= Wand.this.maxCharges) {
                    Wand.this.partialCharge = 0.0f;
                    Wand.this.curCharges = Wand.this.maxCharges;
                }
                Item.updateQuickslot();
            }
        }

        private void setScaleFactor(float value) {
            this.scalingFactor = value;
        }
    }

    public static class PlaceHolder
    extends Wand {
        public PlaceHolder() {
            this.image = ItemSpriteSheet.WAND_HOLDER;
        }

        @Override
        public boolean isSimilar(Item item) {
            return item instanceof Wand;
        }

        @Override
        public void onZap(Ballistica attack) {
        }

        @Override
        public void onHit(MagesStaff staff, Char attacker, Char defender, int damage) {
        }

        @Override
        public String info() {
            return "";
        }
    }
}

