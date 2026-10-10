/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Barrier;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Chill;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Cripple;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Frost;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Slow;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Vertigo;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.WallOfLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DelayedRockFall;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.LloydsBeacon;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.MetalShard;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesBossLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ConeAOE;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.DM300Sprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Iterator;

public class DM300
extends Mob {
    public int pylonsActivated;
    public boolean supercharged;
    public boolean chargeAnnounced;
    private final int MIN_COOLDOWN = 5;
    private final int MAX_COOLDOWN;
    private int turnsSinceLastAbility;
    private int abilityCooldown;
    private int lastAbility;
    private static final int NONE = 0;
    private static final int GAS = 1;
    private static final int ROCKS = 2;
    private static final String PYLONS_ACTIVATED = "pylons_activated";
    private static final String SUPERCHARGED = "supercharged";
    private static final String CHARGE_ANNOUNCED = "charge_announced";
    private static final String TURNS_SINCE_LAST_ABILITY = "turns_since_last_ability";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";
    private static final String LAST_ABILITY = "last_ability";
    private boolean invulnWarned;

    public DM300() {
        this.spriteClass = DM300Sprite.class;
        this.HT = Dungeon.isChallenged(256) ? 400 : 300;
        this.HP = this.HT;
        this.EXP = 30;
        this.defenseSkill = 15;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.INORGANIC);
        this.properties.add(Char.Property.LARGE);
        this.pylonsActivated = 0;
        this.supercharged = false;
        this.chargeAnnounced = false;
        this.MIN_COOLDOWN = 5;
        this.MAX_COOLDOWN = Dungeon.isChallenged(256) ? 7 : 9;
        this.turnsSinceLastAbility = -1;
        this.abilityCooldown = Random.NormalIntRange(5, this.MAX_COOLDOWN);
        this.lastAbility = 0;
        this.invulnWarned = false;
        this.immunities.add(Sleep.class);
        this.resistances.add(Terror.class);
        this.resistances.add(Charm.class);
        this.resistances.add(Vertigo.class);
        this.resistances.add(Cripple.class);
        this.resistances.add(Chill.class);
        this.resistances.add(Frost.class);
        this.resistances.add(Roots.class);
        this.resistances.add(Slow.class);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(15, 25);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 10);
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(PYLONS_ACTIVATED, this.pylonsActivated);
        bundle.put(SUPERCHARGED, this.supercharged);
        bundle.put(CHARGE_ANNOUNCED, this.chargeAnnounced);
        bundle.put(TURNS_SINCE_LAST_ABILITY, this.turnsSinceLastAbility);
        bundle.put(ABILITY_COOLDOWN, this.abilityCooldown);
        bundle.put(LAST_ABILITY, this.lastAbility);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.pylonsActivated = bundle.getInt(PYLONS_ACTIVATED);
        this.supercharged = bundle.getBoolean(SUPERCHARGED);
        this.chargeAnnounced = bundle.getBoolean(CHARGE_ANNOUNCED);
        this.turnsSinceLastAbility = bundle.getInt(TURNS_SINCE_LAST_ABILITY);
        this.abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
        this.lastAbility = bundle.getInt(LAST_ABILITY);
        if (this.turnsSinceLastAbility != -1) {
            BossHealthBar.assignBoss(this);
            if (!this.supercharged && this.pylonsActivated == this.totalPylonsToActivate()) {
                BossHealthBar.bleed(true);
            }
        }
    }

    @Override
    protected boolean act() {
        if (this.paralysed > 0) {
            return super.act();
        }
        if (!this.supercharged) {
            boolean canReach;
            if (this.turnsSinceLastAbility >= 0) {
                ++this.turnsSinceLastAbility;
            }
            if (this.fieldOfView == null || this.fieldOfView.length != Dungeon.level.length()) {
                this.fieldOfView = new boolean[Dungeon.level.length()];
                Dungeon.level.updateFieldOfView(this, this.fieldOfView);
            }
            if (this.enemy == null || !this.enemy.isAlive()) {
                canReach = Dungeon.level.adjacent(this.pos, Dungeon.hero.pos) ? true : Dungeon.findStep(this, Dungeon.hero.pos, Dungeon.level.openSpace, this.fieldOfView, true) != -1;
            } else if (Dungeon.level.adjacent(this.pos, this.enemy.pos)) {
                canReach = true;
            } else {
                boolean bl = canReach = Dungeon.findStep(this, this.enemy.pos, Dungeon.level.openSpace, this.fieldOfView, true) != -1;
            }
            if (this.state != this.HUNTING) {
                if (Dungeon.hero.invisible <= 0 && canReach) {
                    this.beckon(Dungeon.hero.pos);
                }
            } else {
                if (!(this.enemy != null && this.enemy.isAlive() || Dungeon.hero.invisible > 0)) {
                    this.enemy = Dungeon.hero;
                }
                if (this.enemy != null && this.enemy.isAlive() && !canReach) {
                    if (this.turnsSinceLastAbility >= 5) {
                        ConeAOE aim = new ConeAOE(new Ballistica(this.pos, this.enemy.pos, 0), Float.POSITIVE_INFINITY, 30.0f, 4);
                        if (aim.cells.contains(this.enemy.pos) && !Char.hasProp(this.enemy, Char.Property.INORGANIC)) {
                            this.lastAbility = 1;
                            this.turnsSinceLastAbility = 0;
                            if (this.sprite != null && (this.sprite.visible || this.enemy.sprite.visible)) {
                                this.sprite.zap(this.enemy.pos);
                                return false;
                            }
                            this.ventGas(this.enemy);
                            Sample.INSTANCE.play("sounds/gas.mp3");
                            return true;
                        }
                        if (this.enemy.paralysed <= 0) {
                            this.lastAbility = 2;
                            this.turnsSinceLastAbility = 0;
                            if (this.sprite != null && (this.sprite.visible || this.enemy.sprite.visible)) {
                                ((DM300Sprite)this.sprite).slam(this.enemy.pos);
                                return false;
                            }
                            this.dropRocks(this.enemy);
                            Sample.INSTANCE.play("sounds/rocks.mp3");
                            return true;
                        }
                    }
                } else if (this.enemy != null && this.enemy.isAlive() && this.fieldOfView[this.enemy.pos] && this.turnsSinceLastAbility > this.abilityCooldown) {
                    if (this.lastAbility == 0) {
                        this.lastAbility = Random.Int(2) == 0 ? 1 : 2;
                    } else if (this.lastAbility == 1) {
                        this.lastAbility = Random.Int(4) == 0 ? 1 : 2;
                    } else {
                        int n = this.lastAbility = Random.Int(4) != 0 ? 1 : 2;
                    }
                    if (Char.hasProp(this.enemy, Char.Property.INORGANIC)) {
                        this.lastAbility = 2;
                    }
                    if (Dungeon.level.adjacent(this.pos, this.enemy.pos)) {
                        this.spend(1.0f);
                    }
                    this.turnsSinceLastAbility = 0;
                    this.abilityCooldown = Random.NormalIntRange(5, this.MAX_COOLDOWN);
                    if (this.lastAbility == 1) {
                        if (this.sprite != null && (this.sprite.visible || this.enemy.sprite.visible)) {
                            this.sprite.zap(this.enemy.pos);
                            return false;
                        }
                        this.ventGas(this.enemy);
                        Sample.INSTANCE.play("sounds/gas.mp3");
                        return true;
                    }
                    if (this.sprite != null && (this.sprite.visible || this.enemy.sprite.visible)) {
                        ((DM300Sprite)this.sprite).slam(this.enemy.pos);
                        return false;
                    }
                    this.dropRocks(this.enemy);
                    Sample.INSTANCE.play("sounds/rocks.mp3");
                    return true;
                }
            }
        } else {
            if (!this.chargeAnnounced) {
                this.yell(Messages.get(this, SUPERCHARGED, new Object[0]));
                this.chargeAnnounced = true;
            }
            if (Dungeon.hero.invisible <= 0) {
                this.beckon(Dungeon.hero.pos);
                this.state = this.HUNTING;
                this.enemy = Dungeon.hero;
            }
        }
        return super.act();
    }

    @Override
    public boolean attack(Char enemy, float dmgMulti, float dmgBonus, float accMulti) {
        if (enemy == Dungeon.hero && this.supercharged) {
            Statistics.qualifiedForBossChallengeBadge = false;
        }
        return super.attack(enemy, dmgMulti, dmgBonus, accMulti);
    }

    @Override
    protected Char chooseEnemy() {
        Char enemy = super.chooseEnemy();
        if (this.supercharged && enemy == null) {
            enemy = Dungeon.hero;
        }
        return enemy;
    }

    @Override
    public void move(int step, boolean travelling) {
        super.move(step, travelling);
        if (travelling) {
            PixelScene.shake(this.supercharged ? 3.0f : 1.0f, 0.25f);
        }
        if (!this.flying && Dungeon.level.map[this.pos] == 19 && this.state == this.HUNTING) {
            if (CavesBossLevel.PylonEnergy.volumeAt(this.pos, CavesBossLevel.PylonEnergy.class) > 0) {
                return;
            }
            if (Dungeon.level.heroFOV[this.pos]) {
                if (this.buff(Barrier.class) == null) {
                    GLog.w(Messages.get(this, "shield", new Object[0]), new Object[0]);
                }
                Sample.INSTANCE.play("sounds/lightning.mp3");
                this.sprite.emitter().start(SparkParticle.STATIC, 0.05f, 20);
                this.sprite.showStatusWithIcon(65280, Integer.toString(30 + (this.HT - this.HP) / 10), FloatingText.SHIELDING, new Object[0]);
            }
            Buff.affect(this, Barrier.class).setShield(30 + (this.HT - this.HP) / 10);
        }
    }

    @Override
    public float speed() {
        return super.speed() * (float)(this.supercharged ? 2 : 1);
    }

    @Override
    public void notice() {
        super.notice();
        if (!BossHealthBar.isAssigned()) {
            BossHealthBar.assignBoss(this);
            this.turnsSinceLastAbility = 0;
            this.yell(Messages.get(this, "notice", new Object[0]));
            for (Char ch : Actor.chars()) {
                if (!(ch instanceof DriedRose.GhostHero)) continue;
                ((DriedRose.GhostHero)ch).sayBoss();
            }
        }
    }

    public void onZapComplete() {
        this.ventGas(this.enemy);
        this.next();
    }

    public void ventGas(Char target) {
        Dungeon.hero.interrupt();
        final Ballistica trajectory = new Ballistica(this.pos, target.pos, 1);
        final int gasMulti = Dungeon.isChallenged(256) ? 2 : 1;
        Actor.addDelayed(new Actor(){
            {
                this.actPriority = 100;
            }

            @Override
            protected boolean act() {
                int gasVented = 0;
                GameScene.add(Blob.seed(trajectory.collisionPos, 100 * gasMulti, ToxicGas.class));
                for (int i : trajectory.subPath(0, trajectory.dist)) {
                    GameScene.add(Blob.seed(i, 20 * gasMulti, ToxicGas.class));
                    gasVented += 20 * gasMulti;
                }
                if (gasVented < 250 * gasMulti) {
                    int toVentAround = (int)Math.ceil((float)(250 * gasMulti - gasVented) / 8.0f);
                    for (int i : PathFinder.NEIGHBOURS8) {
                        GameScene.add(Blob.seed(DM300.this.pos + i, toVentAround, ToxicGas.class));
                    }
                }
                Actor.remove(this);
                return true;
            }
        }, target.cooldown());
    }

    public void onSlamComplete() {
        this.dropRocks(this.enemy);
        this.next();
    }

    public void dropRocks(Char target) {
        int safeCell;
        int rockCenter;
        Ballistica trajectory;
        int oppositeAdjacent;
        Dungeon.hero.interrupt();
        if (Dungeon.level.adjacent(this.pos, target.pos)) {
            oppositeAdjacent = target.pos + (target.pos - this.pos);
            trajectory = new Ballistica(target.pos, oppositeAdjacent, 6);
            WandOfBlastWave.throwChar(target, trajectory, 2, false, false, this);
            if (target == Dungeon.hero) {
                Dungeon.hero.interrupt();
            }
            rockCenter = trajectory.path.get(Math.min(trajectory.dist, 2));
        } else if (this.fieldOfView[target.pos] && Dungeon.level.distance(this.pos, target.pos) == 2) {
            oppositeAdjacent = target.pos + (target.pos - this.pos);
            trajectory = new Ballistica(target.pos, oppositeAdjacent, 6);
            WandOfBlastWave.throwChar(target, trajectory, 1, false, false, this);
            if (target == Dungeon.hero) {
                Dungeon.hero.interrupt();
            }
            rockCenter = trajectory.path.get(Math.min(trajectory.dist, 1));
        } else {
            rockCenter = target.pos;
        }
        while ((safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)]) == this.pos || Dungeon.level.solid[safeCell] && Random.Int(2) == 0 || Blob.volumeAt(safeCell, CavesBossLevel.PylonEnergy.class) > 0 && Random.Int(2) == 0) {
        }
        ArrayList<Integer> rockCells = new ArrayList<Integer>();
        int start = rockCenter - Dungeon.level.width() * 3 - 3;
        for (int y = 0; y < 7; ++y) {
            int pos = start + Dungeon.level.width() * y;
            for (int x = 0; x < 7; ++x) {
                if (!Dungeon.level.insideMap(pos)) {
                    ++pos;
                    continue;
                }
                if (!Dungeon.level.solid[pos] && pos != safeCell && Random.Int(Dungeon.level.distance(rockCenter, pos)) == 0) {
                    rockCells.add(pos);
                }
                ++pos;
            }
        }
        float delay = GameMath.gate(1.0f, (int)Math.ceil(target.cooldown()), 3.0f);
        Iterator iterator = rockCells.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            GameScene.targetedCell(i, delay);
        }
        Buff.append(this, FallingRockBuff.class, delay).setRockPositions(rockCells);
    }

    @Override
    public void damage(int dmg, Object src) {
        int threshold;
        LockedFloor lock;
        if (!BossHealthBar.isAssigned()) {
            this.notice();
        }
        int preHP = this.HP;
        super.damage(dmg, src);
        if (this.isInvulnerable(src.getClass())) {
            return;
        }
        int dmgTaken = preHP - this.HP;
        if (dmgTaken > 0 && (lock = Dungeon.hero.buff(LockedFloor.class)) != null && !this.isImmune(src.getClass()) && !this.isInvulnerable(src.getClass())) {
            if (Dungeon.isChallenged(256)) {
                lock.addTime((float)dmgTaken / 2.0f);
            } else {
                lock.addTime(dmgTaken);
            }
        }
        if (this.HP <= (threshold = Dungeon.isChallenged(256) ? this.HT / 4 * (3 - this.pylonsActivated) : this.HT / 3 * (2 - this.pylonsActivated)) && threshold > 0) {
            this.HP = threshold;
            this.supercharge();
        }
    }

    public int totalPylonsToActivate() {
        return Dungeon.isChallenged(256) ? 3 : 2;
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        if (this.supercharged && !this.invulnWarned) {
            this.invulnWarned = true;
            GLog.w(Messages.get(this, "charging_hint", new Object[0]), new Object[0]);
        }
        return this.supercharged || super.isInvulnerable(effect);
    }

    public void supercharge() {
        this.supercharged = true;
        ((CavesBossLevel)Dungeon.level).activatePylon();
        ++this.pylonsActivated;
        this.spend(Dungeon.isChallenged(256) ? 2.0f : 3.0f);
        this.yell(Messages.get(this, "charging", new Object[0]));
        this.sprite.showStatus(65280, Messages.get(this, "invulnerable", new Object[0]), new Object[0]);
        ((DM300Sprite)this.sprite).updateChargeState(true);
        ((DM300Sprite)this.sprite).charge();
        this.chargeAnnounced = false;
    }

    public boolean isSupercharged() {
        return this.supercharged;
    }

    public void loseSupercharge() {
        this.supercharged = false;
        ((DM300Sprite)this.sprite).updateChargeState(false);
        this.turnsSinceLastAbility = Math.min(this.turnsSinceLastAbility, 2);
        if (this.pylonsActivated < this.totalPylonsToActivate()) {
            this.yell(Messages.get(this, "charge_lost", new Object[0]));
        } else {
            this.yell(Messages.get(this, "pylons_destroyed", new Object[0]));
            BossHealthBar.bleed(true);
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(0.5f, new Callback(){

                        @Override
                        public void call() {
                            Music.INSTANCE.play("music/caves_boss_finale.ogg", true);
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || this.pylonsActivated < this.totalPylonsToActivate();
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        GameScene.bossSlain();
        Dungeon.level.unseal();
        int shards = Random.chances(new float[]{0.0f, 0.0f, 6.0f, 3.0f, 1.0f});
        for (int i = 0; i < shards; ++i) {
            int ofs;
            while (!Dungeon.level.passable[this.pos + (ofs = PathFinder.NEIGHBOURS8[Random.Int(8)])]) {
            }
            Dungeon.level.drop((Item)new MetalShard(), (int)(this.pos + ofs)).sprite.drop(this.pos);
        }
        Badges.validateBossSlain();
        if (Statistics.qualifiedForBossChallengeBadge) {
            Badges.validateBossChallengeCompleted();
        }
        Statistics.bossScores[2] = Statistics.bossScores[2] + 3000;
        LloydsBeacon beacon = Dungeon.hero.belongings.getItem(LloydsBeacon.class);
        if (beacon != null) {
            beacon.upgrade();
        }
        this.yell(Messages.get(this, "defeated", new Object[0]));
    }

    @Override
    protected boolean getCloser(int target) {
        if (super.getCloser(target)) {
            return true;
        }
        if (!this.supercharged || this.state != this.HUNTING || this.rooted || target == this.pos || Dungeon.level.adjacent(this.pos, target)) {
            return false;
        }
        int bestpos = this.pos;
        for (int i : PathFinder.NEIGHBOURS8) {
            if (Actor.findChar(this.pos + i) != null || !(Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(this.pos + i, target))) continue;
            bestpos = this.pos + i;
        }
        if (bestpos != this.pos) {
            Sample.INSTANCE.play("sounds/rocks.mp3");
            Rect gate = CavesBossLevel.gate;
            for (int i : PathFinder.NEIGHBOURS9) {
                if (Dungeon.level.map[this.pos + i] == 4 || Dungeon.level.map[this.pos + i] == 12) {
                    Point p = Dungeon.level.cellToPoint(this.pos + i);
                    if (p.y < gate.bottom && p.x >= gate.left - 2 && p.x < gate.right + 2 || !CavesBossLevel.diggableArea.inside(p)) continue;
                    Level.set(this.pos + i, 20);
                    GameScene.updateMap(this.pos + i);
                }
                if (Dungeon.level.blobs.get(WallOfLight.LightWall.class) == null) continue;
                Dungeon.level.blobs.get(WallOfLight.LightWall.class).clear(this.pos + i);
            }
            Dungeon.level.cleanWalls();
            Dungeon.observe();
            this.spend(Dungeon.isChallenged(256) ? 2.0f : 3.0f);
            bestpos = this.pos;
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(this.pos + i) != null || !Dungeon.level.openSpace[this.pos + i] || !(Dungeon.level.trueDistance(bestpos, target) > Dungeon.level.trueDistance(this.pos + i, target))) continue;
                bestpos = this.pos + i;
            }
            if (bestpos != this.pos) {
                this.move(bestpos);
            }
            PixelScene.shake(5.0f, 1.0f);
            return true;
        }
        return false;
    }

    @Override
    public String description() {
        Object desc = super.description();
        if (this.supercharged) {
            desc = (String)desc + "\n\n" + Messages.get(this, "desc_supercharged", new Object[0]);
        }
        return desc;
    }

    public static class FallingRockBuff
    extends DelayedRockFall {
        @Override
        public void affectChar(Char ch) {
            if (!(ch instanceof DM300) && !(ch instanceof Pylon)) {
                if (Dungeon.isChallenged(256)) {
                    ch.damage(Random.NormalIntRange(10, 20), this);
                } else {
                    ch.damage(Random.NormalIntRange(6, 12), this);
                }
                if (ch.isAlive()) {
                    Buff.prolong(ch, Paralysis.class, Dungeon.isChallenged(256) ? 5.0f : 3.0f);
                } else if (ch == Dungeon.hero) {
                    Dungeon.fail(this.target);
                    GLog.n(Messages.get(GnollGeomancer.class, "rockfall_kill", new Object[0]), new Object[0]);
                }
            }
        }
    }
}

