/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

public class Maze {
    public static boolean EMPTY = false;
    public static boolean FILLED = true;
    public static boolean allowDiagonals = false;

    public static boolean[][] generate(Room r) {
        boolean[][] maze = new boolean[r.width()][r.height()];
        for (int x = 0; x < maze.length; ++x) {
            for (int y = 0; y < maze[0].length; ++y) {
                if (x != 0 && x != maze.length - 1 && y != 0 && y != maze[0].length - 1) continue;
                maze[x][y] = FILLED;
            }
        }
        for (Room.Door d : r.connected.values()) {
            maze[d.x - r.left][d.y - r.top] = EMPTY;
        }
        return Maze.generate(maze);
    }

    public static boolean[][] generate(Rect r) {
        return Maze.generate(r.width() + 1, r.height() + 1);
    }

    public static boolean[][] generate(Rect r, int[] terrain, int width, int filledTerrainType) {
        boolean[][] maze = new boolean[r.width()][r.height()];
        for (int x = 0; x < maze.length; ++x) {
            for (int y = 0; y < maze[0].length; ++y) {
                if (terrain[x + r.left + (y + r.top) * width] != filledTerrainType) continue;
                maze[x][y] = FILLED;
            }
        }
        return Maze.generate(maze);
    }

    public static boolean[][] generate(int width, int height) {
        return Maze.generate(new boolean[width][height]);
    }

    public static boolean[][] generate(boolean[][] maze) {
        int fails = 0;
        while (fails < 2500) {
            int y;
            int x;
            while (!maze[x = Random.Int(maze.length)][y = Random.Int(maze[0].length)]) {
            }
            int[] mov = Maze.decideDirection(maze, x, y);
            if (mov == null) {
                ++fails;
                continue;
            }
            fails = 0;
            int moves = 0;
            do {
                maze[x += mov[0]][y += mov[1]] = FILLED;
            } while (Random.Int(++moves) == 0 && Maze.checkValidMove(maze, x, y, mov));
        }
        return maze;
    }

    private static int[] decideDirection(boolean[][] maze, int x, int y) {
        if (Random.Int(4) == 0 && Maze.checkValidMove(maze, x, y, new int[]{0, -1})) {
            return new int[]{0, -1};
        }
        if (Random.Int(3) == 0 && Maze.checkValidMove(maze, x, y, new int[]{1, 0})) {
            return new int[]{1, 0};
        }
        if (Random.Int(2) == 0 && Maze.checkValidMove(maze, x, y, new int[]{0, 1})) {
            return new int[]{0, 1};
        }
        if (Maze.checkValidMove(maze, x, y, new int[]{-1, 0})) {
            return new int[]{-1, 0};
        }
        return null;
    }

    private static boolean checkValidMove(boolean[][] maze, int x, int y, int[] mov) {
        int sideX = 1 - Math.abs(mov[0]);
        int sideY = 1 - Math.abs(mov[1]);
        if ((x += mov[0]) <= 0 || x >= maze.length - 1 || (y += mov[1]) <= 0 || y >= maze[0].length - 1) {
            return false;
        }
        if (maze[x][y] || maze[x + sideX][y + sideY] || maze[x - sideX][y - sideY]) {
            return false;
        }
        if ((x += mov[0]) <= 0 || x >= maze.length - 1 || (y += mov[1]) <= 0 || y >= maze[0].length - 1) {
            return false;
        }
        if (maze[x][y]) {
            return false;
        }
        return allowDiagonals || !maze[x + sideX][y + sideY] && !maze[x - sideX][y - sideY];
    }
}

