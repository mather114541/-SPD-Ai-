/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.RatKing;
import com.shatteredpixel.shatteredpixeldungeon.items.Gold;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss.SewerBossEntranceRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.shatteredpixel.shatteredpixeldungeon.tiles.custom.Carpet;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class RatKingRoom
extends SecretRoom {
    @Override
    public boolean canConnect(Room r) {
        return !(r instanceof SewerBossEntranceRoom) && super.canConnect(r);
    }

    @Override
    public boolean canPlaceWater(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceGrass(Point p) {
        return false;
    }

    @Override
    public boolean canPlaceTrap(Point p) {
        return false;
    }

    @Override
    public int minWidth() {
        return 7;
    }

    @Override
    public int maxWidth() {
        return 7;
    }

    @Override
    public int minHeight() {
        return 7;
    }

    @Override
    public int maxHeight() {
        return 7;
    }

    @Override
    public void paint(Level level) {
        int[] statuePositions;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 2, 14);
        Room.Door entrance = this.entrance();
        entrance.set(Room.Door.Type.HIDDEN);
        int door = level.pointToCell(entrance);
        int center = level.pointToCell(this.center());
        int w = level.width();
        for (int cell : statuePositions = new int[]{center - 2 - 2 * w, center - 2 * w, center + 2 - 2 * w, center + 2, center + 2 + 2 * w, center + 2 * w, center - 2 + 2 * w, center - 2}) {
            if (level.distance(door, cell) < 2) continue;
            Painter.set(level, cell, 23);
        }
        Carpet c = new Carpet();
        c.setRect(this.left + 2, this.top + 2, this.width() - 4, this.height() - 4);
        level.customTiles.add(c);
        RatKingRoomDeco deco = new RatKingRoomDeco();
        deco.setRect(this.left + 1, this.top + 1, this.width() - 2, this.height() - 2);
        level.customTiles.add(deco);
        RatKingStatues statues = new RatKingStatues();
        statues.setRect(this.left + 1, this.top, this.width() - 2, this.height());
        level.customTerrain.add(statues);
        StatueOverhang overhang = new StatueOverhang();
        overhang.setRect(this.left + 1, this.top, this.width() - 2, this.height());
        level.customWalls.add(overhang);
        RatKing king = new RatKing();
        king.pos = center;
        level.mobs.add(king);
        for (Point p : this.getPoints()) {
            int cell = level.pointToCell(p);
            if (cell == center || level.map[cell] != 1 && level.map[cell] != 14) continue;
            level.drop(new Gold(Random.IntRange(5, 20)), cell);
        }
    }

    public static class RatKingRoomDeco
    extends CustomTilemap {
        public RatKingRoomDeco() {
            this.texture = "environment/custom_tiles/rat_king_room.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int i = 0;
            for (int y = 0; y < this.tileH; ++y) {
                int cell = this.tileX + (this.tileY + y) * Dungeon.level.width();
                for (int x = 0; x < this.tileW; ++x) {
                    data[i] = x == 2 && y == 2 ? 3 : (Dungeon.level.map[cell] == 23 ? 0 : -1);
                    ++cell;
                    ++i;
                }
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] == 23) {
                Image img = new Image(this.texture);
                img.frame(64, 0, 16, 16);
                return img;
            }
            return super.image(tileX, tileY);
        }

        @Override
        public String name(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] == 23) {
                return Messages.get(this, "statue_name", new Object[0]);
            }
            if (tileX == 2 && tileY == 2) {
                return Messages.get(this, "pillow_name", new Object[0]);
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] == 23) {
                return Messages.get(this, "statue_desc", new Object[0]);
            }
            if (tileX == 2 && tileY == 2) {
                return Messages.get(this, "pillow_desc", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }
    }

    public static class RatKingStatues
    extends CustomTilemap {
        public RatKingStatues() {
            this.texture = "environment/custom_tiles/rat_king_room.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int i = 0;
            for (int y = 0; y < this.tileH; ++y) {
                int cell = this.tileX + (this.tileY + y) * Dungeon.level.width();
                for (int x = 0; x < this.tileW; ++x) {
                    data[i] = Dungeon.level.map[cell] == 23 ? 1 : -1;
                    ++cell;
                    ++i;
                }
            }
            v.map(data, this.tileW);
            return v;
        }
    }

    public static class StatueOverhang
    extends CustomTilemap {
        public StatueOverhang() {
            this.texture = "environment/custom_tiles/rat_king_room.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            int i = 0;
            for (int y = 0; y < this.tileH; ++y) {
                int cell = this.tileX + (this.tileY + y) * Dungeon.level.width();
                for (int x = 0; x < this.tileW; ++x) {
                    data[i] = Dungeon.level.map[cell + Dungeon.level.width()] == 23 ? 2 : -1;
                    ++cell;
                    ++i;
                }
            }
            v.map(data, this.tileW);
            return v;
        }
    }
}

