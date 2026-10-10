/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM200Sprite;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class DM200
extends Mob {
    private int ventCooldown;
    private static final String VENT_COOLDOWN = "vent_cooldown";

    public DM200() {
        this.spriteClass = DM200Sprite.class;
        this.HT = 80;
        this.HP = 80;
        this.defenseSkill = 12;
        this.EXP = 9;
        this.maxLvl = 17;
        this.loot = Random.oneOf(Generator.Category.WEAPON, Generator.Category.ARMOR);
        this.lootChance = 0.2f;
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.LARGE);
        this.HUNTING = new Hunting();
        this.ventCooldown = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(10, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 8);
    }

    @Override
    public float lootChance() {
        return super.lootChance() * (float)Math.pow(0.3333333432674408, Dungeon.LimitedDrops.DM200_EQUIP.count);
    }

    @Override
    public Item createLoot() {
        ++Dungeon.LimitedDrops.DM200_EQUIP.count;
        if (this.loot == Generator.Category.WEAPON) {
            return Generator.randomWeapon(4, true);
        }
        return Generator.randomArmor(4);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(VENT_COOLDOWN, this.ventCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.ventCooldown = bundle.getInt(VENT_COOLDOWN);
    }

    @Override
    protected boolean act() {
        --this.ventCooldown;
        return super.act();
    }

    public void onZapComplete() {
        this.zap();
        this.next();
    }

    private void zap() {
        this.spend(1.0f);
        this.ventCooldown = 30;
        Ballistica trajectory = new Ballistica(this.pos, this.enemy.pos, 1);
        for (int i : trajectory.subPath(0, trajectory.dist)) {
            GameScene.add(Blob.seed(i, 20, ToxicGas.class));
        }
        GameScene.add(Blob.seed(trajectory.collisionPos, 100, ToxicGas.class));
    }

    protected boolean canVent(int target) {
        if (this.ventCooldown > 0) {
            return false;
        }
        PathFinder.buildDistanceMap(target, BArray.not(Dungeon.level.solid, null), Dungeon.level.distance(this.pos, target) + 1);
        return PathFinder.distance[this.pos] != Integer.MAX_VALUE;
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
            super(DM200.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV || DM200.this.canAttack(DM200.this.enemy)) {
                return super.act(enemyInFOV, justAlerted);
            }
            if (this.handleRecentAttackers()) {
                return this.act(true, justAlerted);
            }
            DM200.this.enemySeen = true;
            DM200.this.target = DM200.this.enemy.pos;
            int oldPos = DM200.this.pos;
            if (DM200.this.distance(DM200.this.enemy) >= 1 && Random.Int(100 / DM200.this.distance(DM200.this.enemy)) == 0 && DM200.this.canVent(DM200.this.target)) {
                if (DM200.this.sprite != null && (DM200.this.sprite.visible || DM200.this.enemy.sprite.visible)) {
                    DM200.this.sprite.zap(DM200.this.enemy.pos);
                    return false;
                }
                DM200.this.zap();
                return true;
            }
            if (DM200.this.getCloser(DM200.this.target)) {
                DM200.this.spend(1.0f / DM200.this.speed());
                return DM200.this.moveSprite(oldPos, DM200.this.pos);
            }
            if (DM200.this.canVent(DM200.this.target)) {
                if (DM200.this.sprite != null && (DM200.this.sprite.visible || DM200.this.enemy.sprite.visible)) {
                    DM200.this.sprite.zap(DM200.this.enemy.pos);
                    return false;
                }
                DM200.this.zap();
                return true;
            }
            return this.handleUnreachableTarget(enemyInFOV, justAlerted);
        }
    }
}

