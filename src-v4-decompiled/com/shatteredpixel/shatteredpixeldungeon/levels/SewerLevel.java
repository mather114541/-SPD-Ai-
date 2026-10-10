/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.shatteredpixel.shatteredpixeldungeon.effects.Ripple;
import com.shatteredpixel.shatteredpixeldungeon.effects.Splash;
import com.shatteredpixel.shatteredpixeldungeon.items.Amulet;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.SewerPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.AlarmTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ChillingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FlockTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.OozeTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ShockingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.TeleportationTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ToxicTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WornDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.SurfaceScene;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.ColorMath;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;

public class SewerLevel
extends RegularLevel {
    public static final String[] SEWER_TRACK_LIST = new String[]{"music/sewers_1.ogg", "music/sewers_2.ogg", "music/sewers_2.ogg", "music/sewers_1.ogg", "music/sewers_3.ogg", "music/sewers_3.ogg"};
    public static final float[] SEWER_TRACK_CHANCES = new float[]{1.0f, 1.0f, 0.5f, 0.25f, 1.0f, 0.5f};

    public SewerLevel() {
        this.color1 = 4748860;
        this.color2 = 5871946;
    }

    @Override
    public void playLevelMusic() {
        if (Ghost.Quest.active() || Statistics.amuletObtained) {
            if (Statistics.amuletObtained && Dungeon.depth == 1) {
                Music.INSTANCE.play("music/theme_finale.ogg", true);
            } else {
                Music.INSTANCE.play("music/sewers_tense.ogg", true);
            }
        } else {
            Music.INSTANCE.playTracks(SEWER_TRACK_LIST, SEWER_TRACK_CHANCES, false);
        }
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 6;
        }
        return 4 + Random.chances(new float[]{1.0f, 3.0f, 1.0f});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 2;
        }
        return 1 + Random.chances(new float[]{1.0f, 4.0f});
    }

    @Override
    protected Painter painter() {
        return new SewerPainter().setWater(this.feeling == Level.Feeling.WATER ? 0.85f : 0.3f, 5).setGrass(this.feeling == Level.Feeling.GRASS ? 0.8f : 0.2f, 4).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    public String tilesTex() {
        return "environment/tiles_sewers.png";
    }

    @Override
    public String waterTex() {
        return "environment/water0.png";
    }

    @Override
    protected Class<?>[] trapClasses() {
        Class[] classArray;
        if (Dungeon.depth == 1) {
            Class[] classArray2 = new Class[1];
            classArray = classArray2;
            classArray2[0] = WornDartTrap.class;
        } else {
            Class[] classArray3 = new Class[11];
            classArray3[0] = ChillingTrap.class;
            classArray3[1] = ShockingTrap.class;
            classArray3[2] = ToxicTrap.class;
            classArray3[3] = WornDartTrap.class;
            classArray3[4] = AlarmTrap.class;
            classArray3[5] = OozeTrap.class;
            classArray3[6] = ConfusionTrap.class;
            classArray3[7] = FlockTrap.class;
            classArray3[8] = SummoningTrap.class;
            classArray3[9] = TeleportationTrap.class;
            classArray = classArray3;
            classArray3[10] = GatewayTrap.class;
        }
        return classArray;
    }

    @Override
    protected float[] trapChances() {
        float[] fArray;
        if (Dungeon.depth == 1) {
            float[] fArray2 = new float[1];
            fArray = fArray2;
            fArray2[0] = 1.0f;
        } else {
            float[] fArray3 = new float[11];
            fArray3[0] = 4.0f;
            fArray3[1] = 4.0f;
            fArray3[2] = 4.0f;
            fArray3[3] = 4.0f;
            fArray3[4] = 2.0f;
            fArray3[5] = 2.0f;
            fArray3[6] = 1.0f;
            fArray3[7] = 1.0f;
            fArray3[8] = 1.0f;
            fArray3[9] = 1.0f;
            fArray = fArray3;
            fArray3[10] = 1.0f;
        }
        return fArray;
    }

    @Override
    protected void createMobs() {
        Ghost.Quest.spawn(this, this.roomExit);
        super.createMobs();
    }

    @Override
    public boolean activateTransition(final Hero hero, LevelTransition transition) {
        if (transition.type == LevelTransition.Type.SURFACE) {
            if (hero.belongings.getItem(Amulet.class) == null) {
                if (!rl.RLMode.headless) {
                    Game.runOnRenderThread(new Callback(){

                        @Override
                        public void call() {
                            GameScene.show(new WndMessage(Messages.get(hero, "leave", new Object[0])));
                        }
                    });
                }
                return false;
            }
            Statistics.ascended = true;
            Game.switchScene(SurfaceScene.class, new Game.SceneChangeCallback(){

                @Override
                public void beforeCreate() {
                }

                @Override
                public void afterCreate() {
                    Badges.validateHappyEnd();
                    Dungeon.win(Amulet.class);
                    Dungeon.deleteGame(GamesInProgress.curSlot, true);
                    Badges.saveGlobal();
                }
            });
            return true;
        }
        return super.activateTransition(hero, transition);
    }

    @Override
    public Group addVisuals() {
        super.addVisuals();
        SewerLevel.addSewerVisuals(this, this.visuals);
        return this.visuals;
    }

    @Override
    public void buildFlagMaps() {
        super.buildFlagMaps();
        for (int i = 0; i < this.length(); ++i) {
            if (this.map[i] != 33 && this.map[i] != 34) continue;
            this.flamable[i] = true;
        }
    }

    @Override
    public void destroy(int pos) {
        int terr = this.map[pos];
        if (terr == 33) {
            SewerLevel.set(pos, 29);
            Splash.at(pos, -11502755, 10);
        } else if (terr == 34) {
            SewerLevel.set(pos, 14);
            Splash.at(pos, -11502755, 10);
        }
        super.destroy(pos);
    }

    public static void addSewerVisuals(Level level, Group group) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 12) continue;
            group.add(new Sink(i));
        }
    }

    @Override
    public String tileName(int tile) {
        switch (tile) {
            case 29: {
                return Messages.get(SewerLevel.class, "water_name", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(SewerLevel.class, "region_deco_name", new Object[0]);
            }
        }
        return super.tileName(tile);
    }

    @Override
    public String tileDesc(int tile) {
        switch (tile) {
            case 20: {
                return Messages.get(SewerLevel.class, "empty_deco_desc", new Object[0]);
            }
            case 27: {
                return Messages.get(SewerLevel.class, "bookshelf_desc", new Object[0]);
            }
            case 33: 
            case 34: {
                return Messages.get(SewerLevel.class, "region_deco_desc", new Object[0]);
            }
        }
        return super.tileDesc(tile);
    }

    private static class Sink
    extends Emitter {
        private int pos;
        private float rippleDelay = 0.0f;
        private static final Emitter.Factory factory = new Emitter.Factory(){

            @Override
            public void emit(Emitter emitter, int index, float x, float y) {
                WaterParticle p = (WaterParticle)emitter.recycle(WaterParticle.class);
                p.reset(x, y);
            }
        };

        public Sink(int pos) {
            this.pos = pos;
            PointF p = DungeonTilemap.tileCenterToWorld(pos);
            this.pos(p.x - 2.0f, p.y + 3.0f, 4.0f, 0.0f);
            this.pour(factory, 0.1f);
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                super.update();
                if (!this.isFrozen()) {
                    Ripple ripple;
                    float f = this.rippleDelay;
                    this.rippleDelay -= Game.elapsed;
                    if (f <= 0.0f && (ripple = GameScene.ripple(this.pos + Dungeon.level.width())) != null) {
                        ripple.y -= 8.0f;
                        this.rippleDelay = Random.Float(0.4f, 0.6f);
                    }
                }
            }
        }
    }

    public static final class WaterParticle
    extends PixelParticle {
        public WaterParticle() {
            this.acc.y = 50.0f;
            this.am = 0.5f;
            this.color(ColorMath.random(11979970, 3892819));
            this.size(2.0f);
        }

        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.speed.set(Random.Float(-2.0f, 2.0f), 0.0f);
            this.lifespan = 0.4f;
            this.left = 0.4f;
        }
    }
}

