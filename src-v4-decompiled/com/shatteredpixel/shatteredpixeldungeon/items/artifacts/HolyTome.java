/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndClericSpells;
import com.watabou.utils.Bundle;
import java.util.ArrayList;

public class HolyTome
extends Artifact {
    public static final String AC_CAST = "CAST";
    public ClericSpell targetingSpell;
    private ClericSpell quickSpell;
    private static final String QUICK_CLS = "quick_cls";

    public HolyTome() {
        this.image = ItemSpriteSheet.ARTIFACT_TOME;
        this.exp = 0;
        this.levelCap = 10;
        this.charge = Math.min(this.level() + 3, 10);
        this.partialCharge = 0.0f;
        this.chargeCap = Math.min(this.level() + 3, 10);
        this.defaultAction = AC_CAST;
        this.unique = true;
        this.bones = false;
        this.targetingSpell = null;
        this.quickSpell = null;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if ((this.isEquipped(hero) || hero.hasTalent(Talent.LIGHT_READING)) && !this.cursed && hero.buff(MagicImmune.class) == null) {
            actions.add(AC_CAST);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_CAST)) {
            if (!this.isEquipped(hero) && !hero.hasTalent(Talent.LIGHT_READING)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.i(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                GameScene.show(new WndClericSpells(this, hero, false));
            }
        }
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        if (this.targetingSpell == null || this.targetingSpell.targetingFlags() == -1) {
            return super.targetingPos(user, dst);
        }
        return new Ballistica((int)user.pos, (int)dst, (int)this.targetingSpell.targetingFlags()).collisionPos;
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (collect && hero.hasTalent(Talent.LIGHT_READING)) {
                this.activate(hero);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean collect(Bag container) {
        if (super.collect(container)) {
            if (container.owner instanceof Hero && this.passiveBuff == null && ((Hero)container.owner).hasTalent(Talent.LIGHT_READING)) {
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
    }

    public boolean canCast(Hero hero, ClericSpell spell) {
        return (this.isEquipped(hero) || Dungeon.hero.hasTalent(Talent.LIGHT_READING) && hero.belongings.contains(this)) && hero.buff(MagicImmune.class) == null && (float)this.charge >= spell.chargeUse(hero) && spell.canCast(hero);
    }

    public void spendCharge(float chargesSpent) {
        this.partialCharge -= chargesSpent;
        while (this.partialCharge < 0.0f) {
            --this.charge;
            this.partialCharge += 1.0f;
        }
        int lvlDiffFromTarget = Dungeon.hero.lvl - (1 + this.level() * 2);
        if (this.level() >= 7) {
            lvlDiffFromTarget -= this.level() - 6;
        }
        this.exp = lvlDiffFromTarget >= 0 ? (int)((long)this.exp + Math.round((double)(chargesSpent * 10.0f) * Math.pow(1.1f, lvlDiffFromTarget))) : (int)((long)this.exp + Math.round((double)(chargesSpent * 10.0f) * Math.pow(0.75, -lvlDiffFromTarget)));
        if (this.exp >= (this.level() + 1) * 50 && this.level() < this.levelCap) {
            this.upgrade();
            Catalog.countUse(HolyTome.class);
            this.exp -= this.level() * 50;
            GLog.p(Messages.get(this, "levelup", new Object[0]), new Object[0]);
        }
        HolyTome.updateQuickslot();
    }

    public void directCharge(float amount) {
        if (this.charge < this.chargeCap) {
            this.partialCharge += amount;
            while (this.partialCharge >= 1.0f) {
                ++this.charge;
                this.partialCharge -= 1.0f;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
                this.charge = this.chargeCap;
            }
            HolyTome.updateQuickslot();
        }
        HolyTome.updateQuickslot();
    }

    @Override
    public Item upgrade() {
        this.chargeCap = Math.min(this.chargeCap + 1, 10);
        return super.upgrade();
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new TomeRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.charge < this.chargeCap) {
            if (!this.isEquipped(target)) {
                amount *= 0.75f * (float)target.pointsInTalent(Talent.LIGHT_READING) / 3.0f;
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
            HolyTome.updateQuickslot();
        }
    }

    public void setQuickSpell(ClericSpell spell) {
        if (this.quickSpell == spell) {
            this.quickSpell = null;
            if (this.passiveBuff != null) {
                ActionIndicator.clearAction((ActionIndicator.Action)((Object)this.passiveBuff));
            }
        } else {
            this.quickSpell = spell;
            if (this.passiveBuff != null) {
                ActionIndicator.setAction((ActionIndicator.Action)((Object)this.passiveBuff));
            }
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.quickSpell != null) {
            bundle.put(QUICK_CLS, this.quickSpell.getClass());
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        if (bundle.contains(QUICK_CLS)) {
            Class quickCls = bundle.getClass(QUICK_CLS);
            for (ClericSpell spell : ClericSpell.getAllSpells()) {
                if (spell.getClass() != quickCls) continue;
                this.quickSpell = spell;
            }
        }
    }

    public class TomeRecharge
    extends Artifact.ArtifactBuff
    implements ActionIndicator.Action {
        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                if (HolyTome.this.quickSpell != null) {
                    ActionIndicator.setAction(this);
                }
                return true;
            }
            return false;
        }

        @Override
        public void detach() {
            super.detach();
            ActionIndicator.clearAction(this);
        }

        @Override
        public boolean act() {
            if (HolyTome.this.charge < HolyTome.this.chargeCap && !HolyTome.this.cursed && this.target.buff(MagicImmune.class) == null) {
                if (Regeneration.regenOn()) {
                    float missing = HolyTome.this.chargeCap - HolyTome.this.charge;
                    if (HolyTome.this.level() > 7) {
                        missing += (float)(5 * (HolyTome.this.level() - 7)) / 3.0f;
                    }
                    float turnsToCharge = 45.0f - missing;
                    float chargeToGain = 1.0f / (turnsToCharge /= RingOfEnergy.artifactChargeMultiplier(this.target));
                    if (!HolyTome.this.isEquipped(Dungeon.hero)) {
                        chargeToGain *= 0.75f * (float)Dungeon.hero.pointsInTalent(Talent.LIGHT_READING) / 3.0f;
                    }
                    HolyTome.this.partialCharge += chargeToGain;
                }
                while (HolyTome.this.partialCharge >= 1.0f) {
                    ++HolyTome.this.charge;
                    HolyTome.this.partialCharge -= 1.0f;
                    if (HolyTome.this.charge != HolyTome.this.chargeCap) continue;
                    HolyTome.this.partialCharge = 0.0f;
                }
            } else {
                HolyTome.this.partialCharge = 0.0f;
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }

        @Override
        public String actionName() {
            return HolyTome.this.quickSpell.name();
        }

        @Override
        public int actionIcon() {
            return HolyTome.this.quickSpell.icon() + 32;
        }

        @Override
        public int indicatorColor() {
            if (HolyTome.this.quickSpell == GuidingLight.INSTANCE && HolyTome.this.quickSpell.chargeUse(Dungeon.hero) == 0.0f) {
                return 25599;
            }
            return 8535;
        }

        @Override
        public void doAction() {
            if (HolyTome.this.cursed) {
                GLog.w(Messages.get(HolyTome.this, "cursed", new Object[0]), new Object[0]);
                return;
            }
            if (!HolyTome.this.canCast(Dungeon.hero, HolyTome.this.quickSpell)) {
                GLog.w(Messages.get(HolyTome.this, "no_spell", new Object[0]), new Object[0]);
                return;
            }
            if (QuickSlotButton.targetingSlot != -1 && Dungeon.quickslot.getItem(QuickSlotButton.targetingSlot) == HolyTome.this) {
                HolyTome.this.targetingSpell = HolyTome.this.quickSpell;
                int cell = QuickSlotButton.autoAim(QuickSlotButton.lastTarget, HolyTome.this);
                if (cell != -1) {
                    GameScene.handleCell(cell);
                } else {
                    GameScene.handleCell(QuickSlotButton.lastTarget.pos);
                }
            } else {
                HolyTome.this.quickSpell.onCast(HolyTome.this, Dungeon.hero);
                if (HolyTome.this.quickSpell.targetingFlags() != -1 && Dungeon.quickslot.contains(HolyTome.this)) {
                    HolyTome.this.targetingSpell = HolyTome.this.quickSpell;
                    QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(HolyTome.this));
                }
            }
        }
    }
}

