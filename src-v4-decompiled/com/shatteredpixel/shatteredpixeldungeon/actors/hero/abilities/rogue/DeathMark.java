/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Image;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;

public class DeathMark
extends ArmorAbility {
    public DeathMark() {
        this.baseChargeUse = 25.0f;
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
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(DoubleMarkTracker.class) != null) {
            chargeUse = (float)((double)chargeUse * Math.pow(0.707, hero.pointsInTalent(Talent.DOUBLE_MARK)));
        }
        return chargeUse;
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
        if (ch.alignment != Char.Alignment.ENEMY) {
            GLog.w(Messages.get(this, "ally_target", new Object[0]), new Object[0]);
            return;
        }
        if (ch != null) {
            Buff.affect(ch, DeathMarkTracker.class, DeathMarkTracker.DURATION).setInitialHP(ch.HP);
        }
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        hero.sprite.zap(target);
        hero.next();
        if (hero.buff(DoubleMarkTracker.class) != null) {
            hero.buff(DoubleMarkTracker.class).detach();
        } else if (hero.hasTalent(Talent.DOUBLE_MARK)) {
            Buff.affect(hero, DoubleMarkTracker.class, 0.01f);
        }
    }

    public static void processFearTheReaper(Char ch) {
        if (ch.HP > 0 || ch.buff(DeathMarkTracker.class) == null) {
            return;
        }
        if (Dungeon.hero.hasTalent(Talent.FEAR_THE_REAPER)) {
            if (Dungeon.hero.pointsInTalent(Talent.FEAR_THE_REAPER) >= 2) {
                Buff.prolong((Char)ch, Terror.class, (float)5.0f).object = Dungeon.hero.id();
            }
            Buff.prolong(ch, Cripple.class, 5.0f);
            if (Dungeon.hero.pointsInTalent(Talent.FEAR_THE_REAPER) >= 3) {
                boolean[] passable = BArray.not(Dungeon.level.solid, null);
                PathFinder.buildDistanceMap(ch.pos, passable, 3);
                for (Char near : Actor.chars()) {
                    if (near == ch || near.alignment != Char.Alignment.ENEMY || PathFinder.distance[near.pos] == Integer.MAX_VALUE) continue;
                    if (Dungeon.hero.pointsInTalent(Talent.FEAR_THE_REAPER) == 4) {
                        Buff.prolong((Char)near, Terror.class, (float)5.0f).object = Dungeon.hero.id();
                    }
                    Buff.prolong(near, Cripple.class, 5.0f);
                }
            }
        }
    }

    @Override
    public int icon() {
        return 23;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.FEAR_THE_REAPER, Talent.DEATHLY_DURABILITY, Talent.DOUBLE_MARK, Talent.HEROIC_ENERGY};
    }

    public static class DoubleMarkTracker
    extends FlavourBuff {
    }

    public static class DeathMarkTracker
    extends FlavourBuff {
        public static float DURATION = 5.0f;
        int initialHP = 0;
        private static String INITIAL_HP = "initial_hp";

        public DeathMarkTracker() {
            this.type = Buff.buffType.NEGATIVE;
            this.announced = true;
        }

        @Override
        public int icon() {
            return 57;
        }

        @Override
        public void tintIcon(Image icon) {
            icon.hardlight(1.0f, 0.2f, 0.2f);
        }

        @Override
        public float iconFadePercent() {
            return Math.max(0.0f, (DURATION - this.visualcooldown()) / DURATION);
        }

        private void setInitialHP(int hp) {
            if (this.initialHP < hp) {
                this.initialHP = hp;
            }
        }

        @Override
        public boolean attachTo(Char target) {
            if (super.attachTo(target)) {
                target.deathMarked = true;
                return true;
            }
            return false;
        }

        @Override
        public void detach() {
            super.detach();
            this.target.deathMarked = false;
            if (!this.target.isAlive()) {
                this.target.sprite.flash();
                this.target.sprite.bloodBurstA(this.target.sprite.center(), this.target.HT * 2);
                Sample.INSTANCE.play("sounds/hit_stab.mp3");
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                this.target.die(this);
                int shld = Math.round((float)this.initialHP * (0.125f * (float)Dungeon.hero.pointsInTalent(Talent.DEATHLY_DURABILITY)));
                if (shld > 0 && this.target.alignment != Char.Alignment.ALLY) {
                    Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(shld), FloatingText.SHIELDING, new Object[0]);
                    Buff.affect(Dungeon.hero, Barrier.class).setShield(shld);
                }
            }
        }

        public void detachOnDeath() {
            super.detach();
            this.target.deathMarked = false;
            if (!this.target.isAlive()) {
                this.target.sprite.flash();
                this.target.sprite.bloodBurstA(this.target.sprite.center(), this.target.HT * 2);
                Sample.INSTANCE.play("sounds/hit_stab.mp3");
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
                int shld = Math.round((float)this.initialHP * (0.125f * (float)Dungeon.hero.pointsInTalent(Talent.DEATHLY_DURABILITY)));
                if (shld > 0 && this.target.alignment != Char.Alignment.ALLY) {
                    Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(shld), FloatingText.SHIELDING, new Object[0]);
                    Buff.affect(Dungeon.hero, Barrier.class).setShield(shld);
                }
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(INITIAL_HP, this.initialHP);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.initialHP = bundle.getInt(INITIAL_HP);
        }
    }
}

