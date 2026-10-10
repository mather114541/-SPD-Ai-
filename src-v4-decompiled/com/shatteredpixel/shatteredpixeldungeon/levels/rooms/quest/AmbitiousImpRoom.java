/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.special.SpecialRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class AmbitiousImpRoom
extends SpecialRoom {
    @Override
    public int maxWidth() {
        return 9;
    }

    @Override
    public int minWidth() {
        return 9;
    }

    @Override
    public int maxHeight() {
        return 9;
    }

    @Override
    public int minHeight() {
        return 9;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 12);
        Painter.fill(level, this, 1, 1);
        Point c = this.center();
        Painter.set(level, c.x - 2, c.y - 2, 33);
        Painter.set(level, c.x + 2, c.y - 2, 33);
        Painter.set(level, c.x - 2, c.y + 2, 33);
        Painter.set(level, c.x + 2, c.y + 2, 33);
        Painter.set(level, c.x - 3, c.y - 3, 12);
        Painter.set(level, c.x + 3, c.y - 3, 12);
        Painter.set(level, c.x - 3, c.y + 3, 12);
        Painter.set(level, c.x + 3, c.y + 3, 12);
        Room.Door entrance = this.entrance();
        Imp npc = new Imp();
        npc.pos = level.pointToCell(c);
        if (entrance.x == this.left || entrance.x == this.right) {
            npc.pos += Random.IntRange(-1, 1) * level.width();
            npc.pos = npc.pos + (entrance.x == this.left ? -2 : 2);
        } else if (entrance.y == this.top || entrance.y == this.bottom) {
            npc.pos += Random.IntRange(-1, 1);
            npc.pos = npc.pos + level.width() * (entrance.y == this.top ? -2 : 2);
        }
        level.mobs.add(npc);
        Painter.drawInside(level, this, entrance, 1, 1);
        entrance.set(Room.Door.Type.REGULAR);
        Painter.fill(level, this.left + 1, this.top + 3, 7, 3, 32);
        Painter.fill(level, this.left + 3, this.top + 1, 3, 7, 32);
        Carpet carpet = new Carpet();
        carpet.setRect(this.left + 1, this.top + 3, 7, 3);
        level.customTiles.add(carpet);
        carpet = new Carpet();
        carpet.setRect(this.left + 3, this.top + 1, 3, 7);
        level.customTiles.add(carpet);
        QuestEntrance vis = new QuestEntrance();
        vis.pos(c.x - 2, c.y - 2);
        level.customTiles.add(vis);
        EntranceBarrier vis2 = new EntranceBarrier();
        vis2.pos(c.x - 1, c.y - 1);
        level.customTiles.add(vis2);
        WallBanners vis3 = new WallBanners();
        vis3.pos(this.left + 1, this.top);
        level.customTerrain.add(vis3);
        int entrancePos = level.pointToCell(c);
        level.transitions.add(new LevelTransition(level, entrancePos, LevelTransition.Type.BRANCH_EXIT, Dungeon.depth, Dungeon.branch + 1, LevelTransition.Type.BRANCH_ENTRANCE));
        Painter.set(level, entrancePos, 8);
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return false;
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return Point.distance(p, this.center()) >= 5.0f;
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return Point.distance(p, this.center()) >= 5.0f;
    }

    public static class QuestEntrance
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public QuestEntrance() {
            this.texture = "environment/custom_tiles/city_quest.png";
            this.tileH = 5;
            this.tileW = 5;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(0, 0, 256), 5);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            return Messages.get(this, "name", new Object[0]);
        }

        @Override
        public String desc(int tileX, int tileY) {
            return Messages.get(this, "desc", new Object[0]);
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (!(tileX != 0 && tileX != this.tileW - 1 || tileY != 0 && tileY != this.tileH - 1)) {
                return null;
            }
            return super.image(tileX, tileY);
        }
    }

    public static class EntranceBarrier
    extends CustomTilemap {
        final int TEX_WIDTH = 256;

        public EntranceBarrier() {
            this.texture = "environment/custom_tiles/city_quest.png";
            this.tileH = 3;
            this.tileW = 3;
            this.TEX_WIDTH = 256;
        }

        @Override
        public Tilemap create() {
            if (this.vis != null && this.vis.alive) {
                this.vis.killAndErase();
            }
            this.vis = new Tilemap(this.texture, new TextureFilm(this.texture, 16, 16)){

                @Override
                protected NoosaScript script() {
                    return NoosaScript.get();
                }

                @Override
                public void update() {
                    this.alpha(0.3f + 0.3f * (float)Math.sin(Game.timeTotal));
                    super.update();
                }
            };
            this.vis.x = this.tileX * 16;
            this.vis.y = this.tileY * 16;
            this.vis.map(this.mapSimpleImage(5, 1, 256), 3);
            return this.vis;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }

    public static class WallBanners
    extends CustomTilemap {
        private final int BANNER_1 = 80;
        private final int BANNER_2 = 81;
        private final int BANNER__BOTTOM = 82;

        public WallBanners() {
            this.texture = "environment/custom_tiles/city_quest.png";
            this.tileW = 7;
            this.tileH = 3;
            this.BANNER_1 = 80;
            this.BANNER_2 = 81;
            this.BANNER__BOTTOM = 82;
        }

        @Override
        public void pos(int pos) {
            super.pos(pos);
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int cell = this.tileX + Dungeon.level.width() * this.tileY;
            if (!Dungeon.level.passable[cell + 1]) {
                data[1] = 80 + Random.Int(2);
                data[1 + this.tileW] = 82;
            }
            if (!Dungeon.level.passable[cell + 3]) {
                data[3] = 80 + Random.Int(2);
                if (Dungeon.level.map[cell + 3 + Dungeon.level.width()] != 11) {
                    data[3 + this.tileW] = 82;
                }
            }
            if (!Dungeon.level.passable[cell + 5]) {
                data[5] = 80 + Random.Int(2);
                data[5 + this.tileW] = 82;
            }
            if (!Dungeon.level.passable[cell += Dungeon.level.width()]) {
                data[7] = 80 + Random.Int(2);
                data[7 + this.tileW] = 82;
            }
            if (!Dungeon.level.passable[cell + 6]) {
                data[13] = 80 + Random.Int(2);
                data[13 + this.tileW] = 82;
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

