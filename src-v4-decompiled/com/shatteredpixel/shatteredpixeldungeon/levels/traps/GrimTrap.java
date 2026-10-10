/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;

public class GrimTrap
extends Trap {
    public GrimTrap() {
        this.color = 7;
        this.shape = 6;
        this.canBeHidden = false;
        this.avoidsHallways = true;
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
                Char target = Actor.findChar(GrimTrap.this.pos);
                float range = (float)Math.max(6, Dungeon.level.viewDistance) + 0.5f;
                if (target == null) {
                    float closestDist = Float.MAX_VALUE;
                    for (Char ch : Actor.chars()) {
                        if (!ch.isAlive()) continue;
                        float curDist = Dungeon.level.trueDistance(GrimTrap.this.pos, ch.pos);
                        if (ch.invisible > 0) {
                            curDist = Math.max(curDist, range);
                        }
                        Ballistica bolt = new Ballistica(GrimTrap.this.pos, ch.pos, 7);
                        if (bolt.collisionPos != ch.pos || !(curDist < closestDist) && (curDist != closestDist || !(target instanceof Hero))) continue;
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
                    int damage = Math.round((float)finalTarget.HT / 2.0f + (float)finalTarget.HP / 2.0f);
                    if (finalTarget == Dungeon.hero) {
                        damage = (int)Math.min((float)damage, (float)finalTarget.HT * 0.9f);
                    }
                    final int finalDmg = damage;
                    if (Dungeon.level.heroFOV[GrimTrap.this.pos] || Dungeon.level.heroFOV[target.pos]) {
                        ((MagicMissile)finalTarget.sprite.parent.recycle(MagicMissile.class)).reset(7, DungeonTilemap.tileCenterToWorld(GrimTrap.this.pos), finalTarget.sprite.center(), new Callback(){

                            @Override
                            public void call() {
                                finalTarget.damage(finalDmg, GrimTrap.this);
                                if (finalTarget == Dungeon.hero) {
                                    Sample.INSTANCE.play("sounds/cursed.mp3");
                                    if (!finalTarget.isAlive()) {
                                        Badges.validateDeathFromGrimOrDisintTrap();
                                        Dungeon.fail(GrimTrap.this);
                                        GLog.n(Messages.get(GrimTrap.class, "ondeath", new Object[0]), new Object[0]);
                                        if (GrimTrap.this.reclaimed) {
                                            Badges.validateDeathFromFriendlyMagic();
                                        }
                                    }
                                } else {
                                    Sample.INSTANCE.play("sounds/burning.mp3");
                                }
                                finalTarget.sprite.emitter().burst(ShadowParticle.UP, 10);
                                this.next();
                            }
                        });
                        return false;
                    }
                    finalTarget.damage(finalDmg, GrimTrap.this);
                    return true;
                }
                CellEmitter.get(GrimTrap.this.pos).burst(ShadowParticle.UP, 10);
                Sample.INSTANCE.play("sounds/burning.mp3");
                return true;
            }
        });
    }
}

