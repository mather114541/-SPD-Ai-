/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicImmune;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GolemSprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class Golem
extends Mob {
    private boolean teleporting;
    private int selfTeleCooldown;
    private int enemyTeleCooldown;
    private static final String TELEPORTING = "teleporting";
    private static final String SELF_COOLDOWN = "self_cooldown";
    private static final String ENEMY_COOLDOWN = "enemy_cooldown";

    public Golem() {
        this.spriteClass = GolemSprite.class;
        this.HT = 120;
        this.HP = 120;
        this.defenseSkill = 15;
        this.EXP = 12;
        this.maxLvl = 22;
        this.loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
        this.lootChance = 0.2f;
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.LARGE);
        this.WANDERING = new Wandering();
        this.HUNTING = new Hunting();
        this.teleporting = false;
        this.selfTeleCooldown = 0;
        this.enemyTeleCooldown = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(25, 30);
    }

    @Override
    public int attackSkill(Char target) {
        return 28;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 12);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.3333333432674408, Dungeon.LimitedDrops.GOLEM_EQUIP.count);
    }

    @Override
    public void rollToDropLoot() {
        Imp.Quest.oldProcess(this);
        super.rollToDropLoot();
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.GOLEM_EQUIP.count;
        if (this.loot == Generator.Category.WEAPON) {
            return Generator.randomWeapon(5, true);
        }
        return Generator.randomArmor(5);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(TELEPORTING, this.teleporting);
        bundle.put(SELF_COOLDOWN, this.selfTeleCooldown);
        bundle.put(ENEMY_COOLDOWN, this.enemyTeleCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.teleporting = bundle.getBoolean(TELEPORTING);
        this.selfTeleCooldown = bundle.getInt(SELF_COOLDOWN);
        this.enemyTeleCooldown = bundle.getInt(ENEMY_COOLDOWN);
    }

    @Override
    protected boolean act() {
        --this.selfTeleCooldown;
        --this.enemyTeleCooldown;
        if (this.teleporting) {
            ((GolemSprite)this.sprite).teleParticles(false);
            if (Actor.findChar(this.target) == null && Dungeon.level.openSpace[this.target]) {
                ScrollOfTeleportation.appear(this, this.target);
                this.selfTeleCooldown = 30;
            } else {
                this.target = Dungeon.level.randomDestination(this);
            }
            this.teleporting = false;
            this.spend(1.0f);
            return true;
        }
        return super.act();
    }

    public void onZapComplete() {
        this.teleportEnemy();
        this.next();
    }

    public void teleportEnemy() {
        this.spend(1.0f);
        int bestPos = this.enemy.pos;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (!Dungeon.level.passable[this.pos + i] || Actor.findChar(this.pos + i) != null || !(Dungeon.level.trueDistance(this.pos + i, this.enemy.pos) > Dungeon.level.trueDistance(bestPos, this.enemy.pos))) continue;
            bestPos = this.pos + i;
        }
        if (this.enemy.buff(MagicImmune.class) != null) {
            bestPos = this.enemy.pos;
        }
        if (bestPos != this.enemy.pos) {
            ScrollOfTeleportation.appear(this.enemy, bestPos);
            if (this.enemy instanceof Hero) {
                ((Hero)this.enemy).interrupt();
                Dungeon.observe();
                GameScene.updateFog();
            }
        }
        this.enemyTeleCooldown = 20;
    }

    private boolean canTele(int target) {
        if (this.enemyTeleCooldown > 0) {
            return false;
        }
        PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(this.pos, target) + 1);
        return PathFinder.distance[this.pos] != Integer.MAX_VALUE;
    }

    private class Wandering
    extends Mob.Wandering {
        private Wandering() {
            super(Golem.this);
        }

        @Override
        protected boolean continueWandering() {
            Golem.this.enemySeen = false;
            int oldPos = Golem.this.pos;
            if (Golem.this.target != -1 && Golem.this.getCloser(Golem.this.target)) {
                Golem.this.spend(1.0f / Golem.this.speed());
                return Golem.this.moveSprite(oldPos, Golem.this.pos);
            }
            if (!Dungeon.bossLevel() && Golem.this.target != -1 && Golem.this.target != Golem.this.pos && Golem.this.selfTeleCooldown <= 0) {
                ((GolemSprite)Golem.this.sprite).teleParticles(true);
                Golem.this.teleporting = true;
                Golem.this.spend(2.0f);
            } else {
                Golem.this.target = this.randomDestination();
                Golem.this.spend(1.0f);
            }
            return true;
        }
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
            super(Golem.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV || Golem.this.canAttack(Golem.this.enemy)) {
                return super.act(enemyInFOV, justAlerted);
            }
            if (this.handleRecentAttackers()) {
                return this.act(true, justAlerted);
            }
            Golem.this.enemySeen = true;
            Golem.this.target = Golem.this.enemy.pos;
            int oldPos = Golem.this.pos;
            if (Golem.this.distance(Golem.this.enemy) >= 1 && Random.Int(100 / Golem.this.distance(Golem.this.enemy)) == 0 && !Char.hasProp(Golem.this.enemy, Char.Property.IMMOVABLE) && Golem.this.canTele(Golem.this.target)) {
                if (Golem.this.sprite != null && (Golem.this.sprite.visible || Golem.this.enemy.sprite.visible)) {
                    Golem.this.sprite.zap(Golem.this.enemy.pos);
                    return false;
                }
                Golem.this.teleportEnemy();
                return true;
            }
            if (Golem.this.getCloser(Golem.this.target)) {
                Golem.this.spend(1.0f / Golem.this.speed());
                return Golem.this.moveSprite(oldPos, Golem.this.pos);
            }
            if (!Char.hasProp(Golem.this.enemy, Char.Property.IMMOVABLE) && Golem.this.canTele(Golem.this.target)) {
                if (Golem.this.sprite != null && (Golem.this.sprite.visible || Golem.this.enemy.sprite.visible)) {
                    Golem.this.sprite.zap(Golem.this.enemy.pos);
                    return false;
                }
                Golem.this.teleportEnemy();
                return true;
            }
            return this.handleUnreachableTarget(enemyInFOV, justAlerted);
        }
    }
}

