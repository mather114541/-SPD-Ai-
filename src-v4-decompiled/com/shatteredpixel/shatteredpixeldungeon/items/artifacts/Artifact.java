/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Artifact
extends KindofMisc {
    protected Buff passiveBuff;
    protected Buff activeBuff;
    protected int exp = 0;
    protected int levelCap = 0;
    protected int charge = 0;
    protected float partialCharge = 0.0f;
    protected int chargeCap = 0;
    protected int cooldown = 0;
    private static final String EXP = "exp";
    private static final String CHARGE = "charge";
    private static final String PARTIALCHARGE = "partialcharge";

    @Override
    public boolean doEquip(Hero hero) {
        if (hero.belongings.artifact != null && hero.belongings.artifact.getClass() == this.getClass() || hero.belongings.misc != null && hero.belongings.misc.getClass() == this.getClass()) {
            GLog.w(Messages.get(Artifact.class, "cannot_wear_two", new Object[0]), new Object[0]);
            return false;
        }
        if (super.doEquip(hero)) {
            this.identify();
            return true;
        }
        return false;
    }

    @Override
    public void activate(Char ch) {
        if (this.passiveBuff != null) {
            if (this.passiveBuff.target != null) {
                this.passiveBuff.detach();
            }
            this.passiveBuff = null;
        }
        this.passiveBuff = this.passiveBuff();
        this.passiveBuff.attachTo(ch);
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (this.passiveBuff != null) {
                if (this.passiveBuff.target != null) {
                    this.passiveBuff.detach();
                }
                this.passiveBuff = null;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public int visiblyUpgraded() {
        return this.levelKnown ? Math.round((float)(this.level() * 10) / (float)this.levelCap) : 0;
    }

    @Override
    public int buffedVisiblyUpgraded() {
        return this.visiblyUpgraded();
    }

    @Override
    public int buffedLvl() {
        return this.level();
    }

    public void transferUpgrade(int transferLvl) {
        this.upgrade(Math.round((float)(transferLvl * this.levelCap) / 10.0f));
    }

    public void resetForTrinity(int visibleLevel) {
        this.level(Math.round((float)(visibleLevel * this.levelCap) / 10.0f));
        this.exp = Integer.MIN_VALUE;
        this.charge = this.chargeCap;
        this.cooldown = 0;
    }

    public static void artifactProc(Char target, int artifLevel, int chargesUsed) {
        if (Dungeon.hero.subClass == HeroSubClass.PRIEST && target.buff(GuidingLight.Illuminated.class) != null) {
            target.buff(GuidingLight.Illuminated.class).detach();
            target.damage(5 + Dungeon.hero.lvl, GuidingLight.INSTANCE);
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
    public String info() {
        if (this.cursed && this.cursedKnown && !this.isEquipped(Dungeon.hero)) {
            return super.info() + "\n\n" + Messages.get(Artifact.class, "curse_known", new Object[0]);
        }
        if (!this.isIdentified() && this.cursedKnown && !this.isEquipped(Dungeon.hero)) {
            return super.info() + "\n\n" + Messages.get(Artifact.class, "not_cursed", new Object[0]);
        }
        return super.info();
    }

    @Override
    public String status() {
        if (!this.isIdentified() || this.cursed) {
            return null;
        }
        if (this.cooldown != 0) {
            return Messages.format("%d", this.cooldown);
        }
        if (this.chargeCap == 100) {
            return Messages.format("%d%%", this.charge);
        }
        if (this.chargeCap > 0) {
            return Messages.format("%d/%d", this.charge, this.chargeCap);
        }
        if (this.charge != 0) {
            return Messages.format("%d", this.charge);
        }
        return null;
    }

    @Override
    public Item random() {
        if (Random.Float() < 0.3f) {
            this.cursed = true;
        }
        return this;
    }

    @Override
    public int value() {
        int price = 100;
        if (this.level() > 0) {
            price += 20 * this.visiblyUpgraded();
        }
        if (this.cursed && this.cursedKnown) {
            price /= 2;
        }
        if (price < 1) {
            price = 1;
        }
        return price;
    }

    protected ArtifactBuff passiveBuff() {
        return null;
    }

    protected ArtifactBuff activeBuff() {
        return null;
    }

    public void charge(Hero target, float amount) {
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(EXP, this.exp);
        bundle.put(CHARGE, this.charge);
        bundle.put(PARTIALCHARGE, this.partialCharge);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.exp = bundle.getInt(EXP);
        this.charge = this.chargeCap > 0 ? Math.min(this.chargeCap, bundle.getInt(CHARGE)) : bundle.getInt(CHARGE);
        this.partialCharge = bundle.getFloat(PARTIALCHARGE);
    }

    public class ArtifactBuff
    extends Buff {
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

        public int itemLevel() {
            return Artifact.this.level();
        }

        public boolean isCursed() {
            return this.target.buff(MagicImmune.class) == null && Artifact.this.cursed;
        }

        public void charge(Hero target, float amount) {
            Artifact.this.charge(target, amount);
        }
    }
}

