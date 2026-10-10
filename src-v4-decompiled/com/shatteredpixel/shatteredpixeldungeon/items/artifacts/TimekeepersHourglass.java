/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.artifacts;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfEnergy;
import com.shatteredpixel.shatteredpixeldungeon.journal.Catalog;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Rotberry;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Iterator;

public class TimekeepersHourglass
extends Artifact {
    public static final String AC_ACTIVATE = "ACTIVATE";
    public int sandBags;
    private static final String SANDBAGS = "sandbags";
    private static final String BUFF = "buff";

    public TimekeepersHourglass() {
        this.image = ItemSpriteSheet.ARTIFACT_HOURGLASS;
        this.levelCap = 5;
        this.charge = 5 + this.level();
        this.partialCharge = 0.0f;
        this.chargeCap = 5 + this.level();
        this.defaultAction = AC_ACTIVATE;
        this.sandBags = 0;
    }

    @Override
    public void resetForTrinity(int visibleLevel) {
        super.resetForTrinity(visibleLevel);
        this.charge = visibleLevel / 2 - 1;
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        if (this.isEquipped(hero) && !this.cursed && hero.buff(MagicImmune.class) == null && (this.charge > 0 || this.activeBuff != null)) {
            actions.add(AC_ACTIVATE);
        }
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (hero.buff(MagicImmune.class) != null) {
            return;
        }
        if (action.equals(AC_ACTIVATE)) {
            if (!this.isEquipped(hero)) {
                GLog.i(Messages.get(Artifact.class, "need_to_equip", new Object[0]), new Object[0]);
            } else if (this.activeBuff != null) {
                if (!(this.activeBuff instanceof timeStasis)) {
                    this.activeBuff.detach();
                    GLog.i(Messages.get(this, "deactivate", new Object[0]), new Object[0]);
                }
            } else if (this.charge <= 0) {
                GLog.i(Messages.get(this, "no_charge", new Object[0]), new Object[0]);
            } else if (this.cursed) {
                GLog.i(Messages.get(this, "cursed", new Object[0]), new Object[0]);
            } else {
                GameScene.show(new WndOptions(new ItemSprite(this), Messages.titleCase(this.name()), Messages.get(this, "prompt", new Object[0]), new String[]{Messages.get(this, "stasis", new Object[0]), Messages.get(this, "freeze", new Object[0])}){

                    @Override
                    protected void onSelect(int index) {
                        if (index == 0) {
                            GLog.i(Messages.get(TimekeepersHourglass.class, "onstasis", new Object[0]), new Object[0]);
                            GameScene.flash(-2130706433);
                            Sample.INSTANCE.play("sounds/teleport.mp3");
                            TimekeepersHourglass.this.activeBuff = new timeStasis();
                            Talent.onArtifactUsed(Dungeon.hero);
                            TimekeepersHourglass.this.activeBuff.attachTo(Dungeon.hero);
                        } else if (index == 1) {
                            for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                                if (!Dungeon.level.heroFOV[mob.pos]) continue;
                                Artifact.artifactProc(mob, TimekeepersHourglass.this.visiblyUpgraded(), 1);
                            }
                            GLog.i(Messages.get(TimekeepersHourglass.class, "onfreeze", new Object[0]), new Object[0]);
                            GameScene.flash(-2130706433);
                            Sample.INSTANCE.play("sounds/teleport.mp3");
                            Invisibility.dispel(Dungeon.hero);
                            TimekeepersHourglass.this.activeBuff = new timeFreeze();
                            Talent.onArtifactUsed(Dungeon.hero);
                            TimekeepersHourglass.this.activeBuff.attachTo(Dungeon.hero);
                            --TimekeepersHourglass.this.charge;
                            ((timeFreeze)TimekeepersHourglass.this.activeBuff).processTime(0.0f);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void activate(Char ch) {
        super.activate(ch);
        if (this.activeBuff != null) {
            this.activeBuff.attachTo(ch);
        }
    }

    @Override
    public boolean doUnequip(Hero hero, boolean collect, boolean single) {
        if (super.doUnequip(hero, collect, single)) {
            if (this.activeBuff != null) {
                this.activeBuff.detach();
                this.activeBuff = null;
            }
            return true;
        }
        return false;
    }

    @Override
    protected Artifact.ArtifactBuff passiveBuff() {
        return new hourglassRecharge();
    }

    @Override
    public void charge(Hero target, float amount) {
        if (this.charge < this.chargeCap && !this.cursed && target.buff(MagicImmune.class) == null) {
            this.partialCharge += 0.25f * amount;
            while (this.partialCharge >= 1.0f) {
                this.partialCharge -= 1.0f;
                ++this.charge;
            }
            if (this.charge >= this.chargeCap) {
                this.partialCharge = 0.0f;
            }
            TimekeepersHourglass.updateQuickslot();
        }
    }

    @Override
    public Item upgrade() {
        ++this.chargeCap;
        while (this.level() + 1 > this.sandBags) {
            ++this.sandBags;
        }
        return super.upgrade();
    }

    @Override
    public String desc() {
        Object desc = super.desc();
        if (this.isEquipped(Dungeon.hero)) {
            if (!this.cursed) {
                if (this.level() < this.levelCap) {
                    desc = (String)desc + "\n\n" + Messages.get(this, "desc_hint", new Object[0]);
                }
            } else {
                desc = (String)desc + "\n\n" + Messages.get(this, "desc_cursed", new Object[0]);
            }
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SANDBAGS, this.sandBags);
        if (this.activeBuff != null) {
            bundle.put(BUFF, this.activeBuff);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.sandBags = bundle.getInt(SANDBAGS);
        if (bundle.contains(BUFF)) {
            Bundle buffBundle = bundle.getBundle(BUFF);
            this.activeBuff = buffBundle.contains("presses") ? new timeFreeze() : new timeStasis();
            this.activeBuff.restoreFromBundle(buffBundle);
        }
    }

    public class timeStasis
    extends Artifact.ArtifactBuff {
        public timeStasis() {
            this.type = Buff.buffType.POSITIVE;
            this.actPriority = -33;
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                Invisibility.dispel();
                int usedCharge = Math.min(TimekeepersHourglass.this.charge, 2);
                this.spend(5 * usedCharge);
                Hunger hunger = Buff.affect(target, Hunger.class);
                if (hunger != null && !hunger.isStarving()) {
                    hunger.satisfy(5 * usedCharge);
                }
                TimekeepersHourglass.this.charge -= usedCharge;
                ++target.invisible;
                ++target.paralysed;
                target.next();
                Item.updateQuickslot();
                if (Dungeon.hero != null) {
                    Dungeon.observe();
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean act() {
            this.detach();
            return true;
        }

        @Override
        public void detach() {
            if (this.target.invisible > 0) {
                --this.target.invisible;
            }
            if (this.target.paralysed > 0) {
                --this.target.paralysed;
            }
            super.detach();
            TimekeepersHourglass.this.activeBuff = null;
            Dungeon.observe();
        }

        @Override
        public void fx(boolean on) {
            if (on) {
                this.target.sprite.add(CharSprite.State.PARALYSED);
            } else {
                if (this.target.paralysed == 0) {
                    this.target.sprite.remove(CharSprite.State.PARALYSED);
                }
                if (this.target.invisible == 0) {
                    this.target.sprite.remove(CharSprite.State.INVISIBLE);
                }
            }
        }
    }

    public class hourglassRecharge
    extends Artifact.ArtifactBuff {
        @Override
        public boolean act() {
            if (TimekeepersHourglass.this.charge < TimekeepersHourglass.this.chargeCap && !TimekeepersHourglass.this.cursed && this.target.buff(MagicImmune.class) == null && Regeneration.regenOn()) {
                float chargeGain = 1.0f / (90.0f - (float)(TimekeepersHourglass.this.chargeCap - TimekeepersHourglass.this.charge) * 3.0f);
                TimekeepersHourglass.this.partialCharge += (chargeGain *= RingOfEnergy.artifactChargeMultiplier(this.target));
                while (TimekeepersHourglass.this.partialCharge >= 1.0f) {
                    TimekeepersHourglass.this.partialCharge -= 1.0f;
                    ++TimekeepersHourglass.this.charge;
                    if (TimekeepersHourglass.this.charge != TimekeepersHourglass.this.chargeCap) continue;
                    TimekeepersHourglass.this.partialCharge = 0.0f;
                }
            } else if (TimekeepersHourglass.this.cursed && Random.Int(10) == 0) {
                ((Hero)this.target).spend(1.0f);
            }
            Item.updateQuickslot();
            this.spend(1.0f);
            return true;
        }
    }

    public class timeFreeze
    extends Artifact.ArtifactBuff {
        float turnsToCost;
        ArrayList<Integer> presses;
        private static final String PRESSES = "presses";
        private static final String TURNSTOCOST = "turnsToCost";

        public timeFreeze() {
            this.type = Buff.buffType.POSITIVE;
            this.turnsToCost = 2.0f;
            this.presses = new ArrayList();
        }

        public void processTime(float time) {
            this.turnsToCost -= time;
            while (this.turnsToCost < -0.001f) {
                this.turnsToCost += 2.0f;
                --TimekeepersHourglass.this.charge;
            }
            Item.updateQuickslot();
            if (TimekeepersHourglass.this.charge < 0 || TimekeepersHourglass.this.charge == 0 && this.turnsToCost <= 0.0f) {
                TimekeepersHourglass.this.charge = 0;
                this.detach();
            }
        }

        public void setDelayedPress(int cell) {
            if (!this.presses.contains(cell)) {
                this.presses.add(cell);
            }
        }

        public void triggerPresses() {
            final ArrayList<Integer> toTrigger = this.presses;
            this.presses = new ArrayList();
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    Iterator iterator = toTrigger.iterator();
                    while (iterator.hasNext()) {
                        Trap t;
                        int cell = (Integer)iterator.next();
                        Plant p = (Plant)Dungeon.level.plants.get(cell);
                        if (p != null) {
                            p.trigger();
                        }
                        if ((t = (Trap)Dungeon.level.traps.get(cell)) == null) continue;
                        t.trigger();
                    }
                    Actor.remove(this);
                    return true;
                }
            });
        }

        public void disarmPresses() {
            for (int cell : this.presses) {
                Trap t;
                Plant p = (Plant)Dungeon.level.plants.get(cell);
                if (p != null && !(p instanceof Rotberry)) {
                    Dungeon.level.uproot(cell);
                }
                if ((t = (Trap)Dungeon.level.traps.get(cell)) == null || !t.disarmedByActivation) continue;
                t.disarm();
            }
            this.presses = new ArrayList();
        }

        @Override
        public void detach() {
            Item.updateQuickslot();
            super.detach();
            TimekeepersHourglass.this.activeBuff = null;
            this.triggerPresses();
            this.target.next();
        }

        @Override
        public void fx(boolean on) {
            if (!(this.target instanceof Hero)) {
                return;
            }
            Emitter.freezeEmitters = on;
            if (on) {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob.sprite == null) continue;
                    mob.sprite.add(CharSprite.State.PARALYSED);
                }
            } else {
                for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
                    if (mob.paralysed > 0) continue;
                    mob.sprite.remove(CharSprite.State.PARALYSED);
                }
            }
        }

        @Override
        public int icon() {
            return 7;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.5f, 0.0f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (2.0f - this.turnsToCost) / 2.0f);
        }

        @Override
        public String iconTextDisplay() {
            return Integer.toString((int)(this.turnsToCost + 0.001f));
        }

        @Override
        public String desc() {
            return Messages.get(this, "desc", Messages.decimalFormat("#.##", Math.max(0.0f, this.turnsToCost)));
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            int[] values = new int[this.presses.size()];
            for (int i = 0; i < values.length; ++i) {
                values[i] = this.presses.get(i);
            }
            bundle.put(PRESSES, values);
            bundle.put(TURNSTOCOST, this.turnsToCost);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            int[] values;
            super.restoreFromBundle(bundle);
            for (int value : values = bundle.getIntArray(PRESSES)) {
                this.presses.add(value);
            }
            this.turnsToCost = bundle.getFloat(TURNSTOCOST);
        }
    }

    public static class sandBag
    extends Item {
        public sandBag() {
            this.image = ItemSpriteSheet.SANDBAG;
        }

        @Override
        public boolean doPickUp(Hero hero, int pos) {
            Catalog.setSeen(this.getClass());
            Statistics.itemTypesDiscovered.add(this.getClass());
            TimekeepersHourglass hourglass = hero.belongings.getItem(TimekeepersHourglass.class);
            if (hourglass != null && !hourglass.cursed) {
                hourglass.upgrade();
                Catalog.countUses(hourglass.getClass(), 2);
                Sample.INSTANCE.play("sounds/dewdrop.mp3");
                if (hourglass.level() == hourglass.levelCap) {
                    GLog.p(Messages.get(this, "maxlevel", new Object[0]), new Object[0]);
                } else {
                    GLog.i(Messages.get(this, "levelup", new Object[0]), new Object[0]);
                }
                GameScene.pickUp(this, pos);
                hero.spendAndNext(this.pickupDelay());
                return true;
            }
            GLog.w(Messages.get(this, "no_hourglass", new Object[0]), new Object[0]);
            return false;
        }

        @Override
        public int value() {
            return 30;
        }

        @Override
        public boolean isUpgradable() {
            return false;
        }

        @Override
        public boolean isIdentified() {
            return true;
        }
    }
}

