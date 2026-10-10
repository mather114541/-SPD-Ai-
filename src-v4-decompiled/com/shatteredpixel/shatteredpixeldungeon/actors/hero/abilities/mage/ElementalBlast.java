/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.mage;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Fire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Freezing;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Recharging;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.MagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.SpellSprite;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClassArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfMagicMapping;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorrosion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfCorruption;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfDisintegration;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFireblast;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfFrost;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLivingEarth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfMagicMissile;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfPrismaticLight;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfTransfusion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MagesStaff;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Visual;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.HashMap;
import java.util.Iterator;

public class ElementalBlast
extends ArmorAbility {
    private static final HashMap<Class<? extends Wand>, Integer> effectTypes = new HashMap();
    private static final HashMap<Class<? extends Wand>, Float> damageFactors;

    public ElementalBlast() {
        this.baseChargeUse = 35.0f;
    }

    @Override
    protected void activate(ClassArmor armor, final Hero hero, Integer target) {
        int x = hero.pos % Dungeon.level.width();
        int y = hero.pos / Dungeon.level.width();
        Ballistica aim = Math.max(x, Dungeon.level.width() - x) >= Math.max(y, Dungeon.level.height() - y) ? (x > Dungeon.level.width() / 2 ? new Ballistica(hero.pos, hero.pos - 1, 0) : new Ballistica(hero.pos, hero.pos + 1, 0)) : (y > Dungeon.level.height() / 2 ? new Ballistica(hero.pos, hero.pos - Dungeon.level.width(), 0) : new Ballistica(hero.pos, hero.pos + Dungeon.level.width(), 0));
        Class<? extends Wand> wandCls = null;
        if (hero.belongings.getItem(MagesStaff.class) != null) {
            wandCls = hero.belongings.getItem(MagesStaff.class).wandClass();
        }
        if (wandCls == null) {
            GLog.w(Messages.get(this, "no_staff", new Object[0]), new Object[0]);
            return;
        }
        final int aoeSize = 4 + hero.pointsInTalent(Talent.BLAST_RADIUS);
        int projectileProps = 5;
        if (wandCls == WandOfDisintegration.class) {
            projectileProps = 1;
        } else if (wandCls == WandOfFireblast.class) {
            projectileProps |= 8;
        } else if (wandCls == WandOfWarding.class) {
            projectileProps = 1;
        }
        final ConeAOE aoe = new ConeAOE(aim, aoeSize, 360.0f, projectileProps);
        for (Ballistica ray : aoe.outerRays) {
            ((MagicMissile)hero.sprite.parent.recycle(MagicMissile.class)).reset((int)effectTypes.get(wandCls), (Visual)hero.sprite, (int)ray.path.get(ray.dist), null);
        }
        final float effectMulti = 1.0f + 0.25f * (float)hero.pointsInTalent(Talent.ELEMENTAL_POWER);
        final Class<? extends Wand> finalWandCls = wandCls;
        ((MagicMissile)hero.sprite.parent.recycle(MagicMissile.class)).reset((int)effectTypes.get(wandCls), (Visual)hero.sprite, (int)aim.path.get(Math.min(aoeSize / 2, aim.path.size() - 1)), new Callback(){

            @Override
            public void call() {
                int charsHit = 0;
                Freezing freeze = (Freezing)Dungeon.level.blobs.get(Freezing.class);
                Fire fire = (Fire)Dungeon.level.blobs.get(Fire.class);
                Iterator<Object> iterator = aoe.cells.iterator();
                while (iterator.hasNext()) {
                    int cell = iterator.next();
                    if (finalWandCls == WandOfLightning.class) {
                        if (Dungeon.level.water[cell]) {
                            GameScene.add(Blob.seed(cell, 4, Electricity.class));
                        }
                    } else if (finalWandCls == WandOfFireblast.class) {
                        if (Dungeon.level.map[cell] == 5) {
                            Level.set(cell, 6);
                            GameScene.updateMap(cell);
                        }
                        if (freeze != null) {
                            freeze.clear(cell);
                        }
                        if (Dungeon.level.flamable[cell]) {
                            GameScene.add(Blob.seed(cell, 4, Fire.class));
                        }
                    } else if (finalWandCls == WandOfFrost.class) {
                        if (fire != null) {
                            fire.clear(cell);
                        }
                    } else if (finalWandCls == WandOfPrismaticLight.class) {
                        for (int n : PathFinder.NEIGHBOURS9) {
                            int terr;
                            int c = cell + n;
                            if (Dungeon.level.discoverable[c]) {
                                Dungeon.level.mapped[c] = true;
                            }
                            if ((Terrain.flags[terr = Dungeon.level.map[c]] & 8) == 0) continue;
                            Dungeon.level.discover(c);
                            GameScene.discoverTile(c, terr);
                            ScrollOfMagicMapping.discover(c);
                        }
                    } else if (finalWandCls == WandOfRegrowth.class) {
                        int t = Dungeon.level.map[cell];
                        if (Random.Float() < 0.33f * effectMulti && (t == 1 || t == 20 || t == 9 || t == 2 || t == 30) && Dungeon.level.plants.get(cell) == null) {
                            Level.set(cell, 15);
                            GameScene.updateMap(cell);
                        }
                    }
                    Char mob = Actor.findChar(cell);
                    int damage = Math.round((float)Hero.heroDamageIntRange(15, 25) * effectMulti * damageFactors.get(finalWandCls).floatValue());
                    if (mob != null && damage > 0 && mob.alignment != Char.Alignment.ALLY) {
                        mob.damage(damage, Reflection.newInstance(finalWandCls));
                        ++charsHit;
                    }
                    if (mob == null || mob == hero) continue;
                    if (finalWandCls == WandOfLightning.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.affect(mob, Paralysis.class, effectMulti * 10.0f / 2.0f);
                        continue;
                    }
                    if (finalWandCls == WandOfFireblast.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.affect(mob, Burning.class).reignite(mob);
                        continue;
                    }
                    if (finalWandCls == WandOfCorrosion.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.affect(mob, Corrosion.class).set(4.0f, Math.round(6.0f * effectMulti));
                        ++charsHit;
                        continue;
                    }
                    if (finalWandCls == WandOfBlastWave.class) {
                        if (mob.alignment == Char.Alignment.ALLY) continue;
                        Ballistica aim = new Ballistica(hero.pos, mob.pos, 0);
                        int knockback = aoeSize + 1 - (int)Dungeon.level.trueDistance(hero.pos, mob.pos);
                        knockback = (int)((float)knockback * effectMulti);
                        WandOfBlastWave.throwChar(mob, new Ballistica(mob.pos, aim.collisionPos, 6), knockback, true, true, ElementalBlast.this);
                        continue;
                    }
                    if (finalWandCls == WandOfFrost.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.affect(mob, Frost.class, effectMulti * 10.0f);
                        continue;
                    }
                    if (finalWandCls == WandOfPrismaticLight.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.prolong(mob, Blindness.class, effectMulti * 10.0f / 2.0f);
                        ++charsHit;
                        continue;
                    }
                    if (finalWandCls == WandOfWarding.class) {
                        if (!(mob instanceof WandOfWarding.Ward)) continue;
                        ((WandOfWarding.Ward)mob).wandHeal(0, effectMulti);
                        ++charsHit;
                        continue;
                    }
                    if (finalWandCls == WandOfTransfusion.class) {
                        if (mob.alignment == Char.Alignment.ALLY || mob.buff(Charm.class) != null) {
                            int healing = Math.round(10.0f * effectMulti);
                            int shielding = mob.HP + healing - mob.HT;
                            if (shielding > 0) {
                                healing -= shielding;
                                Buff.affect(mob, Barrier.class).setShield(shielding);
                            } else {
                                shielding = 0;
                            }
                            mob.HP += healing;
                            mob.sprite.emitter().burst(Speck.factory(0), 4);
                            if (healing > 0) {
                                mob.sprite.showStatusWithIcon(65280, Integer.toString(healing), FloatingText.HEALING, new Object[0]);
                            }
                            if (shielding > 0) {
                                mob.sprite.showStatusWithIcon(65280, Integer.toString(shielding), FloatingText.SHIELDING, new Object[0]);
                            }
                        } else if (!mob.properties().contains((Object)Char.Property.UNDEAD)) {
                            Charm charm = Buff.affect(mob, Charm.class, effectMulti * 10.0f / 2.0f);
                            charm.object = hero.id();
                            charm.ignoreHeroAllies = true;
                            mob.sprite.centerEmitter().start(Speck.factory(11), 0.2f, 3);
                        } else {
                            damage = Math.round((float)Hero.heroDamageIntRange(15, 25) * effectMulti);
                            mob.damage(damage, Reflection.newInstance(finalWandCls));
                            mob.sprite.emitter().start(ShadowParticle.UP, 0.05f, 10);
                        }
                        ++charsHit;
                        continue;
                    }
                    if (finalWandCls == WandOfCorruption.class) {
                        if (!mob.isAlive() || mob.alignment == Char.Alignment.ALLY) continue;
                        Buff.prolong(mob, Amok.class, effectMulti * 5.0f);
                        ++charsHit;
                        continue;
                    }
                    if (finalWandCls != WandOfRegrowth.class || mob.alignment == Char.Alignment.ALLY) continue;
                    Buff.prolong(mob, Roots.class, effectMulti * 5.0f);
                    ++charsHit;
                }
                if (finalWandCls == WandOfMagicMissile.class) {
                    Buff.affect(hero, Recharging.class, effectMulti * 30.0f / 2.0f);
                    SpellSprite.show(hero, 2);
                } else if (finalWandCls == WandOfLivingEarth.class && charsHit > 0) {
                    for (Mob m : Dungeon.level.mobs) {
                        if (!(m instanceof WandOfLivingEarth.EarthGuardian)) continue;
                        ((WandOfLivingEarth.EarthGuardian)m).setInfo(hero, 0, Math.round(effectMulti * (float)charsHit * 5.0f));
                        m.sprite.centerEmitter().burst(MagicMissile.EarthParticle.ATTRACT, 8 + charsHit);
                        break;
                    }
                } else if (finalWandCls == WandOfFrost.class) {
                    if (hero.buff(Burning.class) != null) {
                        hero.buff(Burning.class).detach();
                    }
                } else if (finalWandCls == WandOfPrismaticLight.class) {
                    if (Dungeon.isChallenged(32)) {
                        Buff.prolong(hero, Light.class, effectMulti * 10.0f);
                    } else {
                        Buff.prolong(hero, Light.class, effectMulti * 50.0f);
                    }
                }
                charsHit = Math.min(4 + hero.pointsInTalent(Talent.REACTIVE_BARRIER), charsHit);
                if (charsHit > 0 && hero.hasTalent(Talent.REACTIVE_BARRIER)) {
                    int shielding = Math.round((float)charsHit * 2.5f * (float)hero.pointsInTalent(Talent.REACTIVE_BARRIER));
                    hero.sprite.showStatusWithIcon(65280, Integer.toString(shielding), FloatingText.SHIELDING, new Object[0]);
                    Buff.affect(hero, Barrier.class).setShield(shielding);
                }
                hero.spendAndNext(1.0f);
            }
        });
        hero.sprite.operate(hero.pos);
        Invisibility.dispel();
        hero.busy();
        armor.charge -= this.chargeUse(hero);
        armor.updateQuickslot();
        Sample.INSTANCE.play("sounds/chargeup.mp3");
    }

    @Override
    public String desc() {
        MagesStaff staff;
        Object desc = Messages.get(this, "desc", new Object[0]);
        desc = Game.scene() instanceof GameScene ? ((staff = Dungeon.hero.belongings.getItem(MagesStaff.class)) != null && staff.wandClass() != null ? (String)desc + "\n\n" + Messages.get(staff.wandClass(), "eleblast_desc", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "generic_desc", new Object[0])) : (String)desc + "\n\n" + Messages.get(this, "generic_desc", new Object[0]);
        desc = (String)desc + "\n\n" + Messages.get(this, "cost", (int)this.baseChargeUse);
        return desc;
    }

    @Override
    public int icon() {
        return 19;
    }

    @Override
    public Talent[] talents() {
        return new Talent[]{Talent.BLAST_RADIUS, Talent.ELEMENTAL_POWER, Talent.REACTIVE_BARRIER, Talent.HEROIC_ENERGY};
    }

    static {
        effectTypes.put(WandOfMagicMissile.class, 100);
        effectTypes.put(WandOfLightning.class, 112);
        effectTypes.put(WandOfDisintegration.class, 111);
        effectTypes.put(WandOfFireblast.class, 102);
        effectTypes.put(WandOfCorrosion.class, 103);
        effectTypes.put(WandOfBlastWave.class, 105);
        effectTypes.put(WandOfLivingEarth.class, 109);
        effectTypes.put(WandOfFrost.class, 101);
        effectTypes.put(WandOfPrismaticLight.class, 108);
        effectTypes.put(WandOfWarding.class, 110);
        effectTypes.put(WandOfTransfusion.class, 113);
        effectTypes.put(WandOfCorruption.class, 107);
        effectTypes.put(WandOfRegrowth.class, 104);
        damageFactors = new HashMap();
        damageFactors.put(WandOfMagicMissile.class, Float.valueOf(0.5f));
        damageFactors.put(WandOfLightning.class, Float.valueOf(1.0f));
        damageFactors.put(WandOfDisintegration.class, Float.valueOf(1.0f));
        damageFactors.put(WandOfFireblast.class, Float.valueOf(1.0f));
        damageFactors.put(WandOfCorrosion.class, Float.valueOf(0.0f));
        damageFactors.put(WandOfBlastWave.class, Float.valueOf(0.67f));
        damageFactors.put(WandOfLivingEarth.class, Float.valueOf(0.5f));
        damageFactors.put(WandOfFrost.class, Float.valueOf(1.0f));
        damageFactors.put(WandOfPrismaticLight.class, Float.valueOf(0.67f));
        damageFactors.put(WandOfWarding.class, Float.valueOf(0.0f));
        damageFactors.put(WandOfTransfusion.class, Float.valueOf(0.0f));
        damageFactors.put(WandOfCorruption.class, Float.valueOf(0.0f));
        damageFactors.put(WandOfRegrowth.class, Float.valueOf(0.0f));
    }
}

