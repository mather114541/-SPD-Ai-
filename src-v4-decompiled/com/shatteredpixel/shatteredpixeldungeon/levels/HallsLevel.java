/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.items.Torch;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.HallsPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.DemonSpawnerRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BlazingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CursingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisarmingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DisintegrationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.DistortionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlashingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrimTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.glwrap.Blending;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class HallsLevel
extends RegularLevel {
    public static final String[] HALLS_TRACK_LIST = new String[]{"music/halls_1.ogg", "music/halls_2.ogg", "music/halls_2.ogg", "music/halls_1.ogg", "music/halls_3.ogg", "music/halls_3.ogg"};
    public static final float[] HALLS_TRACK_CHANCES = new float[]{1.0f, 1.0f, 0.5f, 0.25f, 1.0f, 0.5f};

    public HallsLevel() {
        this.viewDistance = Math.min(26 - Dungeon.depth, this.viewDistance);
        this.color1 = 8393984;
        this.color2 = 10913057;
    }

    @Override
    public void playLevelMusic() {
        if (Statistics.amuletObtained) {
            Music.INSTANCE.play("music/halls_tense.ogg", true);
        } else {
            Music.INSTANCE.playTracks(HALLS_TRACK_LIST, HALLS_TRACK_CHANCES, false);
        }
    }

    @Override
    protected ArrayList<Room> initRooms() {
        ArrayList<Room> rooms = super.initRooms();
        rooms.add(new DemonSpawnerRoom());
        return rooms;
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 9;
        }
        return 8 + Random.chances(new float[]{2.0f, 1.0f});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 3;
        }
        return 2 + Random.chances(new float[]{1.0f, 1.0f});
    }

    @Override
    protected Painter painter() {
        return new HallsPainter().setWater(this.feeling == Level.Feeling.WATER ? 0.7f : 0.15f, 6).setGrass(this.feeling == Level.Feeling.GRASS ? 0.65f : 0.1f, 3).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    public void create() {
        this.addItemToSpawn(new Torch());
        this.addItemToSpawn(new Torch());
        super.create();
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
    protected Class<?>[] trapClasses() {
        return new Class[]{FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class, RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class, DisarmingTrap.class, SummoningTrap.class, WarpingTrap.class, CursingTrap.class, GrimTrap.class, PitfallTrap.class, DistortionTrap.class, GatewayTrap.class, GeyserTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
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

    public static void addHallsVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 29) continue;
            group.add(new Stream(i));
        }
    }

    private static class Stream
    extends Group {
        private int pos;
        private float delay;

        public Stream(int pos) {
            this.pos = pos;
            this.delay = Random.Float(2.0f);
        }

        @Override
        public void update() {
            if (!Dungeon.level.water[this.pos]) {
                this.killAndErase();
                return;
            }
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                float f;
                super.update();
                this.delay -= Game.elapsed;
                if (f <= 0.0f) {
                    this.delay = Random.Float(2.0f);
                    PointF p = DungeonTilemap.tileToWorld(this.pos);
                    ((FireParticle)this.recycle(FireParticle.class)).reset(p.x + Random.Float(16.0f), p.y + Random.Float(16.0f));
                }
            }
        }

        @Override
        public void draw() {
            Blending.setLightMode();
            super.draw();
            Blending.setNormalMode();
        }
    }

    public static class FireParticle
    extends PixelParticle.Shrinking {
        public FireParticle() {
            this.color(0xEE7722);
            this.lifespan = 1.0f;
            this.acc.set(0.0f, 80.0f);
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.left = this.lifespan;
            this.speed.set(0.0f, -40.0f);
            this.size = 4.0f;
        }

        @Override
        public void update() {
            super.update();
            float p = this.left / this.lifespan;
            this.am = p > 0.8f ? (1.0f - p) * 5.0f : 1.0f;
        }
    }
}

