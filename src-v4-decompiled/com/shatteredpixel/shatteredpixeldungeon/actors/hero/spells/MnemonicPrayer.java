/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AdrenalineSurge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArcaneArmor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barkskin;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FireImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ToxicImbue;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.WellFed;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BeamingRay;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.BodyForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.LifeLinkSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.SpiritForm;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.TargetedClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.elixirs.ElixirOfAquaticRejuvenation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;

public class MnemonicPrayer
extends TargetedClericSpell {
    public static MnemonicPrayer INSTANCE = new MnemonicPrayer();

    @Override
    public int icon() {
        return 53;
    }

    @Override
    public int targetingFlags() {
        return 1;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MNEMONIC_PRAYER);
    }

    @Override
    protected void onTargetSelected(HolyTome tome, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Char ch = Actor.findChar(target);
        if (ch == null || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        QuickSlotButton.target(ch);
        float extension = 2 + hero.pointsInTalent(Talent.MNEMONIC_PRAYER);
        this.affectChar(ch, extension);
        Char ally = PowerOfMany.getPoweredAlly();
        if (ally != null && ally.buff(LifeLinkSpell.LifeLinkSpellBuff.class) != null) {
            if (ch == hero) {
                this.affectChar(ally, extension);
            } else if (ch == ally) {
                this.affectChar(hero, extension);
            }
        }
        if (ch == hero) {
            hero.sprite.operate(ch.pos);
            BuffIndicator.refreshHero();
        } else {
            hero.sprite.zap(ch.pos);
            hero.next();
        }
        this.onSpellCast(tome, hero);
    }

    private void affectChar(Char ch, float extension) {
        if (ch.alignment == Char.Alignment.ALLY) {
            Sample.INSTANCE.play("sounds/chargeup.mp3");
            ch.sprite.emitter().start(Speck.factory(4), 0.15f, 4);
            for (Buff b : ch.buffs()) {
                if (b.type != Buff.buffType.POSITIVE || b.mnemonicExtended || b.icon() == 127 || b instanceof AscendedForm.AscendBuff || b instanceof BodyForm.BodyFormBuff || b instanceof SpiritForm.SpiritFormBuff || b instanceof PowerOfMany.PowerBuff || b instanceof BeamingRay.BeamingRayBoost || b instanceof LifeLink || b instanceof LifeLinkSpell.LifeLinkSpellBuff) continue;
                if (b instanceof FlavourBuff) {
                    Buff.affect(ch, b.getClass(), extension);
                } else if (b instanceof AdrenalineSurge) {
                    ((AdrenalineSurge)b).delay(extension);
                } else if (b instanceof ArcaneArmor) {
                    ((ArcaneArmor)b).delay(extension);
                } else if (b instanceof ArtifactRecharge) {
                    ((ArtifactRecharge)b).extend(extension);
                } else if (b instanceof Barkskin) {
                    ((Barkskin)b).delay(extension);
                } else if (b instanceof FireImbue) {
                    ((FireImbue)b).extend(extension);
                } else if (b instanceof GreaterHaste) {
                    ((GreaterHaste)b).extend(extension);
                } else if (b instanceof Healing) {
                    ((Healing)b).increaseHeal((int)extension);
                } else if (b instanceof ToxicImbue) {
                    ((ToxicImbue)b).extend(extension);
                } else if (b instanceof WellFed) {
                    ((WellFed)b).extend(extension);
                } else if (b instanceof ElixirOfAquaticRejuvenation.AquaHealing) {
                    ((ElixirOfAquaticRejuvenation.AquaHealing)b).extend(extension);
                } else if (b instanceof ScrollOfChallenge.ChallengeArena) {
                    ((ScrollOfChallenge.ChallengeArena)b).extend(extension);
                } else if (b instanceof ShieldBuff) {
                    ((ShieldBuff)b).delay(extension);
                } else if (b instanceof Kinetic.ConservedDamage) {
                    ((Kinetic.ConservedDamage)b).delay(extension);
                } else if (b instanceof Sungrass.Health) {
                    ((Sungrass.Health)b).boost((int)extension);
                }
                b.mnemonicExtended = true;
            }
        } else {
            Sample.INSTANCE.play("sounds/debuff.mp3");
            ch.sprite.emitter().start(Speck.factory(15), 0.15f, 4);
            Buff.affect(ch, GuidingLight.Illuminated.class);
            for (Buff b : ch.buffs()) {
                if (b.type != Buff.buffType.NEGATIVE || b.mnemonicExtended) continue;
                if (b instanceof FlavourBuff) {
                    Buff.affect(ch, b.getClass(), extension);
                } else if (b instanceof Bleeding) {
                    ((Bleeding)b).extend(extension);
                } else if (b instanceof Burning) {
                    ((Burning)b).extend(extension);
                } else if (b instanceof Corrosion) {
                    ((Corrosion)b).extend(extension);
                } else if (b instanceof Dread) {
                    ((Dread)b).extend(extension);
                } else if (b instanceof Ooze) {
                    ((Ooze)b).extend(extension);
                } else if (b instanceof Poison) {
                    ((Poison)b).extend(extension);
                } else if (b instanceof Viscosity.DeferedDamage) {
                    ((Viscosity.DeferedDamage)b).extend(extension);
                }
                b.mnemonicExtended = true;
            }
        }
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", 2 + Dungeon.hero.pointsInTalent(Talent.MNEMONIC_PRAYER)) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }
}

