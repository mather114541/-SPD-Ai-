/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.VelvetPouch;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Earthroot;
import com.shatteredpixel.shatteredpixeldungeon.plants.Fadeleaf;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Mageroyal;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Starflower;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sungrass;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashMap;

public class SandalsOfNature
extends Artifact {
    public static final String AC_FEED = "FEED";
    public static final String AC_ROOT = "ROOT";
    public ArrayList<Class> seeds;
    public Class curSeedEffect;
    private static final HashMap<Class<? extends Plant.Seed>, Integer> seedColors = new HashMap();
    private static final HashMap<Class<? extends Plant.Seed>, Integer> seedChargeReqs;
    private static final String SEEDS = "seeds";
    private static final String CUR_SEED_EFFECT = "cur_seed_effect";
    protected WndBag.ItemSelector itemSelector;
    public CellSelector.Listener cellSelector;

    public SandalsOfNature() {
        this.image = ItemSpriteSheet.ARTIFACT_SANDALS;
        this.levelCap = 3;
        this.charge = 0;
        this.chargeCap = 100;
        this.defaultAction = AC_ROOT;
        this.seeds = new ArrayList();
        this.curSeedEffect = null;
        this.itemSelector = new WndBag.ItemSelector(){

            @Override
            public String textPrompt() {
                return Messages.get(SandalsOfNature.class, "prompt", new Object[0]);
            }

            @Override
            public Class<? extends Bag> preferredBag() {
                return VelvetPouch.class;
            }

            @Override
            public boolean itemSelectable(Item item) {
                return SandalsOfNature.this.canUseSeed(item);
            }

            @Override
            public void onSelect(Item item) {
                if (item != null && item instanceof Plant.Seed) {
                    if (SandalsOfNature.this.level() < 3) {
                        SandalsOfNature.this.seeds.add(0, item.getClass());
                    }
                    SandalsOfNature.this.curSeedEffect = item.getClass();
                    Hero hero = Dungeon.hero;
                    hero.sprite.operate(hero.pos);
                    Sample.INSTANCE.play("sounds/plant.mp3");
                    hero.busy();
                    hero.spend(1.0f);
                    if (SandalsOfNature.this.seeds.size() >= 3 + SandalsOfNature.this.level() * 3) {
                        SandalsOfNature.this.seeds.clear();
                        SandalsOfNature.this.upgrade();
                        Catalog.countUses(SandalsOfNature.class, SandalsOfNature.this.level() == 3 ? 4 : 3);
                        if (SandalsOfNature.this.level() >= 1 && SandalsOfNature.this.level() <= 3) {
                            GLog.p(Messages.get(SandalsOfNature.class, "levelup", new Object[0]), new Object[0]);
                        }
                    } else {
                        GLog.i(Messages.get(SandalsOfNature.class, "absorb_seed", new Object[0]), new Object[0]);
                    }
                    item.detach(hero.belongings.backpack);
                }
            }
        };
        this.cellSelector = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer cell) {
                if (cell != null) {
                    if (!Dungeon.level.heroFOV[cell] || Dungeon.level.distance(curUser.pos, cell) > 3) {
                        GLog.w(Messages.get(SandalsOfNature.class, "out_of_range", new Object[0]), new Object[0]);
                    } else {
                        Ballistica aim = new Ballistica(curUser.pos, cell, 1);
                        for (int c : aim.subPath(0, aim.dist)) {
                            CellEmitter.get(c).burst(LeafParticle.GENERAL, 6);
                        }
                        Splash.at(DungeonTilemap.tileCenterToWorld(cell), -1.5707963f, 1.5707963f, seedColors.get(SandalsOfNature.this.curSeedEffect), 6);
                        Invisibility.dispel(curUser);
                        Plant plant = ((Plant.Seed)Reflection.newInstance(SandalsOfNature.this.curSeedEffect)).couch(cell, null);
                        plant.activate(Actor.findChar(cell));
                        Sample.INSTANCE.play("sounds/plant.mp3");
                        Sample.INSTANCE.playDelayed("sounds/trample.mp3", 0.25f, 1.0f, Random.Float(0.96f, 1.05f));
                        if (Actor.findChar(cell) != null) {
                            Artifact.artifactProc(Actor.findChar(cell), SandalsOfNature.this.visiblyUpgraded(), seedChargeReqs.get(SandalsOfNature.this.curSeedEffect));
                        }
                        SandalsOfNature.this.charge -= seedChargeReqs.get(SandalsOfNature.this.curSeedEffect).intValue();
                        Talent.onArtifactUsed(Dungeon.hero);
                        Item.updateQuickslot();
                        curUser.spendAndNext(1.0f);
                    }
                }
            }

            @Override
            public String prompt() {
                return Messages.get(SandalsOfNature.class, "prompt_target", new Object[0]);
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (hero.buff(MagicImmune.class) != null) {
            return actions;
        }
        if (this.isEquipped(hero) && !this.cursed) {
            actions.add(AC_FEED);
        }
        if (this.isEquipped(hero) && !this.cursed && this.curSeedEffect != null && this.charge >= seedChargeReqs.get(this.curSeedEffect)) {
            actions.add(AC_ROOT);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_FEED)) {
            GameScene.selectItem(this.itemSelector);
        } else if (action.equals(AC_ROOT) && !this.cursed) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.curSeedEffect == null) {
                GLog.i(Messages.get(this, "no_effect", new Object[0]), new Object[0]);
            } else if (this.charge < seedChargeReqs.get(this.curSeedEffect)) {
                GLog.i(Messages.get(this, "low_charge", new Object[0]), new Object[0]);
            } else {
                GameScene.selectCell(this.cellSelector);
            }
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new Naturalism();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.charge < this.chargeCap) {
            this.partialCharge += 2.0f * amount;
            while (this.partialCharge >= 1.0f) {
                ++this.charge;
                this.partialCharge -= 1.0f;
            }
            if (this.charge >= this.chargeCap) {
                this.charge = this.chargeCap;
                this.partialCharge = 0.0f;
            }
            SandalsOfNature.updateQuickslot();
        }
    }

    @Override
    public ItemSprite.Glowing glowing() {
        if (this.curSeedEffect != null) {
            return new ItemSprite.Glowing(seedColors.get(this.curSeedEffect));
        }
        return null;
    }

    @Override
    public String name() {
        if (this.level() == 0) {
            return super.name();
        }
        return Messages.get(this, "name_" + this.level(), new Object[0]);
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc_" + (this.level() + 1), new Object[0]);
        if (this.isEquipped(Dungeon.hero)) {
            desc = (String)desc + "\n\n";
            desc = !this.cursed ? (String)desc + Messages.get(this, "desc_hint", new Object[0]) : (String)desc + Messages.get(this, "desc_cursed", new Object[0]);
        }
        if (this.curSeedEffect != null) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_ability", Messages.titleCase(Messages.get(this.curSeedEffect, "name", new Object[0])), seedChargeReqs.get(this.curSeedEffect));
        }
        if (!this.seeds.isEmpty()) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_seeds", this.seeds.size());
        }
        return desc;
    }

    @Override
    public Item upgrade() {
        if (this.level() < 0) {
            this.image = ItemSpriteSheet.ARTIFACT_SANDALS;
        } else if (this.level() == 0) {
            this.image = ItemSpriteSheet.ARTIFACT_SHOES;
        } else if (this.level() == 1) {
            this.image = ItemSpriteSheet.ARTIFACT_BOOTS;
        } else if (this.level() >= 2) {
            this.image = ItemSpriteSheet.ARTIFACT_GREAVES;
        }
        return super.upgrade();
    }

    public boolean canUseSeed(Item item) {
        return item instanceof Plant.Seed && !this.seeds.contains(item.getClass()) && (this.level() < 3 || this.curSeedEffect != item.getClass());
    }

    @Override
    public void resetForTrinity(int visibleLevel) {
        super.reset();
        this.curSeedEffect = null;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SEEDS, this.seeds.toArray(new Class[0]));
        bundle.put(CUR_SEED_EFFECT, this.curSeedEffect);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.seeds.clear();
        if (bundle.contains(SEEDS) && bundle.getClassArray(SEEDS) != null) {
            for (Class seed : bundle.getClassArray(SEEDS)) {
                if (seed == null) continue;
                this.seeds.add(seed);
            }
        }
        this.curSeedEffect = bundle.getClass(CUR_SEED_EFFECT);
        if (this.level() == 1) {
            this.image = ItemSpriteSheet.ARTIFACT_SHOES;
        } else if (this.level() == 2) {
            this.image = ItemSpriteSheet.ARTIFACT_BOOTS;
        } else if (this.level() >= 3) {
            this.image = ItemSpriteSheet.ARTIFACT_GREAVES;
        }
    }

    static {
        seedColors.put(Rotberry.Seed.class, 0xCC0022);
        seedColors.put(Firebloom.Seed.class, 0xFF7F00);
        seedColors.put(Swiftthistle.Seed.class, 0xCCBB00);
        seedColors.put(Sungrass.Seed.class, 0x2EE62E);
        seedColors.put(Icecap.Seed.class, 6730751);
        seedColors.put(Stormvine.Seed.class, 1662336);
        seedColors.put(Sorrowmoss.Seed.class, 10575077);
        seedColors.put(Mageroyal.Seed.class, 16731346);
        seedColors.put(Earthroot.Seed.class, 6772797);
        seedColors.put(Starflower.Seed.class, 0x404040);
        seedColors.put(Fadeleaf.Seed.class, 0x919999);
        seedColors.put(Blindweed.Seed.class, 0xD9D9D9);
        seedChargeReqs = new HashMap();
        seedChargeReqs.put(Rotberry.Seed.class, 8);
        seedChargeReqs.put(Firebloom.Seed.class, 20);
        seedChargeReqs.put(Swiftthistle.Seed.class, 20);
        seedChargeReqs.put(Sungrass.Seed.class, 80);
        seedChargeReqs.put(Icecap.Seed.class, 20);
        seedChargeReqs.put(Stormvine.Seed.class, 20);
        seedChargeReqs.put(Sorrowmoss.Seed.class, 20);
        seedChargeReqs.put(Mageroyal.Seed.class, 12);
        seedChargeReqs.put(Earthroot.Seed.class, 40);
        seedChargeReqs.put(Starflower.Seed.class, 40);
        seedChargeReqs.put(Fadeleaf.Seed.class, 12);
        seedChargeReqs.put(Blindweed.Seed.class, 12);
    }

    public class Naturalism
    extends Artifact.ArtifactBuff {
        public void charge() {
            if (SandalsOfNature.this.cursed || this.target.buff(MagicImmune.class) != null) {
                return;
            }
            if (SandalsOfNature.this.charge < SandalsOfNature.this.chargeCap) {
                float chargeGain = (3.0f + (float)(2 * SandalsOfNature.this.level())) / 6.0f;
                SandalsOfNature.this.partialCharge += Math.max(0.0f, chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (SandalsOfNature.this.partialCharge >= 1.0f) {
                    ++SandalsOfNature.this.charge;
                    SandalsOfNature.this.partialCharge -= 1.0f;
                }
                SandalsOfNature.this.charge = Math.min(SandalsOfNature.this.charge, SandalsOfNature.this.chargeCap);
                Item.updateQuickslot();
            }
        }
    }
}

