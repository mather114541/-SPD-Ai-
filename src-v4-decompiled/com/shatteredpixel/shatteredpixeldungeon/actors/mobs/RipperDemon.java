/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.RipperSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class RipperDemon
extends Mob {
    private static final String LAST_ENEMY_POS = "last_enemy_pos";
    private static final String LEAP_POS = "leap_pos";
    private static final String LEAP_CD = "leap_cd";
    private int lastEnemyPos;
    private int leapPos;
    private float leapCooldown;

    public RipperDemon() {
        this.spriteClass = RipperSprite.class;
        this.HT = 60;
        this.HP = 60;
        this.defenseSkill = 22;
        this.viewDistance = 6;
        this.EXP = 9;
        this.maxLvl = -2;
        this.HUNTING = new Hunting();
        this.baseSpeed = 1.0f;
        this.properties.add(Char.Property.DEMONIC);
        this.properties.add(Char.Property.UNDEAD);
        this.lastEnemyPos = -1;
        this.leapPos = -1;
        this.leapCooldown = 0.0f;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(15, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 30;
    }

    @Override
    public float attackDelay() {
        return super.attackDelay() * 0.5f;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 4);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(LAST_ENEMY_POS, this.lastEnemyPos);
        bundle.put(LEAP_POS, this.leapPos);
        bundle.put(LEAP_CD, this.leapCooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.lastEnemyPos = bundle.getInt(LAST_ENEMY_POS);
        this.leapPos = bundle.getInt(LEAP_POS);
        this.leapCooldown = bundle.getFloat(LEAP_CD);
    }

    @Override
    protected boolean act() {
        if (this.state == this.WANDERING) {
            this.leapPos = -1;
        }
        Mob.AiState lastState = this.state;
        boolean result = super.act();
        if (this.paralysed <= 0) {
            this.leapCooldown -= 1.0f;
        }
        if (lastState != this.WANDERING || this.state != this.HUNTING) {
            this.lastEnemyPos = this.enemy != null ? this.enemy.pos : Dungeon.hero.pos;
        }
        return result;
    }

    public class Hunting
    extends Mob.Hunting {
        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (RipperDemon.this.leapPos != -1) {
                int endPos;
                RipperDemon.this.leapCooldown = Random.NormalIntRange(2, 4);
                if (RipperDemon.this.rooted) {
                    RipperDemon.this.leapPos = -1;
                    return true;
                }
                Ballistica b = new Ballistica(RipperDemon.this.pos, RipperDemon.this.leapPos, 5);
                RipperDemon.this.leapPos = b.collisionPos;
                final Char leapVictim = Actor.findChar(RipperDemon.this.leapPos);
                if (leapVictim != null) {
                    int bouncepos = -1;
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (bouncepos != -1 && !(Dungeon.level.trueDistance(RipperDemon.this.pos, RipperDemon.this.leapPos + i) < Dungeon.level.trueDistance(RipperDemon.this.pos, bouncepos)) || Actor.findChar(RipperDemon.this.leapPos + i) != null || !Dungeon.level.passable[RipperDemon.this.leapPos + i]) continue;
                        bouncepos = RipperDemon.this.leapPos + i;
                    }
                    if (bouncepos == -1) {
                        for (int i : PathFinder.NEIGHBOURS8) {
                            if (bouncepos != -1 && !(Dungeon.level.trueDistance(RipperDemon.this.pos, RipperDemon.this.leapPos + i) < Dungeon.level.trueDistance(RipperDemon.this.pos, bouncepos)) || Actor.findChar(RipperDemon.this.leapPos + i) != null || Dungeon.level.solid[RipperDemon.this.leapPos + i]) continue;
                            bouncepos = RipperDemon.this.leapPos + i;
                        }
                    }
                    if (bouncepos == -1) {
                        RipperDemon.this.leapPos = -1;
                        return true;
                    }
                    endPos = bouncepos;
                } else {
                    endPos = RipperDemon.this.leapPos;
                }
                RipperDemon.this.sprite.visible = Dungeon.level.heroFOV[RipperDemon.this.pos] || Dungeon.level.heroFOV[RipperDemon.this.leapPos] || Dungeon.level.heroFOV[endPos];
                RipperDemon.this.sprite.jump(RipperDemon.this.pos, RipperDemon.this.leapPos, new Callback(){

                    @Override
                    public void call() {
                        if (leapVictim != null && RipperDemon.this.alignment != leapVictim.alignment) {
                            if (Char.hit(RipperDemon.this, leapVictim, Char.INFINITE_ACCURACY, false)) {
                                Buff.affect(leapVictim, Bleeding.class).set(0.75f * (float)RipperDemon.this.damageRoll());
                                leapVictim.sprite.flash();
                                Sample.INSTANCE.play("sounds/hit.mp3");
                            } else {
                                leapVictim.sprite.showStatus(0xFFFF00, leapVictim.defenseVerb(), new Object[0]);
                                Sample.INSTANCE.play("sounds/miss.mp3");
                            }
                        }
                        if (endPos != RipperDemon.this.leapPos) {
                            Actor.add(new Pushing(RipperDemon.this, RipperDemon.this.leapPos, endPos));
                        }
                        RipperDemon.this.pos = endPos;
                        RipperDemon.this.leapPos = -1;
                        RipperDemon.this.sprite.idle();
                        Dungeon.level.occupyCell(RipperDemon.this);
                        RipperDemon.this.next();
                    }
                });
                return false;
            }
            RipperDemon.this.enemySeen = enemyInFOV;
            if (enemyInFOV && !RipperDemon.this.isCharmedBy(RipperDemon.this.enemy) && RipperDemon.this.canAttack(RipperDemon.this.enemy)) {
                RipperDemon.this.recentlyAttackedBy.clear();
                RipperDemon.this.target = RipperDemon.this.enemy.pos;
                return RipperDemon.this.doAttack(RipperDemon.this.enemy);
            }
            if (enemyInFOV) {
                RipperDemon.this.target = RipperDemon.this.enemy.pos;
            } else if (RipperDemon.this.enemy == null) {
                RipperDemon.this.state = RipperDemon.this.WANDERING;
                RipperDemon.this.target = Dungeon.level.randomDestination(RipperDemon.this);
                return true;
            }
            if (RipperDemon.this.leapCooldown <= 0.0f && enemyInFOV && !RipperDemon.this.rooted && Dungeon.level.distance(RipperDemon.this.pos, RipperDemon.this.enemy.pos) >= 3) {
                int targetPos = RipperDemon.this.enemy.pos;
                if (RipperDemon.this.lastEnemyPos != RipperDemon.this.enemy.pos) {
                    int closestIdx = 0;
                    for (int i = 1; i < PathFinder.CIRCLE8.length; ++i) {
                        if (!(Dungeon.level.trueDistance(RipperDemon.this.lastEnemyPos, RipperDemon.this.enemy.pos + PathFinder.CIRCLE8[i]) < Dungeon.level.trueDistance(RipperDemon.this.lastEnemyPos, RipperDemon.this.enemy.pos + PathFinder.CIRCLE8[closestIdx]))) continue;
                        closestIdx = i;
                    }
                    targetPos = RipperDemon.this.enemy.pos + PathFinder.CIRCLE8[(closestIdx + 4) % 8];
                }
                Ballistica b = new Ballistica(RipperDemon.this.pos, targetPos, 5);
                if (b.collisionPos != targetPos && targetPos != RipperDemon.this.enemy.pos) {
                    targetPos = RipperDemon.this.enemy.pos;
                    b = new Ballistica(RipperDemon.this.pos, targetPos, 5);
                }
                if (b.collisionPos == targetPos) {
                    RipperDemon.this.leapPos = targetPos;
                    float delay = GameMath.gate(RipperDemon.this.attackDelay(), (int)Math.ceil(RipperDemon.this.enemy.cooldown()), 3.0f * RipperDemon.this.attackDelay());
                    RipperDemon.this.spend(delay);
                    if (Dungeon.level.heroFOV[RipperDemon.this.pos] || Dungeon.level.heroFOV[RipperDemon.this.leapPos]) {
                        GLog.w(Messages.get(RipperDemon.this, "leap", new Object[0]), new Object[0]);
                        GameScene.targetedCell(RipperDemon.this.leapPos, delay);
                        ((RipperSprite)RipperDemon.this.sprite).leapPrep(RipperDemon.this.leapPos);
                        Dungeon.hero.interrupt();
                    }
                    return true;
                }
            }
            int oldPos = RipperDemon.this.pos;
            if (RipperDemon.this.target != -1 && RipperDemon.this.getCloser(RipperDemon.this.target)) {
                RipperDemon.this.spend(1.0f / RipperDemon.this.speed());
                return RipperDemon.this.moveSprite(oldPos, RipperDemon.this.pos);
            }
            RipperDemon.this.spend(1.0f);
            if (!enemyInFOV) {
                RipperDemon.this.sprite.showLost();
                RipperDemon.this.state = RipperDemon.this.WANDERING;
                RipperDemon.this.target = Dungeon.level.randomDestination(RipperDemon.this);
            }
            return true;
        }
    }
}

