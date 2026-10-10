/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.quest;

import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs.Blacksmith;
import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.secret.SecretRoom;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.CaveRoom;
import com.watabou.utils.GameMath;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import java.util.ArrayList;

public class MineSmallRoom
extends CaveRoom {
    @Override
    public int minWidth() {
        return Math.max(6, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(6, super.minHeight());
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{1.0f, 0.0f, 0.0f};
    }

    @Override
    protected float fill() {
        return 0.4f;
    }

    @Override
    public void paint(Level level) {
        block9: {
            block8: {
                super.paint(level);
                if (Blacksmith.Quest.Type() != 1) break block8;
                for (int i = 0; i < this.width() * this.height() / 3; ++i) {
                    Point r = this.random(1);
                    if (level.map[level.pointToCell(r)] == 4) continue;
                    Painter.set(level, r, 35);
                }
                break block9;
            }
            if (Blacksmith.Quest.Type() != 2) break block9;
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
                if (level.map[cell] != 1) continue;
                float dist = 1000.0f;
                for (Room.Door d : doors) {
                    dist = Math.min(dist, Point.distance(p, d));
                }
                float val = Random.Float((float)Math.pow(dist = GameMath.gate(1.0f, dist, 5.0f), 2.0));
                if (val <= 0.75f || dist <= 1.0f) {
                    Painter.set(level, cell, 36);
                    continue;
                }
                if (!(val <= 5.0f) || !(dist <= 2.0f)) continue;
                Painter.set(level, cell, 20);
            }
        }
    }
}

