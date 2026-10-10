/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ArtifactRecharge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Healing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMonkAbilities;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Image;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;

public class MonkEnergy
extends Buff
implements ActionIndicator.Action {
    public float energy;
    public int cooldown;
    private static final float MAX_COOLDOWN = 5.0f;
    public static String ENERGY = "energy";
    public static String COOLDOWN = "cooldown";

    public MonkEnergy() {
        this.type = Buff.buffType.POSITIVE;
        this.energy = 0.0f;
    }

    @Override
    public int icon() {
        return 68;
    }

    @Override
    public void tintIcon(Image icon) {
        if (this.cooldown > 0) {
            icon.hardlight(0.33f, 0.33f, 1.0f);
        } else {
            icon.resetColor();
        }
    }

    @Override
    public float iconFadePercent() {
        return GameMath.gate(0.0f, (float)this.cooldown / 5.0f, 1.0f);
    }

    @Override
    public String iconTextDisplay() {
        if (this.cooldown > 0) {
            return Integer.toString(this.cooldown);
        }
        return "";
    }

    @Override
    public boolean act() {
        if (this.cooldown > 0) {
            --this.cooldown;
            if (this.cooldown == 0 && this.energy >= 1.0f) {
                ActionIndicator.setAction(this);
            }
            BuffIndicator.refreshHero();
        }
        this.spend(1.0f);
        return true;
    }

    @Override
    public void detach() {
        super.detach();
        ActionIndicator.clearAction();
    }

    @Override
    public String desc() {
        Object desc = Messages.get(this, "desc", (int)this.energy, this.energyCap());
        if (this.cooldown > 0) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_cooldown", this.cooldown);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(ENERGY, this.energy);
        bundle.put(COOLDOWN, this.cooldown);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.energy = bundle.getFloat(ENERGY);
        this.cooldown = bundle.getInt(COOLDOWN);
        if (this.energy >= 1.0f && this.cooldown == 0) {
            ActionIndicator.setAction(this);
        }
    }

    public void gainEnergy(Mob enemy) {
        Hero hero;
        if (this.target == null) {
            return;
        }
        if (!Regeneration.regenOn()) {
            return;
        }
        float energyGain = Char.hasProp(enemy, Char.Property.BOSS) ? 5.0f : (Char.hasProp(enemy, Char.Property.MINIBOSS) ? 3.0f : (enemy instanceof Ghoul ? 0.5f : (enemy instanceof RipperDemon ? 0.5f : (enemy instanceof YogDzewa.Larva ? 0.5f : (enemy instanceof Wraith ? 0.5f : 1.0f)))));
        float enGainMulti = 1.0f;
        if (this.target instanceof Hero && (hero = (Hero)this.target).hasTalent(Talent.UNENCUMBERED_SPIRIT)) {
            int points = hero.pointsInTalent(Talent.UNENCUMBERED_SPIRIT);
            if (hero.belongings.armor() != null) {
                if (hero.belongings.armor().tier <= 1 && points >= 3) {
                    enGainMulti += 1.0f;
                } else if (hero.belongings.armor().tier <= 2 && points >= 2) {
                    enGainMulti += 0.75f;
                } else if (hero.belongings.armor().tier <= 3 && points >= 1) {
                    enGainMulti += 0.5f;
                }
            }
            if (hero.belongings.weapon() instanceof MeleeWeapon && (hero.buff(RingOfForce.BrawlersStance.class) == null || !hero.buff(RingOfForce.BrawlersStance.class).active)) {
                if (((MeleeWeapon)hero.belongings.weapon()).tier <= 1 && points >= 3) {
                    enGainMulti += 1.0f;
                } else if (((MeleeWeapon)hero.belongings.weapon()).tier <= 2 && points >= 2) {
                    enGainMulti += 0.75f;
                } else if (((MeleeWeapon)hero.belongings.weapon()).tier <= 3 && points >= 1) {
                    enGainMulti += 0.5f;
                }
            }
        }
        this.energy += (energyGain *= enGainMulti);
        if (this.target.buff(MonkAbility.UnarmedAbilityTracker.class) == null) {
            this.energy = Math.min(this.energy, (float)this.energyCap());
        }
        if (this.energy >= 1.0f && this.cooldown == 0) {
            ActionIndicator.setAction(this);
        }
        BuffIndicator.refreshHero();
    }

    public int energyCap() {
        return Math.max(10, 5 + Dungeon.hero.lvl / 2);
    }

    public void abilityUsed(MonkAbility abil) {
        this.energy -= (float)abil.energyCost();
        this.energy = Math.min(this.energy, (float)this.energyCap());
        if (this.target instanceof Hero && ((Hero)this.target).hasTalent(Talent.COMBINED_ENERGY) && abil.energyCost() >= 5 - ((Hero)this.target).pointsInTalent(Talent.COMBINED_ENERGY)) {
            Talent.CombinedEnergyAbilityTracker tracker = this.target.buff(Talent.CombinedEnergyAbilityTracker.class);
            if (tracker == null || !tracker.wepAbilUsed) {
                Buff.prolong((Char)this.target, Talent.CombinedEnergyAbilityTracker.class, (float)5.0f).monkAbilused = true;
            } else {
                tracker.monkAbilused = true;
                this.processCombinedEnergy(tracker);
            }
        }
        if (this.cooldown > 0 || this.energy < 1.0f) {
            ActionIndicator.clearAction(this);
        } else {
            ActionIndicator.refresh();
        }
        BuffIndicator.refreshHero();
    }

    public boolean abilitiesEmpowered(Hero hero) {
        return this.energy / (float)this.energyCap() >= 1.2f - 0.2f * (float)hero.pointsInTalent(Talent.MONASTIC_VIGOR);
    }

    public void processCombinedEnergy(Talent.CombinedEnergyAbilityTracker tracker) {
        this.energy = Math.min(this.energy + 1.0f, (float)this.energyCap());
        tracker.detach();
        if (this.energy >= 1.0f) {
            ActionIndicator.setAction(this);
        }
        BuffIndicator.refreshHero();
    }

    @Override
    public String actionName() {
        return Messages.get(this, "action", new Object[0]);
    }

    @Override
    public int actionIcon() {
        return 110;
    }

    @Override
    public Visual secondaryVisual() {
        BitmapText txt = new BitmapText(PixelScene.pixelFont);
        txt.text(Integer.toString((int)this.energy));
        txt.hardlight(65280);
        txt.measure();
        return txt;
    }

    @Override
    public int indicatorColor() {
        if (this.abilitiesEmpowered(Dungeon.hero)) {
            return 0xAAEE22;
        }
        return 10520640;
    }

    @Override
    public void doAction() {
        GameScene.show(new WndMonkAbilities(this));
    }

    public static abstract class MonkAbility {
        public static MonkAbility[] abilities = new MonkAbility[]{new Flurry(), new Focus(), new Dash(), new DragonKick(), new Meditate()};

        public String name() {
            return Messages.get(this, "name", new Object[0]);
        }

        public String desc() {
            if (Buff.affect(Dungeon.hero, MonkEnergy.class).abilitiesEmpowered(Dungeon.hero)) {
                return Messages.get(this, "empower_desc", new Object[0]);
            }
            return Messages.get(this, "desc", new Object[0]);
        }

        public abstract int energyCost();

        public boolean usable(MonkEnergy buff) {
            return buff.energy >= (float)this.energyCost();
        }

        public String targetingPrompt() {
            return null;
        }

        public abstract void doAbility(Hero var1, Integer var2);

        public static class Flurry
        extends MonkAbility {
            @Override
            public int energyCost() {
                return 1;
            }

            @Override
            public boolean usable(MonkEnergy buff) {
                return super.usable(buff) && buff.target.buff(FlurryCooldownTracker.class) == null;
            }

            @Override
            public String desc() {
                if (Buff.affect(Dungeon.hero, MonkEnergy.class).abilitiesEmpowered(Dungeon.hero)) {
                    return Messages.get(this, "empower_desc", 2, Math.round(1.5f * (float)(Dungeon.hero.STR() - 8)));
                }
                return Messages.get(this, "desc", 2, Math.round(1.5f * (float)(Dungeon.hero.STR() - 8)));
            }

            @Override
            public String targetingPrompt() {
                return Messages.get(MeleeWeapon.class, "prompt", new Object[0]);
            }

            @Override
            public void doAbility(final Hero hero, Integer target) {
                if (target == null || target == -1) {
                    return;
                }
                final Char enemy = Actor.findChar(target);
                if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_no_target", new Object[0]), new Object[0]);
                    return;
                }
                if (Buff.affect(hero, MonkEnergy.class).abilitiesEmpowered(hero)) {
                    Buff.affect(hero, FlurryEmpowerTracker.class, 0.0f);
                }
                final UnarmedAbilityTracker tracker = Buff.affect(hero, UnarmedAbilityTracker.class);
                if (!hero.canAttack(enemy)) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range", new Object[0]), new Object[0]);
                    tracker.detach();
                    if (hero.buff(FlurryEmpowerTracker.class) != null) {
                        hero.buff(FlurryEmpowerTracker.class).detach();
                    }
                    return;
                }
                hero.sprite.attack(enemy.pos, new Callback(){

                    @Override
                    public void call() {
                        AttackIndicator.target(enemy);
                        hero.attack(enemy, 1.5f, 0.0f, Char.INFINITE_ACCURACY);
                        if (enemy.isAlive()) {
                            hero.sprite.attack(enemy.pos, new Callback(){

                                @Override
                                public void call() {
                                    hero.attack(enemy, 1.5f, 0.0f, Char.INFINITE_ACCURACY);
                                    Invisibility.dispel();
                                    hero.next();
                                    tracker.detach();
                                    Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
                                    if (hero.buff(FlurryEmpowerTracker.class) != null) {
                                        hero.buff(FlurryEmpowerTracker.class).detach();
                                    }
                                    Buff.affect(hero, FlurryCooldownTracker.class, 0.0f);
                                }
                            });
                        } else {
                            Invisibility.dispel();
                            hero.next();
                            tracker.detach();
                            Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
                            if (hero.buff(FlurryEmpowerTracker.class) != null) {
                                hero.buff(FlurryEmpowerTracker.class).detach();
                            }
                            Buff.affect(hero, FlurryCooldownTracker.class, 0.0f);
                        }
                    }
                });
            }
        }

        public static class Focus
        extends MonkAbility {
            @Override
            public int energyCost() {
                return 2;
            }

            @Override
            public boolean usable(MonkEnergy buff) {
                return super.usable(buff) && buff.target.buff(FocusBuff.class) == null;
            }

            @Override
            public void doAbility(Hero hero, Integer target) {
                Buff.affect(hero, FocusBuff.class);
                if (Buff.affect(hero, MonkEnergy.class).abilitiesEmpowered(hero)) {
                    hero.next();
                } else {
                    hero.spendAndNext(1.0f);
                }
                Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
            }

            public static class FocusBuff
            extends Buff {
                public FocusBuff() {
                    this.type = Buff.buffType.POSITIVE;
                    this.announced = true;
                }

                @Override
                public int icon() {
                    return 0;
                }

                @Override
                public void tintIcon(Image icon) {
                    icon.hardlight(0.25f, 1.5f, 1.0f);
                }
            }
        }

        public static class Dash
        extends MonkAbility {
            @Override
            public int energyCost() {
                return 3;
            }

            @Override
            public String targetingPrompt() {
                return Messages.get(this, "prompt", new Object[0]);
            }

            @Override
            public void doAbility(final Hero hero, final Integer target) {
                if (target == null || target == -1) {
                    return;
                }
                int range = 4;
                if (Buff.affect(hero, MonkEnergy.class).abilitiesEmpowered(hero)) {
                    range += 4;
                }
                if (Dungeon.hero.rooted) {
                    PixelScene.shake(1.0f, 1.0f);
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range", new Object[0]), new Object[0]);
                    return;
                }
                if (Dungeon.level.distance(hero.pos, target) > range) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range", new Object[0]), new Object[0]);
                    return;
                }
                if (Actor.findChar(target) != null) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_occupied", new Object[0]), new Object[0]);
                    return;
                }
                Ballistica dash = new Ballistica(hero.pos, target, 7);
                if (!dash.collisionPos.equals(target) || Dungeon.level.solid[target] && !Dungeon.level.passable[target]) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range", new Object[0]), new Object[0]);
                    return;
                }
                hero.busy();
                Sample.INSTANCE.play("sounds/miss.mp3");
                hero.sprite.emitter().start(Speck.factory(106), 0.01f, Math.round(4.0f + 2.0f * Dungeon.level.trueDistance(hero.pos, target)));
                hero.sprite.jump(hero.pos, target, 0.0f, 0.1f, new Callback(){

                    @Override
                    public void call() {
                        if (Dungeon.level.map[hero.pos] == 6) {
                            Door.leave(hero.pos);
                        }
                        hero.pos = target;
                        Dungeon.level.occupyCell(hero);
                        hero.next();
                    }
                });
                Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
            }
        }

        public static class DragonKick
        extends MonkAbility {
            @Override
            public int energyCost() {
                return 4;
            }

            @Override
            public String desc() {
                if (Buff.affect(Dungeon.hero, MonkEnergy.class).abilitiesEmpowered(Dungeon.hero)) {
                    return Messages.get(this, "empower_desc", 9, 9 * (Dungeon.hero.STR() - 8));
                }
                return Messages.get(this, "desc", 6, 6 * (Dungeon.hero.STR() - 8));
            }

            @Override
            public String targetingPrompt() {
                return Messages.get(MeleeWeapon.class, "prompt", new Object[0]);
            }

            @Override
            public void doAbility(final Hero hero, Integer target) {
                if (target == null || target == -1) {
                    return;
                }
                final Char enemy = Actor.findChar(target);
                if (enemy == null || enemy == hero || hero.isCharmedBy(enemy) || !Dungeon.level.heroFOV[target]) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_no_target", new Object[0]), new Object[0]);
                    return;
                }
                final UnarmedAbilityTracker tracker = Buff.affect(hero, UnarmedAbilityTracker.class);
                if (!hero.canAttack(enemy)) {
                    GLog.w(Messages.get(MeleeWeapon.class, "ability_target_range", new Object[0]), new Object[0]);
                    tracker.detach();
                    return;
                }
                hero.sprite.attack(enemy.pos, new Callback(){

                    @Override
                    public void call() {
                        AttackIndicator.target(enemy);
                        boolean empowered = Buff.affect(hero, MonkEnergy.class).abilitiesEmpowered(hero);
                        int oldPos = enemy.pos;
                        if (hero.attack(enemy, empowered ? 9.0f : 6.0f, 0.0f, Char.INFINITE_ACCURACY)) {
                            Sample.INSTANCE.play("sounds/hit_strong.mp3");
                        }
                        if (oldPos == enemy.pos) {
                            Ballistica trajectory = new Ballistica(hero.pos, enemy.pos, 1);
                            trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
                            WandOfBlastWave.throwChar(enemy, trajectory, 6, true, false, hero);
                            if (trajectory.dist > 0 && enemy.isActive()) {
                                Buff.affect(enemy, Paralysis.class, Math.min(6, trajectory.dist));
                            }
                        }
                        Invisibility.dispel();
                        hero.spendAndNext(hero.attackDelay());
                        tracker.detach();
                        Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
                        if (empowered) {
                            for (Char ch : Actor.chars()) {
                                if (ch == enemy || ch.alignment != Char.Alignment.ENEMY || !Dungeon.level.adjacent(ch.pos, hero.pos)) continue;
                                Ballistica trajectory = new Ballistica(hero.pos, ch.pos, 1);
                                trajectory = new Ballistica(trajectory.collisionPos, trajectory.path.get(trajectory.path.size() - 1), 7);
                                WandOfBlastWave.throwChar(ch, trajectory, 6, true, false, hero);
                                if (trajectory.dist <= 0 || !enemy.isActive()) continue;
                                Buff.affect(ch, Paralysis.class, Math.min(6, trajectory.dist));
                            }
                        }
                    }
                });
            }
        }

        public static class Meditate
        extends MonkAbility {
            @Override
            public int energyCost() {
                return 5;
            }

            @Override
            public void doAbility(final Hero hero, Integer target) {
                hero.sprite.operate(hero.pos);
                GameScene.flash(-2013265920, false);
                Sample.INSTANCE.play("sounds/scan.mp3");
                for (Buff b : hero.buffs()) {
                    if (b.type != Buff.buffType.NEGATIVE || b instanceof AllyBuff || b instanceof LostInventory) continue;
                    b.detach();
                }
                for (int i = 0; i < 5; ++i) {
                    hero.spendConstant(1.0f);
                }
                if (Buff.affect(hero, MonkEnergy.class).abilitiesEmpowered(hero)) {
                    int toHeal = Math.round((float)(hero.HT - hero.HP) / 5.0f);
                    if (toHeal > 0) {
                        Buff.affect(hero, Healing.class).setHeal(toHeal, 0.0f, 1);
                    }
                    Buff.affect(hero, MeditateResistance.class, hero.cooldown());
                }
                Actor.addDelayed(new Actor(){
                    {
                        this.actPriority = 100;
                    }

                    @Override
                    protected boolean act() {
                        Buff.affect(hero, Recharging.class, 8.0f);
                        Buff.affect((Char)hero, ArtifactRecharge.class).extend((float)8.0f).ignoreHornOfPlenty = false;
                        Actor.remove(this);
                        return true;
                    }
                }, hero.cooldown() - 1.0f);
                hero.next();
                hero.busy();
                Buff.affect(hero, MonkEnergy.class).abilityUsed(this);
            }

            public static class MeditateResistance
            extends FlavourBuff {
                public MeditateResistance() {
                    this.actPriority = 1;
                }
            }
        }

        public static class FlurryCooldownTracker
        extends FlavourBuff {
        }

        public static class FlurryEmpowerTracker
        extends FlavourBuff {
        }

        public static class UnarmedAbilityTracker
        extends FlavourBuff {
        }
    }
}

