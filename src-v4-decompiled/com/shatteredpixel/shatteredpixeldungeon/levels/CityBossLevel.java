/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.DwarfKing;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.CityLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.ImpShopRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.tweeners.AlphaTweener;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;
import java.util.ArrayList;
import java.util.HashSet;

public class CityBossLevel
extends Level {
    private static int WIDTH = 15;
    private static int HEIGHT = 48;
    private static final Rect entry = new Rect(1, 37, 14, 48);
    private static final Rect arena = new Rect(1, 25, 14, 38);
    private static final Rect end = new Rect(0, 0, 15, 22);
    private static final int bottomDoor = 7 + (CityBossLevel.arena.bottom - 1) * 15;
    private static final int topDoor = 7 + CityBossLevel.arena.top * 15;
    public static final int throne;
    private static final int[] pedestals;
    private ImpShopRoom impShop;
    private static final String IMP_SHOP = "imp_shop";

    public CityBossLevel() {
        this.color1 = 4941366;
        this.color2 = 0xF2F2F2;
    }

    @Override
    public void playLevelMusic() {
        if (this.locked) {
            if (BossHealthBar.isBleeding()) {
                Music.INSTANCE.play("music/city_boss_finale.ogg", true);
            } else {
                Music.INSTANCE.play("music/city_boss.ogg", true);
            }
        } else if (this.map[topDoor] == 10) {
            Music.INSTANCE.end();
        } else {
            Music.INSTANCE.playTracks(CityLevel.CITY_TRACK_LIST, CityLevel.CITY_TRACK_CHANCES, false);
        }
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_city.png";
    }

    @Override
    public String waterTex() {
        return "environment/water3.png";
    }

    @Override
    public void storeInBundle(Bundle bundle) {
        super.storeInBundle(bundle);
        bundle.put(IMP_SHOP, this.impShop);
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.impShop = (ImpShopRoom)bundle.get(IMP_SHOP);
        if (this.map[topDoor] != 10 && Imp.Quest.earnedShop() && !this.impShop.shopSpawned()) {
            this.spawnShop();
        }
    }

    @Override
    protected boolean build() {
        this.setSize(WIDTH, HEIGHT);
        Painter.fill(this, entry, 4);
        Painter.fill(this, entry, 1, 27);
        Painter.fill(this, entry, 2, 1);
        Painter.fill(this, CityBossLevel.entry.left + 3, CityBossLevel.entry.top + 3, 1, 5, 27);
        Painter.fill(this, CityBossLevel.entry.right - 4, CityBossLevel.entry.top + 3, 1, 5, 27);
        Painter.set(this, CityBossLevel.entry.left + 5, CityBossLevel.entry.top + 1, 33);
        Painter.set(this, CityBossLevel.entry.left + 7, CityBossLevel.entry.top + 1, 33);
        Point c = entry.center();
        Painter.fill(this, c.x - 1, c.y - 2, 3, 1, 25);
        Painter.fill(this, c.x - 1, c.y, 3, 1, 25);
        Painter.fill(this, c.x - 1, c.y + 2, 3, 1, 25);
        Painter.fill(this, c.x, CityBossLevel.entry.top + 1, 1, 6, 32);
        Painter.set(this, c.x, CityBossLevel.entry.top, 5);
        int entrance = c.x + (c.y + 2) * this.width();
        Painter.set((Level)this, entrance, 7);
        this.transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        Painter.fillDiamond(this, arena, 1, 1);
        Painter.fill(this, arena, 5, 32);
        Painter.fill(this, arena, 6, 23);
        c = arena.center();
        Painter.set(this, c.x - 3, c.y, 25);
        Painter.set(this, c.x - 4, c.y, 25);
        Painter.set(this, c.x + 3, c.y, 25);
        Painter.set(this, c.x + 4, c.y, 25);
        Painter.set((Level)this, pedestals[0], 11);
        Painter.set((Level)this, pedestals[1], 11);
        Painter.set((Level)this, pedestals[2], 11);
        Painter.set((Level)this, pedestals[3], 11);
        Painter.fill(this, c.x, c.y + 2, 1, 4, 32);
        Painter.set(this, c.x, CityBossLevel.arena.top, 10);
        Painter.fill(this, end, 0);
        Painter.fill(this, CityBossLevel.end.left + 4, CityBossLevel.end.top + 5, 7, 18, 1);
        Painter.fill(this, CityBossLevel.end.left + 4, CityBossLevel.end.top + 5, 7, 4, 8);
        int exitCell = CityBossLevel.end.left + 7 + (CityBossLevel.end.top + 8) * this.width();
        LevelTransition exit = new LevelTransition(this, exitCell, LevelTransition.Type.REGULAR_EXIT);
        exit.set(CityBossLevel.end.left + 4, CityBossLevel.end.top + 4, CityBossLevel.end.left + 4 + 6, CityBossLevel.end.top + 4 + 4);
        this.transitions.add(exit);
        this.impShop = new ImpShopRoom();
        this.impShop.set(CityBossLevel.end.left + 3, CityBossLevel.end.top + 12, CityBossLevel.end.left + 11, CityBossLevel.end.top + 20);
        Painter.set((Level)this, this.impShop.center(), 11);
        Painter.set(this, this.impShop.left + 2, this.impShop.top, 25);
        Painter.set(this, this.impShop.left + 6, this.impShop.top, 25);
        Painter.fill(this, CityBossLevel.end.left + 5, CityBossLevel.end.bottom + 1, 5, 1, 1);
        Painter.fill(this, CityBossLevel.end.left + 6, CityBossLevel.end.bottom + 2, 3, 1, 1);
        this.impShop.paint(this);
        new CityPainter().paint(this, null);
        Painter.fill(this, CityBossLevel.end.left + 1, CityBossLevel.end.top + 2, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.left + 1, CityBossLevel.end.top + 7, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.left + 1, CityBossLevel.end.top + 12, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.left + 1, CityBossLevel.end.top + 17, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.right - 3, CityBossLevel.end.top + 2, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.right - 3, CityBossLevel.end.top + 7, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.right - 3, CityBossLevel.end.top + 12, 2, 2, 4);
        Painter.fill(this, CityBossLevel.end.right - 3, CityBossLevel.end.top + 17, 2, 2, 4);
        CustomTilemap customVisuals = new CustomGroundVisuals();
        customVisuals.setRect(0, 0, this.width(), this.height());
        this.customTiles.add(customVisuals);
        customVisuals = new CustomTerrainVisuals();
        customVisuals.setRect(0, 0, this.width(), this.height());
        this.customTerrain.add(customVisuals);
        customVisuals = new CustomWallVisuals();
        customVisuals.setRect(0, 0, this.width(), this.height());
        this.customWalls.add(customVisuals);
        return true;
    }

    public int getSummoningPos() {
        Mob king = this.getKing();
        HashSet<DwarfKing.Summoning> summons = king.buffs(DwarfKing.Summoning.class);
        ArrayList<Integer> positions = new ArrayList<Integer>();
        for (int pedestal : pedestals) {
            boolean clear = true;
            for (DwarfKing.Summoning s : summons) {
                if (s.getPos() != pedestal) continue;
                clear = false;
                break;
            }
            if (!clear) continue;
            positions.add(pedestal);
        }
        if (positions.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(positions);
    }

    private Mob getKing() {
        for (Mob m : this.mobs) {
            if (!(m instanceof DwarfKing)) continue;
            return m;
        }
        return null;
    }

    @Override
    protected void createMobs() {
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
    public boolean invalidHeroPos(int tile) {
        if (this.map[topDoor] == 10 && tile <= topDoor) {
            return true;
        }
        return super.invalidHeroPos(tile);
    }

    @Override
    public void occupyCell(Char ch) {
        if (this.map[bottomDoor] != 10 && this.map[topDoor] == 10 && ch.pos < bottomDoor && ch == Dungeon.hero) {
            this.seal();
        }
        super.occupyCell(ch);
    }

    @Override
    public void seal() {
        super.seal();
        Statistics.qualifiedForBossChallengeBadge = true;
        int doorPos = this.pointToCell(new Point(CityBossLevel.arena.left + arena.width() / 2, CityBossLevel.arena.bottom));
        Mob.holdAllies(this, doorPos);
        Mob.restoreAllies(this, Dungeon.hero.pos, doorPos);
        DwarfKing boss = new DwarfKing();
        boss.state = boss.WANDERING;
        boss.pos = this.pointToCell(arena.center());
        GameScene.add(boss);
        boss.beckon(Dungeon.hero.pos);
        if (this.heroFOV[boss.pos]) {
            boss.notice();
            boss.sprite.alpha(0.0f);
            boss.sprite.parent.add(new AlphaTweener(boss.sprite, 1.0f, 0.1f));
        }
        CityBossLevel.set(bottomDoor, 10);
        GameScene.updateMap(bottomDoor);
        Dungeon.observe();
        Heap h = (Heap)this.heaps.get(bottomDoor);
        if (h != null) {
            for (Item i : h.items) {
                this.drop((Item)i, (int)(CityBossLevel.bottomDoor - this.width())).sprite.drop(bottomDoor);
            }
            h.destroy();
        }
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Music.INSTANCE.play("music/city_boss.ogg", true);
            }
        });
    }

    @Override
    public void unseal() {
        super.unseal();
        CityBossLevel.set(bottomDoor, 5);
        GameScene.updateMap(bottomDoor);
        CityBossLevel.set(topDoor, 5);
        GameScene.updateMap(topDoor);
        if (Imp.Quest.earnedShop()) {
            this.spawnShop();
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

    private void spawnShop() {
        while (this.impShop.spacesNeeded() >= 7 * (this.impShop.height() - 2)) {
            ++this.impShop.bottom;
        }
        this.impShop.spawnShop(this);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(CityLevel.class, "water_name", new Object[0]);
            }
            case 15: {
                return Messages.get(CityLevel.class, "high_grass_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CityLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 7: 
            case 37: {
                return Messages.get(CityLevel.class, "entrance_desc", new Object[0]);
            }
            case 8: {
                return Messages.get(CityLevel.class, "exit_desc", new Object[0]);
            }
            case 12: 
            case 20: {
                return Messages.get(CityLevel.class, "deco_desc", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(CityLevel.class, "statue_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(CityLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(CityLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        CityLevel.addCityVisuals(this, this.visuals);
        return this.visuals;
    }

    @Override
    public Group addWallVisuals() {
        super.addWallVisuals();
        CityLevel.addCityWallVisuals(this, this.wallVisuals);
        return this.wallVisuals;
    }

    static {
        pedestals = new int[4];
        Point c = arena.center();
        throne = c.x + c.y * WIDTH;
        CityBossLevel.pedestals[0] = c.x - 3 + (c.y - 3) * WIDTH;
        CityBossLevel.pedestals[1] = c.x + 3 + (c.y - 3) * WIDTH;
        CityBossLevel.pedestals[2] = c.x + 3 + (c.y + 3) * WIDTH;
        CityBossLevel.pedestals[3] = c.x - 3 + (c.y + 3) * WIDTH;
    }

    public static class CustomGroundVisuals
    extends CustomTilemap {
        private static final int STAIR_ROWS = 7;

        public CustomGroundVisuals() {
            this.texture = "environment/custom_tiles/city_boss.png";
            this.tileW = 15;
            this.tileH = 48;
        }

        @Override
        public Tilemap create() {
            int i;
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int[] map = Dungeon.level.map;
            int stairsTop = -1;
            for (i = this.tileW; i < this.tileW * 22; ++i) {
                if (map[i] == 8 && stairsTop == -1) {
                    stairsTop = i;
                }
                if (map[i] == 4 && map[i - this.tileW] == 0) {
                    data[i] = 110;
                    data[++i] = 111;
                    continue;
                }
                if (map[i] == 4 && map[i - this.tileW] == 4) {
                    data[i] = 118;
                    data[++i] = 119;
                    continue;
                }
                if (i > this.tileW && map[i] == 0 && map[i - this.tileW] == 4) {
                    data[i] = 126;
                    data[++i] = 127;
                    continue;
                }
                if (map[i] == 11) {
                    data[i] = 101;
                    continue;
                }
                if (map[i] == 25) {
                    data[i] = 125;
                    continue;
                }
                if (map[i] == 1 || map[i] == 20 || map[i] == 9 || map[i] == 2 || map[i] == 15 || map[i] == 30) {
                    if (i / this.tileW == 21) {
                        data[i] = 88;
                        data[++i] = 89;
                        data[++i] = 90;
                        data[++i] = 91;
                        data[++i] = 92;
                        data[++i] = 93;
                        data[++i] = 94;
                        continue;
                    }
                    if (map[i - 1] == 0) {
                        data[i] = 97;
                        continue;
                    }
                    if (map[i + 1] == 0) {
                        data[i] = 99;
                        continue;
                    }
                    if (map[i] == 20) {
                        data[i] = 100;
                        continue;
                    }
                    data[i] = 98;
                    continue;
                }
                data[i] = -1;
            }
            for (i = 0; i < 7; ++i) {
                for (int j = 0; j < 7; ++j) {
                    data[stairsTop + j] = (i + 4) * 8 + j;
                }
                stairsTop += this.tileW;
            }
            for (i = this.tileW * 22; i < this.tileW * this.tileH; ++i) {
                if (map[i] == 11) {
                    data[i] = 108;
                    continue;
                }
                if (map[i] == 25) {
                    if (i < this.tileW * 32) {
                        if (i % this.tileW > 7) {
                            data[i] = 87;
                            data[++i] = 95;
                            continue;
                        }
                        data[i] = 71;
                        data[++i] = 79;
                        continue;
                    }
                    if (i % this.tileW > 7) {
                        data[i] = 124;
                        continue;
                    }
                    data[i] = -1;
                    continue;
                }
                if (map[i] == 32 || map[i] == 14) {
                    if (map[i + 1] == 32 && map[i + this.tileW] == 32 || map[i + 1] == 14 && map[i + this.tileW] == 14) {
                        data[i] = 105;
                        data[++i] = 106;
                        data[++i] = 107;
                        continue;
                    }
                    if (map[i + 1] == 23) {
                        data[i] = 113;
                        data[++i] = 114;
                        data[++i] = 115;
                        continue;
                    }
                    if (map[i + 1] == 32 && map[i - this.tileW] == 32 || map[i + 1] == 14 && map[i - this.tileW] == 14) {
                        data[i] = 121;
                        data[++i] = 122;
                        data[++i] = 123;
                        continue;
                    }
                    if (map[i - this.tileW] != 32 && map[i - this.tileW] != 14 || map[i - 2 * this.tileW] == 23) {
                        data[i] = 104;
                        continue;
                    }
                    if (map[i + this.tileW] != 32 && map[i + this.tileW] != 14) {
                        data[i] = 120;
                        continue;
                    }
                    data[i] = 112;
                    continue;
                }
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public boolean allowWater(int tileX, int tileY) {
            if (super.allowWater(tileX, tileY)) {
                return true;
            }
            int i = tileX + tileY * this.tileH;
            int[] map = Dungeon.level.map;
            return i >= this.tileW * 32 || map[i] != 1 && map[i] != 20 || map[i - this.tileW] != 12;
        }

        @Override
        public String name(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * this.tileW;
            if (cell < Dungeon.level.width * 22) {
                if (Dungeon.level.map[cell] == 25) {
                    return Messages.get(HallsLevel.class, "statue_name", new Object[0]);
                }
            } else {
                if (Dungeon.level.map[cell] == 23) {
                    return Messages.get(CityBossLevel.class, "throne_name", new Object[0]);
                }
                if (Dungeon.level.map[cell] == 11) {
                    return Messages.get(CityBossLevel.class, "summoning_name", new Object[0]);
                }
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * this.tileW;
            if (cell < Dungeon.level.width * 22) {
                if (Dungeon.level.map[cell] == 8) {
                    return Messages.get(HallsLevel.class, "exit_desc", new Object[0]);
                }
                if (Dungeon.level.map[cell] == 25) {
                    return Messages.get(HallsLevel.class, "statue_desc", new Object[0]);
                }
                if (Dungeon.level.map[cell] == 20) {
                    return "";
                }
            } else {
                if (Dungeon.level.map[cell] == 23) {
                    return Messages.get(CityBossLevel.class, "throne_desc", new Object[0]);
                }
                if (Dungeon.level.map[cell] == 11) {
                    return Messages.get(CityBossLevel.class, "summoning_desc", new Object[0]);
                }
            }
            return super.desc(tileX, tileY);
        }
    }

    public static class CustomTerrainVisuals
    extends CustomTilemap {
        public CustomTerrainVisuals() {
            this.texture = "environment/custom_tiles/city_boss.png";
            this.tileW = 15;
            this.tileH = 48;
        }

        @Override
        public Tilemap create() {
            int i;
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int[] map = Dungeon.level.map;
            for (i = this.tileW; i < this.tileW * 22; ++i) {
                data[i] = map[i] == 25 ? 125 : -1;
            }
            for (i = this.tileW * 22; i < this.tileW * this.tileH; ++i) {
                if (map[i] == 25) {
                    if (i < this.tileW * 32) {
                        if (i % this.tileW > 7) {
                            data[i] = 87;
                            data[++i] = 95;
                            continue;
                        }
                        data[i] = 71;
                        data[++i] = 79;
                        continue;
                    }
                    if (i % this.tileW > 7) {
                        data[i] = 124;
                        continue;
                    }
                    data[i] = -1;
                    continue;
                }
                data[i] = i < this.tileW * 32 && map[i] == 12 && map[i + this.tileW] != 4 ? 55 : (i < this.tileW * 32 && (map[i] == 1 || map[i] == 20) && map[i - this.tileW] == 12 ? 63 : -1);
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }

    public static class CustomWallVisuals
    extends CustomTilemap {
        public CustomWallVisuals() {
            this.texture = "environment/custom_tiles/city_boss.png";
            this.tileW = 15;
            this.tileH = 48;
        }

        @Override
        public Tilemap create() {
            int i;
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int[] map = Dungeon.level.map;
            int shadowTop = -1;
            for (i = this.tileW; i < this.tileW * 21; ++i) {
                if (map[i] == 8 && shadowTop == -1) {
                    shadowTop = i - this.tileW * 4;
                }
                if (map[i] == 0 && map[i + this.tileW] == 4) {
                    data[i] = 102;
                    data[++i] = 103;
                    continue;
                }
                if (map[i] == 4 && map[i - this.tileW] == 0) {
                    data[i] = 110;
                    data[++i] = 111;
                    continue;
                }
                data[i] = map[i + this.tileW] == 25 ? 117 : -1;
            }
            for (i = 0; i < 8; ++i) {
                if (i < 4) {
                    data[shadowTop] = i * 8 + 0;
                    int n = i * 8 + 1;
                    data[shadowTop + 6] = n;
                    data[shadowTop + 5] = n;
                    data[shadowTop + 4] = n;
                    data[shadowTop + 3] = n;
                    data[shadowTop + 2] = n;
                    data[shadowTop + 1] = n;
                    data[shadowTop + 7] = i * 8 + 2;
                } else {
                    int j = i - 4;
                    data[shadowTop] = j * 8 + 3;
                    int n = j * 8 + 4;
                    data[shadowTop + 6] = n;
                    data[shadowTop + 5] = n;
                    data[shadowTop + 4] = n;
                    data[shadowTop + 3] = n;
                    data[shadowTop + 2] = n;
                    data[shadowTop + 1] = n;
                    data[shadowTop + 7] = j * 8 + 5;
                }
                shadowTop += this.tileW;
            }
            for (i = this.tileW * 21; i < this.tileW * this.tileH; ++i) {
                if (map[i] == 25 && i % this.tileW > 7) {
                    data[i - this.tileW] = 116;
                } else if (map[i] == 23) {
                    data[i - this.tileW] = 109;
                }
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }
    }
}

