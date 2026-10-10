/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.MagicalHolster;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfRecharging;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndUseItem;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MagesStaff
extends MeleeWeapon {
    private Wand wand;
    public static final String AC_IMBUE = "IMBUE";
    public static final String AC_ZAP = "ZAP";
    private static final float STAFF_SCALE_FACTOR = 0.75f;
    private static final String WAND = "wand";
    private final WndBag.ItemSelector itemSelector;
    private final Emitter.Factory StaffParticleFactory;

    public MagesStaff() {
        this.image = ItemSpriteSheet.MAGES_STAFF;
        this.hitSound = "sounds/hit.mp3";
        this.hitSoundPitch = 1.1f;
        this.tier = 1;
        this.defaultAction = AC_ZAP;
        this.usesTargeting = true;
        this.unique = true;
        this.bones = false;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(MagesStaff.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return MagicalHolster.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return item instanceof Wand;
            }

            @Override
            public void onSelect(final Item item) {
                if (item != null) {
                    int itemLevel = item.trueLevel();
                    int newLevel = itemLevel >= MagesStaff.this.trueLevel() ? (MagesStaff.this.trueLevel() > 0 ? itemLevel + 1 : itemLevel) : MagesStaff.this.trueLevel();
                    Object bodyText = Messages.get(MagesStaff.class, "imbue_desc", new Object[0]);
                    bodyText = item.isIdentified() ? (String)bodyText + "\n\n" + Messages.get(MagesStaff.class, "imbue_level", newLevel) : (String)bodyText + "\n\n" + Messages.get(MagesStaff.class, "imbue_unknown", MagesStaff.this.trueLevel());
                    if (!item.cursedKnown || item.cursed) {
                        bodyText = (String)bodyText + "\n\n" + Messages.get(MagesStaff.class, "imbue_cursed", new Object[0]);
                    }
                    if (MagesStaff.this.wand != null) {
                        bodyText = Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION) && Dungeon.hero.buff(Talent.WandPreservationCounter.class) == null ? (String)bodyText + "\n\n" + Messages.get(MagesStaff.class, "imbue_talent", new Object[0]) : (String)bodyText + "\n\n" + Messages.get(MagesStaff.class, "imbue_lost", new Object[0]);
                    }
                    GameScene.show(new WndOptions(new ItemSprite(item), Messages.titleCase(item.name()), (String)bodyText, new String[]{Messages.get(MagesStaff.class, "yes", new Object[0]), Messages.get(MagesStaff.class, "no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                this.applyWand((Wand)item);
                            }
                        }
                    });
                }
            }

            private void applyWand(Wand wand) {
                Sample.INSTANCE.play("sounds/burning.mp3");
                curUser.sprite.emitter().burst(ElmoParticle.FACTORY, 12);
                Item.evoke(curUser);
                Dungeon.quickslot.clearItem(wand);
                wand.detach(curUser.belongings.backpack);
                GLog.p(Messages.get(MagesStaff.class, "imbue", wand.name()), new Object[0]);
                MagesStaff.this.imbueWand(wand, curUser);
                Item.updateQuickslot();
            }
        };
        this.StaffParticleFactory = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                StaffParticle c = (StaffParticle)emitter.getFirstAvailable(StaffParticle.class);
                if (c == null) {
                    c = new StaffParticle();
                    emitter.add(c);
                }
                c.reset(x, y);
            }

            @Override
            public boolean lightMode() {
                return !(MagesStaff.this.wand instanceof WandOfDisintegration) && !(MagesStaff.this.wand instanceof WandOfCorruption) && !(MagesStaff.this.wand instanceof WandOfCorrosion) && !(MagesStaff.this.wand instanceof WandOfRegrowth) && !(MagesStaff.this.wand instanceof WandOfLivingEarth);
            }
        };
        this.wand = null;
    }

    @Override
    public int max(int lvl) {
        return Math.round(3.0f * (float)(this.tier + 1)) + lvl * (this.tier + 1);
    }

    public MagesStaff(Wand wand) {
        this();
        wand.identify();
        wand.cursed = false;
        this.wand = wand;
        this.updateWand(false);
        wand.curCharges = wand.maxCharges;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.add(AC_IMBUE);
        if (this.wand != null && this.wand.curCharges > 0) {
            actions.add(AC_ZAP);
        }
        return actions;
    }

    @Override
    public String defaultAction() {
        return AC_ZAP;
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        this.applyWandChargeBuff(ch);
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (this.wand != null) {
            return this.wand.targetingPos(user, dst);
        }
        return super.targetingPos(user, dst);
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_IMBUE)) {
            curUser = hero;
            GameScene.selectItem(this.itemSelector);
        } else if (action.equals(AC_ZAP)) {
            if (this.wand == null) {
                GameScene.show(new WndUseItem(null, this));
                return;
            }
            this.wand.cursed = this.cursed || this.hasCurseEnchant();
            this.wand.execute(hero, AC_ZAP);
        }
    }

    @Override
    public int buffedVisiblyUpgraded() {
        if (this.wand != null) {
            return Math.max(super.buffedVisiblyUpgraded(), this.wand.buffedVisiblyUpgraded());
        }
        return super.buffedVisiblyUpgraded();
    }

    @Override
    public int proc(Char attacker, Char defender, int damage) {
        Talent.EmpoweredStrikeTracker empoweredStrike;
        if (attacker instanceof Hero && ((Hero)attacker).hasTalent(Talent.MYSTICAL_CHARGE)) {
            Hero hero = (Hero)attacker;
            ArtifactRecharge.chargeArtifacts(hero, (float)hero.pointsInTalent(Talent.MYSTICAL_CHARGE) / 2.0f);
        }
        if ((empoweredStrike = attacker.buff(Talent.EmpoweredStrikeTracker.class)) != null) {
            damage = Math.round((float)damage * (1.0f + (float)Dungeon.hero.pointsInTalent(Talent.EMPOWERED_STRIKE) / 6.0f));
        }
        if (this.wand != null && attacker instanceof Hero && ((Hero)attacker).subClass == HeroSubClass.BATTLEMAGE) {
            if (this.wand.curCharges < this.wand.maxCharges) {
                this.wand.partialCharge += 0.5f;
            }
            ScrollOfRecharging.charge((Hero)attacker);
            this.wand.onHit(this, attacker, defender, damage);
        }
        if (empoweredStrike != null) {
            if (!empoweredStrike.delayedDetach) {
                empoweredStrike.detach();
            }
            if (!(defender instanceof Mob) || !((Mob)defender).surprisedBy(attacker)) {
                Sample.INSTANCE.play("sounds/hit_strong.mp3", 0.75f, 1.2f);
            }
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public int reachFactor(Char owner) {
        int reach = super.reachFactor(owner);
        if (owner instanceof Hero && this.wand instanceof WandOfDisintegration && ((Hero)owner).subClass == HeroSubClass.BATTLEMAGE) {
            reach += Math.round(Wand.procChanceMultiplier(owner));
        }
        return reach;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner != null) {
                this.applyWandChargeBuff(container.owner);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDetach() {
        if (this.wand != null) {
            this.wand.stopCharging();
        }
    }

    public Item imbueWand(Wand wand, Char owner) {
        int slot;
        Talent.WandPreservationCounter counter;
        int oldStaffcharges;
        int n = oldStaffcharges = this.wand != null ? this.wand.curCharges : 0;
        if (owner == Dungeon.hero && this.wand != null && Dungeon.hero.hasTalent(Talent.WAND_PRESERVATION) && (counter = Buff.affect(Dungeon.hero, Talent.WandPreservationCounter.class)).count() == 0.0f) {
            counter.countUp(1.0f);
            this.wand.level(0);
            if (!this.wand.collect()) {
                Dungeon.level.drop(this.wand, owner.pos);
            }
            GLog.newLine();
            GLog.p(Messages.get(this, "preserved", new Object[0]), new Object[0]);
        }
        this.wand = null;
        wand.resinBonus = 0;
        wand.updateLevel();
        int targetLevel = Math.max(this.trueLevel(), wand.trueLevel());
        if (wand.trueLevel() >= this.trueLevel() && this.trueLevel() > 0) {
            ++targetLevel;
        }
        this.level(targetLevel);
        this.wand = wand;
        wand.curChargeKnown = true;
        wand.levelKnown = true;
        this.updateWand(false);
        wand.curCharges = Math.min(wand.maxCharges, wand.curCharges + oldStaffcharges);
        if (owner != null) {
            this.applyWandChargeBuff(owner);
        } else if (Dungeon.hero.belongings.contains(this)) {
            this.applyWandChargeBuff(Dungeon.hero);
        }
        if (!(!wand.cursed || this.cursed && this.hasCurseEnchant())) {
            MagesStaff.equipCursed(Dungeon.hero);
            this.cursedKnown = true;
            this.cursed = true;
            this.enchant(Weapon.Enchantment.randomCurse(new Class[0]));
        }
        if ((slot = Dungeon.quickslot.getSlot(this)) != -1) {
            Dungeon.quickslot.clearSlot(slot);
            MagesStaff.updateQuickslot();
            Dungeon.quickslot.setSlot(slot, this);
            MagesStaff.updateQuickslot();
        }
        Badges.validateItemLevelAquired(this);
        return this;
    }

    public void gainCharge(float amt) {
        this.gainCharge(amt, false);
    }

    public void gainCharge(float amt, boolean overcharge) {
        if (this.wand != null) {
            this.wand.gainCharge(amt, overcharge);
        }
    }

    public void applyWandChargeBuff(Char owner) {
        if (this.wand != null) {
            this.wand.charge(owner, 0.75f);
        }
    }

    public Class<? extends Wand> wandClass() {
        return this.wand != null ? this.wand.getClass() : null;
    }

    @Override
    public Item upgrade(boolean enchant) {
        super.upgrade(enchant);
        this.updateWand(true);
        return this;
    }

    @Override
    public Item degrade() {
        super.degrade();
        this.updateWand(false);
        return this;
    }

    public void updateWand(boolean levelled) {
        if (this.wand != null) {
            int curCharges = this.wand.curCharges;
            this.wand.level(this.level());
            this.wand.maxCharges = Math.min(this.wand.maxCharges + 1, 10);
            this.wand.curCharges = Math.min(curCharges + (levelled ? 1 : 0), this.wand.maxCharges);
            MagesStaff.updateQuickslot();
        }
    }

    @Override
    public String status() {
        if (this.wand == null) {
            return super.status();
        }
        return this.wand.status();
    }

    @Override
    public String name() {
        if (this.wand == null) {
            return super.name();
        }
        String name = Messages.get(this.wand, "staff_name", new Object[0]);
        return this.enchantment != null && (this.cursedKnown || !this.enchantment.curse()) ? this.enchantment.name(name) : name;
    }

    @Override
    public String info() {
        Object info = super.info();
        if (this.wand != null) {
            info = (String)info + "\n\n" + Messages.get(this, "has_wand", Messages.get(this.wand, "name", new Object[0]));
            info = !this.cursed && !this.hasCurseEnchant() || !this.cursedKnown ? (String)info + " " + this.wand.statsDesc() : (String)info + " " + Messages.get(this, "cursed_wand", new Object[0]);
            if (Dungeon.hero.subClass == HeroSubClass.BATTLEMAGE) {
                info = (String)info + "\n\n" + Messages.get(this.wand, "bmage_desc", new Object[0]);
            }
        }
        return info;
    }

    @Override
    public Emitter emitter() {
        if (this.wand == null) {
            return null;
        }
        Emitter emitter = new Emitter();
        emitter.pos(12.5f, 3.0f);
        emitter.fillTarget = false;
        emitter.pour(this.StaffParticleFactory, 0.1f);
        return emitter;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WAND, this.wand);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.wand = (Wand)bundle.get(WAND);
        if (this.wand != null) {
            this.wand.maxCharges = Math.min(this.wand.maxCharges + 1, 10);
        }
    }

    @Override
    public int value() {
        return 0;
    }

    @Override
    public Weapon enchant(Weapon.Enchantment ench) {
        if (this.curseInfusionBonus && (ench == null || !ench.curse())) {
            this.curseInfusionBonus = false;
            this.updateWand(false);
        }
        return super.enchant(ench);
    }

    public class StaffParticle
    extends PixelParticle {
        private float minSize;
        private float maxSize;
        public float sizeJitter = 0.0f;

        public void reset(float x, float y) {
            this.revive();
            this.speed.set(0.0f);
            this.x = x;
            this.y = y;
            if (MagesStaff.this.wand != null) {
                MagesStaff.this.wand.staffFx(this);
            }
        }

        public void setSize(float minSize, float maxSize) {
            this.minSize = minSize;
            this.maxSize = maxSize;
        }

        public void setLifespan(float life) {
            this.lifespan = this.left = life;
        }

        public void shuffleXY(float amt) {
            this.x += Random.Float(-amt, amt);
            this.y += Random.Float(-amt, amt);
        }

        public void radiateXY(float amt) {
            float hypot = (float)Math.hypot(this.speed.x, this.speed.y);
            this.x += this.speed.x / hypot * amt;
            this.y += this.speed.y / hypot * amt;
        }

        @Override
        public void update() {
            super.update();
            this.size(this.minSize + this.left / this.lifespan * (this.maxSize - this.minSize) + Random.Float(this.sizeJitter));
        }
    }
}

