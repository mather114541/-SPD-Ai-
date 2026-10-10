/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.connection.ConnectionRoom;

public class MazeConnectionRoom
extends ConnectionRoom {
    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 1, 1);
        Maze.allowDiagonals = false;
        boolean[][] maze = Maze.generate(this);
        while (this.width() >= 5 && this.height() >= 5 && (this.width() <= 7 || this.height() <= 7) && maze[this.width() / 2][this.height() / 2] == Maze.EMPTY) {
            maze = Maze.generate(this);
        }
        Painter.fill(level, this, 1, 1);
        for (int x = 0; x < maze.length; ++x) {
            for (int y = 0; y < maze[0].length; ++y) {
                if (maze[x][y] != Maze.FILLED) continue;
                Painter.fill(level, x + this.left, y + this.top, 1, 1, 4);
            }
        }
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.HIDDEN);
        }
    }

    @Override
    public int maxConnections(int direction) {
        return 2;
    }
}

