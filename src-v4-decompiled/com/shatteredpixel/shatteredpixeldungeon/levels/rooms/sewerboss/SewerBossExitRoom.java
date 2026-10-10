/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.sewerboss;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.LevelTransition;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.exit.ExitRoom;
import com.shatteredpixel.shatteredpixeldungeon.tiles.CustomTilemap;
import com.watabou.noosa.Image;
import com.watabou.noosa.Tilemap;
import com.watabou.utils.Point;

public class SewerBossExitRoom
extends ExitRoom {
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
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        Point c = this.center();
        Painter.fill(level, c.x - 1, c.y - 1, 3, 2, 4);
        Painter.fill(level, c.x - 1, c.y + 1, 3, 1, 14);
        int exitCell = level.pointToCell(c);
        Painter.set(level, exitCell, 21);
        LevelTransition exit = new LevelTransition(level, exitCell, LevelTransition.Type.REGULAR_EXIT);
        --exit.top;
        --exit.left;
        ++exit.right;
        level.transitions.add(exit);
        CustomTilemap vis = new SewerExit();
        vis.pos(c.x - 1, c.y);
        level.customTiles.add(vis);
        vis = new SewerExitOverhang();
        vis.pos(c.x - 1, c.y - 2);
        level.customWalls.add(vis);
    }

    public static class SewerExit
    extends CustomTilemap {
        private static final int[] layout = new int[]{21, -1, 22, 23, 23, 23, 24, 24, 24};

        public SewerExit() {
            this.texture = "environment/custom_tiles/sewer_boss.png";
            this.tileW = 3;
            this.tileH = 3;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(layout, 3);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            if (tileX == 1 && tileY == 0 || tileY == 2) {
                return null;
            }
            return super.image(tileX, tileY);
        }
    }

    public static class SewerExitOverhang
    extends CustomTilemap {
        private static final int[] layout = new int[]{16, 17, 18, 19, -1, 20};

        public SewerExitOverhang() {
            this.texture = "environment/custom_tiles/sewer_boss.png";
            this.tileW = 3;
            this.tileH = 2;
        }

        @Override
        public Tilemap create() {
            Tilemap v = super.create();
            v.map(layout, 3);
            return v;
        }

        @Override
        public Image image(int tileX, int tileY) {
            return null;
        }
    }
}

