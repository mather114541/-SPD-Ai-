/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.SacrificialFire;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.EbonyMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GoldenMimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mimic;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Statue;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.Artifact;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.DriedRose;
import com.shatteredpixel.shatteredpixeldungeon.items.food.SupplyRation;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.DocumentPage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.GuidePage;
import com.shatteredpixel.shatteredpixeldungeon.items.journal.RegionLorePage;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.CrystalKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.GoldenKey;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.Key;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.CrackedSpyglass;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.MimicTooth;
import com.shatteredpixel.shatteredpixeldungeon.items.trinkets.TrinketCatalyst;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.journal.Notes;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.LoopBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.MagicalFireRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.PitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SacrificeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.ShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.StatueRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.entrance.EntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ExplosiveTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.mechanics.ShadowCaster;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public abstract class RegularLevel
extends Level {
    protected ArrayList<Room> rooms;
    protected Builder builder;
    protected Room roomEntrance;
    protected Room roomExit;
    private static HashMap<Document, Dungeon.LimitedDrops> limitedDocs = new HashMap();

    @Override
    protected boolean build() {
        this.builder = this.builder();
        ArrayList<Room> initRooms = this.initRooms();
        Random.shuffle(initRooms);
        do {
            for (Room r : initRooms) {
                r.neigbours.clear();
                r.connected.clear();
            }
            this.rooms = this.builder.build((ArrayList)initRooms.clone());
        } while (this.rooms == null);
        return this.painter().paint(this, this.rooms);
    }

    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<Room>();
        this.roomEntrance = EntranceRoom.createEntrance();
        initRooms.add(this.roomEntrance);
        this.roomExit = ExitRoom.createExit();
        initRooms.add(this.roomExit);
        int standards = this.standardRooms(this.feeling == Level.Feeling.LARGE);
        if (this.feeling == Level.Feeling.LARGE) {
            standards = (int)Math.ceil((float)standards * 1.5f);
        }
        for (int i = 0; i < standards; ++i) {
            StandardRoom s;
            while (!(s = StandardRoom.createRoom()).setSizeCat(standards - i)) {
            }
            i += s.sizeFactor() - 1;
            initRooms.add(s);
        }
        if (Dungeon.shopOnLevel()) {
            initRooms.add(new ShopRoom());
        }
        int specials = this.specialRooms(this.feeling == Level.Feeling.LARGE);
        if (this.feeling == Level.Feeling.LARGE) {
            ++specials;
        }
        SpecialRoom.initForFloor();
        for (int i = 0; i < specials; ++i) {
            SpecialRoom s = SpecialRoom.createRoom();
            if (s instanceof PitRoom) {
                ++specials;
            }
            initRooms.add(s);
        }
        int secrets = SecretRoom.secretsForFloor(Dungeon.depth);
        if (this.feeling == Level.Feeling.SECRETS) {
            ++secrets;
        }
        for (int i = 0; i < secrets; ++i) {
            initRooms.add(SecretRoom.createRoom());
        }
        return initRooms;
    }

    protected int standardRooms(boolean forceMax) {
        return 0;
    }

    protected int specialRooms(boolean forceMax) {
        return 0;
    }

    protected Builder builder() {
        if (Random.Int(2) == 0) {
            return new LoopBuilder().setLoopShape(2, Random.Float(0.0f, 0.65f), Random.Float(0.0f, 0.5f));
        }
        return new FigureEightBuilder().setLoopShape(2, Random.Float(0.3f, 0.8f), 0.0f);
    }

    protected abstract Painter painter();

    protected int nTraps() {
        return Random.NormalIntRange(2, 3 + Dungeon.depth / 5);
    }

    protected Class<?>[] trapClasses() {
        return new Class[]{WornDartTrap.class};
    }

    protected float[] trapChances() {
        return new float[]{1.0f};
    }

    @Override
    public int mobLimit() {
        if (Dungeon.depth <= 1) {
            if (!Statistics.amuletObtained) {
                return 0;
            }
            return 10;
        }
        int mobs = 3 + Dungeon.depth % 5 + Random.Int(3);
        if (this.feeling == Level.Feeling.LARGE) {
            mobs = (int)Math.ceil((float)mobs * 1.33f);
        }
        return mobs;
    }

    @Override
    protected void createMobs() {
        int mobsToSpawn = Dungeon.depth == 1 ? 8 : this.mobLimit();
        ArrayList<Room> stdRooms = new ArrayList<Room>();
        for (Room room : this.rooms) {
            if (!(room instanceof StandardRoom)) continue;
            for (int i = 0; i < ((StandardRoom)room).mobSpawnWeight(); ++i) {
                stdRooms.add(room);
            }
        }
        Random.shuffle(stdRooms);
        Iterator stdRoomIter = stdRooms.iterator();
        boolean[] entranceFOV = new boolean[this.length()];
        Point c = this.cellToPoint(this.entrance());
        ShadowCaster.castShadow(c.x, c.y, this.width(), entranceFOV, this.losBlocking, 8);
        boolean[] entranceWalkable = BArray.not(this.solid, null);
        for (int y = this.roomEntrance.top + 1; y < this.roomEntrance.bottom; ++y) {
            for (int x = this.roomEntrance.left + 1; x < this.roomEntrance.right; ++x) {
                int cell = x + y * this.width();
                if (!this.passable[cell]) continue;
                entranceWalkable[cell] = true;
            }
        }
        PathFinder.buildDistanceMap(this.entrance(), entranceWalkable, 8);
        Mob mob = null;
        while (mobsToSpawn > 0) {
            if (mob == null) {
                mob = this.createMob();
            }
            if (!stdRoomIter.hasNext()) {
                stdRoomIter = stdRooms.iterator();
            }
            Room roomToSpawn = (Room)stdRoomIter.next();
            int tries = 30;
            do {
                mob.pos = this.pointToCell(roomToSpawn.random());
            } while (--tries >= 0 && (this.findMob(mob.pos) != null || entranceFOV[mob.pos] || PathFinder.distance[mob.pos] != Integer.MAX_VALUE || !this.passable[mob.pos] || this.solid[mob.pos] || !roomToSpawn.canPlaceCharacter(this.cellToPoint(mob.pos), this) || mob.pos == this.exit() || this.traps.get(mob.pos) != null || this.plants.get(mob.pos) != null || !this.openSpace[mob.pos] && mob.properties().contains((Object)Char.Property.LARGE)));
            if (tries < 0) continue;
            this.mobs.add(mob);
            mob = null;
            if (Dungeon.depth <= 1 || --mobsToSpawn <= 0 || Random.Int(4) != 0) continue;
            mob = this.createMob();
            tries = 30;
            do {
                mob.pos = this.pointToCell(roomToSpawn.random());
            } while (--tries >= 0 && (this.findMob(mob.pos) != null || entranceFOV[mob.pos] || PathFinder.distance[mob.pos] != Integer.MAX_VALUE || !this.passable[mob.pos] || this.solid[mob.pos] || !roomToSpawn.canPlaceCharacter(this.cellToPoint(mob.pos), this) || mob.pos == this.exit() || this.traps.get(mob.pos) != null || this.plants.get(mob.pos) != null || !this.openSpace[mob.pos] && mob.properties().contains((Object)Char.Property.LARGE)));
            if (tries < 0) continue;
            --mobsToSpawn;
            this.mobs.add(mob);
            mob = null;
        }
        for (Mob m : this.mobs) {
            if (this.map[m.pos] != 15 && this.map[m.pos] != 30) continue;
            this.map[m.pos] = 2;
            this.losBlocking[m.pos] = false;
        }
    }

    @Override
    public int randomRespawnCell(Char ch) {
        Room room;
        int count = 0;
        int cell = -1;
        do {
            if (++count <= 30) continue;
            return -1;
        } while ((room = this.randomRoom(StandardRoom.class)) == null || room == this.roomEntrance || this.heroFOV[cell = this.pointToCell(room.random(1))] || Actor.findChar(cell) != null || !this.passable[cell] || this.solid[cell] || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell] || !room.canPlaceCharacter(this.cellToPoint(cell), this) || cell == this.exit());
        return cell;
    }

    @Override
    public int randomDestination(Char ch) {
        ArrayList<Point> points;
        Room room;
        int count = 0;
        int cell = -1;
        do {
            if (++count <= 30) continue;
            return -1;
        } while ((room = Random.element(this.rooms)) == null || (points = room.charPlaceablePoints(this)).isEmpty() || !this.passable[cell = this.pointToCell(Random.element(points))] || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]);
        return cell;
    }

    @Override
    protected void createItems() {
        int cell;
        int nItems = 3 + Random.chances(new float[]{6.0f, 3.0f, 1.0f});
        if (this.feeling == Level.Feeling.LARGE) {
            nItems += 2;
        }
        block12: for (int i = 0; i < nItems; ++i) {
            Item toDrop = Generator.random();
            if (toDrop == null) continue;
            cell = this.randomDropCell();
            if (this.map[cell] == 15 || this.map[cell] == 30) {
                this.map[cell] = 2;
                this.losBlocking[cell] = false;
            }
            Heap.Type type = null;
            switch (Random.Int(20)) {
                case 0: {
                    type = Heap.Type.SKELETON;
                    break;
                }
                case 1: 
                case 2: 
                case 3: 
                case 4: {
                    if (Random.Float() < (MimicTooth.mimicChanceMultiplier() - 1.0f) / 4.0f && this.findMob(cell) == null) {
                        this.mobs.add(Mimic.spawnAt(cell, toDrop));
                        continue block12;
                    }
                    type = Heap.Type.CHEST;
                    break;
                }
                case 5: {
                    if (Dungeon.depth > 1 && this.findMob(cell) == null) {
                        this.mobs.add(Mimic.spawnAt(cell, toDrop));
                        continue block12;
                    }
                    type = Heap.Type.CHEST;
                    break;
                }
                default: {
                    type = Heap.Type.HEAP;
                }
            }
            if (toDrop instanceof Artifact && Random.Int(2) == 0 || toDrop.isUpgradable() && Random.Int(4 - toDrop.level()) == 0) {
                float mimicChance = 0.1f * MimicTooth.mimicChanceMultiplier();
                if (Dungeon.depth > 1 && Random.Float() < mimicChance && this.findMob(cell) == null) {
                    this.mobs.add(Mimic.spawnAt(cell, GoldenMimic.class, toDrop));
                    continue;
                }
                Heap dropped = this.drop(toDrop, cell);
                if (this.heaps.get(cell) != dropped) continue;
                dropped.type = Heap.Type.LOCKED_CHEST;
                this.addItemToSpawn(new GoldenKey(Dungeon.depth));
                continue;
            }
            Heap dropped = this.drop(toDrop, cell);
            dropped.type = type;
            if (type != Heap.Type.SKELETON) continue;
            dropped.setHauntedIfCursed();
        }
        for (Item item : this.itemsToSpawn) {
            cell = this.randomDropCell();
            if (item instanceof TrinketCatalyst) {
                this.drop((Item)item, (int)cell).type = Heap.Type.LOCKED_CHEST;
                int keyCell = this.randomDropCell();
                this.drop((Item)new GoldenKey((int)Dungeon.depth), (int)keyCell).type = Heap.Type.HEAP;
                if (this.map[keyCell] == 15 || this.map[keyCell] == 30) {
                    this.map[keyCell] = 2;
                    this.losBlocking[keyCell] = false;
                }
            } else {
                this.drop((Item)item, (int)cell).type = Heap.Type.HEAP;
            }
            if (this.map[cell] != 15 && this.map[cell] != 30) continue;
            this.map[cell] = 2;
            this.losBlocking[cell] = false;
        }
        Random.pushGenerator(Random.Long());
        if (Dungeon.isChallenged(32)) {
            int cell2 = this.randomDropCell();
            if (this.map[cell2] == 15 || this.map[cell2] == 30) {
                this.map[cell2] = 2;
                this.losBlocking[cell2] = false;
            }
            this.drop(new Torch(), cell2);
            if (this.feeling == Level.Feeling.LARGE) {
                cell2 = this.randomDropCell();
                if (this.map[cell2] == 15 || this.map[cell2] == 30) {
                    this.map[cell2] = 2;
                    this.losBlocking[cell2] = false;
                }
                this.drop(new Torch(), cell2);
            }
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int cell3 = this.randomDropCell();
            if (this.map[cell3] == 15 || this.map[cell3] == 30) {
                this.map[cell3] = 2;
                this.losBlocking[cell3] = false;
            }
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)cell3).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        DriedRose rose = Dungeon.hero.belongings.getItem(DriedRose.class);
        if (rose != null && rose.isIdentified() && !rose.cursed && Ghost.Quest.completed()) {
            int petalsNeeded = (int)Math.ceil((float)(Dungeon.depth / 2 - rose.droppedPetals) / 3.0f);
            for (int i = 1; i <= petalsNeeded; ++i) {
                if (rose.droppedPetals >= 11) continue;
                DriedRose.Petal item = new DriedRose.Petal();
                int cell4 = this.randomDropCell();
                this.drop((Item)item, (int)cell4).type = Heap.Type.HEAP;
                if (this.map[cell4] == 15 || this.map[cell4] == 30) {
                    this.map[cell4] = 2;
                    this.losBlocking[cell4] = false;
                }
                ++rose.droppedPetals;
            }
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        if (Dungeon.hero.hasTalent(Talent.CACHED_RATIONS)) {
            Talent.CachedRationsDropped dropped = Buff.affect(Dungeon.hero, Talent.CachedRationsDropped.class);
            int targetFloor = (int)(2.0f + dropped.count());
            if (dropped.count() > 4.0f) {
                ++targetFloor;
            }
            if (Dungeon.depth >= targetFloor && dropped.count() < (float)(2 + 2 * Dungeon.hero.pointsInTalent(Talent.CACHED_RATIONS))) {
                int cell5;
                boolean valid;
                int tries = 100;
                do {
                    boolean bl = valid = (cell5 = this.randomDropCell(SpecialRoom.class)) != -1 && !(this.room(cell5) instanceof SecretRoom) && !(this.room(cell5) instanceof ShopRoom) && this.map[cell5] != 14 && this.map[cell5] != 29 && this.map[cell5] != 11;
                } while (tries-- > 0 && !valid);
                if (valid) {
                    if (this.map[cell5] == 15 || this.map[cell5] == 30) {
                        this.map[cell5] = 2;
                        this.losBlocking[cell5] = false;
                    }
                    this.drop((Item)new SupplyRation(), (int)cell5).type = Heap.Type.CHEST;
                    dropped.countUp(2.0f);
                }
            }
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        Collection<String> allPages = Document.ADVENTURERS_GUIDE.pageNames();
        ArrayList<String> missingPages = new ArrayList<String>();
        for (String page : allPages) {
            if (Document.ADVENTURERS_GUIDE.isPageFound(page)) continue;
            missingPages.add(page);
        }
        missingPages.remove("Searching");
        float dropChance = 0.25f * (float)(Dungeon.depth - 1);
        if (!missingPages.isEmpty() && Random.Float() < dropChance) {
            GuidePage p = new GuidePage();
            p.page((String)missingPages.get(0));
            int cell6 = this.randomDropCell();
            if (this.map[cell6] == 15 || this.map[cell6] == 30) {
                this.map[cell6] = 2;
                this.losBlocking[cell6] = false;
            }
            this.drop(p, cell6);
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        if (Document.ADVENTURERS_GUIDE.allPagesFound()) {
            Dungeon.LimitedDrops limit;
            Document regionDoc;
            int region = 1 + (Dungeon.depth - 1) / 5;
            switch (region) {
                default: {
                    regionDoc = null;
                    break;
                }
                case 1: {
                    regionDoc = Document.SEWERS_GUARD;
                    break;
                }
                case 2: {
                    regionDoc = Document.PRISON_WARDEN;
                    break;
                }
                case 3: {
                    regionDoc = Document.CAVES_EXPLORER;
                    break;
                }
                case 4: {
                    regionDoc = Document.CITY_WARLOCK;
                    break;
                }
                case 5: {
                    regionDoc = Document.HALLS_KING;
                }
            }
            if (!(regionDoc == null || regionDoc.allPagesFound() || (limit = limitedDocs.get((Object)regionDoc)) != null && limit.dropped())) {
                float totalPages = 0.0f;
                float pagesFound = 0.0f;
                String pageToDrop = null;
                for (String page : regionDoc.pageNames()) {
                    totalPages += 1.0f;
                    if (!regionDoc.isPageFound(page)) {
                        if (pageToDrop != null) continue;
                        pageToDrop = page;
                        continue;
                    }
                    pagesFound += 1.0f;
                }
                float percentComplete = pagesFound / totalPages;
                int targetFloor = 5 * (region - 1) + 1;
                if (Dungeon.depth >= (targetFloor += Math.round(3.0f * percentComplete))) {
                    DocumentPage page = RegionLorePage.pageForDoc(regionDoc);
                    page.page(pageToDrop);
                    int cell7 = this.randomDropCell();
                    if (this.map[cell7] == 15 || this.map[cell7] == 30) {
                        this.map[cell7] = 2;
                        this.losBlocking[cell7] = false;
                    }
                    this.drop(page, cell7);
                    if (limit != null) {
                        limit.drop();
                    }
                }
            }
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        if (Random.Float() < MimicTooth.ebonyMimicChance()) {
            ArrayList<Integer> candidateCells = new ArrayList<Integer>();
            if (Random.Int(2) == 0) {
                for (Heap h : this.heaps.valueList()) {
                    if (h.type != Heap.Type.HEAP || this.room(h.pos) instanceof SpecialRoom || this.findMob(h.pos) != null) continue;
                    candidateCells.add(h.pos);
                }
            }
            if (candidateCells.isEmpty()) {
                if (Random.Int(5) == 0 && this.findMob(this.exit()) == null) {
                    candidateCells.add(this.exit());
                } else {
                    for (int i = 0; i < this.length(); ++i) {
                        if (this.map[i] != 5 || this.findMob(i) != null) continue;
                        candidateCells.add(i);
                    }
                }
            }
            int pos = (Integer)Random.element(candidateCells);
            this.mobs.add(Mimic.spawnAt(pos, EbonyMimic.class, false, new Item[0]));
        }
        Random.popGenerator();
        Random.pushGenerator(Random.Long());
        int items = (int)(Random.Float() + CrackedSpyglass.extraLootChance());
        for (int i = 0; i < items; ++i) {
            int cell8 = this.randomDropCell();
            if (this.map[cell8] == 15 || this.map[cell8] == 30) {
                this.map[cell8] = 2;
                this.losBlocking[cell8] = false;
            }
            this.drop((Item)Generator.randomUsingDefaults(), (int)cell8).hidden = true;
        }
        Random.popGenerator();
    }

    public ArrayList<Room> rooms() {
        return new ArrayList<Room>(this.rooms);
    }

    protected Room randomRoom(Class<? extends Room> type) {
        Random.shuffle(this.rooms);
        return this.room(type);
    }

    public Room room(Class<? extends Room> type) {
        for (Room r : this.rooms) {
            if (!type.isInstance(r)) continue;
            return r;
        }
        return null;
    }

    public Room room(int pos) {
        for (Room room : this.rooms) {
            if (!room.inside(this.cellToPoint(pos))) continue;
            return room;
        }
        return null;
    }

    protected int randomDropCell() {
        return this.randomDropCell(StandardRoom.class);
    }

    protected int randomDropCell(Class<? extends Room> roomType) {
        int tries = 100;
        while (tries-- > 0) {
            Trap t;
            int pos;
            Room room = this.randomRoom(roomType);
            if (room == null) {
                return -1;
            }
            if (room == this.roomEntrance || !this.passable[pos = this.pointToCell(room.random())] || this.solid[pos] || pos == this.exit() || this.heaps.get(pos) != null || !room.canPlaceItem(this.cellToPoint(pos), this) || this.findMob(pos) != null || (t = (Trap)this.traps.get(pos)) != null && (t instanceof BurningTrap || t instanceof BlazingTrap || t instanceof ChillingTrap || t instanceof FrostTrap || t instanceof ExplosiveTrap || t instanceof DisintegrationTrap || t instanceof PitfallTrap)) continue;
            return pos;
        }
        return -1;
    }

    @Override
    public int fallCell(boolean fallIntoPit) {
        if (fallIntoPit) {
            for (Room room : this.rooms) {
                if (!(room instanceof PitRoom)) continue;
                ArrayList<Integer> candidates = new ArrayList<Integer>();
                for (Point p : room.getPoints()) {
                    int cell = this.pointToCell(p);
                    if (!this.passable[cell] || this.findMob(cell) != null) continue;
                    candidates.add(cell);
                }
                if (candidates.isEmpty()) continue;
                return (Integer)Random.element(candidates);
            }
        }
        return super.fallCell(fallIntoPit);
    }

    @Override
    public float levelExplorePercent(int depth) {
        HashSet<Room> missedRooms = new HashSet<Room>();
        block5: for (Heap h : this.heaps.valueList()) {
            if (h.autoExplored) continue;
            if (!h.seen || h.type != Heap.Type.HEAP && h.type != Heap.Type.FOR_SALE && h.type != Heap.Type.CRYSTAL_CHEST) {
                missedRooms.add(this.room(h.pos));
                continue;
            }
            for (Item item : h.items) {
                if (!(item instanceof Key)) continue;
                missedRooms.add(this.room(h.pos));
                continue block5;
            }
        }
        for (Blob b : this.blobs.values()) {
            if (b.volume <= 0) continue;
            if (b instanceof MagicalFireRoom.EternalFire) {
                missedRooms.add(this.room(MagicalFireRoom.class));
                continue;
            }
            if (!(b instanceof SacrificialFire)) continue;
            missedRooms.add(this.room(SacrificeRoom.class));
        }
        for (Iterator<Object> iterator : this.mobs.toArray(new Mob[0])) {
            if (((Mob)((Object)iterator)).alignment == Char.Alignment.ALLY) continue;
            if (iterator instanceof Statue && ((Statue)((Object)iterator)).levelGenStatue) {
                missedRooms.add(this.room(StatueRoom.class));
                continue;
            }
            if (!(iterator instanceof Mimic)) continue;
            missedRooms.add(this.room(((Mob)((Object)iterator)).pos));
        }
        for (int i = 0; i < this.length; ++i) {
            if (this.map[i] != 13 && this.map[i] != 10 && this.map[i] != 16) continue;
            Room candidate = null;
            for (int j : PathFinder.NEIGHBOURS4) {
                if (this.room(i + j) == null || candidate != null && missedRooms.contains(candidate)) continue;
                candidate = this.room(i + j);
            }
            if (candidate == null) continue;
            missedRooms.add(candidate);
        }
        for (Notes.KeyRecord rec : Notes.getRecords(Notes.KeyRecord.class)) {
            if (rec.depth() != depth || rec.type() != CrystalKey.class) continue;
            for (Room room : this.rooms()) {
                if (!SpecialRoom.CRYSTAL_KEY_SPECIALS.contains(room.getClass())) continue;
                missedRooms.add(room);
            }
        }
        switch (missedRooms.size()) {
            case 0: {
                return 1.0f;
            }
            case 1: {
                return 0.5f;
            }
            case 2: {
                return 0.2f;
            }
        }
        return 0.0f;
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put("rooms", this.rooms);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.rooms = new ArrayList<Bundlable>(bundle.getCollection("rooms"));
        for (Room r : this.rooms) {
            r.onLevelLoad(this);
            if (r.isEntrance()) {
                this.roomEntrance = r;
                continue;
            }
            if (!r.isExit()) continue;
            this.roomExit = r;
        }
    }

    static {
        limitedDocs.put(Document.SEWERS_GUARD, Dungeon.LimitedDrops.LORE_SEWERS);
        limitedDocs.put(Document.PRISON_WARDEN, Dungeon.LimitedDrops.LORE_PRISON);
        limitedDocs.put(Document.CAVES_EXPLORER, Dungeon.LimitedDrops.LORE_CAVES);
        limitedDocs.put(Document.CITY_WARLOCK, Dungeon.LimitedDrops.LORE_CITY);
        limitedDocs.put(Document.HALLS_KING, Dungeon.LimitedDrops.LORE_HALLS);
    }
}

