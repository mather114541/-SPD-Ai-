/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.AscensionChallenge;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.LostInventory;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.ElmoParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.ClothArmor;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.EscapeCrystal;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CityPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
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
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WeakeningTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ImpSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CityLevel
extends RegularLevel {
    public static final String[] CITY_TRACK_LIST = new String[]{"music/city_1.ogg", "music/city_2.ogg", "music/city_2.ogg", "music/city_1.ogg", "music/city_3.ogg", "music/city_3.ogg"};
    public static final float[] CITY_TRACK_CHANCES = new float[]{1.0f, 1.0f, 0.5f, 0.25f, 1.0f, 0.5f};

    public CityLevel() {
        this.color1 = 4941366;
        this.color2 = 0xF2F2F2;
    }

    @Override
    public void playLevelMusic() {
        if (Statistics.amuletObtained) {
            Music.INSTANCE.play("music/city_tense.ogg", true);
        } else {
            Music.INSTANCE.playTracks(CITY_TRACK_LIST, CITY_TRACK_CHANCES, false);
        }
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 8;
        }
        return 6 + Random.chances(new float[]{1.0f, 3.0f, 1.0f});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 3;
        }
        return 2 + Random.chances(new float[]{2.0f, 1.0f});
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
    protected Painter painter() {
        return new CityPainter().setWater(this.feeling == Level.Feeling.WATER ? 0.9f : 0.3f, 4).setGrass(this.feeling == Level.Feeling.GRASS ? 0.8f : 0.2f, 3).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    protected Class<?>[] trapClasses() {
        return new Class[]{FrostTrap.class, StormTrap.class, CorrosionTrap.class, BlazingTrap.class, DisintegrationTrap.class, RockfallTrap.class, FlashingTrap.class, GuardianTrap.class, WeakeningTrap.class, DisarmingTrap.class, SummoningTrap.class, WarpingTrap.class, CursingTrap.class, PitfallTrap.class, DistortionTrap.class, GatewayTrap.class, GeyserTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 2.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
    }

    @Override
    public boolean activateTransition(final Hero hero, final LevelTransition transition) {
        if (transition.type == LevelTransition.Type.BRANCH_EXIT) {
            if (Imp.Quest.isOld() || Imp.Quest.isCompleted() || !Imp.Quest.given() || hero.buff(AscensionChallenge.class) != null || hero.buff(LostInventory.class) != null) {
                return false;
            }
            Game.runOnRenderThread(new Callback(){

                @Override
                public void call() {
                    GameScene.show(new WndOptions(new ImpSprite(), Messages.titleCase(Messages.get(Imp.class, "name", new Object[0])), Messages.get(Imp.class, "enter_text", new Object[0]), new String[]{Messages.get(Imp.class, "enter_yes", new Object[0]), Messages.get(Imp.class, "enter_no", new Object[0])}){

                        @Override
                        protected void onSelect(int index) {
                            if (index == 0) {
                                Dungeon.hero.live();
                                hero.HP = hero.HT;
                                EscapeCrystal crystal = hero.belongings.getItem(EscapeCrystal.class);
                                if (crystal == null) {
                                    crystal = new EscapeCrystal();
                                } else {
                                    crystal.detachAll(Dungeon.hero.belongings.backpack);
                                }
                                if (crystal.storedItems == null) {
                                    crystal.storeHeroBelongings(Dungeon.hero);
                                }
                                crystal.collect();
                                hero.belongings.armor = new ClothArmor();
                                hero.belongings.armor.identify();
                                hero.updateHT(false);
                                CityLevel.super.activateTransition(hero, transition);
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
    protected ArrayList<Room> initRooms() {
        return Imp.Quest.spawn(super.initRooms());
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

    public static void addCityVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 12) continue;
            group.add(new Smoke(i));
        }
    }

    @Override
    public Group addWallVisuals() {
        super.addWallVisuals();
        CityLevel.addCityWallVisuals(this, this.wallVisuals);
        return this.wallVisuals;
    }

    public static void addCityWallVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 33 && level.map[i] != 34) continue;
            group.add(new GreenFlame(i));
        }
    }

    public static class Smoke
    extends Emitter {
        private int pos;
        public static final Emitter.Factory factory = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                SmokeParticle p = (SmokeParticle)emitter.recycle(SmokeParticle.class);
                p.reset(x, y);
            }
        };

        public Smoke(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.tileCenterToWorld(pos);
            this.pos(p.x - 6.0f, p.y - 4.0f, 12.0f, 12.0f);
            this.pour(factory, 0.2f);
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                super.update();
            }
        }
    }

    public static class GreenFlame
    extends Emitter {
        private int pos;
        public static final Emitter.Factory factory = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                GreenFlameParticle p = (GreenFlameParticle)emitter.recycle(GreenFlameParticle.class);
                p.reset(x, y);
            }

            @Override
            public boolean lightMode() {
                return true;
            }
        };

        public GreenFlame(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.raisedTileCenterToWorld(pos);
            this.pos(p.x - 2.0f, p.y - 5.0f, 4.0f, 4.0f);
            this.pour(factory, 0.1f);
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                super.update();
            }
        }
    }

    public static final class SmokeParticle
    extends PixelParticle {
        public SmokeParticle() {
            this.color(0);
            this.speed.set(Random.Float(-2.0f, 4.0f), -Random.Float(3.0f, 6.0f));
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.lifespan = 2.0f;
            this.left = 2.0f;
        }

        @Override
        public void update() {
            super.update();
            float p = this.left / this.lifespan;
            this.am = p > 0.8f ? 1.0f - p : p * 0.25f;
            this.size(6.0f - p * 3.0f);
        }
    }

    public static class GreenFlameParticle
    extends ElmoParticle {
        public GreenFlameParticle() {
            this.acc.set(0.0f, -40.0f);
        }
    }
}

