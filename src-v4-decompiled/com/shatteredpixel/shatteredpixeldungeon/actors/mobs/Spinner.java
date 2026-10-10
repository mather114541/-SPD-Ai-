/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.food.MysteryMeat;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.SpinnerSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Spinner
extends Mob {
    private int webCoolDown;
    private int lastEnemyPos;
    private static final String WEB_COOLDOWN = "web_cooldown";
    private static final String LAST_ENEMY_POS = "last_enemy_pos";
    private boolean shotWebVisually;

    public Spinner() {
        this.spriteClass = SpinnerSprite.class;
        this.HT = 50;
        this.HP = 50;
        this.defenseSkill = 17;
        this.EXP = 9;
        this.maxLvl = 17;
        this.loot = MysteryMeat.class;
        this.lootChance = 0.125f;
        this.HUNTING = new Hunting();
        this.FLEEING = new Fleeing();
        this.webCoolDown = 0;
        this.lastEnemyPos = -1;
        this.shotWebVisually = false;
        this.resistances.add(Poison.class);
        this.immunities.add(Web.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 20);
    }

    @Override
    public int attackSkill(Char target) {
        return 22;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 6);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(WEB_COOLDOWN, this.webCoolDown);
        bundle.put(LAST_ENEMY_POS, this.lastEnemyPos);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.webCoolDown = bundle.getInt(WEB_COOLDOWN);
        this.lastEnemyPos = bundle.getInt(LAST_ENEMY_POS);
    }

    @Override
    protected boolean act() {
        if (this.state == this.HUNTING || this.state == this.FLEEING) {
            --this.webCoolDown;
        }
        Mob.AiState lastState = this.state;
        boolean result = super.act();
        if (lastState != this.WANDERING || this.state != this.HUNTING) {
            if (!this.shotWebVisually) {
                this.lastEnemyPos = this.enemy != null && this.enemySeen ? this.enemy.pos : Dungeon.hero.pos;
            }
            this.shotWebVisually = false;
        }
        return result;
    }

    @Override
    public int attackProc(Char enemy, int damage) {
        damage = super.attackProc(enemy, damage);
        if (Random.Int(2) == 0) {
            int duration = Random.IntRange(7, 8);
            duration = Math.round((float)duration * (AscensionChallenge.statModifier(this) / 2.0f + 0.5f));
            Buff.affect(enemy, Poison.class).set(duration);
            this.webCoolDown = 0;
            this.state = this.FLEEING;
        }
        return damage;
    }

    public int webPos() {
        Char enemy = this.enemy;
        if (enemy == null) {
            return -1;
        }
        if (this.state != this.FLEEING && enemy.pos == this.lastEnemyPos && this.canAttack(enemy)) {
            return -1;
        }
        Ballistica b = this.lastEnemyPos == enemy.pos ? new Ballistica(enemy.pos, this.pos, 0) : new Ballistica(this.lastEnemyPos, enemy.pos, 0);
        int collisionIndex = 0;
        for (int i = 0; i < b.path.size(); ++i) {
            if (b.path.get(i) != enemy.pos) continue;
            collisionIndex = i;
            break;
        }
        if (b.path.size() <= collisionIndex + 1) {
            return -1;
        }
        int webPos = b.path.get(collisionIndex + 1);
        int projectilePos = new Ballistica((int)this.pos, (int)webPos, (int)5).collisionPos;
        if (webPos != enemy.pos && projectilePos == webPos && Dungeon.level.passable[webPos]) {
            return webPos;
        }
        return -1;
    }

    public void shootWeb() {
        int webPos = this.webPos();
        if (webPos != -1) {
            int i;
            for (i = 0; i < PathFinder.CIRCLE8.length && this.enemy.pos + PathFinder.CIRCLE8[i] != webPos; ++i) {
            }
            int leftPos = this.enemy.pos + PathFinder.CIRCLE8[this.left(i)];
            int rightPos = this.enemy.pos + PathFinder.CIRCLE8[this.right(i)];
            if (Dungeon.level.passable[leftPos]) {
                this.applyWebToCell(leftPos);
            }
            if (Dungeon.level.passable[webPos]) {
                this.applyWebToCell(webPos);
            }
            if (Dungeon.level.passable[rightPos]) {
                this.applyWebToCell(rightPos);
            }
            this.webCoolDown = 10;
            if (Dungeon.level.heroFOV[this.enemy.pos]) {
                Dungeon.hero.interrupt();
            }
        }
        this.next();
    }

    protected void applyWebToCell(int cell) {
        GameScene.add(Blob.seed(cell, 20, Web.class));
    }

    private int left(int direction) {
        return direction == 0 ? 7 : direction - 1;
    }

    private int right(int direction) {
        return direction == 7 ? 0 : direction + 1;
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && Spinner.this.webCoolDown <= 0 && Spinner.this.lastEnemyPos != -1 && Spinner.this.webPos() != -1) {
                if (Spinner.this.sprite != null && (Spinner.this.sprite.visible || Spinner.this.enemy.sprite.visible)) {
                    Spinner.this.sprite.zap(Spinner.this.webPos());
                    Spinner.this.shotWebVisually = true;
                    return false;
                }
                Spinner.this.shootWeb();
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    private class Fleeing
    extends Mob.Fleeing {
        private Fleeing() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (Spinner.this.buff(Terror.class) == null && Spinner.this.buff(Dread.class) == null && enemyInFOV && Spinner.this.enemy.buff(Poison.class) == null) {
                Spinner.this.state = Spinner.this.HUNTING;
                return true;
            }
            if (enemyInFOV && Spinner.this.webCoolDown <= 0 && Spinner.this.lastEnemyPos != -1 && Spinner.this.webPos() != -1) {
                if (Spinner.this.sprite != null && (Spinner.this.sprite.visible || Spinner.this.enemy.sprite.visible)) {
                    Spinner.this.sprite.zap(Spinner.this.webPos());
                    Spinner.this.shotWebVisually = true;
                    return false;
                }
                Spinner.this.shootWeb();
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }
}

