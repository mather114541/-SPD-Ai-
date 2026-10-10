/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Invisibility;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Paralysis;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DelayedRockFall;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollSapper;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfTeleportation;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfBlastWave;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.Ballistica;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.GnollGeomancerSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.MissileSprite;
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
import java.util.ArrayList;
import java.util.Iterator;

public class GnollGeomancer
extends Mob {
    private int abilityCooldown;
    private boolean lastAbilityWasRockfall;
    private int[] throwingRocksFromPos;
    private int throwingRockToPos;
    private int sapperID;
    private int[] sapperSpawns;
    int hits;
    private boolean inFinalBracket;
    private static int rocksInFlight = 0;
    private static ArrayList<Char> knockedChars = new ArrayList();
    public static final String HITS = "hits";
    private static final String ABILITY_COOLDOWN = "ability_cooldown";
    private static final String LAST_ABILITY_WAS_ROCKFALL = "last_ability_was_rockfall";
    private static final String ROCK_FROM_POS = "rock_from_pos";
    private static final String ROCK_TO_POS = "rock_to_pos";
    private static final String SAPPER_ID = "sapper_id";
    private static final String SAPPER_SPAWNS = "sapper_spawns";

    public GnollGeomancer() {
        this.HT = 150;
        this.HP = 150;
        this.spriteClass = GnollGeomancerSprite.class;
        this.EXP = 20;
        this.actPriority = -21;
        this.SLEEPING = new Sleeping();
        this.HUNTING = new Hunting();
        this.state = this.SLEEPING;
        this.viewDistance = 12;
        this.properties.add(Char.Property.BOSS);
        this.properties.add(Char.Property.IMMOVABLE);
        this.abilityCooldown = Random.NormalIntRange(3, 5);
        this.throwingRocksFromPos = null;
        this.throwingRockToPos = -1;
        this.sapperID = -1;
        this.sapperSpawns = null;
        this.hits = 0;
        this.inFinalBracket = false;
    }

    @Override
    protected boolean act() {
        if (this.sapperSpawns == null) {
            this.sapperSpawns = new int[3];
            int i = 0;
            Object object = Dungeon.level.mobs.iterator();
            while (object.hasNext()) {
                Mob m = (Mob)object.next();
                if (m instanceof GnollSapper) {
                    this.sapperSpawns[i] = ((GnollSapper)m).spawnPos;
                    ++i;
                }
                if (i != 3) continue;
                break;
            }
        }
        if (this.throwingRocksFromPos != null) {
            boolean attacked = false;
            for (int rock : this.throwingRocksFromPos) {
                if (rock == -1 || Dungeon.level.map[rock] != 36) continue;
                attacked = true;
                GnollGeomancer.doRockThrowAttack(this, rock, this.throwingRockToPos);
            }
            this.throwingRocksFromPos = null;
            this.throwingRockToPos = -1;
            this.spend(1.0f);
            return !attacked;
        }
        return super.act();
    }

    @Override
    public boolean isInvulnerable(Class effect) {
        return super.isInvulnerable(effect) || this.buff(RockArmor.class) != null && effect != Pickaxe.class || this.hasSapper();
    }

    @Override
    public boolean add(Buff buff) {
        if (this.state == this.SLEEPING && !(buff instanceof RockArmor) && !(buff instanceof DelayedRockFall)) {
            return false;
        }
        return super.add(buff);
    }

    @Override
    public int damageRoll() {
        return Random.NormalIntRange(3, 6);
    }

    @Override
    public int attackSkill(Char target) {
        return 20;
    }

    @Override
    public int drRoll() {
        return super.drRoll() + Random.NormalIntRange(0, 6);
    }

    @Override
    public boolean reset() {
        return true;
    }

    @Override
    public float spawningWeight() {
        return 0.0f;
    }

    @Override
    public boolean heroShouldInteract() {
        return super.heroShouldInteract() || this.buff(RockArmor.class) != null;
    }

    @Override
    protected boolean getCloser(int target) {
        return false;
    }

    @Override
    protected boolean getFurther(int target) {
        return false;
    }

    @Override
    public boolean interact(Char c) {
        if (c != Dungeon.hero || this.buff(RockArmor.class) == null) {
            return super.interact(c);
        }
        final Pickaxe p = Dungeon.hero.belongings.getItem(Pickaxe.class);
        if (p == null) {
            return true;
        }
        Dungeon.hero.sprite.attack(this.pos, new Callback(){

            @Override
            public void call() {
                boolean wasSleeping;
                int dmg = p.damageRoll(GnollGeomancer.this);
                boolean bl = wasSleeping = GnollGeomancer.this.state == GnollGeomancer.this.SLEEPING;
                if (wasSleeping) {
                    dmg = Math.min(dmg, 15);
                }
                dmg = Math.min(dmg, GnollGeomancer.this.buff(RockArmor.class).shielding());
                GnollGeomancer.this.damage(dmg, p);
                GnollGeomancer.this.sprite.bloodBurstA(Dungeon.hero.sprite.center(), dmg);
                GnollGeomancer.this.sprite.flash();
                ++GnollGeomancer.this.hits;
                if (GnollGeomancer.this.hits == 1) {
                    GLog.w(Messages.get(GnollGeomancer.this, "warning", new Object[0]), new Object[0]);
                }
                if (GnollGeomancer.this.hits == 3) {
                    GLog.n(Messages.get(GnollGeomancer.this, "alert", new Object[0]), new Object[0]);
                    wasSleeping = false;
                    GnollGeomancer.this.spend(1.0f);
                    GnollGeomancer.this.sprite.idle();
                    GnollGeomancer.this.carveRockAndDash();
                    GnollGeomancer.this.state = GnollGeomancer.this.HUNTING;
                    GnollGeomancer.this.enemy = Dungeon.hero;
                    BossHealthBar.assignBoss(GnollGeomancer.this);
                    for (Mob m : Dungeon.level.mobs) {
                        if (m instanceof GnollGuard) {
                            m.aggro(Dungeon.hero);
                            if (((GnollGuard)m).hasSapper()) continue;
                            m.beckon(GnollGeomancer.this.pos);
                            continue;
                        }
                        if (!(m instanceof GnollSapper)) continue;
                        m.aggro(Dungeon.hero);
                    }
                }
                if (wasSleeping) {
                    GnollGeomancer.this.state = GnollGeomancer.this.SLEEPING;
                    GnollGeomancer.this.alerted = false;
                }
                if (GnollGeomancer.this.buff(RockArmor.class) == null) {
                    Splash.around(GnollGeomancer.this.sprite, 0x555555, 30);
                    GnollGeomancer.this.sprite.idle();
                }
                Sample.INSTANCE.play("sounds/mine.mp3", 1.0f, Random.Float(0.85f, 1.15f));
                Invisibility.dispel(Dungeon.hero);
                Dungeon.hero.spendAndNext(p.delayFactor(GnollGeomancer.this));
            }
        });
        return false;
    }

    @Override
    public void damage(int dmg, Object src) {
        int hpBracket = this.HT / 3;
        int curbracket = this.HP / hpBracket;
        if (curbracket == 3) {
            --curbracket;
        }
        this.inFinalBracket = curbracket == 0;
        super.damage(dmg, src);
        this.abilityCooldown = (int)((float)this.abilityCooldown - (float)dmg / 10.0f);
        int newBracket = this.HP / hpBracket;
        if (newBracket == 3) {
            --newBracket;
        }
        if (newBracket != curbracket) {
            if (this.HP <= (curbracket - 1) * hpBracket) {
                this.HP = (curbracket - 1) * hpBracket + 1;
            }
            BossHealthBar.bleed(newBracket <= 0);
            this.carveRockAndDash();
            Buff.affect(this, RockArmor.class).setShield(25);
        }
    }

    @Override
    public boolean isAlive() {
        return super.isAlive() || !this.inFinalBracket;
    }

    public void linkSapper(GnollSapper sapper) {
        this.sapperID = sapper.id();
        if (this.sprite instanceof GnollGeomancerSprite) {
            ((GnollGeomancerSprite)this.sprite).setupArmor();
        }
    }

    public boolean hasSapper() {
        return this.sapperID != -1 && Actor.findById(this.sapperID) instanceof GnollSapper && ((GnollSapper)Actor.findById(this.sapperID)).isAlive();
    }

    public void loseSapper() {
        if (this.sapperID != -1) {
            this.sapperID = -1;
            if (this.sprite instanceof GnollGeomancerSprite) {
                ((GnollGeomancerSprite)this.sprite).loseArmor();
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private void carveRockAndDash() {
        void var5_12;
        int closestSapperPos = -1;
        boolean closestisAlive = false;
        block0: for (int i = 0; i < this.sapperSpawns.length; ++i) {
            if (this.sapperSpawns[i] == -1) continue;
            if (closestSapperPos == -1) {
                closestSapperPos = this.sapperSpawns[i];
                for (Mob mob : Dungeon.level.mobs) {
                    if (!(mob instanceof GnollSapper) || ((GnollSapper)mob).spawnPos != closestSapperPos) continue;
                    closestisAlive = true;
                    continue block0;
                }
                continue;
            }
            boolean sapperAlive = false;
            for (Object m3 : Dungeon.level.mobs) {
                if (!(m3 instanceof GnollSapper) || ((GnollSapper)m3).spawnPos != this.sapperSpawns[i]) continue;
                sapperAlive = true;
                break;
            }
            if ((!sapperAlive || closestisAlive || Dungeon.level.distance(this.pos, this.sapperSpawns[i]) > 16) && !(Dungeon.level.trueDistance(this.pos, this.sapperSpawns[i]) < Dungeon.level.trueDistance(this.pos, closestSapperPos))) continue;
            closestSapperPos = this.sapperSpawns[i];
            closestisAlive = sapperAlive;
        }
        int dashPos = closestSapperPos;
        if (dashPos == -1) {
            return;
        }
        Ballistica path = new Ballistica(this.pos, dashPos, 1);
        if (path.dist > 12) {
            dashPos = path.path.get(12);
        }
        if (Actor.findChar(dashPos) != null || Dungeon.level.traps.get(dashPos) != null) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (Object i : (Object)PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(dashPos + i) != null || Dungeon.level.traps.get(dashPos + i) != null) continue;
                arrayList.add(dashPos + i);
            }
            if (!arrayList.isEmpty()) {
                dashPos = (Integer)Random.element(arrayList);
            }
        }
        boolean bl = false;
        while (var5_12 < this.sapperSpawns.length) {
            if (this.sapperSpawns[var5_12] == closestSapperPos) {
                this.sapperSpawns[var5_12] = -1;
            }
            ++var5_12;
        }
        path = new Ballistica(this.pos, dashPos, 1);
        ArrayList<Integer> arrayList = new ArrayList<Integer>(path.subPath(0, path.dist));
        arrayList.addAll(this.spreadDiamondAOE(arrayList));
        arrayList.addAll(this.spreadDiamondAOE(arrayList));
        arrayList.addAll(this.spreadDiamondAOE(arrayList));
        ArrayList<Integer> exteriorCells = this.spreadDiamondAOE(arrayList);
        for (int i : arrayList) {
            if (Dungeon.level.map[i] == 12) {
                Dungeon.level.drop((Item)new DarkGold(), (int)i).sprite.drop();
                Dungeon.level.map[i] = 20;
            } else if (Dungeon.level.solid[i]) {
                Dungeon.level.map[i] = Random.Int(3) == 0 ? 36 : 20;
            } else if (Dungeon.level.map[i] == 15 || Dungeon.level.map[i] == 30) {
                Dungeon.level.map[i] = 2;
            }
            CellEmitter.get(i - Dungeon.level.width()).start(Speck.factory(8), 0.07f, 10);
        }
        for (int i : exteriorCells) {
            if (Dungeon.level.solid[i] || Dungeon.level.map[i] == 14 || Dungeon.level.adjacent(i, Dungeon.level.entrance()) || Dungeon.level.traps.get(i) != null || Dungeon.level.plants.get(i) != null || Actor.findChar(i) != null) continue;
            Dungeon.level.map[i] = 36;
        }
        if (Dungeon.level.solid[dashPos]) {
            Dungeon.level.map[dashPos] = 20;
        }
        Dungeon.level.buildFlagMaps();
        Dungeon.level.cleanWalls();
        GameScene.updateMap();
        GameScene.updateFog();
        Dungeon.observe();
        PixelScene.shake(3.0f, 0.7f);
        Sample.INSTANCE.play("sounds/rocks.mp3");
        int oldpos = this.pos;
        this.pos = dashPos;
        this.spend(1.0f);
        this.abilityCooldown = 1;
        Actor.add(new Pushing(this, oldpos, this.pos));
        if (closestisAlive) {
            GnollSapper closest = null;
            for (Mob m : Dungeon.level.mobs) {
                if (!(m instanceof GnollSapper) || ((GnollSapper)m).spawnPos != closestSapperPos) continue;
                closest = (GnollSapper)m;
                break;
            }
            if (closest != null) {
                Actor guard = closest.getPartner();
                closest.linkPartner(this);
                if (Dungeon.level.distance(closest.pos, dashPos) > 3) {
                    ArrayList<Integer> candidates = new ArrayList<Integer>();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Dungeon.level.solid[dashPos + i] || Dungeon.level.traps.get(dashPos + i) != null || Dungeon.level.plants.get(dashPos + i) != null || Actor.findChar(dashPos + i) != null) continue;
                        candidates.add(dashPos + i);
                    }
                    if (!candidates.isEmpty()) {
                        int newSapperPos = (Integer)Random.element(candidates);
                        ScrollOfTeleportation.appear(closest, newSapperPos);
                        closest.spawnPos = newSapperPos;
                        candidates.remove((Object)newSapperPos);
                        if (guard instanceof GnollGuard && !candidates.isEmpty()) {
                            ScrollOfTeleportation.appear((GnollGuard)guard, (Integer)Random.element(candidates));
                        }
                    }
                }
            }
        }
    }

    private ArrayList<Integer> spreadDiamondAOE(ArrayList<Integer> currentCells) {
        ArrayList<Integer> spreadCells = new ArrayList<Integer>();
        for (int i : currentCells) {
            for (int j : PathFinder.NEIGHBOURS4) {
                if (!Dungeon.level.insideMap(i + j) || spreadCells.contains(i + j) || currentCells.contains(i + j)) continue;
                spreadCells.add(i + j);
            }
        }
        return spreadCells;
    }

    @Override
    public String description() {
        if (this.state == this.SLEEPING) {
            return Messages.get(this, "desc_sleeping", new Object[0]);
        }
        Object desc = super.description();
        if (this.buff(RockArmor.class) != null) {
            desc = this.hasSapper() ? (String)desc + "\n\n" + Messages.get(this, "desc_armor_sapper", new Object[0]) : (String)desc + "\n\n" + Messages.get(this, "desc_armor", new Object[0]);
        }
        return desc;
    }

    @Override
    public void die(Object cause) {
        super.die(cause);
        Blacksmith.Quest.beatBoss();
        Sample.INSTANCE.playDelayed("sounds/rocks.mp3", 0.1f);
        PixelScene.shake(3.0f, 0.7f);
        GameScene.bossSlain();
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Music.INSTANCE.fadeOut(5.0f, new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.end();
                    }
                });
            }
        });
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (Dungeon.level.map[i] != 36 || !(Dungeon.level.trueDistance(i, this.pos) <= 6.0f)) continue;
            Level.set(i, 20);
            GameScene.updateMap(i);
            Splash.at(i, 0x555555, 15);
        }
    }

    @Override
    public void beckon(int cell) {
        if (this.state != this.SLEEPING) {
            super.beckon(cell);
        }
    }

    public static Ballistica prepRockThrowAttack(Char target, Char source) {
        ArrayList<Integer> candidateRocks = new ArrayList<Integer>();
        for (int i = 0; i < Dungeon.level.length(); ++i) {
            if (!source.fieldOfView[i] || Dungeon.level.map[i] != 36 || new Ballistica((int)i, (int)target.pos, (int)7).collisionPos != target.pos) continue;
            candidateRocks.add(i);
        }
        for (Char char_ : Actor.chars()) {
            if (char_ instanceof GnollGeomancer && ((GnollGeomancer)char_).throwingRocksFromPos != null) {
                for (int i : ((GnollGeomancer)char_).throwingRocksFromPos) {
                    candidateRocks.remove((Object)i);
                }
                continue;
            }
            if (!(char_ instanceof GnollSapper)) continue;
            candidateRocks.remove((Object)((GnollSapper)char_).throwingRockFromPos);
        }
        if (candidateRocks.isEmpty()) {
            return null;
        }
        int throwingFromPos = (Integer)candidateRocks.get(0);
        Iterator iterator = candidateRocks.iterator();
        while (iterator.hasNext()) {
            int i = (Integer)iterator.next();
            if (!(Dungeon.level.trueDistance(i, target.pos) < Dungeon.level.trueDistance(throwingFromPos, target.pos))) continue;
            throwingFromPos = i;
        }
        int n = target.pos;
        return new Ballistica(throwingFromPos, n, 7);
    }

    public static void doRockThrowAttack(final Char source, int from, int to) {
        Level.set(from, 1);
        GameScene.updateMap(from);
        source.sprite.attack(from, new Callback(){

            @Override
            public void call() {
                source.sprite.idle();
            }
        });
        final Ballistica rockPath = new Ballistica(from, to, 6);
        Sample.INSTANCE.play("sounds/miss.mp3");
        ((MissileSprite)source.sprite.parent.recycle(MissileSprite.class)).reset(from, (int)rockPath.collisionPos, (Item)new Boulder(), new Callback(){

            @Override
            public void call() {
                Splash.at(rockPath.collisionPos, 0x555555, 15);
                Sample.INSTANCE.play("sounds/rocks.mp3");
                Char ch = Actor.findChar(rockPath.collisionPos);
                if (ch == Dungeon.hero) {
                    PixelScene.shake(3.0f, 0.7f);
                } else {
                    PixelScene.shake(0.5f, 0.5f);
                }
                if (ch != null && !(ch instanceof GnollGeomancer)) {
                    ch.damage(Random.NormalIntRange(6, 12), new Boulder());
                    if (ch == Dungeon.hero) {
                        Statistics.questScores[2] = Statistics.questScores[2] - 100;
                    }
                    if (ch.isAlive()) {
                        Buff.prolong(ch, Paralysis.class, ch instanceof GnollGuard ? 10.0f : 3.0f);
                    } else if (!ch.isAlive() && ch == Dungeon.hero) {
                        Badges.validateDeathFromEnemyMagic();
                        Dungeon.fail(source.getClass());
                        GLog.n(Messages.get(GnollGeomancer.class, "rock_kill", new Object[0]), new Object[0]);
                    }
                    if (!knockedChars.contains(ch) && rockPath.path.size() > rockPath.dist + 1) {
                        Ballistica trajectory = new Ballistica(ch.pos, rockPath.path.get(rockPath.dist + 1), 6);
                        WandOfBlastWave.throwChar(ch, trajectory, 1, false, false, source);
                        knockedChars.add(ch);
                    }
                } else if (ch == null) {
                    Dungeon.level.pressCell(rockPath.collisionPos);
                }
                if (--rocksInFlight <= 0) {
                    rocksInFlight = 0;
                    source.next();
                    knockedChars.clear();
                }
            }
        });
        ++rocksInFlight;
    }

    public static boolean prepRockFallAttack(Char target, final Char source, int range, boolean avoidBarricades) {
        int safeCell;
        int rockCenter = target.pos;
        while ((safeCell = rockCenter + PathFinder.NEIGHBOURS8[Random.Int(8)]) == source.pos || Dungeon.level.solid[safeCell] && Random.Int(5) != 0 || Dungeon.level.traps.containsKey(safeCell) && Random.Int(5) != 0) {
        }
        ArrayList<Integer> rockCells = new ArrayList<Integer>();
        int start = rockCenter - Dungeon.level.width() * range - range;
        for (int y = 0; y < 1 + 2 * range; ++y) {
            int pos = start + Dungeon.level.width() * y;
            for (int x = 0; x < 1 + 2 * range; ++x) {
                if (!Dungeon.level.insideMap(pos)) {
                    ++pos;
                    continue;
                }
                if (avoidBarricades) {
                    boolean barricade = false;
                    for (int j : PathFinder.NEIGHBOURS9) {
                        if (Dungeon.level.map[pos + j] != 13 && Dungeon.level.map[pos + j] != 7) continue;
                        barricade = true;
                    }
                    if (barricade) {
                        ++pos;
                        continue;
                    }
                }
                if (!(Dungeon.level.solid[pos] || pos == safeCell || Actor.findChar(pos) instanceof GnollGeomancer || source instanceof GnollGeomancer && Actor.findChar(pos) instanceof GnollSapper || Random.Int(1 + Dungeon.level.distance(rockCenter, pos) / 2) != 0)) {
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
        Buff.append(source, GnollRockFall.class, delay).setRockPositions(rockCells);
        source.sprite.attack(target.pos, new Callback(){

            @Override
            public void call() {
                source.sprite.idle();
            }
        });
        return true;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(HITS, this.hits);
        bundle.put(ABILITY_COOLDOWN, this.abilityCooldown);
        bundle.put(LAST_ABILITY_WAS_ROCKFALL, this.lastAbilityWasRockfall);
        if (this.throwingRocksFromPos != null) {
            bundle.put(ROCK_FROM_POS, this.throwingRocksFromPos);
        }
        bundle.put(ROCK_TO_POS, this.throwingRockToPos);
        bundle.put(SAPPER_ID, this.sapperID);
        if (this.sapperSpawns != null) {
            bundle.put(SAPPER_SPAWNS, this.sapperSpawns);
        }
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.hits = bundle.getInt(HITS);
        this.abilityCooldown = bundle.getInt(ABILITY_COOLDOWN);
        this.lastAbilityWasRockfall = bundle.getBoolean(LAST_ABILITY_WAS_ROCKFALL);
        if (bundle.contains(ROCK_FROM_POS)) {
            this.throwingRocksFromPos = bundle.getIntArray(ROCK_FROM_POS);
        }
        this.throwingRockToPos = bundle.getInt(ROCK_TO_POS);
        this.sapperID = bundle.getInt(SAPPER_ID);
        if (bundle.contains(SAPPER_SPAWNS)) {
            this.sapperSpawns = bundle.getIntArray(SAPPER_SPAWNS);
        }
        if (this.hits >= 3) {
            BossHealthBar.assignBoss(this);
        }
    }

    private class Sleeping
    extends Mob.Sleeping {
        private Sleeping() {
            super(GnollGeomancer.this);
        }

        @Override
        protected void awaken(boolean enemyInFOV) {
        }
    }

    private class Hunting
    extends Mob.Hunting {
        private Hunting() {
            super(GnollGeomancer.this);
        }

        @Override
        public boolean act(boolean enemyInFOV, boolean justAlerted) {
            if (!enemyInFOV) {
                GnollGeomancer.this.enemy = null;
                GnollGeomancer.this.enemy = GnollGeomancer.this.chooseEnemy();
                if (GnollGeomancer.this.enemy != null) {
                    return this.act(true, justAlerted);
                }
                GnollGeomancer.this.spend(1.0f);
                return true;
            }
            GnollGeomancer.this.enemySeen = true;
            if ((Dungeon.level.distance(GnollGeomancer.this.pos, GnollGeomancer.this.enemy.pos) > 2 || GnollGeomancer.this.hasSapper()) && GnollGeomancer.this.buff(RockArmor.class) != null && GnollGeomancer.this.enemy.buff(Paralysis.class) == null) {
                GnollGeomancer.this.abilityCooldown = (int)((float)GnollGeomancer.this.abilityCooldown - 1.0f);
            }
            if (GnollGeomancer.this.hasSapper()) {
                ((GnollSapper)Actor.findById(GnollGeomancer.this.sapperID)).aggro(GnollGeomancer.this.enemy);
            }
            if (GnollGeomancer.this.abilityCooldown-- <= 0) {
                Ballistica aim;
                int hpBracket;
                int curbracket;
                boolean targetNextToBarricade = false;
                for (int i : PathFinder.NEIGHBOURS8) {
                    if (Dungeon.level.map[GnollGeomancer.this.enemy.pos + i] != 13 && Dungeon.level.map[GnollGeomancer.this.enemy.pos + i] != 7) continue;
                    targetNextToBarricade = true;
                    break;
                }
                if ((curbracket = GnollGeomancer.this.HP / (hpBracket = GnollGeomancer.this.HT / 3)) == 3) {
                    --curbracket;
                }
                if ((aim = GnollGeomancer.prepRockThrowAttack(GnollGeomancer.this.enemy, GnollGeomancer.this)) != null && (targetNextToBarricade || GnollGeomancer.this.lastAbilityWasRockfall || Random.Int(2) == 0)) {
                    GnollGeomancer.this.lastAbilityWasRockfall = false;
                    GnollGeomancer.this.throwingRocksFromPos = new int[]{-1, -1, -1};
                    GnollGeomancer.this.throwingRockToPos = aim.collisionPos;
                    float delay = GameMath.gate(1.0f, (int)Math.ceil(GnollGeomancer.this.enemy.cooldown()), 3.0f);
                    for (int i = 0; i < 3 - curbracket && aim != null; ++i) {
                        GnollGeomancer.this.throwingRocksFromPos[i] = aim.sourcePos;
                        Ballistica warnPath = new Ballistica(aim.sourcePos, aim.collisionPos, 4);
                        for (int j : warnPath.subPath(0, warnPath.dist)) {
                            GameScene.targetedCell(j, delay);
                        }
                        aim = GnollGeomancer.prepRockThrowAttack(GnollGeomancer.this.enemy, GnollGeomancer.this);
                    }
                    Dungeon.hero.interrupt();
                    GnollGeomancer.this.abilityCooldown = Random.NormalIntRange(3, 5);
                    GnollGeomancer.this.spend(delay);
                    return true;
                }
                if (GnollGeomancer.prepRockFallAttack(GnollGeomancer.this.enemy, GnollGeomancer.this, 6 - 2 * curbracket, true)) {
                    GnollGeomancer.this.lastAbilityWasRockfall = true;
                    Dungeon.hero.interrupt();
                    GnollGeomancer.this.spend(GameMath.gate(1.0f, (int)Math.ceil(GnollGeomancer.this.enemy.cooldown()), 3.0f));
                    GnollGeomancer.this.abilityCooldown = Random.NormalIntRange(3, 5);
                    return true;
                }
            }
            GnollGeomancer.this.spend(1.0f);
            return true;
        }
    }

    public static class RockArmor
    extends ShieldBuff {
    }

    public static class Boulder
    extends Item {
        public Boulder() {
            this.image = ItemSpriteSheet.GEO_BOULDER;
        }
    }

    public static class GnollRockFall
    extends DelayedRockFall {
        @Override
        public void affectChar(Char ch) {
            ch.damage(Random.NormalIntRange(6, 12), this);
            if (ch == Dungeon.hero) {
                Statistics.questScores[2] = Statistics.questScores[2] - 100;
            }
            if (ch.isAlive()) {
                Buff.prolong(ch, Paralysis.class, ch instanceof GnollGuard ? 10.0f : 3.0f);
            } else if (ch == Dungeon.hero) {
                Dungeon.fail(this.target);
                GLog.n(Messages.get(GnollGeomancer.class, "rockfall_kill", new Object[0]), new Object[0]);
            }
        }

        @Override
        public void affectCell(int cell) {
            if (Dungeon.level.map[cell] != 14 && !Dungeon.level.adjacent(cell, Dungeon.level.entrance()) && Random.Int(3) == 0) {
                Level.set(cell, 36);
                GameScene.updateMap(cell);
            }
        }
    }
}

