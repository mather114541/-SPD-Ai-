/*
 * Decompiled with CFR 0.152.
 */
package com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard;

import com.shatteredpixel.shatteredpixeldungeon.levels.Level;
import com.shatteredpixel.shatteredpixeldungeon.levels.painters.Painter;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.rooms.standard.StandardRoom;
import com.watabou.utils.Random;

public class PillarsRoom
extends StandardRoom {
    @Override
    public int minWidth() {
        return Math.max(super.minWidth(), 7);
    }

    @Override
    public int minHeight() {
        return Math.max(super.minHeight(), 7);
    }

    @Override
    public float[] sizeCatProbs() {
        return new float[]{9.0f, 3.0f, 1.0f};
    }

    @Override
    public void paint(Level level) {
        Painter.fill(level, this, 4);
        Painter.fill(level, this, 1, 1);
        for (Room.Door door : this.connected.values()) {
            door.set(Room.Door.Type.REGULAR);
        }
        int minDim = Math.min(this.width(), this.height());
        if (minDim == 7 || this.sizeCat == StandardRoom.SizeCategory.NORMAL && Random.Int(2) == 0) {
            int pillarY;
            int pillarX;
            int pillarInset = minDim >= 11 ? 2 : 1;
            int pillarSize = (minDim - 3) / 2 - pillarInset;
            if (Random.Int(2) == 0) {
                pillarX = Random.IntRange(this.left + 1 + pillarInset, this.right - pillarSize - pillarInset);
                pillarY = this.top + 1 + pillarInset;
            } else {
                pillarX = this.left + 1 + pillarInset;
                pillarY = Random.IntRange(this.top + 1 + pillarInset, this.bottom - pillarSize - pillarInset);
            }
            Painter.fill(level, pillarX, pillarY, pillarSize, pillarSize, 4);
            pillarX = this.right - (pillarX - this.left + pillarSize - 1);
            pillarY = this.bottom - (pillarY - this.top + pillarSize - 1);
            Painter.fill(level, pillarX, pillarY, pillarSize, pillarSize, 4);
        } else {
            int pillarInset = minDim >= 12 ? 2 : 1;
            int pillarSize = (minDim - 6) / (pillarInset + 1);
            float xSpaces = this.width() - 2 * pillarInset - pillarSize - 2;
            float ySpaces = this.height() - 2 * pillarInset - pillarSize - 2;
            float minSpaces = Math.min(xSpaces, ySpaces);
            float percentSkew = (float)Math.round(Random.Float() * minSpaces) / minSpaces;
            Painter.fill(level, this.left + 1 + pillarInset + Math.round(percentSkew * xSpaces), this.top + 1 + pillarInset, pillarSize, pillarSize, 4);
            Painter.fill(level, this.right - pillarSize - pillarInset, this.top + 1 + pillarInset + Math.round(percentSkew * ySpaces), pillarSize, pillarSize, 4);
            Painter.fill(level, this.right - pillarSize - pillarInset - Math.round(percentSkew * xSpaces), this.bottom - pillarSize - pillarInset, pillarSize, pillarSize, 4);
            Painter.fill(level, this.left + 1 + pillarInset, this.bottom - pillarSize - pillarInset - Math.round(percentSkew * ySpaces), pillarSize, pillarSize, 4);
        }
    }
}

