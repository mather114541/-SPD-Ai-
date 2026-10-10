/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RotLasherSprite;
import com.watabou.utils.Random;

public class RotLasher
extends Mob {
    public RotLasher() {
        this.spriteClass = RotLasherSprite.class;
        this.HT = 80;
        this.HP = 80;
        this.defenseSkill = 0;
        this.EXP = 1;
        this.loot = Generator.Category.SEED;
        this.lootChance = 0.75f;
        this.state = this.WANDERING = new Waiting();
        this.viewDistance = 1;
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.MINIBOSS);
        this.immunities.add(ToxicGas.class);
    }

    @Override
    protected boolean act() {
        if (!(this.HP >= this.HT || this.enemy != null && Dungeon.level.adjacent(this.pos, this.enemy.pos))) {
            this.sprite.showStatusWithIcon(65280, Integer.toString(Math.min(5, this.HT - this.HP)), FloatingText.HEALING, new Object[0]);
            this.HP = Math.min(this.HT, this.HP + 5);
        }
        return super.act();
    }

    @Override
    public void damage(int dmg, Object src) {
        if (src instanceof Burning) {
            this.destroy();
            this.sprite.die();
        } else {
            super.damage(dmg, src);
        }
    }

    @Override
    public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        if (enemy == Dungeon.hero) {
            Statistics.questScores[1] = Statistics.questScores[1] - 100;
        }
        return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        Buff.affect(enemy, Cripple.class, 2.0f);
        return super.attackProc(enemy, damage);
    }

    @Override
    public boolean reset() {
        return true;
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
        return Random.NormalIntRange(10, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 25;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 8);
    }

    private class Waiting
    extends Mob.Wandering {
        private Waiting() {
        }

        @Override
        protected boolean noticeEnemy() {
            RotLasher.this.spend(1.0f);
            return super.noticeEnemy();
        }
    }
}

