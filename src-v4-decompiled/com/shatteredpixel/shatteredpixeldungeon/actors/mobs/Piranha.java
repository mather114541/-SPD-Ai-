/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.BlobImmunity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Levitation;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.PhantomPiranha;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.RatSkull;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PiranhaSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Piranha
extends Mob {
    public Piranha() {
        this.spriteClass = PiranhaSprite.class;
        this.baseSpeed = 2.0f;
        this.EXP = 0;
        this.loot = MysteryMeat.class;
        this.lootChance = 1.0f;
        this.SLEEPING = new Sleeping();
        this.WANDERING = new Wandering();
        this.HUNTING = new Hunting();
        this.state = this.SLEEPING;
        for (Class c : new BlobImmunity().immunities()) {
            if (c == Electricity.class || c == Freezing.class) continue;
            this.immunities.add(c);
        }
        this.immunities.add(Burning.class);
        this.HP = this.HT = 10 + Dungeon.depth * 5;
        this.defenseSkill = 10 + Dungeon.depth * 2;
    }

    @Override
    protected boolean act() {
        if (!Dungeon.level.water[this.pos] || this.flying) {
            if (this.sprite != null && this.buff(Levitation.class) != null) {
                this.sprite.emitter().burst(Speck.factory(106), 10);
            }
            this.dieOnLand();
            return true;
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2);
    }

    @Override
    public int attackSkill(Char target) {
        return 20 + Dungeon.depth * 2;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, Dungeon.depth);
    }

    @Override
    public boolean surprisedBy(Char enemy, boolean attacking) {
        if (enemy == Dungeon.hero && (!attacking || ((Hero)enemy).canSurpriseAttack())) {
            if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
                this.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(this, this.fieldOfView);
            }
            return this.state == this.SLEEPING || !this.fieldOfView[enemy.pos] || enemy.invisible > 0;
        }
        return super.surprisedBy(enemy, attacking);
    }

    public void dieOnLand() {
        this.die(null);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        ++Statistics.piranhasKilled;
        Badges.validatePiranhasKilled();
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    protected boolean getCloser(int target) {
        if (this.rooted) {
            return false;
        }
        int step = Dungeon.findStep(this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), this.fieldOfView, true);
        if (step != -1) {
            this.move(step);
            return true;
        }
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        int step = Dungeon.flee(this, target, BArray.and(Dungeon.level.water, Dungeon.level.passable, null), this.fieldOfView, true);
        if (step != -1) {
            this.move(step);
            return true;
        }
        return false;
    }

    public static Piranha random() {
        float altChance = 0.02f * RatSkull.exoticChanceMultiplier();
        if (Random.Float() < altChance) {
            return new PhantomPiranha();
        }
        return new Piranha();
    }

    private class Sleeping
    extends Mob.Sleeping {
        private Sleeping() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(Piranha.this.enemy.pos, Dungeon.level.water, Piranha.this.viewDistance);
                enemyInFOV = PathFinder.distance[Piranha.this.pos] != Integer.MAX_VALUE;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Wandering
    extends Mob.Wandering {
        private Wandering() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(Piranha.this.enemy.pos, Dungeon.level.water, Piranha.this.viewDistance);
                enemyInFOV = PathFinder.distance[Piranha.this.pos] != Integer.MAX_VALUE;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(Piranha.this.enemy.pos, Dungeon.level.water, Piranha.this.viewDistance);
                enemyInFOV = PathFinder.distance[Piranha.this.pos] != Integer.MAX_VALUE;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}

