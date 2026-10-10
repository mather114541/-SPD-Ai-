/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Bones;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Goo;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.SewerLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.Builder;
import com.shatteredpixel.shatteredpixeldungeon.levels.builders.FigureEightBuilder;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.RatKingRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.GooBossRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.utils.Bundle;
import com.watabou.utils.Callback;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class SewerBossLevel
extends SewerLevel {
    public SewerBossLevel() {
        this.color1 = 4748860;
        this.color2 = 5871946;
    }

    @Override
    public void playLevelMusic() {
        if (this.locked) {
            Music.INSTANCE.play("music/sewers_boss.ogg", true);
            return;
        }
        boolean gooAlive = false;
        for (Mob m : this.mobs) {
            if (!(m instanceof Goo)) continue;
            gooAlive = true;
            break;
        }
        if (gooAlive) {
            Music.INSTANCE.end();
        } else {
            Music.INSTANCE.playTracks(SewerLevel.SEWER_TRACK_LIST, SewerLevel.SEWER_TRACK_CHANCES, false);
        }
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> initRooms = new ArrayList<Room>();
        this.roomEntrance = new SewerBossEntranceRoom();
        initRooms.add(this.roomEntrance);
        this.roomExit = new SewerBossExitRoom();
        initRooms.add(this.roomExit);
        int standards = this.standardRooms(true);
        for (int i = 0; i < standards; ++i) {
            StandardRoom s = StandardRoom.createRoom();
            s.setSizeCat(0, 0);
            initRooms.add(s);
        }
        GooBossRoom gooRoom = GooBossRoom.randomGooRoom();
        initRooms.add(gooRoom);
        ((FigureEightBuilder)this.builder).setLandmarkRoom(gooRoom);
        initRooms.add(new RatKingRoom());
        return initRooms;
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 3;
        }
        return 2 + Random.chances(new float[]{1.0f, 1.0f});
    }

    @Override
    protected Builder builder() {
        return new FigureEightBuilder().setLoopShape(2, Random.Float(0.3f, 0.8f), 0.0f).setPathLength(1.0f, new float[]{1.0f}).setTunnelLength(new float[]{1.0f, 2.0f}, new float[]{1.0f});
    }

    @Override
    protected Painter painter() {
        return new SewerPainter().setWater(0.5f, 5).setGrass(0.2f, 4).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    protected int nTraps() {
        return 0;
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
            while ((pos = this.pointToCell(this.roomEntrance.random())) == this.entrance() || this.solid[pos]) {
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
        for (Point p : this.roomEntrance.getPoints()) {
            int cell = this.pointToCell(p);
            if (!this.passable[cell] || !this.roomEntrance.inside(p) || Actor.findChar(cell) != null || Char.hasProp(ch, Char.Property.LARGE) && !this.openSpace[cell]) continue;
            candidates.add(cell);
        }
        if (candidates.isEmpty()) {
            return -1;
        }
        return (Integer)Random.element(candidates);
    }

    @Override
    public void seal() {
        if (!this.locked) {
            super.seal();
            Statistics.qualifiedForBossChallengeBadge = true;
            SewerBossLevel.set(this.entrance(), 29);
            GameScene.updateMap(this.entrance());
            GameScene.ripple(this.entrance());
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.play("music/sewers_boss.ogg", true);
                }
            });
        }
    }

    @Override
    public void unseal() {
        if (this.locked) {
            super.unseal();
            SewerBossLevel.set(this.entrance(), 7);
            GameScene.updateMap(this.entrance());
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

    @Override
    public Group addVisuals() {
        super.addVisuals();
        if (this.map[this.exit() - 1] != 12) {
            this.visuals.add(new PrisonLevel.Torch(this.exit() - 1));
        }
        if (this.map[this.exit() + 1] != 12) {
            this.visuals.add(new PrisonLevel.Torch(this.exit() + 1));
        }
        return this.visuals;
    }

    @Override
    public void restoreFromBundle(Bundle bundle) {
        super.restoreFromBundle(bundle);
        this.roomExit = this.roomEntrance;
    }
}

