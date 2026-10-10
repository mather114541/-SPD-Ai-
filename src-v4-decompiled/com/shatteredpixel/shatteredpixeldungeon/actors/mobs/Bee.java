/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BeeSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Bee
extends Mob {
    private int level;
    private int potPos;
    private int potHolder;
    private static final String LEVEL = "level";
    private static final String POTPOS = "potpos";
    private static final String POTHOLDER = "potholder";
    private static final String ALIGMNENT = "alignment";

    public Bee() {
        this.spriteClass = BeeSprite.class;
        this.viewDistance = 4;
        this.EXP = 0;
        this.flying = true;
        this.state = this.WANDERING;
        this.intelligentAlly = true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LEVEL, this.level);
        bundle.put(POTPOS, this.potPos);
        bundle.put(POTHOLDER, this.potHolder);
        bundle.put(ALIGMNENT, this.alignment);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.spawn(bundle.getInt(LEVEL));
        this.potPos = bundle.getInt(POTPOS);
        this.potHolder = bundle.getInt(POTHOLDER);
        if (bundle.contains(ALIGMNENT)) {
            this.alignment = bundle.getEnum(ALIGMNENT, Char.Alignment.class);
        }
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    public void spawn(int level) {
        this.level = level;
        this.HT = (2 + level) * 4;
        this.defenseSkill = 9 + level;
    }

    public void setPotInfo(int potPos, Char potHolder) {
        this.potPos = potPos;
        this.potHolder = potHolder == null ? -1 : potHolder.id();
    }

    public int potPos() {
        return this.potPos;
    }

    public int potHolderID() {
        return this.potHolder;
    }

    @Override
    public int attackSkill(Char target) {
        return this.defenseSkill;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(this.HT / 10, this.HT / 4);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (enemy instanceof Mob) {
            ((Mob)enemy).aggro(this);
        }
        return damage;
    }

    @Override
    public boolean add(Buff buff) {
        if (super.add(buff)) {
            if (buff instanceof AllyBuff) {
                this.intelligentAlly = false;
                this.setPotInfo(-1, null);
            }
            return true;
        }
        return false;
    }

    @Override
    protected Char chooseEnemy() {
        if (this.alignment == Char.Alignment.ALLY || this.potHolder == -1 && this.potPos == -1) {
            return super.chooseEnemy();
        }
        if (Actor.findById(this.potHolder) != null) {
            return (Char)Actor.findById(this.potHolder);
        }
        if ((this.alignment == Char.Alignment.ENEMY || this.buff(Amok.class) != null) && this.state != this.PASSIVE && this.state != this.SLEEPING) {
            if (this.enemy != null && this.enemy.buff(StoneOfAggression.Aggression.class) != null && Dungeon.level.distance(this.enemy.pos, this.potPos) <= 3) {
                this.state = this.HUNTING;
                return this.enemy;
            }
            for (Char ch : Actor.chars()) {
                if (ch == this || !this.fieldOfView[ch.pos] || Dungeon.level.distance(ch.pos, this.potPos) > 3 || ch.buff(StoneOfAggression.Aggression.class) == null) continue;
                this.state = this.HUNTING;
                return ch;
            }
        }
        if (this.enemy == null || !this.enemy.isAlive() || !Actor.chars().contains(this.enemy) || this.state == this.WANDERING || Dungeon.level.distance(this.enemy.pos, this.potPos) > 3 || this.alignment == Char.Alignment.ALLY && this.enemy.alignment == Char.Alignment.ALLY || this.buff(Amok.class) == null && this.enemy.isInvulnerable(this.getClass())) {
            Mob closest = null;
            for (Mob mob : Dungeon.level.mobs) {
                if (mob == this || Dungeon.level.distance(mob.pos, this.potPos) > 3 || mob.alignment == Char.Alignment.NEUTRAL || mob.isInvulnerable(this.getClass()) || this.alignment == Char.Alignment.ALLY && mob.alignment == Char.Alignment.ALLY || closest != null && Dungeon.level.distance(closest.pos, this.pos) <= Dungeon.level.distance(mob.pos, this.pos)) continue;
                closest = mob;
            }
            if (closest != null) {
                return closest;
            }
            if (this.alignment != Char.Alignment.ALLY && Dungeon.level.distance(Dungeon.hero.pos, this.potPos) <= 3) {
                return Dungeon.hero;
            }
            return null;
        }
        return this.enemy;
    }

    @Override
    protected boolean getCloser(int target) {
        if (this.alignment == Char.Alignment.ALLY && this.enemy == null && this.buffs(AllyBuff.class).isEmpty()) {
            target = Dungeon.hero.pos;
        } else if (this.enemy != null && Actor.findById(this.potHolder) == this.enemy) {
            target = this.enemy.pos;
        } else if (this.potPos != -1 && (this.state == this.WANDERING || Dungeon.level.distance(target, this.potPos) > 3)) {
            if (!Dungeon.level.insideMap(this.potPos)) {
                this.potPos = -1;
            } else {
                this.target = target = this.potPos;
            }
        }
        return super.getCloser(target);
    }

    @Override
    public String description() {
        if (this.alignment == Char.Alignment.ALLY && this.buffs(AllyBuff.class).isEmpty()) {
            return Messages.get(this, "desc_honey", new Object[0]);
        }
        return super.description();
    }
}

