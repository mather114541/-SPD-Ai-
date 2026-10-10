/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.sprites.FungalSentrySprite;
import com.watabou.utils.Random;

public class FungalSentry
extends Mob {
    public FungalSentry() {
        this.spriteClass = FungalSentrySprite.class;
        this.HT = 200;
        this.HP = 200;
        this.defenseSkill = 12;
        this.EXP = 10;
        this.maxLvl = -2;
        this.state = this.WANDERING = new Waiting();
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.MINIBOSS);
        this.immunities.add(ToxicGas.class);
        this.immunities.add(Poison.class);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 10);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        Buff.affect(enemy, Poison.class).extend(6.0f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public int attackSkill(Char target) {
        return 50;
    }

    private class Waiting
    extends Mob.Wandering {
        private Waiting() {
            super(FungalSentry.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                return this.noticeEnemy();
            }
            return this.continueWandering();
        }

        @Override
        protected boolean noticeEnemy() {
            FungalSentry.this.spend(1.0f);
            return super.noticeEnemy();
        }
    }
}

