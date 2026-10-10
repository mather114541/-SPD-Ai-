/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.GameMath;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MineEntrance
extends CaveRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{1.0f, 0.0f, 0.0f};
    }

    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public boolean isEntrance() {
        return true;
    }

    @Override
    public void paint(Level level) {
        block12: {
            int entrance;
            block11: {
                boolean valid;
                super.paint(level);
                do {
                    valid = false;
                    entrance = level.pointToCell(this.random(3));
                    for (int i : PathFinder.NEIGHBOURS9) {
                        if (level.map[entrance + i] == 4) continue;
                        valid = true;
                    }
                    if (this.height() != 7 || this.width() != 7) continue;
                    valid = true;
                } while (level.findMob(entrance) != null || !valid);
                Painter.set(level, entrance, 37);
                for (int i : PathFinder.NEIGHBOURS8) {
                    Painter.set(level, entrance + i, 14);
                }
                QuestExit vis = new QuestExit();
                Point e = level.cellToPoint(entrance);
                vis.pos(e.x - 1, e.y - 1);
                level.customTiles.add(vis);
                level.transitions.add(new LevelTransition(level, entrance, LevelTransition.Type.BRANCH_ENTRANCE, Dungeon.depth, 0, LevelTransition.Type.BRANCH_EXIT));
                if (Blacksmith.Quest.Type() != 1) break block11;
                for (int i = 0; i < this.width() * this.height() / 2; ++i) {
                    Point r = this.random(1);
                    if (level.distance(level.pointToCell(r), entrance) <= 1 || level.map[level.pointToCell(r)] == 4) continue;
                    Painter.set(level, r, 35);
                }
                break block12;
            }
            if (Blacksmith.Quest.Type() != 2) break block12;
            for (Room n : this.connected.keySet()) {
                if (n instanceof SecretRoom || ((Room.Door)this.connected.get((Object)n)).type != Room.Door.Type.REGULAR) continue;
                if (Random.Int(10) == 0) {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.EMPTY);
                } else {
                    ((Room.Door)this.connected.get(n)).set(Room.Door.Type.WALL);
                }
                ((Room.Door)this.connected.get(n)).lockTypeChanges(true);
            }
            ArrayList<Room.Door> doors = new ArrayList<Room.Door>();
            for (Room.Door d : this.connected.values()) {
                if (d.type != Room.Door.Type.WALL) continue;
                doors.add(d);
            }
            for (Point p : this.getPoints()) {
                int cell = level.pointToCell(p);
                if (level.distance(cell, entrance) <= 1 || level.map[cell] != 1) continue;
                float dist = 1000.0f;
                for (Room.Door d : doors) {
                    dist = Math.min(dist, Point.distance(p, d));
                }
                float val = Random.Float((float)Math.pow(dist = GameMath.gate(1.0f, dist - 0.5f, 5.0f), 2.0));
                if (val <= 0.75f || dist <= 1.0f) {
                    Painter.set(level, cell, 36);
                    continue;
                }
                if (!(val <= 5.0f) || !(dist <= 3.0f)) continue;
                Painter.set(level, cell, 20);
            }
        }
    }

    public static class QuestExit
    extends CustomTilemap {
        final int TEX_WIDTH = 64;

        public QuestExit() {
            this.texture = "environment/custom_tiles/caves_quest.png";
            this.tileH = 3;
            this.tileW = 3;
            this.TEX_WIDTH = 64;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(this.mapSimpleImage(0, 1, 64), 3);
            return v;
        }

        @Override
        public String name(int tileX, int tileY) {
            if (tileX == 1 && tileY == 1) {
                return Messages.get(this, "name", new Object[0]);
            }
            return super.name(tileX, tileY);
        }

        @Override
        public String desc(int tileX, int tileY) {
            if (tileX == 1 && tileY == 1) {
                return Messages.get(this, "desc", new Object[0]);
            }
            return super.desc(tileX, tileY);
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileX == 1 && tileY == 1) {
                return super.image(tileX, tileY);
            }
            return null;
        }
    }
}

