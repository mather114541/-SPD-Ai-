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
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Electricity;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DM300;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Pylon;
import com.shatteredpixel.shatteredpixeldungeon.effects.BlobEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.BlastParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.PylonSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;

public class CavesBossLevel
extends Level {
    private static int WIDTH = 33;
    private static int HEIGHT = 42;
    public static Rect diggableArea = new Rect(2, 11, 31, 40);
    public static Rect mainArena = new Rect(5, 14, 28, 37);
    public static Rect gate = new Rect(14, 13, 19, 14);
    public static int[] pylonPositions = new int[]{4 + 13 * WIDTH, 28 + 13 * WIDTH, 4 + 37 * WIDTH, 28 + 37 * WIDTH};
    private ArenaVisuals customArenaVisuals;
    private static final short n = -1;
    private static final short W = 4;
    private static final short e = 1;
    private static final short s = 14;
    private static short[] entrance1 = new short[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 1, 4, 4, -1, -1, -1, 4, 4, 1, 4, 4, -1, -1, 4, 4, 1, 1, 1, 1, -1, -1, 1, 1, 1, 4, 4, 1, -1, -1, 4, 4, 1, 4, 1, 1, -1, -1, 4, 4, 1, 1, 1, 1};
    private static short[] entrance2 = new short[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, 1, 1, -1, -1, -1, 4, 1, 4, 4, 1, -1, -1, -1, 1, 1, 1, 1, 1, -1, -1, 1, 4, 1, 4, 4, 1, -1, -1, 1, 4, 1, 4, 1, 1, -1, -1, 1, 1, 1, 1, 1, 1};
    private static short[] entrance3 = new short[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 4, 4, 1, 4, 4, -1, -1, -1, 4, 4, 1, 4, 4, -1, -1, -1, 1, 1, 1, 1, 1, -1, -1, -1, 4, 4, 1, 4, 1, -1, -1, -1, 4, 4, 1, 1, 1};
    private static short[] entrance4 = new short[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, 4, 1, -1, -1, -1, -1, -1, 4, 4, 1, -1, -1, -1, -1, 4, 4, 4, 1, -1, -1, -1, 4, 4, 4, 4, 1, -1, -1, 4, 4, 4, 4, 1, 1, -1, 1, 1, 1, 1, 1, 1, 1};
    private static short[][] entranceVariants = new short[][]{entrance1, entrance2, entrance3, entrance4};
    private static short[] corner1 = new short[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 1, 1, 1, 4, 4, 4, 4, 14, 14, 14, 4, 4, 1, 1, 4, 4, 4, 14, 14, 14, 4, 4, 4, 1, 1, 4, 4, 1, 4, 4, 4, 4, 4, 4, 1, -1, 4, 1, 4, 4, 4, 4, 4, -1, -1, -1, 4, 1, 1, 4, 4, 4, -1, -1, -1, -1, 4, 4, 1, 1, 4, -1, -1, -1, -1, -1, 4, 4, 4, 1, 1, -1, -1, -1, -1, -1, 4, 4, 4, 4, -1, -1, -1, -1, -1, -1};
    private static short[] corner2 = new short[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 1, 1, 1, 1, 1, 4, 4, 14, 14, 14, 4, 4, 4, 4, 1, 1, 4, 4, 1, 4, 4, 4, 4, 4, 4, 1, 4, 4, 1, 4, 4, 4, 4, -1, -1, -1, 4, 4, 1, 4, 4, 4, -1, -1, -1, -1, 4, 4, 1, 4, 4, -1, -1, -1, -1, -1, 4, 4, 1, 1, 4, -1, -1, -1, -1, -1, 4, 4, 4, 1, 1, -1, -1, -1, -1, -1};
    private static short[] corner3 = new short[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 1, 1, 1, 1, 4, 4, 4, 14, 14, 14, 4, 4, 4, 1, 4, 4, 4, 4, 1, 4, 4, 4, 4, 1, 4, -1, 4, 4, 1, 4, 4, 4, 4, 1, 1, -1, 4, 4, 1, 4, 4, 4, -1, -1, -1, -1, 4, 4, 1, 1, 1, 1, -1, -1, -1, -1, 4, 4, 4, 4, 4, 1, -1, -1, -1, -1, 4, 4, 4, 4, -1, -1, -1, -1, -1, -1};
    private static short[] corner4 = new short[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 4, 4, 4, 4, 4, 4, 4, 14, 14, 14, 1, 1, 1, 4, 4, 4, 4, 14, 14, 14, 4, 4, 1, 4, 4, 4, 4, 4, 1, 4, 4, 4, 1, 4, 4, -1, 4, 4, 1, 4, 4, 4, 1, 1, -1, -1, 4, 4, 1, 1, 1, 1, 1, -1, -1, -1, 4, 4, 4, 4, 4, 1, -1, -1, -1, -1, 4, 4, 4, 4, 4, -1, -1, -1, -1, -1, 4, 4, 4, 4, -1, -1, -1, -1, -1, -1};
    private static short[][] cornerVariants = new short[][]{corner1, corner2, corner3, corner4};

    public CavesBossLevel() {
        this.color1 = 5459774;
        this.color2 = 12179041;
    }

    @Override
    public void playLevelMusic() {
        if (this.locked) {
            if (BossHealthBar.isBleeding()) {
                Music.INSTANCE.play("music/caves_boss_finale.ogg", true);
            } else {
                Music.INSTANCE.play("music/caves_boss.ogg", true);
            }
        } else if (this.map[14 + 13 * this.width()] == 23) {
            Music.INSTANCE.end();
        } else {
            Music.INSTANCE.playTracks(CavesLevel.CAVES_TRACK_LIST, CavesLevel.CAVES_TRACK_CHANCES, false);
        }
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_caves.png";
    }

    @Override
    public String waterTex() {
        return "environment/water2.png";
    }

    @Override
    protected boolean build() {
        this.setSize(WIDTH, HEIGHT);
        Painter.fill(this, gate, 23);
        Painter.fillEllipse(this, mainArena, 1);
        boolean[] patch = Patch.generate(this.width, this.height - 14, 0.15f, 2, true);
        for (int i = 14 * this.width(); i < this.length(); ++i) {
            if (this.map[i] != 1) continue;
            if (patch[i - 14 * this.width()]) {
                this.map[i] = 29;
                continue;
            }
            if (Random.Int(Dungeon.isChallenged(256) ? 4 : 8) != 0) continue;
            this.map[i] = 19;
        }
        this.buildEntrance();
        this.buildCorners();
        new CavesPainter().paint(this, null);
        Painter.fill(this, 0, 3, this.width(), 4, 0);
        Painter.fill(this, 6, 7, 21, 1, 0);
        Painter.fill(this, 9, 3, 1, 6, 34);
        Painter.fill(this, 23, 3, 1, 6, 34);
        Painter.fill(this, 10, 8, 13, 1, 0);
        Painter.fill(this, 12, 9, 9, 1, 0);
        Painter.fill(this, 13, 10, 7, 1, 0);
        Painter.fill(this, 14, 3, 5, 10, 1);
        Painter.fill(this, 15, 2, 3, 3, 14);
        Painter.fill(this, 15, 5, 3, 1, 25);
        Painter.fill(this, 15, 7, 3, 1, 25);
        Painter.fill(this, 15, 9, 3, 1, 25);
        Painter.fill(this, 16, 5, 1, 6, 14);
        Painter.fill(this, 15, 0, 3, 3, 8);
        int exitCell = 16 + 2 * this.width();
        LevelTransition exit = new LevelTransition(this, exitCell, LevelTransition.Type.REGULAR_EXIT);
        exit.set(14, 0, 18, 2);
        this.transitions.add(exit);
        CustomTilemap customVisuals = new CityEntrance();
        customVisuals.setRect(0, 0, this.width(), 11);
        this.customTiles.add(customVisuals);
        customVisuals = new EntranceTerrain();
        customVisuals.setRect(WIDTH / 2 - 2, 0, 5, 11);
        this.customTerrain.add(customVisuals);
        customVisuals = new EntranceOverhang();
        customVisuals.setRect(0, 0, this.width(), 11);
        this.customWalls.add(customVisuals);
        this.customArenaVisuals = new ArenaVisuals();
        customVisuals = this.customArenaVisuals;
        customVisuals.setRect(0, 12, this.width(), 27);
        this.customTiles.add(customVisuals);
        boolean[] pass = new boolean[this.length];
        for (int i = 0; i < this.length; ++i) {
            pass[i] = this.map[i] == 1 || this.map[i] == 14 || this.map[i] == 20;
        }
        PathFinder.buildDistanceMap(16 + 25 * this.width(), pass);
        for (int i : pylonPositions) {
            if (PathFinder.distance[i] != Integer.MAX_VALUE) continue;
            return false;
        }
        return true;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        for (CustomTilemap c : this.customTiles) {
            if (!(c instanceof ArenaVisuals)) continue;
            this.customArenaVisuals = (ArenaVisuals)c;
        }
    }

    @Override
    protected void createMobs() {
        for (int i : pylonPositions) {
            Pylon pylon = new Pylon();
            pylon.pos = i;
            this.mobs.add(pylon);
        }
    }

    @Override
    public Actor addRespawner() {
        return null;
    }

    @Override
    protected void createItems() {
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            int pos;
            while ((pos = this.randomRespawnCell(null)) == this.entrance()) {
            }
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)pos).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
    }

    @Override
    public int randomRespawnCell(Char ch) {
        ArrayList<Integer> candidates = new ArrayList<Integer>();
        for (int i : PathFinder.NEIGHBOURS8) {
            int cell = this.entrance() + i;
            if (!this.passable[cell] || Actor.findChar(cell) != null || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) continue;
            candidates.add(cell);
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(candidates);
    }

    @Override
    public boolean setCellToWater(boolean includeTraps, int cell) {
        for (int i : pylonPositions) {
            if (Dungeon.level.distance(cell, i) > 1) continue;
            return false;
        }
        return super.setCellToWater(includeTraps, cell);
    }

    @Override
    public boolean invalidHeroPos(int tile) {
        if (this.map[CavesBossLevel.gate.left + CavesBossLevel.gate.top * this.width()] == 23) {
            Point p = this.cellToPoint(tile);
            if (p.y < CavesBossLevel.diggableArea.top) {
                return true;
            }
            if (p.y < CavesBossLevel.gate.bottom && p.x >= CavesBossLevel.gate.left && p.x < CavesBossLevel.gate.right) {
                return true;
            }
        }
        return super.invalidHeroPos(tile);
    }

    @Override
    public void occupyCell(Char ch) {
        int gatePos = this.pointToCell(new Point(CavesBossLevel.gate.left, CavesBossLevel.gate.top));
        if (ch == Dungeon.hero && !this.locked && this.solid[gatePos]) {
            for (int pos : pylonPositions) {
                if (Dungeon.level.distance(ch.pos, pos) > 3) continue;
                this.seal();
                break;
            }
        }
        super.occupyCell(ch);
    }

    @Override
    public void seal() {
        super.seal();
        Statistics.qualifiedForBossChallengeBadge = true;
        int entrance = this.entrance();
        CavesBossLevel.set(entrance, 4);
        Heap heap = (Heap)Dungeon.level.heaps.get(entrance);
        while (heap != null && !heap.isEmpty()) {
            int n;
            while (!Dungeon.level.passable[n = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]]) {
            }
            Heap dropped = Dungeon.level.drop(heap.pickUp(), n);
            dropped.seen = heap.seen;
        }
        Char ch = Actor.findChar(entrance);
        if (ch != null) {
            int n;
            while (!Dungeon.level.passable[n = entrance + PathFinder.NEIGHBOURS8[Random.Int(8)]]) {
            }
            ch.pos = n;
            ch.sprite.place(n);
        }
        GameScene.updateMap(entrance);
        Dungeon.observe();
        CellEmitter.get(entrance).start(Speck.factory(8), 0.07f, 10);
        PixelScene.shake(3.0f, 0.7f);
        Sample.INSTANCE.play("sounds/rocks.mp3");
        DM300 boss = new DM300();
        boss.state = boss.WANDERING;
        do {
            boss.pos = this.pointToCell(Random.element(mainArena.getPoints()));
        } while (!this.openSpace[boss.pos] || this.map[boss.pos] == 14 || Actor.findChar(boss.pos) != null);
        GameScene.add(boss);
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Music.INSTANCE.play("music/caves_boss.ogg", true);
            }
        });
    }

    @Override
    public void unseal() {
        super.unseal();
        ((Blob)this.blobs.get(PylonEnergy.class)).fullyClear();
        CavesBossLevel.set(this.entrance(), 7);
        int i = CavesBossLevel.gate.top * this.width();
        for (int j = CavesBossLevel.gate.left; j < CavesBossLevel.gate.right; ++j) {
            CavesBossLevel.set(i + j, 1);
            if (!Dungeon.level.heroFOV[i + j]) continue;
            CellEmitter.get(i + j).burst(BlastParticle.FACTORY, 10);
        }
        GameScene.updateMap();
        if (this.customArenaVisuals != null) {
            this.customArenaVisuals.updateState();
        }
        Dungeon.observe();
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

    public void activatePylon() {
        ArrayList<Pylon> pylons = new ArrayList<Pylon>();
        for (Mob m : this.mobs) {
            if (!(m instanceof Pylon) || m.alignment != Char.Alignment.NEUTRAL) continue;
            pylons.add((Pylon)m);
        }
        if (pylons.size() == 1) {
            ((Pylon)pylons.get(0)).activate();
        } else if (!pylons.isEmpty()) {
            Pylon closest = null;
            for (Pylon p : pylons) {
                if (closest != null && !(this.trueDistance(p.pos, Dungeon.hero.pos) < this.trueDistance(closest.pos, Dungeon.hero.pos))) continue;
                closest = p;
            }
            pylons.remove(closest);
            ((Pylon)Random.element(pylons)).activate();
        }
        for (int i = (CavesBossLevel.mainArena.top - 1) * this.width; i < this.length; ++i) {
            if (this.map[i] != 19 && this.map[i] != 29 && this.map[i] != 23) continue;
            GameScene.add(Blob.seed(i, 1, PylonEnergy.class));
        }
    }

    public void eliminatePylon() {
        int finalPylonsRemaining;
        if (this.customArenaVisuals != null) {
            this.customArenaVisuals.updateState();
        }
        int pylonsRemaining = 0;
        for (Mob m : this.mobs) {
            if (m instanceof DM300) {
                ((DM300)m).loseSupercharge();
                PylonEnergy.energySourceSprite = m.sprite;
                continue;
            }
            if (!(m instanceof Pylon)) continue;
            ++pylonsRemaining;
        }
        int n = finalPylonsRemaining = Dungeon.isChallenged(256) ? 1 : 2;
        if (pylonsRemaining > finalPylonsRemaining) {
            ((Blob)this.blobs.get(PylonEnergy.class)).fullyClear();
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 2: {
                return Messages.get(CavesLevel.class, "grass_name", new Object[0]);
            }
            case 15: {
                return Messages.get(CavesLevel.class, "high_grass_name", new Object[0]);
            }
            case 29: {
                return Messages.get(CavesLevel.class, "water_name", new Object[0]);
            }
            case 25: {
                return Messages.get(CityLevel.class, "statue_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CavesLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 29: {
                return super.tileDesc(tile) + "\n\n" + Messages.get(CavesBossLevel.class, "water_desc", new Object[0]);
            }
            case 7: 
            case 37: {
                return Messages.get(CavesLevel.class, "entrance_desc", new Object[0]);
            }
            case 8: {
                return Messages.get(CityLevel.class, "exit_desc", new Object[0]);
            }
            case 15: {
                return Messages.get(CavesLevel.class, "high_grass_desc", new Object[0]);
            }
            case 12: {
                return Messages.get(CavesLevel.class, "wall_deco_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(CavesLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 25: {
                return Messages.get(CityLevel.class, "statue_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CavesLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CavesLevel.addCavesVisuals(this, this.visuals);
        return this.visuals;
    }

    private void buildEntrance() {
        int entrance = 16 + 25 * this.width();
        int NW = entrance - 7 - 7 * this.width();
        int NE = entrance + 7 - 7 * this.width();
        int SE = entrance + 7 + 7 * this.width();
        int SW = entrance - 7 + 7 * this.width();
        short[] entranceTiles = (short[])Random.oneOf(entranceVariants);
        for (int i = 0; i < entranceTiles.length; ++i) {
            if (i % 8 == 0 && i != 0) {
                NW += this.width() - 8;
                NE += this.width() + 8;
                SE -= this.width() - 8;
                SW -= this.width() + 8;
            }
            if (entranceTiles[i] != -1) {
                this.map[SE] = this.map[SW] = entranceTiles[i];
                this.map[NE] = this.map[SW];
                this.map[NW] = this.map[SW];
            }
            ++NW;
            --NE;
            ++SW;
            --SE;
        }
        Painter.set((Level)this, entrance, 7);
        this.transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
    }

    private void buildCorners() {
        int NW = 2 + 11 * this.width();
        int NE = 30 + 11 * this.width();
        int SE = 30 + 39 * this.width();
        int SW = 2 + 39 * this.width();
        short[] cornerTiles = (short[])Random.oneOf(cornerVariants);
        for (int i = 0; i < cornerTiles.length; ++i) {
            if (i % 10 == 0 && i != 0) {
                NW += this.width() - 10;
                NE += this.width() + 10;
                SE -= this.width() - 10;
                SW -= this.width() + 10;
            }
            if (cornerTiles[i] != -1) {
                this.map[SE] = this.map[SW] = cornerTiles[i];
                this.map[NE] = this.map[SW];
                this.map[NW] = this.map[SW];
            }
            ++NW;
            --NE;
            ++SW;
            --SE;
        }
    }

    public static class CityEntrance
    extends CustomTilemap {
        private static short[] entryWay = new short[]{-1, 7, 7, 7, -1, -1, 1, 2, 3, -1, 8, 1, 2, 3, 12, 16, 9, 10, 11, 20, 16, 16, 22, 20, 20, 16, 17, 18, 19, 20, 16, 16, 18, 20, 20, 16, 17, 18, 19, 20, 16, 16, 18, 20, 20, 16, 17, 18, 19, 20, 24, 25, 26, 27, 28};

        public CityEntrance() {
            this.texture = "environment/custom_tiles/caves_boss.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int entryPos = 0;
            for (int i = 0; i < data.length; ++i) {
                if (i % this.tileW == this.tileW / 2 - 2) {
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i] = entryWay[entryPos++];
                    continue;
                }
                if (i / this.tileW == 2) {
                    data[i] = 13;
                    continue;
                }
                if (i / this.tileW == 3) {
                    if (i % this.tileW == 9 || i % this.tileW == 23) {
                        data[i] = -1;
                        continue;
                    }
                    data[i] = 21;
                    continue;
                }
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }
    }

    public static class EntranceTerrain
    extends CustomTilemap {
        private static short[] entryWay = new short[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 8, -1, -1, -1, 12, 30, -1, -1, -1, 31, -1, -1, -1, -1, -1, -1, 17, -1, 19, -1, -1, -1, -1, -1, -1, -1, 17, -1, 19, -1, -1, -1, -1, -1, -1, -1, 17, -1, 19, -1, -1, -1, -1, -1, -1};

        public EntranceTerrain() {
            this.texture = "environment/custom_tiles/caves_boss.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int entryPos = 0;
            for (int i = 0; i < data.length; ++i) {
                data[i] = entryWay[entryPos++];
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }

    public static class EntranceOverhang
    extends CustomTilemap {
        private static short[] entryWay = new short[]{0, 7, 7, 7, 4, 0, 15, 15, 15, 4, -1, 23, 23, 23, -1, -1, -1, -1, -1, -1, -1, 6, -1, 14, -1, -1, -1, -1, -1, -1, -1, 6, -1, 14, -1, -1, -1, -1, -1, -1, -1, 6, -1, 14, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

        public EntranceOverhang() {
            this.texture = "environment/custom_tiles/caves_boss.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int entryPos = 0;
            for (int i = 0; i < data.length; ++i) {
                if (i % this.tileW == this.tileW / 2 - 2) {
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i++] = entryWay[entryPos++];
                    data[i] = entryWay[entryPos++];
                    continue;
                }
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }
    }

    public static class ArenaVisuals
    extends CustomTilemap {
        public ArenaVisuals() {
            this.texture = "environment/custom_tiles/caves_boss.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            this.updateState();
            return v;
        }

        public void updateState() {
            if (this.vis != null) {
                int[] data = new int[this.tileW * this.tileH];
                int j = Dungeon.level.width() * this.tileY;
                for (int i = 0; i < data.length; ++i) {
                    if (Dungeon.level.map[j] == 14) {
                        for (int k : pylonPositions) {
                            if (k == j) {
                                if (Dungeon.level.locked && !(Actor.findChar(k) instanceof Pylon)) {
                                    data[i] = 38;
                                    continue;
                                }
                                data[i] = -1;
                                continue;
                            }
                            if (!Dungeon.level.adjacent(k, j)) continue;
                            int w = Dungeon.level.width;
                            data[i] = 54 + (j % w + 8 * (j / w)) - (k % w + 8 * (k / w));
                        }
                    } else if (Dungeon.level.map[j] == 19) {
                        data[i] = 37;
                    } else if (gate.inside(Dungeon.level.cellToPoint(j))) {
                        int idx = Dungeon.level.solid[j] ? 40 : 32;
                        data[i++] = idx++;
                        data[i++] = idx++;
                        data[i++] = idx++;
                        data[i++] = idx++;
                        data[i] = idx;
                        j += 4;
                    } else {
                        data[i] = -1;
                    }
                    ++j;
                }
                this.vis.map(data, this.tileW);
            }
        }

        @Override
        public String name(int tileX, int tileY) {
            int i = tileX + this.tileW * (tileY + this.tileY);
            if (Dungeon.level.map[i] == 19) {
                return Messages.get(CavesBossLevel.class, "wires_name", new Object[0]);
            }
            if (gate.inside(Dungeon.level.cellToPoint(i))) {
                return Messages.get(CavesBossLevel.class, "gate_name", new Object[0]);
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int i = tileX + this.tileW * (tileY + this.tileY);
            if (Dungeon.level.map[i] == 19) {
                return Messages.get(CavesBossLevel.class, "wires_desc", new Object[0]);
            }
            if (gate.inside(Dungeon.level.cellToPoint(i))) {
                if (Dungeon.level.solid[i]) {
                    return Messages.get(CavesBossLevel.class, "gate_desc", new Object[0]);
                }
                return Messages.get(CavesBossLevel.class, "gate_desc_broken", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }

        @Override
        public Image image(int tileX, int tileY) {
            int i = tileX + this.tileW * (tileY + this.tileY);
            for (int k : pylonPositions) {
                if (Dungeon.level.distance(i, k) > 1) continue;
                return null;
            }
            return super.image(tileX, tileY);
        }
    }

    public static class PylonEnergy
    extends Blob {
        private static CharSprite energySourceSprite = null;
        private static Emitter.Factory DIRECTED_SPARKS = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                if (energySourceSprite == null) {
                    for (Char c : Actor.chars()) {
                        if (c instanceof Pylon && c.alignment != Char.Alignment.NEUTRAL) {
                            energySourceSprite = c.sprite;
                            break;
                        }
                        if (!(c instanceof DM300)) continue;
                        energySourceSprite = c.sprite;
                    }
                    if (energySourceSprite == null) {
                        return;
                    }
                }
                float dist = Math.max(Math.abs(PylonEnergy.energySourceSprite.x - x), Math.abs(PylonEnergy.energySourceSprite.y - y));
                dist = GameMath.gate(0.0f, dist - 40.0f, 320.0f);
                if (Random.Float(360.0f) > dist) {
                    SparkParticle s = (SparkParticle)emitter.recycle(SparkParticle.class);
                    s.resetAttracting(x, y, energySourceSprite);
                }
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        @Override
        protected void evolve() {
            for (int cell = 0; cell < Dungeon.level.length(); ++cell) {
                Char ch;
                if (!Dungeon.level.insideMap(cell)) continue;
                this.off[cell] = this.cur[cell];
                if (this.off[cell] == 0 && Dungeon.level.water[cell]) {
                    this.area.union(cell % Dungeon.level.width(), cell / Dungeon.level.width());
                    int n = cell;
                    this.off[n] = this.off[n] + 1;
                }
                this.volume += this.off[cell];
                if (this.off[cell] <= 0 || (ch = Actor.findChar(cell)) == null || ch instanceof DM300 || ch.flying) continue;
                if (ch instanceof Mob) {
                    Buff.prolong(ch, Trap.HazardAssistTracker.class, 50.0f);
                }
                Sample.INSTANCE.play("sounds/lightning.mp3");
                ch.damage(Random.NormalIntRange(6, 12), new Electricity());
                ch.sprite.flash();
                if (ch != Dungeon.hero) continue;
                if (energySourceSprite != null && energySourceSprite instanceof PylonSprite) {
                    Statistics.qualifiedForBossChallengeBadge = false;
                }
                Statistics.bossScores[2] = Statistics.bossScores[2] - 200;
                if (ch.isAlive()) continue;
                Dungeon.fail(DM300.class);
                GLog.n(Messages.get(Electricity.class, "ondeath", new Object[0]), new Object[0]);
            }
        }

        @Override
        public void fullyClear() {
            super.fullyClear();
            energySourceSprite = null;
        }

        @Override
        public String tileDesc() {
            return Messages.get(CavesBossLevel.class, "energy_desc", new Object[0]);
        }

        @Override
        public void use(BlobEmitter emitter) {
            super.use(emitter);
            energySourceSprite = null;
            emitter.pour(DIRECTED_SPARKS, 0.08f);
        }
    }
}

