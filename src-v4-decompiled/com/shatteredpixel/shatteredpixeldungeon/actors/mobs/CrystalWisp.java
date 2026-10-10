/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrystalWispSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class CrystalWisp
extends Mob {
    public static final String SPRITE = "sprite";

    public CrystalWisp() {
        this.spriteClass = CrystalWispSprite.class;
        this.HT = 30;
        this.HP = 30;
        this.defenseSkill = 16;
        this.EXP = 7;
        this.maxLvl = -2;
        this.flying = true;
        this.properties.add(Char.Property.INORGANIC);
        switch (Random.Int(3)) {
            default: {
                this.spriteClass = CrystalWispSprite.Blue.class;
                break;
            }
            case 1: {
                this.spriteClass = CrystalWispSprite.Green.class;
                break;
            }
            case 2: {
                this.spriteClass = CrystalWispSprite.Red.class;
            }
        }
    }

    @Override
    public boolean[] modifyPassable(boolean[] passable) {
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            passable[i] = passable[i] || Dungeon.level.map[i] == 35;
        }
        return passable;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(5, 10);
    }

    @Override
    public int attackSkill(Char target) {
        return 18;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 5);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return super.canAttack(enemy) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos == enemy.pos;
    }

    @Override
    protected boolean doAttack(Char enemy) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos) || new Ballistica((int)this.pos, (int)enemy.pos, (int)6).collisionPos != enemy.pos) {
            return super.doAttack(enemy);
        }
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            this.sprite.zap(enemy.pos);
            return false;
        }
        this.zap();
        return true;
    }

    @Override
    public void die(Object cause) {
        this.flying = false;
        super.die(cause);
    }

    private void zap() {
        this.spend(1.0f);
        Invisibility.dispel(this);
        Char enemy = this.enemy;
        if (CrystalWisp.hit(this, enemy, true)) {
            int dmg = Random.NormalIntRange(5, 10);
            enemy.damage(dmg, new LightBeam());
            if (!enemy.isAlive() && enemy == Dungeon.hero) {
                Badges.validateDeathFromEnemyMagic();
                Dungeon.fail(this);
                GLog.n(Messages.get(this, "beam_kill", new Object[0]), new Object[0]);
            }
        } else {
            enemy.sprite.showStatus(0xFFFF00, enemy.defenseVerb(), new Object[0]);
        }
    }

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPRITE, this.spriteClass);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.spriteClass = bundle.getClass(SPRITE);
    }

    public static class LightBeam {
    }
}

