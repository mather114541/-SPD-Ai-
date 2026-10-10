/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.items.weapon;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.NaturesPower;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.LeafParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfSharpshooting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Blindweed;
import com.shatteredpixel.shatteredpixeldungeon.plants.Firebloom;
import com.shatteredpixel.shatteredpixeldungeon.plants.Icecap;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Sorrowmoss;
import com.shatteredpixel.shatteredpixeldungeon.plants.Stormvine;
import com.shatteredpixel.shatteredpixeldungeon.scenes.CellSelector;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.QuickSlotButton;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;

public class SpiritBow
extends Weapon {
    public static final String AC_SHOOT = "SHOOT";
    public boolean sniperSpecial;
    public float sniperSpecialBonusDamage;
    private static Class[] harmfulPlants = new Class[]{Blindweed.class, Firebloom.class, Icecap.class, Sorrowmoss.class, Stormvine.class};
    private int targetPos;
    private CellSelector.Listener shooter;

    public SpiritBow() {
        this.image = ItemSpriteSheet.SPIRIT_BOW;
        this.defaultAction = AC_SHOOT;
        this.usesTargeting = true;
        this.unique = true;
        this.bones = false;
        this.sniperSpecial = false;
        this.sniperSpecialBonusDamage = 0.0f;
        this.shooter = new CellSelector.Listener(){

            @Override
            public void onSelect(Integer target) {
                if (target != null) {
                    SpiritBow.this.knockArrow().cast(curUser, target);
                }
            }

            @Override
            public String prompt() {
                return Messages.get(SpiritBow.class, "prompt", new Object[0]);
            }
        };
    }

    @Override
    public ArrayList<String> actions(Hero hero) {
        ArrayList<String> actions = super.actions(hero);
        actions.remove("EQUIP");
        actions.add(AC_SHOOT);
        return actions;
    }

    @Override
    public void execute(Hero hero, String action) {
        super.execute(hero, action);
        if (action.equals(AC_SHOOT)) {
            curUser = hero;
            curItem = this;
            GameScene.selectCell(this.shooter);
        }
    }

    @Override
    public int proc(final Char attacker, final Char defender, int damage) {
        if (attacker.buff(NaturesPower.naturesPowerTracker.class) != null && !this.sniperSpecial) {
            Actor.add(new Actor(){
                {
                    this.actPriority = 100;
                }

                @Override
                protected boolean act() {
                    NaturesPower.naturesPowerTracker tracker;
                    if (Random.Int(12) < ((Hero)attacker).pointsInTalent(Talent.NATURES_WRATH)) {
                        Plant plant = (Plant)Reflection.newInstance(Random.element(harmfulPlants));
                        plant.pos = defender.pos;
                        plant.activate(defender.isAlive() ? defender : null);
                    }
                    if (!defender.isAlive() && (tracker = attacker.buff(NaturesPower.naturesPowerTracker.class)) != null) {
                        tracker.extend(((Hero)attacker).pointsInTalent(Talent.WILD_MOMENTUM));
                    }
                    Actor.remove(this);
                    return true;
                }
            });
        }
        return super.proc(attacker, defender, damage);
    }

    @Override
    public String info() {
        Object info = super.info();
        info = (String)info + "\n\n" + Messages.get(SpiritBow.class, "stats", Math.round(this.augment.damageFactor(this.min())), Math.round(this.augment.damageFactor(this.max())), this.STRReq());
        if (this.STRReq() > Dungeon.hero.STR()) {
            info = (String)info + " " + Messages.get(Weapon.class, "too_heavy", new Object[0]);
        } else if (Dungeon.hero.STR() > this.STRReq()) {
            info = (String)info + " " + Messages.get(Weapon.class, "excess_str", Dungeon.hero.STR() - this.STRReq());
        }
        switch (this.augment) {
            case SPEED: {
                info = (String)info + "\n\n" + Messages.get(Weapon.class, "faster", new Object[0]);
                break;
            }
            case DAMAGE: {
                info = (String)info + "\n\n" + Messages.get(Weapon.class, "stronger", new Object[0]);
                break;
            }
        }
        if (this.enchantment != null && (this.cursedKnown || !this.enchantment.curse())) {
            info = (String)info + "\n\n" + Messages.capitalize(Messages.get(Weapon.class, "enchanted", this.enchantment.name()));
            if (this.enchantHardened) {
                info = (String)info + " " + Messages.get(Weapon.class, "enchant_hardened", new Object[0]);
            }
            info = (String)info + " " + this.enchantment.desc();
        } else if (this.enchantHardened) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "hardened_no_enchant", new Object[0]);
        }
        if (this.cursed && this.isEquipped(Dungeon.hero)) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "cursed_worn", new Object[0]);
        } else if (this.cursedKnown && this.cursed) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "cursed", new Object[0]);
        } else if (!this.isIdentified() && this.cursedKnown) {
            info = (String)info + "\n\n" + Messages.get(Weapon.class, "not_cursed", new Object[0]);
        }
        info = (String)info + "\n\n" + Messages.get(MissileWeapon.class, "distance", new Object[0]);
        return info;
    }

    @Override
    public int STRReq(int lvl) {
        return SpiritBow.STRReq(1, lvl);
    }

    @Override
    public int min(int lvl) {
        int dmg = 1 + Dungeon.hero.lvl / 5 + RingOfSharpshooting.levelDamageBonus(Dungeon.hero) + (this.curseInfusionBonus ? 1 + Dungeon.hero.lvl / 30 : 0);
        return Math.max(0, dmg);
    }

    @Override
    public int max(int lvl) {
        int dmg = 6 + (int)((float)Dungeon.hero.lvl / 2.5f) + 2 * RingOfSharpshooting.levelDamageBonus(Dungeon.hero) + (this.curseInfusionBonus ? 2 + Dungeon.hero.lvl / 15 : 0);
        return Math.max(0, dmg);
    }

    @Override
    public int targetingPos(Hero user, int dst) {
        return this.knockArrow().targetingPos(user, dst);
    }

    @Override
    public int damageRoll(Char owner) {
        int exStr;
        int damage = this.augment.damageFactor(super.damageRoll(owner));
        if (owner instanceof Hero && (exStr = ((Hero)owner).STR() - this.STRReq()) > 0) {
            damage += Hero.heroDamageIntRange(0, exStr);
        }
        if (this.sniperSpecial) {
            damage = Math.round((float)damage * (1.0f + this.sniperSpecialBonusDamage));
            switch (this.augment) {
                case NONE: {
                    damage = Math.round((float)damage * 0.667f);
                    break;
                }
                case SPEED: {
                    damage = Math.round((float)damage * 0.5f);
                    break;
                }
                case DAMAGE: {
                    int distance = Dungeon.level.distance(owner.pos, this.targetPos) - 1;
                    float multiplier = Math.min(3.0f, 1.2f * (float)Math.pow(1.125, distance));
                    damage = Math.round((float)damage * multiplier);
                }
            }
        }
        return damage;
    }

    @Override
    protected float baseDelay(Char owner) {
        if (this.sniperSpecial) {
            switch (this.augment) {
                default: {
                    return 0.0f;
                }
                case SPEED: {
                    return 1.0f;
                }
                case DAMAGE: 
            }
            return 2.0f;
        }
        return super.baseDelay(owner);
    }

    @Override
    protected float speedMultiplier(Char owner) {
        float speed = super.speedMultiplier(owner);
        if (owner.buff(NaturesPower.naturesPowerTracker.class) != null) {
            speed += (float)(8 + ((Hero)owner).pointsInTalent(Talent.GROWING_POWER)) / 24.0f;
        }
        return speed;
    }

    @Override
    public int level() {
        int level;
        int n = level = Dungeon.hero == null ? 0 : Dungeon.hero.lvl / 5;
        if (this.curseInfusionBonus) {
            level += 1 + level / 6;
        }
        return level;
    }

    @Override
    public int buffedLvl() {
        return this.level();
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    public SpiritArrow knockArrow() {
        return new SpiritArrow();
    }

    public class SpiritArrow
    extends MissileWeapon {
        int flurryCount;
        Actor flurryActor;

        public SpiritArrow() {
            this.image = ItemSpriteSheet.SPIRIT_ARROW;
            this.hitSound = "sounds/hit_arrow.mp3";
            this.setID = 0L;
            this.flurryCount = -1;
            this.flurryActor = null;
        }

        @Override
        public ArrayList<String> actions(Hero hero) {
            return new ArrayList<String>();
        }

        @Override
        public String defaultAction() {
            return null;
        }

        @Override
        public int defaultQuantity() {
            return 1;
        }

        @Override
        public Emitter emitter() {
            if (Dungeon.hero.buff(NaturesPower.naturesPowerTracker.class) != null && !SpiritBow.this.sniperSpecial) {
                Emitter e = new Emitter();
                e.pos(5.0f, 5.0f);
                e.fillTarget = false;
                e.pour(LeafParticle.GENERAL, 0.01f);
                return e;
            }
            return super.emitter();
        }

        @Override
        public int damageRoll(Char owner) {
            return SpiritBow.this.damageRoll(owner);
        }

        @Override
        public boolean hasEnchant(Class<? extends Weapon.Enchantment> type, Char owner) {
            return SpiritBow.this.hasEnchant(type, owner);
        }

        @Override
        public int proc(Char attacker, Char defender, int damage) {
            return SpiritBow.this.proc(attacker, defender, damage);
        }

        @Override
        public float delayFactor(Char user) {
            return SpiritBow.this.delayFactor(user);
        }

        @Override
        public float accuracyFactor(Char owner, Char target) {
            if (SpiritBow.this.sniperSpecial && SpiritBow.this.augment == Weapon.Augment.DAMAGE) {
                return Float.POSITIVE_INFINITY;
            }
            return super.accuracyFactor(owner, target);
        }

        @Override
        public int STRReq(int lvl) {
            return SpiritBow.this.STRReq();
        }

        @Override
        protected void onThrow(int cell) {
            Char enemy = Actor.findChar(cell);
            if (enemy == null || enemy == curUser) {
                this.parent = null;
                Splash.at(cell, -862322689, 1);
            } else {
                if (!curUser.shoot(enemy, this)) {
                    Splash.at(cell, -862322689, 1);
                }
                if (SpiritBow.this.sniperSpecial && SpiritBow.this.augment != Weapon.Augment.SPEED) {
                    SpiritBow.this.sniperSpecial = false;
                }
            }
        }

        @Override
        public Item split(int amount) {
            return null;
        }

        @Override
        public void throwSound() {
            Sample.INSTANCE.play("sounds/atk_spiritbow.mp3", 1.0f, Random.Float(0.87f, 1.15f));
        }

        @Override
        public void cast(final Hero user, int dst) {
            int cell;
            SpiritBow.this.targetPos = cell = this.throwPos(user, dst);
            if (SpiritBow.this.sniperSpecial && SpiritBow.this.augment == Weapon.Augment.SPEED) {
                Char enemy;
                if (this.flurryCount == -1) {
                    this.flurryCount = 3;
                }
                if ((enemy = Actor.findChar(cell)) == null) {
                    if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                        user.buff(Talent.LethalMomentumTracker.class).detach();
                        user.next();
                    } else {
                        user.spendAndNext(this.castDelay(user, cell));
                    }
                    SpiritBow.this.sniperSpecial = false;
                    this.flurryCount = -1;
                    if (this.flurryActor != null) {
                        this.flurryActor.next();
                        this.flurryActor = null;
                    }
                    return;
                }
                QuickSlotButton.target(enemy);
                user.busy();
                this.throwSound();
                user.sprite.zap(cell);
                ((MissileSprite)user.sprite.parent.recycle(MissileSprite.class)).reset((Visual)user.sprite, cell, (Item)this, new Callback(){

                    @Override
                    public void call() {
                        if (enemy.isAlive()) {
                            curUser = user;
                            SpiritArrow.this.onThrow(cell);
                        }
                        --SpiritArrow.this.flurryCount;
                        if (SpiritArrow.this.flurryCount > 0) {
                            Actor.add(new Actor(){
                                {
                                    this.actPriority = 99;
                                }

                                @Override
                                protected boolean act() {
                                    SpiritArrow.this.flurryActor = this;
                                    int target = QuickSlotButton.autoAim(enemy, SpiritArrow.this);
                                    if (target == -1) {
                                        target = cell;
                                    }
                                    SpiritArrow.this.cast(user, target);
                                    Actor.remove(this);
                                    return false;
                                }
                            });
                            curUser.next();
                        } else {
                            if (user.buff(Talent.LethalMomentumTracker.class) != null) {
                                user.buff(Talent.LethalMomentumTracker.class).detach();
                                user.next();
                            } else {
                                user.spendAndNext(SpiritArrow.this.castDelay(user, cell));
                            }
                            SpiritBow.this.sniperSpecial = false;
                            SpiritArrow.this.flurryCount = -1;
                        }
                        if (SpiritArrow.this.flurryActor != null) {
                            SpiritArrow.this.flurryActor.next();
                            SpiritArrow.this.flurryActor = null;
                        }
                    }
                });
            } else {
                int shotPos;
                if (user.hasTalent(Talent.SEER_SHOT) && user.buff(Talent.SeerShotCooldown.class) == null && Actor.findChar(shotPos = this.throwPos(user, dst)) == null) {
                    RevealedArea a = Buff.affect(user, RevealedArea.class, 5 * user.pointsInTalent(Talent.SEER_SHOT));
                    a.depth = Dungeon.depth;
                    a.branch = Dungeon.branch;
                    a.pos = shotPos;
                    Buff.affect(user, Talent.SeerShotCooldown.class, 20.0f);
                }
                super.cast(user, dst);
            }
        }
    }
}

