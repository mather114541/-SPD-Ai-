/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Point;
import com.watabou.utils.Random;

public class StatueLineRoom
extends StandardRoom {
    private static final Integer N = 0;
    private static final Integer E = 1;
    private static final Integer S = 2;
    private static final Integer W = 3;

    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        float[] sidePreferences = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            if (door.y == this.top) {
                int n = N;
                sidePreferences[n] = sidePreferences[n] - 2.0f;
            }
            if (door.y == this.top + 1) {
                int n = N;
                sidePreferences[n] = sidePreferences[n] - 1.0f;
            }
            if (door.y == this.bottom) {
                int n = S;
                sidePreferences[n] = sidePreferences[n] - 2.0f;
            }
            if (door.y == this.bottom - 1) {
                int n = S;
                sidePreferences[n] = sidePreferences[n] - 1.0f;
            }
            if (door.x == this.left) {
                int n = W;
                sidePreferences[n] = sidePreferences[n] - 2.0f;
            }
            if (door.x == this.left + 1) {
                int n = W;
                sidePreferences[n] = sidePreferences[n] - 1.0f;
            }
            if (door.x == this.right) {
                int n = E;
                sidePreferences[n] = sidePreferences[n] - 2.0f;
            }
            if (door.x != this.right - 1) continue;
            int n = E;
            sidePreferences[n] = sidePreferences[n] - 1.0f;
        }
        int chosenSide = Random.chances(sidePreferences);
        while (chosenSide == -1) {
            int n = N;
            sidePreferences[n] = sidePreferences[n] + 1.0f;
            int n2 = E;
            sidePreferences[n2] = sidePreferences[n2] + 1.0f;
            int n3 = S;
            sidePreferences[n3] = sidePreferences[n3] + 1.0f;
            int n4 = W;
            sidePreferences[n4] = sidePreferences[n4] + 1.0f;
            chosenSide = Random.chances(sidePreferences);
        }
        this.fillAlongSide(chosenSide, level);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
            Painter.drawInside(level, this, door, 1, 1);
        }
    }

    protected int decoTerrain() {
        return 25;
    }

    private void fillAlongSide(Integer side, Level level) {
        switch (side) {
            case 0: {
                Painter.drawLine(level, new Point(this.left + 1, this.top + 1), new Point(this.right - 1, this.top + 1), this.decoTerrain());
                break;
            }
            case 1: {
                Painter.drawLine(level, new Point(this.right - 1, this.top + 1), new Point(this.right - 1, this.bottom - 1), this.decoTerrain());
                break;
            }
            case 2: {
                Painter.drawLine(level, new Point(this.left + 1, this.bottom - 1), new Point(this.right - 1, this.bottom - 1), this.decoTerrain());
                break;
            }
            case 3: {
                Painter.drawLine(level, new Point(this.left + 1, this.top + 1), new Point(this.left + 1, this.bottom - 1), this.decoTerrain());
            }
        }
    }
}

