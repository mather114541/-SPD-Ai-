/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Shopkeeper;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MasterThievesArmband
extends Artifact {
    public static final String AC_STEAL = "STEAL";
    public CellSelector.Listener targeter;

    public MasterThievesArmband() {
        this.image = ItemSpriteSheet.ARTIFACT_ARMBAND;
        this.levelCap = 10;
        this.charge = 0;
        this.partialCharge = 0.0f;
        this.chargeCap = 5 + this.level() / 2;
        this.defaultAction = AC_STEAL;
        this.targeter = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer target) {
                if (target == null) {
                    return;
                }
                if (!Dungeon.level.adjacent(curUser.pos, target) || Actor.findChar(target) == null) {
                    GLog.w(Messages.get(MasterThievesArmband.class, "no_target", new Object[0]), new Object[0]);
                } else {
                    final Char ch = Actor.findChar(target);
                    if (ch instanceof Shopkeeper) {
                        GLog.w(Messages.get(MasterThievesArmband.class, "steal_shopkeeper", new Object[0]), new Object[0]);
                    } else if (!(ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL)) {
                        GLog.w(Messages.get(MasterThievesArmband.class, "no_target", new Object[0]), new Object[0]);
                    } else if (ch instanceof Mob) {
                        curUser.busy();
                        curUser.sprite.attack(target, new Callback(){

                            @Override
                            public void call() {
                                Sample.INSTANCE.play("sounds/hit.mp3");
                                boolean surprised = ((Mob)ch).surprisedBy(curUser, false);
                                float lootMultiplier = 1.0f + 0.1f * (float)MasterThievesArmband.this.level();
                                int debuffDuration = 3 + MasterThievesArmband.this.level() / 2;
                                Invisibility.dispel(curUser);
                                if (surprised) {
                                    lootMultiplier += 0.5f;
                                    Surprise.hit(ch);
                                    Sample.INSTANCE.play("sounds/hit_strong.mp3");
                                    debuffDuration += 2;
                                    MasterThievesArmband.this.exp += 2;
                                }
                                float lootChance = ((Mob)ch).lootChance() * lootMultiplier;
                                if (Dungeon.hero.lvl > ((Mob)ch).maxLvl + 2) {
                                    lootChance = 0.0f;
                                } else if (ch.buff(StolenTracker.class) != null) {
                                    lootChance = 0.0f;
                                }
                                if (lootChance == 0.0f) {
                                    GLog.w(Messages.get(MasterThievesArmband.class, "no_steal", new Object[0]), new Object[0]);
                                } else if (Random.Float() <= lootChance) {
                                    Item loot = ((Mob)ch).createLoot();
                                    if (Challenges.isItemBlocked(loot)) {
                                        GLog.i(Messages.get(MasterThievesArmband.class, "failed_steal", new Object[0]), new Object[0]);
                                        Buff.affect(ch, StolenTracker.class).setItemStolen(false);
                                    } else {
                                        if (loot.doPickUp(curUser)) {
                                            curUser.spend(-loot.pickupDelay());
                                        } else {
                                            Dungeon.level.drop((Item)loot, (int)curUser.pos).sprite.drop();
                                        }
                                        GLog.i(Messages.get(MasterThievesArmband.class, "stole_item", loot.name()), new Object[0]);
                                        Buff.affect(ch, StolenTracker.class).setItemStolen(true);
                                    }
                                } else {
                                    GLog.i(Messages.get(MasterThievesArmband.class, "failed_steal", new Object[0]), new Object[0]);
                                    Buff.affect(ch, StolenTracker.class).setItemStolen(false);
                                }
                                Buff.prolong(ch, Blindness.class, debuffDuration);
                                Buff.prolong(ch, Cripple.class, debuffDuration);
                                Artifact.artifactProc(ch, MasterThievesArmband.this.visiblyUpgraded(), 1);
                                --MasterThievesArmband.this.charge;
                                MasterThievesArmband.this.exp += 3;
                                Talent.onArtifactUsed(Dungeon.hero);
                                while (MasterThievesArmband.this.exp >= 10 + Math.round(3.33f * (float)MasterThievesArmband.this.level()) && MasterThievesArmband.this.level() < MasterThievesArmband.this.levelCap) {
                                    MasterThievesArmband.this.exp -= 10 + Math.round(3.33f * (float)MasterThievesArmband.this.level());
                                    Catalog.countUse(MasterThievesArmband.class);
                                    GLog.p(Messages.get(MasterThievesArmband.class, "level_up", new Object[0]), new Object[0]);
                                    MasterThievesArmband.this.upgrade();
                                }
                                Item.updateQuickslot();
                                curUser.next();
                            }
                        });
                    }
                }
            }

            @Override
            public String prompt() {
                return Messages.get(MasterThievesArmband.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && this.charge > 0 && hero.buff(MagicImmune.class) == null && !this.cursed) {
            actions.add(AC_STEAL);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_STEAL)) {
            curUser = hero;
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else if (this.charge < 1) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else if (this.cursed) {
                GLog.w(Messages.get(this, "cursed", new Object[0]), new Object[0]);
                this.usesTargeting = false;
            } else {
                this.usesTargeting = true;
                GameScene.selectCell(this.targeter);
            }
        }
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new Thievery();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.cursed || target.buff(MagicImmune.class) != null) {
            return;
        }
        if (this.charge < this.chargeCap) {
            this.partialCharge += 0.1f * amount;
            while (this.partialCharge >= 1.0f) {
                ++this.charge;
                this.partialCharge -= 1.0f;
            }
            if (this.charge >= this.chargeCap) {
                GLog.p(Messages.get(MasterThievesArmband.class, "full", new Object[0]), new Object[0]);
                this.partialCharge = 0.0f;
                this.charge = this.chargeCap;
            }
            MasterThievesArmband.updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        this.chargeCap = 5 + (this.level() + 1) / 2;
        return super.upgrade();
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            desc = this.cursed ? (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "desc_worn", new Object[0]);
        }
        return desc;
    }

    public class Thievery
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (MasterThievesArmband.this.cursed && Dungeon.gold > 0 && Random.Int(5) == 0) {
                --Dungeon.gold;
                Item.updateQuickslot();
            }
            this.spend(1.0f);
            return true;
        }

        public void gainCharge(float levelPortion) {
            if (MasterThievesArmband.this.cursed || this.target.buff(MagicImmune.class) != null) {
                return;
            }
            if (MasterThievesArmband.this.charge < MasterThievesArmband.this.chargeCap) {
                float chargeGain = (3.0f + 0.15f * (float)MasterThievesArmband.this.level()) * levelPortion;
                MasterThievesArmband.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (MasterThievesArmband.this.partialCharge > 1.0f) {
                    MasterThievesArmband.this.partialCharge -= 1.0f;
                    ++MasterThievesArmband.this.charge;
                    Item.updateQuickslot();
                    if (MasterThievesArmband.this.charge != MasterThievesArmband.this.chargeCap) continue;
                    GLog.p(Messages.get(MasterThievesArmband.class, "full", new Object[0]), new Object[0]);
                    MasterThievesArmband.this.partialCharge = 0.0f;
                }
            } else {
                MasterThievesArmband.this.partialCharge = 0.0f;
            }
        }

        public boolean steal(Item item) {
            int chargesUsed = this.chargesToUse(item);
            float stealChance = this.stealChance(item);
            if (Random.Float() > stealChance) {
                return false;
            }
            MasterThievesArmband.this.charge -= chargesUsed;
            MasterThievesArmband.this.exp += 4 * chargesUsed;
            GLog.i(Messages.get(MasterThievesArmband.class, "stole_item", item.name()), new Object[0]);
            Talent.onArtifactUsed(Dungeon.hero);
            while (MasterThievesArmband.this.exp >= 10 + Math.round(3.33f * (float)MasterThievesArmband.this.level()) && MasterThievesArmband.this.level() < MasterThievesArmband.this.levelCap) {
                MasterThievesArmband.this.exp -= 10 + Math.round(3.33f * (float)MasterThievesArmband.this.level());
                Catalog.countUse(MasterThievesArmband.class);
                GLog.p(Messages.get(MasterThievesArmband.class, "level_up", new Object[0]), new Object[0]);
                MasterThievesArmband.this.upgrade();
            }
            Item.updateQuickslot();
            return true;
        }

        public float stealChance(Item item) {
            int chargesUsed = this.chargesToUse(item);
            float val = (float)chargesUsed * (10.0f + (float)MasterThievesArmband.this.level() / 2.0f);
            return Math.min(1.0f, val / (float)item.value());
        }

        public int chargesToUse(Item item) {
            int chargesUsed;
            int value = item.value();
            float valUsing = 0.0f;
            for (chargesUsed = 0; valUsing < (float)value && chargesUsed < MasterThievesArmband.this.charge; valUsing += 10.0f + (float)MasterThievesArmband.this.level() / 2.0f, ++chargesUsed) {
            }
            return chargesUsed;
        }
    }

    public static class StolenTracker
    extends CounterBuff {
        public StolenTracker() {
            this.revivePersists = true;
        }

        public void setItemStolen(boolean stolen) {
            if (stolen) {
                this.countUp(1.0f);
            }
        }

        public boolean itemWasStolen() {
            return this.count() > 0.0f;
        }
    }
}

