/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Wandmaker;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.FlameParticle;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.WindParticle;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.PrisonPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Halo;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class PrisonLevel
extends RegularLevel {
    public static final String[] PRISON_TRACK_LIST = new String[]{"music/prison_1.ogg", "music/prison_2.ogg", "music/prison_2.ogg", "music/prison_1.ogg", "music/prison_3.ogg", "music/prison_3.ogg"};
    public static final float[] PRISON_TRACK_CHANCES = new float[]{1.0f, 1.0f, 0.5f, 0.25f, 1.0f, 0.5f};
    private Boolean wandmakerQuestWasActive;

    public PrisonLevel() {
        this.color1 = 6976061;
        this.color2 = 8950348;
        this.wandmakerQuestWasActive = null;
    }

    @Override
    public void playLevelMusic() {
        if (Wandmaker.Quest.active() || Statistics.amuletObtained) {
            Music.INSTANCE.play("music/prison_tense.ogg", true);
        } else {
            Music.INSTANCE.playTracks(PRISON_TRACK_LIST, PRISON_TRACK_CHANCES, false);
        }
        this.wandmakerQuestWasActive = Wandmaker.Quest.active();
    }

    @Override
    protected ArrayList<Room> initRooms() {
        return Wandmaker.Quest.spawnRoom(super.initRooms());
    }

    @Override
    protected void createMobs() {
        Wandmaker.Quest.spawnWandmaker(this, this.roomEntrance);
        super.createMobs();
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 6;
        }
        return 5 + Random.chances(new float[]{1.0f, 1.0f});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 3;
        }
        return 1 + Random.chances(new float[]{1.0f, 3.0f, 1.0f});
    }

    @Override
    protected Painter painter() {
        return new PrisonPainter().setWater(this.feeling == Level.Feeling.WATER ? 0.9f : 0.3f, 4).setGrass(this.feeling == Level.Feeling.GRASS ? 0.8f : 0.2f, 3).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
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
    protected Class<?>[] trapClasses() {
        return new Class[]{ChillingTrap.class, ShockingTrap.class, ToxicTrap.class, BurningTrap.class, PoisonDartTrap.class, AlarmTrap.class, OozeTrap.class, GrippingTrap.class, ConfusionTrap.class, FlockTrap.class, SummoningTrap.class, TeleportationTrap.class, GatewayTrap.class, GeyserTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    }

    @Override
    public void occupyCell(Char ch) {
        super.occupyCell(ch);
        if (ch == Dungeon.hero) {
            this.updateWandmakerQuestMusic();
        }
    }

    public void updateWandmakerQuestMusic() {
        if (this.wandmakerQuestWasActive == null) {
            this.wandmakerQuestWasActive = Wandmaker.Quest.active();
            return;
        }
        if (Wandmaker.Quest.active() != this.wandmakerQuestWasActive) {
            this.wandmakerQuestWasActive = Wandmaker.Quest.active();
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    Music.INSTANCE.fadeOut(1.0f, new Callback(){

                        @Override
                        public void call() {
                            if (Dungeon.level != null) {
                                Dungeon.level.playLevelMusic();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(PrisonLevel.class, "water_name", new Object[0]);
            }
            case 33: {
                return Messages.get(PrisonLevel.class, "region_deco_name", new Object[0]);
            }
            case 34: {
                return Messages.get(PrisonLevel.class, "region_deco_alt_name", new Object[0]);
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
            case 33: {
                return Messages.get(PrisonLevel.class, "region_deco_desc", new Object[0]);
            }
            case 34: {
                return Messages.get(PrisonLevel.class, "region_deco_alt_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        PrisonLevel.addPrisonVisuals(this, this.visuals);
        return this.visuals;
    }

    public static void addPrisonVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] == 12) {
                group.add(new Torch(i));
            }
            if (level.map[i] != 34) continue;
            group.add(new WindParticle.Wind(i));
        }
    }

    public static class Torch
    extends Emitter {
        private int pos;

        public Torch(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.tileCenterToWorld(pos);
            this.pos(p.x - 1.0f, p.y + 2.0f, 2.0f, 0.0f);
            this.pour(FlameParticle.FACTORY, 0.15f);
            this.add(new Halo(12.0f, 0xFFFFCC, 0.4f).point(p.x, p.y + 1.0f));
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                super.update();
            }
        }
    }
}

