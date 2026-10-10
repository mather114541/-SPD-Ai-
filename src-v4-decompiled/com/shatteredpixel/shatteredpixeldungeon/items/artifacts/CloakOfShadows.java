/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class CloakOfShadows
extends Artifact {
    public static final String AC_STEALTH = "STEALTH";
    private static final String STEALTHED = "stealthed";
    private static final String BUFF = "buff";

    public CloakOfShadows() {
        this.image = ItemSpriteSheet.ARTIFACT_CLOAK;
        this.exp = 0;
        this.levelCap = 10;
        this.charge = Math.min(this.level() + 3, 10);
        this.partialCharge = 0.0f;
        this.chargeCap = Math.min(this.level() + 3, 10);
        this.defaultAction = AC_STEALTH;
        this.unique = true;
        this.bones = false;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (!(!this.isEquipped(hero) && !hero.hasTalent(Talent.LIGHT_CLOAK) || this.cursed || hero.buff(MagicImmune.class) != null || this.charge <= 0 && this.activeBuff == null)) {
            actions.add(AC_STEALTH);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_STEALTH)) {
            if (this.activeBuff == null) {
                if (!this.isEquipped(hero) && !hero.hasTalent(Talent.LIGHT_CLOAK)) {
                    GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
                } else if (this.cursed) {
                    GLog.i(Messages.get(this, "cursed", new Object[0]), new Object[0]);
                } else if (this.charge <= 0) {
                    GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
                } else {
                    hero.spend(1.0f);
                    hero.busy();
                    Sample.INSTANCE.play("sounds/meld.mp3");
                    this.activeBuff = this.activeBuff();
                    this.activeBuff.attachTo(hero);
                    Talent.onArtifactUsed(Dungeon.hero);
                    hero.sprite.operate(hero.pos);
                }
            } else {
                this.activeBuff.detach();
                this.activeBuff = null;
                if (hero.invisible <= 0 && hero.buff(Preparation.class) != null) {
                    hero.buff(Preparation.class).detach();
                }
                hero.sprite.operate(hero.pos);
            }
        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (this.activeBuff != null && this.activeBuff.target == null) {
            this.activeBuff.attachTo(ch);
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (!collect || !hero.hasTalent(Talent.LIGHT_CLOAK)) {
                if (this.activeBuff != null) {
                    this.activeBuff.detach();
                    this.activeBuff = null;
                }
            } else {
                this.activate(hero);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner instanceof Hero && this.passiveBuff == null && ((Hero)container.owner).hasTalent(Talent.LIGHT_CLOAK)) {
                this.activate((Hero)container.owner);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDetach() {
        if (this.passiveBuff != null) {
            this.passiveBuff.detach();
            this.passiveBuff = null;
        }
        if (this.activeBuff != null && !this.isEquipped((Hero)this.activeBuff.target)) {
            this.activeBuff.detach();
            this.activeBuff = null;
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new cloakRecharge();
    }

    @Override
    protected Artifact.ArtifactBuff activeBuff() {
        return new cloakStealth();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.charge < this.chargeCap) {
            if (!this.isEquipped(target)) {
                amount *= 0.75f * (float)target.pointsInTalent(Talent.LIGHT_CLOAK) / 3.0f;
            }
            this.partialCharge += 0.25f * amount;
            while (this.partialCharge >= 1.0f) {
                ++this.charge;
                this.partialCharge -= 1.0f;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
                this.charge = this.chargeCap;
            }
            CloakOfShadows.updateQuickslot();
        }
    }

    public void directCharge(int amount) {
        this.charge = Math.min(this.charge + amount, this.chargeCap);
        CloakOfShadows.updateQuickslot();
    }

    @Override
    public Item upgrade() {
        this.chargeCap = Math.min(this.chargeCap + 1, 10);
        return super.upgrade();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.activeBuff != null) {
            bundle.put(BUFF, this.activeBuff);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(BUFF)) {
            this.activeBuff = new cloakStealth();
            this.activeBuff.restoreFromBundle(bundle.getBundle(BUFF));
        }
    }

    @Override
    public int value() {
        return 0;
    }

    public class cloakRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (CloakOfShadows.this.charge < CloakOfShadows.this.chargeCap && !CloakOfShadows.this.cursed && this.target.buff(MagicImmune.class) == null) {
                if (CloakOfShadows.this.activeBuff == null && Regeneration.regenOn()) {
                    float missing = CloakOfShadows.this.chargeCap - CloakOfShadows.this.charge;
                    if (CloakOfShadows.this.level() > 7) {
                        missing += (float)(5 * (CloakOfShadows.this.level() - 7)) / 3.0f;
                    }
                    float turnsToCharge = 45.0f - missing;
                    float chargeToGain = 1.0f / (turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(this.target));
                    if (!CloakOfShadows.this.isEquipped(Dungeon.hero)) {
                        chargeToGain *= 0.75f * (float)Dungeon.hero.pointsInTalent(Talent.LIGHT_CLOAK) / 3.0f;
                    }
                    CloakOfShadows.this.partialCharge += chargeToGain;
                }
                while (CloakOfShadows.this.partialCharge >= 1.0f) {
                    ++CloakOfShadows.this.charge;
                    CloakOfShadows.this.partialCharge -= 1.0f;
                    if (CloakOfShadows.this.charge != CloakOfShadows.this.chargeCap) continue;
                    CloakOfShadows.this.partialCharge = 0.0f;
                }
            } else {
                CloakOfShadows.this.partialCharge = 0.0f;
            }
            if (CloakOfShadows.this.cooldown > 0) {
                --CloakOfShadows.this.cooldown;
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }
    }

    public class cloakStealth
    extends Artifact.ArtifactBuff {
        int turnsToCost;
        private static final String TURNSTOCOST = "turnsToCost";
        private static final String BARRIER_INC = "barrier_inc";

        public cloakStealth() {
            this.type = Buff.buffType.POSITIVE;
            this.turnsToCost = 0;
        }

        @Override
        public int icon() {
            return 12;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.brightness(0.6f);
        }

        @Override
        public float iconFadePercent() {
            return (4.0f - (float)this.turnsToCost) / 4.0f;
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString(this.turnsToCost);
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", this.turnsToCost);
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                ++target.invisible;
                if (target instanceof Hero && ((Hero)target).subClass == HeroSubClass.ASSASSIN) {
                    Buff.affect(target, Preparation.class);
                }
                if (target instanceof Hero && ((Hero)target).hasTalent(Talent.PROTECTIVE_SHADOWS)) {
                    Buff.affect(target, Talent.ProtectiveShadowsTracker.class);
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            --this.turnsToCost;
            if (this.turnsToCost <= 0) {
                --CloakOfShadows.this.charge;
                if (CloakOfShadows.this.charge < 0) {
                    CloakOfShadows.this.charge = 0;
                    this.detach();
                    GLog.w(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
                    ((Hero)this.target).interrupt();
                } else {
                    int lvlDiffFromTarget = ((Hero)this.target).lvl - (1 + CloakOfShadows.this.level() * 2);
                    if (CloakOfShadows.this.level() >= 7) {
                        lvlDiffFromTarget -= CloakOfShadows.this.level() - 6;
                    }
                    CloakOfShadows.this.exp = lvlDiffFromTarget >= 0 ? (int)((long)CloakOfShadows.this.exp + Math.round(10.0 * Math.pow(1.1f, lvlDiffFromTarget))) : (int)((long)CloakOfShadows.this.exp + Math.round(10.0 * Math.pow(0.75, -lvlDiffFromTarget)));
                    if (CloakOfShadows.this.exp >= (CloakOfShadows.this.level() + 1) * 50 && CloakOfShadows.this.level() < CloakOfShadows.this.levelCap) {
                        CloakOfShadows.this.upgrade();
                        Catalog.countUse(CloakOfShadows.class);
                        CloakOfShadows.this.exp -= CloakOfShadows.this.level() * 50;
                        GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
                    }
                    this.turnsToCost = 4;
                }
                Item.updateQuickslot();
            }
            this.spend(1.0f);
            return true;
        }

        public void dispel() {
            if (this.turnsToCost <= 0 && CloakOfShadows.this.charge > 0) {
                --CloakOfShadows.this.charge;
            }
            Item.updateQuickslot();
            this.detach();
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.INVISIBLE);
            } else if (this.target.invisible == 0) {
                this.target.sprite.remove(CharSprite.State.INVISIBLE);
            }
        }

        @Override
        public void detach() {
            CloakOfShadows.this.activeBuff = null;
            if (this.target.invisible > 0) {
                --this.target.invisible;
            }
            Item.updateQuickslot();
            super.detach();
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(TURNSTOCOST, this.turnsToCost);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.turnsToCost = bundle.getInt(TURNSTOCOST);
        }
    }
}

