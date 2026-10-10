/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.PatchRoom;

public class CaveRoom
extends PatchRoom {
    @Override
    public float[] sizeCatProbs() {
        return new float[]{4.0f, 2.0f, 1.0f};
    }

    @Override
    public int minHeight() {
        return Math.max(5, super.minHeight());
    }

    @Override
    public int minWidth() {
        return Math.max(5, super.minWidth());
    }

    @Override
    protected float fill() {
        int scale = Math.min(this.width() * this.height(), 324);
        return 0.3f + (float)scale / 1024.0f;
    }

    @Override
    protected int clustering() {
        return 3;
    }

    @Override
    protected boolean ensurePath() {
        return this.connected.size() > 0;
    }

    @Override
    protected boolean cleanEdges() {
        return true;
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        this.setupPatch(level);
        this.fillPatch(level, 4);
    }
}

