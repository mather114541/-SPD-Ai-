/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Degrade;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LifeLink;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Ghoul;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Golem;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Monk;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Warlock;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KingsCrown;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.glyphs.Viscosity;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfForce;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfLightning;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.KingSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.HashSet;

public class DwarfKing
extends Mob {
    private int phase;
    private int summonsMade;
    private float summonCooldown;
    private float abilityCooldown;
    private final int MIN_COOLDOWN;
    private final int MAX_COOLDOWN;
    private int lastAbility;
    private static final int NONE = 0;
    private static final int LINK = 1;
    private static final int TELE = 2;
    private static final String PHASE = "phase";
    private static final String SUMMONS_MADE = "summons_made";
    private static final String SUMMON_CD = "summon_cd";
    private static final String ABILITY_CD = "ability_cd";
    private static final String LAST_ABILITY = "last_ability";

    public DwarfKing() {
        this.spriteClass = KingSprite.class;
        this.HT = Dungeon.isChallenged(256) ? 450 : 300;
        this.HP = this.HT;
        this.EXP = 40;
        this.defenseSkill = 22;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.UNDEAD);
        this.phase = 1;
        this.summonsMade = 0;
        this.summonCooldown = 0.0f;
        this.abilityCooldown = 0.0f;
        this.MIN_COOLDOWN = Dungeon.isChallenged(256) ? 8 : 10;
        this.MAX_COOLDOWN = Dungeon.isChallenged(256) ? 10 : 14;
        this.lastAbility = 0;
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(15, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 26;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, this.phase);
        bundle.put(SUMMONS_MADE, this.summonsMade);
        bundle.put(SUMMON_CD, this.summonCooldown);
        bundle.put(ABILITY_CD, this.abilityCooldown);
        bundle.put(LAST_ABILITY, this.lastAbility);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.phase = bundle.getInt(PHASE);
        this.summonsMade = bundle.getInt(SUMMONS_MADE);
        this.summonCooldown = bundle.getFloat(SUMMON_CD);
        this.abilityCooldown = bundle.getFloat(ABILITY_CD);
        this.lastAbility = bundle.getInt(LAST_ABILITY);
        if (this.phase == 2) {
            this.properties.add(Char.Property.IMMOVABLE);
        }
        BossHealthBar.assignBoss(this);
        if (this.phase == 3) {
            BossHealthBar.bleed(true);
        }
    }

    @Override
    protected boolean act() {
        if (this.pos == CityBossLevel.throne) {
            this.throwItems();
        }
        if (this.phase == 1) {
            if (this.summonCooldown <= 0.0f && this.summonSubject(Dungeon.isChallenged(256) ? 2 : 3)) {
                ++this.summonsMade;
                this.summonCooldown += (float)Random.NormalIntRange(this.MIN_COOLDOWN, this.MAX_COOLDOWN);
            } else if (this.summonCooldown > 0.0f) {
                this.summonCooldown -= 1.0f;
            }
            if (this.paralysed > 0) {
                this.spend(1.0f);
                return true;
            }
            if (this.abilityCooldown <= 0.0f) {
                if (this.lastAbility == 0) {
                    this.lastAbility = Random.Int(2) == 0 ? 1 : 2;
                } else if (this.lastAbility == 1) {
                    this.lastAbility = Random.Int(8) == 0 ? 1 : 2;
                } else {
                    int n = this.lastAbility = Random.Int(8) != 0 ? 1 : 2;
                }
                if (this.lastAbility == 1 && this.lifeLinkSubject()) {
                    this.abilityCooldown += (float)Random.NormalIntRange(this.MIN_COOLDOWN, this.MAX_COOLDOWN);
                    this.spend(1.0f);
                    return true;
                }
                if (this.teleportSubject()) {
                    this.lastAbility = 2;
                    this.abilityCooldown += (float)Random.NormalIntRange(this.MIN_COOLDOWN, this.MAX_COOLDOWN);
                    this.spend(1.0f);
                    return true;
                }
            } else {
                this.abilityCooldown -= 1.0f;
            }
        } else {
            if (this.phase == 2) {
                if (Dungeon.isChallenged(256)) {
                    if (this.summonsMade < 6) {
                        if (this.summonsMade == 0) {
                            this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                            Sample.INSTANCE.play("sounds/challenge.mp3");
                            this.yell(Messages.get(this, "wave_1", new Object[0]));
                        }
                        this.summonSubject(3, DKGhoul.class);
                        this.summonSubject(3, DKGhoul.class);
                        this.spend(3.0f);
                        this.summonsMade += 2;
                        return true;
                    }
                    if (this.shielding() <= 300 && this.summonsMade < 12) {
                        if (this.summonsMade == 6) {
                            this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                            Sample.INSTANCE.play("sounds/challenge.mp3");
                            this.yell(Messages.get(this, "wave_2", new Object[0]));
                        }
                        this.summonSubject(3, DKGhoul.class);
                        this.summonSubject(3, DKGhoul.class);
                        if (this.summonsMade == 6) {
                            this.summonSubject(3, DKMonk.class);
                        } else {
                            this.summonSubject(3, DKWarlock.class);
                        }
                        this.summonsMade += 3;
                        this.spend(3.0f);
                        return true;
                    }
                    if (this.shielding() <= 150 && this.summonsMade < 18) {
                        if (this.summonsMade == 12) {
                            this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                            Sample.INSTANCE.play("sounds/challenge.mp3");
                            this.yell(Messages.get(this, "wave_3", new Object[0]));
                            this.summonSubject(3, DKWarlock.class);
                            this.summonSubject(3, DKMonk.class);
                            this.summonSubject(3, DKGhoul.class);
                            this.summonSubject(3, DKGhoul.class);
                            this.summonsMade += 4;
                            this.spend(3.0f);
                        } else {
                            this.summonSubject(3, DKGolem.class);
                            this.summonSubject(3, DKGolem.class);
                            this.summonsMade += 2;
                            this.spend(1.0f);
                        }
                        return true;
                    }
                    this.spend(1.0f);
                    return true;
                }
                if (this.summonsMade < 4) {
                    if (this.summonsMade == 0) {
                        this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                        Sample.INSTANCE.play("sounds/challenge.mp3");
                        this.yell(Messages.get(this, "wave_1", new Object[0]));
                    }
                    this.summonSubject(3, DKGhoul.class);
                    this.spend(3.0f);
                    ++this.summonsMade;
                    return true;
                }
                if (this.shielding() <= 200 && this.summonsMade < 8) {
                    if (this.summonsMade == 4) {
                        this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                        Sample.INSTANCE.play("sounds/challenge.mp3");
                        this.yell(Messages.get(this, "wave_2", new Object[0]));
                    }
                    if (this.summonsMade == 7) {
                        this.summonSubject(3, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
                    } else {
                        this.summonSubject(3, DKGhoul.class);
                    }
                    ++this.summonsMade;
                    this.spend(1.0f);
                    return true;
                }
                if (this.shielding() <= 100 && this.summonsMade < 12) {
                    this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
                    Sample.INSTANCE.play("sounds/challenge.mp3");
                    this.yell(Messages.get(this, "wave_3", new Object[0]));
                    this.summonSubject(4, DKWarlock.class);
                    this.summonSubject(4, DKMonk.class);
                    this.summonSubject(4, DKGhoul.class);
                    this.summonSubject(4, DKGhoul.class);
                    this.summonsMade = 12;
                    this.spend(1.0f);
                    return true;
                }
                this.spend(1.0f);
                return true;
            }
            if (this.phase == 3 && this.buffs(Summoning.class).size() < 4 && this.summonSubject(Dungeon.isChallenged(256) ? 2 : 3)) {
                ++this.summonsMade;
            }
        }
        return super.act();
    }

    private boolean summonSubject(int delay) {
        if (Dungeon.isChallenged(256)) {
            if (this.summonsMade % 3 == 2) {
                if (this.summonsMade % 9 == 8) {
                    return this.summonSubject(delay, DKGolem.class);
                }
                return this.summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
            }
            return this.summonSubject(delay, DKGhoul.class);
        }
        if (this.summonsMade % 4 == 3) {
            return this.summonSubject(delay, Random.Int(2) == 0 ? DKMonk.class : DKWarlock.class);
        }
        return this.summonSubject(delay, DKGhoul.class);
    }

    private boolean summonSubject(int delay, Class<? extends Mob> type) {
        Summoning s = new Summoning();
        s.pos = ((CityBossLevel)Dungeon.level).getSummoningPos();
        if (s.pos == -1) {
            return false;
        }
        s.summon = type;
        s.delay = delay;
        s.attachTo(this);
        return true;
    }

    private HashSet<Mob> getSubjects() {
        HashSet<Mob> subjects = new HashSet<Mob>();
        for (Mob m : Dungeon.level.mobs) {
            if (m.alignment != this.alignment || !(m instanceof Ghoul) && !(m instanceof Monk) && !(m instanceof Warlock) && !(m instanceof Golem)) continue;
            subjects.add(m);
        }
        return subjects;
    }

    private boolean lifeLinkSubject() {
        Actor furthest = null;
        for (Mob m : this.getSubjects()) {
            boolean alreadyLinked = false;
            for (LifeLink l : m.buffs(LifeLink.class)) {
                if (l.object != this.id()) continue;
                alreadyLinked = true;
            }
            if (alreadyLinked || furthest != null && Dungeon.level.distance(this.pos, ((Mob)furthest).pos) >= Dungeon.level.distance(this.pos, m.pos)) continue;
            furthest = m;
        }
        if (furthest != null) {
            Buff.append(furthest, LifeLink.class, (float)100.0f).object = this.id();
            Buff.append((Char)this, LifeLink.class, (float)100.0f).object = furthest.id();
            this.yell(Messages.get(this, "lifelink_" + Random.IntRange(1, 2), new Object[0]));
            this.sprite.parent.add(new Beam.HealthRay(this.sprite.destinationCenter(), ((Mob)furthest).sprite.destinationCenter()));
            Sample.INSTANCE.play("sounds/ray.mp3");
            return true;
        }
        return false;
    }

    private boolean teleportSubject() {
        if (this.enemy == null) {
            return false;
        }
        Mob furthest = null;
        for (Mob m : this.getSubjects()) {
            if (furthest != null && Dungeon.level.distance(this.pos, furthest.pos) >= Dungeon.level.distance(this.pos, m.pos)) continue;
            furthest = m;
        }
        if (furthest != null) {
            int bestPos = this.pos;
            Ballistica trajectory = new Ballistica(this.enemy.pos, this.pos, 1);
            int targetCell = trajectory.path.get(trajectory.dist + 1);
            if (Actor.findChar(targetCell) == null && !Dungeon.level.solid[targetCell]) {
                bestPos = targetCell;
            } else {
                float bestDist = Dungeon.level.trueDistance(this.pos, this.enemy.pos);
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Actor.findChar(this.pos + i) != null || Dungeon.level.solid[this.pos + i] || !(Dungeon.level.trueDistance(this.pos + i, this.enemy.pos) > bestDist)) continue;
                    bestPos = this.pos + i;
                    bestDist = Dungeon.level.trueDistance(this.pos + i, this.enemy.pos);
                }
            }
            Actor.add(new Pushing(this, this.pos, bestPos));
            this.pos = bestPos;
            float bestDist = Dungeon.level.trueDistance(this.enemy.pos, this.pos);
            bestPos = this.enemy.pos;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(this.enemy.pos + i) != null || Dungeon.level.solid[this.enemy.pos + i] || !(Dungeon.level.trueDistance(this.enemy.pos + i, this.pos) < bestDist)) continue;
                bestPos = this.enemy.pos + i;
                bestDist = Dungeon.level.trueDistance(this.enemy.pos + i, this.pos);
            }
            if (bestPos != this.enemy.pos) {
                ScrollOfTeleportation.appear(furthest, bestPos);
            }
            this.yell(Messages.get(this, "teleport_" + Random.IntRange(1, 2), new Object[0]));
            return true;
        }
        return false;
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            this.yell(Messages.get(this, "notice", new Object[0]));
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof DriedRose.GhostHero)) continue;
                ((DriedRose.GhostHero)ch).sayBoss();
            }
        }
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (effect == KingDamager.class) {
            return false;
        }
        return this.phase == 2 || super.isInvulnerable(effect);
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!(src != Dungeon.hero || RingOfForce.fightingUnarmed(Dungeon.hero) && Dungeon.hero.buff(RingOfForce.Force.class) == null)) {
            Statistics.qualifiedForBossChallengeBadge = false;
        } else if (src instanceof Wand && !(src instanceof WandOfLightning)) {
            Statistics.qualifiedForBossChallengeBadge = false;
        } else if (src instanceof ClericSpell) {
            Statistics.qualifiedForBossChallengeBadge = false;
        }
        if (this.isInvulnerable(src.getClass())) {
            super.damage(dmg, src);
            return;
        }
        if (this.phase == 3 && !(src instanceof Viscosity.DeferedDamage)) {
            if (dmg >= 0) {
                Viscosity.DeferedDamage deferred = Buff.affect(this, Viscosity.DeferedDamage.class);
                deferred.extend(dmg);
                this.sprite.showStatus(0xFF8800, Messages.get(Viscosity.class, "deferred", dmg), new Object[0]);
            }
            return;
        }
        int preHP = this.HP;
        super.damage(dmg, src);
        LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
        if (lock != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)dmg / 5.0f);
            } else {
                lock.addTime((float)dmg / 3.0f);
            }
        }
        if (this.phase == 1) {
            int dmgTaken = preHP - this.HP;
            this.abilityCooldown -= (float)dmgTaken / 8.0f;
            this.summonCooldown -= (float)dmgTaken / 8.0f;
            if (this.HP <= (Dungeon.isChallenged(256) ? 100 : 50)) {
                this.HP = Dungeon.isChallenged(256) ? 100 : 50;
                this.sprite.showStatus(65280, Messages.get(this, "invulnerable", new Object[0]), new Object[0]);
                ScrollOfTeleportation.appear(this, CityBossLevel.throne);
                this.properties.add(Char.Property.IMMOVABLE);
                this.phase = 2;
                this.summonsMade = 0;
                this.sprite.idle();
                Buff.affect(this, DKBarrior.class).setShield(this.HT);
                for (Summoning s : this.buffs(Summoning.class)) {
                    s.detach();
                }
                Bestiary.skipCountingEncounters = true;
                for (Mob m : this.getSubjects()) {
                    m.die(null);
                }
                Bestiary.skipCountingEncounters = false;
                for (Buff b : this.buffs()) {
                    if (!(b instanceof LifeLink)) continue;
                    b.detach();
                }
            }
        } else if (this.phase == 2 && this.shielding() == 0) {
            this.properties.remove((Object)Char.Property.IMMOVABLE);
            this.phase = 3;
            this.summonsMade = 1;
            this.sprite.centerEmitter().start(Speck.factory(5), 0.4f, 2);
            Sample.INSTANCE.play("sounds/challenge.mp3");
            this.yell(Messages.get(this, "enraged", Dungeon.hero.name()));
            BossHealthBar.bleed(true);
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(0.5f, new Callback(){

                        @Override
                        public void call() {
                            Music.INSTANCE.play("music/city_boss_finale.ogg", true);
                        }
                    });
                }
            });
        } else if (this.phase == 3 && preHP > 20 && this.HP < 20 && this.isAlive()) {
            this.yell(Messages.get(this, "losing", new Object[0]));
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || this.phase != 3;
    }

    @Override
    public void die(Object cause) {
        GameScene.bossSlain();
        super.die(cause);
        Heap h = (Heap)Dungeon.level.heaps.get(CityBossLevel.throne);
        if (h != null) {
            for (Item i : h.items) {
                Dungeon.level.drop(i, CityBossLevel.throne + Dungeon.level.width());
            }
            h.destroy();
        }
        if (this.pos == CityBossLevel.throne) {
            Dungeon.level.drop((Item)new KingsCrown(), (int)(this.pos + Dungeon.level.width())).sprite.drop(this.pos);
        } else {
            Dungeon.level.drop((Item)new KingsCrown(), (int)this.pos).sprite.drop();
        }
        Badges.validateBossSlain();
        if (Statistics.qualifiedForBossChallengeBadge) {
            Badges.validateBossChallengeCompleted();
        }
        Statistics.bossScores[3] = Statistics.bossScores[3] + 4000;
        Dungeon.level.unseal();
        Bestiary.skipCountingEncounters = true;
        for (Mob m : this.getSubjects()) {
            m.die(null);
        }
        Bestiary.skipCountingEncounters = false;
        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }
        if (Dungeon.hero.buff(Degrade.class) != null) {
            Dungeon.hero.buff(Degrade.class).detach();
        }
        this.yell(Messages.get(this, "defeated", new Object[0]));
    }

    @Override
    public boolean isImmune(Class effect) {
        if (this.phase > 1 && effect == Doom.class && this.buff(Doom.class) != null) {
            return true;
        }
        return super.isImmune(effect);
    }

    public static class DKGhoul
    extends Ghoul {
        public DKGhoul() {
            this.properties.add(Char.Property.BOSS_MINION);
            this.state = this.HUNTING;
        }

        @Override
        protected boolean act() {
            this.partnerID = -2;
            return super.act();
        }
    }

    public static class DKMonk
    extends Monk {
        public DKMonk() {
            this.properties.add(Char.Property.BOSS_MINION);
            this.state = this.HUNTING;
        }
    }

    public static class DKWarlock
    extends Warlock {
        public DKWarlock() {
            this.properties.add(Char.Property.BOSS_MINION);
            this.state = this.HUNTING;
        }

        @Override
        protected void zap() {
            if (this.enemy == Dungeon.hero) {
                Statistics.bossScores[3] = Statistics.bossScores[3] - 400;
            }
            super.zap();
        }
    }

    public static class DKGolem
    extends Golem {
        public DKGolem() {
            this.properties.add(Char.Property.BOSS_MINION);
            this.state = this.HUNTING;
        }
    }

    public static class Summoning
    extends Buff {
        private int delay;
        private int pos;
        private Class<? extends Mob> summon;
        private Emitter particles;
        private static final String DELAY = "delay";
        private static final String POS = "pos";
        private static final String SUMMON = "summon";

        public int getPos() {
            return this.pos;
        }

        @Override
        public boolean act() {
            --this.delay;
            if (this.delay <= 0) {
                if (this.summon == DKGolem.class) {
                    this.particles.burst(SparkParticle.FACTORY, 10);
                    Sample.INSTANCE.play("sounds/chargeup.mp3");
                } else if (this.summon == DKWarlock.class) {
                    this.particles.burst(ShadowParticle.CURSE, 10);
                    Sample.INSTANCE.play("sounds/cursed.mp3");
                } else if (this.summon == DKMonk.class) {
                    this.particles.burst(ElmoParticle.FACTORY, 10);
                    Sample.INSTANCE.play("sounds/burning.mp3");
                } else {
                    this.particles.burst(Speck.factory(6), 10);
                    Sample.INSTANCE.play("sounds/bones.mp3");
                }
                this.particles = null;
                if (Actor.findChar(this.pos) != null) {
                    ArrayList<Integer> candidates = new ArrayList<Integer>();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (!Dungeon.level.passable[this.pos + i] || Actor.findChar(this.pos + i) != null) continue;
                        candidates.add(this.pos + i);
                    }
                    if (!candidates.isEmpty()) {
                        this.pos = (Integer)Random.element(candidates);
                    }
                }
                if (Actor.findChar(this.pos) instanceof Sheep) {
                    Actor.findChar(this.pos).die(null);
                }
                if (Actor.findChar(this.pos) == null) {
                    Mob m = Reflection.newInstance(this.summon);
                    m.pos = this.pos;
                    m.maxLvl = -2;
                    GameScene.add(m);
                    Dungeon.level.occupyCell(m);
                    m.state = m.HUNTING;
                    if (((DwarfKing)this.target).phase == 2) {
                        Buff.affect(m, KingDamager.class);
                    }
                } else {
                    Char ch = Actor.findChar(this.pos);
                    ch.damage(Random.NormalIntRange(20, 40), this);
                    if (((DwarfKing)this.target).phase == 2) {
                        if (Dungeon.isChallenged(256)) {
                            this.target.damage(this.target.HT / 18, new KingDamager());
                        } else {
                            this.target.damage(this.target.HT / 12, new KingDamager());
                        }
                    }
                    if (!ch.isAlive() && ch == Dungeon.hero) {
                        Dungeon.fail(DwarfKing.class);
                        GLog.n(Messages.capitalize(Messages.get(Char.class, "kill", Messages.get(DwarfKing.class, "name", new Object[0]))), new Object[0]);
                    }
                }
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void fx(boolean on) {
            if (on && (this.particles == null || this.particles.parent == null)) {
                this.particles = CellEmitter.get(this.pos);
                if (this.summon == DKGolem.class) {
                    this.particles.pour(SparkParticle.STATIC, 0.05f);
                } else if (this.summon == DKWarlock.class) {
                    this.particles.pour(ShadowParticle.UP, 0.1f);
                } else if (this.summon == DKMonk.class) {
                    this.particles.pour(ElmoParticle.FACTORY, 0.1f);
                } else {
                    this.particles.pour(Speck.factory(105), 0.1f);
                }
            } else if (!on && this.particles != null) {
                this.particles.on = false;
            }
        }

        @Override
        public void storeInBundle(Bundle bundle) {
            super.storeInBundle(bundle);
            bundle.put(DELAY, this.delay);
            bundle.put(POS, this.pos);
            bundle.put(SUMMON, this.summon);
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.delay = bundle.getInt(DELAY);
            this.pos = bundle.getInt(POS);
            this.summon = bundle.getClass(SUMMON);
        }
    }

    public static class KingDamager
    extends Buff {
        public KingDamager() {
            this.revivePersists = true;
        }

        @Override
        public boolean act() {
            if (this.target.alignment != Char.Alignment.ENEMY) {
                this.detach();
            }
            this.spend(1.0f);
            return true;
        }

        @Override
        public void detach() {
            super.detach();
            for (Mob m : Dungeon.level.mobs) {
                if (!(m instanceof DwarfKing)) continue;
                int damage = m.HT / (Dungeon.isChallenged(256) ? 18 : 12);
                m.damage(damage, this);
            }
        }
    }

    public static class DKBarrior
    extends Barrier {
        @Override
        public boolean act() {
            this.incShield();
            return super.act();
        }

        @Override
        public int icon() {
            return 127;
        }
    }
}

