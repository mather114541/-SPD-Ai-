/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollSapper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Spear;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGuardSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GnollGuard
extends Mob {
    private int sapperID;
    private static final String SAPPER_ID = "sapper_id";

    public GnollGuard() {
        this.spriteClass = GnollGuardSprite.class;
        this.HT = 35;
        this.HP = 35;
        this.defenseSkill = 15;
        this.EXP = 7;
        this.maxLvl = -2;
        this.loot = Spear.class;
        this.lootChance = 0.1f;
        this.WANDERING = new Wandering();
        this.sapperID = -1;
    }

    public void linkSapper(GnollSapper sapper) {
        this.sapperID = sapper.id();
        if (this.sprite instanceof GnollGuardSprite) {
            ((GnollGuardSprite)this.sprite).setupArmor();
        }
    }

    public boolean hasSapper() {
        return this.sapperID != -1 && Actor.findById(this.sapperID) instanceof GnollSapper && ((GnollSapper)Actor.findById(this.sapperID)).isAlive();
    }

    public void loseSapper() {
        if (this.sapperID != -1) {
            this.sapperID = -1;
            if (this.sprite instanceof GnollGuardSprite) {
                ((GnollGuardSprite)this.sprite).loseArmor();
            }
        }
    }

    @Override
    public void damage(int dmg, Object src) {
        if (this.hasSapper()) {
            dmg /= 4;
        }
        super.damage(dmg, src);
    }

    @Override
    public int damageRoll() {
        if (this.enemy != null && !Dungeon.level.adjacent(this.pos, this.enemy.pos)) {
            return Random.NormalIntRange(16, 22);
        }
        return Random.NormalIntRange(6, 12);
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        int dmg = super.attackProc(enemy, damage);
        if (enemy == Dungeon.hero && !Dungeon.level.adjacent(this.pos, enemy.pos) && dmg > 12) {
            GLog.n(Messages.get(this, "spear_warn", new Object[0]), new Object[0]);
        }
        return dmg;
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 6);
    }

    @Override
    protected boolean canAttack(Char enemy) {
        return Dungeon.level.distance(enemy.pos, this.pos) <= 2 && new Ballistica((int)this.pos, (int)enemy.pos, (int)7).collisionPos == enemy.pos && new Ballistica((int)enemy.pos, (int)this.pos, (int)7).collisionPos == this.pos;
    }

    @Override
    public String description() {
        if (this.hasSapper()) {
            return super.description() + "\n\n" + Messages.get(this, "desc_armor", new Object[0]);
        }
        return super.description();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SAPPER_ID, this.sapperID);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.sapperID = bundle.getInt(SAPPER_ID);
    }

    public class Wandering
    extends Mob.Wandering {
        public Wandering() {
            super(GnollGuard.this);
        }

        @Override
        protected int randomDestination() {
            if (GnollGuard.this.hasSapper()) {
                return ((GnollSapper)Actor.findById((int)GnollGuard.this.sapperID)).pos;
            }
            return super.randomDestination();
        }
    }
}

