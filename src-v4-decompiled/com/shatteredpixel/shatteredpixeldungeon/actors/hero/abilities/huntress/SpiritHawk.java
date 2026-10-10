/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShaftParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MobSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SpiritHawk
extends ArmorAbility {
    public SpiritHawk() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    public String targetingPrompt() {
        if (SpiritHawk.getHawk() == null) {
            return super.targetingPrompt();
        }
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public boolean useTargeting() {
        return false;
    }

    @Override
    public float chargeUse(Hero hero) {
        if (SpiritHawk.getHawk() == null) {
            return super.chargeUse(hero);
        }
        return 0.0f;
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        HawkAlly ally = SpiritHawk.getHawk();
        if (ally != null) {
            if (target == null) {
                return;
            }
            ally.directTocell(target);
        } else {
            ArrayList<Integer> spawnPoints = new ArrayList<Integer>();
            for (int i = 0; i < PathFinder.NEIGHBOURS8.length; ++i) {
                int p = hero.pos + PathFinder.NEIGHBOURS8[i];
                if (Actor.findChar(p) != null || !Dungeon.level.passable[p] && !Dungeon.level.avoid[p]) continue;
                spawnPoints.add(p);
            }
            if (!spawnPoints.isEmpty()) {
                armor.charge -= this.chargeUse(hero);
                armor.updateQuickslot();
                ally = new HawkAlly();
                ally.pos = (Integer)Random.element(spawnPoints);
                GameScene.add(ally);
                ScrollOfTeleportation.appear(ally, ally.pos);
                Dungeon.observe();
                Invisibility.dispel();
                hero.spendAndNext(1.0f);
            } else {
                GLog.w(Messages.get(this, "no_space", new Object[0]), new Object[0]);
            }
        }
    }

    @Override
    public int icon() {
        return 27;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.EAGLE_EYE, Talent.GO_FOR_THE_EYES, Talent.SWIFT_SPIRIT, Talent.HEROIC_ENERGY};
    }

    private static HawkAlly getHawk() {
        for (Char ch : Actor.chars()) {
            if (!(ch instanceof HawkAlly)) continue;
            return (HawkAlly)ch;
        }
        return null;
    }

    public static class HawkAlly
    extends DirectableAlly {
        private int dodgesUsed;
        private float timeRemaining;
        private static final String DODGES_USED = "dodges_used";
        private static final String TIME_REMAINING = "time_remaining";

        public HawkAlly() {
            this.spriteClass = HawkSprite.class;
            this.HT = 10;
            this.HP = 10;
            this.defenseSkill = 60;
            this.flying = true;
            if (Dungeon.hero != null) {
                this.viewDistance = (int)GameMath.gate(6.0f, 6 + Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE), 8.0f);
                this.baseSpeed = 2.0f + (float)Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT) / 2.0f;
            } else {
                this.viewDistance = 6;
                this.baseSpeed = 2.0f;
            }
            this.attacksAutomatically = false;
            this.immunities.addAll(new BlobImmunity().immunities());
            this.immunities.add(AllyBuff.class);
            this.dodgesUsed = 0;
            this.timeRemaining = 100.0f;
        }

        @Override
        public int attackSkill(Char target) {
            return 60;
        }

        @Override
        public int defenseSkill(Char enemy) {
            if (Dungeon.hero.hasTalent(Talent.SWIFT_SPIRIT) && this.dodgesUsed < 2 * Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT)) {
                ++this.dodgesUsed;
                return Char.INFINITE_EVASION;
            }
            return super.defenseSkill(enemy);
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(5, 10);
        }

        @Override
        public int attackProc(Char enemy, int damage) {
            damage = super.attackProc(enemy, damage);
            switch (Dungeon.hero.pointsInTalent(Talent.GO_FOR_THE_EYES)) {
                case 1: {
                    Buff.prolong(enemy, Blindness.class, 2.0f);
                    break;
                }
                case 2: {
                    Buff.prolong(enemy, Blindness.class, 5.0f);
                    break;
                }
                case 3: {
                    Buff.prolong(enemy, Blindness.class, 5.0f);
                    Buff.prolong(enemy, Cripple.class, 2.0f);
                    break;
                }
                case 4: {
                    Buff.prolong(enemy, Blindness.class, 5.0f);
                    Buff.prolong(enemy, Cripple.class, 5.0f);
                    break;
                }
            }
            return damage;
        }

        @Override
        protected boolean act() {
            if (this.timeRemaining <= 0.0f) {
                this.die(null);
                Dungeon.hero.interrupt();
                return true;
            }
            this.viewDistance = 6 + Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE);
            this.baseSpeed = 2.0f + (float)Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT) / 2.0f;
            boolean result = super.act();
            Dungeon.level.updateFieldOfView(this, this.fieldOfView);
            GameScene.updateFog(this.pos, this.viewDistance + (int)Math.ceil(this.speed()));
            return result;
        }

        @Override
        public void die(Object cause) {
            this.flying = false;
            super.die(cause);
        }

        @Override
        protected void spend(float time) {
            super.spend(time);
            this.timeRemaining -= time;
        }

        @Override
        public void destroy() {
            super.destroy();
            Dungeon.observe();
            GameScene.updateFog();
        }

        @Override
        public void defendPos(int cell) {
            GLog.i(Messages.get(this, "direct_defend", new Object[0]), new Object[0]);
            super.defendPos(cell);
        }

        @Override
        public void followHero() {
            GLog.i(Messages.get(this, "direct_follow", new Object[0]), new Object[0]);
            super.followHero();
        }

        @Override
        public void targetChar(Char ch) {
            GLog.i(Messages.get(this, "direct_attack", new Object[0]), new Object[0]);
            super.targetChar(ch);
        }

        @Override
        public String description() {
            Object message = Messages.get(this, "desc", (int)this.timeRemaining);
            if (Actor.chars().contains(this)) {
                message = (String)message + "\n\n" + Messages.get(this, "desc_remaining", (int)this.timeRemaining);
                if (this.dodgesUsed < 2 * Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT)) {
                    message = (String)message + "\n" + Messages.get(this, "desc_dodges", 2 * Dungeon.hero.pointsInTalent(Talent.SWIFT_SPIRIT) - this.dodgesUsed);
                }
            }
            return message;
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DODGES_USED, this.dodgesUsed);
            bundle.put(TIME_REMAINING, this.timeRemaining);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.dodgesUsed = bundle.getInt(DODGES_USED);
            this.timeRemaining = bundle.getFloat(TIME_REMAINING);
        }
    }

    public static class HawkSprite
    extends MobSprite {
        public HawkSprite() {
            this.texture("sprites/spirit_hawk.png");
            TextureFilm frames = new TextureFilm(this.texture, 15, 15);
            boolean c = false;
            this.idle = new MovieClip.Animation(6, true);
            this.idle.frames(frames, 0, 1);
            this.run = new MovieClip.Animation(8, true);
            this.run.frames(frames, 0, 1);
            this.attack = new MovieClip.Animation(12, false);
            this.attack.frames(frames, 2, 3, 0, 1);
            this.die = new MovieClip.Animation(12, false);
            this.die.frames(frames, 4, 5, 6);
            this.play(this.idle);
        }

        @Override
        public int blood() {
            return -16711681;
        }

        @Override
        public void die() {
            super.die();
            this.emitter().start(ShaftParticle.FACTORY, 0.3f, 4);
            this.emitter().start(Speck.factory(2), 0.2f, 3);
        }
    }
}

