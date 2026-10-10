/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.tweeners.Delayer;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class WildMagic
extends ArmorAbility {
    Actor wildMagicActor;

    public WildMagic() {
        this.baseChargeUse = 25.0f;
        this.wildMagicActor = null;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    protected void activate(ClassArmor armor, Hero hero, Integer target) {
        if (target == null) {
            return;
        }
        if (target == hero.pos) {
            GLog.w(Messages.get(this, "self_target", new Object[0]), new Object[0]);
            return;
        }
        ArrayList<Wand> wands = hero.belongings.getAllItems(Wand.class);
        Random.shuffle(wands);
        float chargeUsePerShot = 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
        for (Wand w : wands.toArray(new Wand[0])) {
            if (w.curCharges >= 1 || !(w.partialCharge < chargeUsePerShot)) continue;
            wands.remove(w);
        }
        int maxWands = 4 + Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING);
        if (wands.size() < maxWands) {
            ArrayList<Wand> seconds = new ArrayList<Wand>(wands);
            ArrayList<Wand> thirds = new ArrayList<Wand>(wands);
            for (Wand w : wands) {
                float totalCharge = (float)w.curCharges + w.partialCharge;
                if (totalCharge < 2.0f * chargeUsePerShot) {
                    seconds.remove(w);
                }
                if (!(totalCharge < 3.0f * chargeUsePerShot) && Random.Int(4) < Dungeon.hero.pointsInTalent(Talent.FIRE_EVERYTHING)) continue;
                thirds.remove(w);
            }
            Random.shuffle(seconds);
            while (!seconds.isEmpty() && wands.size() < maxWands) {
                wands.add(seconds.remove(0));
            }
            Random.shuffle(thirds);
            while (!thirds.isEmpty() && wands.size() < maxWands) {
                wands.add(thirds.remove(0));
            }
        } else {
            while (wands.size() > maxWands) {
                wands.remove(0);
            }
        }
        if (wands.size() == 0) {
            GLog.w(Messages.get(this, "no_wands", new Object[0]), new Object[0]);
            return;
        }
        hero.busy();
        Random.shuffle(wands);
        Buff.affect(hero, WildMagicTracker.class, 0.0f);
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        this.zapWand(wands, hero, target);
    }

    private void zapWand(final ArrayList<Wand> wands, final Hero hero, final int cell) {
        final Wand cur = wands.remove(0);
        final Ballistica aim = new Ballistica(hero.pos, cell, cur.collisionProperties(cell));
        hero.sprite.zap(cell);
        final float startTime = Game.timeTotal;
        if (cur.tryToZap(hero, cell)) {
            if (!cur.cursed) {
                cur.fx(aim, new Callback(){

                    @Override
                    public void call() {
                        boolean alsoCursedZap;
                        cur.onZap(aim);
                        boolean bl = alsoCursedZap = Random.Float() < WondrousResin.extraCurseEffectChance();
                        if (Game.timeTotal - startTime < 0.33f) {
                            hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)){

                                @Override
                                protected void onComplete() {
                                    if (alsoCursedZap) {
                                        WondrousResin.forcePositive = true;
                                        CursedWand.cursedZap(cur, hero, new Ballistica(hero.pos, cell, 6), new Callback(){

                                            @Override
                                            public void call() {
                                                WondrousResin.forcePositive = false;
                                                WildMagic.this.afterZap(cur, wands, hero, cell);
                                            }
                                        });
                                    } else {
                                        WildMagic.this.afterZap(cur, wands, hero, cell);
                                    }
                                }
                            });
                        } else if (alsoCursedZap) {
                            WondrousResin.forcePositive = true;
                            CursedWand.cursedZap(cur, hero, new Ballistica(hero.pos, cell, 6), new Callback(){

                                @Override
                                public void call() {
                                    WondrousResin.forcePositive = false;
                                    WildMagic.this.afterZap(cur, wands, hero, cell);
                                }
                            });
                        } else {
                            WildMagic.this.afterZap(cur, wands, hero, cell);
                        }
                    }
                });
            } else {
                CursedWand.cursedZap(cur, hero, new Ballistica(hero.pos, cell, 6), new Callback(){

                    @Override
                    public void call() {
                        if (Game.timeTotal - startTime < 0.33f) {
                            hero.sprite.parent.add(new Delayer(0.33f - (Game.timeTotal - startTime)){

                                @Override
                                protected void onComplete() {
                                    WildMagic.this.afterZap(cur, wands, hero, cell);
                                }
                            });
                        } else {
                            WildMagic.this.afterZap(cur, wands, hero, cell);
                        }
                    }
                });
            }
        } else {
            this.afterZap(cur, wands, hero, cell);
        }
    }

    private void afterZap(Wand cur, final ArrayList<Wand> wands, final Hero hero, final int target) {
        cur.partialCharge -= 0.5f * (float)Math.pow(0.67f, hero.pointsInTalent(Talent.CONSERVED_MAGIC));
        if (cur.partialCharge < 0.0f) {
            cur.partialCharge += 1.0f;
            --cur.curCharges;
        }
        if (this.wildMagicActor != null) {
            this.wildMagicActor.next();
            this.wildMagicActor = null;
        }
        final Char ch = Actor.findChar(target);
        if (!wands.isEmpty() && hero.isAlive()) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 99;
                }

                @Override
                protected boolean act() {
                    WildMagic.this.wildMagicActor = this;
                    WildMagic.this.zapWand(wands, hero, ch == null ? target : ch.pos);
                    Actor.remove(this);
                    return false;
                }
            });
            hero.next();
        } else {
            if (hero.buff(WildMagicTracker.class) != null) {
                hero.buff(WildMagicTracker.class).detach();
            }
            Item.updateQuickslot();
            Invisibility.dispel();
            if (Random.Int(4) >= hero.pointsInTalent(Talent.CONSERVED_MAGIC)) {
                hero.spendAndNext(1.0f);
            } else {
                hero.next();
            }
        }
    }

    @Override
    public int icon() {
        return 20;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.WILD_POWER, Talent.FIRE_EVERYTHING, Talent.CONSERVED_MAGIC, Talent.HEROIC_ENERGY};
    }

    public static class WildMagicTracker
    extends FlavourBuff {
    }
}

