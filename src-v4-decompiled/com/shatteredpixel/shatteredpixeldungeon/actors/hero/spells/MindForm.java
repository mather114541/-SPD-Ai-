/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.Trinity;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;

public class MindForm
extends ClericSpell {
    public static MindForm INSTANCE = new MindForm();

    @Override
    public int icon() {
        return 62;
    }

    @Override
    public String desc() {
        return Messages.get(this, "desc", MindForm.itemLevel()) + "\n\n" + Messages.get(this, "charge_cost", (int)this.chargeUse(Dungeon.hero));
    }

    @Override
    public float chargeUse(Hero hero) {
        return 3.0f;
    }

    @Override
    public boolean canCast(Hero hero) {
        return super.canCast(hero) && hero.hasTalent(Talent.MIND_FORM);
    }

    public static int effectLevel() {
        return 2 + Dungeon.hero.pointsInTalent(Talent.MIND_FORM);
    }

    @Override
    public void onCast(HolyTome tome, Hero hero) {
        GameScene.show(new Trinity.WndItemtypeSelect(tome, this));
    }

    public static int itemLevel() {
        return 2 + Dungeon.hero.pointsInTalent(Talent.MIND_FORM);
    }

    public static class targetSelector
    extends CellSelector.Listener {
        private Bundlable effect;

        public void setEffect(Bundlable effect) {
            this.effect = effect;
        }

        private Wand wand() {
            if (this.effect instanceof Wand) {
                ((Wand)this.effect).level(MindForm.effectLevel());
                ((Wand)this.effect).curCharges = ((Wand)this.effect).maxCharges;
                ((Wand)this.effect).identify(false);
                return (Wand)this.effect;
            }
            return null;
        }

        private MissileWeapon thrown() {
            if (this.effect instanceof MissileWeapon) {
                ((MissileWeapon)this.effect).level(MindForm.effectLevel());
                ((MissileWeapon)this.effect).repair(100.0f);
                ((MissileWeapon)this.effect).identify(false);
                ((MissileWeapon)this.effect).spawnedForEffect = true;
                return (MissileWeapon)this.effect;
            }
            return null;
        }

        @Override
        public void onSelect(Integer target) {
            if (target == null) {
                return;
            }
            if (this.wand() != null) {
                final Wand wand = this.wand();
                if (wand.tryToZap(Dungeon.hero, target)) {
                    final Ballistica shot = new Ballistica(Dungeon.hero.pos, target, wand.collisionProperties(target));
                    final int cell = shot.collisionPos;
                    if (target == Dungeon.hero.pos || cell == Dungeon.hero.pos) {
                        GLog.i(Messages.get(Wand.class, "self_target", new Object[0]), new Object[0]);
                        return;
                    }
                    Dungeon.hero.sprite.zap(cell);
                    if (Actor.findChar(target) != null) {
                        QuickSlotButton.target(Actor.findChar(target));
                    } else {
                        QuickSlotButton.target(Actor.findChar(cell));
                    }
                    wand.fx(shot, new Callback(){

                        @Override
                        public void call() {
                            wand.onZap(shot);
                            if (Random.Float() < WondrousResin.extraCurseEffectChance()) {
                                WondrousResin.forcePositive = true;
                                CursedWand.cursedZap(wand, Dungeon.hero, new Ballistica(Dungeon.hero.pos, cell, 6), new Callback(){

                                    @Override
                                    public void call() {
                                        WondrousResin.forcePositive = false;
                                    }
                                });
                            }
                            ((ClassArmor)Dungeon.hero.belongings.armor()).charge -= Trinity.trinityChargeUsePerEffect(wand.getClass());
                            wand.wandUsed();
                        }
                    });
                }
            } else if (this.thrown() != null) {
                MissileWeapon thrown = this.thrown();
                thrown.cast(Dungeon.hero, target);
                ((ClassArmor)Dungeon.hero.belongings.armor()).charge -= Trinity.trinityChargeUsePerEffect(thrown.getClass());
            }
        }

        @Override
        public String prompt() {
            if (this.wand() != null) {
                return Messages.get(Wand.class, "prompt", new Object[0]);
            }
            return Messages.get(Item.class, "prompt", new Object[0]);
        }
    }
}

