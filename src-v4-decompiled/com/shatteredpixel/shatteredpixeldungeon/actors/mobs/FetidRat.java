/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StenchGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Rat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FetidRatSprite;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class FetidRat
extends Rat {
    public FetidRat() {
        this.spriteClass = FetidRatSprite.class;
        this.HT = 20;
        this.HP = 20;
        this.defenseSkill = 5;
        this.EXP = 4;
        this.state = this.WANDERING = new Wandering();
        this.properties.add(Char.Property.MINIBOSS);
        this.properties.add(Char.Property.DEMONIC);
        this.immunities.add(StenchGas.class);
    }

    @Override
    public int attackSkill(Char target) {
        return 12;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 2);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(3) == 0) {
            Buff.affect(enemy, Ooze.class).set(20.0f);
            if (enemy == Dungeon.hero && !Dungeon.level.water[enemy.pos]) {
                Statistics.questScores[0] = Statistics.questScores[0] - 50;
            }
        }
        return damage;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        GameScene.add(Blob.seed(this.pos, 20, StenchGas.class));
        return super.defenseProc(enemy, damage);
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Ghost.Quest.process();
    }

    protected class Wandering
    extends Mob.Wandering {
        protected Wandering() {
            super(FetidRat.this);
        }

        @Override
        protected int randomDestination() {
            int pos1 = super.randomDestination();
            int pos2 = super.randomDestination();
            PathFinder.buildDistanceMap(Dungeon.hero.pos, Dungeon.level.passable);
            if (PathFinder.distance[pos2] < PathFinder.distance[pos1]) {
                return pos2;
            }
            return pos1;
        }
    }
}

