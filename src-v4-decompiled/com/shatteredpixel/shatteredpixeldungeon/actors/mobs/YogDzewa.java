/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Light;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bee;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Eye;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.RipperDemon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Scorpio;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.Beam;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.PurpleParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.LarvaSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.YogSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class YogDzewa
extends Mob {
    private int phase;
    private float abilityCooldown;
    private static final int MIN_ABILITY_CD = 10;
    private static final int MAX_ABILITY_CD = 15;
    private float summonCooldown;
    private static final int MIN_SUMMON_CD = 10;
    private static final int MAX_SUMMON_CD = 15;
    private ArrayList<Class> fistSummons;
    private ArrayList<Class> challengeSummons;
    private ArrayList<Class> regularSummons;
    private ArrayList<Integer> targetedCells;
    private static final String PHASE = "phase";
    private static final String ABILITY_CD = "ability_cd";
    private static final String SUMMON_CD = "summon_cd";
    private static final String FIST_SUMMONS = "fist_summons";
    private static final String REGULAR_SUMMONS = "regular_summons";
    private static final String CHALLENGE_SUMMONS = "challenges_summons";
    private static final String TARGETED_CELLS = "targeted_cells";

    public YogDzewa() {
        this.spriteClass = YogSprite.class;
        this.HT = 1000;
        this.HP = 1000;
        this.EXP = 50;
        this.state = this.HUNTING;
        this.viewDistance = 12;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.IMMOVABLE);
        this.properties.add(Char.Property.DEMONIC);
        this.properties.add(Char.Property.STATIC);
        this.phase = 0;
        this.fistSummons = new ArrayList();
        this.challengeSummons = new ArrayList();
        Random.pushGenerator(Dungeon.seedCurDepth() + 1L);
        this.fistSummons.add(Random.Int(2) == 0 ? YogFist.BurningFist.class : YogFist.SoiledFist.class);
        this.fistSummons.add(Random.Int(2) == 0 ? YogFist.RottingFist.class : YogFist.RustedFist.class);
        this.fistSummons.add(Random.Int(2) == 0 ? YogFist.BrightFist.class : YogFist.DarkFist.class);
        Random.shuffle(this.fistSummons);
        if (Random.Int(2) == 0) {
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(1)));
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(2)));
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(0)));
        } else {
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(2)));
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(0)));
            this.challengeSummons.add(YogDzewa.getPairedFist(this.fistSummons.get(1)));
        }
        Random.popGenerator();
        this.regularSummons = new ArrayList();
        if (Dungeon.isChallenged(256)) {
            for (int i = 0; i < 6; ++i) {
                if (i >= 4) {
                    this.regularSummons.add(YogRipper.class);
                    continue;
                }
                if (i >= Statistics.spawnersAlive) {
                    this.regularSummons.add(Larva.class);
                    continue;
                }
                this.regularSummons.add(i % 2 == 0 ? YogEye.class : YogScorpio.class);
            }
        } else {
            for (int i = 0; i < 4; ++i) {
                if (i >= Statistics.spawnersAlive) {
                    this.regularSummons.add(Larva.class);
                    continue;
                }
                this.regularSummons.add(YogRipper.class);
            }
        }
        Random.shuffle(this.regularSummons);
        this.targetedCells = new ArrayList();
    }

    private static Class getPairedFist(Class fist) {
        if (fist == YogFist.BurningFist.class) {
            return YogFist.SoiledFist.class;
        }
        if (fist == YogFist.SoiledFist.class) {
            return YogFist.BurningFist.class;
        }
        if (fist == YogFist.RottingFist.class) {
            return YogFist.RustedFist.class;
        }
        if (fist == YogFist.RustedFist.class) {
            return YogFist.RottingFist.class;
        }
        if (fist == YogFist.BrightFist.class) {
            return YogFist.DarkFist.class;
        }
        if (fist == YogFist.DarkFist.class) {
            return YogFist.BrightFist.class;
        }
        return null;
    }

    @Override
    public int attackSkill(Char target) {
        return INFINITE_ACCURACY;
    }

    @Override
    protected boolean act() {
        Ballistica b;
        if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
            this.fieldOfView = new boolean[Dungeon.level.length()];
        }
        Dungeon.level.updateFieldOfView(this, this.fieldOfView);
        this.throwItems();
        this.sprite.hideAlert();
        this.sprite.hideLost();
        this.enemy = this.chooseEnemy();
        boolean bl = this.enemySeen = this.enemy != null && this.enemy.isAlive() && this.fieldOfView[this.enemy.pos] && this.enemy.invisible <= 0;
        if (this.phase == 0) {
            if (Dungeon.hero.viewDistance >= Dungeon.level.distance(this.pos, Dungeon.hero.pos)) {
                Dungeon.observe();
            }
            if (Dungeon.level.heroFOV[this.pos]) {
                this.notice();
            }
        }
        if (this.phase == 0) {
            this.spend(1.0f);
            return true;
        }
        if (!this.targetedCells.isEmpty() && !Dungeon.hero.rooted) {
            boolean terrainAffected = false;
            HashSet<Char> affected = new HashSet<Char>();
            for (int i : this.targetedCells) {
                b = new Ballistica(this.pos, i, 0);
                this.sprite.parent.add(new Beam.DeathRay(this.sprite.center(), DungeonTilemap.raisedTileCenterToWorld(b.collisionPos)));
                for (int p : b.path) {
                    Char ch = Actor.findChar(p);
                    if (ch != null && (ch.alignment != this.alignment || ch instanceof Bee)) {
                        affected.add(ch);
                    }
                    if (!Dungeon.level.flamable[p]) continue;
                    Dungeon.level.destroy(p);
                    GameScene.updateMap(p);
                    terrainAffected = true;
                }
            }
            Sample.INSTANCE.play("sounds/ray.mp3");
            if (terrainAffected) {
                Dungeon.observe();
            }
            Invisibility.dispel(this);
            for (Char ch : affected) {
                if (ch == Dungeon.hero) {
                    Statistics.bossScores[4] = Statistics.bossScores[4] - 500;
                }
                if (YogDzewa.hit(this, ch, true)) {
                    if (Dungeon.isChallenged(256)) {
                        ch.damage(Random.NormalIntRange(30, 50), new Eye.DeathGaze());
                    } else {
                        ch.damage(Random.NormalIntRange(20, 30), new Eye.DeathGaze());
                    }
                    if (Dungeon.level.heroFOV[this.pos]) {
                        ch.sprite.flash();
                        CellEmitter.center(this.pos).burst(PurpleParticle.BURST, Random.IntRange(1, 2));
                    }
                    if (ch.isAlive() || ch != Dungeon.hero) continue;
                    Badges.validateDeathFromEnemyMagic();
                    Dungeon.fail(this);
                    GLog.n(Messages.get(Char.class, "kill", this.name()), new Object[0]);
                    continue;
                }
                ch.sprite.showStatus(0xFFFF00, ch.defenseVerb(), new Object[0]);
            }
            this.targetedCells.clear();
        }
        if (this.abilityCooldown <= 0.0f) {
            int beams = 1 + (this.HT - this.HP) / 400;
            HashSet<Integer> affectedCells = new HashSet<Integer>();
            for (int i = 0; i < beams; ++i) {
                int targetPos = Dungeon.hero.pos;
                if (i != 0) {
                    do {
                        targetPos = Dungeon.hero.pos + PathFinder.NEIGHBOURS8[Random.Int(8)];
                    } while (Dungeon.level.trueDistance(this.pos, Dungeon.hero.pos) > Dungeon.level.trueDistance(this.pos, targetPos));
                }
                this.targetedCells.add(targetPos);
                b = new Ballistica(this.pos, targetPos, 0);
                affectedCells.addAll(b.path);
            }
            boolean allAdjTargeted = true;
            for (int i : PathFinder.NEIGHBOURS9) {
                if (affectedCells.contains(Dungeon.hero.pos + i) || !Dungeon.level.passable[Dungeon.hero.pos + i]) continue;
                allAdjTargeted = false;
                break;
            }
            if (allAdjTargeted) {
                this.targetedCells.remove(this.targetedCells.size() - 1);
            }
            float delay = GameMath.gate(1.0f, (int)Math.ceil(Dungeon.hero.cooldown()), 3.0f);
            for (int i : this.targetedCells) {
                Ballistica b2 = new Ballistica(this.pos, i, 0);
                for (int p : b2.path) {
                    GameScene.targetedCell(p, delay);
                    affectedCells.add(p);
                }
            }
            this.spend(delay);
            Dungeon.hero.interrupt();
            this.abilityCooldown += Random.NormalFloat(10.0f, 15.0f);
            this.abilityCooldown -= (float)(this.phase - 1);
        } else {
            this.spend(1.0f);
        }
        while (this.summonCooldown <= 0.0f) {
            Class cls = this.regularSummons.remove(0);
            Mob summon = (Mob)Reflection.newInstance(cls);
            this.regularSummons.add(cls);
            int spawnPos = -1;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(this.pos + i) != null || spawnPos != -1 && !(Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, this.pos + i))) continue;
                spawnPos = this.pos + i;
            }
            if (spawnPos == -1) {
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (!(Actor.findChar(this.pos + i) instanceof Sheep) || spawnPos != -1 && !(Dungeon.level.trueDistance(Dungeon.hero.pos, spawnPos) > Dungeon.level.trueDistance(Dungeon.hero.pos, this.pos + i))) continue;
                    spawnPos = this.pos + i;
                }
                if (spawnPos != -1) {
                    Actor.findChar(spawnPos).die(null);
                }
            }
            if (spawnPos == -1) break;
            summon.pos = spawnPos;
            GameScene.add(summon);
            Actor.add(new Pushing(summon, this.pos, summon.pos));
            summon.beckon(Dungeon.hero.pos);
            Dungeon.level.occupyCell(summon);
            this.summonCooldown += Random.NormalFloat(10.0f, 15.0f);
            this.summonCooldown -= (float)(this.phase - 1);
            if (this.findFist() == null) continue;
            this.summonCooldown += (float)(10 - (this.phase - 1));
        }
        if (this.summonCooldown > 0.0f) {
            this.summonCooldown -= 1.0f;
        }
        if (this.abilityCooldown > 0.0f) {
            this.abilityCooldown -= 1.0f;
        }
        if (this.phase == 5 && this.abilityCooldown > 2.0f) {
            this.abilityCooldown = 2.0f;
        }
        if (this.phase == 5 && this.summonCooldown > 3.0f) {
            this.summonCooldown = 3.0f;
        }
        return true;
    }

    public void processFistDeath() {
        if (this.phase == 4 && this.findFist() == null) {
            this.yell(Messages.get(this, "hope", new Object[0]));
            this.summonCooldown = -15.0f;
            this.phase = 5;
            BossHealthBar.bleed(true);
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(0.5f, new Callback(){

                        @Override
                        public void call() {
                            Music.INSTANCE.play("music/halls_boss_finale.ogg", true);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || this.phase != 5;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return this.phase == 0 || this.findFist() != null || super.isInvulnerable(effect);
    }

    @Override
    public void damage(int dmg, Object src) {
        LockedFloor lock;
        int preHP = this.HP;
        super.damage(dmg, src);
        if (this.phase == 0 || this.findFist() != null) {
            return;
        }
        if (this.phase < 4) {
            this.HP = Math.max(this.HP, this.HT - 300 * this.phase);
        } else if (this.phase == 4) {
            this.HP = Math.max(this.HP, 100);
        }
        int dmgTaken = preHP - this.HP;
        if (dmgTaken > 0) {
            this.abilityCooldown -= (float)dmgTaken / 10.0f;
            this.summonCooldown -= (float)dmgTaken / 10.0f;
        }
        if (this.phase < 4 && this.HP <= this.HT - 300 * this.phase) {
            ++this.phase;
            this.updateVisibility(Dungeon.level);
            GLog.n(Messages.get(this, "darkness", new Object[0]), new Object[0]);
            this.sprite.showStatus(65280, Messages.get(this, "invulnerable", new Object[0]), new Object[0]);
            this.addFist((YogFist)Reflection.newInstance(this.fistSummons.remove(0)));
            if (Dungeon.isChallenged(256)) {
                this.addFist((YogFist)Reflection.newInstance(this.challengeSummons.remove(0)));
            }
            CellEmitter.get(Dungeon.level.exit() - 1).burst(ShadowParticle.UP, 25);
            CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
            CellEmitter.get(Dungeon.level.exit() + 1).burst(ShadowParticle.UP, 25);
            if (this.abilityCooldown < 5.0f) {
                this.abilityCooldown = 5.0f;
            }
            if (this.summonCooldown < 5.0f) {
                this.summonCooldown = 5.0f;
            }
        }
        if ((lock = Dungeon.hero.buff(LockedFloor.class)) != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)dmgTaken / 3.0f);
            } else {
                lock.addTime((float)dmgTaken / 2.0f);
            }
        }
    }

    public void addFist(YogFist fist) {
        fist.pos = Dungeon.level.exit();
        CellEmitter.get(Dungeon.level.exit() - 1).burst(ShadowParticle.UP, 25);
        CellEmitter.get(Dungeon.level.exit()).burst(ShadowParticle.UP, 100);
        CellEmitter.get(Dungeon.level.exit() + 1).burst(ShadowParticle.UP, 25);
        if (this.abilityCooldown < 5.0f) {
            this.abilityCooldown = 5.0f;
        }
        if (this.summonCooldown < 5.0f) {
            this.summonCooldown = 5.0f;
        }
        int targetPos = Dungeon.level.exit() + Dungeon.level.width();
        if (!Dungeon.isChallenged(256) && (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep)) {
            fist.pos = targetPos;
        } else if (Actor.findChar(targetPos - 1) == null || Actor.findChar(targetPos - 1) instanceof Sheep) {
            fist.pos = targetPos - 1;
        } else if (Actor.findChar(targetPos + 1) == null || Actor.findChar(targetPos + 1) instanceof Sheep) {
            fist.pos = targetPos + 1;
        } else if (Actor.findChar(targetPos) == null || Actor.findChar(targetPos) instanceof Sheep) {
            fist.pos = targetPos;
        }
        if (Actor.findChar(fist.pos) instanceof Sheep) {
            Actor.findChar(fist.pos).die(null);
        }
        GameScene.add(fist, 4.0f);
        Actor.add(new Pushing(fist, Dungeon.level.exit(), fist.pos));
        Dungeon.level.occupyCell(fist);
    }

    public void updateVisibility(Level level) {
        int viewDistance = 4;
        if (this.phase > 1 && this.isAlive()) {
            viewDistance = Math.max(4 - (this.phase - 1), 1);
        }
        if (Dungeon.isChallenged(32)) {
            viewDistance = Math.min(viewDistance, 2);
        }
        level.viewDistance = viewDistance;
        if (Dungeon.hero != null) {
            if (Dungeon.hero.buff(Light.class) == null) {
                Dungeon.hero.viewDistance = level.viewDistance;
            }
            Dungeon.observe();
        }
    }

    private YogFist findFist() {
        for (Char c : Actor.chars()) {
            if (!(c instanceof YogFist)) continue;
            return (YogFist)c;
        }
        return null;
    }

    @Override
    public void beckon(int cell) {
    }

    @Override
    public void clearEnemy() {
    }

    @Override
    public void aggro(Char ch) {
        if (ch != null && ch.alignment != this.alignment || !(ch instanceof Larva) && !(ch instanceof YogRipper) && !(ch instanceof YogEye) && !(ch instanceof YogScorpio)) {
            for (Mob mob : (Iterable)Dungeon.level.mobs.clone()) {
                if (mob == ch || Dungeon.level.distance(this.pos, mob.pos) > 4 || mob.alignment != this.alignment || !(mob instanceof Larva) && !(mob instanceof YogRipper) && !(mob instanceof YogEye) && !(mob instanceof YogScorpio)) continue;
                mob.aggro(ch);
            }
        }
    }

    @Override
    public void die(Object cause) {
        Bestiary.skipCountingEncounters = true;
        for (Mob mob : (Iterable)Dungeon.level.mobs.clone()) {
            if (!(mob instanceof Larva) && !(mob instanceof YogRipper) && !(mob instanceof YogEye) && !(mob instanceof YogScorpio)) continue;
            mob.die(cause);
        }
        Bestiary.skipCountingEncounters = false;
        this.updateVisibility(Dungeon.level);
        GameScene.bossSlain();
        if (Dungeon.isChallenged(256) && Statistics.spawnersAlive == 4) {
            Badges.validateBossChallengeCompleted();
        } else {
            Statistics.qualifiedForBossChallengeBadge = false;
        }
        Statistics.bossScores[4] = Statistics.bossScores[4] + (5000 + 1250 * Statistics.spawnersAlive);
        Badges.validateTakingTheMick(cause);
        Dungeon.level.unseal();
        super.die(cause);
        this.yell(Messages.get(this, "defeated", new Object[0]));
    }

    @Override
    public void notice() {
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            this.yell(Messages.get(this, "notice", new Object[0]));
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof DriedRose.GhostHero)) continue;
                ((DriedRose.GhostHero)ch).sayBoss();
            }
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.play("music/halls_boss.ogg", true);
                }
            });
            if (this.phase == 0) {
                this.phase = 1;
                this.summonCooldown = Random.NormalFloat(10.0f, 15.0f);
                this.abilityCooldown = Random.NormalFloat(10.0f, 15.0f);
            }
        }
    }

    @Override
    public String description() {
        Object desc = super.description();
        if (Statistics.spawnersAlive > 0) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_spawners", new Object[0]);
        }
        return desc;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PHASE, this.phase);
        bundle.put(ABILITY_CD, this.abilityCooldown);
        bundle.put(SUMMON_CD, this.summonCooldown);
        bundle.put(FIST_SUMMONS, this.fistSummons.toArray(new Class[0]));
        bundle.put(CHALLENGE_SUMMONS, this.challengeSummons.toArray(new Class[0]));
        bundle.put(REGULAR_SUMMONS, this.regularSummons.toArray(new Class[0]));
        int[] bundleArr = new int[this.targetedCells.size()];
        for (int i = 0; i < this.targetedCells.size(); ++i) {
            bundleArr[i] = this.targetedCells.get(i);
        }
        bundle.put(TARGETED_CELLS, bundleArr);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.phase = bundle.getInt(PHASE);
        if (this.phase != 0) {
            BossHealthBar.assignBoss(this);
            if (this.phase == 5) {
                BossHealthBar.bleed(true);
            }
        }
        this.abilityCooldown = bundle.getFloat(ABILITY_CD);
        this.summonCooldown = bundle.getFloat(SUMMON_CD);
        this.fistSummons.clear();
        Collections.addAll(this.fistSummons, bundle.getClassArray(FIST_SUMMONS));
        this.challengeSummons.clear();
        Collections.addAll(this.challengeSummons, bundle.getClassArray(CHALLENGE_SUMMONS));
        this.regularSummons.clear();
        Collections.addAll(this.regularSummons, bundle.getClassArray(REGULAR_SUMMONS));
        for (int i : bundle.getIntArray(TARGETED_CELLS)) {
            this.targetedCells.add(i);
        }
    }

    public static class YogRipper
    extends RipperDemon {
        public YogRipper() {
            this.maxLvl = -2;
            this.properties.add(Char.Property.BOSS_MINION);
        }
    }

    public static class Larva
    extends Mob {
        public Larva() {
            this.spriteClass = LarvaSprite.class;
            this.HT = 20;
            this.HP = 20;
            this.defenseSkill = 12;
            this.viewDistance = 6;
            this.EXP = 5;
            this.maxLvl = -2;
            this.properties.add(Char.Property.DEMONIC);
            this.properties.add(Char.Property.BOSS_MINION);
        }

        @Override
        public int attackSkill(Char target) {
            return 30;
        }

        @Override
        public int damageRoll() {
            return Random.NormalIntRange(15, 25);
        }

        @Override
        public int drRoll() {
            return super.drRoll() + Random.NormalIntRange(0, 4);
        }
    }

    public static class YogEye
    extends Eye {
        public YogEye() {
            this.maxLvl = -2;
            this.properties.add(Char.Property.BOSS_MINION);
        }
    }

    public static class YogScorpio
    extends Scorpio {
        public YogScorpio() {
            this.maxLvl = -2;
            this.properties.add(Char.Property.BOSS_MINION);
        }
    }
}

