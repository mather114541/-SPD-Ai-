/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfSirensSong;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RatSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.HashSet;

public class Ratmogrify
extends ArmorAbility {
    public static boolean useRatroicEnergy = false;

    public Ratmogrify() {
        this.baseChargeUse = 50.0f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        Char ch = Actor.findChar(target);
        if (ch == null || !Dungeon.level.heroFOV[target]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        if (ch == hero) {
            if (!hero.hasTalent(Talent.RATFORCEMENTS)) {
                GLog.w(Messages.get(this, "self_target", new Object[0]), new Object[0]);
                return;
            }
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p]) continue;
                spawnPoints.add(p);
            }
            for (int ratsToSpawn = hero.pointsInTalent(Talent.RATFORCEMENTS); ratsToSpawn > 0 && spawnPoints.size() > 0; --ratsToSpawn) {
                int index = Random.index(spawnPoints);
                Rat rat = new Rat();
                rat.alignment = Char.Alignment.ALLY;
                rat.state = rat.HUNTING;
                Buff.affect(rat, AscensionChallenge.AscensionBuffBlocker.class);
                GameScene.add(rat);
                ScrollOfTeleportation.appear(rat, (Integer)spawnPoints.get(index));
                spawnPoints.remove(index);
            }
        } else {
            if (ch.alignment != Char.Alignment.ENEMY || !(ch instanceof Mob) || ch instanceof Rat) {
                GLog.w(Messages.get(this, "cant_transform", new Object[0]), new Object[0]);
                return;
            }
            if (ch instanceof TransmogRat) {
                if (((TransmogRat)ch).allied || !hero.hasTalent(Talent.RATLOMACY)) {
                    GLog.w(Messages.get(this, "cant_transform", new Object[0]), new Object[0]);
                    return;
                }
                ((TransmogRat)ch).makeAlly();
                ch.sprite.emitter().start(Speck.factory(11), 0.2f, 5);
                Sample.INSTANCE.play("sounds/teleport.mp3");
                if (hero.pointsInTalent(Talent.RATLOMACY) > 1) {
                    Buff.affect(ch, Adrenaline.class, 2 * (hero.pointsInTalent(Talent.RATLOMACY) - 1));
                }
            } else {
                if (Char.hasProp(ch, Char.Property.MINIBOSS) || Char.hasProp(ch, Char.Property.BOSS)) {
                    GLog.w(Messages.get(this, "too_strong", new Object[0]), new Object[0]);
                    return;
                }
                TransmogRat rat = new TransmogRat();
                rat.setup((Mob)ch);
                rat.pos = ch.pos;
                HashSet<Buff> persistentBuffs = new HashSet<Buff>();
                for (Buff b : ch.buffs()) {
                    if (!b.revivePersists) continue;
                    persistentBuffs.add(b);
                }
                Actor.remove(ch);
                ch.sprite.killAndErase();
                Dungeon.level.mobs.remove(ch);
                for (Buff b : persistentBuffs) {
                    ch.add(b);
                }
                GameScene.add(rat);
                TargetHealthIndicator.instance.target(null);
                CellEmitter.get(rat.pos).burst(Speck.factory(7), 4);
                Sample.INSTANCE.play("sounds/puff.mp3");
                if (!rat.isAlive()) {
                    rat.die(this);
                } else {
                    Dungeon.level.occupyCell(rat);
                }
            }
        }
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Invisibility.dispel();
        hero.spendAndNext(1.0f);
    }

    @Override
    public int icon() {
        return 34;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.RATSISTANCE, Talent.RATLOMACY, Talent.RATFORCEMENTS, Talent.HEROIC_ENERGY};
    }

    public static class TransmogRat
    extends Mob {
        private Mob original;
        private boolean allied;
        private float timeLeft;
        private static final String ORIGINAL = "original";
        private static final String ALLIED = "allied";

        public TransmogRat() {
            this.spriteClass = RatSprite.class;
            this.firstAdded = false;
            this.timeLeft = 6.0f;
            this.immunities.add(AllyBuff.class);
        }

        public void setup(Mob original) {
            this.original = original;
            original.clearTime();
            this.HP = original.HP;
            this.HT = original.HT;
            this.defenseSkill = original.defenseSkill;
            this.EXP = original.EXP;
            this.maxLvl = original.maxLvl;
            this.state = original.state == original.SLEEPING ? this.SLEEPING : (original.state == original.HUNTING ? this.HUNTING : this.WANDERING);
        }

        public Mob getOriginal() {
            if (this.original != null) {
                this.original.HP = this.HP;
                this.original.pos = this.pos;
            }
            return this.original;
        }

        @Override
        protected boolean act() {
            if (this.timeLeft <= 0.0f) {
                Mob original = this.getOriginal();
                this.original = null;
                GameScene.add(original);
                this.EXP = 0;
                this.destroy();
                this.sprite.killAndErase();
                CellEmitter.get(original.pos).burst(Speck.factory(7), 4);
                Sample.INSTANCE.play("sounds/puff.mp3");
                return true;
            }
            return super.act();
        }

        @Override
        protected void spend(float time) {
            if (!this.allied) {
                this.timeLeft -= time;
            }
            super.spend(time);
        }

        public void makeAlly() {
            this.allied = true;
            this.alignment = Char.Alignment.ALLY;
            this.timeLeft = Float.POSITIVE_INFINITY;
            if (this.original != null) {
                this.original.pos = this.pos;
                AllyBuff.affectAndLoot(this.original, Dungeon.hero, ScrollOfSirensSong.Enthralled.class);
            }
        }

        @Override
        public int attackSkill(Char target) {
            return this.original.attackSkill(target);
        }

        @Override
        public int drRoll() {
            return this.original.drRoll();
        }

        @Override
        public int damageRoll() {
            int damage = this.original.damageRoll();
            if (!this.allied && Dungeon.hero.hasTalent(Talent.RATSISTANCE)) {
                damage = (int)((double)damage * Math.pow(0.9f, Dungeon.hero.pointsInTalent(Talent.RATSISTANCE)));
            }
            return damage;
        }

        @Override
        public float attackDelay() {
            return this.original.attackDelay();
        }

        @Override
        public void rollToDropLoot() {
            this.original.pos = this.pos;
            this.original.rollToDropLoot();
        }

        @Override
        public void destroy() {
            super.destroy();
            if (this.alignment == Char.Alignment.ENEMY && this.original != null) {
                Bestiary.setSeen(this.original.getClass());
                Bestiary.countEncounter(this.original.getClass());
            }
            if (this.original instanceof Statue) {
                Notes.remove(this.original.landmark());
            }
        }

        @Override
        public String name() {
            return Messages.get(this, "name", this.original.name());
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(ORIGINAL, this.original);
            bundle.put(ALLIED, this.allied);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.original = (Mob)bundle.get(ORIGINAL);
            this.defenseSkill = this.original.defenseSkill;
            this.EXP = this.original.EXP;
            this.allied = bundle.getBoolean(ALLIED);
            if (this.allied) {
                this.alignment = Char.Alignment.ALLY;
            }
        }
    }
}

