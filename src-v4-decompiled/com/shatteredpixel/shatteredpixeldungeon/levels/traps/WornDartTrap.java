/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.traps;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class WornDartTrap
extends Trap {
    public WornDartTrap() {
        this.color = 7;
        this.shape = 5;
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
                Char target = Actor.findChar(WornDartTrap.this.pos);
                float range = (float)Math.max(6, Dungeon.level.viewDistance) + 0.5f;
                if (target == null) {
                    float closestDist = Float.MAX_VALUE;
                    for (Char ch : Actor.chars()) {
                        if (!ch.isAlive()) continue;
                        float curDist = Dungeon.level.trueDistance(WornDartTrap.this.pos, ch.pos);
                        if (ch.invisible > 0) {
                            curDist = Math.max(curDist, range);
                        }
                        Ballistica bolt = new Ballistica(WornDartTrap.this.pos, ch.pos, 7);
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
                    if ((Dungeon.level.heroFOV[WornDartTrap.this.pos] || Dungeon.level.heroFOV[target.pos]) && !rl.RLMode.headless) {
                        ((MissileSprite)ShatteredPixelDungeon.scene().recycle(MissileSprite.class)).reset(WornDartTrap.this.pos, (Visual)finalTarget.sprite, (Item)new Dart(), new Callback(){

                            @Override
                            public void call() {
                                int dmg = Random.NormalIntRange(4, 8) - finalTarget.drRoll();
                                finalTarget.damage(dmg, WornDartTrap.this);
                                if (finalTarget == Dungeon.hero && !finalTarget.isAlive()) {
                                    Dungeon.fail(WornDartTrap.this);
                                    GLog.n(Messages.get(WornDartTrap.class, "ondeath", new Object[0]), new Object[0]);
                                    if (WornDartTrap.this.reclaimed) {
                                        Badges.validateDeathFromFriendlyMagic();
                                    }
                                }
                                Sample.INSTANCE.play("sounds/hit.mp3", 1.0f, 1.0f, Random.Float(0.8f, 1.25f));
                                finalTarget.sprite.bloodBurstA(finalTarget.sprite.center(), dmg);
                                finalTarget.sprite.flash();
                                // headless：动画分支已被短路跳过（走纯伤害分支），此 next 推进不执行
                            }
                        });
                        return false;
                    }
                    finalTarget.damage(Random.NormalIntRange(4, 8) - finalTarget.drRoll(), WornDartTrap.this);
                    return true;
                }
                return true;
            }
        });
    }
}

