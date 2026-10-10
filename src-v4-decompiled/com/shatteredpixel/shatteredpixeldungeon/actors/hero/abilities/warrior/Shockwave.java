/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.warrior;

import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Combo;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class Shockwave
extends ArmorAbility {
    public Shockwave() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return new Ballistica((int)user.pos, (int)dst, (int)5).collisionPos;
    }

    @Override
    protected void activate(ClassArmor armor, final Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (target == hero.pos) {
            GLog.w(Messages.get(this, "self_target", new Object[0]), new Object[0]);
            return;
        }
        hero.busy();
        armor.charge -= this.chargeUse(hero);
        Item.updateQuickslot();
        Ballistica aim = new Ballistica(hero.pos, target, 0);
        int maxDist = 5 + hero.pointsInTalent(Talent.EXPANDING_WAVE);
        int dist = Math.min(aim.dist, maxDist);
        final ConeAOE cone = new ConeAOE(aim, dist, 60 + 15 * hero.pointsInTalent(Talent.EXPANDING_WAVE), 5);
        for (Ballistica ray : cone.outerRays) {
            ((MagicMissile)hero.sprite.parent.recycle(MagicMissile.class)).reset(105, (Visual)hero.sprite, (int)ray.path.get(ray.dist), null);
        }
        hero.sprite.zap(target);
        Sample.INSTANCE.play("sounds/blast.mp3", 1.0f, 0.5f);
        PixelScene.shake(2.0f, 0.5f);
        MagicMissile.boltFromChar(hero.sprite.parent, 105, hero.sprite, cone.coreRay.path.get(dist * 2 / 3), new Callback(){

            @Override
            public void call() {
                for (int cell : cone.cells) {
                    Char ch = Actor.findChar(cell);
                    if (ch == null || ch.alignment == hero.alignment) continue;
                    int scalingStr = hero.STR() - 10;
                    int damage = Hero.heroDamageIntRange(5 + scalingStr, 10 + 2 * scalingStr);
                    damage = Math.round((float)damage * (1.0f + 0.2f * (float)hero.pointsInTalent(Talent.SHOCK_FORCE)));
                    damage -= ch.drRoll();
                    if (hero.pointsInTalent(Talent.STRIKING_WAVE) == 4) {
                        Buff.affect(hero, Talent.StrikingWaveTracker.class, 0.0f);
                    }
                    if (Random.Int(10) < 3 * hero.pointsInTalent(Talent.STRIKING_WAVE)) {
                        boolean wasEnemy = ch.alignment == Char.Alignment.ENEMY || ch instanceof Mimic && ch.alignment == Char.Alignment.NEUTRAL;
                        damage = hero.attackProc(ch, damage);
                        ch.damage(damage, hero);
                        if (hero.subClass == HeroSubClass.GLADIATOR && wasEnemy) {
                            Buff.affect(hero, Combo.class).hit(ch);
                        }
                    } else {
                        ch.damage(damage, hero);
                    }
                    if (!ch.isAlive()) continue;
                    if (Random.Int(4) < hero.pointsInTalent(Talent.SHOCK_FORCE)) {
                        Buff.affect(ch, Paralysis.class, 5.0f);
                        continue;
                    }
                    Buff.affect(ch, Cripple.class, 5.0f);
                }
                Invisibility.dispel();
                hero.spendAndNext(1.0f);
            }
        });
    }

    @Override
    public int icon() {
        return 17;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.EXPANDING_WAVE, Talent.STRIKING_WAVE, Talent.SHOCK_FORCE, Talent.HEROIC_ENERGY};
    }
}

