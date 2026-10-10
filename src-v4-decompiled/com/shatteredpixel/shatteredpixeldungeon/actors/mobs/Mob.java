/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Adrenaline;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AllyBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Amok;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Charm;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Corruption;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Dread;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.GreaterHaste;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Hunger;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MonkEnergy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Preparation;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Sleep;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SoulMark;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.SwarmIntelTracker;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Terror;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.duelist.Feint;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.rogue.ShadowClone;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.GuidingLight;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Wraith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.DirectableAlly;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.FloatingText;
import com.shatteredpixel.shatteredpixeldungeon.effects.Surprise;
import com.shatteredpixel.shatteredpixeldungeon.effects.Wound;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.KindofMisc;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.MasterThievesArmband;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.exotic.ExoticPotion;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.Ring;
import com.shatteredpixel.shatteredpixeldungeon.items.rings.RingOfWealth;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ExoticScroll;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfAggression;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ExoticCrystals;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.ShardOfOblivion;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.SpiritBow;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.enchantments.Lucky;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.darts.Dart;
import com.shatteredpixel.shatteredpixeldungeon.journal.Bestiary;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.VaultLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public abstract class Mob
extends Char {
    public AiState SLEEPING;
    public AiState HUNTING;
    public AiState INVESTIGATING;
    public AiState WANDERING;
    public AiState FLEEING;
    public AiState PASSIVE;
    public AiState state;
    public Class<? extends CharSprite> spriteClass;
    protected int target;
    public int defenseSkill;
    public int EXP;
    public int maxLvl;
    protected Char enemy;
    protected int enemyID;
    protected boolean enemySeen;
    protected boolean alerted;
    protected static final float TIME_TO_WAKE_UP = 1.0f;
    protected boolean firstAdded;
    private static final String STATE = "state";
    private static final String SEEN = "seen";
    private static final String TARGET = "target";
    private static final String MAX_LVL = "max_lvl";
    private static final String ENEMY_ID = "enemy_id";
    private static final String SWARM_TIME = "swarm_time";
    private static final String USING_STEALTH = "using_stealth";
    private static final String INVEST_TURNS = "invest_turns";
    private static final String WANDER_POSITIONS = "wander_positions";
    private static final String WANDER_POS_IDX = "wander_pos_idx";
    private float timeSeenAt;
    protected boolean intelligentAlly;
    protected Object loot;
    protected float lootChance;
    protected ArrayList<Char> recentlyAttackedBy;
    private boolean usingStealthGamePlay;
    private int investigatingTurns;
    private int wanderPosIdx;
    private int[] wanderPositions;
    private static ArrayList<Mob> heldAllies = new ArrayList();

    public Mob() {
        this.actPriority = -20;
        this.alignment = Char.Alignment.ENEMY;
        this.SLEEPING = new Sleeping();
        this.HUNTING = new Hunting();
        this.INVESTIGATING = new Investigating();
        this.WANDERING = new Wandering();
        this.FLEEING = new Fleeing();
        this.PASSIVE = new Passive();
        this.state = this.SLEEPING;
        this.target = -1;
        this.defenseSkill = 0;
        this.EXP = 1;
        this.maxLvl = 29;
        this.enemyID = -1;
        this.alerted = false;
        this.firstAdded = true;
        this.timeSeenAt = Float.MAX_VALUE;
        this.intelligentAlly = false;
        this.loot = null;
        this.lootChance = 0.0f;
        this.recentlyAttackedBy = new ArrayList();
        this.wanderPosIdx = 0;
    }

    @Override
    protected void onAdd() {
        if (this.firstAdded) {
            float percent = (float)this.HP / (float)this.HT;
            this.HT = Math.round((float)this.HT * AscensionChallenge.statModifier(this));
            this.HP = Math.round((float)this.HT * percent);
            this.firstAdded = false;
        }
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        if (this.state == this.SLEEPING) {
            bundle.put(STATE, "SLEEPING");
        } else if (this.state == this.WANDERING) {
            bundle.put(STATE, "WANDERING");
        } else if (this.state == this.INVESTIGATING) {
            bundle.put(STATE, "INVESTIGATING");
        } else if (this.state == this.HUNTING) {
            bundle.put(STATE, "HUNTING");
        } else if (this.state == this.FLEEING) {
            bundle.put(STATE, "FLEEING");
        } else if (this.state == this.PASSIVE) {
            bundle.put(STATE, "PASSIVE");
        }
        bundle.put(SEEN, this.enemySeen);
        bundle.put(TARGET, this.target);
        bundle.put(MAX_LVL, this.maxLvl);
        if (this.enemy != null) {
            bundle.put(ENEMY_ID, this.enemy.id());
        }
        bundle.put(SWARM_TIME, this.timeSeenAt);
        bundle.put(USING_STEALTH, this.usingStealthGamePlay);
        if (this.usingStealthGamePlay) {
            bundle.put(INVEST_TURNS, this.investigatingTurns);
            if (this.wanderPositions != null) {
                bundle.put(WANDER_POSITIONS, this.wanderPositions);
                bundle.put(WANDER_POS_IDX, this.wanderPosIdx);
            }
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        String state;
        super.restoreFromBundle(bundle);
        if (bundle.getBoolean(USING_STEALTH)) {
            this.activateSteathGameplayBehaviour();
            this.investigatingTurns = bundle.getInt(INVEST_TURNS);
            if (bundle.contains(WANDER_POSITIONS)) {
                this.wanderPositions = bundle.getIntArray(WANDER_POSITIONS);
                this.wanderPosIdx = bundle.getInt(WANDER_POS_IDX);
            }
        }
        if ((state = bundle.getString(STATE)).equals("SLEEPING")) {
            this.state = this.SLEEPING;
        } else if (state.equals("WANDERING")) {
            this.state = this.WANDERING;
        } else if (state.equals("INVESTIGATING")) {
            this.state = this.INVESTIGATING;
        } else if (state.equals("HUNTING")) {
            this.state = this.HUNTING;
        } else if (state.equals("FLEEING")) {
            this.state = this.FLEEING;
        } else if (state.equals("PASSIVE")) {
            this.state = this.PASSIVE;
        }
        this.enemySeen = bundle.getBoolean(SEEN);
        this.target = bundle.getInt(TARGET);
        this.maxLvl = bundle.getInt(MAX_LVL);
        if (bundle.contains(ENEMY_ID)) {
            this.enemyID = bundle.getInt(ENEMY_ID);
        }
        this.timeSeenAt = bundle.getFloat(SWARM_TIME);
        this.firstAdded = false;
    }

    public void restoreEnemy() {
        if (this.enemyID != -1 && this.enemy == null) {
            this.enemy = (Char)Actor.findById(this.enemyID);
        }
    }

    public CharSprite sprite() {
        return Reflection.newInstance(this.spriteClass);
    }

    @Override
    protected boolean act() {
        boolean enemyInFOV;
        super.act();
        boolean justAlerted = this.alerted;
        this.alerted = false;
        if (justAlerted) {
            if (this.sprite != null) this.sprite.showAlert();
        } else {
            if (this.sprite != null) this.sprite.hideAlert();
            if (this.sprite != null) this.sprite.hideLost();
            if (this.sprite != null) this.sprite.hideInvestigate();
        }
        if (this.paralysed > 0) {
            this.enemySeen = false;
            this.processSwarmIntel(false);
            this.spend(1.0f);
            return true;
        }
        if (this.buff(Terror.class) != null || this.buff(Dread.class) != null) {
            this.state = this.FLEEING;
        }
        this.enemy = this.chooseEnemy();
        boolean bl = enemyInFOV = this.enemy != null && this.enemy.isAlive() && this.fieldOfView[this.enemy.pos] && this.enemy.invisible <= 0;
        if (this.buff(Feint.AfterImage.FeintConfusion.class) != null) {
            this.enemySeen = enemyInFOV;
            this.spend(1.0f);
            return true;
        }
        AiState curState = this.state;
        boolean result = this.state.act(enemyInFOV, justAlerted);
        this.processSwarmIntel(enemyInFOV && this.state == curState);
        if (this.buff(PowerOfMany.PowerBuff.class) != null) {
            Dungeon.level.updateFieldOfView(this, this.fieldOfView);
            GameScene.updateFog(this.pos, this.viewDistance + (int)Math.ceil(this.speed()));
        }
        return result;
    }

    protected void processSwarmIntel(boolean enemyInFOV) {
        if (this.alignment == Char.Alignment.ENEMY && this.state == this.HUNTING && Dungeon.isChallenged(16) && enemyInFOV && this.enemy != null && this.enemy.alignment == Char.Alignment.ALLY) {
            if (this.timeSeenAt >= Mob.now()) {
                this.timeSeenAt = Mob.now() - 1.0f;
            }
            int range = this.swarmAlertRange();
            for (Mob mob : Dungeon.level.mobs) {
                if (mob.alignment != Char.Alignment.ENEMY || mob.paralysed > 0 || Dungeon.level.distance(this.pos, mob.pos) > range || mob.state == mob.HUNTING) continue;
                mob.beckon(this.enemy.pos);
            }
            Buff.affect(Dungeon.hero, SwarmIntelTracker.class);
        } else {
            this.timeSeenAt = Float.MAX_VALUE;
        }
    }

    public int swarmAlertRange() {
        int range = 2 * (int)Math.max(Mob.now() - this.timeSeenAt, 0.0f);
        return (int)GameMath.gate(0.0f, range, 12.0f);
    }

    @Override
    public void fixTime(float decrement) {
        if (this.swarmAlertRange() > 0) {
            this.timeSeenAt -= decrement;
        }
        super.fixTime(decrement);
    }

    protected Char chooseEnemy() {
        Char source;
        Char source2;
        Dread dread = this.buff(Dread.class);
        if (dread != null && (source2 = (Char)Actor.findById(dread.object)) != null) {
            return source2;
        }
        Terror terror = this.buff(Terror.class);
        if (terror != null && (source = (Char)Actor.findById(terror.object)) != null) {
            return source;
        }
        if ((this.alignment == Char.Alignment.ENEMY || this.buff(Amok.class) != null) && this.state != this.PASSIVE && this.state != this.SLEEPING) {
            if (this.enemy != null && this.enemy.buff(StoneOfAggression.Aggression.class) != null) {
                this.state = this.HUNTING;
                return this.enemy;
            }
            for (Char ch : Actor.chars()) {
                if (ch == this || !this.fieldOfView[ch.pos] || ch.buff(StoneOfAggression.Aggression.class) == null) continue;
                this.state = this.HUNTING;
                return ch;
            }
        }
        boolean newEnemy = false;
        if (this.enemy == null || !this.enemy.isAlive() || !Actor.chars().contains(this.enemy) || this.state == this.WANDERING) {
            newEnemy = true;
        } else if (this.buff(Amok.class) != null && this.enemy == Dungeon.hero) {
            newEnemy = true;
        } else if (this.buff(Charm.class) != null && this.buff(Charm.class).object == this.enemy.id()) {
            newEnemy = true;
        }
        if (!newEnemy && this.alignment == Char.Alignment.ALLY) {
            if (this.enemy.alignment == Char.Alignment.ALLY) {
                newEnemy = true;
            } else if (this.enemy.isInvulnerable(this.getClass())) {
                newEnemy = true;
            }
        }
        if (newEnemy) {
            Char source3;
            Charm charm;
            HashSet<Char> enemies = new HashSet<Char>();
            if (this.buff(Amok.class) != null) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.alignment != Char.Alignment.ENEMY || mob == this || !this.fieldOfView[mob.pos] || mob.invisible > 0) continue;
                    enemies.add(mob);
                }
                if (enemies.isEmpty()) {
                    for (Mob mob : Dungeon.level.mobs) {
                        if (mob.alignment != Char.Alignment.ALLY || mob == this || !this.fieldOfView[mob.pos] || mob.invisible > 0) continue;
                        enemies.add(mob);
                    }
                    if (enemies.isEmpty() && this.fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
                        enemies.add(Dungeon.hero);
                    }
                }
            } else if (this.alignment == Char.Alignment.ALLY) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.alignment != Char.Alignment.ENEMY || !this.fieldOfView[mob.pos] || mob.invisible > 0 || mob.isInvulnerable(this.getClass()) || mob.state == mob.PASSIVE || this.intelligentAlly && (mob.state == mob.SLEEPING || mob.state == mob.WANDERING)) continue;
                    enemies.add(mob);
                }
            } else if (this.alignment == Char.Alignment.ENEMY) {
                for (Mob mob : Dungeon.level.mobs) {
                    if (mob.alignment != Char.Alignment.ALLY || !this.fieldOfView[mob.pos] || mob.invisible > 0) continue;
                    enemies.add(mob);
                }
                if (this.fieldOfView[Dungeon.hero.pos] && Dungeon.hero.invisible <= 0) {
                    enemies.add(Dungeon.hero);
                }
            }
            if ((charm = this.buff(Charm.class)) != null && (source3 = (Char)Actor.findById(charm.object)) != null && enemies.contains(source3) && enemies.size() > 1) {
                enemies.remove(source3);
            }
            if (enemies.isEmpty()) {
                return null;
            }
            PathFinder.buildDistanceMap(this.pos, Dungeon.findPassable(this, Dungeon.level.passable, this.fieldOfView, true));
            Char closest = null;
            int closestDist = Integer.MAX_VALUE;
            for (Char curr : enemies) {
                int currDist = Integer.MAX_VALUE;
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (PathFinder.distance[curr.pos + i] >= currDist) continue;
                    currDist = PathFinder.distance[curr.pos + i];
                }
                if (closest == null) {
                    closest = curr;
                    closestDist = currDist;
                    continue;
                }
                if (this.canAttack(closest) && !this.canAttack(curr)) continue;
                if (this.canAttack(curr) && !this.canAttack(closest) || currDist < closestDist) {
                    closest = curr;
                    continue;
                }
                if ((curr != Dungeon.hero || currDist != closestDist) && (!this.canAttack(curr) || !this.canAttack(closest))) continue;
                closest = curr;
            }
            if (closest == Dungeon.hero) {
                for (Char ch : enemies) {
                    if (!(ch instanceof Feint.AfterImage)) continue;
                    closest = ch;
                    break;
                }
            }
            return closest;
        }
        return this.enemy;
    }

    @Override
    public boolean add(Buff buff) {
        if (super.add(buff)) {
            if (buff instanceof Amok || buff instanceof AllyBuff) {
                this.state = this.HUNTING;
            } else if (buff instanceof Terror || buff instanceof Dread) {
                this.state = this.FLEEING;
            } else if (buff instanceof Sleep) {
                this.state = this.SLEEPING;
                this.postpone(1.5f);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Buff buff) {
        if (super.remove(buff)) {
            if (this.state == this.FLEEING && (buff instanceof Terror && this.buff(Dread.class) == null || buff instanceof Dread && this.buff(Terror.class) == null)) {
                if (this.enemySeen) {
                    if (this.sprite != null) this.sprite.showStatus(0xFF8800, Messages.get(this, "rage", new Object[0]), new Object[0]);
                    this.state = this.HUNTING;
                } else {
                    this.state = this.WANDERING;
                }
            }
            return true;
        }
        return false;
    }

    protected boolean canAttack(Char enemy) {
        if (Dungeon.level.adjacent(this.pos, enemy.pos)) {
            return true;
        }
        for (ChampionEnemy buff : this.buffs(ChampionEnemy.class)) {
            if (!buff.canAttackWithExtraReach(enemy)) continue;
            return true;
        }
        return false;
    }

    private boolean cellIsPathable(int cell) {
        if (!Dungeon.level.passable[cell]) {
            if (this.flying || this.buff(Amok.class) != null) {
                if (!Dungeon.level.avoid[cell]) {
                    return false;
                }
            } else {
                return false;
            }
        }
        if (Char.hasProp(this, Char.Property.LARGE) && !Dungeon.level.openSpace[cell]) {
            return false;
        }
        return Actor.findChar(cell) == null;
    }

    protected boolean getCloser(int target) {
        if (this.rooted || target == this.pos || !Dungeon.level.insideMap(target)) {
            return false;
        }
        int step = -1;
        if (Dungeon.level.adjacent(this.pos, target)) {
            this.path = null;
            if (this.cellIsPathable(target)) {
                step = target;
            }
        } else {
            float longFactor;
            boolean newPath = false;
            float f = longFactor = this.state == this.WANDERING ? 2.0f : 1.33f;
            if (this.path == null || this.path.isEmpty() || !Dungeon.level.adjacent(this.pos, (Integer)this.path.getFirst()) || (float)this.path.size() > longFactor * (float)Dungeon.level.distance(this.pos, target)) {
                newPath = true;
            } else if ((Integer)this.path.getLast() != target) {
                if (Dungeon.level.adjacent(target, (Integer)this.path.getLast())) {
                    int last = (Integer)this.path.removeLast();
                    if (this.path.isEmpty()) {
                        if (Dungeon.level.adjacent(target, this.pos)) {
                            this.path.add(target);
                        } else {
                            this.path.add(last);
                            this.path.add(target);
                        }
                    } else if ((Integer)this.path.getLast() != target) {
                        if (Dungeon.level.adjacent(target, (Integer)this.path.getLast())) {
                            this.path.add(target);
                        } else {
                            this.path.add(last);
                            this.path.add(target);
                        }
                    }
                } else {
                    newPath = true;
                }
            }
            if (!newPath) {
                int nextCell = (Integer)this.path.removeFirst();
                if (!this.cellIsPathable(nextCell)) {
                    newPath = true;
                    if (!this.path.isEmpty()) {
                        for (int i : PathFinder.NEIGHBOURS8) {
                            if (!Dungeon.level.adjacent(this.pos, nextCell + i) || !Dungeon.level.adjacent(nextCell + i, (Integer)this.path.getFirst()) || !this.cellIsPathable(nextCell + i)) continue;
                            this.path.addFirst(nextCell + i);
                            newPath = false;
                            break;
                        }
                    }
                } else {
                    this.path.addFirst(nextCell);
                }
            }
            if (newPath) {
                PathFinder.Path full = Dungeon.findPath(this, target, Dungeon.level.passable, this.fieldOfView, true);
                if (this.state != this.HUNTING && this.state != this.INVESTIGATING) {
                    this.path = full;
                } else {
                    PathFinder.Path ignoreChars = Dungeon.findPath(this, target, Dungeon.level.passable, this.fieldOfView, false);
                    if (ignoreChars != null && (full == null || full.size() > 2 * ignoreChars.size())) {
                        this.path = ignoreChars;
                        if (!this.cellIsPathable((Integer)ignoreChars.getFirst())) {
                            return false;
                        }
                    } else {
                        this.path = full;
                    }
                }
            }
            if (this.path != null) {
                step = (Integer)this.path.removeFirst();
            } else {
                return false;
            }
        }
        if (step != -1) {
            this.move(step);
            return true;
        }
        return false;
    }

    protected boolean getFurther(int target) {
        if (this.rooted || target == this.pos) {
            return false;
        }
        int step = Dungeon.flee(this, target, Dungeon.level.passable, this.fieldOfView, true);
        if (step != -1) {
            this.move(step);
            return true;
        }
        return false;
    }

    @Override
    public void move(int step, boolean travelling) {
        super.move(step, travelling);
        if (this.usingStealthGamePlay && travelling && !this.sprite.visible && Dungeon.level.distance(this.pos, Dungeon.hero.pos) <= 6) {
            if (this.state == this.HUNTING) {
                WandOfBlastWave.BlastWave.blast(this.pos, 1.0f, 0xFF0000);
            } else if (this.state == this.INVESTIGATING) {
                WandOfBlastWave.BlastWave.blast(this.pos, 1.0f, 0xFF8800);
            } else {
                WandOfBlastWave.BlastWave.blast(this.pos, 1.0f);
            }
        }
    }

    @Override
    public void updateSpriteState() {
        super.updateSpriteState();
        if (Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class) != null || Dungeon.hero.buff(Swiftthistle.TimeBubble.class) != null) {
            if (this.sprite != null) this.sprite.add(CharSprite.State.PARALYSED);
        }
    }

    public float attackDelay() {
        float delay = 1.0f;
        if (this.buff(Adrenaline.class) != null) {
            delay /= 1.5f;
        }
        return delay;
    }

    protected boolean doAttack(Char enemy) {
        if (this.sprite != null && (this.sprite.visible || enemy.sprite.visible)) {
            if (this.sprite != null) this.sprite.attack(enemy.pos);
            return false;
        }
        this.attack(enemy);
        Invisibility.dispel(this);
        this.spend(this.attackDelay());
        return true;
    }

    @Override
    public void onAttackComplete() {
        this.attack(this.enemy);
        Invisibility.dispel(this);
        this.spend(this.attackDelay());
        super.onAttackComplete();
    }

    @Override
    public int defenseSkill(Char enemy) {
        if (this.buff(GuidingLight.Illuminated.class) != null && Dungeon.hero.heroClass == HeroClass.CLERIC) {
            if (enemy instanceof Hero) {
                Hero h = (Hero)enemy;
                if (!(h.belongings.attackingWeapon() instanceof Weapon) || ((Weapon)h.belongings.attackingWeapon()).STRReq() <= h.STR()) {
                    return 0;
                }
            } else {
                return 0;
            }
        }
        if (!(this.surprisedBy(enemy) || this.paralysed != 0 || this.alignment == Char.Alignment.ALLY && enemy == Dungeon.hero)) {
            return this.defenseSkill;
        }
        return 0;
    }

    @Override
    public int defenseProc(Char enemy, int damage) {
        if (enemy instanceof Hero && ((Hero)enemy).belongings.attackingWeapon() instanceof MissileWeapon) {
            ++Statistics.thrownAttacks;
            Badges.validateHuntressUnlock();
        }
        if (this.surprisedBy(enemy)) {
            ++Statistics.sneakAttacks;
            Badges.validateRogueUnlock();
            if (Dungeon.hero.belongings.attackingWeapon() instanceof SpiritBow.SpiritArrow || Dungeon.hero.belongings.attackingWeapon() instanceof Dart) {
                Sample.INSTANCE.playDelayed("sounds/hit_strong.mp3", 0.125f);
            } else {
                Sample.INSTANCE.play("sounds/hit_strong.mp3");
            }
            if (enemy.buff(Preparation.class) != null) {
                Wound.hit(this);
            } else {
                Surprise.hit(this);
            }
        }
        if (this.state != this.FLEEING) {
            if (this.state != this.HUNTING) {
                this.aggro(enemy);
                this.target = enemy.pos;
            } else {
                this.recentlyAttackedBy.add(enemy);
            }
        }
        if (this.buff(SoulMark.class) != null) {
            int restoration = Math.min(damage, this.HP + this.shielding());
            if (enemy != Dungeon.hero) {
                restoration = Math.round((float)restoration * 0.4f * (float)Dungeon.hero.pointsInTalent(Talent.SOUL_SIPHON) / 3.0f);
            }
            if (restoration > 0) {
                Buff.affect(Dungeon.hero, Hunger.class).affectHunger((float)(restoration * Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)) / 3.0f);
                if (Dungeon.hero.HP < Dungeon.hero.HT) {
                    int heal = (int)Math.ceil((float)restoration * 0.4f);
                    Dungeon.hero.HP = Math.min(Dungeon.hero.HT, Dungeon.hero.HP + heal);
                    if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(heal), FloatingText.HEALING, new Object[0]);
                }
            }
        }
        return super.defenseProc(enemy, damage);
    }

    @Override
    public float speed() {
        return super.speed() * AscensionChallenge.enemySpeedModifier(this);
    }

    public final boolean surprisedBy(Char enemy) {
        return this.surprisedBy(enemy, true);
    }

    public boolean surprisedBy(Char enemy, boolean attacking) {
        return enemy == Dungeon.hero && (enemy.invisible > 0 || !this.enemySeen || this.fieldOfView != null && this.fieldOfView.length == Dungeon.level.length() && !this.fieldOfView[enemy.pos]) && (!attacking || enemy.canSurpriseAttack());
    }

    public boolean heroShouldInteract() {
        return this.alignment != Char.Alignment.ENEMY && this.buff(Amok.class) == null;
    }

    public void aggro(Char ch) {
        this.enemy = ch;
        if (this.state != this.PASSIVE) {
            this.state = this.HUNTING;
        }
    }

    public void clearEnemy() {
        this.enemy = null;
        this.enemySeen = false;
        if (this.state == this.HUNTING) {
            this.state = this.WANDERING;
        }
    }

    public boolean isTargeting(Char ch) {
        return this.enemy == ch;
    }

    @Override
    public void damage(int dmg, Object src) {
        if (!this.isInvulnerable(src.getClass())) {
            if (this.state == this.SLEEPING) {
                this.state = this.WANDERING;
            }
            if (!(src instanceof Corruption) && this.state != this.FLEEING) {
                if (this.state != this.HUNTING) {
                    this.alerted = true;
                    if (src instanceof Wand || src instanceof ClericSpell || src instanceof ArmorAbility) {
                        this.aggro(Dungeon.hero);
                        this.target = Dungeon.hero.pos;
                    }
                } else if (src instanceof Wand || src instanceof ClericSpell || src instanceof ArmorAbility) {
                    this.recentlyAttackedBy.add(Dungeon.hero);
                }
            }
        }
        super.damage(dmg, src);
    }

    @Override
    public void destroy() {
        super.destroy();
        Dungeon.level.mobs.remove(this);
        if (Dungeon.hero.buff(MindVision.class) != null) {
            Dungeon.observe();
            GameScene.updateFog(this.pos, 2);
        }
        if (Dungeon.hero.isAlive() && this.alignment == Char.Alignment.ENEMY) {
            int exp;
            ++Statistics.enemiesSlain;
            Badges.validateMonstersSlain();
            Statistics.qualifiedForNoKilling = false;
            Bestiary.setSeen(this.getClass());
            Bestiary.countEncounter(this.getClass());
            AscensionChallenge.processEnemyKill(this);
            int n = exp = Dungeon.hero.lvl <= this.maxLvl ? this.EXP : 0;
            if (Dungeon.hero.buff(AscensionChallenge.class) != null && exp == 0 && this.maxLvl > 0 && this.EXP > 0 && Dungeon.hero.lvl < 30) {
                exp = Math.round(10.0f * this.spawningWeight());
            }
            if (exp > 0) {
                if (Dungeon.hero.sprite != null) Dungeon.hero.sprite.showStatusWithIcon(65280, Integer.toString(exp), FloatingText.EXPERIENCE, new Object[0]);
            }
            Dungeon.hero.earnExp(exp, this.getClass());
            if (exp == 0 && Dungeon.level instanceof VaultLevel) {
                KindofMisc misc;
                Ring ring = Dungeon.hero.belongings.ring();
                if (ring != null) {
                    ((Item)ring).onHeroGainExp(0.2f, Dungeon.hero);
                }
                if ((misc = Dungeon.hero.belongings.misc()) instanceof Ring) {
                    misc.onHeroGainExp(0.2f, Dungeon.hero);
                }
            }
            if (Dungeon.hero.subClass == HeroSubClass.MONK) {
                Buff.affect(Dungeon.hero, MonkEnergy.class).gainEnergy(this);
            }
        }
    }

    @Override
    public void die(Object cause) {
        Wraith w;
        if (cause == Chasm.class) {
            if (this.EXP % 2 == 1) {
                this.EXP += Random.Int(2);
            }
            this.EXP /= 2;
        }
        if (this.alignment == Char.Alignment.ENEMY) {
            if (this.buff(Trap.HazardAssistTracker.class) != null) {
                ++Statistics.hazardAssistedKills;
                Badges.validateHazardAssists();
            }
            this.rollToDropLoot();
            if (cause == Dungeon.hero || cause instanceof Weapon || cause instanceof Weapon.Enchantment) {
                if (Dungeon.hero.hasTalent(Talent.LETHAL_MOMENTUM) && Random.Float() < 0.34f + 0.33f * (float)Dungeon.hero.pointsInTalent(Talent.LETHAL_MOMENTUM)) {
                    Buff.affect(Dungeon.hero, Talent.LethalMomentumTracker.class, 0.0f);
                }
                if (Dungeon.hero.heroClass != HeroClass.DUELIST && Dungeon.hero.hasTalent(Talent.LETHAL_HASTE) && Dungeon.hero.buff(Talent.LethalHasteCooldown.class) == null) {
                    Buff.affect(Dungeon.hero, Talent.LethalHasteCooldown.class, 100.0f);
                    Buff.affect(Dungeon.hero, GreaterHaste.class).set(2 + 2 * Dungeon.hero.pointsInTalent(Talent.LETHAL_HASTE));
                }
            }
        }
        if (Dungeon.hero.isAlive() && !Dungeon.level.heroFOV[this.pos]) {
            GLog.i(Messages.get(this, "died", new Object[0]), new Object[0]);
        }
        boolean soulMarked = this.buff(SoulMark.class) != null;
        super.die(cause);
        if (!(this instanceof Wraith) && soulMarked && Random.Float() < 0.4f * (float)Dungeon.hero.pointsInTalent(Talent.NECROMANCERS_MINIONS) / 3.0f && (w = Wraith.spawnAt(this.pos, Wraith.class)) != null) {
            Buff.affect(w, Corruption.class);
            if (Dungeon.level.heroFOV[this.pos]) {
                CellEmitter.get(this.pos).burst(ShadowParticle.CURSE, 6);
                Sample.INSTANCE.play("sounds/cursed.mp3");
            }
        }
    }

    public float lootChance() {
        Preparation prep;
        float lootChance = this.lootChance;
        float dropBonus = RingOfWealth.dropChanceMultiplier(Dungeon.hero);
        Talent.BountyHunterTracker bhTracker = Dungeon.hero.buff(Talent.BountyHunterTracker.class);
        if (bhTracker != null && (prep = Dungeon.hero.buff(Preparation.class)) != null) {
            float bhBonus = 0.02f * (float)Math.pow(2.0, prep.attackLevel() - 1);
            dropBonus += (bhBonus *= (float)Dungeon.hero.pointsInTalent(Talent.BOUNTY_HUNTER));
        }
        return lootChance * (dropBonus += ShardOfOblivion.lootChanceMultiplier() - 1.0f);
    }

    public void rollToDropLoot() {
        Item loot;
        if (Dungeon.hero.lvl > this.maxLvl + 2) {
            return;
        }
        MasterThievesArmband.StolenTracker stolen = this.buff(MasterThievesArmband.StolenTracker.class);
        if ((stolen == null || !stolen.itemWasStolen()) && Random.Float() < this.lootChance() && (loot = this.createLoot()) != null) {
            com.shatteredpixel.shatteredpixeldungeon.items.Heap dh = Dungeon.level.drop((Item)loot, (int)this.pos); if (dh.sprite != null) dh.sprite.drop();
        }
        if (Ring.getBuffedBonus(Dungeon.hero, RingOfWealth.Wealth.class) > 0) {
            int rolls = 1;
            if (this.properties.contains((Object)Char.Property.BOSS)) {
                rolls = 15;
            } else if (this.properties.contains((Object)Char.Property.MINIBOSS)) {
                rolls = 5;
            }
            ArrayList<Item> bonus = RingOfWealth.tryForBonusDrop(Dungeon.hero, rolls);
            if (bonus != null && !bonus.isEmpty()) {
                for (Item b : bonus) {
                    com.shatteredpixel.shatteredpixeldungeon.items.Heap dh2 = Dungeon.level.drop((Item)b, (int)this.pos); if (dh2.sprite != null) dh2.sprite.drop();
                }
                if (this.sprite != null) RingOfWealth.showFlareForBonusDrop(this.sprite);
            }
        }
        if (this.buff(Lucky.LuckProc.class) != null) {
            com.shatteredpixel.shatteredpixeldungeon.items.Heap dh3 = Dungeon.level.drop((Item)this.buff(Lucky.LuckProc.class).genLoot(), (int)this.pos); if (dh3.sprite != null) dh3.sprite.drop();
            if (this.sprite != null) Lucky.showFlare(this.sprite);
        }
        if (this.buff(SoulMark.class) != null && Random.Int(10) < Dungeon.hero.pointsInTalent(Talent.SOUL_EATER)) {
            Talent.onFoodEaten(Dungeon.hero, 0.0f, null);
        }
    }

    public Item createLoot() {
        Item item;
        if (this.loot instanceof Generator.Category) {
            item = Generator.randomUsingDefaults((Generator.Category)((Object)this.loot));
        } else if (this.loot instanceof Class) {
            if (ExoticPotion.regToExo.containsKey(this.loot)) {
                if (Random.Float() < ExoticCrystals.consumableExoticChance()) {
                    return Generator.random(ExoticPotion.regToExo.get(this.loot));
                }
            } else if (ExoticScroll.regToExo.containsKey(this.loot) && Random.Float() < ExoticCrystals.consumableExoticChance()) {
                return Generator.random(ExoticScroll.regToExo.get(this.loot));
            }
            item = Generator.random((Class)this.loot);
        } else {
            item = (Item)this.loot;
        }
        return item;
    }

    public float spawningWeight() {
        return 1.0f;
    }

    public boolean reset() {
        return false;
    }

    public void beckon(int cell) {
        this.notice();
        if (this.state != this.HUNTING && this.state != this.FLEEING) {
            this.state = this.WANDERING;
        }
        this.target = cell;
    }

    public String description() {
        return Messages.get(this, "desc", new Object[0]);
    }

    public String info() {
        String desc = this.description();
        for (Buff buff : this.buffs(ChampionEnemy.class)) {
            desc = (String)desc + "\n\n_" + Messages.titleCase(buff.name()) + "_\n" + buff.desc();
        }
        return desc;
    }

    public void notice() {
        if (this.sprite != null) this.sprite.showAlert();
    }

    public void yell(String str) {
        GLog.newLine();
        GLog.n("%s: \"%s\" ", Messages.titleCase(this.name()), str);
    }

    public Notes.Landmark landmark() {
        return null;
    }

    public void activateSteathGameplayBehaviour() {
        if (!this.usingStealthGamePlay) {
            this.usingStealthGamePlay = true;
            AiState oldInvestigate = this.INVESTIGATING;
            this.INVESTIGATING = new StealthGameplayInvestigating();
            if (this.state == oldInvestigate) {
                this.state = this.INVESTIGATING;
            }
            AiState oldWander = this.WANDERING;
            this.WANDERING = new StealthGameplayWandering();
            if (this.state == oldWander) {
                this.state = this.WANDERING;
            }
            AiState oldSleep = this.SLEEPING;
            this.SLEEPING = new StealthGameplaySleeping();
            if (this.state == oldSleep) {
                this.state = this.SLEEPING;
            }
        }
    }

    public void setupStealthGameplayWanderPositions(int[] wanderPositions, int startingIdx) {
        this.wanderPositions = wanderPositions;
        this.wanderPosIdx = startingIdx;
    }

    public static void holdAllies(Level level) {
        Mob.holdAllies(level, Dungeon.hero.pos);
    }

    public static void holdAllies(Level level, int holdFromPos) {
        heldAllies.clear();
        for (Mob mob : level.mobs.toArray(new Mob[0])) {
            if (mob instanceof DirectableAlly || mob.intelligentAlly && PowerOfMany.getPoweredAlly() == mob) {
                if (mob instanceof DirectableAlly) {
                    ((DirectableAlly)mob).clearDefensingPos();
                }
                level.mobs.remove(mob);
                heldAllies.add(mob);
                continue;
            }
            if (mob.alignment != Char.Alignment.ALLY || !mob.intelligentAlly || Dungeon.level.distance(holdFromPos, mob.pos) > 5) continue;
            level.mobs.remove(mob);
            heldAllies.add(mob);
        }
    }

    public static void restoreAllies(Level level, int pos) {
        Mob.restoreAllies(level, pos, -1);
    }

    public static void restoreAllies(Level level, int pos, final int gravitatePos) {
        if (!heldAllies.isEmpty()) {
            ArrayList<Integer> candidatePositions = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Dungeon.level.solid[i + pos] || Dungeon.level.avoid[i + pos] || level.findMob(i + pos) != null) continue;
                candidatePositions.add(i + pos);
            }
            if (gravitatePos == -1) {
                Collections.shuffle(candidatePositions);
            } else {
                Collections.sort(candidatePositions, new Comparator<Integer>(){

                    @Override
                    public int compare(Integer t1, Integer t2) {
                        return Dungeon.level.distance(gravitatePos, t1) - Dungeon.level.distance(gravitatePos, t2);
                    }
                });
            }
            if (Stasis.getStasisAlly() != null) {
                for (Mob mob : level.mobs.toArray(new Mob[0])) {
                    if (mob.buff(PowerOfMany.PowerBuff.class) == null) continue;
                    mob.buff(PowerOfMany.PowerBuff.class).detach();
                }
            }
            java.util.Iterator<Mob> object = heldAllies.iterator();
            while (object.hasNext()) {
                Mob ally = (Mob)object.next();
                if (ally.buff(PowerOfMany.PowerBuff.class) != null) {
                    for (Mob mob : level.mobs.toArray(new Mob[0])) {
                        if (mob.buff(PowerOfMany.PowerBuff.class) == null) continue;
                        mob.buff(PowerOfMany.PowerBuff.class).detach();
                    }
                }
                level.mobs.add(ally);
                ally.state = ally.WANDERING;
                ally.pos = !candidatePositions.isEmpty() ? (Integer)candidatePositions.remove(0) : pos;
                if (ally.sprite != null) {
                    ally.sprite.place(ally.pos);
                }
                if (ally.fieldOfView == null || ally.fieldOfView.length != level.length()) {
                    ally.fieldOfView = new boolean[level.length()];
                }
                Dungeon.level.updateFieldOfView(ally, ally.fieldOfView);
            }
        }
        heldAllies.clear();
    }

    public static void clearHeldAllies() {
        heldAllies.clear();
    }

    protected class Sleeping
    implements AiState {
        public static final String TAG = "SLEEPING";

        protected Sleeping() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            for (Buff b : Mob.this.buffs()) {
                if (b.type != Buff.buffType.NEGATIVE) continue;
                this.awaken(enemyInFOV);
                if (Mob.this.state == Mob.this.SLEEPING) {
                    Mob.this.spend(1.0f);
                }
                return true;
            }
            if (enemyInFOV || Mob.this.enemy != null && Mob.this.enemy.invisible > 0) {
                float highestChance = Float.POSITIVE_INFINITY;
                Char closestHostile = null;
                for (Char ch : Actor.chars()) {
                    if (!Mob.this.fieldOfView[ch.pos] || ch.invisible != 0 || ch.alignment == Mob.this.alignment || ch.alignment == Char.Alignment.NEUTRAL) continue;
                    float bestChance = this.detectionChance(ch);
                    if ((ch instanceof Hero || ch instanceof ShadowClone.ShadowAlly) && Dungeon.hero.hasTalent(Talent.SILENT_STEPS) && Mob.this.distance(ch) >= 4 - Dungeon.hero.pointsInTalent(Talent.SILENT_STEPS)) {
                        bestChance = Float.POSITIVE_INFINITY;
                    }
                    if (ch.flying && Mob.this.distance(ch) >= 2) {
                        bestChance = Float.POSITIVE_INFINITY;
                    }
                    if (!(bestChance < highestChance)) continue;
                    highestChance = bestChance;
                    closestHostile = ch;
                }
                if (closestHostile != null && Random.Float() < this.detectionChance(closestHostile)) {
                    this.awaken(enemyInFOV);
                    if (Mob.this.state == Mob.this.SLEEPING) {
                        Mob.this.spend(1.0f);
                    }
                    return true;
                }
            }
            Mob.this.enemySeen = false;
            Mob.this.spend(1.0f);
            return true;
        }

        protected float detectionChance(Char enemy) {
            return 1.0f / ((float)Mob.this.distance(enemy) + enemy.stealth());
        }

        protected void awaken(boolean enemyInFOV) {
            if (enemyInFOV) {
                Mob.this.enemySeen = true;
                Mob.this.notice();
                Mob.this.state = Mob.this.HUNTING;
                Mob.this.target = Mob.this.enemy.pos;
            } else {
                Mob.this.notice();
                Mob.this.state = Mob.this.WANDERING;
                Mob.this.target = Dungeon.level.randomDestination(Mob.this);
            }
            Mob.this.spend(1.0f);
        }
    }

    public static interface AiState {
        public boolean act(boolean var1, boolean var2);
    }

    protected class Hunting
    implements AiState {
        public static final String TAG = "HUNTING";
        protected boolean recursing = false;

        protected Hunting() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Mob.this.enemySeen = enemyInFOV;
            if (enemyInFOV && !Mob.this.isCharmedBy(Mob.this.enemy) && Mob.this.canAttack(Mob.this.enemy)) {
                Mob.this.recentlyAttackedBy.clear();
                Mob.this.target = Mob.this.enemy.pos;
                return Mob.this.doAttack(Mob.this.enemy);
            }
            if (this.handleRecentAttackers()) {
                return this.act(true, justAlerted);
            }
            if (enemyInFOV) {
                Mob.this.target = Mob.this.enemy.pos;
            } else if (Mob.this.enemy == null) {
                Mob.this.sprite.showLost();
                Mob.this.state = Mob.this.WANDERING;
                Mob.this.target = ((Wandering)Mob.this.WANDERING).randomDestination();
                Mob.this.spend(1.0f);
                return true;
            }
            int oldPos = Mob.this.pos;
            if (Mob.this.target != -1 && Mob.this.getCloser(Mob.this.target)) {
                Mob.this.spend(1.0f / Mob.this.speed());
                return Mob.this.moveSprite(oldPos, Mob.this.pos);
            }
            return this.handleUnreachableTarget(enemyInFOV, justAlerted);
        }

        protected boolean handleRecentAttackers() {
            boolean swapped = false;
            if (!Mob.this.recentlyAttackedBy.isEmpty()) {
                for (Char ch : Mob.this.recentlyAttackedBy) {
                    if (ch == null || !ch.isActive() || !Actor.chars().contains(ch) || Mob.this.alignment == ch.alignment || !Mob.this.fieldOfView[ch.pos] || ch.invisible != 0 || Mob.this.isCharmedBy(ch) || !Mob.this.canAttack(ch) && Mob.this.enemy != null && Dungeon.level.distance(Mob.this.pos, ch.pos) >= Dungeon.level.distance(Mob.this.pos, Mob.this.enemy.pos)) continue;
                    Mob.this.enemy = ch;
                    Mob.this.target = ch.pos;
                    swapped = true;
                }
                Mob.this.recentlyAttackedBy.clear();
            }
            return swapped;
        }

        protected boolean handleUnreachableTarget(boolean enemyInFOV, boolean justAlerted) {
            if (!this.recursing) {
                Char oldEnemy = Mob.this.enemy;
                Mob.this.enemy = null;
                Mob.this.enemy = Mob.this.chooseEnemy();
                if (Mob.this.enemy != null && Mob.this.enemy != oldEnemy) {
                    this.recursing = true;
                    boolean result = this.act(enemyInFOV, justAlerted);
                    this.recursing = false;
                    return result;
                }
            }
            Mob.this.spend(1.0f);
            if (!enemyInFOV) {
                if (Mob.this.sprite != null) Mob.this.sprite.showLost();   // headless sprite null 保护
                Mob.this.state = Mob.this.WANDERING;
                Mob.this.target = ((Wandering)Mob.this.WANDERING).randomDestination();
            }
            return true;
        }
    }

    protected class Investigating
    extends Wandering {
        public static final String TAG = "INVESTIGATING";

        protected Investigating() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV) {
                Mob.this.target = Mob.this.enemy.pos;
            } else if (Dungeon.level.distance(Mob.this.pos, Mob.this.target) <= 1) {
                Mob.this.sprite.showLost();
                Mob.this.state = Mob.this.WANDERING;
                Mob.this.target = ((Wandering)Mob.this.WANDERING).randomDestination();
                Mob.this.spend(1.0f);
                return true;
            }
            return super.act(enemyInFOV, justAlerted);
        }
    }

    protected class Wandering
    implements AiState {
        public static final String TAG = "WANDERING";

        protected Wandering() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (enemyInFOV && (justAlerted || Random.Float() < this.detectionChance(Mob.this.enemy))) {
                return this.noticeEnemy();
            }
            return this.continueWandering();
        }

        protected float detectionChance(Char enemy) {
            return 1.0f / ((float)Mob.this.distance(enemy) / 2.0f + enemy.stealth());
        }

        protected boolean noticeEnemy() {
            Mob.this.enemySeen = true;
            Mob.this.notice();
            Mob.this.alerted = true;
            Mob.this.state = Mob.this.HUNTING;
            Mob.this.target = Mob.this.enemy.pos;
            return true;
        }

        protected boolean continueWandering() {
            Mob.this.enemySeen = false;
            int oldPos = Mob.this.pos;
            if (Mob.this.target != -1 && Mob.this.getCloser(Mob.this.target)) {
                Mob.this.spend(1.0f / Mob.this.speed());
                return Mob.this.moveSprite(oldPos, Mob.this.pos);
            }
            Mob.this.target = this.randomDestination();
            Mob.this.spend(1.0f);
            return true;
        }

        protected int randomDestination() {
            return Dungeon.level.randomDestination(Mob.this);
        }
    }

    protected class Fleeing
    implements AiState {
        public static final String TAG = "FLEEING";

        protected Fleeing() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Mob.this.enemySeen = enemyInFOV;
            if (Mob.this.enemy == null || !enemyInFOV && 1 + Random.Int(Dungeon.level.distance(Mob.this.pos, Mob.this.target)) >= 6) {
                this.escaped();
                if (Mob.this.state != Mob.this.FLEEING) {
                    Mob.this.spend(1.0f);
                    return true;
                }
            } else if (enemyInFOV) {
                Mob.this.target = Mob.this.enemy.pos;
            }
            int oldPos = Mob.this.pos;
            if (Mob.this.target != -1 && Mob.this.getFurther(Mob.this.target)) {
                Mob.this.spend(1.0f / Mob.this.speed());
                return Mob.this.moveSprite(oldPos, Mob.this.pos);
            }
            Mob.this.spend(1.0f);
            this.nowhereToRun();
            return true;
        }

        protected void escaped() {
        }

        protected void nowhereToRun() {
            if (Mob.this.buff(Terror.class) == null && Mob.this.buff(Dread.class) == null) {
                if (Mob.this.enemySeen) {
                    Mob.this.sprite.showStatus(0xFF8800, Messages.get(Mob.class, "rage", new Object[0]), new Object[0]);
                    Mob.this.state = Mob.this.HUNTING;
                } else {
                    Mob.this.state = Mob.this.WANDERING;
                }
            }
        }
    }

    protected class Passive
    implements AiState {
        public static final String TAG = "PASSIVE";

        protected Passive() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Mob.this.enemySeen = enemyInFOV;
            Mob.this.spend(1.0f);
            return true;
        }
    }

    protected class StealthGameplayInvestigating
    extends Investigating {
        protected StealthGameplayInvestigating() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            Mob.this.investigatingTurns = enemyInFOV ? ++Mob.this.investigatingTurns : 0;
            return super.act(enemyInFOV, justAlerted);
        }

        @Override
        protected float detectionChance(Char enemy) {
            float chance = 1.0f / ((float)Mob.this.distance(enemy) / 2.0f + enemy.stealth());
            if (Mob.this.investigatingTurns == 1 && chance <= 1.0f) {
                chance -= 0.33f;
            }
            return chance;
        }
    }

    protected class StealthGameplayWandering
    extends Wandering {
        protected StealthGameplayWandering() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            return super.act(enemyInFOV, justAlerted);
        }

        @Override
        protected float detectionChance(Char enemy) {
            if (!Dungeon.level.adjacent(Mob.this.pos, Mob.this.previousPos)) {
                return 1.0f / ((float)Mob.this.distance(enemy) + enemy.stealth());
            }
            float movementDir = PointF.angle(Dungeon.level.cellToPoint(Mob.this.previousPos), Dungeon.level.cellToPoint(Mob.this.pos)) / ((float)Math.PI / 180);
            float enemyDir = PointF.angle(Dungeon.level.cellToPoint(Mob.this.pos), Dungeon.level.cellToPoint(enemy.pos)) / ((float)Math.PI / 180);
            if (Math.abs(enemyDir - movementDir) <= 37.5f && Dungeon.level.distance(Mob.this.pos, enemy.pos) <= 6) {
                return 1.0f / ((float)Mob.this.distance(enemy) / 2.0f + enemy.stealth());
            }
            if (Math.abs(enemyDir - movementDir) <= 75.0f) {
                return 1.0f / ((float)Mob.this.distance(enemy) + enemy.stealth());
            }
            float chance = 1.0f / (float)Math.pow((float)Mob.this.distance(enemy) + enemy.stealth(), 3.0);
            if (chance < 0.1f) {
                return 0.0f;
            }
            return chance;
        }

        @Override
        protected boolean noticeEnemy() {
            super.noticeEnemy();
            Mob.this.alerted = false;
            Mob.this.state = Mob.this.INVESTIGATING;
            Mob.this.investigatingTurns = 0;
            Mob.this.sprite.showInvestigate();
            Mob.this.spend(1.0f);
            if (!Dungeon.level.heroFOV[Mob.this.pos]) {
                Buff.affect((Char)Dungeon.hero, TalismanOfForesight.CharAwareness.class, (float)1.0f).charID = Mob.this.id();
            }
            return true;
        }

        @Override
        protected int randomDestination() {
            int wanderPos;
            if (Mob.this.wanderPositions == null) {
                Mob.this.wanderPositions = new int[1];
                Mob.this.wanderPositions[0] = Mob.this.pos;
            }
            if ((wanderPos = Mob.this.wanderPositions[Mob.this.wanderPosIdx]) == Mob.this.pos) {
                if (Mob.this.wanderPositions.length > 1) {
                    ++Mob.this.wanderPosIdx;
                    if (Mob.this.wanderPosIdx == Mob.this.wanderPositions.length) {
                        Mob.this.wanderPosIdx = 0;
                    }
                    wanderPos = Mob.this.wanderPositions[Mob.this.wanderPosIdx];
                } else {
                    Mob.this.previousPos = Mob.this.pos;
                    Mob.this.sprite.idle();
                }
            }
            return wanderPos;
        }
    }

    protected class StealthGameplaySleeping
    extends Sleeping {
        protected StealthGameplaySleeping() {
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (Mob.this.wanderPositions == null) {
                Mob.this.wanderPositions = new int[1];
                Mob.this.wanderPositions[0] = Mob.this.pos;
            }
            return super.act(enemyInFOV, justAlerted);
        }

        @Override
        protected void awaken(boolean enemyInFOV) {
            super.awaken(enemyInFOV);
            if (Mob.this.state == Mob.this.HUNTING) {
                Mob.this.alerted = false;
                Mob.this.state = Mob.this.INVESTIGATING;
                Mob.this.investigatingTurns = 0;
                Mob.this.sprite.showInvestigate();
                if (!Dungeon.level.heroFOV[Mob.this.pos]) {
                    Buff.affect((Char)Dungeon.hero, TalismanOfForesight.CharAwareness.class, (float)1.0f).charID = Mob.this.id();
                }
            }
        }

        @Override
        protected float detectionChance(Char enemy) {
            float chance = 1.0f / (float)Math.pow((float)Mob.this.distance(enemy) + enemy.stealth(), 2.0);
            if (chance < 0.1f) {
                return 0.0f;
            }
            return chance;
        }
    }
}

