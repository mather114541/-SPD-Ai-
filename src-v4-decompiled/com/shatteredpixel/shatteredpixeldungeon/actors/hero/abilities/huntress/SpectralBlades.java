/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.Shuriken;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Visual;
import com.watabou.utils.Callback;
import java.util.HashSet;

public class SpectralBlades
extends ArmorAbility {
    public SpectralBlades() {
        this.baseChargeUse = 25.0f;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void activate(ClassArmor armor, final Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (Actor.findChar(target) == hero) {
            GLog.w(Messages.get(this, "self_target", new Object[0]), new Object[0]);
            return;
        }
        Ballistica b = new Ballistica(hero.pos, target, 0);
        HashSet<Char> targets = new HashSet<Char>();
        final Char enemy = this.findChar(b, hero, 2 * hero.pointsInTalent(Talent.PROJECTING_BLADES), targets);
        if (enemy == null || !hero.fieldOfView[enemy.pos]) {
            GLog.w(Messages.get(this, "no_target", new Object[0]), new Object[0]);
            return;
        }
        targets.add(enemy);
        if (hero.hasTalent(Talent.FAN_OF_BLADES)) {
            ConeAOE cone = new ConeAOE(b, 30 * hero.pointsInTalent(Talent.FAN_OF_BLADES));
            for (Ballistica ray : cone.rays) {
                Char toAdd = this.findChar(ray, hero, 2 * hero.pointsInTalent(Talent.PROJECTING_BLADES), targets);
                if (toAdd == null || !hero.fieldOfView[toAdd.pos]) continue;
                targets.add(toAdd);
            }
            while (targets.size() > 1 + hero.pointsInTalent(Talent.FAN_OF_BLADES)) {
                Char furthest = null;
                for (final Char ch : targets) {
                    if (furthest == null) {
                        furthest = ch;
                        continue;
                    }
                    if (!(Dungeon.level.trueDistance(enemy.pos, ch.pos) > Dungeon.level.trueDistance(enemy.pos, furthest.pos))) continue;
                    furthest = ch;
                }
                targets.remove(furthest);
            }
        }
        armor.charge -= this.chargeUse(hero);
        Item.updateQuickslot();
        Shuriken proto = new Shuriken();
        final HashSet<1> callbacks = new HashSet<1>();
        for (final Char ch : targets) {
            Callback callback = new Callback(){

                @Override
                public void call() {
                    float dmgMulti = ch == enemy ? 1.0f : 0.5f;
                    float accmulti = 1.0f + 0.25f * (float)hero.pointsInTalent(Talent.PROJECTING_BLADES);
                    if (hero.hasTalent(Talent.SPIRIT_BLADES)) {
                        Buff.affect(hero, Talent.SpiritBladesTracker.class, 0.0f);
                    }
                    hero.attack(ch, dmgMulti, 0.0f, accmulti);
                    callbacks.remove(this);
                    if (callbacks.isEmpty()) {
                        Invisibility.dispel();
                        hero.spendAndNext(hero.attackDelay());
                    }
                }
            };
            MissileSprite m = (MissileSprite)hero.sprite.parent.recycle(MissileSprite.class);
            m.reset((Visual)hero.sprite, ch.pos, (Item)proto, callback);
            m.hardlight(0.6f, 1.0f, 1.0f);
            m.alpha(0.8f);
            callbacks.add(callback);
        }
        hero.sprite.zap(enemy.pos);
        hero.busy();
    }

    private Char findChar(Ballistica path, Hero hero, int wallPenetration, HashSet<Char> existingTargets) {
        for (int cell : path.path) {
            Char ch = Actor.findChar(cell);
            if (ch != null) {
                if (ch == hero || existingTargets.contains(ch) || ch.alignment == Char.Alignment.ALLY) continue;
                return ch;
            }
            if (!Dungeon.level.solid[cell] || --wallPenetration >= 0) continue;
            return null;
        }
        return null;
    }

    @Override
    public int icon() {
        return 25;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.FAN_OF_BLADES, Talent.PROJECTING_BLADES, Talent.SPIRIT_BLADES, Talent.HEROIC_ENERGY};
    }
}

