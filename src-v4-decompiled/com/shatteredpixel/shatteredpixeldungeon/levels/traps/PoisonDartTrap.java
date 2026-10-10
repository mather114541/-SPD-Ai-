/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.PoisonDart;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class PoisonDartTrap
extends Trap {
    public PoisonDartTrap() {
        this.color = 3;
        this.shape = 5;
        this.canBeHidden = false;
        this.avoidsHallways = true;
    }

    protected int poisonAmount() {
        return 8 + Math.round((float)(2 * this.scalingDepth()) / 3.0f);
    }

    protected boolean canTarget(Char ch) {
        return true;
    }

    @Override
    public void activate() {
        Actor.add(new Actor(){
            {
                this.actPriority = 100;
            }

            @Override
            protected boolean act() {
                Actor.remove(this);
                Char target = Actor.findChar(PoisonDartTrap.this.pos);
                if (target != null && !PoisonDartTrap.this.canTarget(target)) {
                    target = null;
                }
                float range = (float)Math.max(6, Dungeon.level.viewDistance) + 0.5f;
                if (target == null) {
                    float closestDist = Float.MAX_VALUE;
                    for (Char ch : Actor.chars()) {
                        if (!ch.isAlive()) continue;
                        float curDist = Dungeon.level.trueDistance(PoisonDartTrap.this.pos, ch.pos);
                        if (ch.invisible > 0) {
                            curDist = Math.max(curDist, range);
                        }
                        Ballistica bolt = new Ballistica(PoisonDartTrap.this.pos, ch.pos, 7);
                        if (!PoisonDartTrap.this.canTarget(ch) || bolt.collisionPos != ch.pos || !(curDist < closestDist) && (curDist != closestDist || !(target instanceof Hero))) continue;
                        target = ch;
                        closestDist = curDist;
                    }
                    if (closestDist > range) {
                        target = null;
                    }
                }
                if (target != null) {
                    if (target instanceof Mob) {
                        Buff.prolong(target, Trap.HazardAssistTracker.class, 50.0f);
                    }
                    final Char finalTarget = target;
                    if (Dungeon.level.heroFOV[PoisonDartTrap.this.pos] || Dungeon.level.heroFOV[target.pos]) {
                        ((MissileSprite)ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).reset(PoisonDartTrap.this.pos, (Visual)finalTarget.sprite, (Item)new PoisonDart(), new Callback(){

                            @Override
                            public void call() {
                                int dmg = Random.NormalIntRange(4, 8) - finalTarget.drRoll();
                                finalTarget.damage(dmg, PoisonDartTrap.this);
                                if (finalTarget == Dungeon.hero) {
                                    if (Dungeon.depth == 10) {
                                        Statistics.qualifiedForBossChallengeBadge = false;
                                        Statistics.bossScores[1] = Statistics.bossScores[1] - 100;
                                    }
                                    if (!finalTarget.isAlive()) {
                                        Dungeon.fail(PoisonDartTrap.this);
                                        GLog.n(Messages.get(PoisonDartTrap.class, "ondeath", new Object[0]), new Object[0]);
                                        if (PoisonDartTrap.this.reclaimed) {
                                            Badges.validateDeathFromFriendlyMagic();
                                        }
                                    }
                                }
                                Buff.affect(finalTarget, Poison.class).set(PoisonDartTrap.this.poisonAmount());
                                Sample.INSTANCE.play("sounds/hit.mp3", 1.0f, 1.0f, Random.Float(0.8f, 1.25f));
                                finalTarget.sprite.bloodBurstA(finalTarget.sprite.center(), dmg);
                                finalTarget.sprite.flash();
                                this.next();
                            }
                        });
                        return false;
                    }
                    finalTarget.damage(Random.NormalIntRange(4, 8) - finalTarget.drRoll(), PoisonDartTrap.this);
                    Buff.affect(finalTarget, Poison.class).set(PoisonDartTrap.this.poisonAmount());
                    return true;
                }
                return true;
            }
        });
    }
}

