/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.Pickaxe;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.RegularLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.CavesPainter;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.BurningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.ConfusionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.CorrosionTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.FrostTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GatewayTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GeyserTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GrippingTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.GuardianTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PitfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.PoisonDartTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.RockfallTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.StormTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.SummoningTrap;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.WarpingTrap;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.BlacksmithSprite;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTileSheet;
import com.shatteredpixel.shatteredpixeldungeon.tiles.DungeonTilemap;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndTitledMessage;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.audio.Music;
import com.watabou.noosa.particles.PixelParticle;
import com.watabou.utils.Callback;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class CavesLevel
extends RegularLevel {
    public static final String[] CAVES_TRACK_LIST = new String[]{"music/caves_1.ogg", "music/caves_2.ogg", "music/caves_2.ogg", "music/caves_1.ogg", "music/caves_3.ogg", "music/caves_3.ogg"};
    public static final float[] CAVES_TRACK_CHANCES = new float[]{1.0f, 1.0f, 0.5f, 0.25f, 1.0f, 0.5f};

    public CavesLevel() {
        this.color1 = 5459774;
        this.color2 = 12179041;
    }

    @Override
    public void playLevelMusic() {
        if (Statistics.amuletObtained) {
            Music.INSTANCE.play("music/caves_tense.ogg", true);
        } else {
            Music.INSTANCE.playTracks(CAVES_TRACK_LIST, CAVES_TRACK_CHANCES, false);
        }
    }

    @Override
    protected ArrayList<Room> initRooms() {
        return Blacksmith.Quest.spawn(super.initRooms());
    }

    @Override
    protected int standardRooms(boolean forceMax) {
        if (forceMax) {
            return 7;
        }
        return 6 + Random.chances(new float[]{2.0f, 1.0f});
    }

    @Override
    protected int specialRooms(boolean forceMax) {
        if (forceMax) {
            return 3;
        }
        return 2 + Random.chances(new float[]{4.0f, 1.0f});
    }

    @Override
    protected Painter painter() {
        return new CavesPainter().setWater(this.feeling == Level.Feeling.WATER ? 0.85f : 0.3f, 6).setGrass(this.feeling == Level.Feeling.GRASS ? 0.65f : 0.15f, 3).setTraps(this.nTraps(), this.trapClasses(), this.trapChances());
    }

    @Override
    public boolean activateTransition(final Hero hero, final LevelTransition transition) {
        if (!(transition.type != LevelTransition.Type.BRANCH_EXIT || Blacksmith.Quest.given() && !Blacksmith.Quest.completed() && Blacksmith.Quest.started())) {
            Blacksmith smith = null;
            for (Char c : Actor.chars()) {
                if (!(c instanceof Blacksmith)) continue;
                smith = (Blacksmith)c;
                break;
            }
            if (smith == null || !Blacksmith.Quest.given() || Blacksmith.Quest.completed()) {
                GLog.w(Messages.get(Blacksmith.class, "entrance_blocked", new Object[0]), new Object[0]);
            } else {
                final Pickaxe pick = hero.belongings.getItem(Pickaxe.class);
                Game.runOnRenderThread(new Callback(){

                    @Override
                    public void call() {
                        if (pick == null) {
                            GameScene.show(new WndTitledMessage(new BlacksmithSprite(), Messages.titleCase(Messages.get(Blacksmith.class, "name", new Object[0])), Messages.get(Blacksmith.class, "lost_pick", new Object[0])));
                        } else {
                            GameScene.show(new WndOptions(new BlacksmithSprite(), Messages.titleCase(Messages.get(Blacksmith.class, "name", new Object[0])), Messages.get(Blacksmith.class, "quest_start_prompt", new Object[0]), new String[]{Messages.get(Blacksmith.class, "enter_yes", new Object[0]), Messages.get(Blacksmith.class, "enter_no", new Object[0])}){

                                @Override
                                protected void onSelect(int index) {
                                    if (index == 0) {
                                        Blacksmith.Quest.start();
                                        CavesLevel.super.activateTransition(hero, transition);
                                    }
                                }
                            });
                        }
                    }
                });
            }
            return false;
        }
        return super.activateTransition(hero, transition);
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
    protected Class<?>[] trapClasses() {
        return new Class[]{BurningTrap.class, PoisonDartTrap.class, FrostTrap.class, StormTrap.class, CorrosionTrap.class, GrippingTrap.class, RockfallTrap.class, GuardianTrap.class, ConfusionTrap.class, SummoningTrap.class, WarpingTrap.class, PitfallTrap.class, GatewayTrap.class, GeyserTrap.class};
    }

    @Override
    protected float[] trapChances() {
        return new float[]{4.0f, 4.0f, 4.0f, 4.0f, 4.0f, 2.0f, 2.0f, 2.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
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
            case 7: 
            case 37: {
                return Messages.get(CavesLevel.class, "entrance_desc", new Object[0]);
            }
            case 8: {
                return Messages.get(CavesLevel.class, "exit_desc", new Object[0]);
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

    public static void addCavesVisuals(Level level, Group group) {
        CavesLevel.addCavesVisuals(level, group, false);
    }

    public static void addCavesVisuals(Level level, Group group, boolean overHang) {
        for (int i = 0; i < level.length(); ++i) {
            if (level.map[i] != 12) continue;
            group.add(new Vein(i, overHang));
        }
    }

    private static class Vein
    extends Group {
        private int pos;
        private boolean includeOverhang;
        private float delay;

        public Vein(int pos) {
            this(pos, false);
        }

        public Vein(int pos, boolean includeOverhang) {
            this.pos = pos;
            this.includeOverhang = includeOverhang;
            this.delay = Random.Float(2.0f);
        }

        @Override
        public void update() {
            this.visible = this.pos < Dungeon.level.heroFOV.length && Dungeon.level.heroFOV[this.pos];
            if (this.visible) {
                float f;
                super.update();
                this.delay -= Game.elapsed;
                if (f <= 0.0f) {
                    if (Dungeon.level.map[this.pos] != 12) {
                        this.kill();
                        return;
                    }
                    this.delay = Random.Float();
                    PointF p = DungeonTilemap.tileToWorld(this.pos);
                    if (this.includeOverhang && !DungeonTileSheet.wallStitcheable(Dungeon.level.map[this.pos - Dungeon.level.width()])) {
                        this.delay *= 0.67f;
                        p.y -= 8.0f;
                        ((Sparkle)this.recycle(Sparkle.class)).reset(p.x + Random.Float(16.0f), p.y + Random.Float(24.0f));
                    } else {
                        ((Sparkle)this.recycle(Sparkle.class)).reset(p.x + Random.Float(16.0f), p.y + Random.Float(16.0f));
                    }
                }
            }
        }
    }

    public static final class Sparkle
    extends PixelParticle {
        public void reset(float x, float y) {
            this.revive();
            this.x = x;
            this.y = y;
            this.lifespan = 0.5f;
            this.left = 0.5f;
        }

        @Override
        public void update() {
            super.update();
            float p = this.left / this.lifespan;
            this.am = p < 0.5f ? p * 2.0f : (1.0f - p) * 2.0f;
            this.size(this.am * 2.0f);
        }
    }
}

