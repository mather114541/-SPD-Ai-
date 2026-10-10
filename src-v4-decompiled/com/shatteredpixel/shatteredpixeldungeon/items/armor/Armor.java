/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.armor;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Momentum;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.AuraOfProtection;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.HolyWard;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.PrismaticImage;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.BrokenSeal;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.AntiEntropy;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Bulk;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Displacement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Metabolism;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Multiplicity;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Overgrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.curses.Stench;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Affection;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.AntiMagic;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Brimstone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Camouflage;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Entanglement;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Flow;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Obfuscation;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Potential;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Repulsion;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Stone;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Swiftness;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Thorns;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfArcana;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ParchmentScrap;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Arrays;

public class Armor
extends EquipableItem {
    protected static final String AC_DETACH = "DETACH";
    public Augment augment = Augment.NONE;
    public Glyph glyph;
    public boolean glyphHardened = false;
    public boolean curseInfusionBonus = false;
    public boolean masteryPotionBonus = false;
    protected BrokenSeal seal;
    public int tier;
    private static final int USES_TO_ID = 10;
    private float usesLeftToID = 10.0f;
    private float availableUsesToID = 5.0f;
    private static final String USES_LEFT_TO_ID = "uses_left_to_id";
    private static final String AVAILABLE_USES = "available_uses";
    private static final String GLYPH = "glyph";
    private static final String GLYPH_HARDENED = "glyph_hardened";
    private static final String CURSE_INFUSION_BONUS = "curse_infusion_bonus";
    private static final String MASTERY_POTION_BONUS = "mastery_potion_bonus";
    private static final String SEAL = "seal";
    private static final String AUGMENT = "augment";
    public static boolean testingNoArmDefSkill = false;
    private static ItemSprite.Glowing HOLY = new ItemSprite.Glowing(0xFFFF00);

    public Armor(int tier) {
        this.tier = tier;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(USES_LEFT_TO_ID, this.usesLeftToID);
        bundle.put(AVAILABLE_USES, this.availableUsesToID);
        bundle.put(GLYPH, this.glyph);
        bundle.put(GLYPH_HARDENED, this.glyphHardened);
        bundle.put(CURSE_INFUSION_BONUS, this.curseInfusionBonus);
        bundle.put(MASTERY_POTION_BONUS, this.masteryPotionBonus);
        bundle.put(SEAL, this.seal);
        bundle.put(AUGMENT, this.augment);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.usesLeftToID = bundle.getInt(USES_LEFT_TO_ID);
        this.availableUsesToID = bundle.getInt(AVAILABLE_USES);
        this.inscribe((Glyph)bundle.get(GLYPH));
        this.glyphHardened = bundle.getBoolean(GLYPH_HARDENED);
        this.curseInfusionBonus = bundle.getBoolean(CURSE_INFUSION_BONUS);
        this.masteryPotionBonus = bundle.getBoolean(MASTERY_POTION_BONUS);
        this.seal = (BrokenSeal)bundle.get(SEAL);
        this.augment = bundle.getEnum(AUGMENT, Augment.class);
    }

    @Override
    public void reset() {
        super.reset();
        this.usesLeftToID = 10.0f;
        this.availableUsesToID = 5.0f;
        this.seal = null;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.seal != null) {
            actions.add(AC_DETACH);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_DETACH) && this.seal != null) {
            BrokenSeal detaching = this.detachSeal();
            GLog.i(Messages.get(Armor.class, "detach_seal", new Object[0]), new Object[0]);
            hero.sprite.operate(hero.pos);
            if (!detaching.collect()) {
                Dungeon.level.drop(detaching, hero.pos);
            }
            Armor.updateQuickslot();
        }
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (Dungeon.hero != null && Dungeon.hero.isAlive() && this.isIdentified() && this.glyph != null) {
                Catalog.setSeen(this.glyph.getClass());
                Statistics.itemTypesDiscovered.add(this.glyph.getClass());
            }
            return true;
        }
        return false;
    }

    @Override
    public Item identify(boolean byHero) {
        if (this.glyph != null && byHero && Dungeon.hero != null && Dungeon.hero.isAlive()) {
            Catalog.setSeen(this.glyph.getClass());
            Statistics.itemTypesDiscovered.add(this.glyph.getClass());
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
    public boolean doEquip(Hero hero) {
        if (hero.heroClass != HeroClass.CLERIC && hero.hasTalent(Talent.HOLY_INTUITION) && this.cursed && !this.cursedKnown && Random.Int(20) < 1 + 2 * hero.pointsInTalent(Talent.HOLY_INTUITION)) {
            this.cursedKnown = true;
            GLog.p(Messages.get(this, "curse_detected", new Object[0]), new Object[0]);
            return false;
        }
        this.detach(hero.belongings.backpack);
        final Armor oldArmor = hero.belongings.armor;
        if (hero.belongings.armor == null || hero.belongings.armor.doUnequip(hero, true, false)) {
            hero.belongings.armor = this;
            this.cursedKnown = true;
            if (this.cursed) {
                Armor.equipCursed(hero);
                GLog.n(Messages.get(Armor.class, "equip_cursed", new Object[0]), new Object[0]);
            }
            ((HeroSprite)hero.sprite).updateArmor();
            this.activate(hero);
            Talent.onItemEquipped(hero, this);
            hero.spend(this.timeToEquip(hero));
            if (Dungeon.hero.heroClass == HeroClass.WARRIOR && this.checkSeal() == null) {
                BrokenSeal seal;
                BrokenSeal brokenSeal = seal = oldArmor != null ? oldArmor.checkSeal() : null;
                if (seal != null && (!this.cursed || seal.getGlyph() != null && seal.getGlyph().curse())) {
                    GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.SEAL), Messages.titleCase(seal.trueName()), Messages.get(Armor.class, "seal_transfer", new Object[0]), new String[]{Messages.get(Armor.class, "seal_transfer_yes", new Object[0]), Messages.get(Armor.class, "seal_transfer_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            super.onSelect(index);
                            if (index == 0) {
                                seal.affixToArmor(Armor.this, oldArmor);
                                Item.updateQuickslot();
                            }
                            super.hide();
                        }

                        @Override
                        public void hide() {
                        }
                    });
                } else {
                    hero.next();
                }
            } else {
                hero.next();
            }
            return true;
        }
        this.collect(hero.belongings.backpack);
        return false;
    }

    @Override
    public void activate(Char ch) {
        if (this.seal != null) {
            Buff.affect(ch, BrokenSeal.WarriorShield.class).setArmor(this);
        }
    }

    public void affixSeal(BrokenSeal seal) {
        this.seal = seal;
        if (seal.level() > 0) {
            int newLevel = this.trueLevel() + 1;
            this.level(newLevel);
            Badges.validateItemLevelAquired(this);
        }
        if (seal.getGlyph() != null) {
            this.inscribe(seal.getGlyph());
        }
        if (this.isEquipped(Dungeon.hero)) {
            Buff.affect(Dungeon.hero, BrokenSeal.WarriorShield.class).setArmor(this);
        }
    }

    public BrokenSeal detachSeal() {
        if (this.seal != null) {
            BrokenSeal.WarriorShield sealBuff;
            if (this.isEquipped(Dungeon.hero) && (sealBuff = Dungeon.hero.buff(BrokenSeal.WarriorShield.class)) != null) {
                sealBuff.setArmor(null);
            }
            BrokenSeal detaching = this.seal;
            this.seal = null;
            if (detaching.level() > 0) {
                this.degrade();
            }
            if (detaching.canTransferGlyph()) {
                this.inscribe(null);
            } else {
                detaching.setGlyph(null);
            }
            return detaching;
        }
        return null;
    }

    public BrokenSeal checkSeal() {
        return this.seal;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            hero.belongings.armor = null;
            ((HeroSprite)hero.sprite).updateArmor();
            BrokenSeal.WarriorShield sealBuff = hero.buff(BrokenSeal.WarriorShield.class);
            if (sealBuff != null) {
                sealBuff.setArmor(null);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isEquipped(Hero hero) {
        return hero != null && hero.belongings.armor() == this;
    }

    public final int DRMax() {
        return this.DRMax(this.buffedLvl());
    }

    public int DRMax(int lvl) {
        if (Dungeon.isChallenged(2)) {
            return 1 + this.tier + lvl + this.augment.defenseFactor(lvl);
        }
        int max = this.tier * (2 + lvl) + this.augment.defenseFactor(lvl);
        if (lvl > max) {
            return (lvl - max + 1) / 2;
        }
        return max;
    }

    public final int DRMin() {
        return this.DRMin(this.buffedLvl());
    }

    public int DRMin(int lvl) {
        if (Dungeon.isChallenged(2)) {
            return 0;
        }
        int max = this.DRMax(lvl);
        if (lvl >= max) {
            return lvl - max;
        }
        return lvl;
    }

    public float evasionFactor(Char owner, float evasion) {
        if (testingNoArmDefSkill) {
            return evasion;
        }
        if (this.hasGlyph(Stone.class, owner) && !Stone.testingEvasion()) {
            return 0.0f;
        }
        if (owner instanceof Hero) {
            Momentum momentum;
            int aEnc = this.STRReq() - ((Hero)owner).STR();
            if (aEnc > 0) {
                evasion = (float)((double)evasion / Math.pow(1.5, aEnc));
            }
            if ((momentum = owner.buff(Momentum.class)) != null) {
                evasion += (float)momentum.evasionBonus(((Hero)owner).lvl, Math.max(0, -aEnc));
            }
        }
        return evasion + (float)this.augment.evasionFactor(this.buffedLvl());
    }

    public float speedFactor(Char owner, float speed) {
        int aEnc;
        if (owner instanceof Hero && (aEnc = this.STRReq() - ((Hero)owner).STR()) > 0) {
            speed = (float)((double)speed / Math.pow(1.2, aEnc));
        }
        return speed;
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

    public Item upgrade(boolean inscribe) {
        if (inscribe) {
            if (this.glyph == null) {
                this.inscribe(Glyph.random(new Class[0]));
            }
        } else if (this.glyph != null) {
            if (this.glyphHardened) {
                if (this.level() >= 6 && (double)Random.Float(10.0f) < Math.pow(2.0, this.level() - 6)) {
                    this.glyphHardened = false;
                }
            } else if (this.hasCurseGlyph()) {
                if (Random.Int(3) == 0) {
                    this.inscribe(null);
                }
            } else {
                int lossChanceStart = 4;
                if (Dungeon.hero != null && Dungeon.hero.heroClass != HeroClass.WARRIOR && Dungeon.hero.hasTalent(Talent.RUNIC_TRANSFERENCE)) {
                    lossChanceStart += 1 + Dungeon.hero.pointsInTalent(Talent.RUNIC_TRANSFERENCE);
                }
                if (this.level() >= lossChanceStart && (double)Random.Float(10.0f) < Math.pow(2.0, this.level() - 4)) {
                    this.inscribe(null);
                }
            }
        }
        this.cursed = false;
        if (this.seal != null && this.seal.level() == 0) {
            this.seal.upgrade();
        }
        return super.upgrade();
    }

    public int proc(Char attacker, Char defender, int damage) {
        if (defender.buff(MagicImmune.class) == null) {
            Glyph trinityGlyph = null;
            if (Dungeon.hero.buff(BodyForm.BodyFormBuff.class) != null && (defender == Dungeon.hero || defender instanceof PrismaticImage || defender instanceof ShadowClone.ShadowAlly)) {
                trinityGlyph = Dungeon.hero.buff(BodyForm.BodyFormBuff.class).glyph();
                if (this.glyph != null && trinityGlyph != null && trinityGlyph.getClass() == this.glyph.getClass()) {
                    trinityGlyph = null;
                }
            }
            if (defender instanceof Hero && this.isEquipped((Hero)defender) && defender.buff(HolyWard.HolyArmBuff.class) != null) {
                if (this.glyph != null && (((Hero)defender).subClass == HeroSubClass.PALADIN || this.hasCurseGlyph())) {
                    damage = this.glyph.proc(this, attacker, defender, damage);
                }
                if (trinityGlyph != null) {
                    damage = trinityGlyph.proc(this, attacker, defender, damage);
                }
                int blocking = ((Hero)defender).subClass == HeroSubClass.PALADIN ? 3 : 1;
                damage -= Math.round((float)blocking * Glyph.genericProcChanceMultiplier(defender));
            } else {
                if (this.glyph != null) {
                    damage = this.glyph.proc(this, attacker, defender, damage);
                }
                if (trinityGlyph != null) {
                    damage = trinityGlyph.proc(this, attacker, defender, damage);
                }
                if (defender.alignment == Dungeon.hero.alignment && Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null && (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null) {
                    int blocking = Dungeon.hero.subClass == HeroSubClass.PALADIN ? 3 : 1;
                    damage -= Math.round((float)blocking * Glyph.genericProcChanceMultiplier(defender));
                }
            }
            damage = Math.max(damage, 0);
        }
        if (!this.levelKnown && defender == Dungeon.hero) {
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
                    GLog.p(Messages.get(Armor.class, "identify", new Object[0]), new Object[0]);
                    Badges.validateItemLevelAquired(this);
                }
            }
        }
        return damage;
    }

    @Override
    public void onHeroGainExp(float levelPercent, Hero hero) {
        levelPercent *= Talent.itemIDSpeedFactor(hero, this);
        if (!this.levelKnown && this.isEquipped(hero) && this.availableUsesToID <= 5.0f) {
            this.availableUsesToID = Math.min(5.0f, this.availableUsesToID + levelPercent * 10.0f);
        }
    }

    @Override
    public String name() {
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.glyph == null)) {
            return Messages.get(HolyWard.class, "glyph_name", super.name());
        }
        return this.glyph != null && (this.cursedKnown || !this.glyph.curse()) ? this.glyph.name(super.name()) : super.name();
    }

    @Override
    public String info() {
        Object info = super.info();
        if (this.levelKnown) {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "curr_absorb", this.tier, this.DRMin(), this.DRMax(), this.STRReq());
            if (Dungeon.hero != null && this.STRReq() > Dungeon.hero.STR()) {
                info = (String)info + " " + Messages.get(Armor.class, "too_heavy", new Object[0]);
            }
        } else {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "avg_absorb", this.tier, this.DRMin(0), this.DRMax(0), this.STRReq(0));
            if (Dungeon.hero != null && this.STRReq(0) > Dungeon.hero.STR()) {
                info = (String)info + " " + Messages.get(Armor.class, "probably_too_heavy", new Object[0]);
            }
        }
        switch (this.augment.ordinal()) {
            case 0: {
                info = (String)info + " " + Messages.get(Armor.class, "evasion", new Object[0]);
                break;
            }
            case 1: {
                info = (String)info + " " + Messages.get(Armor.class, "defense", new Object[0]);
                break;
            }
        }
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.glyph == null)) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Armor.class, "inscribed", Messages.get(HolyWard.class, "glyph_name", Messages.get(Glyph.class, GLYPH, new Object[0]))));
            info = (String)info + " " + Messages.get(HolyWard.class, "glyph_desc", new Object[0]);
        } else if (this.glyph != null && (this.cursedKnown || !this.glyph.curse())) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Armor.class, "inscribed", this.glyph.name()));
            if (this.glyphHardened) {
                info = (String)info + " " + Messages.get(Armor.class, GLYPH_HARDENED, new Object[0]);
            }
            info = (String)info + " " + this.glyph.desc();
        } else if (this.glyphHardened) {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "hardened_no_glyph", new Object[0]);
        }
        if (this.cursed && this.isEquipped(Dungeon.hero)) {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "cursed_worn", new Object[0]);
        } else if (this.cursedKnown && this.cursed) {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "cursed", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            info = this.glyph != null && this.glyph.curse() ? (String)info + "\n\n" + Messages.get(Armor.class, "weak_cursed", new Object[0]) : (String)info + "\n\n" + Messages.get(Armor.class, "not_cursed", new Object[0]);
        }
        if (this.seal != null) {
            info = (String)info + "\n\n" + Messages.get(Armor.class, "seal_attached", this.seal.maxShield(this.tier, this.level()));
        }
        return info;
    }

    @Override
    public Emitter emitter() {
        if (this.seal == null) {
            return super.emitter();
        }
        Emitter emitter = new Emitter();
        emitter.pos(ItemSpriteSheet.film.width(this.image) / 2.0f + 2.0f, ItemSpriteSheet.film.height(this.image) / 3.0f);
        emitter.fillTarget = false;
        emitter.pour(Speck.factory(114), 0.6f);
        return emitter;
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
            this.inscribe(Glyph.randomCurse(new Class[0]));
            this.cursed = true;
        } else if (effectRoll >= 1.0f - 0.15f * ParchmentScrap.enchantChanceMultiplier()) {
            this.inscribe();
        }
        Random.popGenerator();
        return this;
    }

    public int STRReq() {
        return this.STRReq(this.level());
    }

    public int STRReq(int lvl) {
        int req = Armor.STRReq(this.tier, lvl);
        if (this.masteryPotionBonus) {
            req -= 2;
        }
        return req;
    }

    protected static int STRReq(int tier, int lvl) {
        lvl = Math.max(0, lvl);
        return 8 + Math.round(tier * 2) - (int)(Math.sqrt(8 * lvl + 1) - 1.0) / 2;
    }

    @Override
    public int value() {
        if (this.seal != null) {
            return 0;
        }
        int price = 20 * this.tier;
        if (this.hasGoodGlyph()) {
            price = (int)((double)price * 1.5);
        }
        if (this.cursedKnown && (this.cursed || this.hasCurseGlyph())) {
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

    public Armor inscribe(Glyph glyph) {
        if (glyph == null || !glyph.curse()) {
            this.curseInfusionBonus = false;
        }
        this.glyph = glyph;
        Armor.updateQuickslot();
        if (this.seal != null) {
            this.seal.setGlyph(glyph);
        }
        if (glyph != null && this.isIdentified() && Dungeon.hero != null && Dungeon.hero.isAlive() && Dungeon.hero.belongings.contains(this)) {
            Catalog.setSeen(glyph.getClass());
            Statistics.itemTypesDiscovered.add(glyph.getClass());
        }
        return this;
    }

    public Armor inscribe() {
        Class<?> oldGlyphClass = this.glyph != null ? this.glyph.getClass() : null;
        Glyph gl = Glyph.random(oldGlyphClass);
        return this.inscribe(gl);
    }

    public boolean hasGlyph(Class<? extends Glyph> type, Char owner) {
        if (owner.buff(MagicImmune.class) != null) {
            return false;
        }
        if (this.glyph != null && !this.glyph.curse() && owner instanceof Hero && this.isEquipped((Hero)owner) && owner.buff(HolyWard.HolyArmBuff.class) != null && ((Hero)owner).subClass != HeroSubClass.PALADIN) {
            return false;
        }
        if (owner.buff(BodyForm.BodyFormBuff.class) != null && owner.buff(BodyForm.BodyFormBuff.class).glyph() != null && owner.buff(BodyForm.BodyFormBuff.class).glyph().getClass().equals(type)) {
            return true;
        }
        if (this.glyph != null) {
            return this.glyph.getClass() == type;
        }
        return false;
    }

    public boolean hasGoodGlyph() {
        return this.glyph != null && !this.glyph.curse();
    }

    public boolean hasCurseGlyph() {
        return this.glyph != null && this.glyph.curse();
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.isEquipped(Dungeon.hero) && !this.hasCurseGlyph() && Dungeon.hero.buff(HolyWard.HolyArmBuff.class) != null && (Dungeon.hero.subClass != HeroSubClass.PALADIN || this.glyph == null)) {
            return HOLY;
        }
        return this.glyph != null && (this.cursedKnown || !this.glyph.curse()) ? this.glyph.glowing() : null;
    }

    public static enum Augment {
        EVASION(2.0f, -1.0f),
        DEFENSE(-2.0f, 1.0f),
        NONE(0.0f, 0.0f);

        private float evasionFactor;
        private float defenceFactor;

        private Augment(float eva, float df) {
            this.evasionFactor = eva;
            this.defenceFactor = df;
        }

        public int evasionFactor(int level) {
            return Math.round((float)(2 + level) * this.evasionFactor);
        }

        public int defenseFactor(int level) {
            return Math.round((float)(2 + level) * this.defenceFactor);
        }
    }

    public static abstract class Glyph
    implements Bundlable {
        public static final Class<?>[] common = new Class[]{Obfuscation.class, Swiftness.class, Viscosity.class, Potential.class};
        public static final Class<?>[] uncommon = new Class[]{Brimstone.class, Stone.class, Entanglement.class, Repulsion.class, Camouflage.class, Flow.class};
        public static final Class<?>[] rare = new Class[]{Affection.class, AntiMagic.class, Thorns.class};
        public static final float[] typeChances = new float[]{50.0f, 40.0f, 10.0f};
        public static final Class<?>[] curses = new Class[]{AntiEntropy.class, Corrosion.class, Displacement.class, Metabolism.class, Multiplicity.class, Stench.class, Overgrowth.class, Bulk.class};

        public abstract int proc(Armor var1, Char var2, Char var3, int var4);

        protected float procChanceMultiplier(Char defender) {
            return Glyph.genericProcChanceMultiplier(defender);
        }

        public static float genericProcChanceMultiplier(Char defender) {
            float multi = RingOfArcana.enchantPowerMultiplier(defender);
            if (Dungeon.hero.alignment == defender.alignment && Dungeon.hero.buff(AuraOfProtection.AuraBuff.class) != null && (Dungeon.level.distance(defender.pos, Dungeon.hero.pos) <= 2 || defender.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null)) {
                multi += 0.25f + 0.25f * (float)Dungeon.hero.pointsInTalent(Talent.AURA_OF_PROTECTION);
            }
            return multi;
        }

        public String name() {
            if (!this.curse()) {
                return this.name(Messages.get(this, Armor.GLYPH, new Object[0]));
            }
            return this.name(Messages.get(Item.class, "curse", new Object[0]));
        }

        public String name(String armorName) {
            return Messages.get(this, "name", armorName);
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

        public static Glyph random(Class<? extends Glyph> ... toIgnore) {
            switch (Random.chances(typeChances)) {
                default: {
                    return Glyph.randomCommon(toIgnore);
                }
                case 1: {
                    return Glyph.randomUncommon(toIgnore);
                }
                case 2: 
            }
            return Glyph.randomRare(toIgnore);
        }

        public static Glyph randomCommon(Class<? extends Glyph> ... toIgnore) {
            ArrayList<Class<Class<? extends Glyph>>> glyphs = new ArrayList<Class<Class<? extends Glyph>>>(Arrays.asList(common));
            glyphs.removeAll(Arrays.asList(toIgnore));
            if (glyphs.isEmpty()) {
                return Glyph.random(new Class[0]);
            }
            return (Glyph)Reflection.newInstance(Random.element(glyphs));
        }

        public static Glyph randomUncommon(Class<? extends Glyph> ... toIgnore) {
            ArrayList<Class<Class<? extends Glyph>>> glyphs = new ArrayList<Class<Class<? extends Glyph>>>(Arrays.asList(uncommon));
            glyphs.removeAll(Arrays.asList(toIgnore));
            if (glyphs.isEmpty()) {
                return Glyph.random(new Class[0]);
            }
            return (Glyph)Reflection.newInstance(Random.element(glyphs));
        }

        public static Glyph randomRare(Class<? extends Glyph> ... toIgnore) {
            ArrayList<Class<Class<? extends Glyph>>> glyphs = new ArrayList<Class<Class<? extends Glyph>>>(Arrays.asList(rare));
            glyphs.removeAll(Arrays.asList(toIgnore));
            if (glyphs.isEmpty()) {
                return Glyph.random(new Class[0]);
            }
            return (Glyph)Reflection.newInstance(Random.element(glyphs));
        }

        public static Glyph randomCurse(Class<? extends Glyph> ... toIgnore) {
            ArrayList<Class<Class<? extends Glyph>>> glyphs = new ArrayList<Class<Class<? extends Glyph>>>(Arrays.asList(curses));
            glyphs.removeAll(Arrays.asList(toIgnore));
            if (glyphs.isEmpty()) {
                return Glyph.random(new Class[0]);
            }
            return (Glyph)Reflection.newInstance(Random.element(glyphs));
        }
    }
}

