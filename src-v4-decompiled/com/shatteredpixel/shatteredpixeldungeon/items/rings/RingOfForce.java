/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.rings;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class RingOfForce
extends Ring {
    public static String AC_ABILITY = "ABILITY";

    public RingOfForce() {
        this.icon = ItemSpriteSheet.Icons.RING_FORCE;
        this.buffClass = Force.class;
    }

    @Override
    protected Ring.RingBuff buff() {
        return new Force();
    }

    public static int armedDamageBonus(Char ch) {
        return RingOfForce.getBuffedBonus(ch, Force.class);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (hero.buff(BrawlersStance.class) != null && hero.buff(Force.class) == null) {
                hero.buff(BrawlersStance.class).active = false;
            }
            return true;
        }
        return false;
    }

    private static float tier(int str) {
        float tier = Math.max(1.0f, (float)(str - 8) / 2.0f);
        if (tier > 5.0f) {
            tier = 5.0f + (tier - 5.0f) / 2.0f;
        }
        return tier;
    }

    public static int damageRoll(Hero hero) {
        boolean usingForce;
        boolean bl = usingForce = hero.buff(Force.class) != null;
        if (hero.buff(SpiritForm.SpiritFormBuff.class) != null && hero.buff(SpiritForm.SpiritFormBuff.class).ring() instanceof RingOfForce) {
            usingForce = true;
        }
        if (hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
            usingForce = false;
        }
        if (usingForce) {
            int level = RingOfForce.getBuffedBonus(hero, Force.class);
            float tier = RingOfForce.tier(hero.STR());
            int dmg = Hero.heroDamageIntRange(RingOfForce.min(level, tier), RingOfForce.max(level, tier));
            if (hero.buff(BrawlersStance.class) != null && hero.buff(BrawlersStance.class).active) {
                dmg += Math.round(3.0f + tier + (float)level * ((4.0f + 2.0f * tier) / 8.0f));
            }
            return dmg;
        }
        return Hero.heroDamageIntRange(1, Math.max(hero.STR() - 8, 1));
    }

    private static int min(int lvl, float tier) {
        if (lvl <= 0) {
            tier = 1.0f;
        }
        return Math.max(0, Math.round(tier + (float)lvl));
    }

    private static int max(int lvl, float tier) {
        if (lvl <= 0) {
            tier = 1.0f;
        }
        return Math.max(0, Math.round(5.0f * (tier + 1.0f) + (float)lvl * (tier + 1.0f)));
    }

    @Override
    public String statsInfo() {
        float tier = RingOfForce.tier(Dungeon.hero != null ? Dungeon.hero.STR() : 10);
        if (this.isIdentified()) {
            int level = this.soloBuffedBonus();
            Object info = Messages.get(this, "stats", RingOfForce.min(level, tier), RingOfForce.max(level, tier), level);
            if (this.isEquipped(Dungeon.hero) && this.soloBuffedBonus() != this.combinedBuffedBonus(Dungeon.hero)) {
                level = this.combinedBuffedBonus(Dungeon.hero);
                info = (String)info + "\n\n" + Messages.get(this, "combined_stats", RingOfForce.min(level, tier), RingOfForce.max(level, tier), level);
            }
            return info;
        }
        return Messages.get(this, "typical_stats", RingOfForce.min(1, tier), RingOfForce.max(1, tier), 1);
    }

    @Override
    public String upgradeStat1(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        float tier = RingOfForce.tier(Dungeon.hero != null ? Dungeon.hero.STR() : 10);
        return RingOfForce.min(level + 1, tier) + "-" + RingOfForce.max(level + 1, tier);
    }

    @Override
    public String upgradeStat2(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        return Integer.toString(level + 1);
    }

    @Override
    public String upgradeStat3(int level) {
        if (this.cursed && this.cursedKnown) {
            level = Math.min(-1, level - 3);
        }
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST) {
            float tier = RingOfForce.tier(Dungeon.hero != null ? Dungeon.hero.STR() : 10);
            int bonus = Math.round(3.0f + tier + (float)level * ((4.0f + 2.0f * tier) / 8.0f));
            return RingOfForce.min(level + 1, tier) + bonus + "-" + (RingOfForce.max(level + 1, tier) + bonus);
        }
        return null;
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (ch instanceof Hero && ((Hero)ch).heroClass == HeroClass.DUELIST) {
            Buff.affect(ch, MeleeWeapon.Charger.class);
        }
    }

    @Override
    public String defaultAction() {
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST) {
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
    public void execute(Hero hero, String action) {
        if (action.equals(AC_ABILITY)) {
            if (!this.isEquipped(hero)) {
                GLog.w(Messages.get(MeleeWeapon.class, "ability_need_equip", new Object[0]), new Object[0]);
            } else if (hero.buff(BrawlersStance.class) != null) {
                if (!hero.buff(BrawlersStance.class).active) {
                    hero.buff(BrawlersStance.class).reset();
                } else {
                    hero.buff(BrawlersStance.class).active = false;
                }
                BuffIndicator.refreshHero();
                AttackIndicator.updateState();
                hero.sprite.operate(hero.pos);
            } else {
                Buff.affect(hero, BrawlersStance.class).reset();
                AttackIndicator.updateState();
                hero.sprite.operate(hero.pos);
            }
        } else {
            super.execute(hero, action);
        }
    }

    @Override
    public String info() {
        Object info = super.info();
        if (Dungeon.hero != null && Dungeon.hero.heroClass == HeroClass.DUELIST && (this.anonymous || this.isIdentified() || this.isEquipped(Dungeon.hero))) {
            int level = this.isIdentified() ? (this.isEquipped(Dungeon.hero) ? RingOfForce.getBuffedBonus(Dungeon.hero, Force.class) : this.soloBuffedBonus()) : 0;
            float tier = RingOfForce.tier(Dungeon.hero.STR());
            int dmgBoost = Math.round(3.0f + tier + (float)level * ((4.0f + 2.0f * tier) / 8.0f));
            info = this.isIdentified() ? (String)info + "\n\n" + Messages.get(this, "ability_desc", RingOfForce.min(level, tier) + dmgBoost, RingOfForce.max(level, tier) + dmgBoost) : (String)info + "\n\n" + Messages.get(this, "typical_ability_desc", RingOfForce.min(level, tier) + dmgBoost, RingOfForce.max(level, tier) + dmgBoost);
        }
        return info;
    }

    public static boolean fightingUnarmed(Hero hero) {
        if (hero.belongings.attackingWeapon() == null || hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
            return true;
        }
        if (hero.belongings.thrownWeapon != null || hero.belongings.abilityWeapon != null) {
            return false;
        }
        BrawlersStance stance = hero.buff(BrawlersStance.class);
        if (stance != null && stance.active) {
            if (hero.buff(Force.class) == null) {
                stance.active = false;
                AttackIndicator.updateState();
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean unarmedGetsWeaponEnchantment(Hero hero) {
        if (hero.belongings.attackingWeapon() == null) {
            return false;
        }
        if (hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
            return hero.buff(MonkEnergy.MonkAbility.FlurryEmpowerTracker.class) != null;
        }
        BrawlersStance stance = hero.buff(BrawlersStance.class);
        return stance != null && stance.active;
    }

    public static boolean unarmedGetsWeaponAugment(Hero hero) {
        if (hero.belongings.attackingWeapon() == null || hero.buff(MonkEnergy.MonkAbility.UnarmedAbilityTracker.class) != null) {
            return false;
        }
        BrawlersStance stance = hero.buff(BrawlersStance.class);
        return stance != null && stance.active;
    }

    public class Force
    extends Ring.RingBuff {
    }

    public static class BrawlersStance
    extends Buff {
        public boolean active;
        private int minTurnsLeft;
        public static final String ACTIVE = "active";
        public static final String MIN_TURNS_LEFT = "min_turns_left";

        public BrawlersStance() {
            this.announced = true;
            this.type = Buff.buffType.POSITIVE;
            this.active = true;
        }

        public void reset() {
            if (!this.active) {
                this.target.sprite.showStatus(65280, Messages.titleCase(this.name()), new Object[0]);
            }
            this.active = true;
            this.minTurnsLeft = 50;
        }

        @Override
        public int icon() {
            return this.active ? 65 : 127;
        }

        @Override
        public boolean act() {
            --this.minTurnsLeft;
            if (!this.active && this.minTurnsLeft <= 0) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ACTIVE, this.active);
            bundle.put(MIN_TURNS_LEFT, this.minTurnsLeft);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.active = bundle.getBoolean(ACTIVE);
            this.minTurnsLeft = bundle.getInt(MIN_TURNS_LEFT);
        }
    }
}

