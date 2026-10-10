/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.items.EquipableItem;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.armor.Armor;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.Weapon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.features.Maze;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class SecretMazeRoom
extends SecretRoom {
    @Override
    public int minWidth() {
        return 14;
    }

    @Override
    public int minHeight() {
        return 14;
    }

    @Override
    public int maxWidth() {
        return 18;
    }

    @Override
    public int maxHeight() {
        return 18;
    }

    @Override
    public void paint(Level level) {
        EquipableItem prize;
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        Maze.allowDiagonals = false;
        boolean[][] maze = Maze.generate(this);
        boolean[] passable = new boolean[this.width() * this.height()];
        Painter.fill(level, this, 1, 1);
        for (int x = 0; x < maze.length; ++x) {
            for (int y = 0; y < maze[0].length; ++y) {
                if (maze[x][y] == Maze.FILLED) {
                    Painter.fill(level, x + this.left, y + this.top, 1, 1, 4);
                }
                passable[x + this.width() * y] = maze[x][y] == Maze.EMPTY;
            }
        }
        PathFinder.setMapSize(this.width(), this.height());
        Room.Door entrance = this.entrance();
        int entrancePos = entrance.x - this.left + this.width() * (entrance.y - this.top);
        PathFinder.buildDistanceMap(entrancePos, passable);
        int bestDist = 0;
        Point bestDistP = new Point();
        for (int i = 0; i < PathFinder.distance.length; ++i) {
            if (PathFinder.distance[i] == Integer.MAX_VALUE || PathFinder.distance[i] <= bestDist) continue;
            bestDist = PathFinder.distance[i];
            bestDistP.x = i % this.width() + this.left;
            bestDistP.y = i / this.width() + this.top;
        }
        if (Random.Int(2) == 0) {
            prize = Generator.randomWeapon(Dungeon.depth / 5 + 1, true);
            if (((Weapon)prize).hasCurseEnchant()) {
                ((Weapon)prize).enchant(null);
            }
        } else {
            prize = Generator.randomArmor(Dungeon.depth / 5 + 1);
            if (((Armor)prize).hasCurseGlyph()) {
                ((Armor)prize).inscribe(null);
            }
        }
        prize.cursed = false;
        prize.cursedKnown = true;
        if (Random.Int(3) == 0) {
            prize.upgrade();
        }
        level.drop((Item)prize, (int)level.pointToCell((Point)bestDistP)).type = Heap.Type.CHEST;
        PathFinder.setMapSize(level.width(), level.height());
        this.entrance().set(Room.Door.Type.HIDDEN);
    }
}

