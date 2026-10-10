/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Bat;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.CrystalWisp;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.FungalSpinner;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.GnollGuard;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.DarkGold;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.levels.CavesLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.MiningLevelPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineEntrance;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineGiantRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineLargeRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineSecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest.MineSmallRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Callback;
import com.watabou.utils.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class MiningLevel
extends CavesLevel {
    @Override
    public String tilesTex() {
        switch (Blacksmith.Quest.Type()) {
            default: {
                return "environment/tiles_caves.png";
            }
            case 1: {
                return "environment/tiles_caves_crystal.png";
            }
            case 2: 
        }
        return "environment/tiles_caves_gnoll.png";
    }

    @Override
    public void playLevelMusic() {
        Music.INSTANCE.play("music/caves_tense.ogg", true);
    }

    @Override
    protected ArrayList<Room> initRooms() {
        int i;
        ArrayList<Room> initRooms = new ArrayList<Room>();
        this.roomEntrance = new MineEntrance();
        initRooms.add(this.roomEntrance);
        CaveRoom s = new MineGiantRoom();
        s.setSizeCat();
        initRooms.add(s);
        int rooms = 3;
        for (i = 0; i < rooms; ++i) {
            s = new MineLargeRoom();
            s.setSizeCat();
            initRooms.add(s);
        }
        rooms = Random.NormalIntRange(6, 8);
        for (i = 0; i < rooms; ++i) {
            s = new MineSmallRoom();
            s.setSizeCat();
            initRooms.add(s);
        }
        rooms = 2;
        for (i = 0; i < rooms; ++i) {
            initRooms.add(new MineSecretRoom());
        }
        return initRooms;
    }

    @Override
    protected Builder builder() {
        return new FigureEightBuilder().setPathLength(0.8f, new float[]{1.0f}).setTunnelLength(new float[]{1.0f}, new float[]{1.0f});
    }

    @Override
    protected boolean build() {
        if (super.build()) {
            CustomTilemap vis = new BorderTopDarken();
            vis.setRect(0, 0, this.width, 1);
            this.customTiles.add(vis);
            vis = new BorderWallsDarken();
            vis.setRect(0, 0, this.width, this.height);
            this.customWalls.add(vis);
            return true;
        }
        return false;
    }

    @Override
    protected Painter painter() {
        return new MiningLevelPainter().setGold(Random.NormalIntRange(45, 47)).setWater(Blacksmith.Quest.Type() == 3 ? 0.1f : 0.35f, 6).setGrass(Blacksmith.Quest.Type() == 3 ? 0.65f : 0.1f, 3);
    }

    @Override
    public int mobLimit() {
        return super.mobLimit() - 1;
    }

    @Override
    public Mob createMob() {
        switch (Blacksmith.Quest.Type()) {
            default: {
                return new Bat();
            }
            case 1: {
                return new CrystalWisp();
            }
            case 2: {
                return new GnollGuard();
            }
            case 3: 
        }
        return new FungalSpinner();
    }

    @Override
    public float respawnCooldown() {
        return 150.0f;
    }

    @Override
    protected void createItems() {
        int cell;
        Random.pushGenerator(Random.Long());
        ArrayList<Item> bonesItems = Bones.get();
        if (bonesItems != null) {
            cell = this.randomDropCell();
            if (this.map[cell] == 15 || this.map[cell] == 30) {
                this.map[cell] = 2;
                this.losBlocking[cell] = false;
            }
            for (Item i : bonesItems) {
                this.drop((Item)i, (int)cell).setHauntedIfCursed().type = Heap.Type.REMAINS;
            }
        }
        Random.popGenerator();
        cell = this.randomDropCell();
        if (this.map[cell] == 15 || this.map[cell] == 30) {
            this.map[cell] = 2;
            this.losBlocking[cell] = false;
        }
        this.drop(Generator.randomUsingDefaults(Generator.Category.FOOD), cell);
        if (Blacksmith.Quest.Type() == 2) {
            cell = this.randomDropCell();
            if (this.map[cell] == 15 || this.map[cell] == 30) {
                this.map[cell] = 2;
                this.losBlocking[cell] = false;
            }
            this.drop(Generator.randomUsingDefaults(Generator.Category.FOOD), cell);
        }
        if (Dungeon.isChallenged(32)) {
            cell = this.randomDropCell();
            if (this.map[cell] == 15 || this.map[cell] == 30) {
                this.map[cell] = 2;
                this.losBlocking[cell] = false;
            }
            this.drop(new Torch(), cell);
        }
    }

    @Override
    protected int randomDropCell() {
        return this.randomDropCell(MineSmallRoom.class);
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 35: {
                return Messages.get(MiningLevel.class, "crystal_name", new Object[0]);
            }
            case 36: {
                return Messages.get(MiningLevel.class, "boulder_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public boolean activateTransition(final Hero hero, final LevelTransition transition) {
        if (transition.type == LevelTransition.Type.BRANCH_ENTRANCE && !Blacksmith.Quest.completed()) {
            int goldAmount;
            if (hero.belongings.getItem(Pickaxe.class) == null) {
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        GameScene.show(new WndTitledMessage(new BlacksmithSprite(), Messages.titleCase(Messages.get(Blacksmith.class, "name", new Object[0])), Messages.get(Blacksmith.class, "lost_pick", new Object[0])));
                    }
                });
                return false;
            }
            DarkGold gold = hero.belongings.getItem(DarkGold.class);
            int n = goldAmount = gold == null ? 0 : gold.quantity();
            Object warnText = goldAmount < 10 ? Messages.get(Blacksmith.class, "exit_warn_none", new Object[0]) : (goldAmount < 20 ? Messages.get(Blacksmith.class, "exit_warn_low", new Object[0]) : (goldAmount < 30 ? Messages.get(Blacksmith.class, "exit_warn_med", new Object[0]) : (goldAmount < 40 ? Messages.get(Blacksmith.class, "exit_warn_high", new Object[0]) : Messages.get(Blacksmith.class, "exit_warn_full", new Object[0]))));
            if (!Blacksmith.Quest.bossBeaten()) {
                switch (Blacksmith.Quest.Type()) {
                    case 1: {
                        warnText = (String)warnText + "\n\n" + Messages.get(Blacksmith.class, "exit_warn_crystal", new Object[0]);
                        break;
                    }
                    case 2: {
                        warnText = (String)warnText + "\n\n" + Messages.get(Blacksmith.class, "exit_warn_gnoll", new Object[0]);
                        break;
                    }
                    case 3: {
                        warnText = (String)warnText + "\n\n" + Messages.get(Blacksmith.class, "exit_warn_fungi", new Object[0]);
                    }
                }
            }
            final String finalWarnText = warnText;
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    GameScene.show(new WndOptions(new BlacksmithSprite(), Messages.titleCase(Messages.get(Blacksmith.class, "name", new Object[0])), finalWarnText, new String[]{Messages.get(Blacksmith.class, "exit_yes", new Object[0]), Messages.get(Blacksmith.class, "exit_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Blacksmith.Quest.complete();
                                MiningLevel.super.activateTransition(hero, transition);
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
    public String tileDesc(int tile) {
        switch (tile) {
            case 4: {
                return Messages.get(MiningLevel.class, "wall_desc", new Object[0]);
            }
            case 12: {
                return super.tileDesc(tile) + "\n\n" + Messages.get(MiningLevel.class, "gold_extra_desc", new Object[0]);
            }
            case 35: {
                return Messages.get(MiningLevel.class, "crystal_desc", new Object[0]);
            }
            case 36: {
                return Messages.get(MiningLevel.class, "boulder_desc", new Object[0]);
            }
            case 13: {
                return Messages.get(MiningLevel.class, "barricade_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        this.visuals.clear();
        return this.visuals;
    }

    @Override
    public Group addWallVisuals() {
        super.addWallVisuals();
        CavesLevel.addCavesVisuals(this, this.wallVisuals, true);
        return this.wallVisuals;
    }

    @Override
    public boolean invalidHeroPos(int tile) {
        return false;
    }

    public static class BorderTopDarken
    extends CustomTilemap {
        public BorderTopDarken() {
            this.texture = "environment/custom_tiles/caves_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            Arrays.fill(data, 1);
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }

    public static class BorderWallsDarken
    extends CustomTilemap {
        public BorderWallsDarken() {
            this.texture = "environment/custom_tiles/caves_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            for (int i = 0; i < data.length; ++i) {
                data[i] = i % this.tileW == 0 || i % this.tileW == this.tileW - 1 ? 1 : (i + 2 * this.tileW > data.length ? 2 : -1);
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }
}

