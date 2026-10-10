/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.AlchemistsToolkit;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.EtherealChains;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HornOfPlenty;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SandalsOfNature;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.SkeletonKey;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.UnstableSpellbook;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfMight;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.AlchemyScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SpiritForm
extends ClericSpell {
    public static SpiritForm INSTANCE = new SpiritForm();

    @Override
    public int icon() {
        return 63;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", SpiritForm.ringLevel(), SpiritForm.artifactLevel()) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public float chargeUse(Hero hero) {
        return 4.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.SPIRIT_FORM);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        GameScene.show(new Trinity.WndItemtypeSelect(tome, this));
    }

    public static int ringLevel() {
        return Dungeon.hero.pointsInTalent(Talent.SPIRIT_FORM);
    }

    public static int artifactLevel() {
        return 2 + 2 * Dungeon.hero.pointsInTalent(Talent.SPIRIT_FORM);
    }

    public static void applyActiveArtifactEffect(ClassArmor armor, Artifact effect) {
        if (effect instanceof AlchemistsToolkit) {
            Talent.onArtifactUsed(Dungeon.hero);
            AlchemyScene.assignToolkit((AlchemistsToolkit)effect);
            Game.switchScene(AlchemyScene.class);
        } else if (effect instanceof DriedRose) {
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || Dungeon.level.solid[p]) continue;
                spawnPoints.add(p);
            }
            if (!spawnPoints.isEmpty()) {
                Wraith w = Wraith.spawnAt((Integer)Random.element(spawnPoints), Wraith.class);
                w.HP = w.HT = 20 + 8 * SpiritForm.artifactLevel();
                Buff.affect(w, Corruption.class);
            }
            Talent.onArtifactUsed(Dungeon.hero);
            Dungeon.hero.spendAndNext(1.0f);
        } else if (effect instanceof EtherealChains) {
            GameScene.selectCell(((EtherealChains)effect).caster);
            if (Dungeon.quickslot.contains(armor)) {
                QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
            }
        } else if (effect instanceof HornOfPlenty) {
            ((HornOfPlenty)effect).doEatEffect(Dungeon.hero, 1);
        } else if (effect instanceof MasterThievesArmband) {
            GameScene.selectCell(((MasterThievesArmband)effect).targeter);
            if (Dungeon.quickslot.contains(armor)) {
                QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
            }
        } else if (effect instanceof SandalsOfNature) {
            ((SandalsOfNature)effect).curSeedEffect = Random.oneOf(Blindweed.Seed.class, Fadeleaf.Seed.class, Firebloom.Seed.class, Icecap.Seed.class, Sorrowmoss.Seed.class, Stormvine.Seed.class);
            GameScene.selectCell(((SandalsOfNature)effect).cellSelector);
            if (Dungeon.quickslot.contains(armor)) {
                QuickSlotButton.useTargeting(Dungeon.quickslot.getSlot(armor));
            }
        } else if (effect instanceof TalismanOfForesight) {
            GameScene.selectCell(((TalismanOfForesight)effect).scry);
        } else if (effect instanceof TimekeepersHourglass) {
            Buff.affect(Dungeon.hero, Swiftthistle.TimeBubble.class).reset(SpiritForm.artifactLevel());
            Dungeon.hero.spendAndNext(1.0f);
        } else if (effect instanceof UnstableSpellbook) {
            ((UnstableSpellbook)effect).doReadEffect(Dungeon.hero);
        } else if (effect instanceof SkeletonKey) {
            GameScene.selectCell(((SkeletonKey)effect).targeter);
        }
    }

    public static class SpiritFormBuff
    extends FlavourBuff {
        public static final float DURATION = 20.0f;
        private Bundlable effect;
        private static final String EFFECT = "effect";

        public SpiritFormBuff() {
            this.type = Buff.buffType.POSITIVE;
        }

        @Override
        public int icon() {
            return 82;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(0.0f, 1.0f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (20.0f - this.visualcooldown()) / 20.0f);
        }

        public void setEffect(Bundlable effect) {
            this.effect = effect;
            if (effect instanceof RingOfMight) {
                ((Ring)effect).level(SpiritForm.ringLevel());
                Dungeon.hero.updateHT(false);
            }
        }

        @Override
        public void detach() {
            super.detach();
            if (this.effect instanceof RingOfMight) {
                Dungeon.hero.updateHT(false);
            }
        }

        public Ring ring() {
            if (this.effect instanceof Ring) {
                ((Ring)this.effect).level(SpiritForm.ringLevel());
                return (Ring)this.effect;
            }
            return null;
        }

        public Artifact artifact() {
            if (this.effect instanceof Artifact) {
                if (((Artifact)this.effect).visiblyUpgraded() < SpiritForm.artifactLevel()) {
                    ((Artifact)this.effect).transferUpgrade(SpiritForm.artifactLevel() - ((Artifact)this.effect).visiblyUpgraded());
                }
                return (Artifact)this.effect;
            }
            return null;
        }

        @Override
        public String desc() {
            if (this.ring() != null) {
                return Messages.get(this, "desc", Messages.titleCase(this.ring().name()), this.dispTurns());
            }
            if (this.artifact() != null) {
                return Messages.get(this, "desc", Messages.titleCase(this.artifact().name()), this.dispTurns());
            }
            return super.desc();
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(EFFECT, this.effect);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.effect = bundle.get(EFFECT);
        }
    }
}

