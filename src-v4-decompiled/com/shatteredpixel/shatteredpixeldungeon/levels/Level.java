/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SmokeScreen;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Web;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.WellWater;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Awareness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Blindness;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Burning;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ChampionEnemy;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LockedFloor;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MagicalSight;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.MindVision;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Ooze;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.PinCushion;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Regeneration;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.RevealedArea;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Shadows;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.PowerOfMany;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.huntress.SpiritHawk;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.DivineSense;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells.Stasis;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGeomancer;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.MobSpawner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Piranha;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogFist;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Sheep;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.TargetedCell;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlowParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SacrificialParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Stylus;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TalismanOfForesight;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.TimekeepersHourglass;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.ScrollOfUpgrade;
import com.shatteredpixel.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfChallenge;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfEnchantment;
import com.shatteredpixel.shatteredpixeldungeon.items.stones.StoneOfIntuition;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.DimensionalSundial;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.EyeOfNewt;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MossyClump;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrapMechanism;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfRegrowth;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.WandOfWarding;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.MiningLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Chasm;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Door;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.HighGrass;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.plants.Swiftthistle;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.InterlevelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Sample;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Reflection;
import com.watabou.utils.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public abstract class Level
implements Bundlable {
    protected int width;
    protected int height;
    protected int length;
    protected static final float TIME_TO_RESPAWN = 50.0f;
    public int version;
    public int[] map;
    public boolean[] visited;
    public boolean[] mapped;
    public boolean[] discoverable;
    public int viewDistance = Dungeon.isChallenged(32) ? 2 : 8;
    public boolean[] heroFOV;
    public boolean[] passable;
    public boolean[] losBlocking;
    public boolean[] flamable;
    public boolean[] secret;
    public boolean[] solid;
    public boolean[] avoid;
    public boolean[] water;
    public boolean[] pit;
    public boolean[] openSpace;
    public Feeling feeling = Feeling.NONE;
    public int entrance;
    public int exit;
    public ArrayList<LevelTransition> transitions;
    public boolean locked = false;
    public HashSet<Mob> mobs;
    public SparseArray<Heap> heaps;
    public HashMap<Class<? extends Blob>, Blob> blobs;
    public SparseArray<Plant> plants;
    public SparseArray<Trap> traps;
    public ArrayList<CustomTilemap> customTiles;
    public ArrayList<CustomTilemap> customTerrain;
    public ArrayList<CustomTilemap> customWalls;
    protected ArrayList<Item> itemsToSpawn = new ArrayList();
    protected Group visuals;
    protected Group wallVisuals;
    public int color1 = 17408;
    public int color2 = 0x88CC44;
    private static final String VERSION = "version";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String MAP = "map";
    private static final String VISITED = "visited";
    private static final String MAPPED = "mapped";
    private static final String TRANSITIONS = "transitions";
    private static final String LOCKED = "locked";
    private static final String HEAPS = "heaps";
    private static final String PLANTS = "plants";
    private static final String TRAPS = "traps";
    private static final String CUSTOM_TILES = "customTiles";
    private static final String CUSTOM_TERRAIN = "customTerrain";
    private static final String CUSTOM_WALLS = "customWalls";
    private static final String MOBS = "mobs";
    private static final String BLOBS = "blobs";
    private static final String FEELING = "feeling";
    private ArrayList<Class<? extends Mob>> mobsToSpawn = new ArrayList();
    private MobSpawner respawner;
    private static boolean[] heroMindFov;
    private static boolean[] modifiableBlocking;

    public void create() {
        TargetedCell.cells.clear();
        Random.pushGenerator(Dungeon.seedCurDepth());
        if (!Dungeon.bossLevel() && Dungeon.branch == 0) {
            this.addItemToSpawn(Generator.random(Generator.Category.FOOD));
            if (Dungeon.posNeeded()) {
                ++Dungeon.LimitedDrops.STRENGTH_POTIONS.count;
                this.addItemToSpawn(new PotionOfStrength());
            }
            if (Dungeon.souNeeded()) {
                ++Dungeon.LimitedDrops.UPGRADE_SCROLLS.count;
                if (!Dungeon.isChallenged(64) || Dungeon.LimitedDrops.UPGRADE_SCROLLS.count % 2 != 0) {
                    this.addItemToSpawn(new ScrollOfUpgrade());
                }
            }
            if (Dungeon.asNeeded()) {
                ++Dungeon.LimitedDrops.ARCANE_STYLI.count;
                this.addItemToSpawn(new Stylus());
            }
            if (Dungeon.enchStoneNeeded()) {
                Dungeon.LimitedDrops.ENCH_STONE.drop();
                this.addItemToSpawn(new StoneOfEnchantment());
            }
            if (Dungeon.intStoneNeeded()) {
                Dungeon.LimitedDrops.INT_STONE.drop();
                this.addItemToSpawn(new StoneOfIntuition());
            }
            if (Dungeon.trinketCataNeeded()) {
                Dungeon.LimitedDrops.TRINKET_CATA.drop();
                this.addItemToSpawn(new TrinketCatalyst());
            }
            if (Dungeon.depth > 1) {
                switch (Random.Int(14)) {
                    case 0: {
                        this.feeling = Feeling.CHASM;
                        break;
                    }
                    case 1: {
                        this.feeling = Feeling.WATER;
                        break;
                    }
                    case 2: {
                        this.feeling = Feeling.GRASS;
                        break;
                    }
                    case 3: {
                        this.feeling = Feeling.DARK;
                        this.viewDistance = Math.round((float)(5 * this.viewDistance) / 8.0f);
                        break;
                    }
                    case 4: {
                        this.feeling = Feeling.LARGE;
                        this.addItemToSpawn(Generator.random(Generator.Category.FOOD));
                        break;
                    }
                    case 5: {
                        this.feeling = Feeling.TRAPS;
                        break;
                    }
                    case 6: {
                        this.feeling = Feeling.SECRETS;
                        break;
                    }
                    default: {
                        float mossyChance = Random.Float();
                        float trapMechChance = Random.Float();
                        this.feeling = mossyChance < MossyClump.overrideNormalLevelChance() ? MossyClump.getNextFeeling() : (trapMechChance < TrapMechanism.overrideNormalLevelChance() ? TrapMechanism.getNextFeeling() : Feeling.NONE);
                    }
                }
            }
        }
        do {
            this.length = 0;
            this.height = 0;
            this.width = 0;
            this.transitions = new ArrayList();
            this.mobs = new HashSet();
            this.heaps = new SparseArray();
            this.blobs = new HashMap();
            this.plants = new SparseArray();
            this.traps = new SparseArray();
            this.customTiles = new ArrayList();
            this.customTerrain = new ArrayList();
            this.customWalls = new ArrayList();
        } while (!this.build());
        this.buildFlagMaps();
        this.cleanWalls();
        this.createMobs();
        this.createItems();
        Random.popGenerator();
    }

    public void setSize(int w, int h) {
        this.width = w;
        this.height = h;
        this.length = w * h;
        this.map = new int[this.length];
        Arrays.fill(this.map, this.feeling == Feeling.CHASM ? 0 : 4);
        this.visited = new boolean[this.length];
        this.mapped = new boolean[this.length];
        this.heroFOV = new boolean[this.length];
        this.passable = new boolean[this.length];
        this.losBlocking = new boolean[this.length];
        this.flamable = new boolean[this.length];
        this.secret = new boolean[this.length];
        this.solid = new boolean[this.length];
        this.avoid = new boolean[this.length];
        this.water = new boolean[this.length];
        this.pit = new boolean[this.length];
        this.openSpace = new boolean[this.length];
        PathFinder.setMapSize(w, h);
    }

    public void reset() {
        for (Mob mob : this.mobs.toArray(new Mob[0])) {
            if (mob.reset()) continue;
            this.mobs.remove(mob);
        }
        this.createMobs();
    }

    public void playLevelMusic() {
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        CustomTilemap vis;
        this.version = bundle.getInt(VERSION);
        if (this.version < 850) {
            throw new RuntimeException("old save");
        }
        this.setSize(bundle.getInt(WIDTH), bundle.getInt(HEIGHT));
        this.mobs = new HashSet();
        this.heaps = new SparseArray();
        this.blobs = new HashMap();
        this.plants = new SparseArray();
        this.traps = new SparseArray();
        this.customTiles = new ArrayList();
        this.customTerrain = new ArrayList();
        this.customWalls = new ArrayList();
        this.map = bundle.getIntArray(MAP);
        this.visited = bundle.getBooleanArray(VISITED);
        this.mapped = bundle.getBooleanArray(MAPPED);
        this.transitions = new ArrayList();
        for (Bundlable b : bundle.getCollection(TRANSITIONS)) {
            this.transitions.add((LevelTransition)b);
        }
        this.locked = bundle.getBoolean(LOCKED);
        Collection<Bundlable> collection = bundle.getCollection(HEAPS);
        for (Bundlable h : collection) {
            Heap heap = (Heap)h;
            if (heap.isEmpty()) continue;
            this.heaps.put(heap.pos, heap);
        }
        collection = bundle.getCollection(PLANTS);
        for (Bundlable p : collection) {
            Plant plant = (Plant)p;
            this.plants.put(plant.pos, plant);
        }
        collection = bundle.getCollection(TRAPS);
        for (Bundlable p : collection) {
            Trap trap = (Trap)p;
            this.traps.put(trap.pos, trap);
        }
        collection = bundle.getCollection(CUSTOM_TILES);
        for (Bundlable p : collection) {
            vis = (CustomTilemap)p;
            this.customTiles.add(vis);
        }
        collection = bundle.getCollection(CUSTOM_TERRAIN);
        for (Bundlable p : collection) {
            vis = (CustomTilemap)p;
            this.customTerrain.add(vis);
        }
        collection = bundle.getCollection(CUSTOM_WALLS);
        for (Bundlable p : collection) {
            vis = (CustomTilemap)p;
            this.customWalls.add(vis);
        }
        collection = bundle.getCollection(MOBS);
        for (Bundlable m : collection) {
            Mob mob = (Mob)m;
            if (mob == null) continue;
            this.mobs.add(mob);
        }
        collection = bundle.getCollection(BLOBS);
        for (Bundlable b : collection) {
            Blob blob = (Blob)b;
            this.blobs.put(blob.getClass(), blob);
        }
        this.feeling = bundle.getEnum(FEELING, Feeling.class);
        if (this.feeling == Feeling.DARK) {
            this.viewDistance = Math.round((float)(5 * this.viewDistance) / 8.0f);
        }
        if (bundle.contains("mobs_to_spawn")) {
            for (Class mob : bundle.getClassArray("mobs_to_spawn")) {
                if (mob == null) continue;
                this.mobsToSpawn.add(mob);
            }
        }
        if (bundle.contains("respawner")) {
            this.respawner = (MobSpawner)bundle.get("respawner");
        }
        TargetedCell.cells.clear();
        if (bundle.contains("targeted_cells")) {
            collection = bundle.getCollection("targeted_cells");
            for (Bundlable c : collection) {
                TargetedCell cell = (TargetedCell)c;
                if (cell == null) continue;
                TargetedCell.cells.put(cell.pos, cell);
            }
        }
        this.buildFlagMaps();
        this.cleanWalls();
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        bundle.put(VERSION, Game.versionCode);
        bundle.put(WIDTH, this.width);
        bundle.put(HEIGHT, this.height);
        bundle.put(MAP, this.map);
        bundle.put(VISITED, this.visited);
        bundle.put(MAPPED, this.mapped);
        bundle.put(TRANSITIONS, this.transitions);
        bundle.put(LOCKED, this.locked);
        bundle.put(HEAPS, this.heaps.valueList());
        bundle.put(PLANTS, this.plants.valueList());
        bundle.put(TRAPS, this.traps.valueList());
        bundle.put(CUSTOM_TILES, this.customTiles);
        bundle.put(CUSTOM_TERRAIN, this.customTerrain);
        bundle.put(CUSTOM_WALLS, this.customWalls);
        bundle.put(MOBS, this.mobs);
        bundle.put(BLOBS, this.blobs.values());
        bundle.put(FEELING, this.feeling);
        bundle.put("mobs_to_spawn", this.mobsToSpawn.toArray(new Class[0]));
        bundle.put("respawner", this.respawner);
        bundle.put("targeted_cells", TargetedCell.cells.valueList());
    }

    public int tunnelTile() {
        return this.feeling == Feeling.CHASM ? 14 : 1;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int length() {
        return this.length;
    }

    public String tilesTex() {
        return null;
    }

    public String waterTex() {
        return null;
    }

    protected abstract boolean build();

    public Mob createMob() {
        if (this.mobsToSpawn == null || this.mobsToSpawn.isEmpty()) {
            this.mobsToSpawn = MobSpawner.getMobRotation(Dungeon.depth);
        }
        Mob m = Reflection.newInstance(this.mobsToSpawn.remove(0));
        ChampionEnemy.rollForChampion(m);
        return m;
    }

    protected abstract void createMobs();

    protected abstract void createItems();

    public int entrance() {
        LevelTransition l = this.getTransition(null);
        if (l != null) {
            return l.cell();
        }
        return 0;
    }

    public int exit() {
        LevelTransition l = this.getTransition(LevelTransition.Type.REGULAR_EXIT);
        if (l != null) {
            return l.cell();
        }
        return 0;
    }

    public LevelTransition getTransition(LevelTransition.Type type) {
        if (this.transitions.isEmpty()) {
            return null;
        }
        for (LevelTransition transition : this.transitions) {
            if (type == null && (transition.type == LevelTransition.Type.REGULAR_ENTRANCE || transition.type == LevelTransition.Type.BRANCH_ENTRANCE || transition.type == LevelTransition.Type.SURFACE)) {
                return transition;
            }
            if (transition.type != type) continue;
            return transition;
        }
        return type != null ? this.getTransition(null) : this.transitions.get(0);
    }

    public LevelTransition getTransition(int cell) {
        for (LevelTransition transition : this.transitions) {
            if (!transition.inside(cell)) continue;
            return transition;
        }
        return null;
    }

    public boolean activateTransition(Hero hero, LevelTransition transition) {
        if (this.locked) {
            return false;
        }
        if (rl.RLMode.headless) {
            if (Boolean.parseBoolean(System.getProperty("rl.debug", "false")))
                System.out.println("[activateTransition] headless destDepth=" + transition.destDepth
                        + " destType=" + transition.destType);
            // headless：直接执行换层核心（对应 InterlevelScene.descend/ascend），绕过场景系统
            Dungeon.depth = transition.destDepth;
            Dungeon.branch = transition.destBranch;
            Level nlevel = Dungeon.newLevel();
            LevelTransition dest = nlevel.getTransition(transition.destType);
            Dungeon.switchLevel(nlevel, dest != null ? dest.cell() : nlevel.entrance());
            return true;
        }
        Level.beforeTransition();
        InterlevelScene.curTransition = transition;
        InterlevelScene.mode = transition.type == LevelTransition.Type.REGULAR_EXIT || transition.type == LevelTransition.Type.BRANCH_EXIT ? InterlevelScene.Mode.DESCEND : InterlevelScene.Mode.ASCEND;
        Game.switchScene(InterlevelScene.class);
        return true;
    }

    public static void beforeTransition() {
        Char ally;
        Awareness awareness;
        ScrollOfChallenge.ChallengeArena arena;
        Talent.WarriorFoodImmunity foodImmune;
        Swiftthistle.TimeBubble timeBubble;
        TimekeepersHourglass.timeFreeze timeFreeze2 = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        if (timeFreeze2 != null) {
            timeFreeze2.disarmPresses();
        }
        if ((timeBubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class)) != null) {
            timeBubble.disarmPresses();
        }
        if ((foodImmune = Dungeon.hero.buff(Talent.WarriorFoodImmunity.class)) != null) {
            foodImmune.detach();
        }
        if ((arena = Dungeon.hero.buff(ScrollOfChallenge.ChallengeArena.class)) != null) {
            arena.detach();
        }
        if ((awareness = Dungeon.hero.buff(Awareness.class)) != null) {
            awareness.detach();
        }
        if (Char.hasProp(ally = Stasis.getStasisAlly(), Char.Property.IMMOVABLE)) {
            Dungeon.hero.buff(Stasis.StasisBuff.class).act();
            GLog.w(Messages.get(Stasis.StasisBuff.class, "left_behind", new Object[0]), new Object[0]);
        }
        Dungeon.hero.spendToWhole();
        for (Actor a : Actor.all()) {
            if (!(a.cooldown() < Dungeon.hero.cooldown())) continue;
            a.spendToWhole();
        }
    }

    public void seal() {
        if (!this.locked) {
            this.locked = true;
            Buff.affect(Dungeon.hero, LockedFloor.class);
        }
    }

    public void unseal() {
        if (this.locked) {
            this.locked = false;
            if (Dungeon.hero.buff(LockedFloor.class) != null) {
                Dungeon.hero.buff(LockedFloor.class).detach();
            }
        }
    }

    public ArrayList<Item> getItemsToPreserveFromSealedResurrect() {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Heap h : this.heaps.valueList()) {
            if (h.type != Heap.Type.HEAP) continue;
            for (Item i : h.items) {
                if (i instanceof Bomb) {
                    ((Bomb)i).fuse = null;
                }
                items.add(i);
            }
        }
        for (Mob m : this.mobs) {
            for (PinCushion b : m.buffs(PinCushion.class)) {
                items.addAll(b.getStuckItems());
            }
        }
        for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)) {
            if (b.activeDepth() != Dungeon.depth) continue;
            items.add(b.cancel());
        }
        return items;
    }

    public Group addVisuals() {
        if (this.visuals == null || this.visuals.parent == null) {
            this.visuals = new Group();
        } else {
            this.visuals.clear();
            this.visuals.camera = null;
        }
        for (int i = 0; i < this.length(); ++i) {
            if (!this.pit[i]) continue;
            this.visuals.add(new WindParticle.Wind(i));
            if (i < this.width() || !this.water[i - this.width()]) continue;
            this.visuals.add(new FlowParticle.Flow(i - this.width()));
        }
        return this.visuals;
    }

    public Group addWallVisuals() {
        if (this.wallVisuals == null || this.wallVisuals.parent == null) {
            this.wallVisuals = new Group();
        } else {
            this.wallVisuals.clear();
            this.wallVisuals.camera = null;
        }
        return this.wallVisuals;
    }

    public int mobLimit() {
        return 0;
    }

    public int mobCount() {
        float count = 0.0f;
        for (Mob mob : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (mob.alignment != Char.Alignment.ENEMY || mob.properties().contains((Object)Char.Property.MINIBOSS)) continue;
            count += mob.spawningWeight();
        }
        return Math.round(count);
    }

    public Mob findMob(int pos) {
        for (Mob mob : this.mobs) {
            if (mob.pos != pos) continue;
            return mob;
        }
        return null;
    }

    public Actor addRespawner() {
        if (this.respawner == null) {
            this.respawner = new MobSpawner();
            Actor.addDelayed(this.respawner, this.respawnCooldown());
        } else {
            Actor.add(this.respawner);
            if (this.respawner.cooldown() > this.respawnCooldown()) {
                this.respawner.resetCooldown();
            }
        }
        return this.respawner;
    }

    public float respawnCooldown() {
        float cooldown = Statistics.amuletObtained ? (Dungeon.depth == 1 ? (float)Dungeon.level.mobCount() * 2.0f : (float)Math.round(GameMath.gate(5.0f, (float)Dungeon.level.mobCount() * 5.0f, 25.0f))) : (Dungeon.level.feeling == Feeling.DARK ? 33.333332f : 50.0f);
        return cooldown / DimensionalSundial.spawnMultiplierAtCurrentTime();
    }

    public boolean spawnMob(int disLimit) {
        PathFinder.buildDistanceMap(Dungeon.hero.pos, BArray.or(this.passable, this.avoid, null));
        Mob mob = this.createMob();
        if (mob.state != mob.PASSIVE) {
            mob.state = mob.WANDERING;
        }
        int tries = 30;
        do {
            mob.pos = this.randomRespawnCell(mob);
        } while ((mob.pos == -1 || PathFinder.distance[mob.pos] < disLimit) && --tries > 0);
        if (Dungeon.hero.isAlive() && mob.pos != -1 && PathFinder.distance[mob.pos] >= disLimit) {
            GameScene.add(mob);
            if (!mob.buffs(ChampionEnemy.class).isEmpty()) {
                GLog.w(Messages.get(ChampionEnemy.class, "warn", new Object[0]), new Object[0]);
            }
            return true;
        }
        return false;
    }

    public int randomRespawnCell(Char ch) {
        int cell;
        int count = 0;
        do {
            if (++count > 30) {
                return -1;
            }
            cell = Random.Int(this.length());
        } while (Dungeon.level == this && this.heroFOV[cell] || !this.passable[cell] || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell] || Actor.findChar(cell) != null);
        return cell;
    }

    public int randomDestination(Char ch) {
        int cell;
        while (!this.passable[cell = Random.Int(this.length())] || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) {
        }
        return cell;
    }

    public void addItemToSpawn(Item item) {
        if (item != null) {
            this.itemsToSpawn.add(item);
        }
    }

    public Item findPrizeItem() {
        return this.findPrizeItem(null);
    }

    public Item findPrizeItem(Class<? extends Item> match) {
        if (this.itemsToSpawn.size() == 0) {
            return null;
        }
        if (match == null) {
            for (Item item : this.itemsToSpawn) {
                if (!(item instanceof TrinketCatalyst)) continue;
                this.itemsToSpawn.remove(item);
                return item;
            }
            Item item = Random.element(this.itemsToSpawn);
            this.itemsToSpawn.remove(item);
            return item;
        }
        for (Item item : this.itemsToSpawn) {
            if (!match.isInstance(item)) continue;
            this.itemsToSpawn.remove(item);
            return item;
        }
        return null;
    }

    public void buildFlagMaps() {
        int i;
        for (int i2 = 0; i2 < this.length(); ++i2) {
            int flags = Terrain.flags[this.map[i2]];
            this.passable[i2] = (flags & 1) != 0;
            this.losBlocking[i2] = (flags & 2) != 0;
            this.flamable[i2] = (flags & 4) != 0;
            this.secret[i2] = (flags & 8) != 0;
            this.solid[i2] = (flags & 0x10) != 0;
            this.avoid[i2] = (flags & 0x20) != 0;
            this.water[i2] = (flags & 0x40) != 0;
            this.pit[i2] = (flags & 0x80) != 0;
        }
        for (Blob b : this.blobs.values()) {
            b.onBuildFlagMaps(this);
        }
        int lastRow = this.length() - this.width();
        for (i = 0; i < this.width(); ++i) {
            this.avoid[i] = false;
            this.passable[i] = false;
            this.solid[i] = true;
            this.losBlocking[i] = true;
            this.avoid[lastRow + i] = false;
            this.passable[lastRow + i] = false;
            this.solid[lastRow + i] = true;
            this.losBlocking[lastRow + i] = true;
        }
        for (i = this.width(); i < lastRow; i += this.width()) {
            this.avoid[i] = false;
            this.passable[i] = false;
            this.solid[i] = true;
            this.losBlocking[i] = true;
            this.avoid[i + this.width() - 1] = false;
            this.passable[i + this.width() - 1] = false;
            this.solid[i + this.width() - 1] = true;
            this.losBlocking[i + this.width() - 1] = true;
        }
        block4: for (i = 0; i < this.length(); ++i) {
            if (this.solid[i]) {
                this.openSpace[i] = false;
                continue;
            }
            for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2) {
                if (this.solid[i + PathFinder.CIRCLE8[j]]) {
                    this.openSpace[i] = false;
                    continue;
                }
                if (this.solid[i + PathFinder.CIRCLE8[(j + 1) % 8]] || this.solid[i + PathFinder.CIRCLE8[(j + 2) % 8]]) continue;
                this.openSpace[i] = true;
                continue block4;
            }
        }
    }

    public void updateOpenSpace(int cell) {
        block0: for (int i : PathFinder.NEIGHBOURS9) {
            if (this.solid[cell + i]) {
                this.openSpace[cell + i] = false;
                continue;
            }
            for (int j = 1; j < PathFinder.CIRCLE8.length; j += 2) {
                if (this.solid[cell + i + PathFinder.CIRCLE8[j]]) {
                    this.openSpace[cell + i] = false;
                    continue;
                }
                if (this.solid[cell + i + PathFinder.CIRCLE8[(j + 1) % 8]] || this.solid[cell + i + PathFinder.CIRCLE8[(j + 2) % 8]]) continue;
                this.openSpace[cell + i] = true;
                continue block0;
            }
        }
    }

    public void destroy(int pos) {
        Blob web;
        int terr = this.map[pos];
        if (terr == 1 || terr == 20 || (Terrain.flags[this.map[pos]] & 4) != 0) {
            Level.set(pos, 9);
        }
        if ((web = this.blobs.get(Web.class)) != null) {
            web.clear(pos);
        }
    }

    public void cleanWalls() {
        if (this.discoverable == null || this.discoverable.length != this.length) {
            this.discoverable = new boolean[this.length()];
        }
        for (int i = 0; i < this.length(); ++i) {
            boolean d = false;
            for (int j = 0; j < PathFinder.NEIGHBOURS9.length; ++j) {
                int n = i + PathFinder.NEIGHBOURS9[j];
                if (n < 0 || n >= this.length() || this.map[n] == 4 || this.map[n] == 12) continue;
                d = true;
                break;
            }
            this.discoverable[i] = d;
        }
    }

    public static void set(int cell, int terrain) {
        Level.set(cell, terrain, Dungeon.level);
    }

    public static void set(int cell, int terrain, Level level) {
        Painter.set(level, cell, terrain);
        if (terrain != 18 && terrain != 17 && terrain != 19) {
            level.traps.remove(cell);
        }
        level.updateCellFlags(cell);
    }

    public void updateCellFlags(int cell) {
        int terrain = this.map[cell];
        int flags = Terrain.flags[terrain];
        this.passable[cell] = (flags & 1) != 0;
        this.losBlocking[cell] = (flags & 2) != 0;
        this.flamable[cell] = (flags & 4) != 0;
        this.secret[cell] = (flags & 8) != 0;
        this.solid[cell] = (flags & 0x10) != 0;
        this.avoid[cell] = (flags & 0x20) != 0;
        this.pit[cell] = (flags & 0x80) != 0;
        boolean bl = this.water[cell] = terrain == 29;
        if (this instanceof SewerLevel && (this.map[cell] == 33 || this.map[cell] == 34)) {
            this.flamable[cell] = true;
        }
        for (Blob b : this.blobs.values()) {
            b.onUpdateCellFlags(this, cell);
        }
        this.updateOpenSpace(cell);
    }

    public Heap drop(Item item, int cell) {
        if (item == null || Challenges.isItemBlocked(item)) {
            Heap heap = new Heap();
            ItemSprite sprite = heap.sprite = new ItemSprite();
            sprite.link(heap);
            return heap;
        }
        Heap heap = (Heap)this.heaps.get(cell);
        if (heap == null) {
            heap = new Heap();
            heap.seen = Dungeon.level == this && this.heroFOV[cell];
            heap.pos = cell;
            heap.drop(item);
            if (this.map[cell] == 0 || Dungeon.level != null && this.pit[cell]) {
                Dungeon.dropToChasm(item);
                GameScene.discard(heap);
            } else {
                this.heaps.put(cell, heap);
                GameScene.add(heap);
            }
        } else {
            if (heap.type == Heap.Type.LOCKED_CHEST || heap.type == Heap.Type.CRYSTAL_CHEST) {
                int n;
                while (!this.passable[n = cell + PathFinder.NEIGHBOURS8[Random.Int(8)]] && !this.avoid[n]) {
                }
                return this.drop(item, n);
            }
            heap.drop(item);
        }
        if (Dungeon.level != null && ShatteredPixelDungeon.scene() instanceof GameScene) {
            this.pressCell(cell);
        }
        return heap;
    }

    public Plant plant(Plant.Seed seed, int pos) {
        Plant plant = (Plant)this.plants.get(pos);
        if (plant != null) {
            plant.wither();
        }
        if (this.map[pos] == 15 || this.map[pos] == 30 || this.map[pos] == 1 || this.map[pos] == 9 || this.map[pos] == 20) {
            Level.set(pos, 2, this);
            GameScene.updateMap(pos);
        }
        if (Dungeon.isChallenged(8)) {
            return null;
        }
        plant = seed.couch(pos, this);
        this.plants.put(pos, plant);
        GameScene.plantSeed(pos);
        for (Char ch : Actor.chars()) {
            if (!(ch instanceof WandOfRegrowth.Lotus) || !((WandOfRegrowth.Lotus)ch).inRange(pos) || Actor.findChar(pos) == null) continue;
            plant.trigger();
            return null;
        }
        return plant;
    }

    public void uproot(int pos) {
        this.plants.remove(pos);
        GameScene.updateMap(pos);
    }

    public Trap setTrap(Trap trap, int pos) {
        Trap existingTrap = (Trap)this.traps.get(pos);
        if (existingTrap != null) {
            this.traps.remove(pos);
        }
        trap.set(pos);
        this.traps.put(pos, trap);
        GameScene.updateMap(pos);
        return trap;
    }

    public void disarmTrap(int pos) {
        Level.set(pos, 19);
        GameScene.updateMap(pos);
    }

    public void discover(int cell) {
        Level.set(cell, Terrain.discover(this.map[cell]));
        Trap trap = (Trap)this.traps.get(cell);
        if (trap != null) {
            trap.reveal();
        }
        GameScene.updateMap(cell);
    }

    public boolean setCellToWater(boolean includeTraps, int cell) {
        Point p = this.cellToPoint(cell);
        for (CustomTilemap cust : this.customTiles) {
            Point custPoint = new Point(p);
            custPoint.x -= cust.tileX;
            custPoint.y -= cust.tileY;
            if (custPoint.x < 0 || custPoint.y < 0 || custPoint.x >= cust.tileW || custPoint.y >= cust.tileH || cust.allowWater(custPoint.x, custPoint.y)) continue;
            return false;
        }
        int terr = this.map[cell];
        if (terr == 1 || terr == 2 || terr == 9 || terr == 14 || terr == 15 || terr == 30 || terr == 20) {
            Level.set(cell, 29);
            GameScene.updateMap(cell);
            return true;
        }
        if (includeTraps && (terr == 17 || terr == 18 || terr == 19)) {
            Level.set(cell, 29);
            Dungeon.level.traps.remove(cell);
            GameScene.updateMap(cell);
            return true;
        }
        return false;
    }

    public int fallCell(boolean fallIntoPit) {
        int result;
        do {
            if ((result = this.randomRespawnCell(null)) != -1) continue;
            return -1;
        } while (this.traps.get(result) != null || this.findMob(result) != null);
        return result;
    }

    public void occupyCell(Char ch) {
        if (!ch.isImmune(Web.class) && Blob.volumeAt(ch.pos, Web.class) > 0) {
            this.blobs.get(Web.class).clear(ch.pos);
            Web.affectChar(ch);
        }
        if (Blob.volumeAt(ch.pos, SacrificialFire.class) > 0 && ch.buff(SacrificialFire.Marked.class) == null) {
            if (Dungeon.level.heroFOV[ch.pos]) {
                CellEmitter.get(ch.pos).burst(SacrificialParticle.FACTORY, 5);
            }
            Buff.prolong(ch, SacrificialFire.Marked.class, 2.0f);
        }
        if (!ch.flying) {
            if (this.map[ch.pos] == 29) {
                if (ch.buff(Burning.class) != null) {
                    ch.buff(Burning.class).act();
                }
                if (ch.buff(Ooze.class) != null) {
                    ch.buff(Ooze.class).act();
                }
            }
            if ((this.map[ch.pos] == 2 || this.map[ch.pos] == 9) && ch == Dungeon.hero && Dungeon.hero.hasTalent(Talent.REJUVENATING_STEPS) && ch.buff(Talent.RejuvenatingStepsCooldown.class) == null) {
                if (!Regeneration.regenOn()) {
                    Level.set(ch.pos, 30);
                } else if (ch.buff(Talent.RejuvenatingStepsFurrow.class) != null && ch.buff(Talent.RejuvenatingStepsFurrow.class).count() >= 200.0f) {
                    Level.set(ch.pos, 30);
                } else {
                    Level.set(ch.pos, 15);
                    Buff.count(ch, Talent.RejuvenatingStepsFurrow.class, 3 - Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
                }
                GameScene.updateMap(ch.pos);
                Buff.affect(ch, Talent.RejuvenatingStepsCooldown.class, 15.0f - 5.0f * (float)Dungeon.hero.pointsInTalent(Talent.REJUVENATING_STEPS));
            }
            if (this.pit[ch.pos]) {
                if (ch == Dungeon.hero) {
                    Chasm.heroFall(ch.pos);
                } else if (ch instanceof Mob) {
                    Chasm.mobFall((Mob)ch);
                }
                return;
            }
            this.pressCell(ch.pos, ch instanceof Hero || ch instanceof Sheep);
        } else if (this.map[ch.pos] == 5) {
            Door.enter(ch.pos);
        }
        if (ch.isAlive() && ch instanceof Piranha && !this.water[ch.pos]) {
            ((Piranha)ch).dieOnLand();
        }
    }

    public void pressCell(int cell) {
        this.pressCell(cell, true);
    }

    private void pressCell(int cell, boolean hard) {
        Plant plant;
        Trap trap = null;
        switch (this.map[cell]) {
            case 17: {
                if (!hard) break;
                trap = (Trap)this.traps.get(cell);
                GLog.i(Messages.get(Level.class, "hidden_trap", trap.name()), new Object[0]);
                break;
            }
            case 18: {
                trap = (Trap)this.traps.get(cell);
                break;
            }
            case 15: 
            case 30: {
                HighGrass.trample(this, cell);
                break;
            }
            case 24: {
                WellWater.affectCell(cell);
                break;
            }
            case 5: {
                Door.enter(cell);
            }
        }
        TimekeepersHourglass.timeFreeze timeFreeze2 = Dungeon.hero.buff(TimekeepersHourglass.timeFreeze.class);
        Swiftthistle.TimeBubble bubble = Dungeon.hero.buff(Swiftthistle.TimeBubble.class);
        if (trap != null) {
            if (bubble != null) {
                Sample.INSTANCE.play("sounds/trap.mp3");
                this.discover(cell);
                bubble.setDelayedPress(cell);
            } else if (timeFreeze2 != null) {
                Sample.INSTANCE.play("sounds/trap.mp3");
                this.discover(cell);
                timeFreeze2.setDelayedPress(cell);
            } else {
                if (Dungeon.hero.pos == cell) {
                    Dungeon.hero.interrupt();
                }
                trap.trigger();
            }
        }
        if ((plant = (Plant)this.plants.get(cell)) != null) {
            if (bubble != null) {
                Sample.INSTANCE.play("sounds/trample.mp3", 1.0f, Random.Float(0.96f, 1.05f));
                bubble.setDelayedPress(cell);
            } else if (timeFreeze2 != null) {
                Sample.INSTANCE.play("sounds/trample.mp3", 1.0f, Random.Float(0.96f, 1.05f));
                timeFreeze2.setDelayedPress(cell);
            } else {
                plant.trigger();
            }
        }
        if (hard && Blob.volumeAt(cell, Web.class) > 0) {
            this.blobs.get(Web.class).clear(cell);
        }
    }

    /*
     * WARNING - void declaration
     */
    public void updateFieldOfView(Char c, boolean[] fieldOfView) {
        int i5;
        int n;
        boolean sighted;
        int cx = c.pos % this.width();
        int cy = c.pos / this.width();
        boolean bl = sighted = c.buff(Blindness.class) == null && c.buff(Shadows.class) == null && c.isAlive();
        if (sighted) {
            boolean[] blocking = null;
            if (modifiableBlocking == null || modifiableBlocking.length != Dungeon.level.losBlocking.length) {
                modifiableBlocking = new boolean[Dungeon.level.losBlocking.length];
            }
            if ((!(Dungeon.level instanceof MiningLevel) || Blacksmith.Quest.Type() != 3) && (c instanceof Hero && ((Hero)c).subClass == HeroSubClass.WARDEN || c instanceof YogFist.SoiledFist || c instanceof GnollGeomancer)) {
                if (blocking == null) {
                    System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
                    blocking = modifiableBlocking;
                }
                for (int i2 = 0; i2 < blocking.length; ++i2) {
                    if (!blocking[i2] || Dungeon.level.map[i2] != 15 && Dungeon.level.map[i2] != 30) continue;
                    blocking[i2] = false;
                }
            }
            if (c.alignment != Char.Alignment.ALLY && !(c instanceof GnollGeomancer) && Dungeon.level.blobs.containsKey(SmokeScreen.class) && Dungeon.level.blobs.get(SmokeScreen.class).volume > 0) {
                int var8_17 = 0;
                if (blocking == null) {
                    System.arraycopy(Dungeon.level.losBlocking, 0, modifiableBlocking, 0, modifiableBlocking.length);
                    blocking = modifiableBlocking;
                }
                Blob s = Dungeon.level.blobs.get(SmokeScreen.class);
                boolean bl2 = false;
                while (var8_17 < blocking.length) {
                    if (!blocking[var8_17] && s.cur[var8_17] > 0) {
                        blocking[var8_17] = true;
                    }
                    ++var8_17;
                }
            }
            if (blocking == null) {
                blocking = Dungeon.level.losBlocking;
            }
            float viewDist = c.viewDistance;
            if (c instanceof Hero) {
                viewDist *= 1.0f + 0.25f * (float)((Hero)c).pointsInTalent(Talent.FARSIGHT);
                viewDist *= EyeOfNewt.visionRangeMultiplier();
            }
            ShadowCaster.castShadow(cx, cy, this.width(), fieldOfView, blocking, Math.round(viewDist));
        } else {
            BArray.setFalse(fieldOfView);
        }
        int sense = 1;
        if (c.isAlive() && c == Dungeon.hero) {
            for (Buff buff : c.buffs(MindVision.class)) {
                sense = Math.max(((MindVision)buff).distance, sense);
            }
            if (c.buff(MagicalSight.class) != null) {
                sense = Math.max(12, sense);
            }
        }
        if (!sighted || sense > 1) {
            int[][] rounding = ShadowCaster.rounding;
            for (int y = Math.max(0, cy - sense); y <= Math.min(this.height() - 1, cy + sense); ++y) {
                int n2;
                if (rounding[sense][Math.abs(cy - y)] < Math.abs(cy - y)) {
                    n2 = cx - rounding[sense][Math.abs(cy - y)];
                } else {
                    n2 = sense;
                    while (rounding[sense][n2] < rounding[sense][Math.abs(cy - y)]) {
                        --n2;
                    }
                    n2 = cx - n2;
                }
                int right = Math.min(this.width() - 1, cx + cx - n2);
                n2 = Math.max(0, n2);
                int pos = n2 + y * this.width();
                System.arraycopy(this.discoverable, pos, fieldOfView, pos, right - n2 + 1);
            }
        }
        if (c instanceof SpiritHawk.HawkAlly && Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) >= 3) {
            int range = 1 + (Dungeon.hero.pointsInTalent(Talent.EAGLE_EYE) - 2);
            for (Mob mob : this.mobs) {
                int p = mob.pos;
                if (fieldOfView[p] || this.distance(c.pos, p) > range) continue;
                int[] y = PathFinder.NEIGHBOURS9;
                int n3 = y.length;
                for (n = 0; n < n3; ++n) {
                    i5 = y[n];
                    fieldOfView[mob.pos + i5] = true;
                }
            }
        }
        if (c.isAlive() && c == Dungeon.hero) {
            int p;
            if (heroMindFov == null || heroMindFov.length != this.length()) {
                heroMindFov = new boolean[this.length];
            } else {
                BArray.setFalse(heroMindFov);
            }
            Dungeon.hero.mindVisionEnemies.clear();
            int mindVisRange = 0;
            if (c.buff(MindVision.class) != null) {
                mindVisRange = Integer.MAX_VALUE;
            } else {
                if (((Hero)c).hasTalent(Talent.HEIGHTENED_SENSES)) {
                    mindVisRange = 1 + ((Hero)c).pointsInTalent(Talent.HEIGHTENED_SENSES);
                }
                if (c.buff(DivineSense.DivineSenseTracker.class) != null) {
                    mindVisRange = ((Hero)c).heroClass == HeroClass.CLERIC ? 4 + 4 * ((Hero)c).pointsInTalent(Talent.DIVINE_SENSE) : 1 + 2 * ((Hero)c).pointsInTalent(Talent.DIVINE_SENSE);
                }
                mindVisRange = Math.max(mindVisRange, EyeOfNewt.mindVisionRange());
            }
            if (mindVisRange >= 1) {
                Char char_ = PowerOfMany.getPoweredAlly();
                if (char_ != null && char_.buff(DivineSense.DivineSenseTracker.class) == null) {
                    Object var8_22 = null;
                }
                for (Mob mob : this.mobs) {
                    Char var8_23 = null;
                    if (mob instanceof Mimic && mob.alignment == Char.Alignment.NEUTRAL && ((Mimic)mob).stealthy() || Char.hasProp(mob, Char.Property.OBJECT) || fieldOfView[p = mob.pos] || this.distance(c.pos, p) > mindVisRange && (var8_23 == null || this.distance(var8_23.pos, p) > mindVisRange)) continue;
                    int[] nArray = PathFinder.NEIGHBOURS9;
                    n = nArray.length;
                    for (i5 = 0; i5 < n; ++i5) {
                        int i = nArray[i5];
                        Level.heroMindFov[mob.pos + i] = true;
                    }
                }
            }
            if (c.buff(Awareness.class) != null) {
                for (Heap heap : this.heaps.valueList()) {
                    int p2 = heap.pos;
                    for (int i : PathFinder.NEIGHBOURS9) {
                        Level.heroMindFov[p2 + i] = true;
                    }
                }
            }
            for (TalismanOfForesight.CharAwareness a : c.buffs(TalismanOfForesight.CharAwareness.class)) {
                Char ch = (Char)Actor.findById(a.charID);
                if (ch == null || !ch.isAlive() || Char.hasProp(ch, Char.Property.OBJECT)) continue;
                p = ch.pos;
                for (int i4 : PathFinder.NEIGHBOURS9) {
                    Level.heroMindFov[p + i4] = true;
                }
            }
            for (TalismanOfForesight.HeapAwareness h : c.buffs(TalismanOfForesight.HeapAwareness.class)) {
                if (Dungeon.depth != h.depth || Dungeon.branch != h.branch) continue;
                for (int i6 : PathFinder.NEIGHBOURS9) {
                    Level.heroMindFov[h.pos + i6] = true;
                }
            }
            for (Mob m : this.mobs) {
                if (!(m instanceof WandOfWarding.Ward) && !(m instanceof WandOfRegrowth.Lotus) && !(m instanceof SpiritHawk.HawkAlly) && m.buff(PowerOfMany.PowerBuff.class) == null) continue;
                if (m.fieldOfView == null || m.fieldOfView.length != this.length()) {
                    m.fieldOfView = new boolean[this.length()];
                    Dungeon.level.updateFieldOfView(m, m.fieldOfView);
                }
                BArray.or(heroMindFov, m.fieldOfView, heroMindFov);
            }
            for (RevealedArea a : c.buffs(RevealedArea.class)) {
                if (Dungeon.depth != a.depth || Dungeon.branch != a.branch) continue;
                for (int i : PathFinder.NEIGHBOURS9) {
                    Level.heroMindFov[a.pos + i] = true;
                }
            }
            for (Mob mob : this.mobs) {
                if (!heroMindFov[mob.pos] || fieldOfView[mob.pos]) continue;
                Dungeon.hero.mindVisionEnemies.add(mob);
            }
            BArray.or(heroMindFov, fieldOfView, fieldOfView);
        }
        if (c == Dungeon.hero) {
            for (Heap heap : this.heaps.valueList()) {
                if (heap.seen || !fieldOfView[heap.pos]) continue;
                heap.seen = true;
            }
        }
    }

    public float levelExplorePercent(int depth) {
        return 0.0f;
    }

    public int distance(int a, int b) {
        int ax = a % this.width();
        int ay = a / this.width();
        int bx = b % this.width();
        int by = b / this.width();
        return Math.max(Math.abs(ax - bx), Math.abs(ay - by));
    }

    public boolean adjacent(int a, int b) {
        return this.distance(a, b) == 1;
    }

    public float trueDistance(int a, int b) {
        int ax = a % this.width();
        int ay = a / this.width();
        int bx = b % this.width();
        int by = b / this.width();
        return (float)Math.sqrt(Math.pow(Math.abs(ax - bx), 2.0) + Math.pow(Math.abs(ay - by), 2.0));
    }

    public boolean invalidHeroPos(int tile) {
        int flags = Terrain.flags[this.map[tile]];
        return (flags & 1) != 0 && (flags & 0x20) != 0;
    }

    public boolean insideMap(int tile) {
        return tile >= this.width && tile < this.length - this.width && tile % this.width != 0 && tile % this.width != this.width - 1;
    }

    public Point cellToPoint(int cell) {
        return new Point(cell % this.width(), cell / this.width());
    }

    public int pointToCell(Point p) {
        return p.x + p.y * this.width();
    }

    public String tileName(int tile) {
        switch (tile) {
            case 0: {
                return Messages.get(Level.class, "chasm_name", new Object[0]);
            }
            case 1: 
            case 14: 
            case 17: 
            case 20: 
            case 32: {
                return Messages.get(Level.class, "floor_name", new Object[0]);
            }
            case 2: {
                return Messages.get(Level.class, "grass_name", new Object[0]);
            }
            case 29: {
                return Messages.get(Level.class, "water_name", new Object[0]);
            }
            case 4: 
            case 12: 
            case 16: {
                return Messages.get(Level.class, "wall_name", new Object[0]);
            }
            case 5: {
                return Messages.get(Level.class, "closed_door_name", new Object[0]);
            }
            case 6: {
                return Messages.get(Level.class, "open_door_name", new Object[0]);
            }
            case 7: 
            case 37: {
                return Messages.get(Level.class, "entrace_name", new Object[0]);
            }
            case 8: {
                return Messages.get(Level.class, "exit_name", new Object[0]);
            }
            case 9: {
                return Messages.get(Level.class, "embers_name", new Object[0]);
            }
            case 30: {
                return Messages.get(Level.class, "furrowed_grass_name", new Object[0]);
            }
            case 10: 
            case 38: {
                return Messages.get(Level.class, "locked_door_name", new Object[0]);
            }
            case 31: {
                return Messages.get(Level.class, "crystal_door_name", new Object[0]);
            }
            case 11: {
                return Messages.get(Level.class, "pedestal_name", new Object[0]);
            }
            case 13: {
                return Messages.get(Level.class, "barricade_name", new Object[0]);
            }
            case 15: {
                return Messages.get(Level.class, "high_grass_name", new Object[0]);
            }
            case 21: {
                return Messages.get(Level.class, "locked_exit_name", new Object[0]);
            }
            case 22: {
                return Messages.get(Level.class, "unlocked_exit_name", new Object[0]);
            }
            case 24: {
                return Messages.get(Level.class, "well_name", new Object[0]);
            }
            case 3: {
                return Messages.get(Level.class, "empty_well_name", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(Level.class, "statue_name", new Object[0]);
            }
            case 19: {
                return Messages.get(Level.class, "inactive_trap_name", new Object[0]);
            }
            case 27: {
                return Messages.get(Level.class, "bookshelf_name", new Object[0]);
            }
            case 28: {
                return Messages.get(Level.class, "alchemy_name", new Object[0]);
            }
        }
        return Messages.get(Level.class, "default_name", new Object[0]);
    }

    public String tileDesc(int tile) {
        switch (tile) {
            case 0: {
                return Messages.get(Level.class, "chasm_desc", new Object[0]);
            }
            case 29: {
                return Messages.get(Level.class, "water_desc", new Object[0]);
            }
            case 7: 
            case 37: {
                return Messages.get(Level.class, "entrance_desc", new Object[0]);
            }
            case 8: 
            case 22: {
                return Messages.get(Level.class, "exit_desc", new Object[0]);
            }
            case 9: {
                return Messages.get(Level.class, "embers_desc", new Object[0]);
            }
            case 15: 
            case 30: {
                return Messages.get(Level.class, "high_grass_desc", new Object[0]);
            }
            case 10: 
            case 38: {
                return Messages.get(Level.class, "locked_door_desc", new Object[0]);
            }
            case 31: {
                return Messages.get(Level.class, "crystal_door_desc", new Object[0]);
            }
            case 21: {
                return Messages.get(Level.class, "locked_exit_desc", new Object[0]);
            }
            case 13: {
                return Messages.get(Level.class, "barricade_desc", new Object[0]);
            }
            case 19: {
                return Messages.get(Level.class, "inactive_trap_desc", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(Level.class, "statue_desc", new Object[0]);
            }
            case 28: {
                return Messages.get(Level.class, "alchemy_desc", new Object[0]);
            }
            case 3: {
                return Messages.get(Level.class, "empty_well_desc", new Object[0]);
            }
        }
        return "";
    }

    public static enum Feeling {
        NONE,
        CHASM,
        WATER,
        GRASS,
        DARK,
        LARGE,
        TRAPS,
        SECRETS;


        public String title() {
            return Messages.get((Object)this, this.name() + "_title", new Object[0]);
        }

        public String desc() {
            return Messages.get((Object)this, this.name() + "_desc", new Object[0]);
        }
    }
}

