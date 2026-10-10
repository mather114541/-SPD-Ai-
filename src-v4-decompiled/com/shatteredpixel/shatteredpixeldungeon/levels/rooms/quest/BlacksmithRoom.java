/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.Arrays;

public class BlacksmithRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 8);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 8);
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Painter.fill(level, this, 2, 14);
        Painter.fill(level, this.left + 2, this.top + 1, this.width() - 4, 1, 34);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 1, 1);
        }
        Painter.fill(level, this, 2, 14);
        Blacksmith npc = new Blacksmith();
        npc.pos = this.left + 3 + level.width() * (this.top + 3);
        level.mobs.add(npc);
        Painter.set(level, npc.pos + 1, 39);
        Painter.set(level, npc.pos + 1 - level.width(), 23);
        Painter.set(level, npc.pos - level.width(), 23);
        Painter.set(level, npc.pos - 1 - level.width(), 23);
        int equipPos = this.right - 3 + (this.bottom - 3) * level.width();
        if (this.height() == 8) {
            equipPos += level.width();
        }
        for (int i = 0; i < 2; ++i) {
            level.drop(Generator.random(Random.oneOf(Generator.Category.ARMOR, Generator.Category.WEAPON, Generator.Category.MISSILE)), equipPos);
            Painter.set(level, equipPos, 11);
            --equipPos;
        }
        boolean entranceOnLeft = Random.Int(2) == 0;
        for (Room.Door d : this.connected.values()) {
            if (d.y > this.top + 2) continue;
            if (d.x <= this.left + 1) {
                entranceOnLeft = false;
                continue;
            }
            if (d.x < this.right - 1) continue;
            entranceOnLeft = true;
        }
        int entrancePos = (this.top + 1) * level.width() + (entranceOnLeft ? this.left + 1 : this.right - 1);
        level.transitions.add(new LevelTransition(level, entrancePos, LevelTransition.Type.BRANCH_EXIT, Dungeon.depth, Dungeon.branch + 1, LevelTransition.Type.BRANCH_ENTRANCE));
        Painter.set(level, entrancePos, 8);
        CustomTilemap vis = new QuestEntrance();
        vis.pos(entrancePos, level);
        level.customTiles.add(vis);
        vis = new SmithyVisuals();
        vis.setRect(this.left + 2, this.top + 2, this.width() - 4, this.height() - 4);
        level.customTiles.add(vis);
        FurnaceOverhang furnace = new FurnaceOverhang();
        Point p = level.cellToPoint(npc.pos);
        furnace.setRect(p.x - 1, p.y - 2, 1, 1);
        level.customWalls.add(furnace);
    }

    @Override
    public boolean canConnect(Room r) {
        if (r.isExit()) {
            return false;
        }
        return super.canConnect(r);
    }

    @Override
    public int maxConnections(int direction) {
        if (direction == 2) {
            return 1;
        }
        return super.maxConnections(direction);
    }

    @Override
    public boolean canConnect(Point p) {
        if (p.y == this.top && p.x != this.left + 1 && p.x != this.right - 1) {
            return false;
        }
        if (p.y == this.top + 1) {
            return false;
        }
        return super.canConnect(p);
    }

    @Override
    public boolean canPlaceCharacter(Point p, Level l) {
        return false;
    }

    @Override
    public boolean canPlaceItem(Point p, Level l) {
        return l.map[l.pointToCell(p)] == 1;
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
    public boolean canPlaceWater(Point p) {
        return false;
    }

    @Override
    public boolean canMerge(Level l, Room other, Point p, int mergeTerrain) {
        return false;
    }

    public static class QuestEntrance
    extends CustomTilemap {
        public QuestEntrance() {
            this.texture = "environment/custom_tiles/caves_quest.png";
            this.tileH = 1;
            this.tileW = 1;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(new int[]{0}, 1);
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
    }

    public static class SmithyVisuals
    extends CustomTilemap {
        public SmithyVisuals() {
            this.texture = "environment/custom_tiles/caves_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            for (int i = 0; i < data.length; ++i) {
                if (i == 0) {
                    data[i] = 7;
                    continue;
                }
                if (i == 1) {
                    data[i] = 16;
                    continue;
                }
                if (i == 2) {
                    data[i] = 17;
                    continue;
                }
                if (i / this.tileW == 1 && i % this.tileW == 2) {
                    data[i] = 18;
                    continue;
                }
                int cell = this.tileX + i % this.tileW;
                if (Dungeon.level.map[cell += (this.tileY + i / this.tileW) * Dungeon.level.width()] == 14) {
                    if (i >= data.length - this.tileW) {
                        if (i % this.tileW == 0) {
                            data[i] = 12;
                            continue;
                        }
                        if (i % this.tileW == this.tileW - 1) {
                            data[i] = 14;
                            continue;
                        }
                        data[i] = 13;
                        continue;
                    }
                    if (i % this.tileW == 0) {
                        data[i] = 8;
                        continue;
                    }
                    if (i % this.tileW == this.tileW - 1) {
                        data[i] = 10;
                        continue;
                    }
                    data[i] = -1;
                    continue;
                }
                if (Dungeon.level.map[cell] == 11) {
                    if (i >= data.length - this.tileW) {
                        data[i] = 20;
                        continue;
                    }
                    data[i] = 21;
                    continue;
                }
                data[i] = -1;
            }
            v.map(data, this.tileW);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] == 23 || Dungeon.level.map[cell] == 39) {
                return Messages.get(this, "name", new Object[0]);
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            int cell = this.tileX + tileX + (this.tileY + tileY) * Dungeon.level.width();
            if (Dungeon.level.map[cell] == 23 || Dungeon.level.map[cell] == 39) {
                return Messages.get(this, "desc", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }
    }

    public static class FurnaceOverhang
    extends CustomTilemap {
        public FurnaceOverhang() {
            this.texture = "environment/custom_tiles/caves_quest.png";
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            int[] data = new int[this.tileW * this.tileH];
            Arrays.fill(data, 3);
            v.map(data, this.tileW);
            return v;
        }
    }
}

