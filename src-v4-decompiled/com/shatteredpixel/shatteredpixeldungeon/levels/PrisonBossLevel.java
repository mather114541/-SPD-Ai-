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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Regrowth;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.StormCloud;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Doom;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Tengu;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.bombs.Bomb;
import com.shatteredpixel.shatteredpixeldungeon.items.keys.IronKey;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.missiles.HeavyBoomerang;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TenguDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.TargetHealthIndicator;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.BArray;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.Iterator;

public class PrisonBossLevel
extends Level {
    private State state;
    private Tengu tengu;
    private static final String STATE = "state";
    private static final String TENGU = "tengu";
    private static final String STORED_ITEMS = "storeditems";
    private static final String TRIGGERED = "triggered";
    private static final int ENTRANCE_POS = 138;
    private static final Rect entranceRoom = new Rect(8, 2, 13, 8);
    private static final Rect startHallway = new Rect(9, 7, 12, 24);
    private static final Rect[] startCells = new Rect[]{new Rect(5, 9, 10, 16), new Rect(11, 9, 16, 16), new Rect(5, 15, 10, 22), new Rect(11, 15, 16, 22)};
    private static final Rect tenguCell = new Rect(6, 23, 15, 32);
    private static final Point tenguCellCenter = new Point(10, 27);
    private static final Point tenguCellDoor = new Point(10, 23);
    private static final Point[] startTorches = new Point[]{new Point(10, 2), new Point(7, 9), new Point(13, 9), new Point(7, 15), new Point(13, 15), new Point(8, 23), new Point(12, 23)};
    private static final Rect pauseSafeArea = new Rect(9, 2, 12, 12);
    private static final Rect arena = new Rect(3, 1, 18, 16);
    private static int W = 4;
    private static int D = 12;
    private static int e = 1;
    private static int E = 8;
    private static int C = 0;
    private static final Point endStart = new Point(PrisonBossLevel.startHallway.left + 2, PrisonBossLevel.startHallway.top + 2);
    private static final Point levelExit = new Point(PrisonBossLevel.endStart.x + 11, PrisonBossLevel.endStart.y + 6);
    private static final int[] endMap = new int[]{W, W, D, W, W, W, W, W, W, W, W, W, W, W, W, e, e, e, W, W, W, W, W, W, W, W, W, W, W, e, e, e, e, e, e, e, e, W, W, W, W, W, e, e, e, e, e, e, e, e, e, e, e, e, W, W, e, e, e, e, e, e, e, e, e, e, e, e, e, W, e, e, e, C, C, C, C, C, C, C, C, e, e, W, e, W, C, C, C, C, C, C, C, C, C, E, E, W, e, e, e, C, C, C, C, C, C, C, C, E, E, W, e, e, e, e, e, C, C, C, C, C, C, E, E, W, e, e, e, e, e, e, e, W, W, W, C, C, C, W, W, e, e, e, e, e, W, W, W, W, C, C, C, W, W, e, e, e, e, W, W, W, W, W, W, C, C, W, W, W, W, W, W, W, W, W, W, W, W, C, C, W, W, W, W, W, W, W, W, W, W, W, W, C, C, W, W, D, W, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, e, e, e, W, W, W, W, W, W, W, W, C, C, W, W, W, W, W, W, W, W, W, W, W, W, C, C, W};
    private ArrayList<Item> storedItems;
    private boolean[] triggered;

    public PrisonBossLevel() {
        this.color1 = 6976061;
        this.color2 = 8950348;
        this.viewDistance = 12;
        this.storedItems = new ArrayList();
        this.triggered = new boolean[]{false, false, false, false};
    }

    @Override
    public void playLevelMusic() {
        if (this.state == State.START) {
            Music.INSTANCE.end();
        } else if (this.state == State.WON) {
            Music.INSTANCE.playTracks(PrisonLevel.PRISON_TRACK_LIST, PrisonLevel.PRISON_TRACK_CHANCES, false);
        } else {
            Music.INSTANCE.play("music/prison_boss.ogg", true);
        }
    }

    public State state() {
        return this.state;
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_prison.png";
    }

    @Override
    public String waterTex() {
        return "environment/water1.png";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(STATE, this.state);
        bundle.put(TENGU, this.tengu);
        bundle.put(STORED_ITEMS, this.storedItems);
        bundle.put(TRIGGERED, this.triggered);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.state = bundle.getEnum(STATE, State.class);
        if (this.state == State.START || this.state == State.FIGHT_PAUSE) {
            this.tengu = (Tengu)bundle.get(TENGU);
        } else {
            for (Mob mob : this.mobs) {
                if (!(mob instanceof Tengu)) continue;
                this.tengu = (Tengu)mob;
                break;
            }
        }
        for (Bundlable item : bundle.getCollection(STORED_ITEMS)) {
            this.storedItems.add((Item)item);
        }
        this.triggered = bundle.getBooleanArray(TRIGGERED);
    }

    @Override
    protected boolean build() {
        this.setSize(32, 32);
        this.state = State.START;
        this.setMapStart();
        return true;
    }

    private void setMapStart() {
        this.transitions.add(new LevelTransition(this, 138, LevelTransition.Type.REGULAR_ENTRANCE));
        Painter.fill(this, 0, 0, 32, 32, 4);
        Painter.fill(this, entranceRoom, 4);
        Painter.fill(this, entranceRoom, 1, 1);
        Painter.set((Level)this, 138, 7);
        Painter.fill(this, startHallway, 4);
        Painter.fill(this, startHallway, 1, 1);
        Painter.set(this, PrisonBossLevel.startHallway.left + 1, PrisonBossLevel.startHallway.top, 5);
        for (Rect r : startCells) {
            Painter.fill(this, r, 4);
            Painter.fill(this, r, 1, 1);
        }
        Painter.set(this, PrisonBossLevel.startHallway.left, PrisonBossLevel.startHallway.top + 5, 5);
        Painter.set(this, PrisonBossLevel.startHallway.right - 1, PrisonBossLevel.startHallway.top + 5, 5);
        Painter.set(this, PrisonBossLevel.startHallway.left, PrisonBossLevel.startHallway.top + 11, 5);
        Painter.set(this, PrisonBossLevel.startHallway.right - 1, PrisonBossLevel.startHallway.top + 11, 5);
        Painter.fill(this, tenguCell, 4);
        Painter.fill(this, tenguCell, 1, 1);
        Painter.set(this, PrisonBossLevel.tenguCell.left + 4, PrisonBossLevel.tenguCell.top, 10);
        for (Point p : startTorches) {
            Painter.set((Level)this, p, 12);
        }
        this.addCagesToCells();
        LevelTransition exit = new LevelTransition(this, this.pointToCell(levelExit), LevelTransition.Type.REGULAR_EXIT);
        exit.right += 2;
        exit.bottom += 3;
        this.transitions.add(exit);
    }

    private void setMapPause() {
        this.setMapStart();
        this.transitions.clear();
        Painter.set(this, PrisonBossLevel.tenguCell.left + 4, PrisonBossLevel.tenguCell.top, 5);
        Painter.fill(this, PrisonBossLevel.startCells[1].left, PrisonBossLevel.startCells[1].top + 3, 1, 7, 1);
        Painter.fill(this, PrisonBossLevel.startCells[1].left + 2, PrisonBossLevel.startCells[1].top + 2, 3, 10, 1);
        Painter.fill(this, entranceRoom, 4);
        Painter.set(this, PrisonBossLevel.startHallway.left + 1, PrisonBossLevel.startHallway.top, 1);
        Painter.set(this, PrisonBossLevel.startHallway.left + 1, PrisonBossLevel.startHallway.top + 1, 5);
        this.addCagesToCells();
    }

    private void setMapArena() {
        this.transitions.clear();
        Painter.fill(this, 0, 0, 32, 32, 4);
        Painter.fill(this, arena, 4);
        Painter.fillEllipse(this, arena, 1, 1);
    }

    private void setMapEnd() {
        Painter.fill(this, 0, 0, 32, 32, 4);
        this.setMapStart();
        for (Heap h : this.heaps.valueList()) {
            if (!(h.peek() instanceof IronKey)) continue;
            h.destroy();
        }
        CustomTilemap vis = new ExitVisual();
        vis.pos(11, 10);
        this.customTiles.add(vis);
        GameScene.add(vis, false);
        vis = new ExitVisualWalls();
        vis.pos(11, 10);
        this.customWalls.add(vis);
        GameScene.add(vis, true);
        Painter.set(this, PrisonBossLevel.tenguCell.left + 4, PrisonBossLevel.tenguCell.top, 5);
        int i = 0;
        for (int cell = this.pointToCell(endStart); cell < this.length(); cell += this.width()) {
            System.arraycopy(endMap, i, this.map, cell, 14);
            i += 14;
        }
        this.addCagesToCells();
    }

    private void clearEntities(Rect safeArea) {
        for (Heap heap : this.heaps.valueList()) {
            if (safeArea != null && safeArea.inside(this.cellToPoint(heap.pos))) continue;
            for (Item item : heap.items) {
                if (item instanceof Bomb && ((Bomb)item).fuse != null) continue;
                this.storedItems.add(item);
            }
            heap.destroy();
        }
        for (HeavyBoomerang.CircleBack b : Dungeon.hero.buffs(HeavyBoomerang.CircleBack.class)) {
            if (b.activeDepth() != Dungeon.depth || safeArea != null && safeArea.inside(this.cellToPoint(b.returnPos()))) continue;
            this.storedItems.add(b.cancel());
        }
        for (Iterator<Object> iterator : Dungeon.level.mobs.toArray(new Mob[0])) {
            if (iterator == this.tengu || safeArea != null && safeArea.inside(this.cellToPoint(((Mob)((Object)iterator)).pos))) continue;
            ((Mob)((Object)iterator)).destroy();
            if (((Mob)((Object)iterator)).sprite == null) continue;
            ((Mob)((Object)iterator)).sprite.killAndErase();
        }
        for (Plant plant : this.plants.valueList()) {
            if (safeArea != null && safeArea.inside(this.cellToPoint(plant.pos))) continue;
            this.plants.remove(plant.pos);
        }
    }

    private void cleanMapState() {
        this.buildFlagMaps();
        this.cleanWalls();
        BArray.setFalse(this.visited);
        BArray.setFalse(this.mapped);
        for (Blob blob : this.blobs.values()) {
            blob.fullyClear();
        }
        this.addVisuals();
        this.traps.clear();
        for (CustomTilemap t : this.customTiles.toArray(new CustomTilemap[0])) {
            if (!(t instanceof FadingTraps)) continue;
            ((FadingTraps)t).remove();
        }
        GameScene.resetMap();
        Dungeon.observe();
    }

    public void addCagesToCells() {
        Random.pushGenerator(Dungeon.seedCurDepth());
        for (int i = 0; i < 5; ++i) {
            int cell = this.randomPrisonCellPos();
            boolean valid = false;
            for (int j : PathFinder.NEIGHBOURS4) {
                if (this.map[cell + j] != 4) continue;
                valid = true;
            }
            if (!valid) continue;
            Painter.set((Level)this, cell, 33);
        }
        Random.popGenerator();
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        PrisonLevel.addPrisonVisuals(this, this.visuals);
        return this.visuals;
    }

    public void progress() {
        switch (this.state.ordinal()) {
            case 0: {
                int tenguPos = this.pointToCell(tenguCellCenter);
                if (Actor.findChar(tenguPos) != null) {
                    ArrayList<Integer> candidates = new ArrayList<Integer>();
                    for (int i : PathFinder.NEIGHBOURS8) {
                        if (Actor.findChar(tenguPos + i) != null) continue;
                        candidates.add(tenguPos + i);
                    }
                    if (!candidates.isEmpty()) {
                        tenguPos = (Integer)Random.element(candidates);
                    } else {
                        return;
                    }
                }
                this.seal();
                Statistics.qualifiedForBossChallengeBadge = true;
                PrisonBossLevel.set(this.pointToCell(tenguCellDoor), 10);
                GameScene.updateMap(this.pointToCell(tenguCellDoor));
                int tenguDoorPos = this.pointToCell(tenguCellDoor);
                Heap h = (Heap)this.heaps.get(tenguDoorPos);
                if (h != null) {
                    for (Item i : h.items) {
                        this.drop((Item)i, (int)(tenguDoorPos + this.width())).sprite.drop(tenguDoorPos);
                    }
                    h.destroy();
                }
                int doorPos = this.pointToCell(tenguCellDoor);
                Mob.holdAllies(this, doorPos);
                Mob.restoreAllies(this, Dungeon.hero.pos, doorPos);
                this.tengu.state = this.tengu.HUNTING;
                this.tengu.pos = tenguPos;
                GameScene.add(this.tengu, 1.0f);
                this.tengu.notice();
                CellEmitter.get(this.tengu.pos).burst(Speck.factory(7), 6);
                Sample.INSTANCE.play("sounds/puff.mp3");
                this.state = State.FIGHT_START;
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.play("music/prison_boss.ogg", true);
                    }
                });
                break;
            }
            case 1: {
                this.clearEntities(tenguCell);
                this.setMapPause();
                this.cleanMapState();
                Doom d = this.tengu.buff(Doom.class);
                Actor.remove(this.tengu);
                this.mobs.remove(this.tengu);
                this.tengu.clearTime();
                TargetHealthIndicator.instance.target(null);
                this.tengu.sprite.kill();
                if (d != null) {
                    this.tengu.add(d);
                }
                GameScene.flash(-2130706433);
                Sample.INSTANCE.play("sounds/blast.mp3");
                this.state = State.FIGHT_PAUSE;
                break;
            }
            case 2: {
                Dungeon.hero.interrupt();
                this.clearEntities(pauseSafeArea);
                this.setMapArena();
                this.cleanMapState();
                this.tengu.state = this.tengu.HUNTING;
                this.tengu.pos = PrisonBossLevel.arena.left + arena.width() / 2 + this.width() * (PrisonBossLevel.arena.top + 2);
                GameScene.add(this.tengu, 1.0f);
                this.tengu.notice();
                CellEmitter.get(this.tengu.pos).burst(Speck.factory(7), 6);
                GameScene.flash(-2130706433);
                Sample.INSTANCE.play("sounds/blast.mp3");
                this.state = State.FIGHT_ARENA;
                break;
            }
            case 3: {
                this.unseal();
                Dungeon.hero.interrupt();
                Dungeon.hero.pos = PrisonBossLevel.tenguCell.left + 4 + (PrisonBossLevel.tenguCell.top + 2) * this.width();
                Dungeon.hero.sprite.interruptMotion();
                Dungeon.hero.sprite.place(Dungeon.hero.pos);
                Camera.main.snapTo(Dungeon.hero.sprite.center());
                this.tengu.pos = this.pointToCell(tenguCellCenter);
                this.tengu.sprite.place(this.tengu.pos);
                ArrayList<Mob> allies = new ArrayList<Mob>();
                for (Mob m : this.mobs.toArray(new Mob[0])) {
                    if (m.alignment != Char.Alignment.ALLY || m.properties().contains((Object)Char.Property.IMMOVABLE)) continue;
                    allies.add(m);
                    this.mobs.remove(m);
                }
                this.setMapEnd();
                for (Mob m : allies) {
                    do {
                        m.pos = this.randomTenguCellPos();
                    } while (this.findMob(m.pos) != null || m.pos == Dungeon.hero.pos);
                    if (m.sprite != null) {
                        m.sprite.place(m.pos);
                    }
                    this.mobs.add(m);
                }
                this.tengu.die(Dungeon.hero);
                this.clearEntities(tenguCell);
                this.cleanMapState();
                for (Item item : this.storedItems) {
                    if (item instanceof Tengu.BombAbility.BombItem || item instanceof Tengu.ShockerAbility.ShockerItem) continue;
                    this.drop(item, this.randomTenguCellPos());
                }
                GameScene.flash(-2130706433);
                Sample.INSTANCE.play("sounds/blast.mp3");
                this.state = State.WON;
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
            }
        }
    }

    @Override
    public void occupyCell(Char ch) {
        if (ch == Dungeon.hero) {
            switch (this.state.ordinal()) {
                case 0: {
                    if (this.cellToPoint((int)ch.pos).y <= PrisonBossLevel.tenguCell.top) break;
                    this.progress();
                    break;
                }
                case 2: {
                    if (this.cellToPoint((int)ch.pos).y > PrisonBossLevel.startHallway.top + 1) break;
                    this.progress();
                }
            }
        }
        super.occupyCell(ch);
    }

    @Override
    protected void createMobs() {
        this.tengu = new Tengu();
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        int pos;
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            while ((pos = this.randomRespawnCell(null)) == this.entrance()) {
            }
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
        while (this.solid[pos = this.randomPrisonCellPos()]) {
        }
        this.drop(new IronKey(10), pos);
    }

    @Override
    public ArrayList<Item> getItemsToPreserveFromSealedResurrect() {
        ArrayList<Item> items = super.getItemsToPreserveFromSealedResurrect();
        items.addAll(this.storedItems);
        for (Item i : items.toArray(new Item[0])) {
            if (!(i instanceof Tengu.BombAbility.BombItem) && !(i instanceof Tengu.ShockerAbility.ShockerItem)) continue;
            items.remove(i);
        }
        return items;
    }

    private int randomPrisonCellPos() {
        Rect room = startCells[Random.Int(startCells.length)];
        return Random.IntRange(room.left + 1, room.right - 2) + this.width() * Random.IntRange(room.top + 1, room.bottom - 2);
    }

    public int randomTenguCellPos() {
        return Random.IntRange(PrisonBossLevel.tenguCell.left + 1, PrisonBossLevel.tenguCell.right - 2) + this.width() * Random.IntRange(PrisonBossLevel.tenguCell.top + 1, PrisonBossLevel.tenguCell.bottom - 2);
    }

    public void cleanTenguCell() {
        this.traps.clear();
        Painter.fill(this, tenguCell, 1, 1);
        this.buildFlagMaps();
        for (CustomTilemap vis : this.customTiles.toArray(new CustomTilemap[0])) {
            if (!(vis instanceof FadingTraps)) continue;
            ((FadingTraps)vis).remove();
        }
    }

    public void placeTrapsInTenguCell(float fill) {
        boolean[] trapsPatch;
        Point tenguPoint = this.cellToPoint(this.tengu.pos);
        Point heroPoint = this.cellToPoint(Dungeon.hero.pos);
        PathFinder.setMapSize(7, 7);
        int tenguPos = tenguPoint.x - (PrisonBossLevel.tenguCell.left + 1) + (tenguPoint.y - (PrisonBossLevel.tenguCell.top + 1)) * 7;
        int heroPos = heroPoint.x - (PrisonBossLevel.tenguCell.left + 1) + (heroPoint.y - (PrisonBossLevel.tenguCell.top + 1)) * 7;
        if (Dungeon.isChallenged(256)) {
            fill = 0.675f + fill / 4.0f;
        }
        int tries = 0;
        do {
            if (++tries >= 100) {
                tries = 0;
                fill -= 0.01f;
            }
            trapsPatch = Patch.generate(7, 7, fill, 0, false);
            PathFinder.buildDistanceMap(tenguPos, BArray.not(trapsPatch, null));
        } while ((double)PathFinder.distance[heroPos] < Math.ceil(7.0f * fill) || (double)PathFinder.distance[heroPos] > Math.ceil(4.0f + 4.0f * fill));
        System.out.println(tries);
        PathFinder.setMapSize(this.width(), this.height());
        for (int i = 0; i < trapsPatch.length; ++i) {
            int y;
            int x;
            int cell;
            if (!trapsPatch[i] || Blob.volumeAt(cell = (x = i % 7) + PrisonBossLevel.tenguCell.left + 1 + ((y = i / 7) + PrisonBossLevel.tenguCell.top + 1) * this.width(), StormCloud.class) != 0 || Blob.volumeAt(cell, Regrowth.class) > 9 || Dungeon.level.plants.get(cell) != null || Actor.findChar(cell) != null) continue;
            Level.set(cell, 17);
            this.setTrap(new TenguDartTrap().hide(), cell);
            CellEmitter.get(cell).burst(Speck.factory(2), 2);
        }
        GameScene.updateMap();
        FadingTraps t = new FadingTraps();
        t.fadeDelay = 2.0f;
        t.setCoveringArea(tenguCell);
        GameScene.add(t, false);
        this.customTiles.add(t);
    }

    @Override
    public int randomRespawnCell(Char ch) {
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = 138 + i;
            if (!this.passable[cell] || Actor.findChar(cell) != null || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) continue;
            candidates.add(cell);
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(candidates);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(PrisonLevel.class, "water_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(PrisonLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 20: {
                return Messages.get(PrisonLevel.class, "empty_deco_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(PrisonLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(PrisonLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    public static enum State {
        START,
        FIGHT_START,
        FIGHT_PAUSE,
        FIGHT_ARENA,
        WON;

    }

    public static class ExitVisual
    extends CustomTilemap {
        final int TEX_WIDTH = 256;
        private static byte[] render = new byte[]{0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        public ExitVisual() {
            this.texture = "environment/custom_tiles/prison_exit.png";
            this.tileW = 14;
            this.tileH = 11;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = this.mapSimpleImage(0, 0, 256);
            for (int i = 0; i < data.length; ++i) {
                if (render[i] != 0) continue;
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.tileX = 11;
            this.tileY = 10;
            this.tileW = 14;
            this.tileH = 11;
        }
    }

    public static class ExitVisualWalls
    extends CustomTilemap {
        final int TEX_WIDTH = 256;
        private static byte[] render = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1};

        public ExitVisualWalls() {
            this.texture = "environment/custom_tiles/prison_exit.png";
            this.tileW = 14;
            this.tileH = 22;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = this.mapSimpleImage(0, 10, 256);
            for (int i = 0; i < data.length; ++i) {
                if (render[i] != 0) continue;
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public void restoreFromBundle(Bundle bundle) {
            super.restoreFromBundle(bundle);
            this.tileX = 11;
            this.tileY = 10;
            this.tileW = 14;
            this.tileH = 22;
        }
    }

    public static class FadingTraps
    extends CustomTilemap {
        Rect area;
        private float fadeDuration;
        private float initialAlpha;
        private float fadeDelay;

        public FadingTraps() {
            this.texture = "environment/terrain_features.png";
            this.fadeDuration = 1.0f;
            this.initialAlpha = 0.4f;
            this.fadeDelay = 1.0f;
        }

        public void setCoveringArea(Rect area) {
            this.tileX = area.left;
            this.tileY = area.top;
            this.tileH = area.bottom - area.top;
            this.tileW = area.right - area.left;
            this.area = area;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int i = 0;
            for (int y = this.tileY; y < this.tileY + this.tileH; ++y) {
                int cell = this.tileX + y * Dungeon.level.width();
                for (int x = this.tileX; x < this.tileX + this.tileW; ++x) {
                    Trap t = (Trap)Dungeon.level.traps.get(cell);
                    data[i] = t != null ? t.color + t.shape * 16 : -1;
                    ++cell;
                    ++i;
                }
            }
            v.map(data, this.tileW);
            this.setFade();
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            int cell = this.tileX + tileX + Dungeon.level.width() * (this.tileY + tileY);
            if (Dungeon.level.traps.get(cell) != null) {
                return Messages.titleCase(((Trap)Dungeon.level.traps.get(cell)).name());
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int cell = this.tileX + tileX + Dungeon.level.width() * (this.tileY + tileY);
            if (Dungeon.level.traps.get(cell) != null) {
                return ((Trap)Dungeon.level.traps.get(cell)).desc();
            }
            return super.desc(tileX, tileY);
        }

        private void setFade() {
            if (this.vis == null) {
                return;
            }
            this.vis.alpha(this.initialAlpha);
            Actor.addDelayed(new Actor(){
                {
                    this.actPriority = 1;
                }

                @Override
                protected boolean act() {
                    Actor.remove(this);
                    if (vis != null && ((FadingTraps)this).vis.parent != null) {
                        Dungeon.level.customTiles.remove(this);
                        ((FadingTraps)this).vis.parent.add(new AlphaTweener(vis, 0.0f, fadeDuration){

                            @Override
                            protected void onComplete() {
                                super.onComplete();
                                vis.killAndErase();
                                this.killAndErase();
                            }
                        });
                    }
                    return true;
                }
            }, this.fadeDelay);
        }

        private void remove() {
            if (this.vis != null) {
                this.vis.killAndErase();
            }
            Dungeon.level.customTiles.remove(this);
        }
    }
}

