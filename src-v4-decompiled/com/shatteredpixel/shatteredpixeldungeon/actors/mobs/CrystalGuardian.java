/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalSpire;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CrystalGuardianSprite;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class CrystalGuardian
extends Mob {
    private boolean recovering;
    public static final String SPRITE = "sprite";
    public static final String RECOVERING = "recovering";

    public boolean recovering() {
        return this.recovering;
    }

    @Override
    protected boolean act() {
        if (this.recovering) {
            if (this.buff(PinCushion.class) != null) {
                this.buff(PinCushion.class).detach();
            }
            this.processSwarmIntel(false);
            this.throwItems();
            this.HP = Math.min(this.HT, this.HP + 5);
            if (Dungeon.level.heroFOV[this.pos]) {
                this.sprite.showStatusWithIcon(65280, "5", FloatingText.HEALING, new Object[0]);
            }
            if (this.HP == this.HT) {
                this.recovering = false;
                if (this.sprite instanceof CrystalGuardianSprite) {
                    ((CrystalGuardianSprite)this.sprite).endCrumple();
                }
            }
            this.spend(1.0f);
            return true;
        }
        return super.act();
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 16);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.recovering) {
            return 0;
        }
        return super.defenseSkill(enemy);
    }

    @Override
    public boolean surprisedBy(Char enemy, boolean attacking) {
        if (this.recovering) {
            return false;
        }
        return super.surprisedBy(enemy, attacking);
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        if (enemy == Dungeon.hero) {
            boolean spireNear = false;
            for (Mob m : Dungeon.level.mobs.toArray(new Mob[0])) {
                if (!(m instanceof CrystalSpire) || m.HP == m.HT || Dungeon.level.distance(this.pos, m.pos) > 8) continue;
                spireNear = true;
            }
            if (!spireNear) {
                Statistics.questScores[2] = Statistics.questScores[2] - 100;
            }
        }
        return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (this.recovering) {
            this.sprite.showStatusWithIcon(0xFF0000, Integer.toString(damage), FloatingText.PHYS_DMG_NO_BLOCK, new Object[0]);
            this.HP = Math.max(1, this.HP - damage);
            damage = -1;
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public boolean isAlive() {
        if (this.HP <= 0) {
            this.HP = 1;
            for (Buff b : this.buffs()) {
                if (b instanceof Doom || b instanceof Cripple) continue;
                b.detach();
            }
            if (!this.recovering) {
                this.recovering = true;
                Bestiary.setSeen(this.getClass());
                Bestiary.countEncounter(this.getClass());
                if (this.sprite != null) {
                    ((CrystalGuardianSprite)this.sprite).crumple();
                }
            }
        }
        return super.isAlive();
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (this.recovering) {
            return super.isInvulnerable(effect) || Char.class.isAssignableFrom(effect) && !Hero.class.isAssignableFrom(effect) && !CrystalSpire.class.isAssignableFrom(effect);
        }
        return super.isInvulnerable(effect);
    }

    public CrystalGuardian() {
        this.spriteClass = CrystalGuardianSprite.class;
        this.HT = 100;
        this.HP = 100;
        this.defenseSkill = 14;
        this.EXP = 10;
        this.maxLvl = -2;
        this.state = this.SLEEPING = new Sleeping();
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.MINIBOSS);
        this.recovering = false;
        switch (Random.Int(3)) {
            default: {
                this.spriteClass = CrystalGuardianSprite.Blue.class;
                break;
            }
            case 1: {
                this.spriteClass = CrystalGuardianSprite.Green.class;
                break;
            }
            case 2: {
                this.spriteClass = CrystalGuardianSprite.Red.class;
            }
        }
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public float speed() {
        if (!Dungeon.level.openSpace[this.pos]) {
            return Math.max(0.25f, super.speed() / 4.0f);
        }
        return super.speed();
    }

    @Override
    public void move(int step, boolean travelling) {
        super.move(step, travelling);
        if (Dungeon.level.map[this.pos] == 35) {
            Level.set(this.pos, 1);
            GameScene.updateMap(this.pos);
            if (Dungeon.level.heroFOV[this.pos]) {
                Splash.at(this.pos, 0xFFFFFF, 5);
                Sample.INSTANCE.play("sounds/shatter.mp3");
            }
            this.spend(1.0f / super.speed());
        }
    }

    @Override
    public boolean[] modifyPassable(boolean[] passable) {
        if (this.state == this.HUNTING && this.target != -1) {
            PathFinder.buildDistanceMap(this.target, passable);
            if (PathFinder.distance[this.pos] > 2 * Dungeon.level.distance(this.pos, this.target)) {
                for (int i = 0; i < Dungeon.level.length(); ++i) {
                    passable[i] = passable[i] || Dungeon.level.map[i] == 35;
                }
            }
        }
        return passable;
    }

    @Override
    public void beckon(int cell) {
        if (this.state != this.SLEEPING) {
            super.beckon(cell);
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(SPRITE, this.spriteClass);
        bundle.put(RECOVERING, this.recovering);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.spriteClass = bundle.getClass(SPRITE);
        this.recovering = bundle.getBoolean(RECOVERING);
    }

    protected class Sleeping
    extends Mob.Sleeping {
        protected Sleeping() {
            super(CrystalGuardian.this);
        }

        @Override
        protected void awaken(boolean enemyInFOV) {
            if (enemyInFOV) {
                PathFinder.buildDistanceMap(CrystalGuardian.this.enemy.pos, Dungeon.level.passable);
                if (PathFinder.distance[CrystalGuardian.this.pos] == Integer.MAX_VALUE) {
                    return;
                }
            }
            super.awaken(enemyInFOV);
        }
    }
}

