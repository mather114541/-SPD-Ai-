/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWeapon;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class MeleeWeapon
extends Weapon {
    public static String AC_ABILITY = "ABILITY";
    public int tier;
    private static boolean evaluatingTwinUpgrades = false;

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (ch instanceof Hero && ((Hero)ch).heroClass == HeroClass.DUELIST) {
            Buff.affect(ch, Charger.class);
        }
    }

    @Override
    public String defaultAction() {
        if (Dungeon.hero != null && (Dungeon.hero.heroClass == HeroClass.DUELIST || Dungeon.hero.hasTalent(Talent.SWIFT_EQUIP))) {
            return AC_ABILITY;
        }
        return super.defaultAction();
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && hero.heroClass == HeroClass.DUELIST) {
            actions.add(AC_ABILITY);
        }
        return actions;
    }

    @Override
    public String actionName(String action, Hero hero) {
        if (action.equals(AC_ABILITY)) {
            return Messages.upperCase(Messages.get(this, "ability_name", new Object[0]));
        }
        return super.actionName(action, hero);
    }

    @Override
    public void execute(final Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_ABILITY)) {
            this.usesTargeting = false;
            if (!this.isEquipped(hero)) {
                if (hero.hasTalent(Talent.SWIFT_EQUIP)) {
                    if (hero.buff(Talent.SwiftEquipCooldown.class) == null || hero.buff(Talent.SwiftEquipCooldown.class).hasSecondUse()) {
                        this.execute(hero, "EQUIP");
                    } else if (hero.heroClass == HeroClass.DUELIST) {
                        GLog.w(Messages.get(this, "ability_need_equip", new Object[0]), new Object[0]);
                    }
                } else if (hero.heroClass == HeroClass.DUELIST) {
                    GLog.w(Messages.get(this, "ability_need_equip", new Object[0]), new Object[0]);
                }
            } else if (hero.heroClass == HeroClass.DUELIST) {
                if (this.STRReq() > hero.STR()) {
                    GLog.w(Messages.get(this, "ability_low_str", new Object[0]), new Object[0]);
                } else if ((float)Buff.affect((Char)hero, Charger.class).charges + Buff.affect((Char)hero, Charger.class).partialCharge < this.abilityChargeUse(hero, null)) {
                    GLog.w(Messages.get(this, "ability_no_charge", new Object[0]), new Object[0]);
                } else if (this.targetingPrompt() == null) {
                    this.duelistAbility(hero, hero.pos);
                    MeleeWeapon.updateQuickslot();
                } else {
                    this.usesTargeting = this.useTargeting();
                    GameScene.selectCell(new CellSelector.Listener(){

                        @Override
                        public void onSelect(Integer cell) {
                            if (cell != null) {
                                MeleeWeapon.this.duelistAbility(hero, cell);
                                Item.updateQuickslot();
                            }
                        }

                        @Override
                        public String prompt() {
                            return MeleeWeapon.this.targetingPrompt();
                        }
                    });
                }
            }
        }
    }

    public String targetingPrompt() {
        return null;
    }

    public boolean useTargeting() {
        return this.targetingPrompt() != null;
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return dst;
    }

    protected void duelistAbility(Hero hero, Integer target) {
    }

    protected void beforeAbilityUsed(Hero hero, Char target) {
        hero.belongings.abilityWeapon = this;
        Charger charger = Buff.affect(hero, Charger.class);
        charger.partialCharge -= this.abilityChargeUse(hero, target);
        while (charger.partialCharge < 0.0f && charger.charges > 0) {
            --charger.charges;
            charger.partialCharge += 1.0f;
        }
        if (hero.heroClass == HeroClass.DUELIST && hero.hasTalent(Talent.AGGRESSIVE_BARRIER) && (float)hero.HP / (float)hero.HT <= 0.5f) {
            int shieldAmt = 1 + 2 * hero.pointsInTalent(Talent.AGGRESSIVE_BARRIER);
            Buff.affect(hero, Barrier.class).setShield(shieldAmt);
            hero.sprite.showStatusWithIcon(65280, Integer.toString(shieldAmt), FloatingText.SHIELDING, new Object[0]);
        }
        MeleeWeapon.updateQuickslot();
    }

    protected void afterAbilityUsed(Hero hero) {
        Buff tracker;
        hero.belongings.abilityWeapon = null;
        if (hero.hasTalent(Talent.PRECISE_ASSAULT)) {
            Buff.prolong(hero, Talent.PreciseAssaultTracker.class, hero.cooldown() + 4.0f);
        }
        if (hero.hasTalent(Talent.VARIED_CHARGE)) {
            tracker = hero.buff(Talent.VariedChargeTracker.class);
            if (tracker == null || ((Talent.VariedChargeTracker)tracker).weapon == this.getClass() || ((Talent.VariedChargeTracker)tracker).weapon == null) {
                Buff.affect((Char)hero, Talent.VariedChargeTracker.class).weapon = this.getClass();
            } else {
                tracker.detach();
                Charger charger = Buff.affect(hero, Charger.class);
                charger.gainCharge((float)hero.pointsInTalent(Talent.VARIED_CHARGE) / 6.0f);
                ScrollOfRecharging.charge(hero);
            }
        }
        if (hero.hasTalent(Talent.COMBINED_LETHALITY)) {
            tracker = hero.buff(Talent.CombinedLethalityAbilityTracker.class);
            if (tracker == null || ((Talent.CombinedLethalityAbilityTracker)tracker).weapon == this || ((Talent.CombinedLethalityAbilityTracker)tracker).weapon == null) {
                Buff.affect((Char)hero, Talent.CombinedLethalityAbilityTracker.class, (float)hero.cooldown()).weapon = this;
            } else {
                tracker.detach();
            }
        }
        if (hero.hasTalent(Talent.COMBINED_ENERGY)) {
            tracker = hero.buff(Talent.CombinedEnergyAbilityTracker.class);
            if (tracker == null || !((Talent.CombinedEnergyAbilityTracker)tracker).monkAbilused) {
                Buff.prolong((Char)hero, Talent.CombinedEnergyAbilityTracker.class, (float)5.0f).wepAbilUsed = true;
            } else {
                ((Talent.CombinedEnergyAbilityTracker)tracker).wepAbilUsed = true;
                Buff.affect(hero, MonkEnergy.class).processCombinedEnergy((Talent.CombinedEnergyAbilityTracker)tracker);
            }
        }
        if (hero.buff(Talent.CounterAbilityTacker.class) != null) {
            Charger charger = Buff.affect(hero, Charger.class);
            charger.gainCharge((float)hero.pointsInTalent(Talent.COUNTER_ABILITY) * 0.375f);
            hero.buff(Talent.CounterAbilityTacker.class).detach();
        }
    }

    public static void onAbilityKill(Hero hero, Char killed) {
        if (killed.alignment == Char.Alignment.ENEMY && hero.hasTalent(Talent.LETHAL_HASTE)) {
            Buff.affect(hero, GreaterHaste.class).set(2 + 2 * hero.pointsInTalent(Talent.LETHAL_HASTE));
        }
    }

    protected int baseChargeUse(Hero hero, Char target) {
        return 1;
    }

    public final float abilityChargeUse(Hero hero, Char target) {
        return this.baseChargeUse(hero, target);
    }

    @Override
    public int min(int lvl) {
        return this.tier + lvl;
    }

    @Override
    public int max(int lvl) {
        return 5 * (this.tier + 1) + lvl * (this.tier + 1);
    }

    @Override
    public int STRReq(int lvl) {
        int req = MeleeWeapon.STRReq(this.tier, lvl);
        if (this.masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }

    @Override
    public int buffedLvl() {
        if (!evaluatingTwinUpgrades && Dungeon.hero != null && this.isEquipped(Dungeon.hero) && Dungeon.hero.hasTalent(Talent.TWIN_UPGRADES)) {
            KindOfWeapon other = null;
            if (Dungeon.hero.belongings.weapon() != this) {
                other = Dungeon.hero.belongings.weapon();
            }
            if (Dungeon.hero.belongings.secondWep() != this) {
                other = Dungeon.hero.belongings.secondWep();
            }
            if (other instanceof MeleeWeapon) {
                evaluatingTwinUpgrades = true;
                int otherLevel = other.buffedLvl();
                evaluatingTwinUpgrades = false;
                if (this.tier + (3 - Dungeon.hero.pointsInTalent(Talent.TWIN_UPGRADES)) <= ((MeleeWeapon)other).tier && otherLevel > super.buffedLvl()) {
                    return otherLevel;
                }
            }
        }
        return super.buffedLvl();
    }

    @Override
    public int damageRoll(Char owner) {
        int exStr;
        int damage = this.augment.damageFactor(super.damageRoll(owner));
        if (owner instanceof Hero && (exStr = ((Hero)owner).STR() - this.STRReq()) > 0) {
            damage += Hero.heroDamageIntRange(0, exStr);
        }
        return damage;
    }

    @Override
    public String info() {
        String statsInfo;
        Object info = super.info();
        if (this.levelKnown) {
            info = (String)info + "\n\n" + Messages.get(MeleeWeapon.class, "stats_known", this.tier, this.augment.damageFactor(this.min()), this.augment.damageFactor(this.max()), this.STRReq());
            if (Dungeon.hero != null) {
                if (this.STRReq() > Dungeon.hero.STR()) {
                    info = (String)info + " " + Messages.get(Weapon.class, "too_heavy", new Object[0]);
                } else if (Dungeon.hero.STR() > this.STRReq()) {
                    info = (String)info + " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - this.STRReq());
                }
            }
        } else {
            info = (String)info + "\n\n" + Messages.get(MeleeWeapon.class, "stats_unknown", this.tier, this.min(0), this.max(0), this.STRReq(0));
            if (Dungeon.hero != null && this.STRReq(0) > Dungeon.hero.STR()) {
                info = (String)info + " " + Messages.get(MeleeWeapon.class, "probably_too_heavy", new Object[0]);
            }
        }
        if (!(statsInfo = this.statsInfo()).equals("")) {
            info = (String)info + "\n\n" + statsInfo;
        }
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
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseEnchant() && Dungeon.hero.buff(HolyWeapon.HolyWepBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.enchantment == null)) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", Messages.get(HolyWeapon.class, "ench_name", Messages.get(Weapon.Enchantment.class, "enchant", new Object[0]))));
            info = (String)info + " " + Messages.get(HolyWeapon.class, "ench_desc", new Object[0]);
        } else if (this.enchantment != null && (this.cursedKnown || !this.enchantment.curse())) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", this.enchantment.name()));
            if (this.enchantHardened) {
                info = (String)info + " " + Messages.get(Weapon.class, "enchant_hardened", new Object[0]);
            }
            info = (String)info + " " + this.enchantment.desc();
        } else if (this.enchantHardened) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant", new Object[0]);
        }
        if (this.cursed && this.isEquipped(Dungeon.hero)) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "cursed_worn", new Object[0]);
        } else if (this.cursedKnown && this.cursed) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "cursed", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            info = this.enchantment != null && this.enchantment.curse() ? (String)info + "\n\n" + Messages.get(Weapon.class, "weak_cursed", new Object[0]) : (String)info + "\n\n" + Messages.get(Weapon.class, "not_cursed", new Object[0]);
        }
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST && !(this instanceof MagesStaff)) {
            info = (String)info + "\n\n" + this.abilityInfo();
        }
        return info;
    }

    public String statsInfo() {
        return Messages.get(this, "stats_desc", new Object[0]);
    }

    public String abilityInfo() {
        return Messages.get(this, "ability_desc", new Object[0]);
    }

    public String upgradeAbilityStat(int level) {
        return null;
    }

    @Override
    public String status() {
        if (this.isEquipped(Dungeon.hero) && Dungeon.hero.buff(Charger.class) != null) {
            Charger buff = Dungeon.hero.buff(Charger.class);
            return buff.charges + "/" + buff.chargeCap();
        }
        return super.status();
    }

    @Override
    public int value() {
        int price = 20 * this.tier;
        if (this.hasGoodEnchant()) {
            price = (int)((float)price * 1.5f);
            if (this.enchantment instanceof Crystal) {
                price *= 3;
            }
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

    public static class Charger
    extends Buff
    implements ActionIndicator.Action {
        public int charges;
        public float partialCharge;
        public static final String CHARGES = "charges";
        private static final String PARTIALCHARGE = "partialCharge";

        public Charger() {
            this.revivePersists = true;
            this.charges = 2;
        }

        @Override
        public boolean act() {
            if (this.charges < this.chargeCap()) {
                int points;
                if (Regeneration.regenOn()) {
                    float chargeToGain = 1.0f / (60.0f - 1.5f * (float)(this.chargeCap() - this.charges));
                    if (Dungeon.hero.subClass == HeroSubClass.CHAMPION) {
                        chargeToGain *= 1.5f;
                    }
                    if (Dungeon.hero.buff(RingOfForce.BrawlersStance.class) != null) {
                        chargeToGain *= 0.5f;
                    }
                    this.partialCharge += chargeToGain;
                }
                if ((points = ((Hero)this.target).pointsInTalent(Talent.WEAPON_RECHARGING)) > 0 && this.target.buff(Recharging.class) != null || this.target.buff(ArtifactRecharge.class) != null) {
                    this.partialCharge += 1.0f / (20.0f - 5.0f * (float)points);
                }
                if (this.partialCharge >= 1.0f) {
                    ++this.charges;
                    this.partialCharge -= 1.0f;
                    Item.updateQuickslot();
                }
            } else {
                this.partialCharge = 0.0f;
            }
            if (ActionIndicator.action != this && Dungeon.hero.subClass == HeroSubClass.CHAMPION) {
                ActionIndicator.setAction(this);
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on && Dungeon.hero.subClass == HeroSubClass.CHAMPION) {
                ActionIndicator.setAction(this);
            }
        }

        @Override
        public void detach() {
            super.detach();
            ActionIndicator.clearAction(this);
        }

        public int chargeCap() {
            if (Dungeon.hero.subClass == HeroSubClass.CHAMPION) {
                return Math.min(10, 4 + (Dungeon.hero.lvl - 1) / 3);
            }
            return Math.min(8, 2 + (Dungeon.hero.lvl - 1) / 3);
        }

        public void gainCharge(float charge) {
            if (this.charges < this.chargeCap()) {
                this.partialCharge += charge;
                while (this.partialCharge >= 1.0f) {
                    ++this.charges;
                    this.partialCharge -= 1.0f;
                }
                if (this.charges >= this.chargeCap()) {
                    this.partialCharge = 0.0f;
                    this.charges = this.chargeCap();
                }
                Item.updateQuickslot();
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(CHARGES, this.charges);
            bundle.put(PARTIALCHARGE, this.partialCharge);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.charges = bundle.getInt(CHARGES);
            this.partialCharge = bundle.getFloat(PARTIALCHARGE);
        }

        @Override
        public String actionName() {
            return Messages.get(MeleeWeapon.class, "swap", new Object[0]);
        }

        @Override
        public int actionIcon() {
            return 109;
        }

        @Override
        public Visual primaryVisual() {
            Image ico = Dungeon.hero.belongings.weapon == null ? new HeroIcon(this) : new ItemSprite(Dungeon.hero.belongings.weapon);
            ico.width += 4.0f;
            return ico;
        }

        @Override
        public Visual secondaryVisual() {
            Image ico = Dungeon.hero.belongings.secondWep == null ? new HeroIcon(this) : new ItemSprite(Dungeon.hero.belongings.secondWep);
            ico.scale.set(PixelScene.align(0.51f));
            ico.brightness(0.6f);
            return ico;
        }

        @Override
        public int indicatorColor() {
            return 0x5500BB;
        }

        @Override
        public void doAction() {
            if (Dungeon.hero.subClass != HeroSubClass.CHAMPION) {
                return;
            }
            if (Dungeon.hero.belongings.secondWep == null && Dungeon.hero.belongings.backpack.items.size() >= Dungeon.hero.belongings.backpack.capacity()) {
                GLog.w(Messages.get(MeleeWeapon.class, "swap_full", new Object[0]), new Object[0]);
                return;
            }
            KindOfWeapon temp = Dungeon.hero.belongings.weapon;
            Dungeon.hero.belongings.weapon = Dungeon.hero.belongings.secondWep;
            Dungeon.hero.belongings.secondWep = temp;
            Dungeon.hero.sprite.operate(Dungeon.hero.pos);
            Sample.INSTANCE.play("sounds/unlock.mp3");
            ActionIndicator.setAction(this);
            Item.updateQuickslot();
            AttackIndicator.updateState();
        }
    }
}

