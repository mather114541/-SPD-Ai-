/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Bleeding;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.CounterBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.FlavourBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hex;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Poison;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.RainbowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindOfWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.WondrousResin;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.CursedWand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Annoying;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Dazzling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Displacing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Explosive;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Friendly;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Polarized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Pressurized;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Sacrificial;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wayward;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.curses.Wondrous;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blazing;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Blooming;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Chilling;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Corrupting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Crystal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Elastic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Eldritch;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Kinetic;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Projecting;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Shocking;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Unstable;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vampiric;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Venomous;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Vorpal;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.AttackIndicator;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

public class ElementalStrike
extends ArmorAbility {
    private static final HashMap<Class<? extends Weapon.Enchantment>, Integer> effectTypes = new HashMap();
    private int storedKineticDamage;
    private int oldEnemyPos;

    public ElementalStrike() {
        this.baseChargeUse = 25.0f;
        this.storedKineticDamage = 0;
    }

    @Override
    public String targetingPrompt() {
        return Messages.get(this, "prompt", new Object[0]);
    }

    @Override
    public int targetedPos(Char user, int dst) {
        return dst;
    }

    @Override
    protected void activate(ClassArmor armor, final Hero hero, final Integer target) {
        if (target == null) {
            return;
        }
        armor.charge -= this.chargeUse(hero);
        Item.updateQuickslot();
        Ballistica aim = new Ballistica(hero.pos, target, 0);
        int maxDist = 4 + hero.pointsInTalent(Talent.ELEMENTAL_REACH);
        int dist = Math.min(aim.dist, maxDist);
        final ConeAOE cone = new ConeAOE(aim, dist, 65 + 10 * hero.pointsInTalent(Talent.ELEMENTAL_REACH), 5);
        KindOfWeapon w = hero.belongings.weapon();
        Weapon.Enchantment enchantment = null;
        if (w instanceof MeleeWeapon) {
            enchantment = ((MeleeWeapon)w).enchantment;
        }
        Class<?> enchCls = null;
        if (enchantment != null) {
            enchCls = enchantment.getClass();
        }
        for (Ballistica ray : cone.outerRays) {
            ((MagicMissile)hero.sprite.parent.recycle(MagicMissile.class)).reset((int)effectTypes.get(enchCls), (Visual)hero.sprite, (int)ray.path.get(ray.dist), null);
        }
        final Weapon.Enchantment finalEnchantment = enchantment;
        hero.sprite.attack(target, new Callback(){

            @Override
            public void call() {
                Char enemy = Actor.findChar(target);
                if (enemy != null) {
                    if (hero.isCharmedBy(enemy)) {
                        enemy = null;
                    } else if (enemy.alignment == hero.alignment) {
                        enemy = null;
                    } else if (!hero.canAttack(enemy)) {
                        enemy = null;
                    }
                }
                ElementalStrike.this.preAttackEffect(cone, hero, finalEnchantment);
                if (enemy != null) {
                    AttackIndicator.target(enemy);
                    ElementalStrike.this.oldEnemyPos = enemy.pos;
                    if (hero.attack(enemy, 1.0f, 0.0f, Char.INFINITE_ACCURACY)) {
                        Sample.INSTANCE.play("sounds/hit_strong.mp3");
                    }
                }
                ElementalStrike.this.perCellEffect(cone, finalEnchantment);
                ElementalStrike.this.perCharEffect(cone, hero, enemy, finalEnchantment);
                Invisibility.dispel();
                hero.spendAndNext(hero.attackDelay());
            }
        });
        Sample.INSTANCE.play("sounds/chargeup.mp3");
        hero.busy();
    }

    private void preAttackEffect(ConeAOE cone, Hero hero, Weapon.Enchantment ench) {
        int targetsHit = 0;
        for (Char ch : Actor.chars()) {
            if (ch.alignment != Char.Alignment.ENEMY || !cone.cells.contains(ch.pos)) continue;
            ++targetsHit;
        }
        if (hero.hasTalent(Talent.DIRECTED_POWER)) {
            float enchBoost;
            Buff.affect((Char)hero, DirectedPowerTracker.class, (float)0.0f).enchBoost = enchBoost = 0.3f * (float)targetsHit * (float)hero.pointsInTalent(Talent.DIRECTED_POWER);
        }
        float powerMulti = 1.0f + 0.3f * (float)Dungeon.hero.pointsInTalent(Talent.STRIKING_FORCE);
        if (ench instanceof Kinetic) {
            if (hero.buff(Kinetic.ConservedDamage.class) != null) {
                this.storedKineticDamage = hero.buff(Kinetic.ConservedDamage.class).damageBonus();
            }
        } else if (ench instanceof Blocking) {
            if (targetsHit > 0) {
                int shield = Math.round(Math.round(6.0f * (float)targetsHit * powerMulti));
                Buff.affect(hero, Barrier.class).setShield(Math.round(6.0f * (float)targetsHit * powerMulti));
                hero.sprite.showStatusWithIcon(65280, Integer.toString(shield), FloatingText.SHIELDING, new Object[0]);
            }
        } else if (ench instanceof Vampiric) {
            if (targetsHit > 0) {
                int heal = Math.round(2.5f * (float)targetsHit * powerMulti);
                if ((heal = Math.min(heal, hero.HT - hero.HP)) > 0) {
                    hero.HP += heal;
                    hero.sprite.showStatusWithIcon(65280, Integer.toString(heal), FloatingText.HEALING, new Object[0]);
                }
            }
        } else if (ench instanceof Sacrificial) {
            Buff.affect(hero, Bleeding.class).set(10.0f * powerMulti);
        }
    }

    private void perCellEffect(ConeAOE cone, Weapon.Enchantment ench) {
        block13: {
            float powerMulti;
            block16: {
                int targetsHit;
                block15: {
                    block14: {
                        block12: {
                            targetsHit = 0;
                            for (Char char_ : Actor.chars()) {
                                if (char_.alignment != Char.Alignment.ENEMY || !cone.cells.contains(char_.pos)) continue;
                                ++targetsHit;
                            }
                            powerMulti = 1.0f + 0.3f * (float)Dungeon.hero.pointsInTalent(Talent.STRIKING_FORCE);
                            if (!(ench instanceof Blazing)) break block12;
                            for (int cell : cone.cells) {
                                GameScene.add(Blob.seed(cell, Math.round(8.0f * powerMulti), Fire.class));
                            }
                            break block13;
                        }
                        if (!(ench instanceof Chilling)) break block14;
                        for (int cell : cone.cells) {
                            GameScene.add(Blob.seed(cell, Math.round(8.0f * powerMulti), Freezing.class));
                        }
                        break block13;
                    }
                    if (!(ench instanceof Shocking)) break block15;
                    for (int cell : cone.cells) {
                        GameScene.add(Blob.seed(cell, Math.round(8.0f * powerMulti), Electricity.class));
                    }
                    break block13;
                }
                if (!(ench instanceof Blooming)) break block16;
                ArrayList<Integer> arrayList = new ArrayList<Integer>(cone.cells);
                Random.shuffle(arrayList);
                int grassToPlace = Math.round(8.0f * powerMulti);
                int highGrassType = 15;
                if (Buff.affect(Dungeon.hero, ElementalStrikeFurrowCounter.class).count() >= 40.0f) {
                    highGrassType = 30;
                } else if (Dungeon.hero.visibleEnemies() == 0 && targetsHit == 0) {
                    Buff.count(Dungeon.hero, ElementalStrikeFurrowCounter.class, 4.0f);
                } else {
                    Buff.count(Dungeon.hero, ElementalStrikeFurrowCounter.class, 1.0f);
                }
                for (int cell : arrayList) {
                    int terr = Dungeon.level.map[cell];
                    if (terr != 1 && terr != 9 && terr != 20 && terr != 2) continue;
                    if (grassToPlace > 0 && !Char.hasProp(Actor.findChar(cell), Char.Property.IMMOVABLE) && Dungeon.level.plants.get(cell) == null) {
                        Level.set(cell, highGrassType);
                        --grassToPlace;
                    } else {
                        Level.set(cell, 2);
                    }
                    GameScene.updateMap(cell);
                }
                Dungeon.observe();
                break block13;
            }
            if (!(ench instanceof Pressurized)) break block13;
            for (int cell : cone.cells) {
                if (!(Random.Float() < powerMulti / 2.0f)) continue;
                Splash.at(cell, 6013411, 5);
                Dungeon.level.setCellToWater(true, cell);
            }
        }
    }

    private void perCharEffect(ConeAOE cone, final Hero hero, Char primaryTarget, Weapon.Enchantment ench) {
        block38: {
            ArrayList<Char> affected;
            float powerMulti;
            block54: {
                Iterator exploding;
                block53: {
                    block52: {
                        block51: {
                            block50: {
                                block49: {
                                    block48: {
                                        block47: {
                                            block46: {
                                                block45: {
                                                    block44: {
                                                        block43: {
                                                            block42: {
                                                                block41: {
                                                                    block40: {
                                                                        block39: {
                                                                            block37: {
                                                                                powerMulti = 1.0f + 0.3f * (float)Dungeon.hero.pointsInTalent(Talent.STRIKING_FORCE);
                                                                                affected = new ArrayList<Char>();
                                                                                for (Char char_ : Actor.chars()) {
                                                                                    if (char_.alignment == Char.Alignment.ALLY || !cone.cells.contains(char_.pos)) continue;
                                                                                    affected.add(char_);
                                                                                }
                                                                                if (ench != null) break block37;
                                                                                for (Char char_ : affected) {
                                                                                    char_.damage(Math.round(powerMulti * (float)Hero.heroDamageIntRange(6, 12)), this);
                                                                                }
                                                                                break block38;
                                                                            }
                                                                            if (!(ench instanceof Kinetic)) break block39;
                                                                            if (this.storedKineticDamage > 0) {
                                                                                for (Char char_ : affected) {
                                                                                    if (char_ == primaryTarget) continue;
                                                                                    char_.damage(Math.round((float)this.storedKineticDamage * 0.4f * powerMulti), ench);
                                                                                }
                                                                                this.storedKineticDamage = 0;
                                                                            }
                                                                            if (primaryTarget != null || hero.buff(Kinetic.ConservedDamage.class) == null) break block38;
                                                                            hero.buff(Kinetic.ConservedDamage.class).detach();
                                                                            break block38;
                                                                        }
                                                                        if (!(ench instanceof Venomous)) break block40;
                                                                        for (Char char_ : affected) {
                                                                            if (char_ == primaryTarget) continue;
                                                                            Poison p = char_.buff(Poison.class);
                                                                            if (p == null) {
                                                                                p = Buff.affect(char_, Poison.class);
                                                                                p.delay(3.0f);
                                                                            }
                                                                            p.extend(powerMulti * 10.0f);
                                                                        }
                                                                        break block38;
                                                                    }
                                                                    if (!(ench instanceof Blooming)) break block41;
                                                                    for (Char char_ : affected) {
                                                                        Buff.affect(char_, Roots.class, Math.round(6.0f * powerMulti));
                                                                    }
                                                                    break block38;
                                                                }
                                                                if (!(ench instanceof Elastic)) break block42;
                                                                Collections.sort(affected, new Comparator<Char>(){

                                                                    @Override
                                                                    public int compare(Char a, Char b) {
                                                                        return Dungeon.level.distance(hero.pos, a.pos) - Dungeon.level.distance(hero.pos, b.pos);
                                                                    }
                                                                });
                                                                for (Char char_ : affected) {
                                                                    if (char_ == primaryTarget && this.oldEnemyPos != primaryTarget.pos) continue;
                                                                    Ballistica aim = new Ballistica(hero.pos, char_.pos, 0);
                                                                    int knockback = Math.round(5.0f * powerMulti);
                                                                    WandOfBlastWave.throwChar(char_, new Ballistica(char_.pos, aim.collisionPos, 6), knockback, true, true, this);
                                                                }
                                                                break block38;
                                                            }
                                                            if (!(ench instanceof Eldritch)) break block43;
                                                            for (Char char_ : affected) {
                                                                Buff.affect((Char)char_, Terror.class, (float)(powerMulti * 10.0f)).object = hero.id();
                                                            }
                                                            break block38;
                                                        }
                                                        if (!(ench instanceof Lucky)) break block44;
                                                        for (Char char_ : affected) {
                                                            if (char_.alignment != Char.Alignment.ENEMY || !(Random.Float() < 0.125f * powerMulti) || char_.buff(ElementalStrikeLuckyTracker.class) != null) continue;
                                                            Dungeon.level.drop((Item)Lucky.genLoot(), (int)char_.pos).sprite.drop();
                                                            Lucky.showFlare(char_.sprite);
                                                            Buff.affect(char_, ElementalStrikeLuckyTracker.class);
                                                        }
                                                        break block38;
                                                    }
                                                    if (!(ench instanceof Projecting)) break block45;
                                                    for (Char char_ : affected) {
                                                        if (char_ == primaryTarget) continue;
                                                        char_.damage(Math.round((float)hero.damageRoll() * 0.3f * powerMulti), ench);
                                                    }
                                                    break block38;
                                                }
                                                if (!(ench instanceof Unstable)) break block46;
                                                KindOfWeapon w = hero.belongings.weapon();
                                                if (!(w instanceof Weapon)) break block38;
                                                for (Char ch : affected) {
                                                    if (ch == primaryTarget) continue;
                                                    ench.proc((Weapon)w, hero, ch, w.damageRoll(hero));
                                                }
                                                break block38;
                                            }
                                            if (!(ench instanceof Vorpal)) break block47;
                                            for (Char char_ : affected) {
                                                if (char_ == primaryTarget) continue;
                                                Buff.affect(char_, Bleeding.class).set(powerMulti * 10.0f);
                                            }
                                            break block38;
                                        }
                                        if (!(ench instanceof Corrupting)) break block48;
                                        for (Char char_ : affected) {
                                            if (char_ == primaryTarget || char_.isImmune(Corruption.class) || char_.buff(Corruption.class) != null || !(char_ instanceof Mob) || !char_.isAlive()) continue;
                                            float hpMissing = 1.0f - (float)char_.HP / (float)char_.HT;
                                            float chance = 0.05f + 0.2f * hpMissing;
                                            if (!(Random.Float() < chance * powerMulti)) continue;
                                            Corruption.corruptionHeal(char_);
                                            AllyBuff.affectAndLoot((Mob)char_, hero, Corruption.class);
                                        }
                                        break block38;
                                    }
                                    if (!(ench instanceof Crystal)) break block49;
                                    for (Char char_ : affected) {
                                        if (char_ == primaryTarget) continue;
                                        char_.damage(Math.round(powerMulti * (float)Hero.heroDamageIntRange(10, 20)), this);
                                        ((Crystal)ench).repair(null, false, 4.0f * powerMulti);
                                    }
                                    break block38;
                                }
                                if (!(ench instanceof Grim)) break block50;
                                for (Char char_ : affected) {
                                    if (char_ == primaryTarget) continue;
                                    float hpMissing = 1.0f - (float)char_.HP / (float)char_.HT;
                                    float chance = 0.06f + 0.24f * hpMissing;
                                    if (!(Random.Float() < chance * powerMulti)) continue;
                                    char_.damage(char_.HP, Grim.class);
                                    char_.sprite.emitter().burst(ShadowParticle.UP, 5);
                                }
                                break block38;
                            }
                            if (!(ench instanceof Annoying)) break block51;
                            for (Char char_ : affected) {
                                if (!(Random.Float() < 0.2f * powerMulti)) continue;
                                Buff.affect(char_, Amok.class, 6.0f);
                            }
                            break block38;
                        }
                        if (!(ench instanceof Displacing)) break block52;
                        for (Char char_ : affected) {
                            if (!(Random.Float() < 0.5f * powerMulti)) continue;
                            int oldpos = char_.pos;
                            if (!ScrollOfTeleportation.teleportChar(char_)) continue;
                            if (Dungeon.level.heroFOV[oldpos]) {
                                CellEmitter.get(oldpos).start(Speck.factory(2), 0.2f, 3);
                            }
                            if (!(char_ instanceof Mob) || ((Mob)char_).state != ((Mob)char_).HUNTING) continue;
                            ((Mob)char_).state = ((Mob)char_).WANDERING;
                        }
                        break block38;
                    }
                    if (!(ench instanceof Dazzling)) break block53;
                    for (Char char_ : affected) {
                        if (!(Random.Float() < 0.5f * powerMulti)) continue;
                        Buff.affect(char_, Blindness.class, 6.0f);
                    }
                    break block38;
                }
                if (!(ench instanceof Explosive)) break block54;
                if (!(Random.Float() < 0.5f * powerMulti) || (exploding = (Char)Random.element(affected)) == null) break block38;
                new Bomb.ConjuredBomb().explode(((Char)((Object)exploding)).pos);
                break block38;
            }
            if (ench instanceof Sacrificial) {
                for (Char char_ : affected) {
                    Buff.affect(char_, Bleeding.class).set(12.0f * powerMulti);
                }
            } else if (ench instanceof Wayward) {
                for (Char char_ : affected) {
                    if (!(Random.Float() < 0.5f * powerMulti)) continue;
                    Buff.affect(char_, Hex.class, 6.0f);
                }
            } else if (ench instanceof Polarized) {
                for (Char char_ : affected) {
                    if (!(Random.Float() < 0.5f * powerMulti)) continue;
                    char_.damage(Hero.heroDamageIntRange(24, 36), this);
                }
            } else if (ench instanceof Friendly) {
                for (Char char_ : affected) {
                    if (!(Random.Float() < 0.5f * powerMulti)) continue;
                    Buff.affect((Char)char_, Charm.class, (float)6.0f).object = hero.id();
                }
            } else if (ench instanceof Wondrous) {
                boolean positiveOnly = Random.Float() < WondrousResin.positiveCurseEffectChance();
                for (Char ch : affected) {
                    if (!(Random.Float() < powerMulti / 2.0f)) continue;
                    Ballistica aim = new Ballistica(hero.pos, ch.pos, 1);
                    ch.sprite.emitter().burst(RainbowParticle.BURST, 25);
                    CursedWand.randomValidEffect(null, hero, aim, positiveOnly).effect(null, hero, aim, positiveOnly);
                }
            } else if (ench instanceof Pressurized) {
                Collections.sort(affected, new Comparator<Char>(){

                    @Override
                    public int compare(Char a, Char b) {
                        return Dungeon.level.distance(hero.pos, a.pos) - Dungeon.level.distance(hero.pos, b.pos);
                    }
                });
                for (Char char_ : affected) {
                    if (char_ == primaryTarget && this.oldEnemyPos != primaryTarget.pos) continue;
                    Ballistica aim = new Ballistica(hero.pos, char_.pos, 0);
                    int knockback = Math.round(2.0f * powerMulti);
                    WandOfBlastWave.throwChar(char_, new Ballistica(char_.pos, aim.collisionPos, 6), knockback, true, true, this);
                }
            }
        }
    }

    @Override
    public String desc() {
        KindOfWeapon w;
        Object desc = Messages.get(this, "desc", new Object[0]);
        desc = Game.scene() instanceof GameScene ? ((w = Dungeon.hero.belongings.weapon()) instanceof MeleeWeapon && ((MeleeWeapon)w).enchantment != null ? (String)desc + "\n\n" + Messages.get(((MeleeWeapon)w).enchantment, "elestrike_desc", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "generic_desc", new Object[0])) : (String)desc + "\n\n" + Messages.get(this, "generic_desc", new Object[0]);
        desc = (String)desc + "\n\n" + Messages.get(this, "cost", (int)this.baseChargeUse);
        return desc;
    }

    @Override
    public int icon() {
        return 29;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.ELEMENTAL_REACH, Talent.STRIKING_FORCE, Talent.DIRECTED_POWER, Talent.HEROIC_ENERGY};
    }

    static {
        effectTypes.put(Blazing.class, 102);
        effectTypes.put(Chilling.class, 101);
        effectTypes.put(Kinetic.class, 105);
        effectTypes.put(Shocking.class, 112);
        effectTypes.put(Venomous.class, 114);
        effectTypes.put(Blocking.class, 110);
        effectTypes.put(Blooming.class, 104);
        effectTypes.put(Eldritch.class, 107);
        effectTypes.put(Elastic.class, 105);
        effectTypes.put(Lucky.class, 108);
        effectTypes.put(Projecting.class, 111);
        effectTypes.put(Unstable.class, 108);
        effectTypes.put(Vorpal.class, 113);
        effectTypes.put(Corrupting.class, 107);
        effectTypes.put(Crystal.class, 1002);
        effectTypes.put(Grim.class, 107);
        effectTypes.put(Vampiric.class, 113);
        effectTypes.put(Annoying.class, 107);
        effectTypes.put(Displacing.class, 107);
        effectTypes.put(Dazzling.class, 107);
        effectTypes.put(Explosive.class, 107);
        effectTypes.put(Friendly.class, 107);
        effectTypes.put(Polarized.class, 107);
        effectTypes.put(Pressurized.class, 107);
        effectTypes.put(Sacrificial.class, 107);
        effectTypes.put(Wayward.class, 107);
        effectTypes.put(Wondrous.class, 107);
        effectTypes.put(null, 100);
    }

    public static class DirectedPowerTracker
    extends FlavourBuff {
        public float enchBoost = 0.0f;
    }

    public static class ElementalStrikeFurrowCounter
    extends CounterBuff {
        public ElementalStrikeFurrowCounter() {
            this.revivePersists = true;
        }
    }

    public static class ElementalStrikeLuckyTracker
    extends Buff {
    }
}

