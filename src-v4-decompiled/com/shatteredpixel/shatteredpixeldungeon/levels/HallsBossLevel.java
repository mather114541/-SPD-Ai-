/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.YogDzewa;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Pushing;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.HallsLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.Patch;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.ui.BossHealthBar;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class HallsBossLevel
extends Level {
    private static final int WIDTH = 32;
    private static final int HEIGHT = 32;
    private static final int ROOM_LEFT = 12;
    private static final int ROOM_RIGHT = 20;
    private static final int ROOM_TOP = 8;
    private static final int ROOM_BOTTOM = 16;

    public HallsBossLevel() {
        this.color1 = 8393984;
        this.color2 = 10913057;
        this.viewDistance = Math.min(4, this.viewDistance);
    }

    @Override
    public void playLevelMusic() {
        if (this.locked && BossHealthBar.isAssigned()) {
            if (BossHealthBar.isBleeding()) {
                Music.INSTANCE.play("music/halls_boss_finale.ogg", true);
            } else {
                Music.INSTANCE.play("music/halls_boss.ogg", true);
            }
        } else if (this.map[this.exit()] != 8 || Statistics.amuletObtained) {
            Music.INSTANCE.end();
        } else {
            Music.INSTANCE.playTracks(HallsLevel.HALLS_TRACK_LIST, HallsLevel.HALLS_TRACK_CHANCES, false);
        }
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_halls.png";
    }

    @Override
    public String waterTex() {
        return "environment/water4.png";
    }

    @Override
    protected boolean build() {
        int i;
        int i2;
        this.setSize(32, 32);
        for (int i3 = 0; i3 < 5; ++i3) {
            int bottom;
            int top;
            if (i3 == 0 || i3 == 4) {
                top = Random.IntRange(7, 11);
                bottom = Random.IntRange(18, 22);
            } else if (i3 == 1 || i3 == 3) {
                top = Random.IntRange(3, 7);
                bottom = Random.IntRange(22, 26);
            } else {
                top = Random.IntRange(2, 5);
                bottom = Random.IntRange(24, 28);
            }
            Painter.fill(this, 4 + i3 * 5, top, 5, bottom - top + 1, 1);
            if (i3 != 2) continue;
            int entrance = 6 + i3 * 5 + (bottom - 1) * this.width();
            this.transitions.add(new LevelTransition(this, entrance, LevelTransition.Type.REGULAR_ENTRANCE));
        }
        int exitCell = this.width / 2 + 9 * this.width;
        int bossPos = exitCell + this.width * 3;
        boolean[] patch = Patch.generate(this.width, this.height, 0.2f, 0, true);
        for (i2 = 0; i2 < this.length(); ++i2) {
            if (this.map[i2] != 1 || !patch[i2]) continue;
            this.map[i2] = this.distance(i2, bossPos) + Random.Int(5) >= 10 ? 33 : 25;
        }
        this.map[this.entrance()] = 7;
        Painter.fill(this, 11, 7, 11, 11, 1);
        patch = Patch.generate(this.width, this.height, 0.3f, 3, true);
        for (i2 = 0; i2 < this.length(); ++i2) {
            if (this.map[i2] != 1 && this.map[i2] != 25 && this.map[i2] != 33 || !patch[i2]) continue;
            this.map[i2] = 29;
        }
        for (i2 = 0; i2 < this.length(); ++i2) {
            if (this.map[i2] != 1 || Random.Int(4) != 0) continue;
            this.map[i2] = 20;
        }
        Painter.fill(this, 12, 8, 9, 9, 14);
        Painter.fill(this, 12, 8, 9, 2, 12);
        Painter.fill(this, 12, 15, 2, 2, 12);
        Painter.fill(this, 19, 15, 2, 2, 12);
        Painter.fill(this, 15, 10, 3, 4, 1);
        LevelTransition exit = new LevelTransition(this, exitCell, LevelTransition.Type.REGULAR_EXIT);
        --exit.top;
        --exit.left;
        ++exit.right;
        this.transitions.add(exit);
        CustomTilemap vis = new CenterPieceVisuals();
        vis.pos(12, 9);
        this.customTiles.add(vis);
        vis = new CenterPieceWalls();
        vis.pos(12, 8);
        this.customWalls.add(vis);
        for (i = 0; i < this.length(); ++i) {
            if (this.map[i] != 33 || Random.Int(2) != 0) continue;
            this.map[i] = 34;
        }
        for (i = 0; i < this.length; ++i) {
            this.passable[i] = (Terrain.flags[this.map[i]] & 1) != 0;
        }
        return PathFinder.getStep(this.entrance(), this.exit(), this.passable) != -1;
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
    public void occupyCell(Char ch) {
        if (this.map[this.entrance()] == 7 && this.map[this.exit()] != 8 && ch == Dungeon.hero && Dungeon.level.distance(ch.pos, this.entrance()) >= 2) {
            this.seal();
        }
        super.occupyCell(ch);
    }

    @Override
    public void seal() {
        super.seal();
        Statistics.qualifiedForBossChallengeBadge = true;
        int entrance = this.entrance();
        HallsBossLevel.set(entrance, 14);
        GameScene.updateMap(entrance);
        CellEmitter.get(entrance).start(FlameParticle.FACTORY, 0.1f, 10);
        Dungeon.observe();
        YogDzewa boss = new YogDzewa();
        boss.pos = this.exit() + this.width * 3;
        if (Actor.findChar(boss.pos) != null) {
            ArrayList<Integer> candidates = new ArrayList<Integer>();
            for (int i : PathFinder.NEIGHBOURS8) {
                if (Actor.findChar(boss.pos + i) != null) continue;
                candidates.add(boss.pos + i);
            }
            Char ch = Actor.findChar(boss.pos);
            ch.pos = !candidates.isEmpty() ? (Integer)Random.element(candidates) : boss.pos + 2 * this.width;
            Actor.add(new Pushing(ch, boss.pos, ch.pos));
        }
        GameScene.add(boss);
    }

    @Override
    public void unseal() {
        super.unseal();
        HallsBossLevel.set(this.entrance(), 7);
        GameScene.updateMap(this.entrance());
        HallsBossLevel.set(this.exit(), 8);
        GameScene.updateMap(this.exit());
        CellEmitter.get(this.exit() - 1).burst(ShadowParticle.UP, 25);
        CellEmitter.get(this.exit()).burst(ShadowParticle.UP, 100);
        CellEmitter.get(this.exit() + 1).burst(ShadowParticle.UP, 25);
        for (CustomTilemap t : this.customTiles) {
            if (!(t instanceof CenterPieceVisuals)) continue;
            ((CenterPieceVisuals)t).updateState();
        }
        for (CustomTilemap t : this.customWalls) {
            if (!(t instanceof CenterPieceWalls)) continue;
            ((CenterPieceWalls)t).updateState();
        }
        Dungeon.observe();
        Game.runOnRenderThread(new Callback(){

            @Override
            public void call() {
                Music.INSTANCE.fadeOut(5.0f, new Callback(){

                    @Override
                    public void call() {
                        Music.INSTANCE.play("music/theme_finale.ogg", true);
                    }
                });
            }
        });
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        for (Mob m : this.mobs) {
            if (!(m instanceof YogDzewa)) continue;
            ((YogDzewa)m).updateVisibility(this);
        }
    }

    @Override
    public boolean activateTransition(final Hero hero, final LevelTransition transition) {
        if (transition.type == LevelTransition.Type.REGULAR_ENTRANCE && hero.belongings.getItem(Amulet.class) != null && hero.buff(AscensionChallenge.class) == null) {
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    GameScene.show(new WndOptions(new ItemSprite(ItemSpriteSheet.AMULET), Messages.get(Amulet.class, "ascent_title", new Object[0]), Messages.get(Amulet.class, "ascent_desc", new Object[0]), new String[]{Messages.get(Amulet.class, "ascent_yes", new Object[0]), Messages.get(Amulet.class, "ascent_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Buff.affect(hero, AscensionChallenge.class);
                                Statistics.highestAscent = 25;
                                HallsBossLevel.super.activateTransition(hero, transition);
                            }
                        }
                    });
                }
            });
            return false;
        }
        return super.activateTransition(hero, transition);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(HallsLevel.class, "water_name", new Object[0]);
            }
            case 2: {
                return Messages.get(HallsLevel.class, "grass_name", new Object[0]);
            }
            case 15: {
                return Messages.get(HallsLevel.class, "high_grass_name", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(HallsLevel.class, "statue_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(HallsLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(HallsLevel.class, "water_desc", new Object[0]);
            }
            case 25: 
            case 26: {
                return Messages.get(HallsLevel.class, "statue_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(HallsLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(HallsLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        HallsLevel.addHallsVisuals(this, this.visuals);
        return this.visuals;
    }

    public static class CenterPieceVisuals
    extends CustomTilemap {
        private static final int[] map = new int[]{8, 9, 10, 11, 11, 11, 12, 13, 14, 16, 17, 18, 27, 19, 27, 20, 21, 22, 24, 25, 26, 19, 19, 19, 28, 29, 30, 24, 25, 26, 19, 19, 19, 28, 29, 30, 24, 25, 26, 19, 19, 19, 28, 29, 30, 24, 25, 34, 35, 35, 35, 34, 29, 30, 40, 41, 36, 36, 36, 36, 36, 40, 41, 48, 49, 36, 36, 36, 36, 36, 48, 49};

        public CenterPieceVisuals() {
            this.texture = "environment/custom_tiles/halls_special.png";
            this.tileW = 9;
            this.tileH = 8;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            this.updateState();
            return v;
        }

        private void updateState() {
            if (this.vis != null) {
                int[] data = (int[])map.clone();
                if (Dungeon.level.map[Dungeon.level.exit()] == 8) {
                    data[4] = 19;
                    data[14] = 31;
                    data[12] = 31;
                }
                this.vis.map(data, this.tileW);
            }
        }
    }

    public static class CenterPieceWalls
    extends CustomTilemap {
        private static final int[] map = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 32, 33, -1, -1, -1, -1, -1, 32, 33, 40, 41, -1, -1, -1, -1, -1, 40, 41};

        public CenterPieceWalls() {
            this.texture = "environment/custom_tiles/halls_special.png";
            this.tileW = 9;
            this.tileH = 9;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            this.updateState();
            return v;
        }

        private void updateState() {
            if (this.vis != null) {
                int[] data = (int[])map.clone();
                if (Dungeon.level.map[Dungeon.level.exit()] == 8) {
                    data[3] = 1;
                    data[4] = 0;
                    data[5] = 2;
                    data[13] = 23;
                }
                this.vis.map(data, this.tileW);
            }
        }
    }
}

