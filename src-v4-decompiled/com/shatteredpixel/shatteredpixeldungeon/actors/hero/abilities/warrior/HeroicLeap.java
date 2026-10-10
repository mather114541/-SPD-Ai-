/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vulnerable;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

public class HeroicLeap
extends ArmorAbility {
    public HeroicLeap() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public float chargeUse(Hero hero) {
        float chargeUse = super.chargeUse(hero);
        if (hero.buff(DoubleJumpTracker.class) != null) {
            chargeUse = (float)((double)chargeUse * Math.pow(0.84, hero.pointsInTalent(Talent.DOUBLE_JUMP)));
        }
        return chargeUse;
    }

    @Override
    public void activate(ClassArmor armor, final Hero hero, Integer target) {
        if (target != null) {
            if (hero.rooted) {
                PixelScene.shake(1.0f, 1.0f);
                return;
            }
            Ballistica route = new Ballistica(hero.pos, target, 5);
            int cell = route.collisionPos;
            int backTrace = route.dist - 1;
            while (Actor.findChar(cell) != null && cell != hero.pos) {
                cell = route.path.get(backTrace);
                --backTrace;
            }
            armor.charge -= this.chargeUse(hero);
            armor.updateQuickslot();
            final int dest = cell;
            hero.busy();
            hero.sprite.jump(hero.pos, cell, new Callback(){

                @Override
                public void call() {
                    hero.move(dest);
                    Dungeon.level.occupyCell(hero);
                    Dungeon.observe();
                    GameScene.updateFog();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        Char mob = Actor.findChar(hero.pos + i);
                        if (mob == null || mob == hero || mob.alignment == Char.Alignment.ALLY) continue;
                        if (hero.hasTalent(Talent.BODY_SLAM)) {
                            int damage = Hero.heroDamageIntRange(hero.pointsInTalent(Talent.BODY_SLAM), 4 * hero.pointsInTalent(Talent.BODY_SLAM));
                            damage += Math.round((float)hero.drRoll() * 0.25f * (float)hero.pointsInTalent(Talent.BODY_SLAM));
                            mob.damage(damage -= mob.drRoll(), hero);
                        }
                        if (mob.pos != hero.pos + i || !hero.hasTalent(Talent.IMPACT_WAVE)) continue;
                        Ballistica trajectory = new Ballistica(mob.pos, mob.pos + i, 6);
                        int strength = 1 + hero.pointsInTalent(Talent.IMPACT_WAVE);
                        WandOfBlastWave.throwChar(mob, trajectory, strength, true, true, HeroicLeap.this);
                        if (Random.Int(4) >= hero.pointsInTalent(Talent.IMPACT_WAVE)) continue;
                        Buff.prolong(mob, Vulnerable.class, 5.0f);
                    }
                    WandOfBlastWave.BlastWave.blast(dest);
                    PixelScene.shake(2.0f, 0.5f);
                    Invisibility.dispel();
                    hero.spendAndNext(1.0f);
                    if (hero.buff(DoubleJumpTracker.class) != null) {
                        hero.buff(DoubleJumpTracker.class).detach();
                    } else if (hero.hasTalent(Talent.DOUBLE_JUMP)) {
                        Buff.affect(hero, DoubleJumpTracker.class, 3.0f);
                    }
                }
            });
        }
    }

    @Override
    public int icon() {
        return 16;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.BODY_SLAM, Talent.IMPACT_WAVE, Talent.DOUBLE_JUMP, Talent.HEROIC_ENERGY};
    }

    public static class DoubleJumpTracker
    extends FlavourBuff {
    }
}

